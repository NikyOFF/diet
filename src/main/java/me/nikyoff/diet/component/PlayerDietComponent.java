package me.nikyoff.diet.component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.api.IDietResult;
import me.nikyoff.diet.api.IPlayerDietComponent;
import me.nikyoff.diet.effect.DietEffect;
import me.nikyoff.diet.effect.DietEffects;
import me.nikyoff.diet.effect.common.DietAttribute;
import me.nikyoff.diet.effect.common.DietStatusEffect;
import me.nikyoff.diet.event.DietModEvents;
import me.nikyoff.diet.group.DietGroups;
import me.nikyoff.diet.impl.DietModApiImpl;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerDietComponent implements IPlayerDietComponent, AutoSyncedComponent, ServerTickingComponent {
    private final PlayerEntity playerEntityProvider;

    private final Set<String> availableGroups = Sets.newHashSet();
    private final Map<String, Float> groupValues = Maps.newHashMap();

    private final HashSet<DietEffect> activeDietEffects = Sets.newHashSet();
    private final Map<DietStatusEffect, Integer> activeDietStatusEffects = new HashMap<>();

    public PlayerDietComponent(PlayerEntity playerEntityProvider) {
        this.playerEntityProvider = playerEntityProvider;

        Set<String> defaultGroups = Set.of("fruits", "vegetables", "proteins", "grains", "sugars");
        availableGroups.addAll(defaultGroups);
    }

    //region available groups
    @Override
    public Set<String> getAvailableGroups() {
        return this.availableGroups;
    }

    @Override
    public Boolean hasAvailableGroup(String name) {
        return this.availableGroups.contains(name);
    }

    @Override
    public Boolean addAvailableGroup(String group) {
        Boolean result = this.availableGroups.add(group);

        if (result) {
            DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
        }

        return result;
    }

    @Override
    public Boolean removeAvailableGroup(String group) {
        Boolean result = this.availableGroups.remove(group);

        if (result) {
            DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
        }

        return result;
    }

    @Override
    public void setAvailableGroups(Set<String> availableGroups) {
        this.availableGroups.clear();
        this.availableGroups.addAll(availableGroups);
        DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
    }
    //endregion

    //region group values
    @Override
    public Map<String, Float> getGroupValues() {
        return this.groupValues;
    }

    @Override
    public void setGroupValues(Map<String, Float> groupValues) {
        this.groupValues.clear();
        this.groupValues.putAll(groupValues);
        DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
    }

    @Override
    public void setGroupValue(String groupName, float groupValue) {
        this.groupValues.put(groupName, MathHelper.clamp(groupValue, 0.0f, 1.0f));
        DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
    }
    //endregion

    //region main
    @Override
    public void onConsume(ItemStack itemStack) {
        IDietResult result = DietModApiImpl.getInstance().get(this.playerEntityProvider, itemStack);

        for (Map.Entry<IDietGroup, Float> entry : result.get().entrySet()) {
            IDietGroup dietGroup = entry.getKey();
            String dietGroupName = dietGroup.getName();
            float currentValue;

            if (this.groupValues.containsKey(dietGroupName)) {
                currentValue = this.groupValues.get(dietGroupName) + entry.getValue();
            } else {
                currentValue = dietGroup.getDefaultValue() + entry.getValue();
            }

            this.groupValues.put(dietGroupName, MathHelper.clamp(currentValue, 0.0f, 1.0f));
        }

        DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
    }

    @Override
    public void applyDecay(int value) {
        List<String> groupValuesToChange = this.groupValues.keySet()
                .stream()
                .filter(groupName -> this.availableGroups.contains(groupName) && this.groupValues.get(groupName) > 0.0f)
                .toList();

        if (groupValuesToChange.isEmpty()) {
            return;
        }

        int size = groupValuesToChange.size();

        float scale = (float) value / size;
        scale *= Math.pow(1.0f, size - 1);

        for (IDietGroup dietGroup : DietGroups.get()) {
            String dietGroupName = dietGroup.getName();
            Float currentDietValue = this.groupValues.get(dietGroupName);

            if (!groupValuesToChange.contains(dietGroupName)) {
                currentDietValue = dietGroup.getDefaultValue();
            }

            float decay = (float) (Math.exp(currentDietValue) * scale * dietGroup.getDecayMultiplier() / 100.0f);

            this.groupValues.put(dietGroupName, MathHelper.clamp(currentDietValue - decay, 0.0f, 1.0f));
        }

        DietModComponents.PLAYER_DIET_COMPONENT.sync(this.playerEntityProvider);
    }

    @Override
    public void applyDietEffect(DietEffect dietEffect, int multiplier) {
        if (this.activeDietEffects.contains(dietEffect)) {
            return;
        }

        this.activeDietEffects.add(dietEffect);

        for (DietAttribute dietAttribute : dietEffect.getAttributes()) {
            EntityAttributeInstance entityAttributeInstance = this.playerEntityProvider.getAttributeInstance(dietAttribute.attribute);
            EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(dietEffect.getUuid(), "diet effect", dietAttribute.amount * multiplier, dietAttribute.operation);

            if (entityAttributeInstance == null || entityAttributeInstance.hasModifier(entityAttributeModifier)) {
                continue;
            }

            entityAttributeInstance.addPersistentModifier(entityAttributeModifier);
        }

        for (DietStatusEffect dietStatusEffect : dietEffect.getStatusEffects()) {
            this.activeDietStatusEffects.put(dietStatusEffect, multiplier);
        }
    }

    @Override
    public void cancelDietEffect(DietEffect dietEffect) {
        if (!this.activeDietEffects.contains(dietEffect)) {
            return;
        }

        for (DietAttribute dietAttribute : dietEffect.getAttributes()) {
            EntityAttributeInstance entityAttributeInstance = this.playerEntityProvider.getAttributeInstance(dietAttribute.attribute);

            if (entityAttributeInstance != null) {
                entityAttributeInstance.removeModifier(dietEffect.getUuid());
            }
        }

        for (DietStatusEffect dietStatusEffect : dietEffect.getStatusEffects()) {
            this.activeDietStatusEffects.remove(dietStatusEffect);
        }

        this.activeDietEffects.remove(dietEffect);
    }

    @Override
    public void applyEffects() {

        for (DietEffect dietEffect : DietEffects.getEffects()) {
            int matches = dietEffect.getMatches(this.playerEntityProvider);
            boolean match = matches > 0;

//            for (IDietCondition dietCondition : dietEffect.getDietConditions()) {
//                int matches = dietCondition.getMatches(this.playerEntityProvider);
//
//                if (matches == 0) {
//                    match = false;
//                    break;
//                }
//
//                multiplier += dietCondition.getMultiplier(matches);
//            }

            boolean contains = this.activeDietEffects.contains(dietEffect);

            if (match && !contains) {
                this.applyDietEffect(dietEffect, matches);
            } else if (!match && contains) {
                this.cancelDietEffect(dietEffect);
            }
        }

        this.updateEffects();
    }

    @Override
    public void cancelEffects() {

        for (DietEffect dietEffect : this.activeDietEffects) {
            this.cancelDietEffect(dietEffect);
        }

    }

    @Override
    public void updateEffects() {

        for (Map.Entry<DietStatusEffect, Integer> dietEffect : this.activeDietStatusEffects.entrySet()) {
            DietStatusEffect dietStatusEffect = dietEffect.getKey();
            Integer multiplier = dietEffect.getValue();

            StatusEffectInstance statusEffectInstance = new StatusEffectInstance(dietStatusEffect.getStatusEffect(), 600, dietStatusEffect.getPower() * multiplier, true, false);

            this.playerEntityProvider.addStatusEffect(statusEffectInstance);
        }

    }
    //endregion

    //region tick
    @Override
    public void serverTick() {
        if (this.playerEntityProvider.isCreative() || this.availableGroups.isEmpty()) {
            return;
        }

        HungerManager hungerManager = this.playerEntityProvider.getHungerManager();
        Integer foodLevel = hungerManager.getFoodLevel();
        Integer prevFoodLevel = hungerManager.getPrevFoodLevel();

        if (foodLevel < prevFoodLevel && !DietModEvents.APPLY_DECAY.invoker().applyDecay(this.playerEntityProvider).isAccepted()) {
            this.applyDecay(prevFoodLevel - foodLevel);
        }

        this.applyEffects();
        this.updateEffects();
    }
    //endregion

    //region sync
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.playerEntityProvider;
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeMap(this.groupValues, PacketByteBuf::writeString, PacketByteBuf::writeFloat);

        buf.writeInt(this.availableGroups.size());

        for (String availableGroup : this.availableGroups) {
            buf.writeString(availableGroup);
        }
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        Map<String, Float> groupValues = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readFloat);
        Set<String> availableGroups = Sets.newHashSet();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            availableGroups.add(buf.readString());
        }

        this.groupValues.clear();
        this.availableGroups.clear();

        this.groupValues.putAll(groupValues);
        this.availableGroups.addAll(availableGroups);
    }

    //endregion

    //region NBT
    @Override
    public void readFromNbt(NbtCompound tag) {
        //region availableGroups
        if (tag.contains(Nbt.AVAILABLE_GROUPS.getKey())) {
            NbtList availableGroupsList = tag.getList(Nbt.AVAILABLE_GROUPS.getKey(), NbtType.STRING);

            for (NbtElement entry : availableGroupsList) {
                this.availableGroups.add(entry.asString());
            }
        }
        //endregion

        //region groupValues
        NbtCompound dietValuesCompound = tag.getCompound(Nbt.DIET_VALUES.getKey());

        for (IDietGroup group : DietGroups.get()) {
            String groupName = group.getName();
            Float groupValue;

            if (dietValuesCompound.contains(groupName)) {
                groupValue = dietValuesCompound.getFloat(groupName);
            } else {
                groupValue = group.getDefaultValue();
            }

            this.groupValues.put(groupName, MathHelper.clamp(groupValue, 0.0f, 1.0f));
        }
        //endregion
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        //region availableGroups
        NbtList availableGroupsList = new NbtList();

        if (this.availableGroups.isEmpty()) {
            List<NbtString> defaultGroups = Stream.of("fruits", "vegetables", "proteins", "grains", "sugars")
                    .map(NbtString::of).collect(Collectors.toList());

            availableGroupsList.addAll(defaultGroups);
        }

        tag.put(Nbt.AVAILABLE_GROUPS.getKey(), availableGroupsList);
        //endregion

        //region groupValues
        NbtCompound dietValuesCompound = new NbtCompound();

        for (IDietGroup group : DietGroups.get()) {
            String groupName = group.getName();
            Float groupValue;

            if (this.groupValues.containsKey(groupName)) {
                groupValue = this.groupValues.get(groupName);
            } else {
                groupValue = group.getDefaultValue();
            }

            dietValuesCompound.putFloat(groupName, MathHelper.clamp(groupValue, 0.0f, 1.0f));
        }

        tag.put(Nbt.DIET_VALUES.getKey(), dietValuesCompound);
        //endregion
    }

    public enum Nbt {
        AVAILABLE_GROUPS("availableGroups"),
        DIET_VALUES("dietValues");

        private final String key;

        Nbt(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
    //endregion
}

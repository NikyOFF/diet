package me.nikyoff.diet.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.nikyoff.core.utils.GuiDrawableHelper;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.api.IPlayerDietComponent;
import me.nikyoff.diet.component.DietModComponents;
import me.nikyoff.diet.component.PlayerDietComponent;
import me.nikyoff.diet.group.DietGroups;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.awt.*;
import java.util.Map;
import java.util.Set;

public class DietScreen extends Screen {
    public static final Identifier ATLAS_TEXTURE = DietMod.id("textures/gui/atlas.png");
    public static final Integer ATLAS_TEXTURE_SIZE = 512;
    public static final Pair<Integer, Integer> ATLAS_BACKGROUND_U = new Pair<>(0, 268);
    public static final Pair<Integer, Integer> ATLAS_BACKGROUND_V = new Pair<>(0, 184);
    public static final Pair<Integer, Integer> ATLAS_BAR_U = new Pair<>(268, 371);
    public static final Pair<Integer, Integer> ATLAS_BAR_V = new Pair<>(0, 5);
    public static final Integer ATLAS_BAR_FILL_V_OFFSET = 5;

    public static final Integer BACKGROUND_WIDTH;
    public static final Integer BACKGROUND_HEIGHT;

    public static final Integer BAR_WIDTH;
    public static final Integer BAR_HEIGHT;

    static {
        BACKGROUND_WIDTH = ATLAS_BACKGROUND_U.getRight() - ATLAS_BACKGROUND_U.getLeft();
        BACKGROUND_HEIGHT = ATLAS_BACKGROUND_V.getRight() - ATLAS_BACKGROUND_V.getLeft();

        BAR_WIDTH = ATLAS_BAR_U.getRight() - ATLAS_BAR_U.getLeft();
        BAR_HEIGHT = ATLAS_BAR_V.getRight() - ATLAS_BAR_V.getLeft();
    }

    private final IPlayerDietComponent playerDietComponent;

    public DietScreen(PlayerEntity playerEntity) {
        super(new TranslatableText(String.format("%s.groups.menu.label", DietMod.MOD_ID)));
        this.playerDietComponent = DietModComponents.PLAYER_DIET_COMPONENT.get(playerEntity);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.renderDietGroups(matrices);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);

        int x = (this.width - DietScreen.BACKGROUND_WIDTH) / 2;
        int y = (this.height - DietScreen.BACKGROUND_HEIGHT) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DietScreen.ATLAS_TEXTURE);

        DrawableHelper.drawTexture(matrices, x, y, 0, DietScreen.ATLAS_BACKGROUND_U.getLeft(), DietScreen.ATLAS_BACKGROUND_V.getLeft(), DietScreen.BACKGROUND_WIDTH, DietScreen.BACKGROUND_HEIGHT, DietScreen.ATLAS_TEXTURE_SIZE, DietScreen.ATLAS_TEXTURE_SIZE);
    }

    public void renderDietGroups(MatrixStack matrices) {
        if (this.client == null || this.client.player == null) {
            return;
        }

        PlayerDietComponent playerComponent = DietModComponents.PLAYER_DIET_COMPONENT.get(this.client.player);
        Map<String, Float> groupValues = playerComponent.getGroupValues();

        int x = ((this.width - DietScreen.BACKGROUND_WIDTH) / 2) + 25;
        int y = ((this.height - DietScreen.BACKGROUND_HEIGHT) / 2) + 20;

        Set<String> availableGroups = playerDietComponent.getAvailableGroups();

        for (IDietGroup dietGroup : DietGroups.get()) {
            if (!availableGroups.contains(dietGroup.getName())) {
                continue;
            }

            Float dietGroupValue = groupValues.containsKey(dietGroup.getName()) ? groupValues.get(dietGroup.getName()) : dietGroup.getDefaultValue();
            this.renderDietGroup(matrices, dietGroup, dietGroupValue, x, y);
            y += 20;
        }
    }

    public void renderDietGroup(MatrixStack matrixStack, IDietGroup dietGroup, Float groupValue, int x, int y) {
        Item icon = dietGroup.getIcon();
        TranslatableText name = new TranslatableText(String.format("%s.groups.%s.name", DietMod.MOD_ID, dietGroup.getName()));
        Color color = dietGroup.getColor();
        int percent = (int) Math.floor(groupValue * 100.0D);

        this.itemRenderer.renderGuiItemIcon(icon.getDefaultStack(), x, y - 5);
        this.textRenderer.draw(matrixStack, name, x + 20, y, color.getRGB());

        RenderSystem.setShaderTexture(0, DietScreen.ATLAS_TEXTURE);
        GuiDrawableHelper.drawColoredTexture(matrixStack, x + 90, y, 0, DietScreen.ATLAS_BAR_U.getLeft(), DietScreen.ATLAS_BAR_V.getLeft(), DietScreen.BAR_WIDTH, DietScreen.ATLAS_BAR_V.getRight(), DietScreen.ATLAS_TEXTURE_SIZE, DietScreen.ATLAS_TEXTURE_SIZE, color);
        GuiDrawableHelper.drawColoredTexture(matrixStack, x + 90, y, 0, DietScreen.ATLAS_BAR_U.getLeft(), DietScreen.ATLAS_BAR_V.getLeft() + DietScreen.ATLAS_BAR_FILL_V_OFFSET,  ((int) Math.floor(((float)DietScreen.BAR_WIDTH / 100) * percent)), DietScreen.ATLAS_BAR_V.getRight(), DietScreen.ATLAS_TEXTURE_SIZE, DietScreen.ATLAS_TEXTURE_SIZE, color);

        this.textRenderer.draw(matrixStack, new LiteralText(percent + "%"),x + 200, y, color.getRGB());
    }
}

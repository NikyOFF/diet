package me.nikyoff.core.api;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Optional;

public interface IPlayerFoodDataComponent extends Component {

    Map<Item, Integer> getFoodEaten();

    void setFoodEaten(Map<Item, Integer> foodEaten);

    Integer getFoodEatenCount(Item item);

    Optional<Integer> onConsume(Item item);
}

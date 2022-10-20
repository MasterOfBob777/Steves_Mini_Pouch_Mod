package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IStorageChangable {
    abstract void toggleInventory(Player player);

    abstract void toggleArmor(Player player);

    abstract void toggleOffhand(Player player);

    abstract void toggleCraft(Player player);

    abstract boolean isActiveInventory();

    abstract boolean isActiveArmor();

    abstract boolean isActiveOffhand();

    abstract boolean isActiveCraft();

    abstract void changeStorageSize(int change, Player player);

    abstract boolean isValidSlot(int id);

    abstract int getMaxPage();

    abstract int getSize();
}

package ga.melara.stevesminipouch.util;

import com.google.common.collect.Lists;
import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class LockableItemStackList extends NonNullList<ItemStack>
{

    private boolean stopper = false;

    //ロックされているときにアイテムをぶちまけるため，自身の所属するインベントリの参照を持っておく
    private Inventory inventory;

    //private static final ItemStack defaultItem = new ItemStack(ModRegistry.DUMMY_ITEM::get, 1);
    private static final ItemStack defaultItem = ItemStack.EMPTY;

    public static LockableItemStackList create(Inventory inventory, boolean stopper)
    {
        return new LockableItemStackList(Lists.newArrayList(), inventory, stopper);
    }

    public static LockableItemStackList createWithCapacity(int p_182648_, Inventory inventory, boolean stopper)
    {
        return new LockableItemStackList(Lists.newArrayListWithCapacity(p_182648_), inventory, stopper);
    }

    public static LockableItemStackList withSize(int p_122781_, Inventory inventory, boolean stopper)
    {
        ItemStack[] aobject = new ItemStack[p_122781_];
        Arrays.fill(aobject, defaultItem);
        return new LockableItemStackList(Arrays.asList(aobject), inventory, stopper);
    }

    @SafeVarargs
    public static LockableItemStackList of(Inventory inventory, boolean stopper, ItemStack... p_122785_)
    {
        return new LockableItemStackList(Arrays.asList(p_122785_), inventory, stopper);
    }

    protected LockableItemStackList(List<ItemStack> p_122777_, Inventory inventory, boolean stopper)
    {
        super(p_122777_, defaultItem);
        this.inventory = inventory;
        this.stopper = stopper;
    }

    @Override
    @Nonnull
    public ItemStack get(int p_122791_)
    {
        if(stopper) return defaultItem;
        return super.get(p_122791_);
    }

    @Override
    public ItemStack set(int p_122795_, ItemStack p_122796_)
    {
        if(stopper)
        {
            Level level = inventory.player.level;
            Player entity = inventory.player;
                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), p_122796_);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(entity.getUUID());
                level.addFreshEntity(itementity);
            return defaultItem;
        }
        return super.set(p_122795_, p_122796_);
    }

    @Override
    public ItemStack remove(int p_122793_)
    {
        if(stopper) return defaultItem;
        return super.remove(p_122793_);
    }
}
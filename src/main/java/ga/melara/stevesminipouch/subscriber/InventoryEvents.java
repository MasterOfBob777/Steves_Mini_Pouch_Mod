package ga.melara.stevesminipouch.subscriber;

import ga.melara.stevesminipouch.event.InitMenuEvent;
import ga.melara.stevesminipouch.event.InventorySyncEvent;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.ICustomInventory;
import ga.melara.stevesminipouch.util.IMenuSynchronizer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public class InventoryEvents {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void initClient(InventorySyncEvent e) {
        System.out.println("client " + e.getData().getInventorySize());
        ((ICustomInventory) Minecraft.getInstance().player.getInventory()). initMiniPouch(e.getData());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onInitMenu(InitMenuEvent e) {
        Player player = Minecraft.getInstance().player;
        AbstractContainerMenu menu = e.getMenu();
        if(Objects.isNull(player)) return;
        if(Objects.isNull(player.inventoryMenu) && Objects.isNull(player.containerMenu)) return;
        if(!(menu.containerId == player.containerMenu.containerId) &&
                !(menu.containerId == player.inventoryMenu.containerId)) return;
        if(Objects.nonNull(player) && player instanceof ServerPlayer serverPlayer) {
            ((ICustomInventory)player.getInventory()).initServer(((ICustomInventory)player.getInventory()).getAllData());
            //((IMenuSynchronizer) this.player.containerMenu).setdataToClient(getAllData());
            Messager.sendToPlayer(new InventorySyncPacket(((ICustomInventory)player.getInventory()).getAllData()), serverPlayer);
        }
    }
}

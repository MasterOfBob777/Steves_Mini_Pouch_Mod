package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.InventorySyncEvent;
import ga.melara.stevesminipouch.subscriber.InventoryEvents;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class InventorySyncPacket {

    InventoryStatsData data;

    public InventorySyncPacket(InventoryStatsData data) {
        this.data = data;
    }

    public InventorySyncPacket(FriendlyByteBuf buf) {
        boolean isActivateInventory = buf.readBoolean();
        boolean isActivateArmor = buf.readBoolean();
        boolean isActiveOffhand = buf.readBoolean();
        boolean isActivateCraft = buf.readBoolean();
        int slot = buf.readInt();
        int effectSlot = buf.readInt();

        this.data = new InventoryStatsData(slot, effectSlot, isActivateInventory, isActivateArmor, isActiveOffhand, isActivateCraft);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.data.isActiveInventory());
        buf.writeBoolean(this.data.isActiveArmor());
        buf.writeBoolean(this.data.isActiveOffhand());
        buf.writeBoolean(this.data.isActiveCraft());
        buf.writeInt(this.data.getInventorySize());
        buf.writeInt(this.data.getEffectSize());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            InventoryEvents.initClient(data);
            ctx.setPacketHandled(true);
        });
        return true;
    }

}

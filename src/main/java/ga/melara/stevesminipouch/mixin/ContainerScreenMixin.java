package ga.melara.stevesminipouch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ga.melara.stevesminipouch.event.PageReduceEvent;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.stats.PageChangedPacket;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements IHasPageButton{

    /*
    Todo ページ変更システム，ボタンを押してページ変数のインクリメント，デクリメント
    Todo ページ変数が変更された際にメッセージ・もしくはイベントとしてページ変数そのものを送信する
    Todo できるだけイベントでこの機能を実装する

    Todo スロットつぶしシステム，スロットがactiveを返さないように設定，closedSlotはtrueを返す(SlotMixin)

    Todo スロットが上記のような状態だったときに灰色の絵で隠せるようにrenderメソッドに対してMixinを適用

    Todo 消えるxマークのミステリー

    Todo ボタンをアイテムで触ってもアイテムを落とさないようにする
    Todo ページを捲るためのキーバインドを設定可能にする
     */

    //patch for common slot
    private static final ResourceLocation PATCH = new ResourceLocation(MODID, "textures/gui/patch.png");

    private int page = 0;

    @Shadow
    protected int inventoryLabelX;
    @Shadow
    protected int inventoryLabelY;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Shadow
    T menu;


    @Shadow
    public abstract T getMenu();

    Button upButton;
    Button downButton;
    Button pageIndicator;

    //dummy
    protected ContainerScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), cancellable = true)
    public void onInit(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Inject(method = "renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSlotRender(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        //Todo そもそもactive = trueを返さないものはレンダリングが動かない
        //Todo renderメソッドそのものに表示非表示のロジックを埋め込む必要がある

        //Todo ここはページによってスロットの色を変えるお楽しみ機能をつけるのくらいにしか使わないはず

        //System.out.println(slot.getItem());

    }


    @Inject(method = "renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At(value = "RETURN"), cancellable = true)
    public void onLabelRender(PoseStack poseStack, int unUsed1, int unUsed2, CallbackInfo ci) {
    }

    @Inject(method = "init()V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci) {
        Messager.sendToServer(new PageChangedPacket(page));
        this.menu.slots.forEach(slot -> ((IHasSlotPage) slot).setPage(page));

        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0F;

        upButton = new Button(this.leftPos + this.inventoryLabelX + this.imageWidth - 5, this.topPos + this.inventoryLabelY + 18, 18, 18,
                Component.literal("▲"), (p_96337_) -> {
            previousPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot ->
            {
                ((IHasSlotPage) slot).setPage(page);
            });
        });

        downButton = new Button(this.leftPos + this.inventoryLabelX + this.imageWidth - 5, this.topPos + this.inventoryLabelY + 54, 18, 18,
                Component.literal("▼"), (p_96337_) -> {
            nextPage();
            Messager.sendToServer(new PageChangedPacket(page));
            this.menu.slots.forEach(slot -> {
                ((IHasSlotPage) slot).setPage(page);
            });
        });

        pageIndicator = new Button(this.leftPos + this.inventoryLabelX + this.imageWidth - 5, this.topPos + this.inventoryLabelY + 36, 18, 18,
                Component.literal(String.valueOf(page + 1)), (p_96337_) -> {
        });
        pageIndicator.active = false;

        this.addRenderableWidget(upButton);

        this.addRenderableWidget(pageIndicator);

        this.addRenderableWidget(downButton);

        this.itemRenderer.blitOffset = 0.0F;
        this.setBlitOffset(0);
    }

    //最大値はClient側menuよりthis.menuとして入手
    public void nextPage() {
        if(page < (((IStorageChangable) Minecraft.getInstance().player.getInventory()).getMaxPage())) page++;
        //ここでもスロットの更新(表示，非表示の切り替え)をかける？
    }

    public void previousPage() {
        if(page > 0) page--;
        //ここでもスロットの更新(表示，非表示の切り替え)をかける？
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At(value = "RETURN"), cancellable = true)
    public void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        //this.renderables.forEach(button -> button.render(poseStack, mouseX, mouseY, partialTick));

        if((((IStorageChangable) Minecraft.getInstance().player.getInventory()).getMaxPage()) > 0) {
            upButton.visible = true;
            downButton.visible = true;
            pageIndicator.visible = true;

            upButton.x = this.leftPos + this.inventoryLabelX + this.imageWidth - 5;
            downButton.x = this.leftPos + this.inventoryLabelX + this.imageWidth - 5;
            pageIndicator.x = this.leftPos + this.inventoryLabelX + this.imageWidth - 5;

            upButton.renderButton(poseStack, mouseX, mouseY, partialTick);
            downButton.renderButton(poseStack, mouseX, mouseY, partialTick);
            pageIndicator.setMessage(Component.literal(String.valueOf(page + 1)));
            pageIndicator.renderButton(poseStack, mouseX, mouseY, partialTick);
        } else {
            upButton.visible = false;
            downButton.visible = false;
            pageIndicator.visible = false;
        }

        //this.font.draw(poseStack, Component.literal(String.valueOf(page)), (float) this.leftPos+this.inventoryLabelX+this.imageWidth, this.topPos+this.inventoryLabelY+40, 0xFFFFFF)

        int j = this.getBlitOffset();
        this.setBlitOffset(-90);

        for(Slot slot: this.menu.slots) {

            SlotType.setHiding(slot);

            ((IHasSlotPage)slot).setPage(page);

            if(((IHasSlotType) slot).getType() == SlotType.UNDEFINED)
            {
                SlotType.setType(slot);
            }

            if(!((ISlotHidable) slot).isShowing()) {
                patchSlot(poseStack, slot);
            }
        }
        this.setBlitOffset(j);
    }

    @Override
    public int getButtonX()
    {
        return upButton.x;
    }

    @Override
    public int getButtonWidth()
    {
        return upButton.getWidth();
    }

    @Override
    public int getButtonY()
    {
        return upButton.y;
    }

    @Override
    public int getButtonHeight()
    {
        return downButton.getHeight()*3;
    }

    @Inject(method = "hasClickedOutside(DDIII)Z", at = @At(value = "HEAD"), cancellable = true)
    protected void onClickedOutside(double mouseX, double mouseY, int leftPos, int RightPos, int p_97761_, CallbackInfoReturnable<Boolean> cir)
    {
        System.out.println("onClickedOutside");
        System.out.printf("mouseX: %f, mouseY: %f, leftPos: %d, rightPos: %d, upButton.x: %d, dounButton.y: %d", mouseX, mouseY, leftPos, RightPos, upButton.x, downButton.y);
        if(mouseX < getButtonX()+getButtonHeight() && mouseX > getButtonX() &&
                mouseY < getButtonY()+getButtonHeight() && mouseY > getButtonY()
        )
        {
            System.out.println("button");
            cir.setReturnValue(false);
        }
    }
    @SubscribeEvent
    public void onPageReduce(PageReduceEvent e) {
        page = 0;
        Messager.sendToServer(new PageChangedPacket(page));
        this.menu.slots.forEach(slot -> {if(((IHasSlotType)slot).getType() == SlotType.INVENTORY) ((IHasSlotPage) slot).setPage(page);});
    }

    private void patchSlot(PoseStack poseStack, Slot slot) {
        RenderSystem.setShaderTexture(0, PATCH);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, slot.x + leftPos - 1, slot.y + topPos - 1, 0, 0, 18, 18, 18, 18);
    }
}

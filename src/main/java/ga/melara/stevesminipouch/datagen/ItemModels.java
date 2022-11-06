package ga.melara.stevesminipouch.datagen;

import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        ResourceLocation parent = mcLoc("item/generated");
        this.singleTexture("slot_add_1" , parent , "layer0", modLoc("item/slot_add_1"));
        this.singleTexture("slot_add_9" , parent , "layer0", modLoc("item/slot_add_9"));
        this.singleTexture("slot_add_27", parent , "layer0", modLoc("item/slot_add_27"));

        this.singleTexture("slot_sub_1" , parent , "layer0", modLoc("item/slot_sub_1"));
        this.singleTexture("slot_sub_9" , parent , "layer0", modLoc("item/slot_sub_9"));
        this.singleTexture("slot_sub_27", parent , "layer0", modLoc("item/slot_sub_27"));

        this.singleTexture("activate_inventory", parent , "layer0", modLoc("item/activate_inventory"));
        this.singleTexture("activate_armor"    , parent , "layer0", modLoc("item/activate_armor"));
        this.singleTexture("activate_offhand"  , parent , "layer0", modLoc("item/activate_offhand"));
        this.singleTexture("activate_craft"    , parent , "layer0", modLoc("item/activate_craft"));
    }

}

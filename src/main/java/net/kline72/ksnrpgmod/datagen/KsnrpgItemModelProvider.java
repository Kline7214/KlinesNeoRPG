package net.kline72.ksnrpgmod.datagen;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.kline72.ksnrpgmod.item.KsnrpgItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class KsnrpgItemModelProvider extends ItemModelProvider {

    public KsnrpgItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, KlinesNeoRPG.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(KsnrpgItems.CRYSTALLITE_INGOT);
        simpleItem(KsnrpgItems.TELEPORT_CRYSTAL);
        simpleItem(KsnrpgItems.HEAL_CRYSTAL);
        simpleItem(KsnrpgItems.CURE_CRYSTAL);

        handheldItem(KsnrpgItems.ANNEAL_BLADE);
        handheldItem(KsnrpgItems.ELUCIDATOR);
        handheldItem(KsnrpgItems.DARK_REPULSER);
    }

    private void simpleItem(RegistryObject<Item> item) {
        getBuilder(item.getId().getPath()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }

    private void handheldItem(RegistryObject<Item> item) {
        getBuilder(item.getId().getPath()).parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}

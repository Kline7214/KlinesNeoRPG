package net.kline72.ksnrpgmod.item;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class KsnrpgItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KlinesNeoRPG.MODID);

    // Materials
    public static final RegistryObject<Item> CRYSTALLITE_INGOT = ITEMS.register("crytallite_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)));

    // Crystals
    public static final RegistryObject<Item> TELEPORT_CRYSTAL = ITEMS.register("teleport_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));
    public static final RegistryObject<Item> HEAL_CRYSTAL = ITEMS.register("heal_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));
    public static final RegistryObject<Item> CURE_CRYSTAL = ITEMS.register("cure_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));

    // Swords
    public static final RegistryObject<Item> ANNEAL_BLADE = ITEMS.register("anneal_blade",
            () -> new SwordItem(KsnrpgTiers.FL1TO25, 2, -0.2F, new Item.Properties()));
    public static final RegistryObject<Item> ELUCIDATOR = ITEMS.register("elucidator",
            () -> new SwordItem(KsnrpgTiers.FL26TO50, 4, 2.2F, new Item.Properties()));
    public static final RegistryObject<Item> DARK_REPULSER = ITEMS.register("dark_repulser",
            () -> new SwordItem(KsnrpgTiers.FL51TO75, 4, 2.2F, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

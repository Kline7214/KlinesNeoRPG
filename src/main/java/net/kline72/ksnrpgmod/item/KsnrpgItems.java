package net.kline72.ksnrpgmod.item;

import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class KsnrpgItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KlinesNeoRPG.MODID);

    public static final RegistryObject<Item> CRYSTALLITE_INGOT = ITEMS.register("crytallite_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)));
    public static final RegistryObject<Item> TELEPORT_CRYSTAL = ITEMS.register("teleport_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));
    public static final RegistryObject<Item> HEAL_CRYSTAL = ITEMS.register("heal_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));
    public static final RegistryObject<Item> CURE_CRYSTAL = ITEMS.register("cure_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

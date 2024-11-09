package net.kline72.ksnrpgmod.item;


import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class KsnrpgCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KlinesNeoRPG.MODID);

    public static final RegistryObject<CreativeModeTab> KSNRPG_ITEMS = TABS.register("ksnrpg_items",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(KsnrpgItems.CRYSTALLITE_INGOT.get()))
                    .title(Component.translatable("ksnrpgtab.one"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(KsnrpgItems.CRYSTALLITE_INGOT.get());
                        output.accept(KsnrpgItems.TELEPORT_CRYSTAL.get());
                        output.accept(KsnrpgItems.HEAL_CRYSTAL.get());
                        output.accept(KsnrpgItems.CURE_CRYSTAL.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }

}

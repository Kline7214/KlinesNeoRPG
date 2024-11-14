package net.kline72.ksnrpgmod.item;


import net.kline72.ksnrpgmod.KlinesNeoRPG;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class KsnrpgTiers {
    public static final Tier FL1TO25 = TierSortingRegistry.registerTier(
            new ForgeTier(2, 750, 6.0f, 2.5f, 12,
                    null, () -> Ingredient.EMPTY),
            new ResourceLocation(KlinesNeoRPG.MODID, "fl1to25"), List.of(Tiers.IRON), List.of(Tiers.DIAMOND));

    public static final Tier FL26TO50 = TierSortingRegistry.registerTier(
            new ForgeTier(3, 1400, 8.5f, 3.75f, 12,
                    null, () -> Ingredient.EMPTY),
            new ResourceLocation(KlinesNeoRPG.MODID, "fl26to50"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));

    public static final Tier FL51TO75 = TierSortingRegistry.registerTier(
            new ForgeTier(4, 2250, 9.25f, 5.0f, 12,
                    null, () -> Ingredient.EMPTY),
            new ResourceLocation(KlinesNeoRPG.MODID, "fl51to75"), List.of(Tiers.NETHERITE), List.of());

    public static final Tier FL76TO100 = TierSortingRegistry.registerTier(
            new ForgeTier(5, 3000, 10.0f, 6.25f, 12,
                    null, () -> Ingredient.EMPTY),
            new ResourceLocation(KlinesNeoRPG.MODID, "fl76to100"), List.of(FL51TO75), List.of());
}

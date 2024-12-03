package net.kline72.ksnrpgmod.data.arraylist;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class EntityGroup {
    public static final List<EntityType<?>> HOSTILE = new ArrayList<>();
    public static final List<EntityType<?>> PASSIVE = new ArrayList<>();
    public static final List<EntityType<?>> NEUTRAL = new ArrayList<>();
    public static final List<EntityType<?>> NPC = new ArrayList<>();

    static {
        HOSTILE.add(EntityType.BLAZE);
        HOSTILE.add(EntityType.CREEPER);
        HOSTILE.add(EntityType.ELDER_GUARDIAN);
        HOSTILE.add(EntityType.ENDERMITE);
        HOSTILE.add(EntityType.ENDER_DRAGON);
        HOSTILE.add(EntityType.EVOKER);
        HOSTILE.add(EntityType.GHAST);
        HOSTILE.add(EntityType.GUARDIAN);
        HOSTILE.add(EntityType.HOGLIN);
        HOSTILE.add(EntityType.HUSK);
        HOSTILE.add(EntityType.MAGMA_CUBE);
        HOSTILE.add(EntityType.PHANTOM);
        HOSTILE.add(EntityType.PIGLIN_BRUTE);
        HOSTILE.add(EntityType.PILLAGER);
        HOSTILE.add(EntityType.RAVAGER);
        HOSTILE.add(EntityType.SHULKER);
        HOSTILE.add(EntityType.SILVERFISH);
        HOSTILE.add(EntityType.SKELETON_HORSE);
        HOSTILE.add(EntityType.SKELETON);
        HOSTILE.add(EntityType.SLIME);
        HOSTILE.add(EntityType.STRAY);
        HOSTILE.add(EntityType.VEX);
        HOSTILE.add(EntityType.VINDICATOR);
        HOSTILE.add(EntityType.WARDEN);
        HOSTILE.add(EntityType.WITCH);
        HOSTILE.add(EntityType.WITHER);
        HOSTILE.add(EntityType.WITHER_SKELETON);
        HOSTILE.add(EntityType.ZOGLIN);
        HOSTILE.add(EntityType.ZOMBIE_HORSE);
        HOSTILE.add(EntityType.ZOMBIE_VILLAGER);
        HOSTILE.add(EntityType.ZOMBIE);

        PASSIVE.add(EntityType.ALLAY);
        PASSIVE.add(EntityType.AXOLOTL);
        PASSIVE.add(EntityType.BAT);
        PASSIVE.add(EntityType.CAMEL);
        PASSIVE.add(EntityType.CHICKEN);
        PASSIVE.add(EntityType.COD);
        PASSIVE.add(EntityType.COW);
        PASSIVE.add(EntityType.DONKEY);
        PASSIVE.add(EntityType.FROG);
        PASSIVE.add(EntityType.GLOW_SQUID);
        PASSIVE.add(EntityType.HORSE);
        PASSIVE.add(EntityType.MOOSHROOM);
        PASSIVE.add(EntityType.MULE);
        PASSIVE.add(EntityType.OCELOT);
        PASSIVE.add(EntityType.PARROT);
        PASSIVE.add(EntityType.PIG);
        PASSIVE.add(EntityType.PUFFERFISH);
        PASSIVE.add(EntityType.RABBIT);
        PASSIVE.add(EntityType.SALMON);
        PASSIVE.add(EntityType.SHEEP);
        PASSIVE.add(EntityType.SNIFFER);
        PASSIVE.add(EntityType.SQUID);
        PASSIVE.add(EntityType.STRIDER);
        PASSIVE.add(EntityType.TADPOLE);
        PASSIVE.add(EntityType.TROPICAL_FISH);
        PASSIVE.add(EntityType.TURTLE);

        NEUTRAL.add(EntityType.BEE);
        NEUTRAL.add(EntityType.CAVE_SPIDER);
        NEUTRAL.add(EntityType.DOLPHIN);
        NEUTRAL.add(EntityType.DROWNED);
        NEUTRAL.add(EntityType.ENDERMAN);
        NEUTRAL.add(EntityType.FOX);
        NEUTRAL.add(EntityType.GOAT);
        NEUTRAL.add(EntityType.IRON_GOLEM);
        NEUTRAL.add(EntityType.LLAMA);
        NEUTRAL.add(EntityType.PANDA);
        NEUTRAL.add(EntityType.PIGLIN);
        NEUTRAL.add(EntityType.POLAR_BEAR);
        NEUTRAL.add(EntityType.SNOW_GOLEM);
        NEUTRAL.add(EntityType.SPIDER);
        NEUTRAL.add(EntityType.TRADER_LLAMA);
        NEUTRAL.add(EntityType.WOLF);
        NEUTRAL.add(EntityType.ZOMBIFIED_PIGLIN);

        NPC.add(EntityType.VILLAGER);
        NPC.add(EntityType.WANDERING_TRADER);
    }
}

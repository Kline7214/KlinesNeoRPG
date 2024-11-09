package net.kline72.ksnrpgmod.item.crystals;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TeleportCrystal extends Item {
    private static final String LOCATION_TAG = "SavedLocation";
    public TeleportCrystal(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if (player.isShiftKeyDown()) {
                // Shift-right-click to teleport
                if (tag.contains(LOCATION_TAG)) {
                    CompoundTag location = tag.getCompound(LOCATION_TAG);
                    double x = location.getDouble("x");
                    double y = location.getDouble("y") + 0.5;
                    double z = location.getDouble("z");

                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo(x, y, z);
                    }
                    player.displayClientMessage(Component.translatable("Teleport!"), true);
                    stack.shrink(1);
                }
            } else {
                // Right-click to save location
                BlockPos pos = player.blockPosition();
                CompoundTag location = new CompoundTag();
                location.putDouble("x", pos.getX());
                location.putDouble("y", pos.getY());
                location.putDouble("z", pos.getZ());
                tag.put(LOCATION_TAG, location);

                player.displayClientMessage(Component.translatable("Tp Save"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

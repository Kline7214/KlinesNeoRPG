package net.kline72.ksnrpgmod.item.crystals;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CureCrystal extends Item {
    public CureCrystal(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            removeAllEffects(player);
            stack.shrink(1);
            player.displayClientMessage(Component.translatable("Heal!"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void removeAllEffects(Player player) {
        player.removeAllEffects();
    }
}

package net.kline72.ksnrpgmod.item.crystals;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HealCrystal extends Item {

    public HealCrystal(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            healPlayer(player);
            stack.shrink(1);
            player.displayClientMessage(Component.translatable("Heal!"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    private void healPlayer(Player player) {
        float maxHealth = player.getMaxHealth();
        float healAmount = maxHealth * 0.35f;
        player.heal(healAmount);
    }
}

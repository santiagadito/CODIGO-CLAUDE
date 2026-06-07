package net.sherfy.crystaldrops.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sherfy.crystaldrops.events.ScrollTntHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * AIR STRIKE — uses a blank map as its base item.
 * On use, spawns 30 TNT entities around the player.
 * The TNTs only damage monsters; players are immune. Blocks are unaffected.
 *
 * Visual: enchantment glint (isFoil = true), bold red name.
 */
public class SummonScrollItem extends Item {

    private static final int    TNT_COUNT   = 30;
    private static final int    FUSE_MIN    = 30;  // 1.5 s
    private static final int    FUSE_RANGE  = 30;  // up to 3 s
    private static final double MIN_RADIUS  = 3.0;
    private static final double MAX_RADIUS  = 7.0;
    private static final Random RNG = new Random();

    private static final Component DISPLAY_NAME = Component.literal("AIR STRIKE")
        .withStyle(Style.EMPTY
            .withColor(ChatFormatting.RED)
            .withBold(true));

    public SummonScrollItem() {
        super(new Item.Properties().stacksTo(16));
    }

    // ── Bold red name ──────────────────────────────────────────────────────

    @Override
    public Component getName(ItemStack stack) {
        return DISPLAY_NAME;
    }

    // ── Enchantment glint without real enchantments ────────────────────────

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // ── Use logic ──────────────────────────────────────────────────────────

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            spawnScrollTnt((ServerLevel) level, player);
            level.playSound(null, player.blockPosition(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 1.0f, 0.6f);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void spawnScrollTnt(ServerLevel level, Player invoker) {
        double angleStep = (2 * Math.PI) / TNT_COUNT;

        for (int i = 0; i < TNT_COUNT; i++) {
            double angle  = angleStep * i + RNG.nextDouble() * 0.4;
            double radius = MIN_RADIUS + RNG.nextDouble() * (MAX_RADIUS - MIN_RADIUS);
            double x = invoker.getX() + Math.cos(angle) * radius;
            double z = invoker.getZ() + Math.sin(angle) * radius;
            double y = invoker.getY() + RNG.nextDouble() * 2.0;

            PrimedTnt tnt = new PrimedTnt(level, x, y, z, null);
            tnt.setFuse(FUSE_MIN + RNG.nextInt(FUSE_RANGE));
            level.addFreshEntity(tnt);
            ScrollTntHandler.registerScrollTnt(tnt.getUUID(), invoker.getUUID());
        }
    }

    // ── Tooltip ────────────────────────────────────────────────────────────

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Invoca 30 explosiones a tu alrededor."));
        tooltip.add(Component.literal("§cSolo dañan monstruos. Jugadores son inmunes."));
    }
}

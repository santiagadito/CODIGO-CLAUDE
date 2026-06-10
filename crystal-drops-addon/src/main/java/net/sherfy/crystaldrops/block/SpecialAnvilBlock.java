package net.sherfy.crystaldrops.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Forja del Vacío (Special Anvil).
 *
 * Right-click while holding something in the main hand:
 *
 *  • ARMOR piece  → applies ALL FOUR protection enchantments at once
 *    (Protection, Fire/Blast/Projectile Protection), bypassing vanilla's
 *    mutual-exclusivity rule.
 *    Cost (flat, for the whole piece): {@value #ARMOR_XP_LEVELS} levels +
 *    {@value #ARMOR_DIAMOND_BLOCKS} diamond blocks.
 *
 *  • NETHERITE SWORD → forges the "Filo del Vacío", a sword carrying every
 *    vanilla combat enchantment stacked (Sharpness V + Smite V + Bane V all
 *    add up here), plus Looting III, Fire Aspect II, Knockback II, Sweeping III,
 *    Unbreaking III and Mending.
 *    Cost: {@value #SWORD_XP_LEVELS} levels + {@value #SWORD_DIAMOND_BLOCKS}
 *    diamond blocks.
 *
 * Because it is a placeable block, every player must craft and place their own.
 */
public class SpecialAnvilBlock extends AnvilBlock {

    // ── Costs ────────────────────────────────────────────────────────────────
    public static final int ARMOR_DIAMOND_BLOCKS = 20;
    public static final int ARMOR_XP_LEVELS      = 10;
    public static final int SWORD_DIAMOND_BLOCKS = 32;
    public static final int SWORD_XP_LEVELS      = 20;

    private static final int PROTECTION_LEVEL = 4; // max vanilla level

    private static final Enchantment[] PROTECTIONS = new Enchantment[]{
        Enchantments.ALL_DAMAGE_PROTECTION,  // Protection
        Enchantments.BLAST_PROTECTION,        // Blast Protection
        Enchantments.PROJECTILE_PROTECTION,   // Projectile Protection
        Enchantments.FIRE_PROTECTION          // Fire Protection
    };

    private static final Component SWORD_NAME = Component.literal("Filo del Vacío")
        .withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withBold(true));

    public SpecialAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack held = player.getMainHandItem();

        if (held.is(Items.NETHERITE_SWORD)) {
            return forgeSword(level, pos, player, held);
        }
        if (held.getItem() instanceof ArmorItem) {
            return forgeArmor(level, pos, player, held);
        }

        player.displayClientMessage(Component.literal(
            "§dForja del Vacío §7» §fSostén una §barmadura §f(protecciones) o una §bespada de netherita §f(forjar)."), true);
        return InteractionResult.CONSUME;
    }

    // ── Armor: all four protections for a flat price ─────────────────────────

    private InteractionResult forgeArmor(Level level, BlockPos pos, Player player, ItemStack armor) {
        Map<Enchantment, Integer> current = new LinkedHashMap<>(EnchantmentHelper.getEnchantments(armor));

        boolean anyMissing = false;
        for (Enchantment prot : PROTECTIONS) {
            if (current.getOrDefault(prot, 0) < PROTECTION_LEVEL) { anyMissing = true; break; }
        }

        if (!anyMissing) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §aEsta armadura ya tiene las 4 protecciones al máximo."), true);
            return InteractionResult.CONSUME;
        }

        if (!canAfford(player, ARMOR_DIAMOND_BLOCKS, ARMOR_XP_LEVELS)) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §cCuesta §b" + ARMOR_DIAMOND_BLOCKS + " bloques de diamante §cy §a"
                + ARMOR_XP_LEVELS + " niveles§c.\n§7Tienes: §b" + countDiamondBlocks(player)
                + " bloques§7, §a" + player.experienceLevel + " niveles§7."), false);
            return InteractionResult.CONSUME;
        }

        for (Enchantment prot : PROTECTIONS) {
            current.put(prot, PROTECTION_LEVEL);
        }
        EnchantmentHelper.setEnchantments(current, armor);
        charge(player, ARMOR_DIAMOND_BLOCKS, ARMOR_XP_LEVELS);

        forgeFx((ServerLevel) level, pos);
        player.displayClientMessage(Component.literal(
            "§dForja del Vacío §7» §a¡Armadura sellada con las 4 protecciones! §7(Prot, Fuego, Explosión, Proyectil)"), false);
        return InteractionResult.CONSUME;
    }

    // ── Sword: forge the legendary blade ─────────────────────────────────────

    private InteractionResult forgeSword(Level level, BlockPos pos, Player player, ItemStack sword) {
        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(sword);

        if (current.getOrDefault(Enchantments.SHARPNESS, 0) >= 5
            && current.containsKey(Enchantments.MENDING)) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §aEsta espada ya está forjada al máximo."), true);
            return InteractionResult.CONSUME;
        }

        if (!canAfford(player, SWORD_DIAMOND_BLOCKS, SWORD_XP_LEVELS)) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §cForjar el §5Filo del Vacío §ccuesta §b" + SWORD_DIAMOND_BLOCKS
                + " bloques de diamante §cy §a" + SWORD_XP_LEVELS + " niveles§c.\n§7Tienes: §b"
                + countDiamondBlocks(player) + " bloques§7, §a" + player.experienceLevel + " niveles§7."), false);
            return InteractionResult.CONSUME;
        }

        Map<Enchantment, Integer> max = new LinkedHashMap<>();
        max.put(Enchantments.SHARPNESS, 5);
        max.put(Enchantments.SMITE, 5);
        max.put(Enchantments.BANE_OF_ARTHROPODS, 5);
        max.put(Enchantments.MOB_LOOTING, 3);
        max.put(Enchantments.FIRE_ASPECT, 2);
        max.put(Enchantments.KNOCKBACK, 2);
        max.put(Enchantments.SWEEPING_EDGE, 3);
        max.put(Enchantments.UNBREAKING, 3);
        max.put(Enchantments.MENDING, 1);
        EnchantmentHelper.setEnchantments(max, sword);
        sword.setHoverName(SWORD_NAME);

        charge(player, SWORD_DIAMOND_BLOCKS, SWORD_XP_LEVELS);

        forgeFx((ServerLevel) level, pos);
        level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.5f, 1.6f);
        player.displayClientMessage(Component.literal(
            "§dForja del Vacío §7» §5¡El §dFilo del Vacío §5ha sido forjado! §7(daño máximo posible)"), false);
        return InteractionResult.CONSUME;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static boolean canAfford(Player player, int diamondBlocks, int xpLevels) {
        return countDiamondBlocks(player) >= diamondBlocks && player.experienceLevel >= xpLevels;
    }

    private static void charge(Player player, int diamondBlocks, int xpLevels) {
        removeDiamondBlocks(player, diamondBlocks);
        player.giveExperienceLevels(-xpLevels);
    }

    private static int countDiamondBlocks(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.DIAMOND_BLOCK)) count += stack.getCount();
        }
        return count;
    }

    private static void removeDiamondBlocks(Player player, int amount) {
        for (ItemStack stack : player.getInventory().items) {
            if (amount <= 0) break;
            if (stack.is(Items.DIAMOND_BLOCK)) {
                int take = Math.min(amount, stack.getCount());
                stack.shrink(take);
                amount -= take;
            }
        }
    }

    private static void forgeFx(ServerLevel server, BlockPos pos) {
        server.sendParticles(ParticleTypes.ENCHANT,
            pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
            45, 0.45, 0.5, 0.45, 0.9);
        server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
            14, 0.3, 0.3, 0.3, 0.02);
        server.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.4f);
        server.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
    }
}

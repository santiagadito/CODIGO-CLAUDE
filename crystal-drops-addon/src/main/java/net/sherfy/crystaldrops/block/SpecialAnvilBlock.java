package net.sherfy.crystaldrops.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
 * A custom anvil that lets a single armor piece carry ALL protection
 * enchantments at once (Protection, Fire Protection, Blast Protection and
 * Projectile Protection), bypassing vanilla's mutual-exclusivity rule.
 *
 * Each protection added costs:
 *   - 16 diamond blocks
 *   - {@value #XP_LEVELS_PER_ENCHANT} experience levels
 *
 * Right-click the block while holding an armor piece in the main hand.
 * Every interaction adds as many missing protections as the player can afford,
 * in a fixed priority order. Because it is a placeable block, every player must
 * craft and place their own — nothing is shared.
 */
public class SpecialAnvilBlock extends AnvilBlock {

    public static final int DIAMOND_BLOCKS_PER_ENCHANT = 16;
    public static final int XP_LEVELS_PER_ENCHANT      = 10;
    private static final int PROTECTION_LEVEL          = 4; // max vanilla level

    /** Protection enchantments to apply, in priority order. */
    private static final Enchantment[] PROTECTIONS = new Enchantment[]{
        Enchantments.ALL_DAMAGE_PROTECTION,  // Protection
        Enchantments.BLAST_PROTECTION,        // Blast Protection
        Enchantments.PROJECTILE_PROTECTION,   // Projectile Protection
        Enchantments.FIRE_PROTECTION          // Fire Protection
    };

    public SpecialAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack armor = player.getMainHandItem();

        if (!(armor.getItem() instanceof ArmorItem)) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §fSostén una pieza de armadura en la mano principal."), true);
            return InteractionResult.CONSUME;
        }

        // Which protections are still missing (or below max level)?
        Map<Enchantment, Integer> current = new LinkedHashMap<>(EnchantmentHelper.getEnchantments(armor));
        int missing = 0;
        for (Enchantment prot : PROTECTIONS) {
            if (current.getOrDefault(prot, 0) < PROTECTION_LEVEL) missing++;
        }

        if (missing == 0) {
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §aEsta armadura ya tiene TODAS las protecciones al máximo."), true);
            return InteractionResult.CONSUME;
        }

        // How many can the player afford?
        int diamondBlocks = countDiamondBlocks(player);
        int affordByDiamonds = diamondBlocks / DIAMOND_BLOCKS_PER_ENCHANT;
        int affordByXp       = player.experienceLevel / XP_LEVELS_PER_ENCHANT;
        int toApply          = Math.min(missing, Math.min(affordByDiamonds, affordByXp));

        if (toApply <= 0) {
            int needBlocks = DIAMOND_BLOCKS_PER_ENCHANT;
            int needXp     = XP_LEVELS_PER_ENCHANT;
            player.displayClientMessage(Component.literal(
                "§dForja del Vacío §7» §cNecesitas §b" + needBlocks + " bloques de diamante §cy §a"
                + needXp + " niveles §cpor cada protección.\n§7Tienes: §b" + diamondBlocks
                + " bloques§7, §a" + player.experienceLevel + " niveles§7."), false);
            return InteractionResult.CONSUME;
        }

        // Apply the cheapest-missing protections first, charging per enchantment.
        int applied = 0;
        StringBuilder names = new StringBuilder();
        for (Enchantment prot : PROTECTIONS) {
            if (applied >= toApply) break;
            if (current.getOrDefault(prot, 0) >= PROTECTION_LEVEL) continue;

            current.put(prot, PROTECTION_LEVEL);
            removeDiamondBlocks(player, DIAMOND_BLOCKS_PER_ENCHANT);
            player.giveExperienceLevels(-XP_LEVELS_PER_ENCHANT);
            applied++;

            if (names.length() > 0) names.append("§7, ");
            names.append("§b").append(protName(prot));
        }

        EnchantmentHelper.setEnchantments(current, armor);

        // Feedback: sound, particles, chat
        ServerLevel server = (ServerLevel) level;
        server.sendParticles(ParticleTypes.ENCHANT,
            pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
            40, 0.45, 0.5, 0.45, 0.8);
        server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
            12, 0.3, 0.3, 0.3, 0.02);
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.4f);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);

        int remaining = missing - applied;
        player.displayClientMessage(Component.literal(
            "§dForja del Vacío §7» §fAplicado: " + names + " §7(§a" + applied + " protección(es)§7)."
            + (remaining > 0 ? " §8Faltan " + remaining + " — sigue pagando." : " §a¡Armadura completa!")),
            false);

        CrystalDropsLog(player, applied, remaining);
        return InteractionResult.CONSUME;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

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

    private static String protName(Enchantment prot) {
        if (prot == Enchantments.ALL_DAMAGE_PROTECTION) return "Protección";
        if (prot == Enchantments.BLAST_PROTECTION)      return "Prot. Explosiones";
        if (prot == Enchantments.PROJECTILE_PROTECTION) return "Prot. Proyectiles";
        if (prot == Enchantments.FIRE_PROTECTION)       return "Prot. Fuego";
        return "Protección";
    }

    private static void CrystalDropsLog(Player player, int applied, int remaining) {
        net.sherfy.crystaldrops.CrystalDropsMod.LOGGER.debug(
            "[ForjaDelVacio] {} applied {} protection(s), {} remaining",
            player.getName().getString(), applied, remaining);
    }
}

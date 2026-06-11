package net.sherfy.crystaldrops.nirvana;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

import java.util.Random;

/**
 * #1.4 "Barrio Antioquia" — the cannabis Dealer villager.
 *
 * Right-click a PROFESSIONAL villager while holding Nirvana hemp to convert it
 * into a Dealer ("Gibaro" / "Dealer" / "De la vuelta", chosen at random).
 *
 * Crucially, this does NOT reset the villager's existing trades — a Master
 * librarian keeps every enchanted book. We simply APPEND new cannabis trades on
 * top. Feeding more hemp upgrades the Dealer (up to level 3), unlocking better
 * cannabis trades each tier.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DealerVillagerHandler {

    private static final String NBT_LEVEL = "crystaldrops_dealer_level";
    private static final int    MAX_LEVEL = 3;
    private static final Random RNG = new Random();
    private static final String[] NAMES = { "Gibaro", "Dealer", "De la vuelta" };

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof Villager villager)) return;

        Player player = event.getEntity();
        ItemStack held = event.getItemStack();

        // Must be holding Nirvana hemp.
        if (!NirvanaRefs.isItem(held, NirvanaRefs.HEMP_BLOCK)) return;

        // Must be a real professional villager (keep their enchanted trades!).
        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT) {
            player.displayClientMessage(Component.literal(
                "§6Dealer §7» §fEste aldeano necesita una §eprofesión §fantes de meterse al negocio."), true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        CompoundTag data = villager.getPersistentData();
        int level = data.getInt(NBT_LEVEL);
        if (level >= MAX_LEVEL) {
            player.displayClientMessage(Component.literal(
                "§6Dealer §7» §aYa es un capo (nivel máximo). No necesita más mercancía."), true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // Consume one hemp.
        if (!player.isCreative()) held.shrink(1);

        level++;
        data.putInt(NBT_LEVEL, level);

        // First conversion: rename + grant achievement.
        if (level == 1) {
            String name = NAMES[RNG.nextInt(NAMES.length)];
            villager.setCustomName(Component.literal(name).withStyle(
                Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)));
            villager.setCustomNameVisible(true);

            if (player instanceof ServerPlayer sp) {
                CannabisAdvancements.grant(sp, CannabisAdvancements.BARRIO_ANTIOQUIA);
            }
        }

        // Append cannabis trades for the new tier — existing trades untouched.
        addCannabisTrades(villager.getOffers(), level);

        // FX
        ServerLevel level3d = (ServerLevel) event.getLevel();
        level3d.sendParticles(ParticleTypes.HAPPY_VILLAGER,
            villager.getX(), villager.getY() + 1.4, villager.getZ(),
            12, 0.4, 0.5, 0.4, 0.1);
        level3d.playSound(null, villager.blockPosition(),
            SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, 1.0f, 1.1f);

        player.displayClientMessage(Component.literal(
            "§6Dealer §7» §aTradeos de cannabis añadidos §7(nivel §e" + level + "§7/§e" + MAX_LEVEL + "§7)."
            + " §8Sus tradeos originales siguen intactos."), false);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        CrystalDropsMod.LOGGER.debug("[Dealer] {} upgraded a villager to dealer level {}",
            player.getName().getString(), level);
    }

    private static void addCannabisTrades(MerchantOffers offers, int level) {
        switch (level) {
            case 1 -> {
                addSell(offers, new ItemStack(Items.EMERALD, 1), NirvanaRefs.stack(NirvanaRefs.HEMP_SEEDS, 3), 16, 2);
                addBuy(offers, NirvanaRefs.stack(NirvanaRefs.HEMP_BLOCK, 12), new ItemStack(Items.EMERALD, 1), 16, 2);
            }
            case 2 -> {
                addSell(offers, new ItemStack(Items.EMERALD, 4), NirvanaRefs.stack(NirvanaRefs.JOINT, 2), 12, 5);
                addSell(offers, new ItemStack(Items.EMERALD, 6), NirvanaRefs.stack(NirvanaRefs.OLD_PIPE, 1), 8, 8);
            }
            case 3 -> {
                addSell(offers, new ItemStack(Items.EMERALD, 14), NirvanaRefs.stack(NirvanaRefs.BONG, 1), 5, 15);
                addSell(offers, new ItemStack(Items.EMERALD, 20), NirvanaRefs.stack(NirvanaRefs.POTION_BONG, 1), 4, 20);
            }
            default -> { /* nothing */ }
        }
    }

    /** Villager sells {@code result} for {@code cost} emeralds. */
    private static void addSell(MerchantOffers offers, ItemStack cost, ItemStack result, int maxUses, int xp) {
        if (result.isEmpty()) return;
        offers.add(new MerchantOffer(cost, result, maxUses, xp, 0.05f));
    }

    /** Villager buys {@code give} from the player for {@code result} emeralds. */
    private static void addBuy(MerchantOffers offers, ItemStack give, ItemStack result, int maxUses, int xp) {
        if (give.isEmpty()) return;
        offers.add(new MerchantOffer(give, result, maxUses, xp, 0.05f));
    }
}

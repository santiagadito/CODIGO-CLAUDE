package net.sherfy.crystaldrops.nirvana;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Cool pixel-art chat announcements broadcast to the whole server.
 * Uses Unicode block characters + colour codes to draw little signs.
 */
public final class ChatBanner {

    private ChatBanner() {}

    private static void broadcast(MinecraftServer server, String... lines) {
        for (String line : lines) {
            server.getPlayerList().broadcastSystemMessage(Component.literal(line), false);
        }
        // a little ding so nobody misses it
        server.getPlayerList().getPlayers().forEach(p ->
            p.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0f, 1.2f));
    }

    // ── #3 Smoking window opens (dusk → night) ───────────────────────────────

    public static void broadcastSmokeWindowOpen(MinecraftServer server) {
        broadcast(server,
            "§a§m                                          ",
            "§2     §a▄▄   §2LA HORA DE LA PAZ §a  ▄▄",
            "§2    §a███   §7Atardeció. §fFumen §2hierba",
            "§2   §a█████  §7para §acalmar la dificultad§7.",
            "§2     §a██   §8(ventana: atardecer → anochecer)",
            "§a§m                                          ");
    }

    // ── #2 Peace Day triggered (everyone smoked) ─────────────────────────────

    public static void broadcastPeaceDay(MinecraftServer server) {
        broadcast(server,
            "§2§m                                          ",
            "§a   ✌ §2§lP E A C E   D A Y §a✌",
            "§a    §2▄§a█§2▄  §fTodo el servidor fumó.",
            "§a   §2█§a█§2█§a█ §fLa dificultad cae a §a25% §fpor",
            "§a    §2▀§a█§2▀  §fun §aDÍA COMPLETO§f. §7Relájense.",
            "§2§m                                          ");
    }

    public static void broadcastPeaceDayEnd(MinecraftServer server, double restored) {
        broadcast(server,
            "§8§m                                          ",
            "§7   ☠ §c§lEL VIAJE TERMINÓ §7☠",
            "§7   La dificultad vuelve a §e" + Math.round(restored * 100) + "%§7.",
            "§8§m                                          ");
    }

    // ── #5 Crystal awakens (first achievement) ───────────────────────────────

    public static void broadcastAwaken(MinecraftServer server) {
        broadcast(server,
            "§5§m                                          ",
            "§d   ✦ §5§lCRYSTAL HA DESPERTADO §d✦",
            "§d   §7Alguien encontró la primera semilla.",
            "§d   §7La §5dificultad§7 del servidor §cdespierta§7.",
            "§5§m                                          ");
    }
}

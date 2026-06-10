package net.sherfy.crystaldrops.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.init.ServerDifficulty;

/**
 * /serverdifficulty            → shows the current global difficulty
 * /serverdifficulty set <pct>  → sets the global difficulty (25–300%)
 *
 * The global multiplier raises BOTH how often tough mobs spawn AND how hard
 * every mob hits / how much health & armor they get. It is the master knob for
 * the whole server experience (and tempers the combo with Dangerous Forge).
 *
 * Note: this controls Crystal Drops' own scaling. It cannot reach inside
 * Dangerous Forge's internal config, but because Crystal Drops drives the
 * difficulty_level every mob is built from, raising this makes the WHOLE
 * server harder in a balanced, predictable way.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SetServerDifficultyCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("serverdifficulty")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> query(ctx.getSource()))
                .then(Commands.literal("set")
                    .then(Commands.argument("percent", IntegerArgumentType.integer(25, 300))
                        .executes(ctx -> {
                            int pct = IntegerArgumentType.getInteger(ctx, "percent");
                            return set(ctx.getSource(), pct);
                        })
                    )
                )
        );
    }

    private static int query(CommandSourceStack source) {
        int pct = (int) Math.round(ServerDifficulty.getMultiplier() * 100);
        source.sendSuccess(() -> Component.literal(
            "§d[CrystalDrops] §7Dificultad global del servidor: §e" + pct + "%"
            + "\n§8Usa §f/serverdifficulty set <25-300> §8para cambiarla."), false);
        return pct;
    }

    private static int set(CommandSourceStack source, int percent) {
        ServerDifficulty.setMultiplier(source.getServer(), percent / 100.0);
        int applied = (int) Math.round(ServerDifficulty.getMultiplier() * 100);

        source.sendSuccess(() -> Component.literal(
            "§d[CrystalDrops] §aDificultad global ajustada a §e" + applied + "%§a."
            + "\n§7Afecta la aparición de mobs duros y su daño/vida/armadura."
            + "\n§8(Aplica a los mobs que aparezcan de ahora en adelante.)"), true);

        CrystalDropsMod.LOGGER.info("[ServerDifficulty] {} set global difficulty to {}%",
            source.getTextName(), applied);
        return applied;
    }
}

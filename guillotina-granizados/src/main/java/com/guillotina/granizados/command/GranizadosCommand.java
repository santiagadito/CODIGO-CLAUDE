package com.guillotina.granizados.command;

import com.guillotina.granizados.ModItems;
import com.guillotina.granizados.events.ModEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.List;
import java.util.Map;

/**
 * Comandos de testeo — solo OP (nivel 2).
 *
 *  /granizados give <nombre>        → entrega un granizado al jugador
 *  /granizados ingredientes         → entrega los 4 ingredientes (x3)
 *  /granizados todo                 → entrega todos los granizados + ingredientes
 *  /granizados simular <tier>       → simula drops de mob según el tier indicado
 *  /granizados efectos              → muestra tus efectos activos en el chat
 */
public class GranizadosCommand {

    // Lazy: evita llamar .get() antes de que Forge termine el registro
    private static Map<String, Item> getGranizados() {
        return Map.of(
                "quipitos",   ModItems.GRANIZADO_QUIPITOS.get(),
                "revolcon",   ModItems.GRANIZADO_REVOLCON.get(),
                "tamarindo",  ModItems.GRANIZADO_SMINORFF_TAMARINDO.get(),
                "bombon",     ModItems.GRANIZADO_BOMBON.get()
        );
    }

    private static List<Item> getIngredientes() {
        return List.of(
                ModItems.MEZCLA_QUIPITOS.get(),
                ModItems.MEZCLA_REVOLCON.get(),
                ModItems.TAMARINDO.get(),
                ModItems.MEZCLA_BOMBON.get()
        );
    }

    private static final List<String> TIERS = List.of(
            "WEAK", "COMMON", "COMMON+", "UNCOMMON", "RARE", "LEGENDARY"
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("granizados")
                .requires(src -> src.hasPermission(2))

                // /granizados give <nombre>
                .then(Commands.literal("give")
                    .then(Commands.argument("nombre", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            getGranizados().keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(GranizadosCommand::cmdGive)))

                // /granizados ingredientes
                .then(Commands.literal("ingredientes")
                    .executes(GranizadosCommand::cmdIngredientes))

                // /granizados todo
                .then(Commands.literal("todo")
                    .executes(GranizadosCommand::cmdTodo))

                // /granizados simular <tier>
                .then(Commands.literal("simular")
                    .then(Commands.argument("tier", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            TIERS.forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(GranizadosCommand::cmdSimular)))

                // /granizados efectos
                .then(Commands.literal("efectos")
                    .executes(GranizadosCommand::cmdEfectos))
        );
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private static int cmdGive(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = getPlayer(ctx);
        if (player == null) return 0;

        String nombre = StringArgumentType.getString(ctx, "nombre").toLowerCase();
        Item item = getGranizados().get(nombre);

        if (item == null) {
            ctx.getSource().sendFailure(Component.literal(
                    "§cGranizado desconocido: §e" + nombre +
                    "\n§7Opciones: " + String.join(", ", getGranizados().keySet())));
            return 0;
        }

        player.getInventory().add(new ItemStack(item, 3));
        ctx.getSource().sendSuccess(
                () -> Component.literal("§aEntregado §e3x §b" + nombre + "§a a §f" + player.getName().getString()),
                false);
        return 1;
    }

    private static int cmdIngredientes(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = getPlayer(ctx);
        if (player == null) return 0;

        for (Item ing : getIngredientes()) {
            player.getInventory().add(new ItemStack(ing, 3));
        }
        ctx.getSource().sendSuccess(
                () -> Component.literal("§aEntregados todos los ingredientes §7(x3 cada uno)"),
                false);
        return 1;
    }

    private static int cmdTodo(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = getPlayer(ctx);
        if (player == null) return 0;

        for (Item g : getGranizados().values()) player.getInventory().add(new ItemStack(g, 3));
        for (Item i : getIngredientes())        player.getInventory().add(new ItemStack(i, 3));

        ctx.getSource().sendSuccess(
                () -> Component.literal("§aEntregados §etodos§a los granizados e ingredientes §7(x3 cada uno)"),
                false);
        return 1;
    }

    private static int cmdSimular(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = getPlayer(ctx);
        if (player == null) return 0;

        String tier = StringArgumentType.getString(ctx, "tier").toUpperCase();
        if (!TIERS.contains(tier)) {
            ctx.getSource().sendFailure(Component.literal(
                    "§cTier inválido: §e" + tier +
                    "\n§7Tiers: " + String.join(", ", TIERS)));
            return 0;
        }

        // Simula qué dropearia un mob de ese tier y lo spawna en el jugador
        List<Item> dropped = ModEvents.simulateDrop(tier, player.getRandom());

        if (dropped.isEmpty()) {
            ctx.getSource().sendSuccess(
                    () -> Component.literal("§7[Tier §e" + tier + "§7] El mob no soltó ningún ingrediente esta vez."),
                    false);
        } else {
            for (Item item : dropped) {
                int count = getDropCount(tier);
                ItemEntity ie = new ItemEntity(player.level(),
                        player.getX(), player.getY(), player.getZ(),
                        new ItemStack(item, count));
                ie.setDefaultPickUpDelay();
                player.level().addFreshEntity(ie);
            }
            String nombres = dropped.stream()
                    .map(Item::getDescriptionId)
                    .map(id -> id.replace("item.guillotina_granizados.", ""))
                    .reduce((a, b) -> a + ", " + b).orElse("");
            ctx.getSource().sendSuccess(
                    () -> Component.literal("§a[Tier §e" + tier + "§a] Simulado. Drops: §f" + nombres),
                    false);
        }
        return 1;
    }

    private static int cmdEfectos(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = getPlayer(ctx);
        if (player == null) return 0;

        var effects = player.getActiveEffects();
        if (effects.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("§7No tienes efectos activos."), false);
            return 1;
        }

        StringBuilder sb = new StringBuilder("§eEfectos activos:\n");
        for (MobEffectInstance e : effects) {
            int secsLeft = e.getDuration() / 20;
            int mins = secsLeft / 60, secs = secsLeft % 60;
            sb.append(String.format("  §b%s §7Nivel %d — §a%dm %ds restantes\n",
                    e.getEffect().getDisplayName().getString(),
                    e.getAmplifier() + 1, mins, secs));
        }
        String msg = sb.toString();
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return 1;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> ctx) {
        try {
            return ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cEste comando solo funciona como jugador."));
            return null;
        }
    }

    private static int getDropCount(String tier) {
        return switch (tier) {
            case "UNCOMMON", "RARE", "LEGENDARY" -> 2;
            default -> 1;
        };
    }
}

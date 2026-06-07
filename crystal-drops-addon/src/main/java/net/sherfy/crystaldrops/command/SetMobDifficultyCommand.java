package net.sherfy.crystaldrops.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.init.DifficultyCalculator;

import java.util.List;

/**
 * /setmobdifficulty <value>
 *
 * Sets difficulty_level on the living entity the player is looking at
 * (raycast up to 10 blocks). Requires permission level 2 (op).
 *
 * Usage examples:
 *   /setmobdifficulty 85   → applies legendary tier to the target mob
 *   /setmobdifficulty 0    → resets to baseline
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SetMobDifficultyCommand {

    private static final double REACH = 10.0;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("setmobdifficulty")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        double value = DoubleArgumentType.getDouble(ctx, "value");
                        return applyToLookedEntity(player, value);
                    })
                )
        );
    }

    private static int applyToLookedEntity(ServerPlayer player, double value) {
        LivingEntity target = getLookedEntity(player);

        if (target == null) {
            player.sendSystemMessage(Component.literal(
                "§cNo se encontró ningún mob en la dirección que miras (alcance: " + (int) REACH + " bloques)."));
            return 0;
        }

        AttributeInstance attr = target.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());

        if (attr == null) {
            player.sendSystemMessage(Component.literal(
                "§cEste mob no tiene el atributo difficulty_level. "
                + "¿Crystal Leveling Mod está instalado?"));
            return 0;
        }

        double oldValue = attr.getBaseValue();
        attr.setBaseValue(value);
        String tier = DifficultyCalculator.tierName(value);

        player.sendSystemMessage(Component.literal(String.format(
            "§a[CrystalDrops] §f%s §7| difficulty: §e%.0f §7→ §b%.0f §7(tier: §d%s§7)",
            target.getName().getString(), oldValue, value, tier)));

        CrystalDropsMod.LOGGER.info("[SetMobDifficulty] {} set {} from {} to {} ({})",
            player.getName().getString(),
            target.getName().getString(),
            oldValue, value, tier);

        return 1;
    }

    /**
     * Raycasts from the player's eye position and returns the nearest
     * LivingEntity (non-player) within REACH blocks.
     */
    private static LivingEntity getLookedEntity(ServerPlayer player) {
        Vec3 eyePos  = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos  = eyePos.add(lookVec.scale(REACH));

        AABB searchBox = player.getBoundingBox()
            .expandTowards(lookVec.scale(REACH))
            .inflate(2.0);

        List<Entity> candidates = player.level().getEntities(player, searchBox,
            e -> e instanceof LivingEntity && !(e instanceof net.minecraft.world.entity.player.Player));

        LivingEntity closest = null;
        double closestDist   = Double.MAX_VALUE;

        for (Entity candidate : candidates) {
            AABB entityBox = candidate.getBoundingBox().inflate(0.3);
            var hitResult  = entityBox.clip(eyePos, endPos);
            if (hitResult.isPresent()) {
                double dist = eyePos.distanceTo(hitResult.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest     = (LivingEntity) candidate;
                }
            }
        }

        return closest;
    }
}

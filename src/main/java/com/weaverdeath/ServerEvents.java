package com.weaverdeath;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ServerEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!Config.ENABLED.get()) {
            return;
        }
        Net.sendToAll(player.getGameProfile().getName());
    }

    /** /weaverdeath test [nombre]  -> prueba la animacion sin morir (solo operadores). */
    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("weaverdeath")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("test")
                                .executes(ctx -> {
                                    String name = ctx.getSource().getTextName();
                                    Net.sendToAll(name);
                                    return 1;
                                })
                                .then(Commands.argument("nombre", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Net.sendToAll(StringArgumentType.getString(ctx, "nombre"));
                                            return 1;
                                        }))));
    }

    private ServerEvents() {
    }
}

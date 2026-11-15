package com.weaverdeath.client;

import com.weaverdeath.WeaverDeathMod;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class ClientEvents {

    /** Eventos del bus del mod. */
    @Mod.EventBusSubscriber(modid = WeaverDeathMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        @SubscribeEvent
        public static void onOverlays(RegisterGuiOverlaysEvent event) {
            // Se dibuja encima de todo el HUD (solo cuando no hay ninguna pantalla abierta)
            event.registerAboveAll("animacion_muerte", (gui, graphics, partialTick, width, height) -> {
                if (net.minecraft.client.Minecraft.getInstance().screen == null) {
                    ClientAnim.render(graphics, width, height);
                }
            });
        }

        @SubscribeEvent
        public static void onReload(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener((ResourceManagerReloadListener) rm -> FrameCache.invalidate());
        }
    }

    /** Eventos del bus de Forge. */
    @Mod.EventBusSubscriber(modid = WeaverDeathMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {
        @SubscribeEvent
        public static void onTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                ClientAnim.clientTick();
            }
        }

        /** Si hay una pantalla abierta (por ejemplo la de "Has muerto"), se dibuja encima de ella. */
        @SubscribeEvent
        public static void onScreenRender(ScreenEvent.Render.Post event) {
            ClientAnim.render(event.getGuiGraphics(), event.getScreen().width, event.getScreen().height);
        }

        @SubscribeEvent
        public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            FrameCache.prewarm();
        }

        @SubscribeEvent
        public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientAnim.reset();
        }
    }

    private ClientEvents() {
    }
}

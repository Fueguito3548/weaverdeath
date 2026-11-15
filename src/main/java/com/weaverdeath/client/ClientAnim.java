package com.weaverdeath.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.weaverdeath.Config;
import com.weaverdeath.ModSounds;
import com.weaverdeath.WeaverDeathMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Reproduce la animacion: 1 fotograma por tick (20 fps), igual que en el script de Blender.
 * Los sonidos se disparan en el mismo contador de ticks, asi siempre van sincronizados con la imagen.
 */
public final class ClientAnim {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WeaverDeathMod.MODID, "dynamic/frame");
    private static final int RED = 0xFF3030;

    private record Snd(int tick, Supplier<SoundEvent> event) {
    }

    private static final List<Snd> SOUNDS = new ArrayList<>();

    private static void s(double seconds, Supplier<SoundEvent> ev) {
        SOUNDS.add(new Snd((int) Math.round(seconds * 20), ev));
    }

    static {
        s(0.0, ModSounds.RISE);
        s(0.2, ModSounds.ORB_APPEAR);
        s(0.3, ModSounds.DARK_BURST);
        s(1.3, ModSounds.MASK_APPEAR);
        s(1.8, ModSounds.MOUTH_OPEN);
        s(2.2, ModSounds.TREMBLE);
        s(3.65, ModSounds.ORB_ENTER);
        s(4.8, ModSounds.EYES);
        s(5.0, ModSounds.LIGHT_GROW);
        s(6.65, ModSounds.SNAP);
        s(6.7, ModSounds.WAIL);
        s(6.8, ModSounds.SMOKE);
        s(8.45, ModSounds.VANISH);
        s(8.65, ModSounds.DARK_BURST);
    }

    private static final List<SoundInstance> PLAYING = new ArrayList<>();
    private static String pending = null;
    private static String current = null;
    private static int tick = -1;
    private static int total = 200;

    private static DynamicTexture texture = null;
    private static int texW = 0;
    private static int texH = 0;
    private static int uploadedFrame = -1;

    // ------------------------------------------------------------------ control
    /** Inicia la animacion. Si ya habia una en curso, se corta y empieza esta de cero. */
    public static void start(String name) {
        pending = name;
    }

    private static void stopSounds() {
        Minecraft mc = Minecraft.getInstance();
        for (SoundInstance si : PLAYING) {
            mc.getSoundManager().stop(si);
        }
        PLAYING.clear();
    }

    public static void reset() {
        stopSounds();
        pending = null;
        current = null;
        tick = -1;
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            reset();
            return;
        }
        if (pending != null) {
            stopSounds();
            current = pending;
            pending = null;
            tick = -1;
            uploadedFrame = -1;
            int n = FrameCache.count();
            total = n > 0 ? n : 200;
            FrameCache.prewarm();
        }
        if (current == null) {
            return;
        }
        tick++;
        if (Config.SOUND_ENABLED.get()) {
            for (Snd snd : SOUNDS) {
                if (snd.tick() == tick) {
                    SoundInstance si = SimpleSoundInstance.forUI(snd.event().get(), 1.0F,
                            Config.VOLUME.get().floatValue());
                    mc.getSoundManager().play(si);
                    PLAYING.add(si);
                }
            }
        }
        if (tick >= total + 2) {
            current = null;
            tick = -1;
            PLAYING.clear();
        }
    }

    // ------------------------------------------------------------------ dibujo
    public static void render(GuiGraphics g, int screenW, int screenH) {
        if (current == null || tick < 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        float size = screenH * Config.SIZE_PERCENT.get().floatValue() / 100.0F;
        float top = screenH * Config.TOP_PERCENT.get().floatValue() / 100.0F;

        // ---- imagen
        int frame = Math.min(tick, Math.max(0, total - 1));
        if (frame != uploadedFrame) {
            NativeImage img = FrameCache.get(frame);
            if (img != null) {
                upload(mc, img);
                uploadedFrame = frame;
            }
        }
        float bottom = top + size;
        if (texture != null && texW > 0 && texH > 0) {
            float drawH = size;
            float drawW = size * texW / texH;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.pose().pushPose();
            g.pose().translate((screenW - drawW) / 2.0F, top, 0);
            g.pose().scale(drawW / texW, drawH / texH, 1.0F);
            g.blit(TEXTURE, 0, 0, 0, 0.0F, 0.0F, texW, texH, texW, texH);
            g.pose().popPose();
            RenderSystem.disableBlend();
        }

        // ---- texto (aparece cuando aparece la mascara y se va antes de que termine)
        float alpha = textAlpha();
        int a = (int) (alpha * 255.0F);
        if (a > 8) {
            Font font = mc.font;
            Component line1 = Component.literal(Config.LINE1.get()).withStyle(ChatFormatting.BOLD);
            Component line2 = Component.literal(current).withStyle(ChatFormatting.BOLD);

            // La primera linea nunca es mas ancha que un % del tamano de la mascara (los cuernos)
            float w1 = Math.max(1, font.width(line1));
            float maxW = size * Config.TEXT_WIDTH_PERCENT.get().floatValue() / 100.0F;
            float s1 = Math.min(Config.TEXT_SCALE.get().floatValue(), maxW / w1);
            // El nombre es un poco mas grande que la linea 1, pero nunca mas ancho que ella
            float w2 = Math.max(1, font.width(line2));
            float s2 = Math.min(s1 * Config.NAME_RATIO.get().floatValue(), (w1 * s1) / w2);

            float cx = screenW / 2.0F;
            float y1 = bottom + size * Config.TEXT_GAP_PERCENT.get().floatValue() / 100.0F;
            float y2 = y1 + 9.0F * s1 + 1.0F * s1;

            drawCentered(g, font, line1, cx, y1, s1, (a << 24) | 0xFFFFFF);
            drawCentered(g, font, line2, cx, y2, s2, (a << 24) | RED);
        }
    }

    private static void drawCentered(GuiGraphics g, Font font, Component text, float cx, float y, float scale,
                                     int argb) {
        PoseStack ps = g.pose();
        ps.pushPose();
        ps.translate(cx, y, 0);
        ps.scale(scale, scale, 1.0F);
        g.drawString(font, text, -font.width(text) / 2, 0, argb, true);
        ps.popPose();
    }

    private static float textAlpha() {
        int fadeInStart = 26;                 // 1.3 s: aparece la mascara
        int fadeInEnd = 36;
        int fadeOutStart = Math.max(fadeInEnd + 1, total - 24);
        int fadeOutEnd = Math.max(fadeOutStart + 1, total - 4);
        if (tick < fadeInStart) {
            return 0.0F;
        }
        if (tick < fadeInEnd) {
            return (tick - fadeInStart) / (float) (fadeInEnd - fadeInStart);
        }
        if (tick < fadeOutStart) {
            return 1.0F;
        }
        if (tick < fadeOutEnd) {
            return 1.0F - (tick - fadeOutStart) / (float) (fadeOutEnd - fadeOutStart);
        }
        return 0.0F;
    }

    private static void upload(Minecraft mc, NativeImage img) {
        if (texture == null || texW != img.getWidth() || texH != img.getHeight()) {
            if (texture != null) {
                mc.getTextureManager().release(TEXTURE);
            }
            texW = img.getWidth();
            texH = img.getHeight();
            texture = new DynamicTexture(texW, texH, true);
            mc.getTextureManager().register(TEXTURE, texture);
        }
        NativeImage dst = texture.getPixels();
        if (dst != null) {
            dst.copyFrom(img);
            texture.upload();
        }
    }

    public static void releaseTexture() {
        if (texture != null) {
            Minecraft.getInstance().getTextureManager().release(TEXTURE);
            texture = null;
            texW = 0;
            texH = 0;
            uploadedFrame = -1;
        }
    }

    private ClientAnim() {
    }
}

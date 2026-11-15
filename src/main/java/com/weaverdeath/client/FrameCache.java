package com.weaverdeath.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.weaverdeath.WeaverDeathMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Carga los fotogramas PNG en un hilo aparte (solo guarda unos pocos por delante en memoria).
 * Primero busca  <juego>/weaverdeath/frames/*.png ; si esta vacia, usa los que vienen dentro del mod.
 */
public final class FrameCache {
    private static final int LOOKAHEAD = 40;
    private static final int PINNED = 24;   // los primeros fotogramas se quedan en memoria (para repetir rapido)

    private interface FrameSource {
        InputStream open() throws IOException;
    }

    private static List<FrameSource> sources = null;
    private static volatile int generation = 0;
    private static final ConcurrentHashMap<Integer, NativeImage> READY = new ConcurrentHashMap<>();
    private static final Set<Integer> PENDING = ConcurrentHashMap.newKeySet();
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "weaverdeath-frames");
        t.setDaemon(true);
        return t;
    });

    private static synchronized List<FrameSource> sources() {
        if (sources == null) {
            sources = scan();
        }
        return sources;
    }

    public static int count() {
        return sources().size();
    }

    /** Llamar cuando se recargan los recursos (F3+T). */
    public static synchronized void invalidate() {
        generation++;
        sources = null;
        for (NativeImage img : READY.values()) {
            img.close();
        }
        READY.clear();
        PENDING.clear();
    }

    public static void prewarm() {
        request(0);
    }

    /** Devuelve el fotograma si ya esta cargado (o null si todavia no). */
    public static NativeImage get(int index) {
        request(index);
        for (Integer k : READY.keySet()) {
            if (k >= PINNED && k < index - 2) {
                NativeImage old = READY.remove(k);
                PENDING.remove(k);
                if (old != null) {
                    old.close();
                }
            }
        }
        return READY.get(index);
    }

    private static void request(int from) {
        List<FrameSource> list = sources();
        int gen = generation;
        int to = Math.min(list.size() - 1, from + LOOKAHEAD);
        for (int i = Math.max(0, from); i <= to; i++) {
            if (!READY.containsKey(i) && PENDING.add(i)) {
                final int idx = i;
                final FrameSource src = list.get(i);
                POOL.execute(() -> {
                    try (InputStream in = src.open()) {
                        NativeImage img = NativeImage.read(in);
                        if (gen != generation) {
                            img.close();
                        } else {
                            READY.put(idx, img);
                        }
                    } catch (Exception e) {
                        // si un fotograma falla simplemente se salta
                    }
                });
            }
        }
    }

    private static List<FrameSource> scan() {
        List<FrameSource> list = new ArrayList<>();

        Path dir = FMLPaths.GAMEDIR.get().resolve("weaverdeath").resolve("frames");
        if (Files.isDirectory(dir)) {
            try (var stream = Files.list(dir)) {
                stream.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                        .sorted()
                        .forEach(p -> list.add(() -> Files.newInputStream(p)));
            } catch (IOException ignored) {
            }
        }
        if (!list.isEmpty()) {
            return list;
        }

        Map<ResourceLocation, Resource> found = Minecraft.getInstance().getResourceManager().listResources(
                "frames",
                rl -> rl.getNamespace().equals(WeaverDeathMod.MODID) && rl.getPath().endsWith(".png"));
        found.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    final Resource res = e.getValue();
                    list.add(res::open);
                });
        return list;
    }

    private FrameCache() {
    }
}

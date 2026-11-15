package com.weaverdeath;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Registra la carpeta  <juego>/weaverdeath/  como un paquete de recursos que va POR ENCIMA del mod.
 * Todo .ogg que el jugador ponga en  weaverdeath/assets/weaverdeath/sounds/  con el mismo nombre
 * reemplaza al sonido original (sin recompilar nada).
 */
public final class SoundPack {

    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        Path root = FMLPaths.GAMEDIR.get().resolve("weaverdeath");
        try {
            Files.createDirectories(root.resolve("assets").resolve(WeaverDeathMod.MODID).resolve("sounds"));
            Files.createDirectories(root.resolve("frames"));
            Path meta = root.resolve("pack.mcmeta");
            if (!Files.exists(meta)) {
                Files.writeString(meta,
                        "{\"pack\":{\"description\":\"Sonidos propios de Weaver Death\",\"pack_format\":15}}",
                        StandardCharsets.UTF_8);
            }
            Path readme = root.resolve("LEEME.txt");
            if (!Files.exists(readme)) {
                Files.writeString(readme, README, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            return;
        }

        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(
                    "weaverdeath_custom",
                    Component.literal("Weaver Death (tus sonidos)"),
                    true,
                    id -> new PathPackResources(id, root, true),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN);
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }

    private static final String README = String.join("\n",
            "WEAVER DEATH - carpeta de personalizacion",
            "",
            "SONIDOS: pon tus .ogg en  assets/weaverdeath/sounds/  con ESTOS nombres para reemplazar los originales:",
            "  rise.ogg         fondo que sube de volumen (dura ~6.6 s, empieza al inicio)",
            "  orb_appear.ogg   aparece la luz (tipo experiencia)       (0.2 s)",
            "  dark_burst.ogg   rafaga de humo negro                    (0.3 s y 8.65 s)",
            "  mask_appear.ogg  aparece la mascara                      (1.3 s)",
            "  mouth_open.ogg   abre la boca                            (1.8 s)",
            "  tremble.ogg      temblor                                 (2.2 s)",
            "  orb_enter.ogg    la luz entra en la boca                 (3.65 s)",
            "  eyes.ogg         brillan los ojos                        (4.8 s)",
            "  light_grow.ogg   la luz crece dentro de la boca          (5.0 s)",
            "  snap.ogg         cierra la boca de golpe                 (6.65 s)",
            "  wail.ogg         lamento / grito de alma al cerrar la boca (6.7 s)",
            "  smoke.ogg        humo por la boca                        (6.8 s)",
            "  vanish.ogg       la mascara desaparece                   (8.45 s)",
            "Los .ogg deben ser OGG Vorbis (no mp3, no wav). Para silenciar uno, reemplazalo por un .ogg en silencio.",
            "Despues de cambiar archivos, dentro del juego pulsa F3 + T para recargar.",
            "",
            "FOTOGRAMAS: si pones los PNG de Blender en la carpeta  frames/  se usan ESOS en lugar de los que trae el mod",
            "(sirve para probar una animacion nueva sin recompilar). Se ordenan por nombre.",
            "",
            "TAMANO, POSICION, TEXTO Y VOLUMEN: config/weaverdeath-client.toml",
            "");

    private SoundPack() {
    }
}

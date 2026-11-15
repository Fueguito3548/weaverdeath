package com.weaverdeath;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;

    // ---- servidor / comun
    public static final ForgeConfigSpec.BooleanValue ENABLED;

    // ---- cliente (cada jugador)
    public static final ForgeConfigSpec.BooleanValue SOUND_ENABLED;
    public static final ForgeConfigSpec.DoubleValue VOLUME;
    public static final ForgeConfigSpec.DoubleValue SIZE_PERCENT;
    public static final ForgeConfigSpec.DoubleValue TOP_PERCENT;
    public static final ForgeConfigSpec.DoubleValue TEXT_GAP_PERCENT;
    public static final ForgeConfigSpec.DoubleValue TEXT_SCALE;
    public static final ForgeConfigSpec.DoubleValue TEXT_WIDTH_PERCENT;
    public static final ForgeConfigSpec.DoubleValue NAME_RATIO;
    public static final ForgeConfigSpec.ConfigValue<String> LINE1;

    static {
        ForgeConfigSpec.Builder c = new ForgeConfigSpec.Builder();
        c.push("servidor");
        ENABLED = c.comment("Si es false, el servidor no manda la animacion cuando alguien muere.")
                .define("activado", true);
        c.pop();
        COMMON_SPEC = c.build();

        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("sonido");
        SOUND_ENABLED = b.comment("Activar o desactivar todos los sonidos de la animacion.")
                .define("sonidos", true);
        VOLUME = b.comment("Volumen de los sonidos de la animacion (1.0 = normal).")
                .defineInRange("volumen", 1.0, 0.0, 2.0);
        b.pop();

        b.push("imagen");
        SIZE_PERCENT = b.comment("Alto de la animacion como porcentaje del alto de la pantalla.")
                .defineInRange("tamano_animacion_porcentaje", 60.0, 5.0, 100.0);
        TOP_PERCENT = b.comment("Distancia desde arriba, como porcentaje del alto de la pantalla.")
                .defineInRange("arriba_porcentaje", 3.0, -20.0, 90.0);
        b.pop();

        b.push("texto");
        LINE1 = b.comment("Primera linea de texto (blanca). El nombre del jugador va debajo, en rojo.")
                .define("linea1", "Mongrel ha consumido la conciencia de");
        TEXT_GAP_PERCENT = b.comment("Separacion entre la imagen y el texto, como % del tamano de la animacion. Negativo = mas pegado a la mascara.")
                .defineInRange("separacion_texto_porcentaje", -8.0, -50.0, 50.0);
        TEXT_WIDTH_PERCENT = b.comment("Ancho maximo de la primera linea, como % del tamano de la animacion (60 = hasta los cuernos).")
                .defineInRange("ancho_maximo_texto_porcentaje", 60.0, 10.0, 150.0);
        TEXT_SCALE = b.comment("Tamano maximo de la primera linea (si es mas ancha que el limite, se achica sola).")
                .defineInRange("tamano_maximo_linea1", 1.5, 0.3, 5.0);
        NAME_RATIO = b.comment("Cuantas veces mas grande que la primera linea es el nombre (nunca pasa de su ancho).")
                .defineInRange("proporcion_nombre", 1.45, 1.0, 3.0);
        b.pop();

        CLIENT_SPEC = b.build();
    }

    private Config() {
    }
}

# Weaver Death (Forge 1.20.1 · 47.4.28)

Cuando muere cualquier jugador, a **todos** les aparece la animación de la máscara arriba al centro,
con el texto debajo ("Mongrel ha consumido la conciencia de" en blanco y el nombre en rojo) y con sonidos.
El mod va en el servidor **y** en el cliente de cada jugador (lo mismo que tu launcher ya sincroniza).

## 1. Poner los fotogramas
1. En Blender: `MODE = "RENDER"` y ejecuta el script. Salen ~201 PNG en `weaver_frames`.
2. Copia **todos** esos PNG a `src/main/resources/assets/weaverdeath/frames/` (sin cambiarles el nombre).

## 2. Compilar (GitHub Actions, como el otro mod)
1. Crea un repositorio nuevo y arrastra todo el contenido de esta carpeta (incluida `.github`).
   GitHub solo deja subir 100 archivos por vez: sube los PNG en dos tandas.
   Si `.github` no se deja arrastrar: *Add file → Create new file*, nombre `.github/workflows/build.yml`, y pega el contenido.
2. Pestaña **Actions** → "Compilar mod" → cuando termine en verde, abre la ejecución y baja `weaverdeath-mod`.
3. Dentro del zip está el `.jar`: ponlo en `mods` del servidor y de los jugadores.

## 3. Probar sin morir
En el juego, siendo operador: `/weaverdeath test` (o `/weaverdeath test NombreDeJugador`).

## 4. Ajustes (cada jugador, `config/weaverdeath-client.toml`)
Volumen, sonidos on/off, tamaño de la animación, posición, separación del texto, tamaños del texto, el texto de la primera línea.
En el servidor, `config/weaverdeath-common.toml` → `activado = false` apaga el efecto.

## 5. Cambiar los sonidos
Carpeta `weaverdeath/` dentro de la carpeta del juego (el mod la crea sola al abrir el juego).
Pon tus `.ogg` en `weaverdeath/assets/weaverdeath/sounds/` con el mismo nombre que el original y pulsa **F3 + T**.
La lista de nombres y en qué segundo suena cada uno está en `weaverdeath/LEEME.txt`.
Los sonidos tienen que ser OGG Vorbis.

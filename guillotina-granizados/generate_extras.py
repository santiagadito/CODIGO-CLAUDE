#!/usr/bin/env python3
"""
Genera texturas para los ingredientes y el bloque Guillotina.
"""
from PIL import Image, ImageDraw
import os

ITEM_DIR  = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/item"
BLOCK_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/block"
os.makedirs(ITEM_DIR, exist_ok=True)
os.makedirs(BLOCK_DIR, exist_ok=True)

T  = (0, 0, 0, 0)
W  = (255, 255, 255, 255)
K  = (30, 30, 30, 255)

# ─── Ingredientes (16x16 bolsita/polvo) ─────────────────────────────────────
# Forma: bolsita cuadrada con nudo arriba y color del granizado
INGREDIENTS = {
    "mezcla_quipitos":  ((220, 220, 220), (170, 170, 170), "Q"),
    "mezcla_revolcon":  (( 40, 170,  60), ( 25, 130,  45), "R"),
    "tamarindo":        ((180, 100,  30), (130,  65,  15), "T"),
    "mezcla_bombon":    ((240,  90, 160), (200,  55, 125), "B"),
}

# Pixel art de bolsita 16x16
BAG_TEMPLATE = [
    #0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
    [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 1, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 1, 2, 2, 9, 2, 2, 2, 1, 0, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 9, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 9, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],
    [ 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 1, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 0, 1, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0],
    [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
]

def make_ingredient(name, main_rgb, shade_rgb):
    img = Image.new("RGBA", (16, 16), T)
    px  = img.load()
    hi  = tuple(min(255, c + 50) for c in main_rgb) + (255,)
    palette = {
        0: T,
        1: (30, 30, 30, 255),
        2: main_rgb + (255,),
        9: hi,
    }
    for y, row in enumerate(BAG_TEMPLATE):
        for x, code in enumerate(row):
            px[x, y] = palette[code]
    img.save(os.path.join(ITEM_DIR, f"{name}.png"), "PNG")
    print(f"  ✓ {name}.png")

print("Generando ingredientes...")
for name, (main, shade, _) in INGREDIENTS.items():
    make_ingredient(name, main, shade)

# ─── Bloque Guillotina (16x16) ───────────────────────────────────────────────
# Aspecto: máquina metálica azul oscuro con detalles plateados y "G"
def make_guillotina_block():
    METAL_DARK  = ( 35,  45,  80, 255)  # azul oscuro (como el vaso)
    METAL_MID   = ( 55,  70, 120, 255)
    METAL_LIGHT = ( 90, 110, 170, 255)
    SILVER      = (180, 190, 200, 255)
    SILVER_D    = (120, 130, 145, 255)
    WHITE       = (235, 235, 245, 255)
    RED_ACC     = (200,  40,  40, 255)

    # 16x16 pixel art de cara frontal de la máquina
    G = METAL_DARK
    M = METAL_MID
    L = METAL_LIGHT
    S = SILVER
    D = SILVER_D
    W = WHITE
    R = RED_ACC
    _ = T

    grid = [
        [D, S, S, S, S, S, S, S, S, S, S, S, S, S, S, D],
        [S, G, G, G, G, G, G, G, G, G, G, G, G, G, G, S],
        [S, G, D, D, D, D, D, D, D, D, D, D, D, D, G, S],
        [S, G, D, M, M, M, M, M, M, M, M, M, M, D, G, S],
        [S, G, D, M, L, W, W, W, W, W, W, W, L, M, G, S],
        [S, G, D, M, W, G, G, M, M, G, G, W, W, M, G, S],
        [S, G, D, M, W, G, M, M, M, M, G, W, W, M, G, S],
        [S, G, D, M, W, M, M, W, W, M, M, W, W, M, G, S],
        [S, G, D, M, W, M, W, W, W, W, M, W, W, M, G, S],
        [S, G, D, M, W, G, G, M, M, G, G, W, W, M, G, S],
        [S, G, D, M, L, W, W, W, W, W, W, W, L, M, G, S],
        [S, G, D, M, M, D, R, R, R, R, D, M, M, D, G, S],
        [S, G, D, D, D, D, D, D, D, D, D, D, D, D, G, S],
        [S, G, G, G, G, G, G, G, G, G, G, G, G, G, G, S],
        [S, D, D, D, D, D, D, D, D, D, D, D, D, D, D, S],
        [D, S, S, S, S, S, S, S, S, S, S, S, S, S, S, D],
    ]

    img = Image.new("RGBA", (16, 16), T)
    px  = img.load()
    for y, row in enumerate(grid):
        for x, color in enumerate(row):
            px[x, y] = color

    img.save(os.path.join(BLOCK_DIR, "guillotina.png"), "PNG")
    print("  ✓ guillotina.png (bloque)")

print("Generando bloque Guillotina...")
make_guillotina_block()

print("\n¡Todas las texturas generadas!")

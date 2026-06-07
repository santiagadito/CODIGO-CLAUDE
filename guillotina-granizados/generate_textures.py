#!/usr/bin/env python3
"""
Genera texturas 16x16 de los granizados de Guillotina Granizados.
Diseño: vaso con pajita (straw), cuerpo coloreado, logo blanco pequeño, hielo encima.
"""
from PIL import Image
import os

TEXTURES_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/item"
os.makedirs(TEXTURES_DIR, exist_ok=True)

# ─── Paleta por granizado ────────────────────────────────────────────────────
# (cup_main, cup_shade, cup_dark, ice_color, candy_color)
VARIANTS = {
    "granizado_quipitos": {
        "main":   (230, 230, 230, 255),   # blanco
        "shade":  (190, 190, 190, 255),   # gris claro
        "dark":   (140, 140, 140, 255),   # borde gris
        "text":   ( 80,  80,  80, 255),   # texto oscuro
        "ice":    (200, 230, 255, 255),   # hielo azulado
        "candy1": (255,  80,  80, 255),   # dulce rojo
        "candy2": ( 80, 200,  80, 255),   # dulce verde
    },
    "granizado_revolcon": {
        "main":   ( 40, 170,  60, 255),   # verde
        "shade":  ( 25, 130,  45, 255),   # verde oscuro
        "dark":   ( 15,  90,  30, 255),   # borde verde muy oscuro
        "text":   (255, 255, 255, 255),
        "ice":    (180, 230, 185, 255),
        "candy1": (255,  80,  80, 255),
        "candy2": (255, 200,  50, 255),
    },
    "granizado_sminorff_tamarindo": {
        "main":   (210,  35,  35, 255),   # rojo
        "shade":  (160,  20,  20, 255),   # rojo oscuro
        "dark":   (110,  10,  10, 255),   # borde muy oscuro
        "text":   (255, 255, 255, 255),
        "ice":    (255, 160, 160, 255),
        "candy1": (255, 200,  50, 255),
        "candy2": (255, 120,  20, 255),
    },
    "granizado_bombon": {
        "main":   (240,  90, 160, 255),   # rosado
        "shade":  (200,  55, 125, 255),   # rosado oscuro
        "dark":   (150,  30,  90, 255),   # borde oscuro
        "text":   (255, 255, 255, 255),
        "ice":    (255, 200, 220, 255),
        "candy1": (200,  50, 200, 255),
        "candy2": (255,  80,  80, 255),
    },
}

# ─── Pixel art del vaso (16x16) ─────────────────────────────────────────────
# Leyenda: 0=transparente, 1=borde, 2=cuerpo principal, 3=sombra lateral,
#          4=hielo, 5=straw, 6=dulce1, 7=dulce2, 8=logo blanco, 9=highlight
TEMPLATE = [
    # 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
    [  0, 0, 0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # 0  straw
    [  0, 0, 0, 0, 6, 5, 5, 7, 0, 6, 0, 0, 0, 0, 0, 0],  # 1  straw + dulces
    [  0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0],  # 2  hielo
    [  0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0],  # 3  borde superior
    [  0, 0, 1, 2, 2, 9, 2, 2, 2, 2, 2, 2, 3, 1, 0, 0],  # 4  cuerpo
    [  0, 0, 1, 2, 9, 2, 2, 8, 8, 2, 2, 2, 3, 1, 0, 0],  # 5  cuerpo + logo
    [  0, 0, 1, 2, 2, 2, 8, 8, 8, 8, 2, 2, 3, 1, 0, 0],  # 6  cuerpo + logo
    [  0, 0, 1, 2, 2, 2, 8, 2, 2, 8, 2, 2, 3, 1, 0, 0],  # 7  cuerpo
    [  0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 1, 0, 0],  # 8  cuerpo
    [  0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0],  # 9  estrecha
    [  0, 0, 0, 1, 2, 9, 2, 2, 2, 2, 3, 1, 0, 0, 0, 0],  # 10 estrecha
    [  0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 3, 1, 0, 0, 0, 0],  # 11 estrecha
    [  0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 1, 0, 0, 0, 0, 0],  # 12 más estrecha
    [  0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 1, 0, 0, 0, 0, 0],  # 13
    [  0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0],  # 14 base
    [  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],  # 15
]

STRAW_COLOR  = (220, 40, 40, 255)   # pajita roja para todos
TRANSPARENT  = (0, 0, 0, 0)
WHITE        = (255, 255, 255, 255)
HIGHLIGHT    = (255, 255, 255, 180) # brillo semitransparente

def make_texture(name, p):
    img = Image.new("RGBA", (16, 16), TRANSPARENT)
    px  = img.load()

    palette = {
        0: TRANSPARENT,
        1: p["dark"],
        2: p["main"],
        3: p["shade"],
        4: p["ice"],
        5: STRAW_COLOR,
        6: p["candy1"],
        7: p["candy2"],
        8: WHITE,
        9: tuple(min(255, c + 40) for c in p["main"][:3]) + (255,),  # highlight
    }

    for y, row in enumerate(TEMPLATE):
        for x, code in enumerate(row):
            px[x, y] = palette[code]

    out_path = os.path.join(TEXTURES_DIR, f"{name}.png")
    img.save(out_path, "PNG")
    print(f"  ✓ {name}.png")

print("Generando texturas de granizados...")
for name, palette in VARIANTS.items():
    make_texture(name, palette)

print("\nListo. Texturas guardadas en textures/item/")

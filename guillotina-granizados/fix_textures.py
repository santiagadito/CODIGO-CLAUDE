#!/usr/bin/env python3
"""
Corrige las texturas de Bombón y Tamarindo.
"""
from PIL import Image
import os

ITEM_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/item"

T  = (0, 0, 0, 0)

# ─── BOMBÓN: paleta diagonal — bola roja sin adornos, palo blanco ─────────────
def make_bombon():
    img = Image.new("RGBA", (16, 16), T)
    px  = img.load()

    RD  = (210,  30,  30, 255)   # rojo cuerpo
    RL  = (240,  70,  55, 255)   # rojo claro
    RV  = (150,  15,  15, 255)   # rojo muy oscuro (borde)
    WR  = (255, 150, 130, 255)   # reflejo brillante
    WH  = (248, 248, 248, 255)   # palo blanco
    WG  = (190, 190, 190, 255)   # palo sombra

    # Bola centrada en (10, 4), radio ~3 — parte superior derecha
    ball = {
        (8,1),(9,1),(10,1),(11,1),
        (7,2),(8,2),(9,2),(10,2),(11,2),(12,2),
        (7,3),(8,3),(9,3),(10,3),(11,3),(12,3),
        (7,4),(8,4),(9,4),(10,4),(11,4),(12,4),
        (7,5),(8,5),(9,5),(10,5),(11,5),(12,5),
        (8,6),(9,6),(10,6),(11,6),
        (9,7),(10,7),
    }
    ball_light = {(8,2),(9,2),(8,3)}   # reflejo
    ball_dark  = {(11,5),(12,5),(12,4),(11,6),(10,7)}  # sombra borde

    # Palo diagonal desde (8,7) hasta (1,14), grosor 2px
    stick = []
    for i in range(8):
        x, y = 8 - i, 7 + i
        if 0 <= x <= 15 and 0 <= y <= 15:
            stick.append((x, y))
        x2, y2 = 7 - i, 7 + i
        if 0 <= x2 <= 15 and 0 <= y2 <= 15:
            stick.append((x2, y2))

    for (x, y) in ball:
        if (x, y) in ball_light:
            px[x, y] = WR
        elif (x, y) in ball_dark:
            px[x, y] = RV
        else:
            # gradiente simple: más claro arriba-izquierda
            if y <= 3:
                px[x, y] = RL
            else:
                px[x, y] = RD

    for i, (x, y) in enumerate(stick):
        px[x, y] = WH if i % 2 == 0 else WG

    # Borde de la bola (RV)
    for (bx, by) in [(8,1),(11,1),(7,3),(13,3),(7,5),(13,5),(9,7),(10,7)]:
        if 0 <= bx <= 15 and 0 <= by <= 15:
            px[bx, by] = RV

    img.save(f"{ITEM_DIR}/mezcla_bombon.png")
    print("✓ mezcla_bombon.png")

# ─── TAMARINDO: botella Smirnoff con calavera CENTRADA ────────────────────────
def make_tamarindo():
    img = Image.new("RGBA", (16, 16), T)
    px  = img.load()

    GS  = (215, 228, 235, 210)   # vidrio
    GL  = (238, 248, 252, 230)   # vidrio brillo
    GD  = (155, 172, 182, 190)   # vidrio sombra
    RD  = (200,  32,  28, 255)   # etiqueta roja
    RL  = (228,  65,  48, 255)   # rojo claro
    OR  = (228, 100,  18, 255)   # naranja tamarindo
    SK  = (238, 218, 175, 255)   # calavera crema
    SKD = (160, 130,  85, 255)   # calavera sombra
    BK  = ( 18,  12,   8, 255)   # negro ojos/nariz
    WH  = (245, 245, 245, 255)   # tapa
    SV  = (175, 188, 198, 255)   # cuello plata

    # Botella: cuerpo en cols 4-11, centrado en 16px
    # Tapa (row 0)
    for x in range(6, 10):  px[x, 0] = WH
    # Cuello (rows 1-2)
    for x in range(5, 11):  px[x, 1] = SV
    for x in range(5, 11):  px[x, 2] = SV

    # Hombros (rows 3-4) expandiendo
    for x in range(4, 12):
        px[x, 3] = GL if x in (4, 5) else (GD if x in (10, 11) else GS)
    for x in range(3, 13):
        px[x, 4] = GL if x in (3, 4) else (GD if x in (11, 12) else GS)

    # Etiqueta (rows 5-11), cols 3-12
    for y in range(5, 12):
        px[3,  y] = GD
        px[12, y] = GD
        for x in range(4, 12):
            px[x, y] = RD if y not in (5, 11) else RL

    # ── Calavera CENTRADA en la etiqueta (cols 5-10, rows 6-10) ──
    # Cráneo (fila 6-8, cols 5-10)
    skull = [
        # (x, y, color)
        (6,6,SK),(7,6,SK),(8,6,SK),(9,6,SK),
        (5,7,SK),(6,7,SK),(7,7,SK),(8,7,SK),(9,7,SK),(10,7,SK),
        (5,8,SK),(6,8,SK),(7,8,SK),(8,8,SK),(9,8,SK),(10,8,SK),
        # Ojos (huecos negros)
        (6,7,BK),(7,7,BK),   # ojo izq
        (8,7,BK),(9,7,BK),   # ojo der
        # Nariz
        (7,8,BK),(8,8,BK),
        # Mandíbula (fila 9-10)
        (6,9,SK),(7,9,SK),(8,9,SK),(9,9,SK),
        (6,10,SK),(8,10,SK),                  # dientes
        (7,10,BK),(9,10,BK),                  # huecos dientes
    ]
    for (x, y, c) in skull:
        px[x, y] = c

    # Cuerpo inferior de la botella (rows 12-15)
    for y in range(12, 15):
        for x in range(3, 13):
            px[x, y] = GL if x in (3, 4) else (GD if x in (11, 12) else GS)
    # Base
    for x in range(4, 12): px[x, 15] = SV

    img.save(f"{ITEM_DIR}/tamarindo.png")
    print("✓ tamarindo.png")

# ─── Preview ×12 ─────────────────────────────────────────────────────────────
def preview():
    ZOOM = 12
    items = ["mezcla_bombon", "tamarindo"]
    W = len(items) * (16*ZOOM + 6) + 6
    canvas = Image.new("RGBA", (W, 16*ZOOM + 12), (35, 35, 35, 255))
    for i, name in enumerate(items):
        src = Image.open(f"{ITEM_DIR}/{name}.png").resize((16*ZOOM, 16*ZOOM), Image.NEAREST)
        canvas.paste(src, (6 + i * (16*ZOOM + 6), 6), src)
    canvas.save("/tmp/preview_fix.png")
    print("✓ Preview: /tmp/preview_fix.png")

print("Corrigiendo texturas...")
make_bombon()
make_tamarindo()
preview()

#!/usr/bin/env python3
"""
Texturas de ingredientes inspiradas en los productos reales:
- Quipitos:   bolsa de dulces colorida (rosa/rojo/morado, estrellas)
- Bombón:     Bon Bon Bum paleta (roja, palo blanco, banderita)
- Tamarindo:  botella Smirnoff Tamarindo (transparente, etiqueta roja con calavera)
- Revolcón:   bolsa verde/negra ácida (rayas de advertencia, personaje)
"""
from PIL import Image
import os

ITEM_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/item"
os.makedirs(ITEM_DIR, exist_ok=True)

T = (0,0,0,0)

def img16():
    return Image.new("RGBA", (16,16), T)

# ─── QUIPITOS ─────────────────────────────────────────────────────────────────
# Bolsa de dulces: fondo morado/rosa, estrellas amarillas, nudo arriba
def make_quipitos():
    img = img16(); px = img.load()
    PU  = ( 90,  20, 140, 255)  # morado oscuro (borde bolsa)
    PM  = (160,  40, 200, 255)  # morado medio
    PK  = (220,  60, 180, 255)  # rosa-morado
    PL  = (240, 100, 210, 255)  # rosa claro
    YL  = (255, 220,  30, 255)  # amarillo estrella
    YD  = (200, 160,  10, 255)  # amarillo oscuro
    RD  = (230,  30,  50, 255)  # rojo acento
    WH  = (255, 255, 255, 200)  # brillo
    grid = [
        [T,  T,  T,  T,  T,  PU, PU, PU, PU, T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  PU, PU, PM, PK, PK, PM, PU, PU, T,  T,  T,  T,  T ],
        [T,  T,  PU, PM, PK, YL, PL, PL, YL, PK, PM, PU, T,  T,  T,  T ],
        [T,  PU, PM, PK, PL, YD, YL, YL, YD, PL, PK, PM, PU, T,  T,  T ],
        [T,  PU, PK, PL, WH, PK, PL, PL, PK, RD, PL, PK, PU, T,  T,  T ],
        [T,  PU, PK, YL, PK, PL, PK, PK, PL, PK, YL, PK, PU, T,  T,  T ],
        [T,  PU, PM, PK, PL, PK, RD, RD, PK, PL, PK, PM, PU, T,  T,  T ],
        [T,  PU, PM, PK, YL, PK, PL, PL, PK, YL, PK, PM, PU, T,  T,  T ],
        [T,  PU, PK, PL, PK, PL, PK, PK, PL, PK, PL, PK, PU, T,  T,  T ],
        [T,  PU, PK, PL, WH, PK, YL, YL, PK, RD, PL, PK, PU, T,  T,  T ],
        [T,  PU, PM, PK, PL, PK, PL, PL, PK, PL, PK, PM, PU, T,  T,  T ],
        [T,  PU, PM, PK, PK, PL, PK, PK, PL, PK, PK, PM, PU, T,  T,  T ],
        [T,  T,  PU, PM, PK, PK, PL, PL, PK, PK, PM, PU, T,  T,  T,  T ],
        [T,  T,  T,  PU, PU, PM, PM, PM, PM, PU, PU, T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  PU, PU, PU, PU, T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    for y,row in enumerate(grid):
        for x,c in enumerate(row): px[x,y]=c
    img.save(f"{ITEM_DIR}/mezcla_quipitos.png")
    print("✓ mezcla_quipitos.png")

# ─── BOMBÓN (Bon Bon Bum paleta) ──────────────────────────────────────────────
def make_bombon():
    img = img16(); px = img.load()
    RD  = (210,  30,  30, 255)   # rojo paleta
    RL  = (240,  70,  60, 255)   # rojo claro
    RV  = (160,  15,  15, 255)   # rojo oscuro
    WR  = (240, 130, 100, 255)   # rojo brillante highlight
    WH  = (245, 245, 245, 255)   # palo blanco
    WG  = (200, 200, 200, 255)   # palo sombra
    YL  = (255, 210,  20, 255)   # bandera amarilla
    BL  = ( 20,  80, 200, 255)   # bandera azul
    GN  = ( 20, 160,  60, 255)   # bandera verde
    PK  = (230,  80, 160, 255)   # envoltorio rosa
    PD  = (180,  40, 120, 255)   # envoltorio oscuro
    grid = [
        [T,  T,  T,  BL, BL, YL, GN, T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  BL, BL, YL, YL, GN, GN, T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  PK, PD, PK, PD, PK, PD, T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  RV, RD, RL, WR, RL, RD, RD, RV, T,  T,  T,  T,  T,  T,  T ],
        [T,  RV, RD, WR, RL, WR, RL, RD, RV, T,  T,  T,  T,  T,  T,  T ],
        [T,  RV, RL, WR, RL, WR, RL, RL, RV, T,  T,  T,  T,  T,  T,  T ],
        [T,  RV, RD, RL, WR, RL, RD, RD, RV, T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  RV, RD, RD, RD, RD, RV, T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  RV, RV, RV, RV, T,  T,  WH, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  WH, WG, WH, WH, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  WH, WG, WH, WG, T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  WH, WG, WH, WG, T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  WH, WG, WH, WG, T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  WH, WG, WH, WG, T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  WH, WG, WH, WG, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
        [T,  WG, WH, WG, T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    for y,row in enumerate(grid):
        for x,c in enumerate(row): px[x,y]=c
    img.save(f"{ITEM_DIR}/mezcla_bombon.png")
    print("✓ mezcla_bombon.png")

# ─── TAMARINDO (botella Smirnoff Tamarindo) ───────────────────────────────────
def make_tamarindo():
    img = img16(); px = img.load()
    GS  = (220, 230, 235, 220)   # vidrio transparente
    GL  = (240, 248, 252, 240)   # vidrio brillo
    GD  = (160, 175, 185, 200)   # vidrio sombra
    RD  = (200,  35,  30, 255)   # etiqueta roja
    RL  = (230,  70,  50, 255)   # rojo claro
    OR  = (230, 100,  20, 255)   # naranja tamarindo
    SK  = (240, 220, 180, 255)   # calavera crema
    SKD = (180, 150, 100, 255)   # calavera sombra
    WH  = (245, 245, 245, 255)   # tapa blanca
    SV  = (180, 190, 200, 255)   # cuello plateado
    BK  = ( 20,  15,  10, 255)   # negro
    grid = [
        [T,  T,  T,  T,  T,  WH, WH, WH, WH, T,  T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  SV, SV, SV, SV, SV, SV, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  GL, GS, GS, GS, GL, GD, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, RD, RD, RD, RD, RD, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, RL, SK, SK, SK, RL, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, SK, BK, SK, BK, SK, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, RL, SK, BK, SK, RL, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, OR, OR, OR, OR, OR, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, RL, OR, OR, OR, RL, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  RD, RD, RD, RD, RD, RD, RD, GD, T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  GL, GS, GS, GS, GL, GD, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  GL, GS, GS, GS, GL, GD, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  GL, GS, GS, GS, GL, GD, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  GL, GS, GS, GS, GL, GD, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  SV, SV, SV, SV, SV, SV, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  SV, SV, SV, SV, T,  T,  T,  T,  T,  T,  T ],
    ]
    for y,row in enumerate(grid):
        for x,c in enumerate(row): px[x,y]=c
    img.save(f"{ITEM_DIR}/tamarindo.png")
    print("✓ tamarindo.png")

# ─── REVOLCÓN (bolsa verde/negra ácida) ──────────────────────────────────────
def make_revolcon():
    img = img16(); px = img.load()
    BK  = ( 15,  15,  10, 255)   # negro
    YL  = (220, 200,  10, 255)   # amarillo advertencia
    GN  = ( 30, 155,  40, 255)   # verde oscuro
    GL  = ( 55, 200,  60, 255)   # verde claro
    GH  = (100, 230,  80, 255)   # verde highlight
    WH  = (240, 245, 200, 255)   # blanco-amarillo
    SK  = (240, 210, 100, 255)   # skin personaje
    HR  = (220, 180,  10, 255)   # pelo amarillo
    EY  = ( 20,  20,  20, 255)   # ojos
    RD  = (200,  30,  20, 255)   # boca/detalle
    AC  = (180, 240,  50, 255)   # ácido verde-amarillo
    grid = [
        [T,  BK, YL, BK, YL, BK, YL, BK, YL, BK, YL, BK, T,  T,  T,  T ],  # rayas advertencia
        [T,  BK, BK, BK, BK, BK, BK, BK, BK, BK, BK, BK, T,  T,  T,  T ],
        [T,  BK, GN, GL, GN, HR, HR, HR, GN, GL, GN, BK, T,  T,  T,  T ],
        [T,  BK, GL, GH, HR, HR, HR, HR, HR, GH, GL, BK, T,  T,  T,  T ],
        [T,  BK, GN, GH, GN, SK, SK, SK, GN, GH, GN, BK, T,  T,  T,  T ],
        [T,  BK, GL, GN, SK, EY, SK, EY, SK, GN, GL, BK, T,  T,  T,  T ],
        [T,  BK, GN, GL, SK, SK, RD, SK, SK, GL, GN, BK, T,  T,  T,  T ],
        [T,  BK, GL, GN, GN, AC, AC, AC, GN, GN, GL, BK, T,  T,  T,  T ],  # boca ácida
        [T,  BK, GN, GL, GN, GL, WH, GL, GN, GL, GN, BK, T,  T,  T,  T ],
        [T,  BK, GL, GN, GL, GN, GL, GN, GL, GN, GL, BK, T,  T,  T,  T ],
        [T,  BK, GN, GL, GN, GL, GN, GL, GN, GL, GN, BK, T,  T,  T,  T ],
        [T,  BK, GL, GN, WH, GN, GL, GN, WH, GN, GL, BK, T,  T,  T,  T ],
        [T,  BK, GN, GL, GN, GL, GN, GL, GN, GL, GN, BK, T,  T,  T,  T ],
        [T,  T,  BK, GN, GL, GN, GL, GN, GL, GN, BK, T,  T,  T,  T,  T ],
        [T,  T,  T,  BK, BK, BK, BK, BK, BK, BK, T,  T,  T,  T,  T,  T ],
        [T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T,  T ],
    ]
    for y,row in enumerate(grid):
        for x,c in enumerate(row): px[x,y]=c
    img.save(f"{ITEM_DIR}/mezcla_revolcon.png")
    print("✓ mezcla_revolcon.png")

print("Generando texturas de ingredientes...")
make_quipitos()
make_bombon()
make_tamarindo()
make_revolcon()

# Preview conjunto ×10
ZOOM = 10
items = ["mezcla_quipitos", "mezcla_bombon", "tamarindo", "mezcla_revolcon"]
W = len(items) * (16*ZOOM + 6) + 6
preview = Image.new("RGBA", (W, 16*ZOOM + 12), (35,35,35,255))
for i, name in enumerate(items):
    src = Image.open(f"{ITEM_DIR}/{name}.png").resize((16*ZOOM, 16*ZOOM), Image.NEAREST)
    preview.paste(src, (6 + i*(16*ZOOM+6), 6), src)
preview.save("/tmp/preview_ingredientes.png")
print("\n✓ Preview en /tmp/preview_ingredientes.png")

#!/usr/bin/env python3
"""
Textura 16x16 del bloque Guillotina inspirada en la Ninja Slushi Machine:
- Cuerpo oscuro charcoal con panel ventilado lateral
- Contenedor cilíndrico transparente arriba con granizado rojo
- Boquilla plateada al frente
- Botones y logo
"""
from PIL import Image
import os

BLOCK_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/block"
os.makedirs(BLOCK_DIR, exist_ok=True)

# ─── Paleta ──────────────────────────────────────────────────────────────────
T   = (  0,   0,   0,   0)   # transparente
BK  = ( 28,  30,  32, 255)   # negro-charcoal (cuerpo)
BK2 = ( 38,  42,  46, 255)   # charcoal medio
BK3 = ( 55,  60,  65, 255)   # charcoal claro
GRY = ( 75,  82,  90, 255)   # gris azulado (marco)
GR2 = (100, 110, 120, 255)   # gris medio
SLV = (170, 180, 190, 255)   # plateado
SL2 = (210, 218, 225, 255)   # plateado claro
SL3 = (240, 245, 250, 255)   # casi blanco
TRS = (160, 200, 220, 130)   # contenedor transparente (azul-gris)
TRL = (190, 220, 235, 160)   # contenedor highlight
GRZ = (200,  50,  40, 255)   # granizado rojo adentro
GRZ2= (160,  30,  25, 255)   # granizado oscuro
BLU = ( 80, 100, 130, 255)   # azul grisáceo del marco lateral
BLU2= (100, 125, 160, 255)   # azul claro
RED = (200,  40,  40, 255)   # botón encendido
WH  = (235, 240, 245, 255)   # blanco texto
VNT = ( 35,  38,  42, 255)   # ventilación oscura
VN2 = ( 48,  52,  57, 255)   # ventilación media

# ─── Diseño 16x16 (vista frontal) ────────────────────────────────────────────
# Fila 0-1: parte superior del contenedor cilíndrico
# Fila 2-6: contenedor con granizado
# Fila 7:   transición cuerpo
# Fila 8-14: cuerpo principal con panel, botones, boquilla
# Fila 15:  base

grid = [
# col 0    1    2    3    4    5    6    7    8    9   10   11   12   13   14   15
  [BK2, BK2, GRY, TRS, TRS, TRS, TRS, TRS, TRS, TRS, TRS, SLV, SL2, SLV, BK2, BK2],  # 0
  [BK2, GRY, TRL, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, TRL, SL2, SL3, SL2, GRY, BK2],  # 1
  [BK2, TRS, GRZ, GRZ2,GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, TRS, SLV, SL2, BK2],  # 2
  [BK2, TRS, GRZ, GRZ2,GRZ, GRZ, GRZ, GRZ, GRZ2,GRZ, GRZ, GRZ, TRS, SLV, SL2, BK2],  # 3
  [BK2, TRS, GRZ, GRZ, GRZ, GRZ2,GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, TRS, SLV, SL2, BK2],  # 4
  [BK2, GRY, TRL, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, GRZ, TRL, GRY, SLV, GRY, BK2],  # 5
  [BK2, BK2, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, BK3, SLV, BK2, BK2],  # 6
  [BLU, BLU, BLU, BLU, BLU, BLU, BLU, BLU, BLU, BLU, BLU, BLU, SLV, SL2, SLV, BLU],  # 7 franja azul
  [BK , BK , VNT, VN2, VNT, VN2, BK3, BK3, BK3, BK3, RED, BK3, SLV, GRZ, SLV, BK ],  # 8 ventilación + botón + boquilla
  [BK , BK , VN2, VNT, VN2, VNT, BK3, WH , WH , WH , BK3, BK3, GR2, GRZ, GR2, BK ],  # 9 ventilación + "N"
  [BK , BK , VNT, VN2, VNT, VN2, BK3, WH , BK3, WH , BK3, BK3, GR2, GRZ, GR2, BK ],  # 10 "I"
  [BK , BK , VN2, VNT, VN2, VNT, BK3, WH , WH , BK3, BK3, BK3, GR2, GRZ, GR2, BK ],  # 11 ventilación
  [BK , BK , VNT, VN2, BK3, BK3, BK3, BK3, BK3, BK3, BK3, BK3, SLV, SL2, SLV, BK ],  # 12 panel botones
  [BK , BK , BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK2, BK ],  # 13
  [BK , GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, BK ],  # 14 base superior
  [GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY, GRY],  # 15 base
]

img = Image.new("RGBA", (16, 16), T)
px  = img.load()
for y, row in enumerate(grid):
    for x, color in enumerate(row):
        px[x, y] = color

img.save(os.path.join(BLOCK_DIR, "guillotina.png"), "PNG")
print("✓ guillotina.png (máquina Ninja style)")

# ─── Preview ×12 ─────────────────────────────────────────────────────────────
Z = 20
preview = Image.new("RGBA", (16*Z + 8, 16*Z + 8), (50, 50, 50, 255))
big = img.resize((16*Z, 16*Z), Image.NEAREST)
preview.paste(big, (4, 4), big)
preview.save("/tmp/preview_machine.png")
print("✓ preview guardado en /tmp/preview_machine.png")

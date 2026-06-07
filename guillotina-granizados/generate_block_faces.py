#!/usr/bin/env python3
"""
Genera las 6 caras del bloque Guillotina + textura de GUI (176x166).
Inspirado en la Ninja Slushi Machine.
"""
from PIL import Image, ImageDraw
import os

BLOCK_DIR = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/block"
GUI_DIR   = "/home/user/CODIGO-CLAUDE/guillotina-granizados/src/main/resources/assets/guillotina_granizados/textures/gui/container"
os.makedirs(BLOCK_DIR, exist_ok=True)
os.makedirs(GUI_DIR, exist_ok=True)

T   = (  0,   0,   0,   0)
BK  = ( 28,  30,  32, 255)   # negro-charcoal
BK2 = ( 40,  44,  48, 255)   # charcoal medio
BK3 = ( 55,  60,  65, 255)   # charcoal claro
GRY = ( 75,  82,  90, 255)   # gris azulado
GR2 = (100, 110, 120, 255)   # gris medio
SLV = (170, 180, 190, 255)   # plateado
SL2 = (210, 218, 225, 255)   # plateado claro
BLU = ( 80, 100, 130, 255)   # azul grisáceo
BL2 = (100, 125, 160, 255)   # azul claro
RED = (200,  40,  40, 255)
GRN = ( 50, 180,  60, 255)
TRS = (160, 200, 220, 130)
GRZ = (200,  50,  40, 255)
VNT = ( 35,  38,  42, 255)
VN2 = ( 48,  52,  57, 255)
WH  = (235, 240, 245, 255)

def save16(img, name):
    img.save(os.path.join(BLOCK_DIR, f"{name}.png"))
    print(f"  ✓ {name}.png")

# ── FRENTE: panel de control con boquilla y display ───────────────────────────
def make_front():
    g = [
        [BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU],  # 0
        [BLU,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BLU],  # 1
        [BLU,BK ,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,BK ,BLU],  # 2
        [BLU,BK ,GRY,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,GRY,BK ,BLU],  # 3
        [BLU,BK ,GRY,BK2,WH ,WH ,WH ,WH ,WH ,WH ,WH ,WH ,BK2,GRY,BK ,BLU],  # 4 display
        [BLU,BK ,GRY,BK2,WH ,RED,BK2,WH ,WH ,BK2,GRN,WH ,BK2,GRY,BK ,BLU],  # 5 botones
        [BLU,BK ,GRY,BK2,WH ,WH ,WH ,WH ,WH ,WH ,WH ,WH ,BK2,GRY,BK ,BLU],  # 6
        [BLU,BK ,GRY,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,GRY,BK ,BLU],  # 7
        [BLU,BK ,GRY,GRY,GRY,SLV,SLV,SLV,SLV,SLV,GRY,GRY,GRY,GRY,BK ,BLU],  # 8 boquilla
        [BLU,BK ,BK ,BK ,GRY,SL2,GRY,SLV,SLV,GRY,SL2,GRY,BK ,BK ,BK ,BLU],  # 9
        [BLU,BK ,BK ,BK ,GRY,SLV,GRY,GRZ,GRZ,GRY,SLV,GRY,BK ,BK ,BK ,BLU],  # 10 granizado
        [BLU,BK ,BK ,BK ,GRY,SLV,GRY,GRZ,GRZ,GRY,SLV,GRY,BK ,BK ,BK ,BLU],  # 11
        [BLU,BK ,BK ,BK ,BK ,GRY,SLV,SL2,SL2,SLV,GRY,BK ,BK ,BK ,BK ,BLU],  # 12
        [BLU,BK ,BK ,BK ,BK ,BK ,GRY,SLV,SLV,GRY,BK ,BK ,BK ,BK ,BK ,BLU],  # 13
        [GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY],  # 14 base
        [SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV],  # 15
    ]
    img = Image.new("RGBA",(16,16)); px=img.load()
    for y,row in enumerate(g):
        for x,c in enumerate(row): px[x,y]=c
    save16(img,"guillotina_front")

# ── TRASERA: rejilla de ventilación ──────────────────────────────────────────
def make_back():
    g = [
        [GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY],
        [GRY,BK ,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,BK ,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,BK ,GRY],
        [GRY,BK ,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,BK ,GRY],
        [GRY,BK ,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,BK ,GRY],
        [GRY,BK ,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,BK ,GRY],
        [GRY,BK ,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,BK ,GRY],
        [GRY,BK ,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,GRY],
        [GRY,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,BK ,GRY],
        [GRY,BK ,BK ,VNT,VN2,VNT,VN2,VNT,VN2,VNT,VN2,VNT,BK ,BK ,BK ,GRY],
        [GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY],
        [SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV],
    ]
    img = Image.new("RGBA",(16,16)); px=img.load()
    for y,row in enumerate(g):
        for x,c in enumerate(row): px[x,y]=c
    save16(img,"guillotina_back")

# ── LADO: paneles laterales con rejillas verticales ───────────────────────────
def make_side():
    g = [
        [BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU,BLU],
        [BLU,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,VN2,BK2,BK ,BLU],
        [BLU,BK ,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,VNT,BK2,BK ,BLU],
        [BLU,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BLU],
        [GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY],
        [SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV],
    ]
    img = Image.new("RGBA",(16,16)); px=img.load()
    for y,row in enumerate(g):
        for x,c in enumerate(row): px[x,y]=c
    save16(img,"guillotina_side")

# ── SUPERIOR: contenedor cilíndrico desde arriba ──────────────────────────────
def make_top():
    img = Image.new("RGBA",(16,16),BK); px=img.load()
    # Marco exterior
    for x in range(16): px[x,0]=GRY; px[x,15]=GRY
    for y in range(16): px[0,y]=GRY;  px[15,y]=GRY
    # Contenedor circular (elipse)
    for y in range(1,15):
        for x in range(1,15): px[x,y]=BK2
    # Círculo del contenedor (radio ~5, centro 7,7)
    import math
    cx,cy,r=7.5,7.5,4.5
    for y in range(16):
        for x in range(16):
            d=math.sqrt((x-cx)**2+(y-cy)**2)
            if abs(d-r)<1.1: px[x,y]=SLV
            elif d<r-1.1: px[x,y]=(160,200,220,180)  # contenedor transparente
    # Granizado dentro
    cx2,cy2,r2=7.5,7.5,3.0
    for y in range(16):
        for x in range(16):
            d=math.sqrt((x-cx2)**2+(y-cy2)**2)
            if d<r2: px[x,y]=GRZ
    # Boquilla (arriba)
    for y in range(2,5): px[7,y]=SLV; px[8,y]=SLV
    save16(img,"guillotina_top")

# ── INFERIOR: base con patas ──────────────────────────────────────────────────
def make_bottom():
    g = [
        [SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV],
        [SLV,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,SLV],
        [SLV,GRY,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,SLV,SLV,BK ,BK ,SLV,SLV,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,SLV,GRY,BK ,BK ,GRY,SLV,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,SLV,SLV,BK ,BK ,SLV,SLV,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,SLV,GRY,BK ,BK ,GRY,SLV,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK2,BK ,GRY,SLV],
        [SLV,GRY,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,BK ,GRY,SLV],
        [SLV,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,GRY,SLV],
        [SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV,SLV],
    ]
    img = Image.new("RGBA",(16,16)); px=img.load()
    for y,row in enumerate(g):
        for x,c in enumerate(row): px[x,y]=c
    save16(img,"guillotina_bottom")

# ════════════════════════════════════════════════════════════════════════════════
# GUI 176x166 — interfaz de La Guillotina
# Slots: ingrediente(56,35) hielo(56,17) salida(116,35)
# Flecha de progreso: (79,34) → (103,34), 24x16px
# ════════════════════════════════════════════════════════════════════════════════
def make_gui():
    W, H = 256, 256   # atlas completo (Minecraft espera 256x256)
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # ── Fondo principal 176x166 ───────────────────────────────────────────────
    BG    = ( 45,  50,  58, 255)   # fondo oscuro azul-gris
    PNL   = ( 35,  38,  44, 255)   # paneles más oscuros
    BRD   = ( 80,  90, 105, 255)   # borde iluminado
    SLOT  = ( 25,  27,  32, 255)   # interior de slot (oscuro)
    SLOT2 = ( 55,  60,  70, 255)   # borde slot claro
    ARW   = (120, 180, 100, 255)   # flecha progreso (verde)
    ARW2  = ( 60,  90,  50, 255)   # flecha vacía
    RIVT  = (100, 110, 125, 255)   # remaches decorativos
    GRZ_C = (200,  50,  40, 220)   # tono granizado en deco

    # Fondo base
    draw.rectangle([0, 0, 175, 165], fill=BG)

    # Bordes iluminados exteriores
    draw.rectangle([0, 0, 175, 165], outline=BRD)
    draw.rectangle([1, 1, 174, 164], outline=PNL)

    # Panel superior (título)
    draw.rectangle([2, 2, 173, 14], fill=PNL)
    draw.rectangle([2, 2, 173, 14], outline=BRD)

    # Panel inferior (inventario)
    draw.rectangle([7, 82, 168, 161], fill=PNL)
    draw.rectangle([7, 82, 168, 161], outline=BRD)

    # Área de la máquina (centro superior)
    draw.rectangle([7, 16, 168, 78], fill=( 38,  42,  50, 255))
    draw.rectangle([7, 16, 168, 78], outline=BRD)

    # Decoración: línea de tubería horizontal
    draw.rectangle([8, 45, 167, 47], fill=( 55,  60,  70, 255))

    # Remaches decorativos en esquinas del panel máquina
    for rx, ry in [(10,19),(165,19),(10,75),(165,75)]:
        draw.ellipse([rx-3, ry-3, rx+3, ry+3], fill=RIVT, outline=BRD)

    # ── Slot HIELO (arriba-izq) 56,17 ────────────────────────────────────────
    def draw_slot(cx, cy, accent=None):
        x, y = cx-1, cy-1
        draw.rectangle([x, y, x+18, y+18], fill=SLOT2)
        draw.rectangle([x+1, y+1, x+17, y+17], fill=SLOT)
        if accent:
            draw.rectangle([x+1, y+1, x+5, y+5], fill=accent)

    draw_slot(56, 17, accent=(160,200,230,180))  # hielo — tono azul
    draw_slot(56, 35, accent=(180,100,40,200))   # ingrediente — tono naranja
    draw_slot(116, 35, accent=GRZ_C)             # salida — tono granizado

    # ── Flechas indicadoras ───────────────────────────────────────────────────
    # Flecha de hielo→mezcla (vertical, 56,30 → 56,33)
    for dy in range(3):
        draw.point((63, 28+dy), fill=BRD)
        draw.point((64, 28+dy), fill=BRD)

    # Flecha de progreso (horizontal 79,34 → 103,34) — vacía (UV 176,14)
    # En UV: primero la flecha vacía en x=0,y=166 (fuera del panel principal)
    # La flecha llena se superpone desde x=176,y=14 (fuera del área 176x166)

    # Flecha vacía (background de la barra)
    draw.rectangle([79, 34, 103, 50], fill=ARW2, outline=BRD)
    # Punto central indicador
    draw.polygon([(79,42),(90,34),(90,50)], fill=( 70, 100,  60, 255))

    # ── Líneas de separación del inventario ──────────────────────────────────
    draw.rectangle([7, 118, 168, 120], fill=( 55, 62, 72, 255))

    # ── Slots del inventario del jugador (3×9 + hotbar) ──────────────────────
    for row in range(3):
        for col in range(9):
            sx = 8 + col*18
            sy = 84 + row*18
            draw.rectangle([sx, sy, sx+17, sy+17], fill=( 30,33,40,255))
            draw.rectangle([sx, sy, sx+16, sy+16], outline=( 60,65,75,255))

    for col in range(9):
        sx = 8 + col*18
        sy = 142
        draw.rectangle([sx, sy, sx+17, sy+17], fill=( 30,33,40,255))
        draw.rectangle([sx, sy, sx+16, sy+16], outline=( 75,80,95,255))

    # ── Sprite de la flecha llena (UV 176,14 tamaño 24x16) ───────────────────
    # Esto va fuera de los 176px para que Minecraft lo use como overlay
    for px_x in range(24):
        for px_y in range(16):
            ratio = px_x / 23
            r = int(ARW2[0] + (ARW[0]-ARW2[0])*ratio)
            g_ = int(ARW2[1] + (ARW[1]-ARW2[1])*ratio)
            b = int(ARW2[2] + (ARW[2]-ARW2[2])*ratio)
            img.putpixel((176+px_x, 14+px_y), (r, g_, b, 255))
    # Punta de flecha
    for i in range(8):
        for j in range(i):
            img.putpixel((176+16+i, 14+8-j, ), ARW)
            img.putpixel((176+16+i, 14+8+j, ), ARW)

    img.save(os.path.join(GUI_DIR, "guillotina.png"))
    print("  ✓ guillotina.png (GUI 256x256)")

# ── Ejecutar ──────────────────────────────────────────────────────────────────
print("Generando texturas del bloque...")
make_front()
make_back()
make_side()
make_top()
make_bottom()
print("Generando GUI...")
make_gui()

# Preview de las 5 caras
ZOOM = 8
faces = ["guillotina_front","guillotina_back","guillotina_side","guillotina_top","guillotina_bottom"]
PW = len(faces)*(16*ZOOM+4)+4
preview = Image.new("RGBA",(PW,16*ZOOM+8),(40,40,40,255))
for i,name in enumerate(faces):
    src=Image.open(f"{BLOCK_DIR}/{name}.png").resize((16*ZOOM,16*ZOOM),Image.NEAREST)
    preview.paste(src,(4+i*(16*ZOOM+4),4),src)
preview.save("/tmp/preview_faces.png")

# Preview GUI
gui=Image.open(f"{GUI_DIR}/guillotina.png").crop((0,0,176,166))
gui=gui.resize((176*2,166*2),Image.NEAREST)
gui.save("/tmp/preview_gui.png")
print("✓ Previews en /tmp/")

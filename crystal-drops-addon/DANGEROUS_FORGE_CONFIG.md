# Dangerous Forge — Configuración recomendada para dificultad 75/100

Estos valores van en: `config/dangerous-common.toml`

## Objetivo
Con Crystal Leveling + Crystal Drops + Dangerous Forge, la dificultad combinada
debe ser ~75/100. Dangerous aporta ~45%, Crystal Drops aporta ~30%.

## Valores recomendados

```toml
# Multiplicador base de vida (1.0 = sin cambio, 1.5 = +50% HP)
baseHealthMultiplier = 1.4

# Multiplicador máximo que puede alcanzar con el tiempo
maxHealthMultiplier = 2.2

# Cuánto aumenta por incremento (cada N días)
healthMultiplierIncrement = 0.1

# Días entre cada incremento de dificultad
daysPerIncrement = 3

# Probabilidad de que un mob reciba un arma al spawnear (0.0 - 1.0)
weaponChance = 0.55

# Velocidad del Creeper (1.0 = normal, 1.3 = +30%)
# No subir más — Crystal Drops Frenzy agrega Strength II encima
creeperSpeedMultiplier = 1.25

# Velocidad de la Araña
spiderSpeedMultiplier = 1.2

# Probabilidad de gear por dificultad de Minecraft (Easy/Normal/Hard)
easyGearChance   = 0.10
normalGearChance = 0.30
hardGearChance   = 0.55

# Anuncios en chat cuando sube la dificultad
enableChatAnnouncements = true

# Escalado de HP habilitado
enableHealthScaling = true
```

## Por qué estos valores

| Parámetro | Valor elegido | Razón |
|---|---|---|
| baseHealthMultiplier = 1.4 | +40% HP base | Suficiente para sentir la diferencia sin ser injusto |
| maxHealthMultiplier = 2.2 | +120% HP máximo | Muy duro al final del mundo, pero alcanzable con buen gear |
| creeperSpeedMultiplier = 1.25 | +25% velocidad | Crystal Drops Frenzy ya da Strength II, no agregar más velocidad |
| weaponChance = 0.55 | 55% chance de arma | Mobs armados frecuentes pero no siempre |
| hardGearChance = 0.55 | 55% en Hard | Peligrosos pero no todos blindados |

## Conflictos resueltos automáticamente por Crystal Drops

- **Creepers/Arañas en Frenzy**: Crystal Drops detecta Dangerous y omite Speed II
  para estos mobs (solo aplica Strength II + Resistance I)
- **Armadura en mobs pasivos**: Crystal Drops limpia gear de vacas/ovejas/etc al spawnear
- **Drops duplicados**: Crystal Drops usa `LivingDeathEvent`, Dangerous usa
  `LivingDropsEvent` — no se solapan

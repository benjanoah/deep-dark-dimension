# Deep Dark Dimension Mod 🎮

Een Minecraft mod voor versie 1.20.1 (Fabric) die een enge Warden dimensie toevoegt!

## Wat zit erin (tot nu toe)

✅ **Sculk Shard** - Een nieuw item om de portal te activeren  
✅ **Deep Dark Dimensie** - Een donkere dimensie vol met Wardens  
✅ **Warden Wastes Biome** - Het biome in de dimensie waar Wardens spawnen  
🚧 **Portal Systeem** - Komt nog! (Deepslate frame + Sculk Shard activatie)  
🚧 **Seeker Mob** - Komt nog!

## Hoe te gebruiken

### 1. Open het project in IntelliJ IDEA
- Open IntelliJ IDEA
- Klik op "Open" en selecteer de `deep-dark-dimension` folder
- Wacht tot Gradle klaar is met laden

### 2. Gradle setup (eerste keer)
Open de Terminal in IntelliJ en run:
```bash
./gradlew genSources
```

Dit kan een paar minuten duren!

### 3. Run de mod
In IntelliJ, bovenaan zie je een groene play knop ▶️. Klik daarop!

Of via terminal:
```bash
./gradlew runClient
```

### 4. Test de mod in Minecraft
- Ga naar Creative mode
- Open je inventory (E)
- Ga naar de "Ingredients" tab
- Je zou de **Sculk Shard** moeten zien! 🔮

### 5. Naar de dimensie gaan
Dit werkt nog niet automatisch! We moeten de portal nog maken.

**Tijdelijke manier:**
- Open de chat (T)
- Type: `/execute in deepdark:deep_dark run tp @s ~ ~ ~`

## Wat komt er nog?

1. **Portal systeem** - Deepslate frame bouwen en activeren met Sculk Shard
2. **Seeker mob** - Jouw custom mob!
3. **Sculk Shard crafting recipe** - Hoe krijg je hem?
4. **Betere world generation** - Meer sculk, structures, etc.
5. **Textures** - Een echte texture voor de Sculk Shard

## Problemen?

Als er errors zijn, laat het me weten! We fixen het samen 🔧

---

**Gemaakt door Benjamin met hulp van Bert 🎮**

# Changelog

## 1.2 (Battle Music Update)
- Added battle music events!
  - Allows music to play when you battle specific pokemon/players/trainers
  - Configurable via resource packs!
  - Defaults to adding music to Arceus (music from Legends Arceus)
  - See wiki for how this works
- Made tridents spawn in water related loot chests (and drop from pokemon like Feraligator)

## 1.1 (Config Update)
- Added a config to the mod
  - Made the range of the sparkle effect configurable
  - Made the volume of the sparkle effect configurable
  - Made healers dropping themselves configurable
- Added two new gamerules: `doPokemonSpawning` & `doTrainerSpawning`
  - The last update had a serious issue due to me not understanding how the `allow-vanilla-mobs` config entry in pixelmon works. So this fixes the issues that came up because of that
- Added an option to configure the required level for Hypertraining. Defaults to 50, Pixelmon defaults to 100

## 1.0 (Initial Release)
- Makes healers drop themselves
- Makes shiny pokemon sparkle with the particles and sound effect from Legends Arceus
- Pokemon no longer spawn when doMobSpawning gamerule is false
- Boss pokemon are colored in the battle UI
- Boss pokemon colors are tweaked
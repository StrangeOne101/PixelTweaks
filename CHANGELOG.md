# Changelog

## 1.7.2
- Fixed crash when viewing items in a language without localization for that item (#7)
- Fixed infuser recipes not being registered when you log into a server with JEI installed
- Made unknown moves no longer spam the console

## 1.7.1
- Fixed crashing when you level up a pokemon with a rare candy
- Added 3 config options for Zygarde cell spawning
  - Including the spawn times, lucky spawn chance, and stormy weather core boost rate
- Zygarde spawning now ensures the chunk is loaded before trying anything
- Fixed some debug left in on startup

## 1.7
- Added JEI recipes for Infusers
- Added anti-crop trampling setting to prevent pokemon from trampling crops. Configurable.
- Made Zygarde cell spawning obey the pixelmon config setting (in spawning.yml)
- Made the length of lore wrapping on items configurable

## 1.6.1
- Fixed viewing an item in a pokeloot chest recipe from filling every single slot
- Attempted to fix Zygarde cell placement bug (#6)

## 1.6
- Added pokemon drops and poke loot chests recipes to JEI
  - Shows all the possible drops for pokemon in JEI
  - Shows all the possible drops for poke loot chests in JEI
- Added English (GB) and Japanese lang files

## 1.5
- Added JEI Integration
  - Makes Pokeballs, Pokeball lids, TMs and TRs show in JEI.
  - Can now be looked up for recipes easily
- Made all pixelmon item information (the lore you see when you hold SNEAK) now split into multiple lines after a certain length (60 characters)
- Added `pokemon_overlay` event for resource packs
  - Allows resource packs to define textures to overlay on pokemon under certain conditions
  - Allows you to make emissive textures without the need for a datapack
  - As an example, Arceus' eyes are now emissive (glow in the dark)

## 1.4.1
- Zygarde cores spawn 3x as often in thunder now
- Disabled Zygarde cell feature spawning as it was causing timeout issues
- Fixed the wild property in pokemon conditions not working (still not working properly in MP)
- Added invert property to pokemon conditions
- Added is_raid condition type

## 1.4
- Added Zygarde cell spawning
  - Cells now spawn again
  - Cells will spawn in chunks closer to you more often than not when they decide to spawn
  - Cells that spawn in newly generated chunks will be invisible. But this can't be fixed
  - The materials that cells spawn on is configurable via tags
- Fixed intro battle music not fading out when the battle ends early
- Added new battle music conditions
  - Added Dimension condition
  - Added Chance condition
  - Added Calendar condition
  - Updated Biome condition to support BetterSpawnerConfig biome categories
  - Updated Trainer condition to support way more things
  - Fixed Player condition breaking in Multiplayer
  - Fixed biome condition not working in Multiplayer
- Nerfed tridents in loot
- Fixed lang files missing gamerule lang nodes

## 1.3.2
- Updated to Pixelmon Reforged 9.1.4

## 1.3.1
- Made Mimikyu and Aegislash support custom forms
- Fixed Aegislash not reverting to Shield form

## 1.3 (Berry Update)
- Added apricorns (and berries) dropping naturally
  - Configurable (defaults to enabled).
  - They take 20x longer to drop naturally than they do to ripen
  - They despawn after 1 minute
- Made fox pokemon immune to berry bushes. They can also be healed by feeding them berries
- Added new Time Condition for events (for battle music resource packs)
- Added new Weather condition for events (for battle music resource packs)
- Added new "invert" property to some conditions (for battle music resource packs)

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
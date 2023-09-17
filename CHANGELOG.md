# Changelog

## 1.9.5
- Added PokemonConfig type for FTB Quests
  - Allows the specification property to now get on-the-fly parsing. Will evaluate the specs and make sure it is valid before accepting the value
- Added BossTier property to Defeat Trainer Task
- Fixed custom battle music not stopping when the battle ends sometimes
- Fixed Z-moves crashing the game when using battle sound effects
- Fixed Critical Hit property in Battle Move tasks not working
- Fixed pokedex percentage tasks having incorrect percentage
- Fixed pokedex task's single mon filter triggering for any mon
- Fixed abilities that cause weather not triggering weather battle actions

## 1.9.4
- Added LIGHT_SCREEN and REFLECT battle action types
- Added "Source Pokemon Spec" to defeat tasks. Allows you to check what pokemon defeated the other
- Added SINGLE_MON pokedex filter for Pokedex Amount
- Fixed issues with the Pixelmon Economy Bridge
- Fixed serverside NoSuchMethodFoundException for Pokemon rewards
- Fixed Defeat and Evolution tasks still not updating their tasks (the previous cache issue still affected only these two types)
- Fixed Pokedex Percentage tasks showing more than the max percentage
- Fixed Pokedex pokemon type filter causing issues
- Changed the Breed Pokemon task to trigger when you collect the pokemon


## 1.9.3
- Renamed Whiteout task to Party Wipeout
- Fixed NPE on catching via raids
  - Pokeballs can no longer be tested in spec, but there is nothing I can do about this
- Fixed Ultra Beast lang missing
- Fixed boss specs not working in defeat tasks
- Fixed External Moves task not checking the move
- Fixed Evolve Pokemon task using interact filter not working when you don't specify an item
- Fixed Pokedex Percentage task having a weird percentage
- Fixed Pokedex tasks not working for generation filters
- Attempted to fix Pokedollar tasks not updating on the client
- Fixed missing Invert config option in the lang file

## 1.9.2
- Added shinyChance to pokemon rewards
- Fixed catch task bugging out
- Fixed running from trainers triggering defeat pokemon task
- Fixed wild config value in defeat pokemon tasks not working
- Fixed creating a task after triggering a task of the same type making the created task be unable to be triggered
- Made pokedex tasks only able to be updated serverside

## 1.9.1-9.1.6
- Compiled for Pixelmon 9.1.6
- Same as 1.9.1, but no Zygarde cell changes

## 1.9.1
- Updated for Pixelmon 9.1.7
- Fixed Pokedex tasks not working at all
- Disabled Zygarde cell spawning
  - Pixelmon has changed how this works recently, and until I can prove that cells don't spawn at all in chunks now, this is disabled
- Fixed player battle tasks crashing when no UUID is provided
- Fixed trainer battle tasks not abiding by the amount variable
- Fixed missing icons and lang node for Evolution task

# 1.9 (FTB Quests Integration)
- Added FTB Quests Integration for Pixelmon
- Includes the following FTB Tasks
  - Catching Pokemon
  - Defeating Pokemon
  - Trading Pokemon
  - Evolving Pokemon
  - Leveling Pokemon
  - Breeding Pokemon
  - Hatching Pokemon
  - Pokedex Progress (Amount)
  - Pokedex Progress (Percentage)
  - Defeating Trainers in Battle
  - Defeating Players in Battle
  - Using a Move in battle
  - Whiting Out
  - Pokedollars
  - Photograph Pokemon
  - Using External Move
- Includes the following FTB Reward types
  - PokeLoot Chest Drops
  - PokeDollars
  - Pokemon
- Fixed EFFECTIVE_HIT battle action not playing correctly

## 1.8.3
- Fixed crash that occurred when you had SophisticatedBackpacks but not CuriosAPI

## 1.8.2
- Fixed Sophisticated Backpack only getting items from the first backpack
- Fixed Sophisticated Backpacks in the CuriousAPI backpack slot not being able to be accessed in battle

## 1.8.1
- Updated PixelTweaks to Pixelmon 9.1.6

# 1.8 (Backpacks & Battle Sounds)
- Added backpack mod integrations for the following mods
  - SimplyBackpacks
  - UsefulBackpacks
  - SophisticatedBackpacks
  - TravelersBackpack
- Added battle action sound event
  - Allows resource packs to play sound or music events when certain things happen in battle
  - Including super effective hits, status effects, crits, catching, defeat, and lots more
- All music events now support playing a sound instead of music

## 1.7.3
- Fixed crash when opening the game in zh_tw locale (#7)
- Optimized Zygarde spawning a little, and fixed minor bugs with it

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

# 1.7 (Infusers & Tweaks)
- Added JEI recipes for Infusers
- Added anti-crop trampling setting to prevent pokemon from trampling crops. Configurable.
- Made Zygarde cell spawning obey the pixelmon config setting (in spawning.yml)
- Made the length of lore wrapping on items configurable

## 1.6.1
- Fixed viewing an item in a pokeloot chest recipe from filling every single slot
- Attempted to fix Zygarde cell placement bug (#6)

# 1.6 (Pokemon Drops & Pokeloot)
- Added pokemon drops and poke loot chests recipes to JEI
  - Shows all the possible drops for pokemon in JEI
  - Shows all the possible drops for poke loot chests in JEI
- Added English (GB) and Japanese lang files

# 1.5 (JEI Integration & Pokemon Overlays)
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

# 1.4 (Zygarde Cells)
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

# 1.3 (Berry Update)
- Added apricorns (and berries) dropping naturally
  - Configurable (defaults to enabled).
  - They take 20x longer to drop naturally than they do to ripen
  - They despawn after 1 minute
- Made fox pokemon immune to berry bushes. They can also be healed by feeding them berries
- Added new Time Condition for events (for battle music resource packs)
- Added new Weather condition for events (for battle music resource packs)
- Added new "invert" property to some conditions (for battle music resource packs)

# 1.2 (Battle Music Update)
- Added battle music events!
  - Allows music to play when you battle specific pokemon/players/trainers
  - Configurable via resource packs!
  - Defaults to adding music to Arceus (music from Legends Arceus)
  - See wiki for how this works
- Made tridents spawn in water related loot chests (and drop from pokemon like Feraligator)

# 1.1 (Config Update)
- Added a config to the mod
  - Made the range of the sparkle effect configurable
  - Made the volume of the sparkle effect configurable
  - Made healers dropping themselves configurable
- Added two new gamerules: `doPokemonSpawning` & `doTrainerSpawning`
  - The last update had a serious issue due to me not understanding how the `allow-vanilla-mobs` config entry in pixelmon works. So this fixes the issues that came up because of that
- Added an option to configure the required level for Hypertraining. Defaults to 50, Pixelmon defaults to 100

# 1.0 (Initial Release)
- Makes healers drop themselves
- Makes shiny pokemon sparkle with the particles and sound effect from Legends Arceus
- Pokemon no longer spawn when doMobSpawning gamerule is false
- Boss pokemon are colored in the battle UI
- Boss pokemon colors are tweaked
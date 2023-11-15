package com.strangeone101.pixeltweaks.integration.ftbquests;

import com.strangeone101.pixeltweaks.integration.ftbquests.rewards.PokeDollarReward;
import com.strangeone101.pixeltweaks.integration.ftbquests.rewards.PokelootReward;
import com.strangeone101.pixeltweaks.integration.ftbquests.rewards.PokemonReward;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import net.minecraft.util.ResourceLocation;

public class PokemonRewardTypes {

    public static RewardType POKELOOT;
    public static RewardType POKEDOLLARS;
    public static RewardType POKEMON;

    public static void register() {
        POKELOOT = RewardTypes.register(new ResourceLocation("pixelmon", "pokeloot"), PokelootReward::new,
                () -> Icon.getIcon("pixeltweaks:textures/gui/pokeloot/1.png"));

        POKEDOLLARS = RewardTypes.register(new ResourceLocation("pixelmon", "pokedollars"), PokeDollarReward::new,
                () -> Icon.getIcon("pixelmon:textures/gui/pokedollar.png"));

        POKEMON = RewardTypes.register(new ResourceLocation("pixelmon", "pokemon"), PokemonReward::new,
                () -> Icon.getIcon("pixelmon:items/pokeballs/poke_ball"));

    }
}

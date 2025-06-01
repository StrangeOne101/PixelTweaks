package com.strangeone101.pixeltweaks.integration.jei;


import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PokeLootPool {

    private int tier = 1;
    private List<ItemStack> items;

    public PokeLootPool(int tier, List<ItemStack> items) {
        this.tier = tier;
        this.items = items;
    }

    public int getTier() {
        return tier;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getTierIcon() {
        ItemStack itemStack = new ItemStack(ItemRegistration.POKE_BALL);
        RegistryValue<PokeBall> ballID = PokeBallRegistry.POKE_BALL;
        if (tier == 2) ballID = PokeBallRegistry.ULTRA_BALL;
        else if (tier == 3) ballID = PokeBallRegistry.MASTER_BALL;
        else if (tier == 4) ballID = PokeBallRegistry.BEAST_BALL;
        itemStack.set(PixelmonDataComponents.POKE_BALL, PokeBallRegistry.POKE_BALL);
        return itemStack;
    }

    public String getTierPokeball() {
        String ballID = "poke_ball";
        if (tier == 2) ballID = "ultra_ball";
        else if (tier == 3) ballID = "master_ball";
        else if (tier == 4) ballID = "beast_ball";
        return ballID;
    }
}

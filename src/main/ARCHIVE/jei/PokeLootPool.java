package com.strangeone101.pixeltweaks.integration.jei;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
        ItemStack itemStack = new ItemStack(PixelmonItems.poke_ball);
        CompoundTag compoundNBT = new CompoundTag();
        String ballID = "poke_ball";
        if (tier == 2) ballID = "ultra_ball";
        else if (tier == 3) ballID = "master_ball";
        else if (tier == 4) ballID = "beast_ball";
        compoundNBT.putString("PokeBallID", ballID);
        itemStack.setTag(compoundNBT);
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

package com.strangeone101.pixeltweaks.mixin.ability;

import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.ability.abilities.StanceChange;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.strangeone101.pixeltweaks.PixelTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(StanceChange.class)
public class MixinAegislashAbility extends AbstractAbility {

    /**
     * @author StrangeOne101
     * @reason Fixed Aegislash not changing forms
     */
    @Overwrite(remap = false)
    public void preProcessAttackUser(PixelmonWrapper pokemon, PixelmonWrapper target, Attack a) {
        /*String shield = "shield";
        String blade = "blade";
        String newShield = "shield";
        String newBlade = "blade";

        //If the pokemon has a custom form
        if (pokemon.getForm().getName().contains("_")) {
            String[] split = pokemon.getForm().getName().split("_");
            newShield = split[0] + "_shield";
            newBlade = split[0] + "_blade";
        }

        PixelTweaks.LOGGER.debug("Form name: " + pokemon.getForm().getName());
        PixelTweaks.LOGGER.debug("Attack: " + a.savedAttack.getAttackName());

        if (Attack.dealsDamage(a) && pokemon.getForm().getName().endsWith(shield)) {
            pokemon.setForm(newBlade);
            pokemon.bc.modifyStats(pokemon);
            pokemon.bc.sendToAll("pixelmon.abilities.stancechange.blade", pokemon.getNickname());
        } else if (!Attack.dealsDamage(a) && pokemon.getForm().getName().endsWith(blade)) {
            pokemon.setForm(newShield);
            pokemon.bc.modifyStats(pokemon);
            pokemon.bc.sendToAll("pixelmon.abilities.stancechange.shield", pokemon.getNickname());
        }*/

    }


    /**
     * @author StrangeOne101
     * @reason Fixed Aegislash not changing forms
     */
    @Overwrite(remap = false)
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        if (oldPokemon.getForm().getName().endsWith("blade")) {
            if (oldPokemon.getForm().getName().contains("_")) {
                String[] split = oldPokemon.getForm().getName().split("_");
                oldPokemon.setForm(split[0] + "_shield");
                return;
            }
            oldPokemon.setForm("shield");
        }

    }

    @Override
    public void startMove(PixelmonWrapper user) {
        String shield = "shield";
        String blade = "blade";
        String newShield = "shield";
        String newBlade = "blade";

        //If the pokemon has a custom form
        if (user.getForm().getName().contains("_")) {
            String[] split = user.getForm().getName().split("_");
            newShield = split[0] + "_shield";
            newBlade = split[0] + "_blade";
        }

        PixelTweaks.LOGGER.debug("Form name: " + user.getForm().getName());
        //PixelTweaks.LOGGER.debug("Attack: " + user.attack.savedAttack.getAttackName());

        if (Attack.dealsDamage(user.attack) && user.getForm().getName().endsWith(shield)) {
            user.setForm(newBlade);
            user.bc.modifyStats(user);
            user.bc.sendToAll("pixelmon.abilities.stancechange.blade", user.getNickname());
        } else if (!Attack.dealsDamage(user.attack) && user.getForm().getName().endsWith(blade)) {
            user.setForm(newShield);
            user.bc.modifyStats(user);
            user.bc.sendToAll("pixelmon.abilities.stancechange.shield", user.getNickname());
        }

        return;
    }
}

package com.strangeone101.pixeltweaks.mixin.ability;

import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.ability.abilities.Disguise;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Disguise.class)
public abstract class MixinMimikyuAbility extends AbstractAbility {


    /**
     * @author StrangeOne101
     * @reason Patched custom forms not busting correctly
     */
    @Overwrite(remap = false)
    public int modifyDamageIncludeFixed(int damage, PixelmonWrapper user, PixelmonWrapper target, Attack a) {
        if (!target.getForm().getName().endsWith("busted") && !a.hasNoEffect(user, target) &&
                !a.isAttack(AttackRegistry.MOONGEIST_BEAM, AttackRegistry.SUNSTEEL_STRIKE,
                        AttackRegistry.PHOTON_GEYSER, AttackRegistry.SEARING_SUNRAZE_SMASH,
                        AttackRegistry.MENACING_MOONRAZE_MAELSTROM, AttackRegistry.LIGHT_THAT_BURNS_THE_SKY)) {
            String newForm = "busted";
            if (!target.getForm().getName().equals("") && !target.getForm().getName().equals("disguised") && target.getSpecies().hasForm(target.getForm().getName() + "_busted")) {
                newForm = target.getForm().getName() + "_busted";
            }

            target.bc.sendToAll("pixelmon.abilities.disguise");
            target.setForm(newForm);
            target.bc.sendToAll("pixelmon.abilities.disguisebusted", target.getPokemonName());
            return 0;
        } else {
            return damage;
        }
    }
}

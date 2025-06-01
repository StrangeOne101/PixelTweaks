package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.pixelmon.api.pokemon.Element;

import java.lang.reflect.Field;

public class BetterTypeColors {

    public BetterTypeColors() {
        try {
            Field colorField = Element.class.getDeclaredField("color");
            colorField.setAccessible(true);
            colorField.set(Element.WATER, 0x2481ef);
            colorField.set(Element.FIGHTING, 0xFF8100);
            colorField.set(Element.PSYCHIC, 0xef3f7a);
            colorField.set(Element.DRAGON, 0x4F60E2);
            colorField.set(Element.DARK, 0x503f3f);
            colorField.set(Element.FAIRY, 0xef70ef);
            colorField.set(Element.STEEL, 0x60a2b9);
            colorField.set(Element.ROCK, 0xb0aa82);
            colorField.set(Element.GHOST, 0x703f70);
            colorField.set(Element.GRASS, 0x3da224);
            colorField.set(Element.BUG, 0x92a212);
            colorField.set(Element.ELECTRIC, 0xfac100);
            colorField.set(Element.ICE, 0x3dd9ff);
            colorField.set(Element.NORMAL, 0xa0a1a0);
            colorField.set(Element.FLYING, 0x82baef);
            colorField.set(Element.POISON, 0x923fcc);
            colorField.set(Element.GROUND, 0x91501b);
            colorField.set(Element.FIRE, 0xe72324);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

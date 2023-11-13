package com.strangeone101.pixeltweaks.tweaks;

import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.ItemStackHelper;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeChat {

    private final Pattern pokemon = Pattern.compile("\\[pokemon]", Pattern.CASE_INSENSITIVE);
    private final Pattern party = Pattern.compile("\\[party]", Pattern.CASE_INSENSITIVE);
    private final Pattern slot = Pattern.compile("\\[(?:slot|pokemon)[(1-6)]]", Pattern.CASE_INSENSITIVE);

    public PokeChat() {
        if (TweaksConfig.enablePokemonChat.get()) {
            MinecraftForge.EVENT_BUS.addListener(this::onChat);
        }
    }


    public void onChat(ServerChatEvent event) {
        long time = System.currentTimeMillis();
        if (event.getMessage().toLowerCase().matches(".*\\[(pokemon[1-6]?|party|slot[1-6])\\].*")) {
            Pokemon pokemon = StorageProxy.getParty(event.getPlayer()).getSelectedPokemon();
            String usedMatcher = this.pokemon.toString();
            Matcher matcher = this.pokemon.matcher(event.getMessage());
            Matcher matcher2 = this.party.matcher(event.getMessage());
            Matcher matcher3 = this.slot.matcher(event.getMessage());

            if (matcher.find()) {
                pokemon = StorageProxy.getParty(event.getPlayer()).getSelectedPokemon();
            } else if (matcher3.find()) {
                int slot = Integer.parseInt(matcher3.group().replaceAll("[^1-6]", ""));
                pokemon = StorageProxy.getParty(event.getPlayer()).getAll()[slot - 1];
                usedMatcher = "\\[(pokemon|slot)" + slot + "]";
            }

            if (pokemon != null) {
                ITextComponent component = pokemon.getFormattedDisplayName(); //The name of the pokemon. Nickname or localized.
                ((IFormattableTextComponent)component).mergeStyle(TextFormatting.RESET).mergeStyle(TextFormatting.GREEN); //Make green

                Style style = component.getStyle();
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(getItem(pokemon)));
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "YES DADDY");
                ((IFormattableTextComponent) component).mergeStyle(style.setHoverEvent(hoverEvent).setClickEvent(clickEvent).setItalic(false));

                component = TextComponentUtils.wrapWithSquareBrackets(component); //Wrap in square brackets

                event.setComponent(replaceInComponent(event.getComponent(), usedMatcher, component));
            }
        }
        PixelTweaks.LOGGER.info("TOok " + (System.currentTimeMillis() - time) + "ms");
    }

    public static ITextComponent replaceInComponent(ITextComponent baseComponent, String matcher, ITextComponent replacement) {
        if (baseComponent instanceof StringTextComponent) {
            String baseString = ((StringTextComponent) baseComponent).getText();

            if (!baseString.replaceFirst(matcher, "").equals(baseString)) { //If it matches
                String[] split = baseString.split(matcher, 2);
                String pre = split[0];

                StringTextComponent newComponent = new StringTextComponent(pre);
                newComponent.setStyle(baseComponent.getStyle());
                newComponent.appendSibling(replacement);

                if (split.length != 1) {
                    String post = split[1];
                    StringTextComponent postComponent = new StringTextComponent(post);
                    postComponent.setStyle(baseComponent.getStyle());
                    newComponent.appendSibling(postComponent);
                }

                for (ITextComponent sibling : baseComponent.getSiblings()) {
                    newComponent.appendSibling(sibling);
                }
                return newComponent;

            }
        } else if (baseComponent instanceof TranslationTextComponent) {
            Object[] args = ((TranslationTextComponent) baseComponent).getFormatArgs();

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ITextComponent) {
                    args[i] = replaceInComponent((ITextComponent) args[i], matcher, replacement);
                }
            }
        }
        List<ITextComponent> siblings = baseComponent.getSiblings();
        List<ITextComponent> newSiblings = new ArrayList<>(siblings.size());

        for (ITextComponent sib : siblings) {
            newSiblings.add(replaceInComponent(sib, matcher, replacement));
        }
        baseComponent.getSiblings().clear();
        baseComponent.getSiblings().addAll(newSiblings);


        return baseComponent;
    }

    public ItemStack getItem(Pokemon pokemon) {
        ItemStack stack = SpriteItemHelper.getPhoto(pokemon); //Get the photo itemstack
        CompoundNBT tag = stack.getTag();
        CompoundNBT display = new CompoundNBT();

        IFormattableTextComponent name = (IFormattableTextComponent) pokemon.getFormattedDisplayName();
        name.mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_GREEN);
        name = name.setStyle(name.getStyle().setItalic(false));

        display.putString("Name", ITextComponent.Serializer.toJson(name)); //Set the name to the pokemon's name
        List<String> lore = new ArrayList<>();

        if (pokemon.isEgg()) {
            tag.put("display", display);
            stack.setTag(tag);
            return stack;
        } else if (pokemon.getSpecies().is(PixelmonSpecies.MISSINGNO)) {
            return getMissingNo();
        }


        if (!pokemon.getForm().getName().equals("") && !pokemon.getForm().getName().equals("none")) {
            TranslationTextComponent form = new TranslationTextComponent("gui.screenpokechecker.form", new TranslationTextComponent(pokemon.getForm().getTranslationKey()));
            form.setStyle(form.getStyle().setItalic(false));
            form.mergeStyle(TextFormatting.GRAY);
            lore.add(ITextComponent.Serializer.toJson(form));
        }

        if (!pokemon.getPalette().getName().equals("") && !pokemon.getPalette().getName().equals("none")) {
            TranslationTextComponent palette = new TranslationTextComponent("gui.screenpokechecker.palette", new TranslationTextComponent(pokemon.getPalette().getTranslationKey()));
            if (pokemon.isShiny()) {
                palette.setStyle(palette.getStyle().setColor(Color.fromInt(PixelTweaks.SHINY_COLOR)).setItalic(false));
            } else {
                palette.mergeStyle(TextFormatting.GRAY);
            }
            lore.add(ITextComponent.Serializer.toJson(palette));
        }
        lore.add("");

        TranslationTextComponent lvl = new TranslationTextComponent("pixelmon.command.pokemoninfo.level");
        lvl.setStyle(lvl.getStyle().setItalic(false));
        lvl.mergeStyle(TextFormatting.GREEN);
        lvl.appendString(": ");
        lvl.appendSibling(new StringTextComponent(String.valueOf(pokemon.getPokemonLevel())).mergeStyle(TextFormatting.YELLOW));
        lore.add(ITextComponent.Serializer.toJson(lvl));

        if (pokemon.getGender() != Gender.NONE) {
            TranslationTextComponent gender = new TranslationTextComponent("pixelmon.command.pokemoninfo.gender");
            gender.setStyle(gender.getStyle().setItalic(false));
            gender.mergeStyle(TextFormatting.GREEN);
            gender.appendString(": ");
            Style style = Style.EMPTY.setColor(Color.fromInt(pokemon.getGender() == Gender.FEMALE ? 0xff6464 : 0x6464ff)).setItalic(false);
            gender.appendSibling(new TranslationTextComponent(pokemon.getGender().getTranslationKey()).mergeStyle(style));
            lore.add(ITextComponent.Serializer.toJson(gender));
        }

        TranslationTextComponent ability = new TranslationTextComponent("pixelmon.command.pokemoninfo.ability");
        ability.setStyle(ability.getStyle().setItalic(false));
        ability.mergeStyle(TextFormatting.GREEN);
        ability.appendString(": ");
        ability.appendSibling(pokemon.getAbility().getTranslatedName().mergeStyle(TextFormatting.YELLOW));

        if (pokemon.getForm().getAbilities().isHiddenAbility(pokemon.getAbility())) {
            StringTextComponent hidden = new StringTextComponent(" (");
            hidden.setStyle(hidden.getStyle().setItalic(false));
            hidden.mergeStyle(TextFormatting.GREEN);
            hidden.appendSibling(new TranslationTextComponent("type.ha").mergeStyle(TextFormatting.LIGHT_PURPLE));
            hidden.appendString(")");
            ability.appendSibling(hidden);
        }

        lore.add(ITextComponent.Serializer.toJson(ability));

        TranslationTextComponent pokeball = new TranslationTextComponent("gui.pokemoneditor.pokeball");
        pokeball.setStyle(pokeball.getStyle().setItalic(false));
        pokeball.mergeStyle(TextFormatting.GREEN);
        pokeball.appendString(": ");
        pokeball.appendSibling(new TranslationTextComponent(pokemon.getBall().getTranslationKey()).mergeStyle(TextFormatting.YELLOW));
        lore.add(ITextComponent.Serializer.toJson(pokeball));

        TranslationTextComponent growth = new TranslationTextComponent("pixelmon.command.pokemoninfo.growth");
        growth.setStyle(growth.getStyle().setItalic(false));
        growth.mergeStyle(TextFormatting.GREEN);
        growth.appendString(": ");
        growth.appendSibling(new TranslationTextComponent(pokemon.getGrowth().getTranslationKey()).mergeStyle(TextFormatting.YELLOW));
        lore.add(ITextComponent.Serializer.toJson(growth));

        TranslationTextComponent nature = new TranslationTextComponent("pixelmon.command.pokemoninfo.nature");
        nature.setStyle(nature.getStyle().setItalic(false));
        nature.mergeStyle(TextFormatting.GREEN);
        nature.appendString(": ");
        nature.appendSibling(new TranslationTextComponent(pokemon.getNature().getTranslationKey()).mergeStyle(TextFormatting.YELLOW));
        lore.add(ITextComponent.Serializer.toJson(nature));

        TranslationTextComponent moves = new TranslationTextComponent("pixelmon.command.pokemoninfo.moves");
        moves.setStyle(moves.getStyle().setItalic(false));
        moves.mergeStyle(TextFormatting.GREEN);
        moves.appendString(": ");

        if (pokemon.getMoveset().isEmpty()) {
            StringTextComponent none = new StringTextComponent(" (");
            none.setStyle(none.getStyle().setItalic(false));
            none.mergeStyle(TextFormatting.GREEN);
            none.appendSibling(new TranslationTextComponent("pixelmon.command.pokemoninfo.none").mergeStyle(TextFormatting.GRAY));
            none.appendString(")");
            moves.appendSibling(none);
        } else {
            for (int i = 0; i < pokemon.getMoveset().size(); i++) {
                if (i != 0) {
                    StringTextComponent spacer = new StringTextComponent(" | ");
                    spacer.setStyle(spacer.getStyle().setItalic(false));
                    spacer.mergeStyle(TextFormatting.GRAY);
                    moves.appendSibling(spacer);
                }

                ImmutableAttack attack = pokemon.getMoveset().get(i).getActualMove();
                ITextComponent attackComponent = attack.getTranslatedName().mergeStyle(Style.EMPTY.setColor(Color.fromInt(attack.getAttackType().getColor())).setItalic(false));

                moves.appendSibling(attackComponent);
            }
        }
        lore.add(ITextComponent.Serializer.toJson(moves));

        ListNBT loreToList = new ListNBT();
        for (String l : lore ) loreToList.add(StringNBT.valueOf(l));

        display.put("Lore", NBTDynamicOps.INSTANCE.createList(loreToList.stream()));

        tag.put("display", display);
        stack.setTag(tag);
        return stack;
    }

    public static ItemStack getMissingNo() {
        Pokemon pokemon = PokemonSpecificationProxy.create("missingno").create();
        ItemStack stack = SpriteItemHelper.getPhoto(pokemon);
        IFormattableTextComponent name = pokemon.getTranslatedName();
        name.mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_GREEN);
        name = name.setStyle(name.getStyle().setItalic(false));
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT display = new CompoundNBT();

        display.putString("Name", ITextComponent.Serializer.toJson(name)); //Set the name to the pokemon's name
        List<String> lore = new ArrayList<>();
        int lines = ThreadLocalRandom.current().nextInt(5) + 5;

        for (int i = 0; i < lines; i++) {
            int length = ThreadLocalRandom.current().nextInt(4) + 2 + 4 * (i % 3);
            char[] chars = {'\uFFFD', '\uFFFE', '\uFFFF', '\uFFFD', '\uFFFF', '\uFFFE', '\uFFFD'};
            String s = "";
            for (int j = 0; j < length; j++) {
                s += chars[(i * 7 + length + j * 3) % 7];
            }
            StringTextComponent stc = new StringTextComponent(s);
            lore.add(ITextComponent.Serializer.toJson(stc.setStyle(stc.getStyle().setItalic(false).setColor(Color.fromTextFormatting(TextFormatting.WHITE)))));
        }

        ListNBT loreToList = new ListNBT();
        for (String l : lore ) loreToList.add(StringNBT.valueOf(l));

        display.put("Lore", NBTDynamicOps.INSTANCE.createList(loreToList.stream()));

        tag.put("display", display);
        stack.setTag(tag);
        return stack;
    }
}

package com.strangeone101.pixeltweaks.tweaks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import com.strangeone101.pixeltweaks.arclight.ArclightPokeChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeChat {

    public final Pattern pokemon = Pattern.compile("\\[pokemon]", Pattern.CASE_INSENSITIVE);
    public final Pattern party = Pattern.compile("\\[party]", Pattern.CASE_INSENSITIVE);
    public final Pattern slot = Pattern.compile("\\[(?:slot|pokemon)[(1-6)]]", Pattern.CASE_INSENSITIVE);

    public PokeChat() {
        if (TweaksConfig.enablePokemonChat.get()) {
            if (PixelTweaks.IS_ARCLIGHT) {
                ArclightPokeChat.registerListeners();
            } else {
                MinecraftForge.EVENT_BUS.addListener(this::onChat);
            }

            if (FMLEnvironment.dist == Dist.CLIENT) {
                MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
            }
        }
    }


    public void onChat(ServerChatEvent event) {
        long time = System.currentTimeMillis();
        if (event.getMessage().toLowerCase().matches(".*\\[(pokemon[1-6]?|party|slot[1-6])\\].*")) {
            for (int i = 0; i < 6; i++) { //Stops an infinite loop, if it somehow occurs due to broken find and replace
                Pokemon pokemon = null;
                String usedMatcher = this.pokemon.toString();
                ITextComponent c = event.getComponent();
                String chat = c.getString();
                Matcher matcher = this.pokemon.matcher(chat);
                Matcher matcher2 = this.party.matcher(chat);
                Matcher matcher3 = this.slot.matcher(chat);

                if (matcher.find()) {
                    pokemon = StorageProxy.getParty(event.getPlayer()).getSelectedPokemon();
                } else if (matcher3.find()) {
                    int slot = Integer.parseInt(matcher3.group().replaceAll("[^1-6]", ""));
                    pokemon = StorageProxy.getParty(event.getPlayer()).getAll()[slot - 1];
                    usedMatcher = "\\[(pokemon|slot)" + slot + "]";
                }

                if (pokemon != null) {
                    ITextComponent component = pokemon.getFormattedDisplayName().deepCopy(); //The name of the pokemon. Nickname or localized.
                    Style style = component.getStyle().setBold(false).setItalic(false).setFormatting(TextFormatting.GREEN);
                    ((IFormattableTextComponent) component).mergeStyle(TextFormatting.RESET);

                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(getItem(pokemon)));
                    ((IFormattableTextComponent) component).mergeStyle(style.setHoverEvent(hoverEvent));

                    component = TextComponentUtils.wrapWithSquareBrackets(component); //Wrap in square brackets

                    ITextComponent replaced = replaceInComponent(c, usedMatcher, component);
                    event.setComponent(replaced);
                    //PixelTweaks.LOGGER.info("TOok " + (System.currentTimeMillis() - time) + "ms");
                } else {
                    return;
                }
            }
            PixelTweaks.LOGGER.error("PokeChat exceeded 6 loops with message of: " + event.getComponent().getString());
        }

    }

    public static ITextComponent replaceInComponent(ITextComponent baseComponent, String matcher, ITextComponent replacement) {
        if (baseComponent instanceof StringTextComponent) {
            String baseString = ((StringTextComponent) baseComponent).getText();

            if (!baseString.replaceFirst(matcher, "").equals(baseString)) { //If it matches
                String[] split = baseString.split(matcher, 2);
                String pre = split[0];

                IFormattableTextComponent newComponent = new StringTextComponent(pre);
                newComponent.setStyle(baseComponent.getStyle());
                newComponent.appendSibling(replacement);

                if (pre.equals("")) {
                    newComponent = (IFormattableTextComponent) replacement;
                }

                if (split.length != 1 && !split[1].equals("")) {
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

            List<ITextComponent> siblings = baseComponent.getSiblings();
            baseComponent = new TranslationTextComponent(((TranslationTextComponent) baseComponent).getKey(), args);
            for (ITextComponent sib : siblings) ((TranslationTextComponent) baseComponent).appendSibling(sib);

            //((TranslationTextComponent)baseComponent).ensureInitialized();
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

    public static ItemStack getItem(Pokemon pokemon) {
        ItemStack stack = SpriteItemHelper.getPhoto(pokemon); //Get the photo itemstack
        CompoundNBT tag = stack.getTag();
        CompoundNBT display = new CompoundNBT();

        IFormattableTextComponent name = (IFormattableTextComponent) pokemon.getFormattedDisplayName().deepCopy();
        name.mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_GREEN);
        name = name.setStyle(name.getStyle().setItalic(false));
        if (pokemon.getFormattedNickname() != null && !pokemon.getFormattedNickname().getString().equals("") && !pokemon.isEgg()) {
            name.appendString(" (");
            name.appendSibling(pokemon.getSpecies().getNameTranslation());
            name.appendString(")");
        }

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
                palette.mergeStyle(TextFormatting.GRAY).modifyStyle(style -> style.setItalic(false));
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
        tag.putBoolean("PokeChat", true);
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

    @OnlyIn(Dist.CLIENT)
    public void onItemTooltip(RenderTooltipEvent.PostBackground event) {
        if (event.getStack().getItem() == PixelmonItems.pixelmon_sprite.getItem()) {
            if (event.getStack().hasTag() && event.getStack().getTag().getBoolean("PokeChat")) {
                renderItem(event.getStack(), event.getX() + event.getWidth() - 48, event.getY());
                //Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(event.getStack(), event.getX() + event.getWidth() - 19, event.getY() + 3);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderItem(ItemStack stack, int x, int y) {
        IBakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);

        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F + 400F);
        RenderSystem.translatef(24.0F, 24.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(48.0F, 48.0F, 48.0F);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();
        if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }
}

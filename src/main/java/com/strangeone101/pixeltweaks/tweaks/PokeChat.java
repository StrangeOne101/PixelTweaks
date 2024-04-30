package com.strangeone101.pixeltweaks.tweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.ItemStackHelper;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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

    private final Pattern pokemon = Pattern.compile("\\[pokemon]", Pattern.CASE_INSENSITIVE);
    private final Pattern party = Pattern.compile("\\[party]", Pattern.CASE_INSENSITIVE);
    private final Pattern slot = Pattern.compile("\\[(?:slot|pokemon)[(1-6)]]", Pattern.CASE_INSENSITIVE);

    public PokeChat() {
        if (TweaksConfig.enablePokemonChat.get()) {
            MinecraftForge.EVENT_BUS.addListener(this::onChat);
            if (FMLEnvironment.dist == Dist.CLIENT) {
                MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
            }
        }
    }


    public void onChat(ServerChatEvent event) {
        long time = System.currentTimeMillis();
        try {
            if (event.getRawText().toLowerCase().matches(".*\\[(pokemon[1-6]?|party|slot[1-6])\\].*")) {
                for (int i = 0; i < 6; i++) { //Stops an infinite loop, if it somehow occurs due to broken find and replace
                    Pokemon pokemon = null;
                    String usedMatcher = this.pokemon.toString();
                    Component c = event.getMessage();
                    String chat = c.getString();
                    Matcher matcher = this.pokemon.matcher(chat);
                    Matcher matcher2 = this.party.matcher(chat);
                    Matcher matcher3 = this.slot.matcher(chat);

                    if (matcher.find()) {
                        pokemon = StorageProxy.getParty(event.getPlayer()).get().getSelectedPokemon();
                    } else if (matcher3.find()) {
                        int slot = Integer.parseInt(matcher3.group().replaceAll("[^1-6]", ""));
                        pokemon = StorageProxy.getParty(event.getPlayer()).get().getAll()[slot - 1];
                        usedMatcher = "\\[(pokemon|slot)" + slot + "]";
                    }

                    if (pokemon != null) {
                        Component component = pokemon.getFormattedDisplayName().copy(); //The name of the pokemon. Nickname or localized.
                        Style style = component.getStyle().withBold(false).withItalic(false).withColor(ChatFormatting.GREEN);
                        ((MutableComponent)component).withStyle(ChatFormatting.RESET);

                        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(getItem(pokemon)));
                        ((MutableComponent) component).withStyle(style.withHoverEvent(hoverEvent));

                        component = ComponentUtils.wrapInSquareBrackets(component); //Wrap in square brackets

                        Component replaced = replaceInComponent(c, usedMatcher, component);
                        event.setMessage(replaced);
                        //PixelTweaks.LOGGER.info("TOok " + (System.currentTimeMillis() - time) + "ms");
                    } else {
                        return;
                    }
                }
                PixelTweaks.LOGGER.error("PokeChat exceeded 6 loops with message of: " + event.getMessage().getString());
            }
        }catch (Exception e) {}


    }

    public static Component replaceInComponent(Component baseComponent, String matcher, Component replacement) {
        if (baseComponent.getContents() instanceof LiteralContents) {
            String baseString = ((LiteralContents) baseComponent.getContents()).text();

            if (!baseString.replaceFirst(matcher, "").equals(baseString)) { //If it matches
                String[] split = baseString.split(matcher, 2);
                String pre = split[0];

                MutableComponent newComponent = Component.literal(pre);
                newComponent.setStyle(baseComponent.getStyle());
                newComponent.append(replacement);

                if (pre.isEmpty()) {
                    newComponent = (MutableComponent) replacement;
                }

                if (split.length != 1 && !split[1].isEmpty()) {
                    String post = split[1];
                    MutableComponent postComponent = Component.literal(post);
                    postComponent.setStyle(baseComponent.getStyle());
                    newComponent.append(postComponent);
                }

                for (Component sibling : baseComponent.getSiblings()) {
                    newComponent.append(sibling);
                }
                return newComponent;

            }
        } else if (baseComponent.getContents() instanceof TranslatableContents) {
            TranslatableContents baseCompTraslatable = (TranslatableContents) baseComponent.getContents();
            Object[] args = baseCompTraslatable.getArgs();

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Component) {
                    args[i] = replaceInComponent((Component) args[i], matcher, replacement);
                }
            }

            List<Component> siblings = baseComponent.getSiblings();
            baseComponent = Component.translatable(baseCompTraslatable.getKey(), args);
            for (Component sib : siblings) ((MutableComponent)baseComponent).append(sib);

            //((MutableComponent)baseComponent).ensureInitialized();
        }

        List<Component> siblings = baseComponent.getSiblings();
        List<Component> newSiblings = new ArrayList<>(siblings.size());

        for (Component sib : siblings) {
            newSiblings.add(replaceInComponent(sib, matcher, replacement));
        }
        baseComponent.getSiblings().clear();
        baseComponent.getSiblings().addAll(newSiblings);


        return baseComponent;
    }

    public ItemStack getItem(Pokemon pokemon) {
        ItemStack stack = SpriteItemHelper.getPhoto(pokemon); //Get the photo itemstack
        CompoundTag tag = stack.getTag();
        CompoundTag display = new CompoundTag();

        MutableComponent name = (MutableComponent) pokemon.getFormattedDisplayName().copy();
        name.withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN);
        name = name.setStyle(name.getStyle().withItalic(false));
        if (pokemon.getFormattedNickname() != null && !pokemon.getFormattedNickname().getString().equals("") && !pokemon.isEgg()) {
            name.append(" (");
            name.append(pokemon.getSpecies().getNameTranslation());
            name.append(")");
        }

        display.putString("Name", Component.Serializer.toJson(name)); //Set the name to the pokemon's name
        List<String> lore = new ArrayList<>();

        if (pokemon.isEgg()) {
            tag.put("display", display);
            stack.setTag(tag);
            return stack;
        } else if (pokemon.getSpecies().is(PixelmonSpecies.MISSINGNO)) {
            return getMissingNo();
        }


        if (!pokemon.getForm().getName().equals("") && !pokemon.getForm().getName().equals("none")) {
            MutableComponent form = Component.translatable("gui.screenpokechecker.form", Component.translatable(pokemon.getForm().getTranslationKey()));
            form.setStyle(form.getStyle().withItalic(false));
            form.withStyle(ChatFormatting.GRAY);
            lore.add(Component.Serializer.toJson(form));
        }

        if (!pokemon.getPalette().getName().equals("") && !pokemon.getPalette().getName().equals("none")) {
            MutableComponent palette = Component.translatable("gui.screenpokechecker.palette", Component.translatable(pokemon.getPalette().getTranslationKey()));
            if (pokemon.isShiny()) {
                palette.setStyle(palette.getStyle().withColor(PixelTweaks.SHINY_COLOR).withItalic(false));
            } else {
                palette.withStyle(ChatFormatting.GRAY).withStyle(style -> style.withItalic(false));
            }
            lore.add(Component.Serializer.toJson(palette));
        }
        lore.add("");

        MutableComponent lvl = Component.translatable("pixelmon.command.pokemoninfo.level");
        lvl.setStyle(lvl.getStyle().withItalic(false));
        lvl.withStyle(ChatFormatting.GREEN);
        lvl.append(": ");
        lvl.append(Component.literal(String.valueOf(pokemon.getPokemonLevel())).withStyle(ChatFormatting.YELLOW));
        lore.add(Component.Serializer.toJson(lvl));

        if (pokemon.getGender() != Gender.NONE) {
            MutableComponent gender = Component.translatable("pixelmon.command.pokemoninfo.gender");
            gender.setStyle(gender.getStyle().withItalic(false));
            gender.withStyle(ChatFormatting.GREEN);
            gender.append(": ");
            Style style = Style.EMPTY.withColor(pokemon.getGender() == Gender.FEMALE ? 0xff6464 : 0x6464ff).withItalic(false);
            gender.append(Component.translatable(pokemon.getGender().getTranslationKey()).withStyle(style));
            lore.add(Component.Serializer.toJson(gender));
        }

        MutableComponent ability = Component.translatable("pixelmon.command.pokemoninfo.ability");
        ability.setStyle(ability.getStyle().withItalic(false));
        ability.withStyle(ChatFormatting.GREEN);
        ability.append(": ");
        ability.append(pokemon.getAbility().getTranslatedName().withStyle(ChatFormatting.YELLOW));

        if (pokemon.getForm().getAbilities().isHiddenAbility(pokemon.getAbility())) {
            MutableComponent hidden = Component.literal(" (");
            hidden.setStyle(hidden.getStyle().withItalic(false));
            hidden.withStyle(ChatFormatting.GREEN);
            hidden.append(Component.translatable("type.ha").withStyle(ChatFormatting.LIGHT_PURPLE));
            hidden.append(")");
            ability.append(hidden);
        }

        lore.add(Component.Serializer.toJson(ability));

        MutableComponent pokeball = Component.translatable("gui.pokemoneditor.pokeball");
        pokeball.setStyle(pokeball.getStyle().withItalic(false));
        pokeball.withStyle(ChatFormatting.GREEN);
        pokeball.append(": ");
        pokeball.append(Component.translatable(pokemon.getBall().getTranslationKey()).withStyle(ChatFormatting.YELLOW));
        lore.add(Component.Serializer.toJson(pokeball));

        MutableComponent growth = Component.translatable("pixelmon.command.pokemoninfo.growth");
        growth.setStyle(growth.getStyle().withItalic(false));
        growth.withStyle(ChatFormatting.GREEN);
        growth.append(": ");
        growth.append(Component.translatable(pokemon.getGrowth().getTranslationKey()).withStyle(ChatFormatting.YELLOW));
        lore.add(Component.Serializer.toJson(growth));

        MutableComponent nature = Component.translatable("pixelmon.command.pokemoninfo.nature");
        nature.setStyle(nature.getStyle().withItalic(false));
        nature.withStyle(ChatFormatting.GREEN);
        nature.append(": ");
        nature.append(Component.translatable(pokemon.getNature().getTranslationKey()).withStyle(ChatFormatting.YELLOW));
        lore.add(Component.Serializer.toJson(nature));

        MutableComponent moves = Component.translatable("pixelmon.command.pokemoninfo.moves");
        moves.setStyle(moves.getStyle().withItalic(false));
        moves.withStyle(ChatFormatting.GREEN);
        moves.append(": ");

        if (pokemon.getMoveset().isEmpty()) {
            MutableComponent none = Component.literal(" (");
            none.setStyle(none.getStyle().withItalic(false));
            none.withStyle(ChatFormatting.GREEN);
            none.append(Component.translatable("pixelmon.command.pokemoninfo.none").withStyle(ChatFormatting.GRAY));
            none.append(")");
            moves.append(none);
        } else {
            for (int i = 0; i < pokemon.getMoveset().size(); i++) {
                if (i != 0) {
                    MutableComponent spacer = Component.literal(" | ");
                    spacer.setStyle(spacer.getStyle().withItalic(false));
                    spacer.withStyle(ChatFormatting.GRAY);
                    moves.append(spacer);
                }

                ImmutableAttack attack = pokemon.getMoveset().get(i).getActualMove();
                Component attackComponent = attack.getTranslatedName().withStyle(Style.EMPTY.withColor(attack.getAttackType().getColor()).withItalic(false));

                moves.append(attackComponent);
            }
        }
        lore.add(Component.Serializer.toJson(moves));

        ListTag loreToList = new ListTag();
        for (String l : lore ) loreToList.add(StringTag.valueOf(l));

        display.put("Lore", NbtOps.INSTANCE.createList(loreToList.stream()));

        tag.put("display", display);
        tag.putBoolean("PokeChat", true);
        stack.setTag(tag);
        return stack;
    }

    public static ItemStack getMissingNo() {
        Pokemon pokemon = PokemonSpecificationProxy.create("missingno").get().create();
        ItemStack stack = SpriteItemHelper.getPhoto(pokemon);
        MutableComponent name = pokemon.getTranslatedName();
        name.withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN);
        name = name.setStyle(name.getStyle().withItalic(false));
        CompoundTag tag = new CompoundTag();
        CompoundTag display = new CompoundTag();

        display.putString("Name", Component.Serializer.toJson(name)); //Set the name to the pokemon's name
        List<String> lore = new ArrayList<>();
        int lines = ThreadLocalRandom.current().nextInt(5) + 5;

        for (int i = 0; i < lines; i++) {
            int length = ThreadLocalRandom.current().nextInt(4) + 2 + 4 * (i % 3);
            char[] chars = {'\uFFFD', '\uFFFE', '\uFFFF', '\uFFFD', '\uFFFF', '\uFFFE', '\uFFFD'};
            String s = "";
            for (int j = 0; j < length; j++) {
                s += chars[(i * 7 + length + j * 3) % 7];
            }
            MutableComponent stc = Component.literal(s);
            lore.add(Component.Serializer.toJson(stc.setStyle(stc.getStyle().withItalic(false).withColor(ChatFormatting.WHITE))));
        }

        ListTag loreToList = new ListTag();
        for (String l : lore ) loreToList.add(StringTag.valueOf(l));

        display.put("Lore", NbtOps.INSTANCE.createList(loreToList.stream()));

        tag.put("display", display);
        stack.setTag(tag);
        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    public void onItemTooltip(RenderTooltipEvent.Pre event) {
        if (event.getItemStack().getItem() == PixelmonItems.pixelmon_sprite.asItem()) {
            if (event.getItemStack().hasTag() && event.getItemStack().getTag().getBoolean("PokeChat")) {
                renderItem(event.getGraphics(), event.getItemStack(), event.getX() + event.getGraphics().guiWidth() - 48, event.getY());
                //Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(event.getStack(), event.getX() + event.getWidth() - 19, event.getY() + 3);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderItem(GuiGraphics matrixStack, ItemStack stack, int x, int y) {
        matrixStack.pose().pushPose();
        Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, false);

        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
        bakedmodel = bakedmodel.getOverrides().resolve(bakedmodel, stack, null, null, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float half = 8.0F * 2;
        float full = 16.0F * 2;

        matrixStack.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pose().translate((float)x, (float)y, 100.0F + 400);
        matrixStack.pose().translate(half, half, 0.0F);
        matrixStack.pose().scale(1.0F, -1.0F, 1.0F);
        matrixStack.pose().scale(full, full, full);
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedmodel.useAmbientOcclusion();
        /*if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }*/

        Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GUI, false, matrixStack.pose(), irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();

        RenderSystem.disableDepthTest();

        matrixStack.pose().popPose();
    }
}

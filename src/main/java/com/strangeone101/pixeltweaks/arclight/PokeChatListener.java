package com.strangeone101.pixeltweaks.arclight;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.strangeone101.pixeltweaks.PixelTweaks;
import com.strangeone101.pixeltweaks.tweaks.PokeChat;
import io.izzel.arclight.api.Arclight;
import io.izzel.arclight.common.bridge.bukkit.CraftItemStackBridge;
import io.izzel.arclight.common.mod.ArclightConnector;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;

public class PokeChatListener implements Listener {

    private PokeChat parent;

    public PokeChatListener(PokeChat parent) {
        this.parent = parent;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().matches(".*\\[(pokemon[1-6]?|party|slot[1-6])\\].*")) {
            /*for (int i = 0; i < 6; i++) { //Stops an infinite loop, if it somehow occurs due to broken find and replace
                Pokemon pokemon = null;
                String usedMatcher = parent.pokemon.toString();
                String chat = event.getMessage();
                Matcher matcher = parent.pokemon.matcher(chat);
                Matcher matcher2 = parent.party.matcher(chat);
                Matcher matcher3 = parent.slot.matcher(chat);

                if (matcher.find()) {
                    pokemon = StorageProxy.getParty(event.getPlayer().getUniqueId()).getSelectedPokemon();
                } else if (matcher3.find()) {
                    int slot = Integer.parseInt(matcher3.group().replaceAll("[^1-6]", ""));
                    pokemon = StorageProxy.getParty(event.getPlayer().getUniqueId()).getAll()[slot - 1];
                    usedMatcher = "\\[(pokemon|slot)" + slot + "]";
                }

                if (pokemon != null) {
                    ITextComponent component = pokemon.getFormattedDisplayName().deepCopy(); //The name of the pokemon. Nickname or localized.
                    Style style = component.getStyle().setBold(false).setItalic(false).setFormatting(TextFormatting.GREEN);
                    ((IFormattableTextComponent)component).mergeStyle(TextFormatting.RESET);

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
*/

        }
    }
}

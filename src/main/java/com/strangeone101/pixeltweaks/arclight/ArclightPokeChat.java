package com.strangeone101.pixeltweaks.arclight;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.strangeone101.pixeltweaks.tweaks.PokeChat.getItem;
import static com.strangeone101.pixeltweaks.tweaks.PokeChat.replaceInComponent;

public class ArclightPokeChat {

    public static final Pattern pokemon = Pattern.compile("\\[pokemon]", Pattern.CASE_INSENSITIVE);
    public static final Pattern party = Pattern.compile("\\[party]", Pattern.CASE_INSENSITIVE);
    public static final Pattern slot = Pattern.compile("\\[(?:slot|pokemon)[(1-6)]]", Pattern.CASE_INSENSITIVE);

    public static void registerListeners() {
        FakePlugin plugin = new FakePlugin();
        AsyncPlayerChatEvent.getHandlerList().register(new RegisteredListener(plugin, (listener, event) -> {
            if (event instanceof AsyncPlayerChatEvent) {
                String msg = ((AsyncPlayerChatEvent) event).getMessage();
                Player player = ((AsyncPlayerChatEvent) event).getPlayer();
                if (msg.toLowerCase().matches(".*\\[(pokemon[1-6]?|party|slot[1-6])\\].*")) {
                    for (int i = 0; i < 6; i++) { //Stops an infinite loop, if it somehow occurs due to broken find and replace
                        Pokemon pokemon = null;
                        String usedMatcher = ArclightPokeChat.pokemon.toString();
                        Matcher matcher = ArclightPokeChat.pokemon.matcher(msg);
                        Matcher matcher2 = ArclightPokeChat.party.matcher(msg);
                        Matcher matcher3 = ArclightPokeChat.slot.matcher(msg);

                        if (matcher.find()) {
                            pokemon = StorageProxy.getParty(player.getUniqueId()).getSelectedPokemon();
                        } else if (matcher3.find()) {
                            int slot = Integer.parseInt(matcher3.group().replaceAll("[^1-6]", ""));
                            pokemon = StorageProxy.getParty(player.getUniqueId()).getAll()[slot - 1];
                            usedMatcher = "\\[(pokemon|slot)" + slot + "]";
                        }

                        if (pokemon != null) {
                            ITextComponent component = pokemon.getFormattedDisplayName().deepCopy(); //The name of the pokemon. Nickname or localized.
                            Style style = component.getStyle().setBold(false).setItalic(false).setFormatting(TextFormatting.GREEN);

                            ((IFormattableTextComponent) component).mergeStyle(TextFormatting.RESET);

                            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(getItem(pokemon)));
                            ((IFormattableTextComponent) component).mergeStyle(style.setHoverEvent(hoverEvent));

                            component = TextComponentUtils.wrapWithSquareBrackets(component); //Wrap in square brackets

                            ITextComponent replaced = replaceInComponent(new StringTextComponent(String.format(((AsyncPlayerChatEvent) event).getFormat(), player.getDisplayName(), msg)), usedMatcher, component);

                            ((AsyncPlayerChatEvent) event).setCancelled(true);

                            for (Player p : ((AsyncPlayerChatEvent)event).getRecipients()) {
                                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(p.getUniqueId()).sendMessage(replaced, player.getUniqueId());
                            }
                            break;
                        } else {
                            return;
                        }
                    }
                }
            }
        }, EventPriority.HIGHEST, plugin,false));
    }
}

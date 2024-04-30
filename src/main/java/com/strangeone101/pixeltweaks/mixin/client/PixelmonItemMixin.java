package com.strangeone101.pixeltweaks.mixin.client;

import com.pixelmonmod.pixelmon.items.PixelmonItem;
import com.strangeone101.pixeltweaks.TweaksConfig;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(PixelmonItem.class)
public abstract class PixelmonItemMixin extends Item {

    @Unique
    private static final int pixelTweaks$LENGTH = 60;

    public PixelmonItemMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author StrangeOne101
     * @reason Overwrite the tooltip to split it based on the line length
     */
    @Overwrite(remap = false)
    public String getTooltipText() {
        return
                I18n.exists(this.getDescriptionId() + ".tooltip") && I18n.get(this.getDescriptionId() + ".tooltip", new Object[0]).isEmpty() ?
                        (TweaksConfig.autoWrapLoreLength.get() > 0 ?
                                String.join("\n", pixelTweaks$splitString(I18n.get(this.getDescriptionId() + ".tooltip"), TweaksConfig.autoWrapLoreLength.get()))
                                : I18n.get(this.getDescriptionId() + ".tooltip", new Object[0])) : "";
    }

    /**
     * Splits a string into multiple lines by the length provided
     * @param string The string to split
     * @param length The length of each line (in characters). Recommended is 60.
     */
    @Unique
    private static List<String> pixelTweaks$splitString(String string, int length)
    {
        Pattern p = Pattern.compile("\\G\\s*(.{1,"+length+"})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(string);
        List<String> l = new ArrayList<>();
        char lastColor = '7';
        while (m.find())
        {
            String s = m.group(1);
            l.add("\u00A7" + lastColor + s);
            if (s.contains("\u00A7")) {
                lastColor = s.charAt(s.lastIndexOf('\u00A7') + 1);
            }

        }
        if (l.isEmpty()) { //It can't be split with regex
            l.add(string);
            return l;
        }

        l.set(0, l.get(0).substring(2)); //Take off the extra white color at the front
        return l;
    }
}

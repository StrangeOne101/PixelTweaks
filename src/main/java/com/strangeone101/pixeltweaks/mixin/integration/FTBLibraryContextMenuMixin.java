package com.strangeone101.pixeltweaks.mixin.integration;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.strangeone101.pixeltweaks.PixelTweaks;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContextMenu.class)
public abstract class FTBLibraryContextMenuMixin extends Panel {

    private int nColumns = 0;
    private int maxRows = 0;
    private int columnWidth;
    private int MARGIN = 3;
    private boolean drawVerticalSeparators = true;

    public FTBLibraryContextMenuMixin(Panel panel) {
        super(panel);
    }

    /**
     * @author StrangeOne101
     * @reason Make Widgets split after so much
     */
    @Override
    @Overwrite(remap = false)
    public void alignWidgets() {
        setWidth(0);

        int totalHeight = 0;
        int maxWidth = 0;
        for (Widget widget : widgets) {
            maxWidth = Math.max(maxWidth, widget.width);
            totalHeight += widget.height + 1;
        }
        totalHeight += MARGIN * 2;

        // if there are too many menu items to fit vertically on-screen, use a multi-column layout
        nColumns = parent.getScreen().getScaledHeight() > 0 ? (totalHeight / parent.getScreen().getScaledHeight()) + 1 : 1;
        if (maxRows > 0) {
            nColumns = Math.max(nColumns, widgets.size() / maxRows);
        }
        int nRows = nColumns == 1 ? widgets.size() : (widgets.size() / (nColumns)) + 1;

        columnWidth = maxWidth + MARGIN * 2;
        setWidth(columnWidth * nColumns);

        int yPos = MARGIN;
        int prevCol = 0;
        int maxHeight = 0;
        for (int i = 0; i < widgets.size(); i++) {
            int col = i / nRows;
            if (prevCol != col) {
                yPos = MARGIN;
                prevCol = col;
            }
            Widget widget = widgets.get(i);
            widget.setPosAndSize(MARGIN + columnWidth * col, yPos, maxWidth, widget.height);
            maxHeight = Math.max(maxHeight, yPos + widget.height + 1);
            yPos += widget.height + 1;
        }

        setHeight(maxHeight + MARGIN - 1);
    }

    /**
     * @author StrangeOne101
     * @reason Allow multiple columns to be rendered
     */
    @Override
    @Overwrite(remap = false)
    public void draw(MatrixStack graphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        graphics.push();
        graphics.translate(0, 0, 900);
        super.draw(graphics, theme, x, y, w, h);
        if (drawVerticalSeparators) {
            for (int i = 1; i < nColumns; i++) {
                // vertical separator line between columns (only in multi-column layouts)
                Color4I.WHITE.withAlpha(130).draw(graphics, x + columnWidth * i, y + MARGIN, 1, height - MARGIN * 2);
            }
        }
        graphics.pop();
    }
}

package com.dcqinv_mixins.Player.Inventory;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookScreenMix {
    protected RecipeBookScreenMix(List<RecipeBookComponent.TabInfo> tabInfos, List<RecipeBookTabButton> tabButtons) {
        this.tabButtons = tabButtons;
    }
    @Shadow private int xOffset;@Shadow private int width;@Shadow private int height;
    @Shadow private final List<RecipeBookTabButton> tabButtons; @Shadow private ClientRecipeBook book;

    private void updateTabs(boolean isFiltering) {
        int i = (this.width - 215) / 2 - this.xOffset - 30;
        int j = (this.height - 194) / 2 + 3;
        int k = 27;
        int l = 0;

        for (RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
            ExtendedRecipeBookCategory extendedrecipebookcategory = recipebooktabbutton.getCategory();
            // Neo: Add support for modded search categories.
            if (extendedrecipebookcategory instanceof SearchRecipeBookCategory) {
                recipebooktabbutton.visible = true;
                recipebooktabbutton.setPosition(i, j + 27 * l++);
            } else if (recipebooktabbutton.updateVisibility(this.book)) {
                recipebooktabbutton.setPosition(i, j + 27 * l++);
                recipebooktabbutton.startAnimation(this.book, isFiltering);
            }
        }
    }
    @Overwrite
    private int getXOrigin() {
        this.xOffset = this.xOffset>0?69:0;
        return (this.width - 215) / 2 - this.xOffset;
    }
    @Overwrite
    private int getYOrigin() {
        return (this.height - 194) / 2;
    }
    @Overwrite
    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 69 || this.xOffset == 86;
    }
}

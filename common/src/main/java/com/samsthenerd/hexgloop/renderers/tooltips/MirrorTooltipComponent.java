package com.samsthenerd.hexgloop.renderers.tooltips;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.items.tooltips.MirrorTooltipData;
import com.samsthenerd.hexgloop.renderers.HUDOverlay;
import com.samsthenerd.hexgloop.utils.GloopyRenderUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

// yoinked and modified from hex

public class MirrorTooltipComponent implements TooltipComponent {

    private static final float RENDER_SIZE = 64f;

    private final ItemStack storedItem;

    public MirrorTooltipComponent(MirrorTooltipData tt) {
        this.storedItem = tt.storedItem();
    }

    @Override
    public void drawItems(TextRenderer font, int mouseX, int mouseY, DrawContext context) {
        var width = this.getWidth(font);
        var height = this.getHeight();

        // far as i can tell "mouseX" and "mouseY" are actually the positions of the corner of the tooltip
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(mouseX, mouseY, 500);
        RenderSystem.enableBlend();
        renderBG(context, HUDOverlay.SELECTED_HAND_MIRROR_INDICATOR);

        // renderText happens *before* renderImage for some asinine reason
//                RenderSystem.disableBlend();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        int itemSize = 32;

        GloopyRenderUtils.renderGuiItemIcon(storedItem, mouseX + width /2 - itemSize/2, mouseY + height / 2 - itemSize/2, itemSize);
        // pItemRenderer.renderGuiItemIcon(storedItem, mouseX + width /2, mouseY + height / 2);

        matrices.pop();
    }

    private static void renderBG(DrawContext context, Identifier background, float u, float v, int textWidth, int textHeight,
    int sWidth, int sHeight) {
        context.setShaderColor(1f, 1f, 1f, 1f);
        var matrices = context.getMatrices();
        matrices.push();
        matrices.scale(RENDER_SIZE / (float)sWidth, RENDER_SIZE / (float)sHeight, 1f);
        context.drawTexture(background, 0, 0, u, v, sWidth, sHeight, textWidth,
            textHeight);
        matrices.pop();
    }

    private static void renderBG(DrawContext context, Identifier background){
        renderBG(context, background, 0, 0, (int)RENDER_SIZE, (int) RENDER_SIZE, (int) RENDER_SIZE, (int) RENDER_SIZE);
    }

    // idk just use this for now 
    private static void renderBG(DrawContext context, HUDOverlay overlay){
        Pair<Integer, Integer> size = overlay.getTextureSize();
        renderBG(context, overlay.getTextureId(), overlay.getMinU(), overlay.getMinV(), size.getLeft(), size.getRight(),
            overlay.getTWidth(), overlay.getTHeight());
    }

    @Override
    public int getWidth(TextRenderer pFont) {
        return (int) RENDER_SIZE;
    }

    @Override
    public int getHeight() {
        return (int) RENDER_SIZE;
    }
}


package com.guillotina.granizados.screen;

import com.guillotina.granizados.GranizadosMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuilotinaScreen extends AbstractContainerScreen<GuilotinaMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GranizadosMod.MOD_ID, "textures/gui/container/guillotina.png");

    public GuilotinaScreen(GuilotinaMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
        // Mueve el título y el label del inventario
        this.titleLabelX = 7;
        this.titleLabelY = 4;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mx, int my) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = (width  - imageWidth)  / 2;
        int y = (height - imageHeight) / 2;

        // Fondo de la GUI (textura propia del mod)
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Barra de progreso (flecha): avanza de izquierda a derecha
        int progress    = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int w = (int) ((float) progress / maxProgress * 24);
            // La flecha en la textura está en UV (176, 14), tamaño 24×16
            graphics.blit(TEXTURE, x + 79, y + 34, 176, 14, w, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        renderBackground(graphics);
        super.render(graphics, mx, my, delta);
        renderTooltip(graphics, mx, my);
    }
}

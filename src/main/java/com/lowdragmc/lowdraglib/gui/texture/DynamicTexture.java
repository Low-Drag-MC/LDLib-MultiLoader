package com.lowdragmc.lowdraglib.gui.texture;

import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class DynamicTexture implements IGuiTexture {
    public Supplier<IGuiTexture> textureSupplier;

    public DynamicTexture(Supplier<IGuiTexture> rendererSupplier) {
        this.textureSupplier = rendererSupplier;
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        textureSupplier.get().draw(graphics, mouseX, mouseY, x, y, width, height);
    }
}

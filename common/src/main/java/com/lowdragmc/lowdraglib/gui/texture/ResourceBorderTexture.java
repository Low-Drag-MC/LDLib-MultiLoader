package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

@LDLRegister(name = "border_texture", group = "texture")
public class ResourceBorderTexture extends ResourceTexture {
    public static final ResourceBorderTexture BORDERED_BACKGROUND = new ResourceBorderTexture("ldlib:textures/gui/background.png", 16, 16, 4, 4);
    public static final ResourceBorderTexture BORDERED_BACKGROUND_INVERSE = new ResourceBorderTexture("ldlib:textures/gui/background_inverse.png", 16, 16, 4, 4);
    public static final ResourceBorderTexture BORDERED_BACKGROUND_BLUE = new ResourceBorderTexture("ldlib:textures/gui/bordered_background_blue.png", 195, 136, 4, 4);
    public static final ResourceBorderTexture BUTTON_COMMON = new ResourceBorderTexture("ldlib:textures/gui/button.png", 32, 32, 2, 2);
    public static final ResourceBorderTexture BAR = new ResourceBorderTexture("ldlib:textures/gui/button_common.png", 180, 20, 1, 1);
    public static final ResourceBorderTexture SELECTED = new ResourceBorderTexture("ldlib:textures/gui/selected.png", 16, 16, 2, 2);

    public static final ResourceBorderTexture VANILLA_BUTTON_PRESSED = (ResourceBorderTexture) new ResourceBorderTexture("minecraft:textures/gui/widgets.png", 200, 20, 3, 3).setSliceMode(NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffset(0, 46/256f);
    public static final ResourceBorderTexture VANILLA_BUTTON_NORMAL = (ResourceBorderTexture) new ResourceBorderTexture("minecraft:textures/gui/widgets.png", 200, 20, 3, 3).setSliceMode(NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffset(0, 66/256f);
    public static final ResourceBorderTexture VANILLA_BUTTON_SELECTED = (ResourceBorderTexture) new ResourceBorderTexture("minecraft:textures/gui/widgets.png", 200, 20, 3, 3).setSliceMode(NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffset(0, 86/256f);

    @Configurable(tips = {"ldlib.gui.editor.tips.corner_size.0", "ldlib.gui.editor.tips.corner_size.1"}, collapse = false)
    public Size borderSize;

    @Configurable(tips = "ldlib.gui.editor.tips.image_size", collapse = false)
    public Size imageSize;

    @Configurable(tips = {"ldlib.gui.editor.tips.mode.0", "ldlib.gui.editor.tips.mode.1", "ldlib.gui.editor.tips.mode.2", "ldlib.gui.editor.tips.mode.3"})
    @Getter
    public NineSliceMode mode = NineSliceMode.FIT;

    public ResourceBorderTexture() {
        this("ldlib:textures/gui/bordered_background_blue.png", 195, 136, 4, 4);
    }

    public ResourceBorderTexture(String imageLocation, int imageWidth, int imageHeight, int cornerWidth, int cornerHeight) {
        super(imageLocation);
        borderSize = new Size(cornerWidth, cornerHeight);
        imageSize = new Size(imageWidth, imageHeight);
    }

    public ResourceBorderTexture(String imageLocation, int imageWidth, int imageHeight, int cornerWidth, int cornerHeight, NineSliceMode sliceMode) {
        super(imageLocation);
        borderSize = new Size(cornerWidth, cornerHeight);
        imageSize = new Size(imageWidth, imageHeight);
        mode = sliceMode;
    }

    public ResourceBorderTexture setBorderSize(int width, int height) {
        this.borderSize = new Size(width, height);
        return this;
    }

    public ResourceBorderTexture setImageSize(int width, int height) {
        this.imageSize = new Size(width, height);
        return this;
    }

    @Override
    public ResourceTexture copy() {
        return new ResourceBorderTexture(imageLocation.toString(), imageSize.width, imageSize.height, borderSize.width, borderSize.height);
    }

    @Override
    public ResourceBorderTexture setColor(int color) {
        super.setColor(color);
        return this;
    }

    public ResourceBorderTexture setSliceMode(NineSliceMode mode) {
        this.mode = mode;
        return this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawSubAreaInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
//        drawBoarderInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        switch (mode) {
            case FIT -> {
                drawBoarderInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
                super.drawSubAreaInternal(graphics, x + borderSize.width, y + borderSize.height,
                width - 2 * borderSize.width, height - 2 * borderSize.height,
                cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight);
            }
            case STRETCH -> {
                drawBoarderStretchInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
                drawStretchInternal(graphics, x + borderSize.width, y + borderSize.height,
                    width - 2 * borderSize.width, height - 2 * borderSize.height,
                    cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight);
            }
            case TILE -> {
                drawBoarderTileInternal(graphics, x, y, width, height, drawnU, drawnV, drawnWidth, drawnHeight);
                drawTileInternal(graphics, x + borderSize.width, y + borderSize.height,
                width - 2 * borderSize.width, height - 2 * borderSize.height,
                cornerWidth, cornerHeight, 1 - 2 * cornerWidth, 1 - 2 * cornerHeight);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    protected void drawBoarderInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        //compute relative sizes
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        //draw up corners
        super.drawSubAreaInternal(graphics, x, y, borderSize.width, borderSize.height, 0, 0, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y, borderSize.width, borderSize.height, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubAreaInternal(graphics, x, y + height - borderSize.height, borderSize.width, borderSize.height, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + height - borderSize.height, borderSize.width, borderSize.height, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        super.drawSubAreaInternal(graphics, x + borderSize.width, y, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 0, 1 - 2 * cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + borderSize.width, y + height - borderSize.height, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 1 - cornerHeight, 1 - 2 * cornerWidth, cornerHeight);
        //draw vertical connections
        super.drawSubAreaInternal(graphics, x, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            0, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            1 - cornerWidth, cornerHeight, cornerWidth, 1 - 2 * cornerHeight);
    }

    @Environment(EnvType.CLIENT)
    protected void drawBoarderStretchInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        float sizeWidth = Math.min((width - borderSize.width * 2) / (imageSize.width - borderSize.width * 2), 1);
        float sizeHeight = Math.min((height - borderSize.height * 2) / (imageSize.height - borderSize.height * 2), 1);

        //compute relative sizes
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        //draw up corners
        super.drawSubAreaInternal(graphics, x, y, borderSize.width, borderSize.height, 0, 0, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y, borderSize.width, borderSize.height, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubAreaInternal(graphics, x, y + height - borderSize.height, borderSize.width, borderSize.height, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + height - borderSize.height, borderSize.width, borderSize.height, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        super.drawSubAreaInternal(graphics, x + borderSize.width, y, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 0, (1 - 2 * cornerWidth) * sizeWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + borderSize.width, y + height - borderSize.height, width - 2 * borderSize.width, borderSize.height,
            cornerWidth, 1 - cornerHeight, (1 - 2 * cornerWidth) * sizeWidth, cornerHeight);
        //draw vertical connections
        super.drawSubAreaInternal(graphics, x, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            0, cornerHeight, cornerWidth, (1 - 2 * cornerHeight) * sizeHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + borderSize.height, borderSize.width, height - 2 * borderSize.height,
            1 - cornerWidth, cornerHeight, cornerWidth, (1 - 2 * cornerHeight) * sizeHeight);
    }

    @Environment(EnvType.CLIENT)
    protected void drawBoarderTileInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        float sizeWidth = (width - borderSize.width * 2) / (imageSize.width - borderSize.width * 2);
        float sizeHeight = (height - borderSize.height * 2) / (imageSize.height - borderSize.height * 2);

        //compute relative sizes
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        //draw up corners
        super.drawSubAreaInternal(graphics, x, y, borderSize.width, borderSize.height, 0, 0, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y, borderSize.width, borderSize.height, 1 - cornerWidth, 0, cornerWidth, cornerHeight);
        //draw down corners
        super.drawSubAreaInternal(graphics, x, y + height - borderSize.height, borderSize.width, borderSize.height, 0, 1 - cornerHeight, cornerWidth, cornerHeight);
        super.drawSubAreaInternal(graphics, x + width - borderSize.width, y + height - borderSize.height, borderSize.width, borderSize.height, 1 - cornerWidth, 1 - cornerHeight, cornerWidth, cornerHeight);
        //draw horizontal connections
        if (sizeWidth != Float.POSITIVE_INFINITY) for(int tileX = 0; tileX < sizeWidth; tileX++) {
            float drawWidth = (imageSize.width - borderSize.width * 2) * Math.min(sizeWidth - tileX, 1);
            super.drawSubAreaInternal(graphics, x + borderSize.getWidth() + (imageSize.width - borderSize.width * 2) * tileX, y, drawWidth, borderSize.getHeight(), cornerWidth, 0,
                (1 - 2 * cornerWidth) * Math.min(sizeWidth - tileX, 1), cornerHeight);
            super.drawSubAreaInternal(graphics, x + borderSize.getWidth() + (imageSize.width - borderSize.width * 2) * tileX, y + (height - borderSize.getHeight()), drawWidth, borderSize.getHeight(), cornerWidth, 1 - cornerHeight,
                (1 - 2 * cornerWidth) * Math.min(sizeWidth - tileX, 1), cornerHeight);
        }
        if (sizeHeight != Float.POSITIVE_INFINITY) for(int tileY = 0; tileY < sizeHeight; tileY++) {
            float drawHeight = (imageSize.height - borderSize.height * 2) * Math.min(sizeHeight - tileY, 1);
            super.drawSubAreaInternal(graphics, x, y + borderSize.getHeight() + (imageSize.height - borderSize.height * 2) * tileY, borderSize.getWidth(), drawHeight, 0, cornerHeight,
                cornerWidth, (1 - 2 * cornerHeight) * Math.min(sizeHeight - tileY, 1));
            super.drawSubAreaInternal(graphics, x + (width - borderSize.getWidth()), y + borderSize.getHeight() + (imageSize.height - borderSize.height * 2) * tileY, borderSize.getWidth(), drawHeight, 1 - cornerWidth, cornerHeight,
                cornerWidth, (1 - 2 * cornerHeight) * Math.min(sizeHeight - tileY, 1));
        }

    }

    @Environment(EnvType.CLIENT)
    protected void drawStretchInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        float sizeWidth = Math.min(width / (imageSize.width - borderSize.width * 2), 1);
        float sizeHeight = Math.min(height / (imageSize.height - borderSize.height * 2), 1);
        //compute relative sizes
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;
        super.drawSubAreaInternal(graphics, x, y, width, height, cornerWidth, cornerHeight,
            (1 - 2 * cornerWidth) * sizeWidth, (1 - 2 * cornerHeight) * sizeHeight);
    }

    @Environment(EnvType.CLIENT)
    protected void drawTileInternal(GuiGraphics graphics, float x, float y, float width, float height, float drawnU, float drawnV, float drawnWidth, float drawnHeight) {
        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;

        float sizeWidth = width / (imageSize.width - borderSize.width * 2);
        float sizeHeight = height / (imageSize.height - borderSize.height * 2);
        int xSteps = (int) Math.ceil(sizeWidth % 1);
        int ySteps = (int) Math.ceil(sizeHeight % 1);
        if (sizeWidth != Float.POSITIVE_INFINITY && sizeHeight != Float.POSITIVE_INFINITY) for(int tileX = 0; tileX < Mth.clamp(sizeWidth, -10000, 10000); tileX++) {
            for(int tileY = 0; tileY < Mth.clamp(sizeHeight, -10000, 10000); tileY++) {
//                super.drawSubAreaInternal(graphics, x + width * tileX, y + height * height,
//                    imageSize.width - borderSize.width * 2 + 1, imageSize.height - borderSize.height * 2 + 1,
//                    cornerWidth, cornerHeight,
//                    drawnWidth * Math.min(sizeWidth - tileX, 1), drawnHeight * Math.min(sizeHeight - tileY, 1));
                float drawWidth = (imageSize.width - borderSize.width * 2) * Math.min(sizeWidth - tileX, 1);
                float drawHeight = (imageSize.height - borderSize.height * 2) * Math.min(sizeHeight - tileY, 1);
                super.drawSubAreaInternal(graphics, x + (imageSize.width - borderSize.width * 2) * tileX, y + (imageSize.height - borderSize.height * 2) * tileY, drawWidth, drawHeight, cornerWidth, cornerHeight,
                    (1 - 2 * cornerWidth) * Math.min(sizeWidth - tileX, 1), (1 - 2 * cornerHeight) * Math.min(sizeHeight - tileY, 1));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    protected void drawGuides(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        new ColorBorderTexture(-1, 0xffff0000).draw(graphics, 0, 0,
                x + width * offsetX, y + height * offsetY,
                (int) (width * imageWidth), (int) (height * imageHeight));

        float cornerWidth = borderSize.width * 1f / imageSize.width;
        float cornerHeight = borderSize.height * 1f / imageSize.height;

        new ColorBorderTexture(-1, 0xff00ff00).draw(graphics, 0, 0,
                x, y, (int) (width * cornerWidth), (int) (height * cornerHeight));
    }

    public enum NineSliceMode {
        FIT,
        STRETCH,
        TILE
    }
}

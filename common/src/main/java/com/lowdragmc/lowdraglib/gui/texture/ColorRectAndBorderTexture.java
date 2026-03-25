package com.lowdragmc.lowdraglib.gui.texture;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberColor;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.utils.Rect;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector4f;

import java.awt.*;

@LDLRegister(name = "color_rect_and_border_texture", group = "texture")
@Accessors(chain = true)
public class ColorRectAndBorderTexture extends TransformTexture {

    @Configurable
    @NumberColor
    @Setter
    public int fillColor;

    @Configurable
    @NumberColor
    @Setter
    public int borderColor;

    @Configurable
    @Setter
    @NumberRange(range = {0, 100}, wheel = 1)
    public float borderThickness;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLT;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusLB;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRT;

    @Configurable
    @Setter
    @NumberRange(range = {0, Float.MAX_VALUE}, wheel = 1)
    public float radiusRB;

    public ColorRectAndBorderTexture() {
        this(0xFF2A2A36, 0xFF3D3D4E, 1.0f);
    }

    public ColorRectAndBorderTexture(int fillColor, int borderColor, float borderThickness) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
    }

    public ColorRectAndBorderTexture(Color fillColor, Color borderColor, float borderThickness) {
        this.fillColor = fillColor.getRGB();
        this.borderColor = borderColor.getRGB();
        this.borderThickness = borderThickness;
    }

    public ColorRectAndBorderTexture setRadius(float radius) {
        this.radiusLB = radius;
        this.radiusRT = radius;
        this.radiusRB = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectAndBorderTexture setLeftRadius(float radius) {
        this.radiusLB = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectAndBorderTexture setRightRadius(float radius) {
        this.radiusRT = radius;
        this.radiusRB = radius;
        return this;
    }

    public ColorRectAndBorderTexture setTopRadius(float radius) {
        this.radiusRT = radius;
        this.radiusLT = radius;
        return this;
    }

    public ColorRectAndBorderTexture setBottomRadius(float radius) {
        this.radiusLB = radius;
        this.radiusRB = radius;
        return this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawInternal(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        if (width <= 0 || height <= 0) return;

        if (radiusLT > 0 || radiusLB > 0 || radiusRT > 0 || radiusRB > 0) {
            float maxRadius = Math.min(width, height) / 2f;

            DrawerHelper.drawFilledFrameRoundBox(
                    graphics,
                    Rect.ofRelative((int) x, width, (int) y, height),
                    borderThickness,
                    new Vector4f(
                            Math.min(maxRadius, radiusRT),
                            Math.min(maxRadius, radiusRB),
                            Math.min(maxRadius, radiusLT),
                            Math.min(maxRadius, radiusLB)
                    ),
                    fillColor,
                    borderColor
            );
        } else {
            DrawerHelper.drawSolidRect(graphics, (int) x, (int) y, width, height, fillColor);
            if (borderThickness > 0) {
                DrawerHelper.drawBorder(graphics, (int) x, (int) y, width, height, borderColor, (int) borderThickness);
            }
        }
    }
}

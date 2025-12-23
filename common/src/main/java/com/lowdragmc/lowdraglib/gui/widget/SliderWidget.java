package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Getter
@LDLRegister(name = "slider", group = "widget.basic")
@RemapPrefixForJS("kjs$")
public class SliderWidget extends Widget implements IConfigurableWidget {
    public final IGuiTexture defaultSliderBackground = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 1, 1, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f);
    public final IGuiTexture defaultSliderBackgroundHover = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 1, 1, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(20/256f);
    public final IGuiTexture defaultSliderHandel = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 3, 3, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(40/256f);
    public final IGuiTexture defaultSliderHandelHover = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 3, 3, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(60/256f);

    @Setter
    public int leftUpKey = 263, rightDownKey = 262;

    @Configurable
    public SliderDirection direction = SliderDirection.HORIZONTAL;

    @Configurable
    @Setter
    public IGuiTexture handleTexture;

    @Configurable
    @Setter
    public IGuiTexture handleHoverTexture;

    @Configurable
    @Setter
    @NumberRange(range = {0, Integer.MAX_VALUE})
    public int handleSize = 8;

    @Configurable
    @Setter
    public float minAmount = 0;

    @Configurable
    @Setter
    public float maxAmount = 10;

    @Configurable
    @Setter
    @NumberRange(range = {0, 1}, wheel = 0.01)
    public float sliderValue = 0.5f;

    @Configurable
    @Setter
    @NumberRange(range = {0, Integer.MAX_VALUE})
    public int valueStep;

    private boolean isDragging = false;

    public SliderWidget setDefaultKeysHorizontal() {
        rightDownKey  = 262;
        leftUpKey = 263;
        return this;
    }

    public SliderWidget setDefaultKeysVertical() {
        rightDownKey  = 264;
        leftUpKey = 265;
        return this;
    }

    public SliderWidget setDirection(SliderDirection direction) {
        this.direction = direction;
        return this;
    }

    public SliderWidget() {
        super(0,0,80,20);
    }

    public SliderWidget(Position selfPosition, Size size) {
        super(selfPosition, size);
    }

    @Override
    public void initTemplate() {
        IConfigurableWidget.super.initTemplate();
        backgroundTexture = defaultSliderBackground;
        handleTexture = defaultSliderHandel;
        handleHoverTexture = defaultSliderHandelHover;
    }

    @Override
    protected void drawBackgroundTexture(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.drawBackgroundTexture(graphics, mouseX, mouseY);
        drawHandle(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(button != 0) return false;
        if(!isDragging && !isMouseOverElement(mouseX, mouseY)) return false;
        isDragging = true;
        if (direction == SliderDirection.HORIZONTAL) sliderValue = Math.round(Mth.clamp((mouseX - getPositionX() - handleSize / 2f) / (getSizeWidth() - handleSize), 0, 1) * calculateStepSize()) / calculateStepSize();
        else sliderValue = Math.round(Mth.clamp((mouseY - getPositionY() - handleSize / 2f) / (getSizeHeight() - handleSize), 0, 1) * calculateStepSize()) / calculateStepSize();

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0) isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == leftUpKey) {
            sliderValue -= Mth.clamp(1/calculateStepSize(), 0, 1);
            return true;
        }
        if (keyCode == rightDownKey) {
            sliderValue += Mth.clamp(1f/calculateStepSize(), 0, 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawHandle(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        var isHovered = isMouseOverElement(mouseX, mouseY) || isDragging;
        if (handleTexture != null && (!isHovered || drawBackgroundWhenHover)) {
            if (direction == SliderDirection.HORIZONTAL) handleTexture.draw(graphics, mouseX, mouseY, getPositionX() + (getSizeWidth() - handleSize) * sliderValue, getPositionY(), handleSize, getSizeHeight());
            else handleTexture.draw(graphics, mouseX, mouseY, getPositionX(), getPositionY() + (getSizeHeight() - handleSize) * sliderValue, getSizeWidth(), handleSize);
        }
        if (handleHoverTexture != null && isHovered && isActive()) {
            if (direction == SliderDirection.HORIZONTAL) handleHoverTexture.draw(graphics, mouseX, mouseY, getPositionX() + (getSizeWidth() - handleSize) * sliderValue, getPositionY(), handleSize, getSizeHeight());
            else handleHoverTexture.draw(graphics, mouseX, mouseY, getPositionX(), getPositionY() + (getSizeHeight() - handleSize) * sliderValue, getSizeWidth(), handleSize);
        }
    }

    public float calculateStepSize() {
        return valueStep != 0 ? valueStep : direction == SliderDirection.HORIZONTAL ? getSizeWidth() : getSizeHeight();
    }

    public enum SliderDirection {
        HORIZONTAL,
        VERTICAL
    }
}

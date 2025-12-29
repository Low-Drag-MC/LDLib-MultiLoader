package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Consumer;

@LDLRegister(name = "slider", group = "widget.basic")
@RemapPrefixForJS("kjs$")
public class SliderWidget extends Widget implements IConfigurableWidget {
    public final IGuiTexture defaultSliderBackground = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 1, 1, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f);
    public final IGuiTexture defaultSliderBackgroundHover = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 1, 1, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(20/256f);
    public final IGuiTexture defaultSliderHandel = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 3, 3, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(40/256f);
    public final IGuiTexture defaultSliderHandelHover = new ResourceBorderTexture("minecraft:textures/gui/slider.png", 200, 20, 3, 3, ResourceBorderTexture.NineSliceMode.TILE).setImageWidthHeight(200/256f, 20/256f).setImageOffsetY(60/256f);

    @Setter
    @Getter
    @Configurable(tips = {"ldlib.gui.editor.tips.slider_keys.0", "ldlib.gui.editor.tips.slider_keys.1", "ldlib.gui.editor.tips.slider_keys.2", "ldlib.gui.editor.tips.slider_keys.3", "ldlib.gui.editor.tips.slider_keys.4", "ldlib.gui.editor.tips.slider_keys.5"})
    @NumberRange(range = {0, Integer.MAX_VALUE})
    public int leftUpKey = 263, rightDownKey = 262;

    @Configurable
    public SliderDirection direction = SliderDirection.HORIZONTAL;

    @Configurable
    @Setter
    @Getter
    public IGuiTexture handleTexture;

    @Configurable
    @Setter
    @Getter
    public IGuiTexture handleHoverTexture;

    @Configurable
    @Setter
    @Getter
    @NumberRange(range = {0, Integer.MAX_VALUE})
    public int handleSize = 8;

    @Configurable(tips = "ldlib.gui.editor.tips.min_max_amount")
    @Setter
    @Getter
    @NumberRange(range = {Float.MIN_VALUE, Float.MAX_VALUE})
    public float minAmount = 0;

    @Configurable(tips = "ldlib.gui.editor.tips.min_max_amount")
    @Setter
    @Getter
    @NumberRange(range = {Float.MIN_VALUE, Float.MAX_VALUE})
    public float maxAmount = 10;

    @Configurable
    @Getter
    @NumberRange(range = {0, 1}, wheel = 0.01)
    private float sliderValue = 0.5f;

    @Configurable(tips = "ldlib.gui.editor.tips.slider_steps")
    @Setter
    @NumberRange(range = {0, Integer.MAX_VALUE})
    public int valueStep;

    private boolean isDragging = false;
    private boolean isSelected = false;

    @Setter
    private Consumer<Float> sliderCallback = null;
    protected float oldValue;

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

    public SliderWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
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
        overlay = new TextTexture("50%%", Color.LIGHT_GRAY.getRGB()).setDropShadow(true);
    }


    @Environment(EnvType.CLIENT)
    @Override
    protected void drawBackgroundTexture(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.drawBackgroundTexture(graphics, mouseX, mouseY);
        drawHandle(graphics, mouseX, mouseY);
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        drawOverlay(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 0) return false;
        if (!isDragging && !isMouseOverElement(mouseX, mouseY)) return false;
        isDragging = true;
        if (direction == SliderDirection.HORIZONTAL) sliderValue = Math.round(Mth.clamp((mouseX - getPositionX() - handleSize / 2f) / (getSizeWidth() - handleSize), 0, 1) * calculateStepSize()) / calculateStepSize();
        else sliderValue = Math.round(Mth.clamp((mouseY - getPositionY() - handleSize / 2f) / (getSizeHeight() - handleSize), 0, 1) * calculateStepSize()) / calculateStepSize();

        if (oldValue != sliderValue) {
            writeClientAction(1, buffer -> {
                buffer.writeFloat(sliderValue);
            });
            if (sliderCallback != null) sliderCallback.accept(sliderValue);
        }
        oldValue = sliderValue;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        isSelected = isMouseOverElement(mouseX, mouseY);
        mouseDragged(mouseX, mouseY, button, 0, 0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSelected) {
            if (keyCode == leftUpKey) {
                sliderValue = Mth.clamp(sliderValue - 1 / calculateStepSize(), 0, 1);
                return true;
            }
            if (keyCode == rightDownKey) {
                sliderValue = Mth.clamp(sliderValue + 1f / calculateStepSize(), 0, 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(EnvType.CLIENT)
    private void drawHandle(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        var isHovered = isMouseOverElement(mouseX, mouseY) || isDragging;
        if (handleHoverTexture != null && (isHovered || isSelected) && isActive()) {
            if (direction == SliderDirection.HORIZONTAL) handleHoverTexture.draw(graphics, mouseX, mouseY, getPositionX() + (getSizeWidth() - handleSize) * sliderValue, getPositionY(), handleSize, getSizeHeight());
            else handleHoverTexture.draw(graphics, mouseX, mouseY, getPositionX(), getPositionY() + (getSizeHeight() - handleSize) * sliderValue, getSizeWidth(), handleSize);
        }
        if (handleTexture != null && !((isHovered || isSelected) || !drawBackgroundWhenHover)) {
            if (direction == SliderDirection.HORIZONTAL) handleTexture.draw(graphics, mouseX, mouseY, getPositionX() + (getSizeWidth() - handleSize) * sliderValue, getPositionY(), handleSize, getSizeHeight());
            else handleTexture.draw(graphics, mouseX, mouseY, getPositionX(), getPositionY() + (getSizeHeight() - handleSize) * sliderValue, getSizeWidth(), handleSize);
        }
    }

    public float calculateStepSize() {
        return valueStep != 0 ? valueStep : direction == SliderDirection.HORIZONTAL ? getSizeWidth() : getSizeHeight();
    }

    @Info("Gets the amount based on min and max values.")
    public float getAmount() {
        return Mth.lerp(sliderValue, minAmount, maxAmount);
    }

    @Info("Sets the amount based on min and max values.")
    public void setAmount(float amount) {
        sliderValue = (amount - minAmount) / (maxAmount - minAmount);
    }

    @Info("Sets the value (from 0 to 1)")
    public void setValue(float value) {
        sliderValue = value;

        if (isRemote()) {
            writeClientAction(2, buffer -> buffer.writeFloat(sliderValue));
        } else {
            writeUpdateInfo(2, buffer -> buffer.writeFloat(sliderValue));
        }
    }


    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        buffer.writeFloat(sliderValue);
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        setValue(buffer.readFloat());
    }

    @Override
    public void detectAndSendChanges() {
        writeUpdateInfo(1, buffer -> buffer.writeFloat(sliderValue));
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            sliderValue = buffer.readFloat();
            if (sliderCallback != null) {
                sliderCallback.accept(sliderValue = buffer.readFloat());
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 1) {
            sliderValue = buffer.readFloat();
        }
    }

    public enum SliderDirection {
        HORIZONTAL,
        VERTICAL
    }
}

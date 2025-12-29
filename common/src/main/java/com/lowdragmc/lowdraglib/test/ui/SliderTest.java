package com.lowdragmc.lowdraglib.test.ui;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.SliderWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LDLRegisterClient(name="slider_widget", group = "ui_test")
public class SliderTest implements IUITest {
    private final Logger log = LoggerFactory.getLogger(SliderTest.class);
    @Override
    public ModularUI createUI(IUIHolder holder, Player entityPlayer) {
        return new ModularUI(createUI2(), holder, entityPlayer);
    }

    public WidgetGroup createUI2() {
        // create a root container
        var root = new WidgetGroup();
        root.setSize(120, 40);
        root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

        SliderWidget slider = new SliderWidget(10, 10, 100,20);
        root.addWidget(slider);

        slider.initTemplate();
        slider.setSliderCallback(value -> {
            if (slider.getOverlay() instanceof TextTexture) {
                ((TextTexture) slider.getOverlay()).updateText(((int) (value * 100)) + "%%");
            }
        });

        return root;
    }
}

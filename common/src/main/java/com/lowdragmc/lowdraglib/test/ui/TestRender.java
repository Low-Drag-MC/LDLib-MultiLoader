package com.lowdragmc.lowdraglib.test.ui;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectAndBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.player.Player;

@LDLRegisterClient(name="render", group = "ui_test")
@NoArgsConstructor
public class TestRender implements IUITest{

    @Override
    public ModularUI createUI(IUIHolder holder, Player entityPlayer) {
        return new ModularUI(createUI2(), holder, entityPlayer);
    }

    public WidgetGroup createUI2() {
        var root = new WidgetGroup();
        root.setSize(100, 100);
        root.setBackground(new ColorRectAndBorderTexture().setRadius(6));
        return root;
    }
}

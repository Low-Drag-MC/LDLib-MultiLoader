package com.lowdragmc.lowdraglib.test.ui;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.player.Player;

@LDLRegisterClient(name="inventory", group = "ui_test")
@NoArgsConstructor
public class TestInventory implements IUITest {
    @Override
    public ModularUI createUI(IUIHolder holder, Player entityPlayer) {
        return new ModularUI(createUI2(), holder, entityPlayer);
    }

    public WidgetGroup createUI2() {
        // create a root container
        var root = new WidgetGroup();
        root.setSize(100, 100);
        root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

        root.addWidget(new PlayerInventoryWidget());

        return root;
    }
}

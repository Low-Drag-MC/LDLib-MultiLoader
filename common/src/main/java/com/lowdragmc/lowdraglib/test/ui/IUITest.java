package com.lowdragmc.lowdraglib.test.ui;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.utils.Size;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface IUITest {

    /**
     * Get the size of the UI.
     *
     * @return the size of the UI, if null, the UI will be full screen.
     */
    @Nullable
    default Size getSize() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    default int getScreenWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    @Environment(EnvType.CLIENT)
    default int getScreenHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    default ModularUI createUI(IUIHolder holder, Player entityPlayer) {
        var size = getSize();
        if (size != null) {
            return new ModularUI(size, holder, entityPlayer);
        }
        return new ModularUI(holder, entityPlayer);
    }

    /**
     * Create a real world scene.
     * @param size the size of the scene, if null, the scene will be full screen.
     * @param positions the captured positions of the world. if null, will append the player's surrounding positions.
     * @return SceneWidget
     */
    default SceneWidget createRealWorldScene(@Nullable Size size, @Nullable Collection<BlockPos> positions) {
        if (size == null) {
            size = new Size(Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
        }
        var sceneWidget = new SceneWidget(0, 0, size.width, size.height, Minecraft.getInstance().level);
        sceneWidget.useCacheBuffer();
        if (positions == null) {
            var playerPos = Minecraft.getInstance().player.getOnPos();
            sceneWidget.setRenderedCore(List.of(
                    playerPos,
                    playerPos.above(),
                    playerPos.below(),
                    playerPos.east(),
                    playerPos.west(),
                    playerPos.north(),
                    playerPos.south()
            ));
        } else {
            sceneWidget.setRenderedCore(positions);
        }
        return sceneWidget;
    }

}

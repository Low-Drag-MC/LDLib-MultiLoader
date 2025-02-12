package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurable;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.ui.ConfigPanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.widget.HsbColorWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.utils.ColorUtils;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import javax.annotation.Nullable;

import static com.lowdragmc.lowdraglib.gui.editor.data.resource.ColorsResource.RESOURCE_NAME;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote TextureResource
 */
@LDLRegister(name = RESOURCE_NAME, group = "resource")
public class ColorsResource extends Resource<Integer> {

    public final static String RESOURCE_NAME = "ldlib.gui.editor.group.colors";


    @Override
    public void buildDefault() {
        for (ColorPattern color : ColorPattern.values()) {
            data.put(color.name(), color.color);
        }
    }

    @Override
    public String name() {
        return RESOURCE_NAME;
    }

    @Override
    public ResourceContainer<Integer, ImageWidget> createContainer(ResourcePanel panel) {
        ResourceContainer<Integer, ImageWidget> container = new ResourceContainer<>(this, panel);
        container.setWidgetSupplier(k -> new ImageWidget(0, 0, 30, 30, new ColorRectTexture(getResourceOrDefault(k, -1)) {
                    @Override
                    public void updateTick() {
                        this.color = getResourceOrDefault(k, -1);
                    }
                }))
                .setDragging(this::getResource, ColorRectTexture::new)
                .setOnEdit(k -> openTextureConfigurator(container, k))
                .setOnAdd(key -> -1);
        return container;
    }

    private void openTextureConfigurator(ResourceContainer<Integer, ImageWidget> container, String key) {
        container.getPanel().getEditor().getConfigPanel().openConfigurator(ConfigPanel.Tab.RESOURCE, new IConfigurable() {
            @Override
            public void buildConfigurator(ConfiguratorGroup father) {
                var configurator = new WrapperConfigurator("color", wrapper -> new HsbColorWidget(0, 0, 150, 150)
                        .setOnChanged(newColor -> {
                            addResource(key, newColor);
                            wrapper.notifyChanges();
                        })
                        .setColorSupplier(() -> getResourceOrDefault(key, -1))
                        .setColor(getResourceOrDefault(key, -1)));
                var r = new NumberConfigurator("R",
                        () -> (int)(ColorUtils.red(getResourceOrDefault(key, -1)) * 255),
                        value -> {
                            int lastColor = getResourceOrDefault(key, -1);
                            addResource(key, ColorUtils.color(ColorUtils.alpha(lastColor), value.intValue() / 255f, ColorUtils.green(lastColor), ColorUtils.blue(lastColor)));
                        }, -1, true).setRange(0, 255);
                var g = new NumberConfigurator("G",
                        () -> (int)(ColorUtils.green(getResourceOrDefault(key, -1)) * 255),
                        value -> {
                            int lastColor = getResourceOrDefault(key, -1);
                            addResource(key, ColorUtils.color(ColorUtils.alpha(lastColor), ColorUtils.red(lastColor), value.intValue() / 255f, ColorUtils.blue(lastColor)));
                        }, -1, true).setRange(0, 255);
                var b = new NumberConfigurator("B",
                        () -> (int)(ColorUtils.blue(getResourceOrDefault(key, -1)) * 255),
                        value -> {
                            int lastColor = getResourceOrDefault(key, -1);
                            addResource(key, ColorUtils.color(ColorUtils.alpha(lastColor), ColorUtils.red(lastColor), ColorUtils.green(lastColor), value.intValue() / 255f));
                        }, -1, true).setRange(0, 255);
                var a = new NumberConfigurator("A",
                        () -> (int)(ColorUtils.alpha(getResourceOrDefault(key, -1)) * 255),
                        value -> {
                            int lastColor = getResourceOrDefault(key, -1);
                            addResource(key, ColorUtils.color(value.intValue() / 255f, ColorUtils.red(lastColor), ColorUtils.green(lastColor), ColorUtils.blue(lastColor)));
                        }, -1, true).setRange(0, 255);
                father.addConfigurators(configurator, r, g, b, a);
            }
        });
    }

    @Nullable
    @Override
    public Tag serialize(Integer value) {
        return IntTag.valueOf(value);
    }

    @Override
    public Integer deserialize(Tag nbt) {
        return nbt instanceof IntTag intTag ? intTag.getAsInt() : -1;
    }
}

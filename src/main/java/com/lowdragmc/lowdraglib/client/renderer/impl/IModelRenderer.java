package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.ISerializableRenderer;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlockEntity;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.WrapperConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@LDLRegisterClient(name = "json_model", group = "renderer")
public class IModelRenderer implements ISerializableRenderer {
    @Getter
    @Configurable(forceUpdate = false)
    protected ResourceLocation modelLocation;

    @OnlyIn(Dist.CLIENT)
    protected BakedModel itemModel;

    @OnlyIn(Dist.CLIENT)
    @Deprecated(forRemoval = true, since = "1.21")
    protected Map<Direction, BakedModel> blockModels;

    @Environment(EnvType.CLIENT)
    protected Map<ModelState, BakedModel> modelCaches;

    protected IModelRenderer() {
        modelLocation = ResourceLocation.withDefaultNamespace("block/furnace");
    }

    public IModelRenderer(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
        if (LDLib.isClient()) {
            blockModels = new ConcurrentHashMap<>();
            registerEvent();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        BakedModel model = getItemBakedModel();
        if (model == null) {
            return ISerializableRenderer.super.getParticleTexture();
        }
        return model.getParticleIcon();
    }

    @OnlyIn(Dist.CLIENT)
    protected UnbakedModel getModel() {
        return ModelFactory.getUnBakedModel(modelLocation);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack,
                           ItemDisplayContext transformType,
                           boolean leftHand, PoseStack poseStack,
                           MultiBufferSource buffer, int combinedLight,
                           int combinedOverlay, BakedModel model) {
        IItemRendererProvider.disabled.set(true);
        model = getItemBakedModel(stack);
        if (model != null) {
            Minecraft.getInstance().getItemRenderer().render(stack, transformType, leftHand, poseStack, buffer, combinedLight, combinedOverlay, model);
        }
        IItemRendererProvider.disabled.set(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean useBlockLight(ItemStack stack) {
        var model = getItemBakedModel(stack);
        if (model != null) {
            return model.usesBlockLight();
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public TriState useAO() {
        var model = getItemBakedModel();
        if (model != null) {
            return model.useAmbientOcclusion() ? TriState.DEFAULT : TriState.FALSE;
        }
        return TriState.FALSE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> renderModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        var ibakedmodel = getBlockBakedModel(level, pos, state);
        if (ibakedmodel == null) return Collections.emptyList();
        return ibakedmodel.getQuads(state, side, rand);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel() {
        if (itemModel == null) {
            var model = getModel();
            if (model instanceof BlockModel blockModel && blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                // fabric doesn't help us to fix vanilla bakery, so we have to do it ourselves
                model = ModelFactory.ITEM_MODEL_GENERATOR.generateBlockModel(this::materialMapping, blockModel);
            }
            itemModel = model.bake(
                    ModelFactory.getModelBaker(),
                    this::materialMapping,
                    BlockModelRotation.X0_Y0);
        }
        return itemModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel(ItemStack itemStack) {
        return getItemBakedModel();
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Deprecated(forRemoval = true, since = "1.21")
    protected BakedModel getBlockBakedModel(@Nullable BlockPos pos, @Nullable BlockAndTintGetter level) {
        return getRotatedModel(Direction.NORTH);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getBlockBakedModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state) {
        // TODO make it a standard api since 1.21
        if (level != null && pos != null && state != null && state.getBlock() instanceof IBlockRendererProvider provider) {
            var modelState = provider.getModelState(level, pos, state);
            if (modelState != null) {
                return modelCaches.computeIfAbsent(modelState, facing -> getModel().bake(
                        ModelFactory.getModeBaker(),
                        this::materialMapping,
                        modelState,
                        modelLocation));
            }
        }
        return getBlockBakedModel(pos, level);
    }

    @OnlyIn(Dist.CLIENT)
    @Deprecated(forRemoval = true, since = "1.21")
    public BakedModel getRotatedModel(Direction frontFacing) {
        return blockModels.computeIfAbsent(frontFacing, facing -> getModel().bake(
                ModelFactory.getModelBaker(),
                this::materialMapping,
                ModelFactory.getRotation(facing)));
    }

    @OnlyIn(Dist.CLIENT)
    protected TextureAtlasSprite materialMapping(Material material) {
        return material.sprite();
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        if (atlasName.equals(TextureAtlas.LOCATION_BLOCKS)) {
            itemModel = null;
            blockModels.clear();
            modelCaches.clear();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onAdditionalModel(Consumer<ModelResourceLocation> registry) {
        registry.accept(ModelResourceLocation.standalone(modelLocation));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isGui3d() {
        var model = getItemBakedModel();
        if (model == null) {
            return ISerializableRenderer.super.isGui3d();
        }
        return model.isGui3d();
    }

    // ISerializableRenderer
    public void initRenderer() {
        if (this.modelLocation != null) {
            if (LDLib.isClient()) {
                blockModels = new ConcurrentHashMap<>();
                modelCaches = new ConcurrentHashMap<>();
                registerEvent();
            }
        }
    }

    @ConfigSetter(field = "modelLocation")
    @SuppressWarnings("unused")
    public void updateModelWithoutReloadingResource(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
        if (LDLib.isClient()) {
            itemModel = null;
            if (blockModels != null) blockModels.clear();
            if (modelCaches != null) modelCaches.clear();
        }
    }

    @Override
    public void createPreview(ConfiguratorGroup father) {
        var preview = new WidgetGroup(0, 0, 100, 120);
        var level = new TrackedDummyWorld();
        level.addBlock(BlockPos.ZERO, BlockInfo.fromBlock(RendererBlock.BLOCK));
        Optional.ofNullable(level.getBlockEntity(BlockPos.ZERO)).ifPresent(blockEntity -> {
            if (blockEntity instanceof RendererBlockEntity holder) {
                holder.setRenderer(this);
            }
        });

        var sceneWidget = new SceneWidget(0, 0, 100, 100, level);
        sceneWidget.setRenderFacing(false);
        sceneWidget.setRenderSelect(false);
        sceneWidget.createScene(level);
        sceneWidget.getRenderer().setOnLookingAt(null); // better performance
        sceneWidget.setRenderedCore(Collections.singleton(BlockPos.ZERO), null);
        sceneWidget.setBackground(new ColorBorderTexture(2, ColorPattern.T_WHITE.color));

        preview.addWidget(new ButtonWidget(5, 110, 90, 10,
                new GuiTextureGroup(ColorPattern.T_GRAY.rectTexture().setRadius(5), new TextTexture("ldlib.gui.editor.tips.select_model")), cd -> {
            if (Editor.INSTANCE == null) return;
            File path = new File(Editor.INSTANCE.getWorkSpace(), "models");
            DialogWidget.showFileDialog(Editor.INSTANCE, "ldlib.gui.editor.tips.select_model", path, true,
                    DialogWidget.suffixFilter(".json"), r -> {
                        if (r != null && r.isFile()) {
                            updateModelWithoutReloadingResource(getModelFromFile(path, r));
                        }
                    });
        }));

        preview.addWidget(sceneWidget);
        father.addConfigurators(new WrapperConfigurator("ldlib.gui.editor.group.preview", preview));
    }

    private static ResourceLocation getModelFromFile(File path, File r){
        var id = path.getPath().replace('\\', '/').split("assets/")[1].split("/")[0];
        return ResourceLocation.fromNamespaceAndPath(id, r.getPath().replace(path.getPath(), "").replace(".json", "").replace('\\', '/').substring(1));
    }
}

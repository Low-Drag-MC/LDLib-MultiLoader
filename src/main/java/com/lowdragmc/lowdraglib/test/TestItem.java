package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * @author KilaBash
 * @date 2022/05/24
 * @implNote TestItem
 */
public class TestItem extends BlockItem implements IItemRendererProvider {

    public static final TestItem ITEM = new TestItem();

    private TestItem() {
        super(TestBlock.BLOCK, new Properties());
    }

    @Override
    public IRenderer getRenderer(ItemStack stack) {
        return TestBlock.BLOCK.getRenderer(TestBlock.BLOCK.defaultBlockState());
    }
}

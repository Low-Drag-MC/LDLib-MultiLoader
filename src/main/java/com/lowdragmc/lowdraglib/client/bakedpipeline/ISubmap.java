package com.lowdragmc.lowdraglib.client.bakedpipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

// This is from ConnectedTexturesMod.
public interface ISubmap {
    float getYOffset();

    float getXOffset();

    float getWidth();

    float getHeight();

    float getInterpolatedU(TextureAtlasSprite var1, float var2);

    float getInterpolatedV(TextureAtlasSprite var1, float var2);

    float[] toArray();

    ISubmap normalize();

    ISubmap relativize();
}

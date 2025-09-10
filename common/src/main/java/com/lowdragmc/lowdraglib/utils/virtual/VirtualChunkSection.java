package com.lowdragmc.lowdraglib.utils.virtual;

import com.lowdragmc.lowdraglib.utils.DummyWorld;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VirtualChunkSection extends LevelChunkSection {

    public final DummyWorld dummyWorld;

	public final int xStart;
	public final int yStart;
	public final int zStart;

    public VirtualChunkSection(DummyWorld dummyWorld, ChunkPos chunkPos, int yBase) {
        super(dummyWorld.registryAccess().registryOrThrow(Registries.BIOME));
        this.dummyWorld = dummyWorld;
        this.xStart = chunkPos.getMinBlockX();
        this.zStart = chunkPos.getMinBlockZ();
        this.yStart = yBase;
    }

	@Override
	public BlockState getBlockState(int x, int y, int z) {
		// ChunkSection#getBlockState expects local chunk coordinates, so we add to get
		// back into world coords.
        return dummyWorld.getBlockState(x + xStart, y + yStart, z + zStart);
	}

	@Override
	public BlockState setBlockState(int p_177484_1_, int p_177484_2_, int p_177484_3_, BlockState p_177484_4_,
		boolean p_177484_5_) {
		throw new IllegalStateException("Chunk sections should not be mutated in a fake world.");
	}
}

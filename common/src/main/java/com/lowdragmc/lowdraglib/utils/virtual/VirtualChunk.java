package com.lowdragmc.lowdraglib.utils.virtual;

import com.lowdragmc.lowdraglib.utils.DummyWorld;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VirtualChunk extends LevelChunk {
	boolean needsLight;
	final int x;
	final int z;

	public VirtualChunk(DummyWorld world, int x, int z) {
		super(
            world,
            new ChunkPos(x, z),
            UpgradeData.EMPTY,
            new LevelChunkTicks<>(),
            new LevelChunkTicks<>(),
            0L,
            Util.make(new LevelChunkSection[world.getSectionsCount()], sections -> {
                for (int i = 0; i < sections.length; i++) {
                    sections[i] = new VirtualChunkSection(world, new ChunkPos(x, z), i << 4);
                }
            }),
            null,
            null
            );

		this.needsLight = true;
		this.x = x;
		this.z = z;
	}

	public DummyWorld getDummyWorld() {
		return (DummyWorld) this.getLevel();
	}

    @Override
	public ChunkStatus getStatus() {
		return ChunkStatus.LIGHT;
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
		return null;
	}

	@Override
	public void setBlockEntity(BlockEntity p_177426_2_) {}

	@Override
	public void addEntity(Entity p_76612_1_) {}

	@Override
	public Set<BlockPos> getBlockEntitiesPos() {
		return Set.of();
	}

	@Override
	public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
		return Collections.emptyList();
	}

	@Override
	public void setHeightmap(Heightmap.Types p_201607_1_, long[] p_201607_2_) {}

	@Override
	public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types p_217303_1_) {
		return null;
	}

	@Override
	public void setUnsaved(boolean p_177427_1_) {}

	@Override
	public boolean isUnsaved() {
		return false;
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
		getDummyWorld().removeBlockEntity(pos);
	}

	@Override
	public ShortList[] getPostProcessing() {
		return new ShortList[0];
	}

	@Nullable
	@Override
	public CompoundTag getBlockEntityNbt(BlockPos p_201579_1_) {
		return null;
	}

	@Nullable
	@Override
	public CompoundTag getBlockEntityNbtForSaving(BlockPos p_223134_1_) {
		return null;
	}

	@Override
	public UpgradeData getUpgradeData() {
		return null;
	}

	@Override
	public void setInhabitedTime(long p_177415_1_) {}

	@Override
	public long getInhabitedTime() {
		return 0;
	}

	@Override
	public boolean isLightCorrect() {
		return needsLight;
	}

	@Override
	public void setLightCorrect(boolean needsLight) {
		this.needsLight = needsLight;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return getDummyWorld().getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return getDummyWorld().getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos p_204610_1_) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	@Nullable
	public StructureStart getStartForStructure(Structure structure) {
		return null;
	}

	@Override
	public void setStartForStructure(Structure structure, StructureStart start) {
	}

	@Override
	public Map<Structure, StructureStart> getAllStarts() {
		return Collections.emptyMap();
	}

	@Override
	public void setAllStarts(Map<Structure, StructureStart> structureStarts) {
	}

	@Override
	public LongSet getReferencesForStructure(Structure structure) {
		return LongSets.emptySet();
	}

	@Override
	public void addReferenceForStructure(Structure structure, long reference) {
	}

	@Override
	public Map<Structure, LongSet> getAllReferences() {
		return Collections.emptyMap();
	}

	@Override
	public void setAllReferences(Map<Structure, LongSet> structureReferences) {
	}

	@Override
	public TickContainerAccess<Fluid> getFluidTicks() {
		return BlackholeTickAccess.emptyContainer();
	}

	@Override
	public TicksToSave getTicksForSerialization() {
		return null;
	}

	@Override
	public TickContainerAccess<Block> getBlockTicks() {
		return BlackholeTickAccess.emptyContainer();
	}

}

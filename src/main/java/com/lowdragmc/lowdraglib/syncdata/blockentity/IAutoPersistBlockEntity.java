package com.lowdragmc.lowdraglib.syncdata.blockentity;

import com.lowdragmc.lowdraglib.utils.TagUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

/**
 * Interface for block entities that automatically save and load managed data.
 *
 * @see Persisted
 */
public interface IAutoPersistBlockEntity extends IManagedBlockEntity {

    default void saveManagedPersistentData(CompoundTag tag, HolderLookup.Provider provider, boolean forDrop) {
        var persistedFields = getRootStorage().getPersistedFields();
        for (var persistedField : persistedFields) {
            if (forDrop && !persistedField.getKey().isDrop()) {
                continue;
            }
            var data = persistedField.readPersisted(NbtOps.INSTANCE);
            if (data != null) {
                TagUtils.setTagExtended(tag, persistedField.getPersistedKey(), data);
            }
        }
        saveCustomPersistedData(tag, provider, forDrop);
    }

    default void loadManagedPersistentData(CompoundTag tag, HolderLookup.Provider provider) {
        var refs = getRootStorage().getPersistedFields();
        for (var ref : refs) {
            var key = ref.getPersistedKey();
            var data = TagUtils.getTagExtended(tag, key);
            if (data != null) {
                ref.writePersisted(NbtOps.INSTANCE, data);
            }
        }
        loadCustomPersistedData(tag, provider);
    }


    /**
     * write custom data to the save
     */
    default void saveCustomPersistedData(CompoundTag tag, HolderLookup.Provider provider, boolean forDrop) {

    }

    /**
     * read custom data from the save
     */
    default void loadCustomPersistedData(CompoundTag tag, HolderLookup.Provider provider) {

    }
}

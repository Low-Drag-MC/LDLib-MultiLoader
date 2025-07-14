package com.lowdragmc.lowdraglib.syncdata.ref;

import com.lowdragmc.lowdraglib.syncdata.accessor.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import com.lowdragmc.lowdraglib.syncdata.var.IReadOnlyManagedVar;
import com.lowdragmc.lowdraglib.syncdata.var.ReadOnlyVar;
import com.lowdragmc.lowdraglib.utils.LDLibExtraCodecs;
import com.mojang.serialization.DynamicOps;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;

import javax.annotation.Nullable;

public abstract class ReadOnlyManagedRef<TYPE> extends Ref<TYPE> {
    @Getter
    private final ReadOnlyVar<TYPE> readOnlyVar;
    protected @Nullable CompoundTag oldUid;

    protected ReadOnlyManagedRef(ReadOnlyVar<TYPE> readOnlyVar, ManagedKey key, IAccessor<TYPE> accessor) {
        super(key, accessor);
        this.readOnlyVar = readOnlyVar;
    }

    /**
     * Check if the var is a read-only managed var. If it is, the instance of the value can be changed internal via {@link IReadOnlyManagedVar}.
     */
    public boolean isReadOnlyManaged() {
        return getReadOnlyVar().isReadOnlyManaged();
    }

    @Override
    public TYPE readRaw() {
        return getReadOnlyVar().value();
    }

    @Override
    public final void update() {
        if (isReadOnlyManaged()) {
            readOnlyManagedUpdate();
        } else {
            readOnlyUpdate();
        }
    }

    /**
     * Update the value of the read-only var while the var is not a read-only managed var.
     */
    public abstract void readOnlyUpdate();

    public void readOnlyManagedUpdate() {
        var newValue = readRaw();
        if ((oldUid == null && newValue != null) || (oldUid != null && newValue == null)) {
            markAsDirty();
        }
        if (newValue != null) {
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            var newUid = field.getManagedVar().serializeUid(newValue);
            if (newUid.equals(oldUid)) {
                readOnlyUpdate();
            } else {
                markAsDirty();
                oldUid = newUid;
            }
        } else {
            oldUid = null;
        }
    }

    @Override
    public final void readSyncToStream(RegistryFriendlyByteBuf buffer) {
        if (isReadOnlyManaged()) {
            assert getReadOnlyVar().getManagedVar() != null;
            var value = readRaw();
            if (value == null) {
                buffer.writeBoolean(true);
            } else {
                buffer.writeBoolean(false);
                buffer.writeNbt(getReadOnlyVar().getManagedVar().serializeUid(value));
                readReadOnlySyncToStream(buffer);
            }
        } else {
            readReadOnlySyncToStream(buffer);
        }
    }

    public void readReadOnlySyncToStream(RegistryFriendlyByteBuf buffer) {
        super.readSyncToStream(buffer);
    }

    @Override
    public final void writeSyncFromStream(RegistryFriendlyByteBuf buffer) {
        if (isReadOnlyManaged()) {
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            if (buffer.readBoolean()) {
                field.set(null);
            } else {
                var uid = buffer.readNbt();
                var value = readRaw();
                var managedVar = field.getManagedVar();
                if (value == null || !managedVar.serializeUid(value).equals(uid)) {
                    value = managedVar.deserializeUid(uid);
                    field.set(value);
                }
                writeReadOnlySyncFromStream(buffer);
            }
        } else {
            writeReadOnlySyncFromStream(buffer);
        }
    }

    public void writeReadOnlySyncFromStream(RegistryFriendlyByteBuf buffer) {
        super.writeSyncFromStream(buffer);
    }


    @Override
    public final <T> T readInitialSync(DynamicOps<T> ops) {
        if (isReadOnlyManaged()) {
            var value = readRaw();
            if (value == null) {
                return LDLibExtraCodecs.createStringNull(ops);
            }
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            return ops.mapBuilder()
                    .add("uid", NbtOps.INSTANCE.convertMap(ops, field.getManagedVar().serializeUid(value)))
                    .add("payload", readReadOnlySync(ops))
                    .build(ops.empty()).getOrThrow();
        } else {
            return readReadOnlySync(ops);
        }
    }

    public  <T> T readReadOnlySync(DynamicOps<T> ops) {
        return super.readInitialSync(ops);
    }

    @Override
    public final <T> void writeInitialSync(DynamicOps<T> ops, T payload) {
        if (isReadOnlyManaged()) {
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            if (LDLibExtraCodecs.isEmptyOrStringNull(ops, payload)) {
                field.set(null);
            } else {
                var uid = ops.get(payload, "uid").result().map(data -> ops.convertTo(NbtOps.INSTANCE, data)).map(CompoundTag.class::cast).orElseThrow();
                var value = readRaw();
                var managedVar = field.getManagedVar();
                if (value == null || !managedVar.serializeUid(value).equals(uid)) {
                    value = managedVar.deserializeUid(uid);
                    field.set(value);
                }
                writeReadOnlySync(ops, ops.get(payload, "payload").result().orElse(ops.empty()));
            }
        } else {
            writeReadOnlySync(ops, payload);
        }
    }

    public <T> void writeReadOnlySync(DynamicOps<T> ops, T payload) {
        super.writeInitialSync(ops, payload);
    }

    @Override
    public final <T> T readPersisted(DynamicOps<T> ops) {
        if (isReadOnlyManaged()) {
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            var value = readRaw();
            if (value == null) {
                return LDLibExtraCodecs.createStringNull(ops);
            }
            return ops.mapBuilder()
                    .add("uid", NbtOps.INSTANCE.convertMap(ops, field.getManagedVar().serializeUid(value)))
                    .add("payload", readReadOnlyPersisted(ops))
                    .build(ops.empty()).getOrThrow();
        } else {
            return readReadOnlyPersisted(ops);
        }
    }

    public <T> T readReadOnlyPersisted(DynamicOps<T> ops) {
        return super.readPersisted(ops);
    }

    @Override
    public final <T> void writePersisted(DynamicOps<T> ops, T payload) {
        if (isReadOnlyManaged()) {
            var field = getReadOnlyVar();
            assert field.getManagedVar() != null;
            if (LDLibExtraCodecs.isEmptyOrStringNull(ops, payload)) {
                field.set(null);
            } else {
                var uid = ops.get(payload, "uid").result().map(data -> ops.convertTo(NbtOps.INSTANCE, data)).map(CompoundTag.class::cast).orElseThrow();
                var value = readRaw();
                var managedVar = field.getManagedVar();
                if (value == null || !managedVar.serializeUid(value).equals(uid)) {
                    value = managedVar.deserializeUid(uid);
                    field.set(value);
                }
                writeReadOnlyPersisted(ops, ops.get(payload, "payload").result().orElse(ops.empty()));
            }
        } else {
            writeReadOnlyPersisted(ops, payload);
        }
    }

    public  <T> void writeReadOnlyPersisted(DynamicOps<T> ops, T payload) {
        super.writePersisted(ops, payload);
    }
}

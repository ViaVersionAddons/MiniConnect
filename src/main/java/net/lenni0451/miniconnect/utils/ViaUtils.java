package net.lenni0451.miniconnect.utils;

import com.viaversion.nbt.tag.*;
import net.lenni0451.mcstructs.nbt.INbtTag;

import javax.annotation.Nullable;
import java.util.Map;

public class ViaUtils {

    public static Tag convertNbt(@Nullable final INbtTag nbt) {
        if (nbt == null) return null;
        return switch (nbt.getNbtType()) {
            case END -> throw new UnsupportedOperationException();
            case BYTE -> new ByteTag(nbt.asByteTag().getValue());
            case SHORT -> new ShortTag(nbt.asShortTag().getValue());
            case INT -> new IntTag(nbt.asIntTag().getValue());
            case LONG -> new LongTag(nbt.asLongTag().getValue());
            case FLOAT -> new FloatTag(nbt.asFloatTag().getValue());
            case DOUBLE -> new DoubleTag(nbt.asDoubleTag().getValue());
            case BYTE_ARRAY -> new ByteArrayTag(nbt.asByteArrayTag().getValue());
            case STRING -> new StringTag(nbt.asStringTag().getValue());
            case LIST -> {
                ListTag<? super Tag> listTag = new ListTag<>();
                for (INbtTag tag : nbt.asListTag()) {
                    listTag.add(convertNbt(tag));
                }
                yield listTag;
            }
            case COMPOUND -> {
                CompoundTag compoundTag = new CompoundTag();
                for (Map.Entry<String, INbtTag> entry : nbt.asCompoundTag()) {
                    compoundTag.put(entry.getKey(), convertNbt(entry.getValue()));
                }
                yield compoundTag;
            }
            case INT_ARRAY -> new IntArrayTag(nbt.asIntArrayTag().getValue());
            case LONG_ARRAY -> new LongArrayTag(nbt.asLongArrayTag().getValue());
        };
    }

}

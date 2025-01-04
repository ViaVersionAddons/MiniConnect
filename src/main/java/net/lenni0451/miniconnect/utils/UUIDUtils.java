package net.lenni0451.miniconnect.utils;

import com.google.common.hash.Hashing;

import java.util.UUID;

public class UUIDUtils {

    public static byte[] toBytes(final UUID uuid) {
        byte[] bytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >> 8 * (7 - i));
            bytes[i + 8] = (byte) (leastSigBits >> 8 * (7 - i));
        }
        return bytes;
    }

    public static String hash(final UUID uuid) {
        return Hashing.sha512().hashBytes(toBytes(uuid)).toString();
    }

}

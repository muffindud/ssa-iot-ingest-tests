package utils;

import java.util.List;
import java.util.Random;

public abstract class RandomUtils {
    public static String generateRandomMacAddress() {
        Random random = new Random();
        byte[] macAddressBytes = new byte[6];

        random.nextBytes(macAddressBytes);
        StringBuilder sb = new StringBuilder();

        for (byte b : macAddressBytes) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    public static <T> T getRandomFromList(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(items.size());
        return items.get(index);
    }
}

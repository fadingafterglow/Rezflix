package ua.edu.ukma.springers.rezflix.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UUIDUtils {

    public UUID fromStringOrNull(String str) {
        if (str == null) return null;
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

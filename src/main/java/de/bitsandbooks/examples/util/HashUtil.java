package de.bitsandbooks.examples.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HashUtil {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final HashFunction FUNCTION = Hashing.sha256();

    public static long getHashCode(String s) {
        return FUNCTION.hashString(s, UTF_8).asLong();
    }

}

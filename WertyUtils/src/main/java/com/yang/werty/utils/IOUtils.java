package com.yang.werty.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtils {

    public static void closeAll(AutoCloseable... closeables) {
        if (closeables != null) {
            for (AutoCloseable closeable : closeables) {
                closeQuietly(closeable);
            }
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String readAsString(InputStream in, Charset cs) throws IOException {
        StringBuilder builder = new StringBuilder();
        int len;
        byte[] bytes = new byte[128];
        while ((len = in.read(bytes)) != -1) {
            builder.append(new String(bytes, 0, len, cs));
        }
        return builder.toString();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        byte[] bytes = new byte[128];
        int count = 0;
        int len;
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
            count += len;
        }
        return count;
    }
}

package com.yang.werty.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

public class ClassPathResources implements Closeable {

    private static final String PROTOCOL_JAR = "jar";
    private static final boolean IS_WIN = System.getProperty("os.name").toUpperCase().startsWith("WINDOWS");
    private final Map<String, JarFile> jarFileMap = new ConcurrentHashMap<>();

    public InputStream openStream(URL url) throws IOException {
        String protocol = url.getProtocol();
        if (!PROTOCOL_JAR.equals(protocol)) {
            return url.openStream();
        }
        String file = url.getFile();
        int sepIdx = file.indexOf('!');
        String jarLocation = file.substring(IS_WIN ? 6 : 5, sepIdx);
        String entryName = file.substring(sepIdx + 2);
        JarFile jarFile;
        synchronized (jarFileMap) {
            if (jarFileMap.containsKey(jarLocation)) {
                jarFile = jarFileMap.get(jarLocation);
            } else {
                jarFile = new JarFile(jarLocation);
                jarFileMap.put(jarLocation, jarFile);
            }
        }
        return jarFile.getInputStream(jarFile.getEntry(entryName));
    }

    @Override
    public void close() {
        synchronized (jarFileMap) {
            for (JarFile jarFile : jarFileMap.values()) {
                IOUtils.closeQuietly(jarFile);
            }
        }
    }
}

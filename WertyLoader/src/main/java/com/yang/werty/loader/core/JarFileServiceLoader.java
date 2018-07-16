package com.yang.werty.loader.core;

import com.yang.werty.loader.ServiceContext;
import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static com.yang.werty.loader.Constants.KEY_SERVICE_CLASS;
import static com.yang.werty.loader.Constants.KEY_SERVICE_CONFIG;

public class JarFileServiceLoader extends AbstractServiceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryServiceLoader.class);
    private final Path path;
    private final URL pathUrl;
    private final ClassLoader parent;
    private long lastModifed = 0;

    public JarFileServiceLoader(Path path, ClassLoader parent, ServiceContext context) throws LoaderException {
        super(context);
        this.path = path.normalize();
        this.pathUrl = toPathUrl();
        this.parent = parent;
        if (!Files.exists(path)) {
            throw new LoaderException(this.path + " not found");
        }
    }

    @Override
    public void load() throws LoaderException {
        JarFile jarFile = null;
        try {
            long fileModified = Files.getLastModifiedTime(path).toMillis();
            if (fileModified > lastModifed) {
                destroyService();
                LOGGER.debug("load service from {}", path);
                jarFile = new JarFile(path.toFile());
                ZipEntry entry = jarFile.getEntry(KEY_SERVICE_CONFIG);
                if (entry == null) {
                    throw new LoaderException(KEY_SERVICE_CONFIG + " not found ");
                }
                Properties props = getServiceConfig(jarFile.getInputStream(entry));
                getContext().put(KEY_SERVICE_CONFIG, props);
                String className = props.getProperty(KEY_SERVICE_CLASS);
                initService(className, () -> new URLClassLoader(new URL[]{this.pathUrl}, this.parent));
                lastModifed = fileModified;
            }
        } catch (IOException e) {
            throw new LoaderException("service load Failed", e);
        } finally {
            IOUtils.closeQuietly(jarFile);
        }
    }

    private URL toPathUrl() throws LoaderException {
        try {
            return this.path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new LoaderException("can not convert path to url", e);
        }
    }
}

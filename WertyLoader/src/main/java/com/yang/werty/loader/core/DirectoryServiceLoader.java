package com.yang.werty.loader.core;

import com.yang.werty.loader.ServiceContext;
import com.yang.werty.loader.Version;
import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.yang.werty.loader.Constants.KEY_SERVICE_CLASS;
import static com.yang.werty.loader.Constants.KEY_SERVICE_CONFIG;
import static com.yang.werty.loader.Constants.KEY_SERVICE_VERSION;

public class DirectoryServiceLoader extends AbstractServiceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryServiceLoader.class);
    private static final String SUFFIX = ".jar";
    private final Path path;
    private final Path confPath;
    private final ClassLoader parent;
    private Version loadedVersion;

    public DirectoryServiceLoader(Path path, ClassLoader parent, ServiceContext context) throws LoaderException {
        super(context);
        this.path = path.normalize();
        this.confPath = this.path.resolve(KEY_SERVICE_CONFIG);
        this.parent = parent;
        if (!Files.exists(path)) {
            throw new LoaderException(this.path + " not found");
        }
    }

    @Override
    public void load() throws LoaderException {
        if (!Files.exists(confPath)) {
            throw new LoaderException(KEY_SERVICE_CONFIG + " not found");
        }
        try {
            Properties props = getServiceConfig(Files.newInputStream(confPath));
            String version = props.getProperty(KEY_SERVICE_VERSION);
            if (StringUtils.isEmpty(version)) {
                throw new LoaderException("property not found: " + KEY_SERVICE_VERSION);
            }
            if (loadedVersion == null || loadedVersion.isUpdated(version)) {
                destroyService();
                LOGGER.debug("load service from {}", path);
                getContext().put(KEY_SERVICE_CONFIG, props);
                String className = props.getProperty(KEY_SERVICE_CLASS);
                initService(className, this::newClassLoader);
                loadedVersion = Version.of(version);
            }
        } catch (IOException | RuntimeException e) {
            throw new LoaderException("service load failed", e);
        }
    }

    private URLClassLoader newClassLoader() throws LoaderException {
        List<Path> paths = new ArrayList<>();
        paths.add(path);
        try {
            Files.list(path).forEach(p -> addIfJar(paths, p));
            Path libPath = path.resolve("lib");
            if (Files.exists(libPath)) {
                paths.add(libPath);
                Files.walkFileTree(libPath, Collections.singleton(FileVisitOption.FOLLOW_LINKS), 3,
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                addIfJar(paths, file);
                                return super.visitFile(file, attrs);
                            }
                        });
            }
            int size = paths.size();
            URL[] urls = new URL[size];
            for (int idx = 0; idx < size; idx++) {
                urls[idx] = paths.get(idx).toUri().toURL();
            }
            return new URLClassLoader(urls, parent);
        } catch (IOException e) {
            throw new LoaderException(e);
        }
    }

    private void addIfJar(List<Path> paths, Path path) {
        if (path.toString().endsWith(SUFFIX)) {
            paths.add(path);
        }
    }
}

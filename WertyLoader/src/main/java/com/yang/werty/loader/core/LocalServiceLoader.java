package com.yang.werty.loader.core;

import com.yang.werty.loader.ServiceContext;
import com.yang.werty.loader.ServiceLoader;
import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.loader.service.ReloadableService;

import java.nio.file.Files;
import java.nio.file.Path;

public class LocalServiceLoader implements ServiceLoader<ReloadableService> {

    private final ServiceLoader<ReloadableService> serviceLoader;

    public LocalServiceLoader(Path path, ClassLoader parent, ServiceContext context) throws LoaderException {
        if (Files.isDirectory(path)) {
            this.serviceLoader = new DirectoryServiceLoader(path, parent, context);
        } else {
            this.serviceLoader = new JarFileServiceLoader(path, parent, context);
        }
    }

    @Override
    public void load() throws LoaderException {
        serviceLoader.load();
    }

    @Override
    public ReloadableService get() {
        return serviceLoader.get();
    }
}

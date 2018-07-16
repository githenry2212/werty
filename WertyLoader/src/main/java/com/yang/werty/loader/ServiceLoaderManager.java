package com.yang.werty.loader;

import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.loader.service.ReloadableService;

import java.nio.file.Path;

public interface ServiceLoaderManager {

    void addService(ServiceLoader<ReloadableService> serviceLoader) throws LoaderException;

    void addService(Path path, ServiceContext context) throws LoaderException;

    void reloadServices() throws LoaderException;
}

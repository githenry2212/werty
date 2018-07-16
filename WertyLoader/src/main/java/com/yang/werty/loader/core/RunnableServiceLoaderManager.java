package com.yang.werty.loader.core;

import com.yang.werty.loader.ServiceContext;
import com.yang.werty.loader.ServiceLoader;
import com.yang.werty.loader.ServiceLoaderManager;
import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.loader.service.ReloadableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RunnableServiceLoaderManager implements ServiceLoaderManager, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunnableServiceLoaderManager.class);
    private final List<ServiceLoader<ReloadableService>> serviceLoaderList;
    private int interval = 10;
    private boolean breakOnException = false;
    private boolean keepRunning = true;

    public RunnableServiceLoaderManager() {
        this.serviceLoaderList = Collections.synchronizedList(new ArrayList<>());
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setBreakOnException(boolean breakOnException) {
        this.breakOnException = breakOnException;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    @Override
    public void addService(ServiceLoader<ReloadableService> serviceLoader) throws LoaderException {
        serviceLoader.load();
        this.serviceLoaderList.add(serviceLoader);
    }

    @Override
    public void addService(Path path, ServiceContext context) throws LoaderException {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        this.addService(new LocalServiceLoader(path, parent, context));
    }

    @Override
    public void reloadServices() throws LoaderException {
        for (ServiceLoader<ReloadableService> serviceLoader : this.serviceLoaderList) {
            serviceLoader.load();
        }
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                TimeUnit.SECONDS.sleep(interval);
                this.reloadServices();
            } catch (Exception e) {
                LOGGER.error("service reload exception", e);
                if (breakOnException) {
                    break;
                }
            }
        }
    }
}

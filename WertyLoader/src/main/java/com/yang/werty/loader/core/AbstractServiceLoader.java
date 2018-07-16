package com.yang.werty.loader.core;

import com.yang.werty.loader.ClassLoaderCreator;
import com.yang.werty.loader.ServiceContext;
import com.yang.werty.loader.ServiceLoader;
import com.yang.werty.loader.exception.LoaderException;
import com.yang.werty.loader.service.ReloadableService;
import com.yang.werty.utils.IOUtils;
import com.yang.werty.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Properties;

import static com.yang.werty.loader.Constants.KEY_SERVICE_CLASS;

public abstract class AbstractServiceLoader implements ServiceLoader<ReloadableService> {

    private final ServiceContext context;
    private URLClassLoader classLoader;
    private ReloadableService service;

    public AbstractServiceLoader(ServiceContext context) {
        this.context = context;
    }

    @Override
    public abstract void load() throws LoaderException;

    @Override
    public ReloadableService get() {
        return service;
    }

    protected Properties getServiceConfig(InputStream in) throws IOException {
        try (InputStream propsStream = in) {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
    }

    protected void initService(String className, ClassLoaderCreator creator) throws LoaderException {
        if (StringUtils.isEmpty(className)) {
            throw new LoaderException("invalid service config: property "+ KEY_SERVICE_CLASS+" not found");
        }
        try {
            classLoader = creator.create();
            @SuppressWarnings("unchecked")
            Class<ReloadableService> serviceClass = (Class<ReloadableService>) classLoader.loadClass(className);
            service = serviceClass.newInstance();
            service.init(context);
        } catch (ClassNotFoundException e) {
            throw new LoaderException(e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new LoaderException("service class must has default constructor");
        }
    }

    protected void destroyService() {
        if (service != null) {
            service.destroy(context);
            IOUtils.closeQuietly(classLoader);
            classLoader = null;
        }
    }

    protected ServiceContext getContext() {
        return context;
    }
}

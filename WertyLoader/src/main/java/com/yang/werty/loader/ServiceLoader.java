package com.yang.werty.loader;

import com.yang.werty.loader.exception.LoaderException;

/**
 * service loader interface
 *
 * @param <T> service type
 */
public interface ServiceLoader<T> {

    /**
     * load service
     *
     * @throws LoaderException ex
     */
    void load() throws LoaderException;

    /**
     * get loaded service
     *
     * @return service instance
     */
    T get();
}

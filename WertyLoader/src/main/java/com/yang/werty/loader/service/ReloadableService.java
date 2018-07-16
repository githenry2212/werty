package com.yang.werty.loader.service;

import com.yang.werty.loader.ServiceContext;

/**
 * reloadable service interface
 */
public interface ReloadableService {

    /**
     * init service
     *
     * @param context service context
     */
    void init(ServiceContext context);

    /**
     * destroy service
     *
     * @param context service context
     */
    void destroy(ServiceContext context);
}

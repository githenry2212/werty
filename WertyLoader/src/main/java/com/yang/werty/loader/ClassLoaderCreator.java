package com.yang.werty.loader;

import com.yang.werty.loader.exception.LoaderException;

import java.net.URLClassLoader;

@FunctionalInterface
public interface ClassLoaderCreator {

    URLClassLoader create() throws LoaderException;
}

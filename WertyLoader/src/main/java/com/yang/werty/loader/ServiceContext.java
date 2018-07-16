package com.yang.werty.loader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContext {

    private final Map<String, Object> contextMap = new ConcurrentHashMap<>(8);

    public Object get(String name) {
        return contextMap.get(name);
    }

    public void put(String name, Object value) {
        contextMap.put(name, value);
    }

    public void remove(String name) {
        contextMap.remove(name);
    }

    public Set<String> keys() {
        return contextMap.keySet();
    }
}

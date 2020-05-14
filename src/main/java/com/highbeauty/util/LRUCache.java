package com.highbeauty.util;

import java.util.LinkedHashMap;

@SuppressWarnings("rawtypes")
public class LRUCache extends LinkedHashMap
{

    public LRUCache(int maxSize)
    {
        super(maxSize);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest)
    {
        return size() > maxElements;
    }

    private static final long serialVersionUID = 1L;
    protected int maxElements;
}
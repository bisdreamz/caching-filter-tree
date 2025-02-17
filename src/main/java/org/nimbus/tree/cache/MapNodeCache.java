package org.nimbus.tree.cache;

import org.nimbus.demo.filters.NodeCache;
import org.nimbus.tree.TreeNode;

import java.util.concurrent.ConcurrentHashMap;

public class MapNodeCache<I, O> implements NodeCache<I, O> {

    private final ConcurrentHashMap<Object, TreeNode<I, O>> cache;

    public MapNodeCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public NodeCache newInstance() {
        return new MapNodeCache();
    }

    @Override
    public TreeNode<I, O> get(Object key) {
        return cache.get(key);
    }

    @Override
    public void put(Object key, TreeNode<I, O> children) {

    }
}

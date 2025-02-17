package org.nimbus.tree;

import org.nimbus.demo.filters.NodeCache;
import org.nimbus.tree.cache.MapNodeCache;

/**
 * Collection of standard offered {@link NodeCache} implementations.
 */
public class NodeCaches {

    /**
     * No caching enabled for this node, which will then always re-evaluate every input.
     * Note, this also forces all further children to be evaluated as well.
     */
    public static NodeCache NO_CACHE = null;

    /**
     * An in memory (concurrent) hashmap cache. Quick and thread safe.
     * May not be the most memory efficient and does not feature any eviction policies.
     */
    public static <I, O> NodeCache<I, O> ofMap() {
        return new MapNodeCache();
    }

}

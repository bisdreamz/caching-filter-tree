package org.nimbus.demo.filters;

import org.nimbus.tree.NodeFilter;

public record NodeConfig<I, O>(NodeFilter<I, O> filter, NodeCache<I, O> cache) {

    /**
     * Construct a NodeConfig which represents the configuration of a certain field, or filter entry,
     * in the tree. For example,this may be an entry which filters input based on color criteria.
     * The cache is optional, and will be used to cache the evaluations and child nodes which
     * avoids the need to re evaluate logic and reduces it to a simple map lookup.
     * The order of nodeconfigs in a tree matters and will dicate the shape of the tree.
     * @param filter {@link NodeFilter}
     * @param cache {@link NodeCache} optional
     */
    public NodeConfig(NodeFilter<I, O> filter, NodeCache<I, O> cache) {
        if (filter == null)
            throw new IllegalArgumentException("filter cannot be null");

        this.filter = filter;
        this.cache = cache;
    }
}

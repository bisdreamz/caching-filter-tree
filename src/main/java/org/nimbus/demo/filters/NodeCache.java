package org.nimbus.demo.filters;

import org.nimbus.tree.TreeNode;

/**
 * Represents an implementation of a node cache, which caches precomputed results from a specific point in the
 * tree down, preventing further need to recalculate the whole remainder of the branch.
 * @param <I> The input to match against to retrieve from the cache.
 * @param <O>
 * @implNote Cache implementation must be thread safe
 * @implNote It is suggested to leverage compound keys here manually. If you have an input which can have 4
 * values, then leverage a compound key of all of them to cache the computed results such that next time
 * an input has the same values, it results in only a single cache lookup.
 */
public interface NodeCache<I, O> {

    /**
     * @return A new instance of this cache. Be aware, depending on node cardinality there may be many cache instances
     * as a cache instance is required for each
     */
    public NodeCache<I,O> newInstance();

    /**
     * @param key The lookup key
     * @return A cache {@link TreeNode} if any, representing a child results node which has already been evaluated
     * for this given input
     */
    public TreeNode<I, O> get(Object key);

    /**
     * Store a tree node in the cache, which represents the computed result of evaluating the provided input
     * against the matching criteria of this node.
     * @param key The key of which to lookup the child node. The key uniquely represents the field(s)
     *            which include matching child criteria, as determined by the {@link org.nimbus.tree.NodeFilter}
     * @param child The child node to store with matching computed results for the key
     */
    public void put(Object key, TreeNode<I, O> child);

}

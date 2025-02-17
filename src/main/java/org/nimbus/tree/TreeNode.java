package org.nimbus.tree;

import org.nimbus.demo.filters.NodeCache;
import org.nimbus.demo.filters.NodeConfig;

import java.util.*;

/**
 * Represents a branch node within the tree at a predefined depth, which handles
 * matching and caching of child values for a specific {@link NodeFilter}. This can be thought of
 * as representing a specific field of matching logic, ordered, and in a natural position of the tree
 * as defined by its matching parents and children.
 * @param <I>
 * @param <O>
 */
public class TreeNode<I, O> {

    private final Set<O> matches;
    private final NodeFilter<I, O> filter;
    private final NodeCache<I, O> cache;
    private final List<NodeConfig<I, O>> childNodes;
    private final boolean leaf;

    /**
     * Construct a TreeNode for a particular posiiton in the tree.
     * @param childNodes The childNodes from its parent, of which
     *                   this node becomes the first node in the list
     *                   and passes down remaining nodes to its children
     *                   when spawning.
     * @param matches All matching parent outputs. This node will use them
     *                as starting reference when performing its own
     *                output filtering.
     */
    public TreeNode(List<NodeConfig<I, O>> childNodes, Set<O> matches) {
        if (childNodes == null)
            throw new IllegalArgumentException("Child nodes cannot be null");

        this.leaf = childNodes.isEmpty();

        if (!this.leaf) {
            NodeConfig myNode = childNodes.removeFirst();
            this.filter = myNode.filter();
            this.cache = myNode.cache() != null ? myNode.cache().newInstance() : null;
        } else {
            this.filter = null;
            this.cache = null;
        }

        this.childNodes = childNodes;

        this.matches = Collections.unmodifiableSet(matches);
    }

    public Set<O> matches(I input) {
        if (this.leaf || matches.isEmpty())
            return matches;

        if (this.cache != null) {
            Object key = this.filter.lookupKey(input);
            if (key == null)
                throw new NullPointerException("Filter lookupKey method returned null");

            // if we have a precomputed child in the cache, use it
            TreeNode<I, O> directChild = this.cache.get(key);
            if (directChild != null)
                return directChild.matches(input);

            Set<O> childMatches = new HashSet<>(this.matches);
            this.filter.filter(input, childMatches);

            TreeNode<I, O> newChild = new TreeNode<>(new ArrayList<>(childNodes), childMatches);
            this.cache.put(key, newChild);

            return newChild.matches(input);
        }

        // no cache, which means from this node down the whole remainder of the branch we must evaluate every input
        // so were gonna fast track these in a loopty loop
        Set<O> childMatches = new HashSet<>(this.matches);
        for (NodeConfig<I, O> childNode : childNodes) {
            childNode.filter().filter(input, childMatches);
            if (childMatches.isEmpty())
                break;
        }

        return Collections.unmodifiableSet(childMatches);
    }

}

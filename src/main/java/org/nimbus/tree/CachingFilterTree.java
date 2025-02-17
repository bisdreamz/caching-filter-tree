package org.nimbus.tree;

import org.nimbus.demo.filters.NodeConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A tree which is responsible for matching a set of consistent possible outputs against an input,
 * in which each input would typically be some form of custom if/then logic. The FilterTree accelerates
 * such business rule use cases by optionally caching precomputed results at each node level, which
 * can reduce evaluating thousands of <i>things</i> down to a simple map lookup for each field node.
 * @param <I> The type of the Input or <i>request</i> to match against
 * @param <O> The <i>outputs</i> or all possible matches, which are matched against the input
 */
public class CachingFilterTree<I, O> {

    private final Set<O> outputs;
    private final TreeNode<I, O> root;

    /**
     * Construct a new filter tree.
     * @param nodes List of {@link NodeConfig} entries representing the logical filtering
     *              fields. The order here is important and dictates the order in which
     *              fields are evaluated and the tree is built!
     * @param outputs The constant set of possible outputs which the tree should match against.
     */
    public CachingFilterTree(List<NodeConfig<I, O>> nodes, Set<O> outputs) {
        if (nodes == null || nodes.isEmpty())
            throw new IllegalArgumentException("Node configs cannot be null or empty");

        if (outputs == null || outputs.isEmpty())
            throw new IllegalArgumentException("Outputs cannot be null or empty");

        if (new HashSet<>(nodes).size() != nodes.size())
            throw new IllegalArgumentException("Filters must not have duplicate entries");

        boolean cache = nodes.getFirst().cache() != null;
        if (cache) {
            for (NodeConfig<I, O> node : nodes) {
                boolean nodeIsCache = node.cache() != null;
                if (!nodeIsCache && cache) {
                    // from this branch downward, no cache!!
                    cache = false;
                    continue;
                }

                if (nodeIsCache && !cache)
                    throw new IllegalArgumentException("Found child node with cache but parent without cache. " +
                            "Any node without means all children must disable cache also!");
            }
        }

        this.root = new TreeNode<>(new ArrayList<>(nodes), outputs);
        this.outputs = outputs;
    }

    /**
     * Return a set of the outputs matched against the provided input.
     * @implNote Node caching will be used
     * where enabled and applicable, otherwise any remaining nodes
     * with caching disabld will be re-evaluated for every <i>matches</i>
     * invocation.
     * @param input Input to match against outputs.
     * @return Unmodifiable set of matching outputs.
     */
    public Set<O> matches(I input) {
        return this.root.matches(input);
    }

}

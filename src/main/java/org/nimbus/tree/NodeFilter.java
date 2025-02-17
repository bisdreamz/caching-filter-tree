package org.nimbus.tree;

import java.util.Set;

/**
 * Represents the matching logic for a particular node (field) in the tree. For example
 * if you may commonly have a block in your logic flow which says <i>if animal.legCount() > 4 && ..</i>
 * then this filter may represent that legCount branch.<br />
 * The NodeFilter becomes most powerful when paired with a {@link org.nimbus.demo.filters.NodeCache}
 * which will consume memory but prevents re evaluating every input for recognized values.
 * Additionally, filter outputs may leverage compound keys during caching which means the
 * the typical lookup-per-value scenario may be reduced down to a single lookup per node.
 * @param <I> Input type
 * @param <O> Output(s) type
 */
public interface NodeFilter<I, O> {

    /**
     * Should accept an input and apply the required matching logic to reduce the
     * provided set of outputs to only those which pass.
     * @param input
     * @param outputs
     */
    public void filter(I input, Set<O> outputs);


    /**
     * Generate a unique lookup cache key for the given input, if caching is enabled. E.g. if the input is an animal and this filter
     * belongs to a legCount, then the returned cache key ay be the legCount value. Then the next input provided
     * with a legCount=4 can do a simple map lookup without eval again. Likewise, it is possible to leverage
     * compound keys or multi value inputs to avoid multiple lookups or expensive re-evaluations.
     * E.g. if an input has multiple categories, return a single compound key which represents the unique
     * set of categories. Likewise, the next input with these values is reduced to a single cache get.
     * @param input
     * @return The unique key representing the field values of this input for this associated node.
     * <b>should never return null</b>. One may return a predefined character to suit a "match any" behavior.
     */
    public Object lookupKey(I input);

}

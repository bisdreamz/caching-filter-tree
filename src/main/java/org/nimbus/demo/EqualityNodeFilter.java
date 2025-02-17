package org.nimbus.demo;

import org.nimbus.tree.NodeFilter;

import java.util.Set;

public class EqualityNodeFilter<I, O> implements NodeFilter<I, O> {

    @Override
    public void filter(Object input, Set outputs) {

    }

    @Override
    public Object lookupKey(Object input) {
        return input.hashCode();
    }

}

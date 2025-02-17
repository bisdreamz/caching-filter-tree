package org.nimbus.demo.filters;


import org.nimbus.demo.TestInput;
import org.nimbus.demo.TestOutput;

import java.util.Set;

public class TestConstFilter implements TestFilter {

    @Override
    public void filter(TestInput input, Set<TestOutput> outputs) {
        outputs.removeIf(output -> {
            return !input.getListVal().contains(output.getConstValue());
        });
    }

    @Override
    public Integer lookupKey(TestInput input) {
        // very simple cumulative hash
        return input.getListVal().stream().mapToInt(cnst -> cnst.hashCode()).sum();
    }

}

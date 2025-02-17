package org.nimbus.demo.filters;

import org.nimbus.demo.TestInput;
import org.nimbus.demo.TestOutput;

import java.util.Set;

public class TestRangeFilter implements TestFilter {
    @Override
    public void filter(TestInput input, Set<TestOutput> outputs) {
        outputs.removeIf(output -> {
            return !(input.getRangeValueMin() <= output.getRangeValue() &&
                    input.getRangeValueMax() >= output.getRangeValue());
        });
    }

    @Override
    public String lookupKey(TestInput input) {
        return input.getRangeValueMin() + ":" + input.getRangeValueMax();
    }
}

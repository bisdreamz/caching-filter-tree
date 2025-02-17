package org.nimbus.demo;

import org.nimbus.demo.filters.NodeConfig;
import org.nimbus.demo.filters.TestConstFilter;
import org.nimbus.demo.filters.TestRangeFilter;
import org.nimbus.tree.CachingFilterTree;
import org.nimbus.tree.cache.MapNodeCache;

import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String... a) {
        List<NodeConfig<TestInput, TestOutput>> filters = List.of(
                new NodeConfig<>(new TestRangeFilter(), new MapNodeCache<>()),
                new NodeConfig<>(new TestConstFilter(), new MapNodeCache<>())
        );

        TestInput ti1 = TestInput.sampleOne();

        CachingFilterTree<TestInput, TestOutput> tree = new CachingFilterTree<>(filters, Set.of(
                new TestOutput(ti1.getStringValue(), ti1.getIntValue(),ti1.getRangeValueMin() - 1, ti1.getListVal().getFirst()),
                new TestOutput(ti1.getStringValue(), ti1.getIntValue()-5, ti1.getRangeValueMin(), TestInput.sampleOne().getListVal().getFirst())
        ));

        System.out.println(tree.matches(ti1).size());
    }

}

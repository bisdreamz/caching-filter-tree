# Caching Filter Tree

A high-performance Java library for building and evaluating hierarchical business rules with intelligent caching. This library accelerates complex filtering operations by caching intermediate results at each decision node, potentially reducing thousands of evaluations to simple map lookups. It also helps enforce a standard of structure and organization between rule conditions.

## Features

- **Hierarchical Filtering**: Build tree structures representing complex business rules and conditions
- **Intelligent Caching**: Cache intermediate results at each node level to avoid redundant evaluations
- **Flexible Cache Implementation**: Bring your own caching solution (in-memory, Caffeine, on-disk, etc.)
- **Type-Safe**: Fully generic implementation supporting any input and output types
- **Thread-Safe**: When used with appropriate cache implementations

## Quick Start

```java
// Define your filters (decision nodes)
List<NodeConfig<TestInput, TestOutput>> filters = List.of(
    new NodeConfig<>(new TestRangeFilter(), new MapNodeCache<>()),
    new NodeConfig<>(new TestConstFilter(), new MapNodeCache<>())
);

// Define possible outputs
Set<TestOutput> possibleOutputs = Set.of(
    new TestOutput("value1", 100, 50, "list1"),
    new TestOutput("value2", 200, 75, "list2")
);

// Create the filter tree
CachingFilterTree<TestInput, TestOutput> tree = 
    new CachingFilterTree<>(filters, possibleOutputs);

// Match inputs against the tree
TestInput input = TestInput.sampleOne();
Set<TestOutput> matches = tree.matches(input);
```

## How It Works

1. **Node Configuration**: Each `NodeConfig` represents a decision point in your business logic
2. **Tree Construction**: The library builds an optimized tree structure based on your filter order
3. **Caching**: Results are cached at each node level when enabled
4. **Matching**: Inputs are evaluated against the tree, requiring one map lookup per cached node
5. **Compound Keys**: Support for caching multiple field values under a single key

### Caching Behavior

The caching mechanism is designed to minimize evaluations in multi-value scenarios:

- **Compound Keys**: For inputs with multiple values (e.g., arrays or multiple fields), you can generate a compound cache key. This means future inputs with the same combination of values require only a single lookup, rather than evaluating or caching each value individually.

- **Per-Node Lookups**: For a tree with N cached nodes, matching requires exactly N map lookups. While this means a 30-node tree needs 30 lookups, it's significantly faster than re-evaluating complex business rules thousands of times.

- **Optional Caching**: Caching can be disabled (by passing null for the cache) for any node. However, if a node has caching disabled, all its children must also have caching disabled. This creates a smaller memory footprint but requires full evaluation of the uncached nodes for each match operation.

## Creating Custom Filters

Implement the `NodeFilter` interface to create your own filtering logic:

```java
public class MyCustomFilter implements NodeFilter<MyInput, MyOutput> {
    @Override
    public void filter(MyInput input, Set<MyOutput> outputs) {
        // Your filtering logic here
    }

    @Override
    public Object lookupKey(MyInput input) {
        // Return a unique key for caching
        return input.getRelevantField();
    }
}
```

## Cache Configuration

The library supports flexible caching strategies:

- Use the provided `MapNodeCache` for simple in-memory caching
- Implement `NodeCache` interface for custom caching solutions
- Disable caching for specific nodes in the tree
- Mix cached and non-cached nodes (with restrictions)

## Performance Considerations

- Order filters by selectivity (most selective first) for optimal performance
- Consider memory usage when enabling caching for large datasets
- Use compound cache keys to optimize inputs with multiple values
- Strategic cache disabling: Consider disabling caching for the last few nodes if memory is constrained
- Cache invalidation strategy depends on your chosen cache implementation
- Performance scales with cache hit rates: Frequently repeated input patterns benefit most

## Requirements

- Java 21 or higher
- No additional dependencies for core functionality

## License

-- By request

## Contributing

Lmk

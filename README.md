# Caching Filter Tree

A high-performance Java library for building and evaluating hierarchical business rules with intelligent caching. This library accelerates complex filtering operations by caching intermediate results at each decision node, potentially reducing thousands of evaluations to a series of simple map lookups.

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

## Example Use Cases

### Traditional Conditional Logic vs Filter Tree

Consider a typical scenario where you need to match animals against a set of criteria. Here's how you might transform traditional conditional logic into an efficient filter tree:

#### Traditional Approach
```java
public Set<AnimalCriteria> findMatchingCriteria(Animal animal, Set<AnimalCriteria> possibleMatches) {
    Set<AnimalCriteria> matches = new HashSet<>();
    
    // Without caching, these expensive operations must be performed
    // for EVERY animal, EVERY time the method is called
    for (AnimalCriteria criteria : possibleMatches) {
        // Expensive set operation - must be computed every time
        if (!Collections.disjoint(animal.getHabitats(), criteria.invalidHabitats())) {
            continue;
        }
        // Multiple comparisons that could be cached but aren't
        if (animal.getLegCount() <= criteria.minimumLegs()) {
            continue;
        }
        if (animal.getColor() != criteria.color()) {
            continue;
        }
        matches.add(criteria);
    }
    return matches;
}
```

#### Filter Tree Approach

First, define individual filters for each criteria:

```java
public class HabitatFilter implements NodeFilter<Animal, AnimalCriteria> {
    @Override
    public void filter(Animal input, Set<AnimalCriteria> outputs) {
        // Expensive set operation - but results will be cached
        outputs.removeIf(criteria -> 
            !Collections.disjoint(input.getHabitats(), criteria.invalidHabitats()));
    }

    @Override
    public Object lookupKey(Animal input) {
        // Create a compound key for the set of habitats
        // Future inputs with the same habitat set will hit the cache
        return input.getHabitats().stream()
                   .sorted()
                   .collect(Collectors.joining("|"));
    }
}

public class LegCountFilter implements NodeFilter<Animal, AnimalCriteria> {
    @Override
    public void filter(Animal input, Set<AnimalCriteria> outputs) {
        // Range comparison example
        outputs.removeIf(criteria -> input.getLegCount() <= criteria.minimumLegs());
    }

    @Override
    public Object lookupKey(Animal input) {
        return input.getLegCount();
    }
}

public class ColorFilter implements NodeFilter<Animal, AnimalCriteria> {
    @Override
    public void filter(Animal input, Set<AnimalCriteria> outputs) {
        // Simple equality check - no caching needed for this example
        outputs.removeIf(criteria -> input.getColor() != criteria.color());
    }

    @Override
    public Object lookupKey(Animal input) {
        throw new UnsupportedOperationException("This filter doesn't use caching");
    }
}
```

Then construct the filter tree:

```java
// Define filters - order matters for performance
List<NodeConfig<Animal, AnimalCriteria>> filters = List.of(
    // Cache the expensive habitat set operations
    new NodeConfig<>(new HabitatFilter(), new MapNodeCache<>()),
    // Cache leg count comparisons
    new NodeConfig<>(new LegCountFilter(), new MapNodeCache<>()),
    // Simple color check doesn't need caching
    new NodeConfig<>(new ColorFilter(), null)
);

// Create tree with possible matches
Set<AnimalCriteria> possibleMatches = Set.of(
    new AnimalCriteria(Color.BLACK, 4, Set.of("desert", "savanna")),
    new AnimalCriteria(Color.BROWN, 2, Set.of("forest", "mountain")),
    new AnimalCriteria(Color.GREY, 6, Set.of("tundra", "mountain"))
);

CachingFilterTree<Animal, AnimalCriteria> tree = 
    new CachingFilterTree<>(filters, possibleMatches);

// First usage - full evaluation required
Animal animal1 = new Animal(Color.BLACK, 4, Set.of("desert", "savanna"));
Set<AnimalCriteria> matches1 = tree.matches(animal1);

// Second usage with same habitats - habitat check uses cache
Animal animal2 = new Animal(Color.GREY, 5, Set.of("desert", "savanna"));
Set<AnimalCriteria> matches2 = tree.matches(animal2);
```

### Benefits of this Approach

1. **Organized Logic**: Each filter represents a single, clear condition
2. **Efficient Caching of Expensive Operations**: 
   - Habitat set operations are cached by a compound habitat key
   - Future inputs with the same habitat combinations will use cached results
   - Leg count comparisons are cached by count value
   - Simple operations (like color checks) can be left uncached
3. **Compound Key Example**: Notice how `HabitatFilter` creates a compound key from multiple values, enabling single-lookup caching for complex set operations
4. **Performance Optimization**: 
   - Expensive operations (like habitat set comparisons) are cached for reuse
   - Simpler operations (like color matching) can be left uncached to save memory
   - Each node's result is cached independently, so future matches only need to evaluate new combinations
   - Particularly beneficial when the same input patterns are seen frequently

## Cache Configuration

The library supports flexible caching strategies:

- Use the provided `MapNodeCache` for simple in-memory caching
- Implement `NodeCache` interface for custom caching solutions
- Disable caching for specific nodes in the tree
- Mix cached and non-cached nodes (with restrictions)

## Performance Benefits

Consider how the traditional approach would handle a scenario with 1000 different animal criteria and frequent checks:

```java
// Traditional approach - must evaluate EVERYTHING, EVERY time
Set<AnimalCriteria> findMatchingCriteria(Animal animal, Set<AnimalCriteria> possibleMatches) {
    Set<AnimalCriteria> matches = new HashSet<>();
    
    for (AnimalCriteria criteria : possibleMatches) {
        // Expensive set operation - computed every single time
        if (!Collections.disjoint(animal.getHabitats(), criteria.invalidHabitats())) {
            continue;
        }
        // Range comparison - computed every time
        if (animal.getLegCount() <= criteria.minimumLegs()) {
            continue;
        }
        // Simple check but still done every time
        if (animal.getColor() != criteria.color()) {
            continue;
        }
        matches.add(criteria);
    }
    return matches;
}
```

The filter tree approach transforms this into a more efficient structure:

```java
// Define filters prioritizing caching for expensive operations
List<NodeConfig<Animal, AnimalCriteria>> filters = List.of(
    // Cache expensive habitat set operations first
    new NodeConfig<>(new HabitatFilter(), new MapNodeCache<>()),
    // Cache numeric comparisons
    new NodeConfig<>(new LegCountFilter(), new MapNodeCache<>()),
    // Simple equality check without caching
    new NodeConfig<>(new ColorFilter(), null)
);

CachingFilterTree<Animal, AnimalCriteria> tree = 
    new CachingFilterTree<>(filters, possibleMatches);

// First check - full evaluation
Animal animal1 = new Animal(Color.BLACK, 4, Set.of("desert", "savanna"));
Set<AnimalCriteria> matches1 = tree.matches(animal1);

// Second check with same habitats - habitat comparison uses cache!
Animal animal2 = new Animal(Color.GREY, 5, Set.of("desert", "savanna"));
Set<AnimalCriteria> matches2 = tree.matches(animal2);
```

Key advantages:
- Expensive habitat set operations are computed once per unique habitat combination
- Leg count comparisons are cached per unique count
- Simple color checks remain uncached to save memory
- Each cached node result can be reused independently

## Requirements

- Java 21 or higher
- No additional dependencies for core functionality

## License

By Request

## Contributing

Lmk

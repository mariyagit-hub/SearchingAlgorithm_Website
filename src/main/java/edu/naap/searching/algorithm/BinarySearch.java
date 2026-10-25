package edu.naap.searching.algorithm;

import edu.naap.searching.model.SearchStep;
import edu.naap.searching.model.SourceLine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class BinarySearch implements SearchAlgorithm {
    private static final List<SourceLine> SOURCE = List.of(
            new SourceLine("init-low", "int low = 0;"),
            new SourceLine("init-high", "int high = array.length - 1;"),
            new SourceLine("loop", "while (low <= high) {"),
            new SourceLine("midpoint", "    int mid = low + (high - low) / 2;"),
            new SourceLine("inspect", "    int value = array[mid];"),
            new SourceLine("compare", "    int comparison = Integer.compare(value, target);"),
            new SourceLine("found-check", "    if (comparison == 0) {"),
            new SourceLine("found", "        return mid;"),
            new SourceLine("close-found", "    }"),
            new SourceLine("lower-check", "    if (comparison < 0) {"),
            new SourceLine("move-low", "        low = mid + 1;"),
            new SourceLine("else", "    } else {"),
            new SourceLine("move-high", "        high = mid - 1;"),
            new SourceLine("close-order", "    }"),
            new SourceLine("close-loop", "}"),
            new SourceLine("not-found", "return -1;"));

    @Override
    public String name() {
        return "Binary Search";
    }

    @Override
    public List<SourceLine> sourceLines() {
        return SOURCE;
    }

    @Override
    public List<SearchStep> trace(int[] input, int target) {
        int[] array = input.clone();
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                throw new IllegalArgumentException("Binary Search requires a sorted array.");
            }
        }

        List<SearchStep> steps = new ArrayList<>();
        Set<Integer> examined = new LinkedHashSet<>();
        int comparisons = 0;
        int resultIndex = -1;
        int low = 0;
        int high = array.length - 1;

        add(steps, SearchStep.EventType.INITIALIZE, -1, null, target,
                low, -1, high, comparisons, resultIndex, "init-low",
                "Initialize low to the first array index: 0.", examined);
        add(steps, SearchStep.EventType.INITIALIZE, -1, null, target,
                low, -1, high, comparisons, resultIndex, "init-high",
                "Initialize high to the last array index: " + high + ".", examined);

        while (true) {
            boolean intervalExists = low <= high;
            add(steps, SearchStep.EventType.LOOP_CHECK, -1, null, target,
                    low, -1, high, comparisons, resultIndex, "loop",
                    intervalExists ? "Check that low (" + low + ") is not greater than high (" + high + ")."
                            : "The search interval is empty; stop searching.",
                    examined);
            if (!intervalExists) {
                break;
            }

            int mid = low + (high - low) / 2;
            add(steps, SearchStep.EventType.MIDPOINT, mid, array[mid], target,
                    low, mid, high, comparisons, resultIndex, "midpoint",
                    "Calculate the middle index: " + mid + ".", examined);

            int value = array[mid];
            add(steps, SearchStep.EventType.INSPECT, mid, value, target,
                    low, mid, high, comparisons, resultIndex, "inspect",
                    "Read the middle value array[" + mid + "] = " + value + ".", examined);

            int comparison = Integer.compare(value, target);
            comparisons++;
            examined.add(mid);
            add(steps, SearchStep.EventType.COMPARE, mid, value, target,
                    low, mid, high, comparisons, resultIndex, "compare",
                    "Compare " + value + " with " + target + ": "
                            + (comparison == 0 ? "equal" : comparison < 0 ? "less than" : "greater than") + ".",
                    examined);

            add(steps, SearchStep.EventType.CONDITION, mid, value, target,
                    low, mid, high, comparisons, resultIndex, "found-check",
                    "Check whether the comparison equals 0.", examined);
            if (comparison == 0) {
                resultIndex = mid;
                add(steps, SearchStep.EventType.FOUND, mid, value, target,
                        low, mid, high, comparisons, resultIndex, "found",
                        "The comparison equals 0, so the target is found at index " + mid + ".",
                        examined);
                break;
            }
            add(steps, SearchStep.EventType.CONDITION, mid, value, target,
                    low, mid, high, comparisons, resultIndex,
                    comparison < 0 ? "lower-check" : "else",
                    comparison < 0
                            ? "Check whether the middle value is less than the target."
                            : "The middle value is greater than the target, so take the else branch.",
                    examined);
            if (comparison < 0) {
                low = mid + 1;
                add(steps, SearchStep.EventType.MOVE_LOW, mid, value, target,
                        low, mid, high, comparisons, resultIndex, "move-low",
                        value + " is less than " + target + "; discard the left half and set low to " + low + ".",
                        examined);
            } else {
                high = mid - 1;
                add(steps, SearchStep.EventType.MOVE_HIGH, mid, value, target,
                        low, mid, high, comparisons, resultIndex, "move-high",
                        value + " is greater than " + target + "; discard the right half and set high to " + high + ".",
                        examined);
            }
        }

        if (resultIndex < 0) {
            add(steps, SearchStep.EventType.NOT_FOUND, -1, null, target,
                    low, -1, high, comparisons, -1, "not-found",
                    "No positions remain in the search interval. Target " + target + " was not found.",
                    examined);
        }
        return List.copyOf(steps);
    }

    private static void add(
            List<SearchStep> steps,
            SearchStep.EventType eventType,
            int currentIndex,
            Integer currentValue,
            int target,
            int low,
            int mid,
            int high,
            int comparisons,
            int resultIndex,
            String sourceLineId,
            String explanation,
            Set<Integer> examined) {
        steps.add(new SearchStep(steps.size() + 1, eventType, currentIndex,
                currentValue, target, low, mid, high, comparisons, resultIndex,
                sourceLineId, explanation, Set.copyOf(examined)));
    }
}

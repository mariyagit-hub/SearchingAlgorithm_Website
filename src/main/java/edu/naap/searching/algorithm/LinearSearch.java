package edu.naap.searching.algorithm;

import edu.naap.searching.model.SearchStep;
import edu.naap.searching.model.SourceLine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LinearSearch implements SearchAlgorithm {
    private static final List<SourceLine> SOURCE = List.of(
            new SourceLine("init", "int i = 0;"),
            new SourceLine("loop", "while (i < array.length) {"),
            new SourceLine("inspect", "    int current = array[i];"),
            new SourceLine("compare", "    if (current == target) {"),
            new SourceLine("found", "        return i;"),
            new SourceLine("close-if", "    }"),
            new SourceLine("advance", "    i++;"),
            new SourceLine("close-loop", "}"),
            new SourceLine("not-found", "return -1;"));

    @Override
    public String name() {
        return "Linear Search";
    }

    @Override
    public List<SourceLine> sourceLines() {
        return SOURCE;
    }

    @Override
    public List<SearchStep> trace(int[] input, int target) {
        int[] array = input.clone();
        List<SearchStep> steps = new ArrayList<>();
        Set<Integer> examined = new LinkedHashSet<>();
        int comparisons = 0;
        int resultIndex = -1;
        int i = 0;

        add(steps, SearchStep.EventType.INITIALIZE, -1, null, target,
                -1, -1, -1, comparisons, resultIndex, "init",
                "Initialize the scan at index 0.", examined);

        while (true) {
            boolean inBounds = i < array.length;
            add(steps, SearchStep.EventType.LOOP_CHECK, inBounds ? i : -1,
                    inBounds ? array[i] : null, target, -1, -1, -1,
                    comparisons, resultIndex, "loop",
                    inBounds ? "Check whether index " + i + " is still inside the array."
                            : "Index " + i + " is outside the array, so the scan ends.",
                    examined);
            if (!inBounds) {
                break;
            }

            int current = array[i];
            add(steps, SearchStep.EventType.INSPECT, i, current, target,
                    -1, -1, -1, comparisons, resultIndex, "inspect",
                    "Read array[" + i + "] = " + current + ".", examined);

            comparisons++;
            examined.add(i);
            add(steps, SearchStep.EventType.COMPARE, i, current, target,
                    -1, -1, -1, comparisons, resultIndex, "compare",
                    "Compare " + current + " with target " + target + ".", examined);
            if (current == target) {
                resultIndex = i;
                add(steps, SearchStep.EventType.FOUND, i, current, target,
                        -1, -1, -1, comparisons, resultIndex, "found",
                        "Target found at index " + i + ".", examined);
                break;
            }

            add(steps, SearchStep.EventType.ADVANCE, i, current, target,
                    -1, -1, -1, comparisons, resultIndex, "advance",
                    current + " is not equal to " + target + "; move to the next index.",
                    examined);
            i++;
        }

        if (resultIndex < 0) {
            add(steps, SearchStep.EventType.NOT_FOUND, -1, null, target,
                    -1, -1, -1, comparisons, -1, "not-found",
                    "The array is exhausted. Target " + target + " was not found.",
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

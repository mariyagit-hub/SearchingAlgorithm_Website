package edu.naap.searching;

import edu.naap.searching.algorithm.BinarySearch;
import edu.naap.searching.algorithm.LinearSearch;
import edu.naap.searching.algorithm.SearchAlgorithm;
import edu.naap.searching.model.SearchStep;
import edu.naap.searching.ui.InputValidator;
import edu.naap.searching.ui.SearchController;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class AlgorithmTests {
    private static int passed;

    private AlgorithmTests() {
    }

    public static void main(String[] args) throws Exception {
        LinearSearch linear = new LinearSearch();
        BinarySearch binary = new BinarySearch();

        checkResult("linear first index", linear, new int[]{10, 20, 30}, 10, 0, 1);
        checkResult("linear middle index", linear, new int[]{10, 20, 30, 40, 50}, 30, 2, 3);
        checkResult("linear last index", linear, new int[]{10, 20, 30}, 30, 2, 3);
        checkResult("linear missing target", linear, new int[]{10, 20, 30}, 99, -1, 3);
        checkResult("linear empty array", linear, new int[]{}, 8, -1, 0);
        checkResult("linear singleton match", linear, new int[]{-4}, -4, 0, 1);
        checkResult("linear singleton miss", linear, new int[]{-4}, 4, -1, 1);
        checkResult("linear duplicates", linear, new int[]{5, 5, 5}, 5, 0, 1);
        checkResult("linear negative values", linear, new int[]{-9, -2, 0}, -2, 1, 2);

        checkResult("binary first index", binary, new int[]{10, 20, 30, 40, 50}, 10, 0, 2);
        checkResult("binary middle index", binary, new int[]{10, 20, 30, 40, 50, 60, 70}, 40, 3, 1);
        checkResult("binary last index", binary, new int[]{10, 20, 30, 40, 50}, 50, 4, 3);
        checkResult("binary missing target", binary, new int[]{10, 20, 30, 40, 50}, 35, -1, 2);
        checkResult("binary empty array", binary, new int[]{}, 8, -1, 0);
        checkResult("binary singleton match", binary, new int[]{-4}, -4, 0, 1);
        checkResult("binary singleton miss", binary, new int[]{-4}, 4, -1, 1);
        checkResult("binary duplicates", binary, new int[]{5, 5, 5, 5}, 5, 1, 1);
        checkResult("binary negative values", binary, new int[]{-9, -2, 0, 3}, -2, 1, 1);

        expectInvalid("binary rejects unsorted input", () -> binary.trace(new int[]{2, 1}, 1));
        int[] sorted = InputValidator.parseArray("3, -4, 2");
        Arrays.sort(sorted);
        checkResult("sort before binary search", binary, sorted, 2, 1, 1);
        expectInvalid("reject invalid array token", () -> InputValidator.parseArray("1, two, 3"));
        expectInvalid("reject invalid target", () -> InputValidator.parseTarget("3.5"));
        expectInvalid("reject empty array token", () -> InputValidator.parseArray("1,,2"));
        check("accept empty array input", InputValidator.parseArray("   ").length == 0);

        List<SearchStep> linearTrace = linear.trace(new int[]{4, 8}, 8);
        check("trace source mapping", linearTrace.stream().allMatch(step ->
                linear.sourceLines().stream().anyMatch(line -> line.id().equals(step.sourceLineId()))));
        List<SearchStep> binaryTrace = binary.trace(new int[]{2, 5, 8}, 5);
        check("binary trace source mapping", binaryTrace.stream().allMatch(step ->
                binary.sourceLines().stream().anyMatch(line -> line.id().equals(step.sourceLineId()))));
        List<SearchStep> firstRun = linear.trace(new int[]{4, 8}, 4);
        List<SearchStep> secondRun = linear.trace(new int[]{4, 8}, 8);
        check("independent repeated search",
                firstRun.get(firstRun.size() - 1).resultIndex() == 0
                        && secondRun.get(secondRun.size() - 1).resultIndex() == 1);

        testControllerSteppingPlaybackAndReset(linear);
        System.out.println("PASS: " + passed + " checks");
    }

    private static void checkResult(
            String name, SearchAlgorithm algorithm, int[] array, int target, int expectedIndex, int expectedComparisons) {
        List<SearchStep> trace = algorithm.trace(array, target);
        SearchStep last = trace.get(trace.size() - 1);
        check(name + " result", last.resultIndex() == expectedIndex);
        check(name + " comparisons", last.comparisons() == expectedComparisons);
    }

    private static void testControllerSteppingPlaybackAndReset(LinearSearch algorithm) throws Exception {
        AtomicInteger eventCount = new AtomicInteger();
        AtomicReference<SearchController> controllerRef = new AtomicReference<>();
        CountDownLatch pausedAfterEvents = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(1);
        int[] values = new int[2];
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }

        SwingUtilities.invokeAndWait(() -> {
            SearchController controller = new SearchController(step -> {
                int count = eventCount.incrementAndGet();
                if (count == 3) {
                    controllerRef.get().pause();
                    pausedAfterEvents.countDown();
                }
            }, completed::countDown);
            controllerRef.set(controller);
            controller.prepare(algorithm, values, -1);
            controller.step();
        });
        check("step advances exactly one event", eventCount.get() == 1);

        SwingUtilities.invokeAndWait(() -> controllerRef.get().play());
        check("play pauses at requested point", pausedAfterEvents.await(5, TimeUnit.SECONDS));
        check("pause preserves progress", eventCount.get() == 3 && !controllerRef.get().isComplete());
        SwingUtilities.invokeAndWait(() -> controllerRef.get().play());
        check("resume completes", completed.await(15, TimeUnit.SECONDS));
        check("playback emits every trace event once",
                eventCount.get() == controllerRef.get().getTotalSteps() && controllerRef.get().isComplete());

        SwingUtilities.invokeAndWait(() -> {
            controllerRef.get().reset();
            controllerRef.get().prepare(algorithm, values, -1);
            controllerRef.get().play();
            controllerRef.get().reset();
        });
        int resetCount = eventCount.get();
        Thread.sleep(750);
        check("reset stops active playback", eventCount.get() == resetCount && !controllerRef.get().isPrepared());
    }

    private static void expectInvalid(String name, Runnable action) {
        boolean rejected = false;
        try {
            action.run();
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }
        check(name, rejected);
    }

    private static void check(String name, boolean passedCheck) {
        if (!passedCheck) {
            throw new AssertionError("FAIL: " + name);
        }
        passed++;
        System.out.println("PASS: " + name);
    }
}

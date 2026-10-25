package edu.naap.searching.ui;

import edu.naap.searching.algorithm.SearchAlgorithm;
import edu.naap.searching.model.SearchStep;

import javax.swing.Timer;
import java.util.List;
import java.util.function.Consumer;

public final class SearchController {
    private final Timer timer;
    private final Consumer<SearchStep> stepListener;
    private final Runnable completionListener;
    private List<SearchStep> trace = List.of();
    private int cursor;
    private boolean prepared;

    public SearchController(
            Consumer<SearchStep> stepListener,
            Runnable completionListener) {
        this.stepListener = stepListener;
        this.completionListener = completionListener;
        timer = new Timer(650, event -> advance());
    }

    public void prepare(SearchAlgorithm algorithm, int[] array, int target) {
        pause();
        trace = algorithm.trace(array, target);
        cursor = 0;
        prepared = true;
    }

    public void step() {
        if (!prepared) {
            throw new IllegalStateException("Prepare a search before stepping.");
        }
        advance();
    }

    public void play() {
        if (!prepared) {
            throw new IllegalStateException("Prepare a search before playing.");
        }
        if (cursor < trace.size()) {
            timer.start();
        }
    }

    public void pause() {
        timer.stop();
    }

    public void reset() {
        pause();
        trace = List.of();
        cursor = 0;
        prepared = false;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public boolean isComplete() {
        return prepared && cursor >= trace.size();
    }

    public int getTotalSteps() {
        return trace.size();
    }

    public int getResultIndex() {
        return trace.isEmpty() ? -1 : trace.get(trace.size() - 1).resultIndex();
    }

    public int getComparisonCount() {
        return trace.isEmpty() ? 0 : trace.get(trace.size() - 1).comparisons();
    }

    private void advance() {
        if (cursor >= trace.size()) {
            pause();
            return;
        }
        SearchStep step = trace.get(cursor++);
        stepListener.accept(step);
        if (cursor >= trace.size()) {
            pause();
            completionListener.run();
        }
    }
}

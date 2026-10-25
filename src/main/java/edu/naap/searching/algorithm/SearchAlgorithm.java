package edu.naap.searching.algorithm;

import edu.naap.searching.model.SearchStep;
import edu.naap.searching.model.SourceLine;

import java.util.List;

public interface SearchAlgorithm {
    String name();

    List<SourceLine> sourceLines();

    List<SearchStep> trace(int[] array, int target);
}

package edu.naap.searching.model;

import java.util.Set;

public record SearchStep(
        int number,
        EventType eventType,
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
        Set<Integer> examinedIndexes) {

    public enum EventType {
        INITIALIZE,
        LOOP_CHECK,
        MIDPOINT,
        INSPECT,
        COMPARE,
        CONDITION,
        MOVE_LOW,
        MOVE_HIGH,
        ADVANCE,
        FOUND,
        NOT_FOUND
    }
}

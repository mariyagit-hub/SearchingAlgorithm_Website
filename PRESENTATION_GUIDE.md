# Presentation Guide

## Suggested demonstration

### Linear Search

1. Select **Linear Search** and load the sample `[10, 20, 30, 40, 50, 60, 70]`.
2. Set target to `30`, press **Start**, and briefly explain that the trace is prepared before it runs.
3. Press **Step** repeatedly. Point out the current array value, the equality statement highlighted in Live Java, the comparison count, and the events where the index advances.
4. Continue until the result shows index 2 (position 3), with three comparisons.
5. Mention that Linear Search works without sorted data and has an O(n) worst case.

### Binary Search

1. Select **Binary Search** and use the sorted sample with target `50`.
2. Press **Start**, then **Play**. Pause during playback and resume to demonstrate preserved progress.
3. Explain the low/mid/high labels. The search compares index 3, discards the left portion, then finds the target at index 4.
4. Show the final result: index 4 (position 5), three comparisons.
5. Enter unsorted values and demonstrate that the application refuses to start. Use **Sort for Binary Search**, inspect the sorted array and its indexes, and then retry.
6. Explain why the sorted precondition allows the algorithm to discard half the remaining range, giving O(log n) worst-case time.

## Questions an instructor may ask

**Q: Why does Binary Search need sorted data?**  
A: The comparison with the midpoint tells us which half can be discarded only when the values are in order.

**Q: Why can Linear Search handle unsorted data?**  
A: It checks each element directly and makes no assumptions about the order.

**Q: What does one comparison mean in this project?**  
A: One comparison between an inspected array element and the target. Interval checks and assignments are trace steps, but are not counted as element-to-target comparisons.

**Q: Why is Binary Search O(log n)?**  
A: Each comparison can remove about half of the remaining search interval, so the number of checks grows logarithmically with the array size.

**Q: What happens when the array contains duplicates?**  
A: Ordinary Binary Search may return any matching index. This project does not promise the first or last duplicate.

**Q: Does sorting change the indexes?**  
A: Yes, it changes the order. The application displays the sorted values before execution; reported indexes refer to that displayed sorted order.

**Q: Is this what a database does for every search?**  
A: No. The array algorithms demonstrate basic ideas. Real databases may use indexes, trees, hashes, query planners, and other storage- and query-specific techniques.

**Q: How does the code highlight stay synchronized?**  
A: The Java algorithm produces an event with a stable source-line ID. The controller sends that same event to both the array renderer and the source panel.

**Q: Why use Java Swing instead of a browser-only page?**  
A: Swing runs the required Java algorithm locally, without a backend or a browser-side Java substitute.

**Q: What is the space complexity?**  
A: Both searches are iterative and use O(1) additional algorithm space. The educational application separately retains a trace for visualization.

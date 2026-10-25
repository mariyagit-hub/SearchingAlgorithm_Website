# Project Report

## Purpose and implementation

This desktop application teaches two searching algorithms through an interactive, event-based trace. The actual search logic is implemented in Java and produces the same events consumed by the visualization and source panel. It runs locally with Java Swing and requires no backend or network.

The event model records the step number, event kind, current index and value, target, `low`/`mid`/`high`, comparison count, result index, stable Java source-line identifier, explanation, and examined indexes. `SearchController` delivers one event at a time on Swing's event-dispatch thread; a Swing Timer supports playback without blocking the interface.

## Architecture and major class methods

- `Main.main`: schedules the UI creation on Swing's event-dispatch thread.
- `MainFrame`: constructs the two-panel application, validates inputs, prepares traces, connects controls, logs events, and displays final results. Its `prepareRun`, `renderStep`, `showFinalResult`, and `resetExecution` methods coordinate the UI.
- `SearchAlgorithm`: common contract for `name`, `sourceLines`, and `trace`.
- `LinearSearch.trace`: scans from index zero, records a comparison for each inspected element, and stops on a match or at the end.
- `BinarySearch.trace`: uses an inclusive low/high interval and `Integer.compare`; it checks sorted input before searching and updates one interval boundary per non-match.
- `SearchStep`: immutable data passed to both display panels.
- `SearchController`: `prepare`, `step`, `play`, `pause`, and `reset` manage trace lifecycle and timed delivery.
- `VisualizationPanel.showArray`: renders the current array state and range labels.
- `CodePanel.showSource`: syntax-colors source lines, marks the active mapped line, and scrolls it into view.
- `InputValidator`: parses values/target and reports invalid input; `isSorted` enforces the Binary Search precondition.
- `AlgorithmTests`: dependency-free test runner for algorithm and controller behavior.

The code panel shows the algorithm's core Java statements rather than an independently scripted animation. Trace-generation code associates an event with a stable source-line ID where the corresponding operation occurs.

## Concepts, types, and operations

The project uses integer arrays for values and indexes, a common algorithm interface, immutable record types for source and event data, lists for ordered trace events, and sets for examined indexes. Linear Search accepts unsorted arrays. Binary Search uses an inclusive interval and requires ascending order. Duplicate matches may return any matching index; this implementation follows the ordinary midpoint-based form, not a first-match variant.

| Algorithm | Best | Average | Worst | Extra space |
|---|---:|---:|---:|---:|
| Linear Search | O(1) | O(n) | O(n) | O(1) |
| Iterative Binary Search | O(1) | O(log n) | O(log n) | O(1) |

## Practical relevance

Searching is used conceptually in directories, inventory systems, information systems, and database applications to find records or values. These examples illustrate the need to locate data; they do not claim that production databases use these exact array algorithms. Databases may use indexes, trees, hashing, query planning, or storage-specific methods.

## Evaluation rubric alignment

| Criterion | Weight | Implemented evidence |
|---|---:|---|
| Technical Accuracy and Implementation | 25% | Java implementations, correct boundary behavior, sorted-input enforcement, comparison accounting, and trace events. |
| Visualization and Interactivity | 20% | Array/index display, event log, per-event stepping, timed playback, pause/resume, reset, and line synchronization. |
| Concepts, Types, and Operations | 15% | Integer array operations, interval state, comparisons, trace data types, and complexity notes. |
| User Interface and Usability | 10% | Side-by-side panels, dark navy/blue academic theme, input messages, legends, and independently scrollable panes. |
| Testing and Reliability | 10% | Offline test runner covers algorithm edge cases, validation, mapping, and playback controller behavior. |
| Practical Application and Relevance | 10% | Learning panel explains contextual use while distinguishing conceptual examples from database internals. |
| Technical Presentation and Defense | 10% | Readable live code, explanations, project guide, test report, demo sequence, and instructor Q&A. |
| **Total** | **100%** | |

## Limitations and deliberate behavior

Arrays are capped at 100 values so the visual display remains understandable. Automatic sorting is explicit and updates the values shown to the user; the application does not retain original-index mapping after sorting because it searches the displayed sorted array itself.

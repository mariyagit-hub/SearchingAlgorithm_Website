# Interactive Searching Algorithms

A local, offline Java Swing learning application for demonstrating Linear Search and Binary Search. The algorithm implementations generate immutable execution-trace events; the visualization and source viewer consume those same events, keeping the array state and active Java statement synchronized.

**Project:** Interactive Searching Algorithms Visualization Application  
**Institution:** National Aviation Academy of the Philippines (NAAP), Institute of Computer Studies  
**Subject:** Data Structures and Algorithms

## Requirements

- JDK 17 or later
- Windows for the included `.bat` launchers
- No network access, third-party libraries, or paid services

## Run on Windows

1. Install a JDK and ensure `java` and `javac` are available in `PATH`.
2. Open this project folder in File Explorer or a terminal.
3. Double-click `run.bat`, or run:

   ```powershell
   .\run.bat
   ```

The script compiles the Java source into `build\classes` and opens the Swing application. To compile and launch manually from PowerShell:

```powershell
New-Item -ItemType Directory -Force build\classes | Out-Null
$sources = Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d build\classes $sources
java -cp build\classes edu.naap.searching.Main
```

## Run the tests

```powershell
.\run-tests.bat
```

The test runner compiles the application and standalone, dependency-free tests, then checks algorithm results, comparison counts, validation, trace-line mappings, and controller stepping/playback/reset behavior. See [TESTING_REPORT.md](./TESTING_REPORT.md) for the test cases and recorded outcome.

## Quick start

1. Choose Linear Search or Binary Search.
2. Enter comma-separated integers and an integer target. The provided sample is sorted.
3. For Binary Search, an unsorted input is rejected with a clear prompt. Choose **Sort for Binary Search** and inspect the new order/indexes before running.
4. Choose **Start** to prepare the trace, then use **Step** to advance one event or **Play/Pause** for timed playback.
5. **Reset** clears the current run and restores the displayed array to its unexamined state. Changing either input also resets progress.
6. Read the right-side Live Java tab to see the active source statement. The Learn tab contains algorithm explanations, complexities, and practical context.

One comparison is defined as one element-to-target comparison. Trace steps also include array reads, interval checks, and updates, so step count is not comparison count.

## Architecture

| Class | Responsibility |
|---|---|
| `Main` | Starts Swing on the event-dispatch thread. |
| `MainFrame` | Builds the application layout, controls, input workflow, result display, and learning notes. |
| `SearchAlgorithm` | Common interface for algorithms, source-line metadata, and trace creation. |
| `LinearSearch` | Implements the sequential search and emits events as its Java logic runs. |
| `BinarySearch` | Implements iterative binary search and rejects unsorted input. |
| `SearchStep` | Immutable snapshot of one trace event, including indexes, comparison count, source-line ID, explanation, and examined positions. |
| `SourceLine` | Stable source-line identifier and display text. |
| `SearchController` | Drives one-event stepping and Swing Timer playback, pause, resume, and reset. |
| `VisualizationPanel` | Renders values, indexes, examined/found states, and Binary Search boundaries. |
| `CodePanel` | Renders syntax-colored Java statements, line numbers, active-line highlighting, and operation explanation. |
| `InputValidator` | Parses integer arrays and targets and checks sort order. |
| `AlgorithmTests` | Runs offline checks without a test-framework dependency. |

The displayed source lines are the core Java statements represented by stable IDs. Algorithm methods emit the corresponding ID directly at the point where that operation occurs; no JavaScript algorithm or pre-scripted animation is involved.

## Project documents

- [User guide](./USER_GUIDE.md)
- [Project report, rubric mapping, and class/method notes](./PROJECT_REPORT.md)
- [Testing report](./TESTING_REPORT.md)
- [Presentation guide and instructor Q&A](./PRESENTATION_GUIDE.md)

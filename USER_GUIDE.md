# User Guide

## Prepare data

- Select an algorithm from the Algorithm list.
- Enter up to 100 integer values separated by commas. Negative numbers are supported. A blank array is valid and useful for demonstrating the empty case.
- Enter an integer target.
- **Sample data** restores `10, 20, 30, 40, 50, 60, 70`.
- **Generate random** creates 12 values, including possible negatives.
- **Sort for Binary Search** explicitly sorts the entered values and updates the input field. Indexes always refer to the displayed order. Sorting also resets the current run.

Binary Search cannot start on an unsorted array. The application explains why and directs the user to the sort button; it never silently changes the user's data.

## Run a trace

- **Start:** validate the inputs and prepare a fresh Java-generated trace without advancing it.
- **Step:** show exactly one trace event. Clicking Step before Start prepares a trace and advances its first event.
- **Play:** advance one event every 650 ms.
- **Pause:** stop automatic playback without discarding progress. Use Play to resume.
- **Reset:** stop playback and clear progress. Editing the array or target also resets a run.

The left side shows the current index and value, target, comparison count, trace event, array highlights, and execution log. `L`, `M`, and `H` identify Binary Search's current low, mid, and high indexes. Colors are described in the legend. At completion, the result includes the zero-based index, one-based position, comparisons, and number of trace events.

## Read the live code

The Live Java tab displays the selected algorithm's core Java statements. The marker and contrasting row background identify the statement attached to the latest trace event. An explanation below the code describes that operation. The Learn tab explains the algorithms and time/space complexity.

## Input errors

The application accepts 32-bit integer values only, reports malformed or empty comma-separated entries, requires a target, and limits arrays to 100 values for readability. An empty array itself is allowed; enter no values and provide a valid target.

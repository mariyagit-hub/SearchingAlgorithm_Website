# Testing Report

## Automated verification

**Command:** `.\run-tests.bat`  
**Toolchain:** OpenJDK 17  
**Test style:** Dependency-free Java assertions and headless Swing Timer/controller checks.

The runner checks matches at first/middle/last positions, misses, empty/singleton arrays, duplicates, negative values, accurate element-to-target comparison counts, unsorted Binary Search rejection, sorting before Binary Search, invalid array/target inputs, blank-array acceptance, trace-to-source mapping, repeated searches, one-event Step, playback pause/resume, and reset during playback.

| Scenario | Expected result | Actual result |
|---|---|---|
| Linear Search finds the middle item in `[10,20,30,40,50]` for target `30` | Index 2; 3 comparisons | PASS |
| Binary Search finds `50` in `[10,20,30,40,50,60,70]` | Index 4; 3 comparisons | PASS |
| Missing target in either algorithm | Not found; comparison count matches inspected values | PASS |
| Empty and one-item arrays | Safe result; no crash | PASS |
| Duplicate and negative values | Correct match and index | PASS |
| Unsorted input to Binary Search | Reject with precondition message | PASS |
| Explicit sort, then Binary Search | Sorted order searched with matching displayed index | PASS |
| Malformed array/target | Rejected with a useful validation message | PASS |
| Step, playback pause/resume, and reset | One event per Step; resume continues; reset stops playback | PASS |
| Source-line IDs | Every emitted event maps to a displayed Java line | PASS |

**Actual result:** 52 checks passed; the test runner exited successfully. The runner prints individual PASS checks and a total count; it fails with a non-zero exit code on the first failed assertion.

## Manual UI checks

The `run.bat` launcher compiles and opens the desktop UI. Use [USER_GUIDE.md](./USER_GUIDE.md) for the recommended checks: sample-data execution with Step and Play, pause/resume, result display, invalid input, and the unsorted Binary Search warning followed by explicit sorting. GUI-specific appearance and window resizing are not covered by headless automated tests.

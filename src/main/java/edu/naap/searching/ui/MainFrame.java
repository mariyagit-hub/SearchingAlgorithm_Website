package edu.naap.searching.ui;

import edu.naap.searching.algorithm.BinarySearch;
import edu.naap.searching.algorithm.LinearSearch;
import edu.naap.searching.algorithm.SearchAlgorithm;
import edu.naap.searching.model.SearchStep;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Random;

public final class MainFrame extends JFrame {
    private static final SearchAlgorithm[] ALGORITHMS = {new LinearSearch(), new BinarySearch()};
    private final JComboBox<SearchAlgorithm> algorithmChoice = new JComboBox<>(ALGORITHMS);
    private final JTextField arrayInput = new JTextField("10, 20, 30, 40, 50, 60, 70");
    private final JTextField targetInput = new JTextField("50", 8);
    private final VisualizationPanel visualization = new VisualizationPanel();
    private final CodePanel codePanel = new CodePanel();
    private final JTextArea executionLog = new JTextArea();
    private final JLabel stepValue = new JLabel("—");
    private final JLabel indexValue = new JLabel("—");
    private final JLabel elementValue = new JLabel("—");
    private final JLabel targetValue = new JLabel("—");
    private final JLabel currentComparisonValue = new JLabel("—");
    private final JLabel comparisonValue = new JLabel("0");
    private final JLabel statusValue = new JLabel("Ready");
    private final JLabel resultValue = new JLabel("Not started");
    private final SearchController controller;
    private int[] activeArray = new int[0];

    public MainFrame() {
        super("Interactive Searching Algorithms | NAAP");
        controller = new SearchController(this::renderStep, this::showFinalResult);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);
        setArrayDisplay(activeArray, null);
        showSelectedSource();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 3));
        header.setBackground(new Color(17, 37, 67));
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 15, 22));
        JLabel title = new JLabel("Interactive Searching Algorithms");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 23f));
        JLabel subtitle = new JLabel("NAAP · Institute of Computer Studies  |  Data Structures and Algorithms");
        subtitle.setForeground(new Color(201, 216, 236));
        subtitle.setFont(subtitle.getFont().deriveFont(12f));
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 3));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);
        JLabel badge = new JLabel("JAVA 17  ·  SWING", SwingConstants.CENTER);
        badge.setForeground(new Color(220, 235, 255));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(94, 129, 174)),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    private JSplitPane createWorkspace() {
        JPanel simulation = createSimulationPanel();
        JTabbedPaneWithLearning rightSide = new JTabbedPaneWithLearning(codePanel);
        JScrollPane simulationScroll = new JScrollPane(simulation);
        simulationScroll.setBorder(BorderFactory.createEmptyBorder());
        simulationScroll.getVerticalScrollBar().setUnitIncrement(16);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simulationScroll, rightSide);
        split.setResizeWeight(0.53);
        split.setDividerLocation(770);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }

    private JPanel createSimulationPanel() {
        JPanel panel = new JPanel(new BorderLayout(9, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 14));
        JPanel inputs = new JPanel(new GridLayout(0, 1, 6, 6));
        inputs.setBackground(Color.WHITE);
        inputs.setBorder(BorderFactory.createTitledBorder("Search setup"));
        algorithmChoice.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.name());
            label.setOpaque(true);
            label.setBackground(selected ? new Color(221, 234, 251) : Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
            return label;
        });
        inputs.add(formRow("Algorithm", algorithmChoice));
        inputs.add(formRow("Array values", arrayInput));
        inputs.add(formRow("Target", targetInput));

        JPanel dataButtons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        dataButtons.setBackground(Color.WHITE);
        JButton sampleButton = new JButton("Sample data");
        JButton randomButton = new JButton("Generate random");
        JButton sortButton = new JButton("Sort for Binary Search");
        dataButtons.add(sampleButton);
        dataButtons.add(randomButton);
        dataButtons.add(sortButton);
        inputs.add(dataButtons);

        JPanel controls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 7, 2));
        controls.setBackground(Color.WHITE);
        JButton startButton = new JButton("Start");
        JButton stepButton = new JButton("Step");
        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");
        startButton.setToolTipText("Prepare the trace. Use Step or Play to advance.");
        stepButton.setToolTipText("Advance exactly one trace event.");
        playButton.setToolTipText("Advance trace events automatically.");
        pauseButton.setToolTipText("Pause playback without losing progress.");
        resetButton.setToolTipText("Clear progress and return to the initial array.");
        controls.add(startButton);
        controls.add(stepButton);
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(resetButton);

        JPanel info = createInfoPanel();
        JPanel logPanel = new JPanel(new BorderLayout(3, 4));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder("Execution log"));
        executionLog.setEditable(false);
        executionLog.setLineWrap(true);
        executionLog.setWrapStyleWord(true);
        executionLog.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(executionLog);
        logScroll.setPreferredSize(new Dimension(460, 140));
        logPanel.add(logScroll, BorderLayout.CENTER);
        logPanel.add(resultValue, BorderLayout.SOUTH);
        resultValue.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        resultValue.setForeground(new Color(25, 91, 59));
        resultValue.setFont(resultValue.getFont().deriveFont(Font.BOLD, 12f));

        JPanel upper = new JPanel(new BorderLayout(8, 8));
        upper.setBackground(Color.WHITE);
        upper.add(inputs, BorderLayout.CENTER);
        upper.add(controls, BorderLayout.SOUTH);
        JPanel middle = new JPanel(new BorderLayout(8, 8));
        middle.setBackground(Color.WHITE);
        middle.add(visualization, BorderLayout.CENTER);
        middle.add(VisualizationPanel.createLegend(), BorderLayout.SOUTH);
        JPanel lower = new JPanel(new GridLayout(2, 1, 7, 7));
        lower.setBackground(Color.WHITE);
        lower.add(info);
        lower.add(logPanel);
        panel.add(upper, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        panel.add(lower, BorderLayout.SOUTH);

        sampleButton.addActionListener(event -> setArrayText("10, 20, 30, 40, 50, 60, 70"));
        randomButton.addActionListener(event -> generateRandomArray());
        sortButton.addActionListener(event -> sortForBinarySearch());
        startButton.addActionListener(event -> prepareRun());
        stepButton.addActionListener(event -> {
            if (ensurePrepared()) {
                controller.step();
            }
        });
        playButton.addActionListener(event -> {
            if (ensurePrepared()) {
                controller.play();
                statusValue.setText("Playing");
            }
        });
        pauseButton.addActionListener(event -> {
            controller.pause();
            statusValue.setText(controller.isComplete() ? "Complete" : "Paused");
        });
        resetButton.addActionListener(event -> resetExecution());
        algorithmChoice.addActionListener(event -> {
            resetExecution();
            showSelectedSource();
            if (getAlgorithm() instanceof BinarySearch) {
                try {
                    if (!InputValidator.isSorted(InputValidator.parseArray(arrayInput.getText()))) {
                        statusValue.setText("Sort the values before Binary Search.");
                    }
                } catch (IllegalArgumentException ignored) {
                    statusValue.setText("Correct the array input before searching.");
                }
            }
        });
        DocumentListener inputListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                resetExecution();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                resetExecution();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                resetExecution();
            }
        };
        arrayInput.getDocument().addDocumentListener(inputListener);
        targetInput.getDocument().addDocumentListener(inputListener);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3, 8, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Execution information"));
        addInfo(panel, "Trace step", stepValue);
        addInfo(panel, "Current index", indexValue);
        addInfo(panel, "Current element", elementValue);
        addInfo(panel, "Target", targetValue);
        addInfo(panel, "Current comparison", currentComparisonValue);
        addInfo(panel, "Comparisons", comparisonValue);
        addInfo(panel, "Status", statusValue);
        return panel;
    }

    private static void addInfo(JPanel panel, String label, JLabel value) {
        JPanel item = new JPanel(new BorderLayout(3, 2));
        item.setBackground(Color.WHITE);
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(103, 116, 134));
        caption.setFont(caption.getFont().deriveFont(10f));
        value.setForeground(new Color(29, 45, 67));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 12f));
        item.add(caption, BorderLayout.NORTH);
        item.add(value, BorderLayout.CENTER);
        panel.add(item);
    }

    private static JPanel formRow(String labelText, java.awt.Component field) {
        JPanel row = new JPanel(new BorderLayout(9, 2));
        row.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(110, 25));
        label.setForeground(new Color(46, 61, 80));
        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private boolean ensurePrepared() {
        if (!controller.isPrepared() || controller.isComplete()) {
            return prepareRun();
        }
        return true;
    }

    private boolean prepareRun() {
        try {
            int[] values = InputValidator.parseArray(arrayInput.getText());
            int target = InputValidator.parseTarget(targetInput.getText());
            SearchAlgorithm algorithm = getAlgorithm();
            if (algorithm instanceof BinarySearch && !InputValidator.isSorted(values)) {
                statusValue.setText("Binary Search requires sorted values.");
                JOptionPane.showMessageDialog(this,
                        "The array is not sorted. Use “Sort for Binary Search” first, then review the displayed order and indexes.",
                        "Array must be sorted", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            activeArray = values;
            controller.prepare(algorithm, values, target);
            executionLog.setText("");
            setArrayDisplay(values, null);
            stepValue.setText("0");
            indexValue.setText("—");
            elementValue.setText("—");
            targetValue.setText(Integer.toString(target));
            comparisonValue.setText("0");
            statusValue.setText("Ready");
            resultValue.setText("Trace prepared. Select Step or Play.");
            showSelectedSource();
            appendLog(algorithm.name() + " trace prepared for target " + target + ".");
            appendLog("Step advances one Java trace event; comparisons count element-to-target operations.");
            return true;
        } catch (IllegalArgumentException exception) {
            statusValue.setText("Input needs attention.");
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Invalid search input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void renderStep(SearchStep step) {
        setArrayDisplay(activeArray, step);
        stepValue.setText(step.number() + " / " + controller.getTotalSteps());
        indexValue.setText(step.currentIndex() < 0 ? "—" : Integer.toString(step.currentIndex()));
        elementValue.setText(step.currentValue() == null ? "—" : Integer.toString(step.currentValue()));
        targetValue.setText(Integer.toString(step.target()));
        comparisonValue.setText(Integer.toString(step.comparisons()));
        currentComparisonValue.setText(comparisonFor(step));
        statusValue.setText(step.eventType().name().replace('_', ' '));
        appendLog("Step " + step.number() + ": " + step.explanation());
        codePanel.showSource(getAlgorithm().sourceLines(), step.sourceLineId(), step.explanation());
    }

    private void showFinalResult() {
        int index = controller.getResultIndex();
        int comparisons = controller.getComparisonCount();
        statusValue.setText(index >= 0 ? "Found" : "Not found");
        String result = index >= 0
                ? "Target found at index " + index + " (position " + (index + 1) + ")."
                : "Target not found in the array.";
        resultValue.setText(result + "  " + comparisons + " comparison(s), "
                + controller.getTotalSteps() + " trace step(s).");
        appendLog(resultValue.getText());
    }

    private void resetExecution() {
        controller.reset();
        executionLog.setText("");
        stepValue.setText("—");
        indexValue.setText("—");
        elementValue.setText("—");
        comparisonValue.setText("0");
        currentComparisonValue.setText("—");
        targetValue.setText("—");
        statusValue.setText("Ready");
        resultValue.setText("Not started");
        try {
            activeArray = InputValidator.parseArray(arrayInput.getText());
        } catch (IllegalArgumentException exception) {
            activeArray = new int[0];
        }
        setArrayDisplay(activeArray, null);
        showSelectedSource();
    }

    private void setArrayText(String text) {
        arrayInput.setText(text);
        resetExecution();
    }

    private void generateRandomArray() {
        Random random = new Random();
        int[] values = random.ints(12, -30, 101).toArray();
        String text = Arrays.stream(values).mapToObj(Integer::toString)
                .collect(java.util.stream.Collectors.joining(", "));
        setArrayText(text);
    }

    private void sortForBinarySearch() {
        try {
            int[] values = InputValidator.parseArray(arrayInput.getText());
            Arrays.sort(values);
            arrayInput.setText(Arrays.stream(values).mapToObj(Integer::toString)
                    .collect(java.util.stream.Collectors.joining(", ")));
            resetExecution();
            statusValue.setText("Sorted array ready; indexes use this displayed order.");
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Cannot sort array", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setArrayDisplay(int[] values, SearchStep step) {
        visualization.showArray(values, step);
    }

    private SearchAlgorithm getAlgorithm() {
        SearchAlgorithm selected = (SearchAlgorithm) algorithmChoice.getSelectedItem();
        return selected == null ? ALGORITHMS[0] : selected;
    }

    private void showSelectedSource() {
        codePanel.showSource(getAlgorithm().sourceLines(), null,
                "This panel shows the core Java statements executed by the traced algorithm.");
    }

    private void appendLog(String message) {
        executionLog.append(message + "\n");
        executionLog.setCaretPosition(executionLog.getDocument().getLength());
    }

    private String comparisonFor(SearchStep step) {
        if (step.eventType() != SearchStep.EventType.COMPARE || step.currentValue() == null) {
            return "—";
        }
        int value = step.currentValue();
        int target = step.target();
        if (getAlgorithm() instanceof LinearSearch) {
            return value + " == " + target + " : " + (value == target ? "true" : "false");
        }
        String relation = value == target ? "==" : value < target ? "<" : ">";
        return value + " " + relation + " " + target;
    }

    private static final class JTabbedPaneWithLearning extends javax.swing.JTabbedPane {
        private JTabbedPaneWithLearning(CodePanel codePanel) {
            addTab("Live Java", codePanel);
            JTextArea learning = new JTextArea("""
                    WHAT IS A SEARCHING ALGORITHM?
                    A searching algorithm looks through data to locate a requested value.

                    LINEAR SEARCH
                    Check values in order, from the first index to the last. It works even when the values are unsorted. It is simple, but may inspect every item.
                    Best: O(1)   Average: O(n)   Worst: O(n)   Extra space: O(1)

                    BINARY SEARCH
                    Check the middle value, then discard the half that cannot contain the target. The array must be sorted first. Ordinary Binary Search may return any matching index when duplicates exist.
                    Best: O(1)   Average: O(log n)   Worst: O(log n)   Extra space: O(1)

                    COMPARISON
                    Linear Search makes steady progress and needs no ordering. Binary Search can eliminate about half the remaining range after each comparison, but requires sorted data.

                    PRACTICAL CONTEXT
                    Searching appears in directories, inventory tools, information systems, and databases. These are conceptual examples: real databases may use indexes, trees, hashes, query planners, or other techniques rather than these exact array algorithms.

                    COUNTING NOTE
                    One comparison means one element-to-target comparison. Trace steps also include assignments, interval checks, and array reads, so steps and comparisons are different totals.
                    """);
            learning.setEditable(false);
            learning.setLineWrap(true);
            learning.setWrapStyleWord(true);
            learning.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            learning.setForeground(new Color(40, 54, 72));
            learning.setBackground(new Color(250, 252, 255));
            learning.setBorder(BorderFactory.createEmptyBorder(14, 15, 14, 15));
            JScrollPane scroll = new JScrollPane(learning);
            addTab("Learn", scroll);
        }
    }
}

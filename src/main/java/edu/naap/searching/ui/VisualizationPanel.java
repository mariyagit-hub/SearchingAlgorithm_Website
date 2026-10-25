package edu.naap.searching.ui;

import edu.naap.searching.model.SearchStep;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Set;

public final class VisualizationPanel extends JPanel {
    private static final Color NAVY = new Color(18, 32, 56);
    private static final Color BLUE = new Color(46, 112, 211);
    private static final Color AMBER = new Color(255, 222, 143);
    private static final Color GREEN = new Color(164, 230, 192);
    private static final Color GRAY = new Color(232, 237, 244);
    private static final Color INK = new Color(28, 40, 58);

    private final JPanel cells = new JPanel(new GridLayout(0, 10, 9, 10));
    private final JLabel arrayMessage = new JLabel("Enter values, then start a search.", SwingConstants.CENTER);

    public VisualizationPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Array visualization"),
                BorderFactory.createEmptyBorder(9, 8, 8, 8)));
        cells.setBackground(Color.WHITE);
        arrayMessage.setForeground(new Color(105, 118, 136));
        JScrollPane scrollPane = new JScrollPane(cells);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(500, 260));
        add(scrollPane, BorderLayout.CENTER);
        add(arrayMessage, BorderLayout.SOUTH);
        showArray(new int[0], null);
    }

    public void showArray(int[] array, SearchStep step) {
        cells.removeAll();
        if (array.length == 0) {
            arrayMessage.setText("The array is empty.");
        } else {
            arrayMessage.setText("Index numbers are zero-based. L = low, M = mid, H = high.");
            Set<Integer> examined = step == null ? Set.of() : step.examinedIndexes();
            for (int index = 0; index < array.length; index++) {
                boolean found = step != null && step.eventType() == SearchStep.EventType.FOUND
                        && step.resultIndex() == index;
                boolean current = step != null && step.currentIndex() == index;
                boolean visited = examined.contains(index);
                Color background = found ? GREEN : current ? AMBER : visited ? new Color(216, 231, 252) : GRAY;
                JPanel item = new JPanel(new BorderLayout(2, 4));
                item.setOpaque(false);
                JLabel value = new JLabel(Integer.toString(array[index]), SwingConstants.CENTER);
                value.setFont(value.getFont().deriveFont(Font.BOLD, 16f));
                value.setForeground(INK);
                value.setOpaque(true);
                value.setBackground(background);
                value.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(found ? new Color(46, 145, 88) : current ? BLUE : new Color(197, 207, 220), 2),
                        BorderFactory.createEmptyBorder(9, 2, 9, 2)));
                JLabel roles = new JLabel(rolesAt(index, step), SwingConstants.CENTER);
                roles.setFont(roles.getFont().deriveFont(Font.BOLD, 10f));
                roles.setForeground(BLUE);
                JLabel indexLabel = new JLabel("index " + index, SwingConstants.CENTER);
                indexLabel.setFont(indexLabel.getFont().deriveFont(10f));
                indexLabel.setForeground(new Color(94, 107, 125));
                item.add(roles, BorderLayout.NORTH);
                item.add(value, BorderLayout.CENTER);
                item.add(indexLabel, BorderLayout.SOUTH);
                cells.add(item);
            }
        }
        cells.revalidate();
        cells.repaint();
    }

    private static String rolesAt(int index, SearchStep step) {
        if (step == null) {
            return " ";
        }
        StringBuilder roles = new StringBuilder();
        if (step.low() == index) {
            roles.append("L");
        }
        if (step.mid() == index) {
            roles.append("M");
        }
        if (step.high() == index) {
            roles.append("H");
        }
        return roles.length() == 0 ? " " : roles.toString();
    }

    public static JPanel createLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        legend.setOpaque(false);
        legend.add(legendItem(AMBER, "Current"));
        legend.add(legendItem(new Color(216, 231, 252), "Examined"));
        legend.add(legendItem(GREEN, "Found"));
        legend.add(legendItem(GRAY, "Not examined"));
        return legend;
    }

    private static JLabel legendItem(Color color, String label) {
        JLabel item = new JLabel("  " + label);
        item.setOpaque(true);
        item.setBackground(color);
        item.setForeground(NAVY);
        item.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        item.setFont(item.getFont().deriveFont(11f));
        return item;
    }
}

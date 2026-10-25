package edu.naap.searching.ui;

import edu.naap.searching.model.SourceLine;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CodePanel extends JPanel {
    private static final Color BACKGROUND = new Color(17, 25, 39);
    private static final Color ACTIVE = new Color(42, 66, 99);
    private static final Pattern TOKEN = Pattern.compile(
            "//.*|\"(?:\\\\.|[^\"\\\\])*\"|\\b(?:public|private|protected|static|final|int|while|if|else|return|new)\\b|\\b\\d+\\b|\\b[A-Za-z_][A-Za-z_0-9]*\\b");

    private final JTextPane code = new JTextPane();
    private final JLabel explanation = new JLabel("Prepare a search to follow the corresponding Java statements.");
    private List<SourceLine> lines = List.of();
    private Map<String, Integer> lineOffsets = Map.of();

    public CodePanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setBackground(Color.WHITE);
        JLabel title = new JLabel("LIVE JAVA SOURCE");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setForeground(new Color(18, 42, 74));
        code.setEditable(false);
        code.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        code.setBackground(BACKGROUND);
        code.setForeground(new Color(222, 230, 242));
        code.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        JScrollPane scrollPane = new JScrollPane(code);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(211, 220, 232)));
        explanation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 226, 236)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        explanation.setForeground(new Color(45, 62, 84));
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(explanation, BorderLayout.SOUTH);
    }

    public void showSource(List<SourceLine> sourceLines, String activeLineId, String activeExplanation) {
        lines = List.copyOf(sourceLines);
        StringBuilder text = new StringBuilder();
        Map<String, Integer> offsets = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            SourceLine line = lines.get(i);
            offsets.put(line.id(), text.length());
            text.append("   ").append(String.format("%2d", i + 1)).append(" | ").append(line.text()).append('\n');
        }
        code.setText(text.toString());
        lineOffsets = Map.copyOf(offsets);
        explanation.setText("<html><b>Current operation:</b> " + escape(activeExplanation) + "</html>");

        StyledDocument document = code.getStyledDocument();
        SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultStyle, new Color(222, 230, 242));
        StyleConstants.setBackground(defaultStyle, BACKGROUND);
        document.setCharacterAttributes(0, document.getLength(), defaultStyle, true);

        int activeOffset = offsets.getOrDefault(activeLineId, -1);
        if (activeOffset >= 0) {
            try {
                int lineLength = document.getText(activeOffset, document.getLength() - activeOffset).indexOf('\n') + 1;
                SimpleAttributeSet activeStyle = new SimpleAttributeSet();
                StyleConstants.setBackground(activeStyle, ACTIVE);
                document.setCharacterAttributes(activeOffset, lineLength, activeStyle, false);
            } catch (BadLocationException exception) {
                throw new IllegalStateException("Could not highlight the active Java source line.", exception);
            }
        }

        applySyntaxStyles(document);
        if (activeOffset >= 0) {
            int offset = activeOffset;
            SwingUtilities.invokeLater(() -> {
                try {
                    Rectangle rectangle = code.modelToView2D(offset).getBounds();
                    code.scrollRectToVisible(rectangle);
                } catch (BadLocationException exception) {
                    throw new IllegalStateException("Could not scroll to the active Java source line.", exception);
                }
            });
        }
    }

    private void applySyntaxStyles(StyledDocument document) {
        for (int i = 0; i < lines.size(); i++) {
            SourceLine line = lines.get(i);
            int codeOffset = lineOffsets.get(line.id()) + 8;
            Matcher matcher = TOKEN.matcher(line.text());
            while (matcher.find()) {
                String token = matcher.group();
                Color color;
                if (token.startsWith("//")) {
                    color = new Color(133, 157, 184);
                } else if (token.startsWith("\"")) {
                    color = new Color(157, 219, 160);
                } else if (Character.isDigit(token.charAt(0))) {
                    color = new Color(238, 191, 115);
                } else if (isKeyword(token)) {
                    color = new Color(126, 177, 255);
                } else {
                    color = new Color(224, 184, 255);
                }
                SimpleAttributeSet style = new SimpleAttributeSet();
                StyleConstants.setForeground(style, color);
                document.setCharacterAttributes(codeOffset + matcher.start(), matcher.end() - matcher.start(), style, false);
            }
        }
    }

    private static boolean isKeyword(String token) {
        return switch (token) {
            case "public", "private", "protected", "static", "final", "int",
                    "while", "if", "else", "return", "new" -> true;
            default -> false;
        };
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

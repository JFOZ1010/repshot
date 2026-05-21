// REPSHOT-CHANGE-LOG: 2026-05-16 — Cycle 14 — Fixed search (document getText), fixed › button layout, fixed scroll (overlay visibility), fixed word-wrap (JLayeredPane sizing)

package com.repshot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RepShotPanel extends JPanel {

    static final Color BG_DARK       = new Color(13,  17,  23);
    static final Color BG_PANEL      = new Color(22,  27,  34);
    static final Color BG_CODE       = new Color(30,  36,  46);
    static final Color TEXT_PRIMARY  = new Color(201, 209, 217);
    static final Color TEXT_PURE     = new Color(230, 237, 243);
    static final Color TEXT_MUTED    = new Color(139, 148, 158);
    static final Color ACCENT_ORANGE = new Color(247, 129, 102);
    static final Color ACCENT_CYAN   = new Color(121, 192, 255);
    static final Color TEXT_GREEN    = new Color(86,  211, 100);

    private JTextField titleField;
    private JComboBox<String> vulnTypeCombo;
    private JTextField vulnTypeOtherField;
    private JComboBox<String> severityCombo;
    private JTextField impactField;
    private JLabel impactCounter;
    private JTextField handleField;
    private JTextPane requestPane;
    private JTextPane responsePane;
    private String lastAutoFilledImpact = "";

    private BufferedImage reqCapture = null;
    private BufferedImage resCapture = null;
    private JLabel reqCaptureLabel;
    private JLabel resCaptureLabel;

    private static final int IMPACT_MAX    = 120;
    private static final int VULNOTHER_MAX = 20;

    public RepShotPanel(String request, String response) {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        add(buildFormPanel(),                    BorderLayout.NORTH);
        add(buildEditorsPanel(request, response), BorderLayout.CENTER);
        add(buildButtonsPanel(),                 BorderLayout.SOUTH);
    }

    // ── FORM ─────────────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(createStyledBorder("Finding Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(label("Finding Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        titleField = new JTextField("SQL Injection in /api/login");
        styleTextField(titleField);
        panel.add(titleField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(label("Vulnerability Type:"), gbc);

        JPanel vulnPanel = new JPanel(new BorderLayout(6, 0));
        vulnPanel.setBackground(BG_PANEL);
        vulnTypeCombo = new JComboBox<>(new String[]{
            "SQL Injection (Error-Based)", "SQL Injection (Blind/Boolean)",
            "SQL Injection (Out-of-Band)", "XSS (Reflected)", "XSS (Stored)",
            "XSS (DOM-Based)", "Command Injection", "Remote Code Execution (RCE)",
            "Server-Side Template Injection (SSTI)", "Path Traversal / LFI",
            "Remote File Inclusion (RFI)", "XML External Entity (XXE)",
            "Server-Side Request Forgery (SSRF)",
            "Insecure Direct Object Reference (IDOR)", "Broken Access Control",
            "Insecure File Upload", "Open Redirect", "Cache Poisoning",
            "HTTP Request Smuggling", "CORS Misconfiguration", "Clickjacking",
            "Subdomain Takeover", "Authentication Bypass",
            "Broken Auth / Session Management", "JWT Vulnerabilities",
            "Mass Assignment", "GraphQL Injection", "Prototype Pollution",
            "Insecure Deserialization", "Business Logic Flaw", "Other..."
        });
        styleCombo(vulnTypeCombo);

        vulnTypeOtherField = new JTextField();
        vulnTypeOtherField.setVisible(false);
        vulnTypeOtherField.setToolTipText("Specify vulnerability (max 20 chars)");
        styleTextField(vulnTypeOtherField);
        limitLength(vulnTypeOtherField, VULNOTHER_MAX);

        vulnTypeCombo.addActionListener(e -> {
            String sel = (String) vulnTypeCombo.getSelectedItem();
            boolean isOther = "Other...".equals(sel);
            vulnTypeOtherField.setVisible(isOther);
            vulnPanel.revalidate();
            if (isOther) {
                impactField.setText("");
                lastAutoFilledImpact = "";
            } else {
                String tmpl = ImpactTemplates.get(sel);
                if (!tmpl.isEmpty()) {
                    impactField.setText(tmpl);
                    lastAutoFilledImpact = tmpl;
                }
            }
        });

        vulnPanel.add(vulnTypeCombo,      BorderLayout.CENTER);
        vulnPanel.add(vulnTypeOtherField, BorderLayout.EAST);
        gbc.gridx = 3; gbc.weightx = 0.25;
        panel.add(vulnPanel, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(label("Business Impact:"), gbc);

        JPanel impactPanel = new JPanel(new BorderLayout(4, 0));
        impactPanel.setBackground(BG_PANEL);
        impactField = new JTextField("Attacker can extract sensitive database contents including credentials, PII, and business-critical data.");
        styleTextField(impactField);
        limitLength(impactField, IMPACT_MAX);
        impactCounter = new JLabel("106/" + IMPACT_MAX);
        impactCounter.setForeground(TEXT_MUTED);
        impactCounter.setFont(new Font("Monospaced", Font.PLAIN, 11));
        impactCounter.setBorder(new EmptyBorder(0, 6, 0, 0));
        impactField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateCounter(); }
            public void removeUpdate(DocumentEvent e)  { updateCounter(); }
            public void changedUpdate(DocumentEvent e) { updateCounter(); }
            private void updateCounter() {
                int len = impactField.getText().length();
                impactCounter.setText(len + "/" + IMPACT_MAX);
                impactCounter.setForeground(len > IMPACT_MAX * 0.85 ? ACCENT_ORANGE : TEXT_MUTED);
            }
        });
        impactPanel.add(impactField,   BorderLayout.CENTER);
        impactPanel.add(impactCounter, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 0.35;
        panel.add(impactPanel, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(label("Severity:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.25;
        severityCombo = new JComboBox<>(new String[]{"Critical","High","Medium","Low","Informational"});
        styleCombo(severityCombo);
        panel.add(severityCombo, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(label("Your Handle:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        handleField = new JTextField("@JF0x0r");
        styleTextField(handleField);
        panel.add(handleField, gbc);

        return panel;
    }

    // ── EDITORS ──────────────────────────────────────────────────────────

    private JPanel buildEditorsPanel(String request, String response) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(BG_DARK);
        panel.add(buildEditorBlock("REQUEST  — scroll, then Capture", request, true));
        panel.add(buildEditorBlock("RESPONSE — scroll, then Capture", response, false));
        return panel;
    }

    private JPanel buildEditorBlock(String title, String content, boolean isRequest) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setBackground(BG_PANEL);
        block.setBorder(createStyledBorder(title));

        // ── Search bar (hidden by default) ───────────────────────────
        // Layout: [‹][     search field     ][›][N/M]
        JPanel searchBar = new JPanel(new BorderLayout(4, 0));
        searchBar.setVisible(false);
        searchBar.setBackground(new Color(22, 27, 34));
        searchBar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JTextField searchField = new JTextField();
        styleTextField(searchField);
        searchField.setFont(getCodeFont(11));

        JButton btnPrev = smallButton("‹", new Color(40, 50, 60));
        JButton btnNext = smallButton("›", new Color(40, 50, 60));
        JLabel matchLabel = new JLabel(" 0/0 ");
        matchLabel.setForeground(TEXT_MUTED);
        matchLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));

        // Put prev+next+label in an EAST sub-panel so all three are visible
        JPanel searchRight = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        searchRight.setBackground(new Color(22, 27, 34));
        searchRight.add(btnPrev);
        searchRight.add(btnNext);
        searchRight.add(matchLabel);

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchRight, BorderLayout.EAST);

        // ── JTextPane with forced word-wrap ──────────────────────────
        JTextPane pane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true; // forces wrap at viewport width
            }
        };
        pane.setBackground(BG_CODE);
        pane.setForeground(TEXT_PRIMARY);
        pane.setCaretColor(TEXT_PURE);
        pane.setFont(getCodeFont(12));
        pane.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // Force character-level wrapping in StyledDocument
        pane.setEditorKit(new javax.swing.text.StyledEditorKit() {
            public javax.swing.text.ViewFactory getViewFactory() {
                return new javax.swing.text.ViewFactory() {
                    public javax.swing.text.View create(javax.swing.text.Element elem) {
                        String name = elem.getName();
                        if (name.equals(javax.swing.text.AbstractDocument.ParagraphElementName)) {
                            return new javax.swing.text.ParagraphView(elem) {
                                public int getResizeWeight(int axis) {
                                    return 1;  // Makes paragraph resizable and wrappable
                                }
                            };
                        } else if (name.equals(javax.swing.text.AbstractDocument.ContentElementName)) {
                            return new javax.swing.text.LabelView(elem);
                        } else if (name.equals(javax.swing.text.AbstractDocument.SectionElementName)) {
                            return new javax.swing.text.BoxView(elem, javax.swing.text.View.Y_AXIS);
                        } else if (name.equals("component")) {
                            return new javax.swing.text.ComponentView(elem);
                        } else if (name.equals("icon")) {
                            return new javax.swing.text.IconView(elem);
                        }
                        return new javax.swing.text.LabelView(elem);
                    }
                };
            }
        });
        
        pane.setText(content);
        
        SwingUtilities.invokeLater(() -> {
            applySyntaxHighlight(pane, content);
            if (pane.getParent() instanceof JViewport) {
                pane.setSize(new Dimension(((JViewport)pane.getParent()).getWidth(), 
                    Integer.MAX_VALUE));
            }
        });
        if (isRequest) requestPane = pane; else responsePane = pane;

        // ── ScrollPane ───────────────────────────────────────────────
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(48, 54, 61)));
        scroll.getViewport().setBackground(BG_CODE);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // ── RedBox overlay using JLayeredPane ────────────────────────
        // IMPORTANT: use null layout and manually size children so
        // the scroll pane keeps its full width (word-wrap works correctly)
        RedBoxOverlay overlay = new RedBoxOverlay();

        JLayeredPane layered = new JLayeredPane() {
            @Override
            public void doLayout() {
                // Both children fill the entire layered pane
                for (Component c : getComponents()) {
                    c.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        };
        layered.add(scroll,   JLayeredPane.DEFAULT_LAYER);
        layered.add(overlay,  JLayeredPane.PALETTE_LAYER);

        // Forward wheel events from overlay to scroll when not drawing
        overlay.addMouseWheelListener(e -> {
            if (!overlay.isDrawing) {
                scroll.dispatchEvent(SwingUtilities.convertMouseEvent(overlay, e, scroll));
            }
        });

        // Hide overlay (pass-through) when not in draw mode
        overlay.setVisible(false); // starts hidden — only shown when Draw Box active

        // ── Search state ─────────────────────────────────────────────
        List<Object>  searchTags    = new ArrayList<>();
        List<Integer> matchOffsets  = new ArrayList<>();
        int[]         curIdx        = {0};

        Runnable scrollToCur = () -> {
            if (matchOffsets.isEmpty()) return;
            try {
                Rectangle r = pane.modelToView(matchOffsets.get(curIdx[0]));
                if (r != null) pane.scrollRectToVisible(r);
            } catch (BadLocationException ex) {}
            matchLabel.setText(" " + (curIdx[0]+1) + "/" + matchOffsets.size() + " ");
        };

        Runnable runSearch = () -> {
            Highlighter h = pane.getHighlighter();
            for (Object tag : searchTags) {
                try { h.removeHighlight(tag); } catch (Exception ex) {}
            }
            searchTags.clear();
            matchOffsets.clear();

            String query = searchField.getText();
            if (query.isEmpty()) {
                matchLabel.setText(" 0/0 ");
                return;
            }

            // FIX: use document raw text to avoid StyledDocument artifacts
            String full;
            try {
                Document d = pane.getDocument();
                full = d.getText(0, d.getLength());
            } catch (BadLocationException ex) {
                full = pane.getText();
            }

            String fullLower  = full.toLowerCase();
            String queryLower = query.toLowerCase();
            int i = 0;
            while ((i = fullLower.indexOf(queryLower, i)) != -1) {
                matchOffsets.add(i);
                try {
                    Object tag = h.addHighlight(i, i + query.length(),
                        new DefaultHighlighter.DefaultHighlightPainter(
                            new Color(227, 179, 65, 200)));
                    searchTags.add(tag);
                } catch (BadLocationException ex) {}
                i += query.length();
            }

            curIdx[0] = 0;
            matchLabel.setText(matchOffsets.isEmpty() ? " 0/0 "
                : " 1/" + matchOffsets.size() + " ");
            if (!matchOffsets.isEmpty()) scrollToCur.run();
        };

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { runSearch.run(); }
            public void removeUpdate(DocumentEvent e)  { runSearch.run(); }
            public void changedUpdate(DocumentEvent e) { runSearch.run(); }
        });

        btnNext.addActionListener(e -> {
            if (matchOffsets.isEmpty()) return;
            curIdx[0] = (curIdx[0] + 1) % matchOffsets.size();
            scrollToCur.run();
        });

        btnPrev.addActionListener(e -> {
            if (matchOffsets.isEmpty()) return;
            curIdx[0] = (curIdx[0] - 1 + matchOffsets.size()) % matchOffsets.size();
            scrollToCur.run();
        });

        // Cmd+F / Ctrl+F — register on block panel so it fires even when pane loses focus
        KeyStroke ksOpen = KeyStroke.getKeyStroke(KeyEvent.VK_F,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        block.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ksOpen, "openSearch_" + title);
        block.getActionMap().put("openSearch_" + title, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                searchBar.setVisible(true);
                searchBar.revalidate();
                searchField.requestFocus();
            }
        });

        // Escape closes search
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    searchBar.setVisible(false);
                    Highlighter h = pane.getHighlighter();
                    for (Object tag : searchTags) {
                        try { h.removeHighlight(tag); } catch (Exception ex) {}
                    }
                    searchTags.clear();
                    matchOffsets.clear();
                    matchLabel.setText(" 0/0 ");
                    pane.requestFocus();
                }
            }
        });

        // ── Capture & draw buttons ────────────────────────────────────
        JLabel capLabel = new JLabel("No capture");
        capLabel.setForeground(TEXT_MUTED);
        capLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        capLabel.setBorder(new EmptyBorder(2, 6, 2, 0));
        if (isRequest) reqCaptureLabel = capLabel; else resCaptureLabel = capLabel;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        btnPanel.setBackground(BG_PANEL);

        JButton btnCapture = smallButton("[ 📷 Capture ]", new Color(40, 60, 40));
        btnCapture.addActionListener(e -> {
            // Make sure overlay is visible for capture if it has boxes
            boolean wasVisible = overlay.isVisible();
            if (!overlay.boxes.isEmpty()) overlay.setVisible(true);

            BufferedImage img = new BufferedImage(
                scroll.getViewport().getWidth(),
                scroll.getViewport().getHeight(),
                BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            scroll.getViewport().paint(g2);
            if (!overlay.boxes.isEmpty()) overlay.paint(g2);
            g2.dispose();

            overlay.setVisible(wasVisible);

            if (isRequest) reqCapture = img; else resCapture = img;
            updateCaptureLabel(isRequest);
        });

        JButton btnDrawBox = smallButton("[ ✏ Draw Box ]", new Color(40, 50, 60));
        btnDrawBox.addActionListener(e -> {
            overlay.toggleDrawMode();
            // Show overlay when drawing, hide when not (so scroll works)
            overlay.setVisible(overlay.isDrawing);
            btnDrawBox.setBackground(overlay.isDrawing
                ? new Color(60, 80, 60) : new Color(40, 50, 60));
        });

        JButton btnClearBoxes = smallButton("[ Clear Boxes ]", new Color(60, 60, 70));
        btnClearBoxes.addActionListener(e -> {
            overlay.clear();
            // Hide overlay after clearing if not in draw mode
            if (!overlay.isDrawing) overlay.setVisible(false);
        });

        JButton btnReset = smallButton("[ Reset Capture ]", new Color(60, 60, 70));
        btnReset.addActionListener(e -> {
            if (isRequest) reqCapture = null; else resCapture = null;
            updateCaptureLabel(isRequest);
        });

        btnPanel.add(btnCapture);
        btnPanel.add(btnDrawBox);
        btnPanel.add(btnClearBoxes);
        btnPanel.add(btnReset);
        btnPanel.add(capLabel);

        block.add(searchBar, BorderLayout.NORTH);
        block.add(layered,   BorderLayout.CENTER);
        block.add(btnPanel,  BorderLayout.SOUTH);
        return block;
    }

    private void updateCaptureLabel(boolean isRequest) {
        BufferedImage cap = isRequest ? reqCapture : resCapture;
        JLabel lbl = isRequest ? reqCaptureLabel : resCaptureLabel;
        if (cap == null) {
            lbl.setText("No capture");
            lbl.setForeground(TEXT_MUTED);
        } else {
            lbl.setText("✓ Captured");
            lbl.setForeground(TEXT_GREEN);
        }
    }


    private void applySyntaxHighlight(JTextPane pane, String text) {
        if (text == null || text.isEmpty()) return;
        StyledDocument doc = pane.getStyledDocument();
        String t = text.trim();

        Style base = pane.addStyle("base", null);
        StyleConstants.setForeground(base, TEXT_PRIMARY);
        doc.setCharacterAttributes(0, text.length(), base, true);

        if (t.startsWith("HTTP/") || t.startsWith("<!DOCTYPE") || t.startsWith("<html")) {
            highlightRegex(doc, text, "<[^>]+>",        new Color(121, 192, 255));
            highlightRegex(doc, text, "\\s[a-zA-Z\\-]+=", new Color(247, 129, 102));
            highlightRegex(doc, text, "\"[^\"]*\"",     new Color(86, 211, 100));
            highlightRegex(doc, text, "<!--.*?-->",     new Color(139, 148, 158));
        } else if (t.startsWith("{") || t.startsWith("[")) {
            highlightRegex(doc, text, "\"([^\"]+)\"\\s*:", new Color(121, 192, 255));
            highlightRegex(doc, text, ":\\s*\"([^\"]*)\"", new Color(86, 211, 100));
            highlightRegex(doc, text, "\\b\\d+(\\.\\d+)?\\b", new Color(247, 129, 102));
            highlightRegex(doc, text, "\\b(true|false|null)\\b", new Color(255, 123, 114));
        } else if (t.startsWith("GET ")  || t.startsWith("POST ")  ||
                   t.startsWith("PUT ")  || t.startsWith("DELETE ") ||
                   t.startsWith("PATCH ")|| t.startsWith("OPTIONS ")|| t.startsWith("HEAD ")) {
            int firstNL = text.indexOf('\n');
            if (firstNL > 0) {
                Style bold = pane.addStyle("bold", null);
                StyleConstants.setForeground(bold, TEXT_PURE);
                StyleConstants.setBold(bold, true);
                doc.setCharacterAttributes(0, firstNL, bold, true);
            }
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "^([a-zA-Z\\-]+):\\s*(.*)$", java.util.regex.Pattern.MULTILINE);
            java.util.regex.Matcher m = p.matcher(text);
            Style keyStyle = pane.addStyle("hkey", null);
            StyleConstants.setForeground(keyStyle, new Color(121, 192, 255));
            Style valStyle = pane.addStyle("hval", null);
            StyleConstants.setForeground(valStyle, new Color(201, 209, 217));
            while (m.find()) {
                doc.setCharacterAttributes(m.start(1), m.end(1)-m.start(1), keyStyle, true);
                if (m.groupCount() >= 2)
                    doc.setCharacterAttributes(m.start(2), m.end(2)-m.start(2), valStyle, true);
            }
        }
    }

    private void highlightRegex(StyledDocument doc, String text, String regex, Color c) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            regex, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(text);
        Style s = doc.addStyle(null, null);
        StyleConstants.setForeground(s, c);
        while (m.find()) doc.setCharacterAttributes(m.start(), m.end()-m.start(), s, false);
    }

    // ── BUTTONS ──────────────────────────────────────────────────────────

    private JPanel buildButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setBackground(BG_DARK);
        JButton previewBtn = new JButton("Preview Card");
        styleButton(previewBtn, BG_PANEL, TEXT_PRIMARY);
        previewBtn.addActionListener(this::onPreview);
        JButton exportBtn = new JButton("Export PNG");
        styleButton(exportBtn, ACCENT_ORANGE, Color.WHITE);
        exportBtn.addActionListener(this::onExport);
        panel.add(previewBtn);
        panel.add(exportBtn);
        return panel;
    }

    private void onPreview(ActionEvent e) {
        new CardRenderer(collectData()).showPreview();
    }

    private void onExport(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("repshot-finding.png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                new CardRenderer(collectData()).exportPNG(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Card exported! Ready to share.",
                    "RepShot", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "RepShot Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private CardData collectData() {
        String vulnType = (String) vulnTypeCombo.getSelectedItem();
        if ("Other...".equals(vulnType)) {
            String c = vulnTypeOtherField.getText().trim();
            vulnType = c.isEmpty() ? "Other" : c;
        }
        String reqText, resText;
        try {
            Document rd = requestPane.getDocument();
            reqText = rd.getText(0, rd.getLength());
            Document sd = responsePane.getDocument();
            resText = sd.getText(0, sd.getLength());
        } catch (BadLocationException ex) {
            reqText = requestPane.getText();
            resText = responsePane.getText();
        }
        return new CardData(
            titleField.getText().trim(), vulnType,
            (String) severityCombo.getSelectedItem(),
            impactField.getText().trim(),
            handleField.getText().trim(),
            reqText, resText,
            reqCapture, resCapture
        );
    }

    // ── STYLING ──────────────────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return l;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(BG_CODE);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_CYAN);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(48, 54, 61)),
            new EmptyBorder(4, 8, 4, 8)));
        tf.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(BG_CODE);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton smallButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(new Font("Monospaced", Font.BOLD, 11));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private TitledBorder createStyledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(48, 54, 61)), title);
        b.setTitleColor(ACCENT_CYAN);
        b.setTitleFont(new Font("Monospaced", Font.BOLD, 11));
        return b;
    }

    private static Font getCodeFont(int size) {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        HashSet<String> avail = new HashSet<>(Arrays.asList(names));
        for (String c : new String[]{"JetBrains Mono","Hack","Fira Code","Courier New"})
            if (avail.contains(c)) return new Font(c, Font.PLAIN, size);
        return new Font("Monospaced", Font.PLAIN, size);
    }

    private void limitLength(JTextField tf, int max) {
        ((AbstractDocument) tf.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                throws BadLocationException {
                if (fb.getDocument().getLength() + str.length() <= max)
                    super.insertString(fb, off, str, a);
            }
            public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                throws BadLocationException {
                if (fb.getDocument().getLength() - len + str.length() <= max)
                    super.replace(fb, off, len, str, a);
            }
        });
    }

    // ── RED BOX OVERLAY ──────────────────────────────────────────────────

    private class RedBoxOverlay extends JComponent {
        List<Rectangle> boxes = new ArrayList<>();
        boolean isDrawing = false;
        boolean drawing   = false;
        Point     startPoint;
        Rectangle currentBox;

        RedBoxOverlay() {
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (!isDrawing) return;
                    startPoint = e.getPoint();
                    drawing = true;
                }
                public void mouseReleased(MouseEvent e) {
                    if (!isDrawing) return;
                    if (drawing && currentBox != null) boxes.add(new Rectangle(currentBox));
                    drawing = false; currentBox = null;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (!isDrawing || !drawing) return;
                    currentBox = normalize(startPoint, e.getPoint());
                    repaint();
                }
            });
        }

        void toggleDrawMode() {
            isDrawing = !isDrawing;
            setCursor(isDrawing
                ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        void clear() { boxes.clear(); repaint(); }

        private Rectangle normalize(Point p1, Point p2) {
            return new Rectangle(
                Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(2f));
            for (Rectangle box : boxes) drawBox(g2, box);
            if (drawing && currentBox != null) drawBox(g2, currentBox);
            g2.dispose();
        }

        private void drawBox(Graphics2D g2, Rectangle box) {
            g2.setColor(new Color(255, 68, 68, 40));
            g2.fillRect(box.x, box.y, box.width, box.height);
            g2.setColor(new Color(255, 68, 68, 255));
            g2.drawRect(box.x, box.y, box.width, box.height);
        }

        public void paint(Graphics g) { paintComponent(g); }
    }
}
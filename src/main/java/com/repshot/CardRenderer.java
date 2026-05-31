// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 7 — addPin uses char-accumulation (no \n counting); pin = startLine exactly
// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 8 — Visual viewport capture replaces pin system
// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 9 — Capture aspect ratio scaling + syntax highlighting

package com.repshot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CardRenderer {

    // HD Scaling: CARD_WIDTH doubled from 1200 to 2400 pixels
    private static final int CARD_WIDTH  = 2400;
    private static final int PADDING     = 80;
    private static final int MAX_LINES   = 35;
    private static final int LINE_HEIGHT = 34;

    // GitHub Dark Color Palette
    private static final Color BG_DARK       = new Color(13,  17,  23);
    private static final Color BG_PANEL      = new Color(22,  27,  34);
    private static final Color BG_CODE       = new Color(30,  36,  46);
    private static final Color TEXT_PRIMARY  = new Color(201, 209, 217);     // #c9d1d9 (opaque white)
    private static final Color TEXT_PURE     = new Color(230, 237, 243);     // #e6edf3 (pure white)
    private static final Color TEXT_MUTED    = new Color(139, 148, 158);
    private static final Color BORDER_COLOR  = new Color(48,  54,  61);
    private static final Color ACCENT_ORANGE = new Color(247, 129, 102);
    private static final Color ACCENT_CYAN   = new Color(121, 192, 255);
    private static final Color TEXT_GREEN    = new Color(86,  211, 100);

    private final CardData data;
    private static String cachedFontName = null;

    public CardRenderer(CardData data) { this.data = data; }

    private static Font getCodeFont(int size) {
        if (cachedFontName == null) {
            String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
            java.util.Set<String> availableFonts = new java.util.HashSet<>(java.util.Arrays.asList(fontNames));
            for (String candidate : new String[]{"JetBrains Mono", "Hack", "Fira Code", "Courier New"}) {
                if (availableFonts.contains(candidate)) {
                    cachedFontName = candidate;
                    break;
                }
            }
            if (cachedFontName == null) cachedFontName = "Monospaced";
        }
        return new Font(cachedFontName, Font.PLAIN, size);
    }

    public void showPreview() {
        BufferedImage img = render();
        int pw = (int)(img.getWidth()  * 0.5);
        int ph = (int)(img.getHeight() * 0.5);
        JLabel label = new JLabel(new ImageIcon(
            img.getScaledInstance(pw, ph, Image.SCALE_SMOOTH)));
        JScrollPane scroll = new JScrollPane(label);
        scroll.setPreferredSize(new Dimension(Math.min(pw + 30, 1400), 800));
        JFrame frame = new JFrame("RepShot — Preview (50% scale)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(scroll);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void exportPNG(File file) throws IOException {
        BufferedImage img = render();
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".png")) path += ".png";
        ImageIO.write(img, "PNG", new File(path));
    }

    public BufferedImage render() {
        int height = calculateHeight();
        BufferedImage img = new BufferedImage(CARD_WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = setupGraphics(img);

        g.setColor(BG_DARK);
        g.fillRect(0, 0, CARD_WIDTH, height);

        int y = 0;
        y = drawHeader(g, y);
        y = drawSeverityRow(g, y);
        y = drawTitle(g, y);
        y = drawImpact(g, y);
        y = drawDivider(g, y);
        y = drawCodePanels(g, y);
        drawFooter(g, height);

        g.dispose();
        return img;
    }

    private Graphics2D setupGraphics(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,         RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,            RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,       RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    private int drawHeader(Graphics2D g, int y) {
        GradientPaint gp = new GradientPaint(0, 0, new Color(30, 36, 46),
                                             CARD_WIDTH, 0, new Color(20, 25, 35));
        g.setPaint(gp);
        g.fillRect(0, 0, CARD_WIDTH, 136);
        g.setColor(ACCENT_ORANGE);
        g.fillRect(0, 0, 10, 136);

        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        g.setColor(ACCENT_ORANGE);
        g.drawString("⚡ RepShot", PADDING, 86);

        g.setFont(new Font("Monospaced", Font.PLAIN, 24));
        g.setColor(TEXT_MUTED);
        g.drawString("Security Finding Card", PADDING + 310, 86);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int tsW = g.getFontMetrics().stringWidth(ts);
        g.drawString(ts, CARD_WIDTH - PADDING - tsW, 86);

        g.setColor(BORDER_COLOR);
        g.fillRect(0, 136, CARD_WIDTH, 2);
        return 136;
    }

    private int drawSeverityRow(Graphics2D g, int y) {
        y += 36;
        Color sevColor = getSeverityColor();

        g.setColor(sevColor);
        g.fill(new RoundRectangle2D.Float(PADDING, y, 220, 52, 16, 16));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String sev = data.severity.toUpperCase();
        g.drawString(sev, PADDING + (220 - fm.stringWidth(sev)) / 2, y + 34);

        g.setColor(new Color(48, 54, 61));
        g.fill(new RoundRectangle2D.Float(PADDING + 236, y, 420, 52, 16, 16));
        g.setColor(ACCENT_CYAN);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.drawString(data.vulnType, PADDING + 260, y + 34);

        if (data.handle != null && !data.handle.isBlank()) {
            g.setColor(TEXT_MUTED);
            g.setFont(new Font("Monospaced", Font.PLAIN, 24));
            String h = "Reported by: " + data.handle;
            g.drawString(h, CARD_WIDTH - PADDING - g.getFontMetrics().stringWidth(h), y + 34);
        }
        return y + 52 + 28;
    }

    private int drawTitle(Graphics2D g, int y) {
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.setColor(TEXT_PRIMARY);
        g.drawString(data.title, PADDING, y + 56);
        return y + 56 + 28;
    }

    private int drawImpact(Graphics2D g, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(TEXT_MUTED);
        g.drawString("BUSINESS IMPACT", PADDING, y + 24);
        y += 32;
        g.setColor(new Color(255, 100, 50, 18));
        g.fillRoundRect(PADDING, y, CARD_WIDTH - PADDING * 2, 60, 12, 12);
        g.setColor(ACCENT_ORANGE);
        g.fillRect(PADDING, y, 6, 60);
        g.setFont(new Font("SansSerif", Font.PLAIN, 26));
        g.setColor(TEXT_PRIMARY);
        g.drawString(data.impact, PADDING + 24, y + 40);
        return y + 60 + 28;
    }

    private int drawDivider(Graphics2D g, int y) {
        g.setColor(BORDER_COLOR);
        g.fillRect(PADDING, y, CARD_WIDTH - PADDING * 2, 2);
        return y + 36;
    }

    private int drawCodePanels(Graphics2D g, int y) {
        int panelW = (CARD_WIDTH - PADDING * 2 - 24) / 2;

        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(ACCENT_CYAN);
        g.drawString("[REQ]", PADDING, y + 26);
        g.setColor(TEXT_GREEN);
        g.drawString("[RES]", PADDING + panelW + 24, y + 26);
        y += 36;

        int panelH = getPanelHeight();

        g.setColor(BG_CODE);
        g.fillRoundRect(PADDING,               y, panelW, panelH, 16, 16);
        g.fillRoundRect(PADDING + panelW + 24, y, panelW, panelH, 16, 16);
        
        Shape savedClip = g.getClip();

        // Panel REQ
        g.setClip(PADDING + 2, y + 2, panelW - 4, panelH - 4);
        if (data.reqCapture != null) {
            int scaledW = data.reqCapture.getWidth() * 2;
            int scaledH = data.reqCapture.getHeight() * 2;
            if (scaledW > panelW) {
                double ratio = (double) data.reqCapture.getHeight() / data.reqCapture.getWidth();
                scaledW = panelW;
                scaledH = (int)(panelW * ratio);
            }
            int drawX = PADDING + (panelW - scaledW) / 2;
            g.drawImage(data.reqCapture, drawX, y + 4, scaledW, scaledH, null);
        } else {
            List<String> reqLines = splitLines(data.request, MAX_LINES, 0);
            drawCode(g, reqLines, data.request, PADDING + 20, y + LINE_HEIGHT, panelW - 32);
        }
        g.setClip(savedClip);

        // Panel RES
        g.setClip(PADDING + panelW + 26, y + 2, panelW - 4, panelH - 4);
        if (data.resCapture != null) {
            int scaledW = data.resCapture.getWidth() * 2;
            int scaledH = data.resCapture.getHeight() * 2;
            if (scaledW > panelW) {
                double ratio = (double) data.resCapture.getHeight() / data.resCapture.getWidth();
                scaledW = panelW;
                scaledH = (int)(panelW * ratio);
            }
            int drawX = PADDING + panelW + 24 + (panelW - scaledW) / 2;
            g.drawImage(data.resCapture, drawX, y + 4, scaledW, scaledH, null);
        } else {
            List<String> resLines = splitLines(data.response, MAX_LINES, 0);
            drawCode(g, resLines, data.response, PADDING + panelW + 44, y + LINE_HEIGHT, panelW - 32);
        }
        g.setClip(savedClip);

        // Draw borders over everything
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(PADDING,               y, panelW, panelH, 16, 16);
        g.drawRoundRect(PADDING + panelW + 24, y, panelW, panelH, 16, 16);

        return y + panelH + 40;
    }

    private void drawCode(Graphics2D g, List<String> displayLines,
                          String fullText, int x, int y, int maxW) {
        Font codeFont = getCodeFont(22);
        g.setFont(codeFont);
        FontMetrics fm = g.getFontMetrics();

        String[] originalLines = fullText.split("\n", -1);

        int[] origToVisualStart = new int[originalLines.length];
        int totalVisual = 0;
        for (int i = 0; i < originalLines.length; i++) {
            origToVisualStart[i] = totalVisual;
            totalVisual += wrapLine(originalLines[i], maxW, fm).size();
        }

        int maxVisible = displayLines.size();

        for (int i = 0; i < originalLines.length; i++) {
            List<String> wrappedSegments = wrapLine(originalLines[i], maxW, fm);

            for (int wi = 0; wi < wrappedSegments.size(); wi++) {
                int absVisual = origToVisualStart[i] + wi;
                if (absVisual >= maxVisible) return;

                int lineY = y + (absVisual * LINE_HEIGHT);
                g.setColor((i == 0 && wi == 0) ? TEXT_PURE : TEXT_PRIMARY);
                g.drawString(wrappedSegments.get(wi), x, lineY);
            }
        }
    }

    private List<String> wrapLine(String line, int maxW, FontMetrics fm) {
        List<String> result = new ArrayList<>();
        if (line.isEmpty()) { result.add(""); return result; }
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            String testStr = current.toString() + line.charAt(i);
            if (fm.stringWidth(testStr) > maxW) {
                if (current.length() == 0) {
                    current.append(line.charAt(i));
                    result.add(current.toString());
                    current = new StringBuilder();
                } else {
                    result.add(current.toString());
                    current = new StringBuilder().append(line.charAt(i));
                }
            } else {
                current.append(line.charAt(i));
            }
        }
        if (current.length() > 0) result.add(current.toString());
        return result;
    }

    private void drawFooter(Graphics2D g, int totalH) {
        int fy = totalH - 76;
        g.setColor(BORDER_COLOR);
        g.fillRect(0, fy, CARD_WIDTH, 2);
        g.setColor(new Color(22, 27, 34));
        g.fillRect(0, fy + 2, CARD_WIDTH, 74);
        g.setColor(ACCENT_ORANGE);
        g.fillRect(0, fy + 2, 10, 74);

        g.setFont(new Font("Monospaced", Font.PLAIN, 22));
        g.setColor(TEXT_MUTED);
        g.drawString("⚡ RepShot | Created by Juan Felipe Oz aka @JF0x0r | portswigger.net",
            PADDING, fy + 46);

        for (int i = 0; i < 4; i++) {
            g.setColor(i == 0 ? ACCENT_ORANGE : BORDER_COLOR);
            g.fillOval(CARD_WIDTH - PADDING - (i * 26), fy + 28, 14, 14);
        }
    }

    private List<String> splitLines(String text, int max, int startLine) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) { result.add("(empty)"); return result; }
        String[] lines = text.split("\n", -1);
        int from   = Math.min(startLine, lines.length);
        int to     = Math.min(from + max, lines.length);
        for (int i = from; i < to; i++) result.add(lines[i]);
        int hidden = lines.length - to;
        if (hidden > 0) result.add("  \u2026 (" + hidden + " lines hidden)");
        return result;
    }

    private int getPanelHeight() {
        int reqH = (MAX_LINES * LINE_HEIGHT) + 40;
        int resH = (MAX_LINES * LINE_HEIGHT) + 40;
        int panelW = (CARD_WIDTH - PADDING * 2 - 24) / 2;

        if (data.reqCapture != null) {
            int scaledW = data.reqCapture.getWidth() * 2;
            if (scaledW > panelW) {
                double ratio = (double) data.reqCapture.getHeight() / data.reqCapture.getWidth();
                reqH = (int)(panelW * ratio) + 8;
            } else {
                reqH = data.reqCapture.getHeight() * 2 + 8;
            }
        }
        if (data.resCapture != null) {
            int scaledW = data.resCapture.getWidth() * 2;
            if (scaledW > panelW) {
                double ratio = (double) data.resCapture.getHeight() / data.resCapture.getWidth();
                resH = (int)(panelW * ratio) + 8;
            } else {
                resH = data.resCapture.getHeight() * 2 + 8;
            }
        }
        return Math.max(reqH, resH);
    }

    private int calculateHeight() {
        int panelH = getPanelHeight();
        return 136 + 36 + 52 + 28 + 56 + 60 + 28 + 36 + 36 + panelH + 40 + 76 + 40;
    }

    private Color getSeverityColor() {
        return switch (data.severity) {
            case "Critical"      -> new Color(218, 54,  51);
            case "High"          -> new Color(210, 153, 34);
            case "Medium"        -> new Color(181, 137, 0);
            case "Low"           -> new Color(63,  185, 80);
            case "Informational" -> new Color(88,  166, 255);
            default              -> new Color(100, 100, 100);
        };
    }
}
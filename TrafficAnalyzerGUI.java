import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TrafficAnalyzerGUI extends JFrame {

    private final Map<String, JunctionCard> cardsMap = new HashMap<>();
    private JTextPane logPane;
    private JButton startButton;
    private JLabel statusLabel;
    
    private int activeThreads = 0;
    private final String[] junctions = {
        "Silk Board", "KR Puram", "Hebbal", "Marathahalli", "Electronic City"
    };

    // Design Colors
    public static final Color COLOR_BG = new Color(18, 18, 20);
    public static final Color COLOR_HEADER = new Color(24, 24, 28);
    public static final Color COLOR_CONSOLE = new Color(9, 9, 11);
    public static final Color COLOR_TEXT_LIGHT = new Color(244, 244, 245);
    public static final Color COLOR_MUTED = new Color(160, 160, 170);
    
    public static final Color COLOR_GREEN = new Color(52, 211, 153);
    public static final Color COLOR_RED = new Color(248, 113, 113);
    public static final Color COLOR_BLUE = new Color(96, 165, 250);

    public TrafficAnalyzerGUI() {
        super("Bengaluru Traffic Junction Monitor - Parallel Sensor Analyzer");
        initializeGUI();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(15, 15));

        // --- 1. HEADER PANEL ---
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(63, 63, 70)); // subtle bottom border
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout(20, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Title and Subtitle Info
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("BENGALURU TRAFFIC JUNCTION MONITOR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_LIGHT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));

        JLabel subtitleLabel = new JLabel("Real-Time Parallel Analytics Engine (SDG 9: Industry, Innovation & Infrastructure)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_MUTED);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Control Panel inside Header
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlPanel.setOpaque(false);

        statusLabel = new JLabel("Status: Idle");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(COLOR_MUTED);
        controlPanel.add(statusLabel);

        // Custom Styled Rounded Start Button
        startButton = new JButton("Start Simulation") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(63, 63, 70));
                } else if (getModel().isPressed()) {
                    g2.setColor(new Color(79, 70, 229));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(99, 102, 241).brighter());
                } else {
                    g2.setColor(new Color(79, 70, 229));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        startButton.setPreferredSize(new Dimension(140, 38));
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        controlPanel.add(startButton);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. GRID PANEL FOR JUNCTIONS ---
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsGrid.setOpaque(false);
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Create cards and add to grid
        for (String junction : junctions) {
            JunctionCard card = new JunctionCard(junction);
            cardsMap.put(junction, card);
            cardsGrid.add(card);
        }

        // Fill 6th spot in grid with an info card
        JPanel infoCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 24, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(63, 63, 70));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setLayout(new BorderLayout());
        infoCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel infoTitle = new JLabel("System Diagnostics");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(COLOR_TEXT_LIGHT);
        infoCard.add(infoTitle, BorderLayout.NORTH);

        JTextArea infoBody = new JTextArea(
            "AI Pipeline Thresholds:\n" +
            "• High traffic: > 80 vehicles\n" +
            "• Congestion: < 20 km/h avg speed\n" +
            "• Toxic air quality: AQI > 150\n\n" +
            "Multithreading:\n" +
            "• Each sensor executes in a parallel worker thread.\n" +
            "• Independent simulation loops (5 runs each)."
        );
        infoBody.setEditable(false);
        infoBody.setOpaque(false);
        infoBody.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoBody.setForeground(COLOR_MUTED);
        infoCard.add(infoBody, BorderLayout.CENTER);

        cardsGrid.add(infoCard);
        add(cardsGrid, BorderLayout.CENTER);

        // --- 3. CONSOLE LOG PANEL ---
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setOpaque(false);
        consolePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel logHeader = new JLabel("Live Analytics & Event Stream");
        logHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logHeader.setForeground(COLOR_TEXT_LIGHT);
        logHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        consolePanel.add(logHeader, BorderLayout.NORTH);

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(COLOR_CONSOLE);
        logPane.setCaretColor(Color.WHITE);
        logPane.setFont(new Font("Consolas", Font.PLAIN, 12));
        logPane.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(63, 63, 70)));
        // Customizing scrollbar UI if possible is cool, but standard scrollbar in dark looks okay
        consolePanel.add(scrollPane, BorderLayout.CENTER);

        add(consolePanel, BorderLayout.SOUTH);
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        logPane.setText("");
        statusLabel.setText("Status: Analyzing");
        statusLabel.setForeground(COLOR_BLUE);

        // Reset all cards
        for (JunctionCard card : cardsMap.values()) {
            card.resetCard();
        }

        activeThreads = junctions.length;

        // Spawn a thread for each junction
        for (String junction : junctions) {
            JunctionMonitor monitor = new JunctionMonitor(junction, this);
            Thread thread = new Thread(monitor, "JunctionThread-" + junction);
            thread.start();
        }
    }

    public synchronized void updateJunctionCard(String name, SensorReading reading, AnomalyResult result, boolean active) {
        JunctionCard card = cardsMap.get(name);
        if (card != null) {
            card.updateData(reading, result, active);
        }
    }

    public synchronized void reportThreadFinished(String name) {
        activeThreads--;
        if (activeThreads <= 0) {
            startButton.setEnabled(true);
            statusLabel.setText("Status: Idle");
            statusLabel.setForeground(COLOR_MUTED);
            appendLog("[COMPLETE] All sensors completed simulation loops.", COLOR_BLUE);
        }
    }

    public void appendLog(String text, Color color) {
        StyledDocument doc = logPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text + "\n", style);
            // Autoscroll to bottom
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Run GUI construction on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Set System Look & Feel (if possible) or stay cross-platform
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            TrafficAnalyzerGUI frame = new TrafficAnalyzerGUI();
            frame.setVisible(true);
        });
    }
}

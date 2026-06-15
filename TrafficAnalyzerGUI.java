import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TrafficAnalyzerGUI extends JFrame {

    private final Map<String, JunctionCard> cardsMap = new HashMap<>();
    private final Map<String, Integer> junctionIterationMap = new HashMap<>();
    private JTextPane logPane;
    private JButton startButton;
    private JLabel statusLabel;
    private JComboBox<String> weatherComboBox;
    
    private JLabel briefingHeadlineLabel;
    private JTextArea briefingTextArea;
    
    private CityMapPanel cityMapPanel;
    private AnalyticsDashboardPanel dashboardPanel;
    
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
    public static final Color COLOR_YELLOW = new Color(251, 191, 36);
    public static final Color COLOR_RED = new Color(248, 113, 113);
    public static final Color COLOR_BLUE = new Color(96, 165, 250);

    public TrafficAnalyzerGUI() {
        super("Bengaluru Traffic Junction Monitor - Smart AI Operations Centre");
        initializeGUI();
        startRepaintTimer();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 780);
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

        JLabel subtitleLabel = new JLabel("Smart AI Transport & Analytics System (SDG 9: Infrastructure & Innovation)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_MUTED);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Control Panel inside Header
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlPanel.setOpaque(false);

        // Weather Selector Label
        JLabel weatherLabel = new JLabel("Weather:");
        weatherLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        weatherLabel.setForeground(COLOR_MUTED);
        controlPanel.add(weatherLabel);

        // Weather Dropdown
        weatherComboBox = new JComboBox<>(new String[]{"Sunny", "Heavy Rain", "Dense Fog"});
        weatherComboBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
        weatherComboBox.setBackground(COLOR_HEADER);
        weatherComboBox.setForeground(COLOR_TEXT_LIGHT);
        weatherComboBox.setPreferredSize(new Dimension(110, 30));
        weatherComboBox.setFocusable(false);
        
        // Custom cell renderer to guarantee text visibility across dark modes on all OS
        weatherComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(COLOR_HEADER);
                c.setForeground(COLOR_TEXT_LIGHT);
                if (isSelected) {
                    c.setBackground(new Color(79, 70, 229));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        controlPanel.add(weatherComboBox);

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

        // --- 2. TABBED PANEL FOR CORE OPERATIONS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(COLOR_HEADER);
        tabbedPane.setForeground(COLOR_MUTED);

        // TAB 1: Live Operations
        JPanel liveOpsPanel = new JPanel(new BorderLayout(15, 15));
        liveOpsPanel.setOpaque(false);

        // Junction Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsGrid.setOpaque(false);
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Create cards and add to grid
        for (String junction : junctions) {
            JunctionCard card = new JunctionCard(junction);
            cardsMap.put(junction, card);
            cardsGrid.add(card);
        }

        // Replace 6th diagnostics card with CityMapPanel
        cityMapPanel = new CityMapPanel();
        cardsGrid.add(cityMapPanel);
        liveOpsPanel.add(cardsGrid, BorderLayout.CENTER);

        // Split Bottom Console Area: Left is Console Log, Right is AI Explainer Panel
        JPanel splitConsolePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitConsolePanel.setOpaque(false);
        splitConsolePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Column 1: Live Log Pane
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setOpaque(false);

        JLabel logHeader = new JLabel("Live AI Analytics & Event Stream");
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
        scrollPane.setPreferredSize(new Dimension(0, 160));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(63, 63, 70)));
        consolePanel.add(scrollPane, BorderLayout.CENTER);
        splitConsolePanel.add(consolePanel);

        // Column 2: AI Command Briefing & Explainer Panel
        JPanel briefingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_HEADER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(63, 63, 70));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        briefingPanel.setOpaque(false);
        briefingPanel.setLayout(new BorderLayout());
        briefingPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel briefingHeader = new JLabel("🧠 AI BRIEFING & EXPLAINER BOARD");
        briefingHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        briefingHeader.setForeground(COLOR_BLUE);
        briefingPanel.add(briefingHeader, BorderLayout.NORTH);

        JPanel briefingBody = new JPanel();
        briefingBody.setOpaque(false);
        briefingBody.setLayout(new BoxLayout(briefingBody, BoxLayout.Y_AXIS));
        briefingBody.add(Box.createVerticalStrut(6));

        briefingHeadlineLabel = new JLabel("Status: Idle");
        briefingHeadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        briefingHeadlineLabel.setForeground(COLOR_TEXT_LIGHT);
        briefingBody.add(briefingHeadlineLabel);
        briefingBody.add(Box.createVerticalStrut(4));

        briefingTextArea = new JTextArea("Ready to display real-time transport actions and explainer briefs...");
        briefingTextArea.setEditable(false);
        briefingTextArea.setOpaque(false);
        briefingTextArea.setLineWrap(true);
        briefingTextArea.setWrapStyleWord(true);
        briefingTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        briefingTextArea.setForeground(COLOR_MUTED);
        briefingBody.add(briefingTextArea);

        briefingPanel.add(briefingBody, BorderLayout.CENTER);
        splitConsolePanel.add(briefingPanel);

        liveOpsPanel.add(splitConsolePanel, BorderLayout.SOUTH);

        tabbedPane.addTab("🚦 Live Operations Centre", liveOpsPanel);

        // TAB 2: Analytics Dashboard
        dashboardPanel = new AnalyticsDashboardPanel();
        tabbedPane.addTab("📊 Analytics & Insights Dashboard", dashboardPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void startRepaintTimer() {
        // Run a repaint timer at 25fps (every 40ms) to allow card flashing/re-rendering animations to be fluid
        Timer repaintTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (activeThreads > 0) {
                    for (JunctionCard card : cardsMap.values()) {
                        card.repaint();
                    }
                    if (cityMapPanel != null) {
                        cityMapPanel.repaint();
                    }
                }
            }
        });
        repaintTimer.start();
    }

    public String getWeatherCondition() {
        return (String) weatherComboBox.getSelectedItem();
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        weatherComboBox.setEnabled(false);
        logPane.setText("");
        statusLabel.setText("Status: Analyzing");
        statusLabel.setForeground(COLOR_BLUE);

        // Log initial Weather
        String weather = getWeatherCondition();
        appendLog("[WEATHER ENGINE] Global condition initialized: " + weather.toUpperCase(), COLOR_BLUE);
        if ("Heavy Rain".equals(weather)) {
            appendLog("[ALERT] Rain detected: Average vehicle speed reduced by 40% across city network.", COLOR_RED);
            updateBriefing("🌧️ Speed Cap: Heavy Rain", 
                "Global weather is Heavy Rain. AI speed bounds are scaled down by 40% (wet-road braking safety margins enforced). Slower congestion clearance is anticipated.", 
                COLOR_YELLOW);
        } else if ("Dense Fog".equals(weather)) {
            appendLog("[ALERT] Fog detected: Speed reduced by 50%. Smog trapping active (AQI increases by 30%).", COLOR_RED);
            updateBriefing("🌫️ Visibility Cap: Dense Fog", 
                "Global weather is Dense Fog. Vehicle speeds are capped at 50% for low-visibility conditions. Local air filtration filters reflect a simulated 30% rise in local AQI levels.", 
                COLOR_YELLOW);
        } else {
            updateBriefing("☀️ Baseline Operations: Sunny", 
                "Global weather is Sunny. All speeds and ambient AQI properties are running at 100% capacity. AI sensors are auditing regular commuter flow rates.", 
                COLOR_GREEN);
        }

        // Reset all components
        for (JunctionCard card : cardsMap.values()) {
            card.resetCard();
        }
        cityMapPanel.resetMap();
        dashboardPanel.resetDashboard();
        junctionIterationMap.clear();

        activeThreads = junctions.length;

        // Spawn a thread for each junction
        for (String junction : junctions) {
            JunctionMonitor monitor = new JunctionMonitor(junction, this);
            Thread thread = new Thread(monitor, "JunctionThread-" + junction);
            thread.start();
        }
    }

    public void updateBriefing(String headline, String explanation, Color color) {
        SwingUtilities.invokeLater(() -> {
            briefingHeadlineLabel.setText(headline);
            briefingHeadlineLabel.setForeground(color);
            briefingTextArea.setText(explanation);
        });
    }

    public synchronized void updateJunctionCard(String name, SensorReading reading, AnomalyResult result, boolean active) {
        JunctionCard card = cardsMap.get(name);
        if (card != null) {
            card.updateData(reading, result, active);
        }
        
        // Update City Map
        if (cityMapPanel != null) {
            boolean hasEmergency = reading != null && !"None".equals(reading.getEmergencyVehicle());
            String state = result != null ? result.getTrafficState() : "Normal";
            cityMapPanel.updateJunctionState(name, state, hasEmergency, active);
        }

        // Update Dashboard & AI Briefing Board
        if (active && reading != null && result != null) {
            int iteration = junctionIterationMap.getOrDefault(name, 0) + 1;
            junctionIterationMap.put(name, iteration);
            dashboardPanel.updateJunctionData(name, iteration, reading.getVehicleCount(), reading.getAqi());
            
            // Dynamically update the AI Briefing Explainer Board to reflect key situations
            boolean hasEmergency = !"None".equals(reading.getEmergencyVehicle());
            if (hasEmergency) {
                updateBriefing("🚨 Siren Override: " + reading.getEmergencyVehicle().toUpperCase() + " @ " + name,
                    "An emergency responder (" + reading.getEmergencyVehicle() + ") has requested priority passage at " + name + ".\n" +
                    "AI System Response: Standard loop suspended. Initiated instant Priority Signal Activation (holding green light) and advising adjacent corridors to clear.",
                    COLOR_BLUE);
            } else {
                String state = result.getTrafficState();
                if ("Severe".equals(state) || "Heavy".equals(state)) {
                    updateBriefing("⚠️ High Density Alert at " + name.toUpperCase(),
                        "Junction " + name + " is congested (" + reading.getVehicleCount() + " vehicles, speed: " + reading.getAverageSpeed() + " km/h).\n" +
                        "AI System Response: Adjusted signal (+" + (state.equals("Severe") ? "45s" : "30s") + " green cycle) and suggesting smart route detour: '" + result.getRouteDiversion() + "'.",
                        COLOR_RED);
                } else if ("Moderate".equals(state) && activeThreads > 0) {
                    // Muted yellow for moderate shifts
                    updateBriefing("🟡 Congestion Prevention: " + name,
                        "Vehicle load is elevating slightly at " + name + ".\n" +
                        "AI System Response: Incrementing green cycle by 15s to keep transit velocity nominal. Trend prediction: " + result.getPredictedState() + ".",
                        COLOR_YELLOW);
                }
            }
        }
    }

    public synchronized void reportThreadFinished(String name) {
        activeThreads--;
        if (activeThreads <= 0) {
            startButton.setEnabled(true);
            weatherComboBox.setEnabled(true);
            statusLabel.setText("Status: Idle");
            statusLabel.setForeground(COLOR_MUTED);
            appendLog("[COMPLETE] All sensors completed simulation loops.", COLOR_BLUE);
            updateBriefing("✅ Operations Audit Complete",
                "All parallel junction simulation loops have finished processing. System is now idle. Review the 'Analytics & Insights Dashboard' tab to inspect aggregate trends.",
                COLOR_GREEN);
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
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            TrafficAnalyzerGUI frame = new TrafficAnalyzerGUI();
            frame.setVisible(true);
        });
    }
}

import javax.swing.*;
import java.awt.*;

public class JunctionCard extends JPanel {
    private final String name;
    private int vehicleCount = 0;
    private int averageSpeed = 0;
    private int aqi = 0;
    private boolean active = false;
    private boolean abnormal = false;
    private String alertText = "";

    // Design Colors
    private static final Color BG_COLOR = new Color(30, 30, 36);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 246);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 170);
    private static final Color TRACK_COLOR = new Color(45, 45, 53);
    
    private static final Color COLOR_GREEN = new Color(16, 185, 129);
    private static final Color COLOR_RED = new Color(239, 68, 68);
    private static final Color COLOR_YELLOW = new Color(245, 158, 11);
    private static final Color COLOR_ORANGE = new Color(249, 115, 22);
    private static final Color COLOR_MUTED = new Color(75, 85, 99);

    public JunctionCard(String name) {
        this.name = name;
        setPreferredSize(new Dimension(240, 180));
        setOpaque(false);
    }

    public synchronized void updateData(SensorReading reading, AnomalyResult result, boolean active) {
        this.active = active;
        if (reading != null) {
            this.vehicleCount = reading.getVehicleCount();
            this.averageSpeed = reading.getAverageSpeed();
            this.aqi = reading.getAqi();
        }
        if (result != null) {
            this.abnormal = result.isAbnormal();
            this.alertText = result.getAlertsString();
        } else {
            this.abnormal = false;
            this.alertText = "";
        }
        repaint();
    }

    public synchronized void setCompleted() {
        this.active = false;
        repaint();
    }

    public synchronized void resetCard() {
        this.vehicleCount = 0;
        this.averageSpeed = 0;
        this.aqi = 0;
        this.active = false;
        this.abnormal = false;
        this.alertText = "";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Turn on high-quality rendering (anti-aliased)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int round = 16;

        // Draw Card Background
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(0, 0, width, height, round, round);

        // Draw Border Highlight
        if (active) {
            if (abnormal) {
                g2.setColor(new Color(239, 68, 68, 100)); // Glowing red border
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, width - 2, height - 2, round, round);
            } else {
                g2.setColor(new Color(16, 185, 129, 80)); // Soft green border
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, width - 2, height - 2, round, round);
            }
        } else {
            g2.setColor(new Color(60, 60, 70));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, round, round);
        }

        // Draw Status Indicator Dot (top right)
        int dotSize = 12;
        int dotX = width - dotSize - 16;
        int dotY = 20;
        
        if (active) {
            if (abnormal) {
                g2.setColor(COLOR_RED);
            } else {
                g2.setColor(COLOR_GREEN);
            }
            // Draw a subtle glow behind active dots
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.fillOval(dotX - 2, dotY - 2, dotSize + 4, dotSize + 4);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            g2.setColor(COLOR_MUTED);
        }
        g2.fillOval(dotX, dotY, dotSize, dotSize);

        // Draw Junction Name
        g2.setColor(TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString(name, 16, 30);

        // Metrics Layout Starting Y
        int startY = 55;
        int rowHeight = 32;

        // 1. Vehicle Metric
        drawMetricRow(g2, "Vehicles", String.valueOf(vehicleCount), vehicleCount, 120, 
                      vehicleCount > 80 ? COLOR_RED : COLOR_GREEN, 16, startY);

        // 2. Speed Metric
        Color speedColor = averageSpeed < 20 ? COLOR_RED : (averageSpeed < 35 ? COLOR_YELLOW : COLOR_GREEN);
        drawMetricRow(g2, "Avg Speed", averageSpeed + " km/h", averageSpeed, 80, 
                      speedColor, 16, startY + rowHeight);

        // 3. AQI Metric
        Color aqiColor = aqi > 150 ? COLOR_RED : (aqi > 100 ? COLOR_ORANGE : (aqi > 50 ? COLOR_YELLOW : COLOR_GREEN));
        drawMetricRow(g2, "AQI Index", String.valueOf(aqi), aqi, 300, 
                      aqiColor, 16, startY + (rowHeight * 2));

        // Draw Alert text or Normal Status at the bottom
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if (active) {
            if (abnormal) {
                g2.setColor(COLOR_RED);
                // Truncate text if it's too long
                String shortAlert = alertText.length() > 32 ? alertText.substring(0, 30) + ".." : alertText;
                g2.drawString(shortAlert, 16, height - 12);
            } else {
                g2.setColor(COLOR_GREEN);
                g2.drawString("✓ All systems nominal", 16, height - 12);
            }
        } else {
            g2.setColor(TEXT_SECONDARY);
            g2.drawString("● Inactive / Finished", 16, height - 12);
        }

        g2.dispose();
    }

    private void drawMetricRow(Graphics2D g2, String label, String valueStr, int val, int maxVal, Color barColor, int x, int y) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(TEXT_SECONDARY);
        g2.drawString(label, x, y);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(TEXT_PRIMARY);
        int valWidth = g2.getFontMetrics().stringWidth(valueStr);
        g2.drawString(valueStr, getWidth() - x - valWidth, y);

        // Draw Bar Track
        int barY = y + 4;
        int barHeight = 6;
        int barWidth = getWidth() - (x * 2);
        
        g2.setColor(TRACK_COLOR);
        g2.fillRoundRect(x, barY, barWidth, barHeight, 4, 4);

        // Draw Bar Fill
        double pct = Math.min(1.0, Math.max(0.0, (double) val / maxVal));
        int fillWidth = (int) (barWidth * pct);
        if (fillWidth > 0) {
            g2.setColor(barColor);
            g2.fillRoundRect(x, barY, fillWidth, barHeight, 4, 4);
        }
    }
}

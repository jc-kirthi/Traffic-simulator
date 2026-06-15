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
    
    // AI indicators
    private String trafficState = "Normal";
    private String signalRecommendation = "Keep default cycle";
    private String predictedState = "Normal Traffic";
    private String routeDiversion = "No diversion needed";
    private String emergencyVehicle = "None";

    // Design Colors
    private static final Color BG_COLOR = new Color(30, 30, 36);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 246);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 170);
    private static final Color TRACK_COLOR = new Color(45, 45, 53);
    
    private static final Color COLOR_GREEN = new Color(16, 185, 129);
    private static final Color COLOR_YELLOW = new Color(245, 158, 11);
    private static final Color COLOR_ORANGE = new Color(249, 115, 22);
    private static final Color COLOR_RED = new Color(239, 68, 68);
    private static final Color COLOR_MUTED = new Color(75, 85, 99);
    
    private static final Color COLOR_BLUE = new Color(59, 130, 246); // Emergency primary
    private static final Color COLOR_EMERGENCY_BG = new Color(239, 68, 68, 25); // Transparent red

    public JunctionCard(String name) {
        this.name = name;
        setPreferredSize(new Dimension(280, 210));
        setOpaque(false);
    }

    public synchronized void updateData(SensorReading reading, AnomalyResult result, boolean active) {
        this.active = active;
        if (reading != null) {
            this.vehicleCount = reading.getVehicleCount();
            this.averageSpeed = reading.getAverageSpeed();
            this.aqi = reading.getAqi();
            this.emergencyVehicle = reading.getEmergencyVehicle();
        } else {
            this.emergencyVehicle = "None";
        }
        
        if (result != null) {
            this.abnormal = result.isAbnormal();
            this.alertText = result.getAlertsString();
            this.trafficState = result.getTrafficState();
            this.signalRecommendation = result.getSignalRecommendation();
            this.predictedState = result.getPredictedState();
            this.routeDiversion = result.getRouteDiversion();
        } else {
            this.abnormal = false;
            this.alertText = "";
            this.trafficState = "Normal";
            this.signalRecommendation = "Keep default cycle";
            this.predictedState = "Normal Traffic";
            this.routeDiversion = "No diversion needed";
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
        this.trafficState = "Normal";
        this.signalRecommendation = "Keep default cycle";
        this.predictedState = "Normal Traffic";
        this.routeDiversion = "No diversion needed";
        this.emergencyVehicle = "None";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int round = 16;

        boolean hasEmergency = !"None".equals(emergencyVehicle) && active;

        // Draw Card Background
        if (hasEmergency) {
            g2.setColor(new Color(25, 30, 48)); // Slight blue tint for emergency
        } else {
            g2.setColor(BG_COLOR);
        }
        g2.fillRoundRect(0, 0, width, height, round, round);

        // Draw Border Highlight
        if (active) {
            if (hasEmergency) {
                // Flashing red/blue border
                boolean flash = (System.currentTimeMillis() / 400) % 2 == 0;
                g2.setColor(flash ? COLOR_BLUE : COLOR_RED);
                g2.setStroke(new BasicStroke(2.5f));
            } else if (abnormal) {
                g2.setColor(COLOR_RED); // Glowing red border
                g2.setStroke(new BasicStroke(2.0f));
            } else if ("Moderate".equals(trafficState)) {
                g2.setColor(COLOR_YELLOW);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(new Color(16, 185, 129, 120)); // Soft green border
                g2.setStroke(new BasicStroke(1.5f));
            }
            g2.drawRoundRect(1, 1, width - 2, height - 2, round, round);
        } else {
            g2.setColor(new Color(60, 60, 70));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, round, round);
        }

        // Draw Junction Name
        g2.setColor(TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString(name, 16, 28);

        // Draw Status Badge (top right)
        if (active) {
            String badgeText = trafficState.toUpperCase();
            Color badgeBg = COLOR_GREEN;
            
            if (hasEmergency) {
                badgeText = "PRIORITY";
                badgeBg = COLOR_BLUE;
            } else if ("Severe".equals(trafficState)) {
                badgeText = "SEVERE";
                badgeBg = COLOR_RED;
            } else if ("Heavy".equals(trafficState)) {
                badgeText = "HEAVY";
                badgeBg = COLOR_RED;
            } else if ("Moderate".equals(trafficState)) {
                badgeText = "MODERATE";
                badgeBg = COLOR_YELLOW;
            }
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            int bTextWidth = g2.getFontMetrics().stringWidth(badgeText);
            int badgeW = bTextWidth + 12;
            int badgeH = 18;
            int bx = width - badgeW - 16;
            int by = 13;
            
            g2.setColor(badgeBg);
            g2.fillRoundRect(bx, by, badgeW, badgeH, 6, 6);
            
            g2.setColor(Color.WHITE);
            g2.drawString(badgeText, bx + 6, by + 12);
        } else {
            // Draw Inactive dot
            g2.setColor(COLOR_MUTED);
            g2.fillOval(width - 24, 16, 10, 10);
        }

        // Metrics Layout Starting Y
        int startY = 46;
        int rowHeight = 25;

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

        // Draw AI / Intelligence Panel at the bottom
        int aiY = startY + (rowHeight * 3) + 4;
        
        if (active) {
            if (hasEmergency) {
                // Emergency Panel Highlight
                g2.setColor(COLOR_EMERGENCY_BG);
                g2.fillRoundRect(16, aiY - 2, width - 32, 50, 8, 8);
                g2.setColor(COLOR_RED);
                g2.drawRoundRect(16, aiY - 2, width - 32, 50, 8, 8);
                
                // Pulsing light indicator
                boolean flash = (System.currentTimeMillis() / 400) % 2 == 0;
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(flash ? COLOR_RED : Color.WHITE);
                g2.drawString("🚨 EMERGENCY DETECTED: " + emergencyVehicle.toUpperCase(), 24, aiY + 14);
                
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(TEXT_PRIMARY);
                g2.drawString("Action: " + signalRecommendation, 24, aiY + 32);
            } else {
                // Regular AI panel: prediction and recommendation
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(TEXT_SECONDARY);
                g2.drawString("🔮 Predict (15m): ", 16, aiY + 12);
                
                // Color prediction based on severity
                Color predColor = COLOR_GREEN;
                if (predictedState.contains("Severe")) predColor = COLOR_RED;
                else if (predictedState.contains("Heavy")) predColor = COLOR_RED;
                else if (predictedState.contains("Moderate")) predColor = COLOR_YELLOW;
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(predColor);
                g2.drawString(predictedState.replace(" in 15 mins", ""), 98, aiY + 12);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(TEXT_SECONDARY);
                g2.drawString("🚦 AI Signal Rec: ", 16, aiY + 28);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(TEXT_PRIMARY);
                g2.drawString(signalRecommendation, 98, aiY + 28);
                
                // Diversion recommendation if applicable
                if (!"No diversion needed".equals(routeDiversion)) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(TEXT_SECONDARY);
                    g2.drawString("➡️ Smart Detour: ", 16, aiY + 44);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.setColor(COLOR_BLUE);
                    // Truncate diversion text if too long
                    String diversionDisp = routeDiversion;
                    if (diversionDisp.length() > 22) {
                        diversionDisp = diversionDisp.substring(0, 20) + "..";
                    }
                    g2.drawString(diversionDisp, 98, aiY + 44);
                }
            }
            
            // Bottom-most minor status line
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            if (abnormal && !hasEmergency) {
                g2.setColor(COLOR_RED);
                String shortAlert = alertText.length() > 38 ? alertText.substring(0, 36) + ".." : alertText;
                g2.drawString(shortAlert, 16, height - 8);
            } else if (!hasEmergency) {
                g2.setColor(COLOR_GREEN);
                g2.drawString("✓ All systems nominal", 16, height - 8);
            }
        } else {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(TEXT_SECONDARY);
            g2.drawString("● Inactive / Finished", 16, height - 10);
        }

        g2.dispose();
    }

    private void drawMetricRow(Graphics2D g2, String label, String valueStr, int val, int maxVal, Color barColor, int x, int y) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(TEXT_SECONDARY);
        g2.drawString(label, x, y);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(TEXT_PRIMARY);
        int valWidth = g2.getFontMetrics().stringWidth(valueStr);
        g2.drawString(valueStr, getWidth() - x - valWidth, y);

        // Draw Bar Track
        int barY = y + 4;
        int barHeight = 5;
        int barWidth = getWidth() - (x * 2);
        
        g2.setColor(TRACK_COLOR);
        g2.fillRoundRect(x, barY, barWidth, barHeight, 3, 3);

        // Draw Bar Fill
        double pct = Math.min(1.0, Math.max(0.0, (double) val / maxVal));
        int fillWidth = (int) (barWidth * pct);
        if (fillWidth > 0) {
            g2.setColor(barColor);
            g2.fillRoundRect(x, barY, fillWidth, barHeight, 3, 3);
        }
    }
}

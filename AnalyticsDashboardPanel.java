import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsDashboardPanel extends JPanel {

    private final String[] junctions = {
        "Silk Board", "KR Puram", "Hebbal", "Marathahalli", "Electronic City"
    };

    // Live data structures
    private final Map<String, int[]> vehicleHistory = new HashMap<>();
    private final Map<String, Integer> currentAqi = new HashMap<>();

    // Colors
    private static final Color BG_DARK = new Color(18, 18, 20);
    private static final Color CARD_BG = new Color(24, 24, 28);
    private static final Color GRID_COLOR = new Color(45, 45, 53);
    private static final Color BORDER_COLOR = new Color(63, 63, 70);

    private static final Color COLOR_TEXT_LIGHT = new Color(244, 244, 245);
    private static final Color COLOR_MUTED = new Color(160, 160, 170);

    private static final Color GRAPH_PURPLE = new Color(139, 92, 246);
    private static final Color GRAPH_INDIGO = new Color(99, 102, 241);
    private static final Color GRAPH_TEAL = new Color(20, 184, 166);
    private static final Color GRAPH_PINK = new Color(236, 72, 153);

    private static final Color COLOR_GREEN = new Color(16, 185, 129);
    private static final Color COLOR_YELLOW = new Color(245, 158, 11);
    private static final Color COLOR_ORANGE = new Color(249, 115, 22);
    private static final Color COLOR_RED = new Color(239, 68, 68);

    public AnalyticsDashboardPanel() {
        setBackground(BG_DARK);
        setLayout(new GridLayout(2, 2, 16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        resetDashboard();
    }

    public synchronized void resetDashboard() {
        vehicleHistory.clear();
        currentAqi.clear();
        for (String j : junctions) {
            vehicleHistory.put(j, new int[5]); // 5 simulation steps
            currentAqi.put(j, 0);
        }
        repaint();
    }

    public synchronized void updateJunctionData(String junction, int iteration, int vehicles, int aqi) {
        // iteration is 1-indexed (1 to 5) from JunctionMonitor
        int idx = Math.min(4, Math.max(0, iteration - 1));
        int[] history = vehicleHistory.get(junction);
        if (history != null) {
            history[idx] = vehicles;
        }
        currentAqi.put(junction, aqi);
        repaint();
    }

    @Override
    protected void paintChildren(Graphics g) {
        // Override paintChildren or use custom components. Since we want complete control,
        // we can draw the 4 graphs as standard JPanel children, or paint them directly.
        // Painting them directly gives us the ultimate design freedom and avoids complex component hierarchies.
        // Let's paint them directly! We will clear the layout components, and draw them.
        super.paintChildren(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 16;

        int cardW = (w - (margin * 3)) / 2;
        int cardH = (h - (margin * 3)) / 2;

        // Draw Card 1: Traffic Volume Trend (Top-Left)
        drawTrafficTrendCard(g2, margin, margin, cardW, cardH);

        // Draw Card 2: AQI Comparison (Top-Right)
        drawAqiCard(g2, margin * 2 + cardW, margin, cardW, cardH);

        // Draw Card 3: Peak Hours Congestion (Bottom-Left)
        drawPeakHoursCard(g2, margin, margin * 2 + cardH, cardW, cardH);

        // Draw Card 4: Vehicle Growth Projection (Bottom-Right)
        drawVehicleGrowthCard(g2, margin * 2 + cardW, margin * 2 + cardH, cardW, cardH);

        g2.dispose();
    }

    private synchronized void drawTrafficTrendCard(Graphics2D g2, int x, int y, int w, int h) {
        drawCardFrame(g2, x, y, w, h, "Traffic Volume Trend", "Average vehicle count per simulation cycle");

        int graphX = x + 40;
        int graphY = y + 60;
        int graphW = w - 60;
        int graphH = h - 90;

        // Draw grid
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i <= 4; i++) {
            int gy = graphY + (graphH * i / 4);
            g2.drawLine(graphX, gy, graphX + graphW, gy);
            // Grid labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(COLOR_MUTED);
            int val = 100 - (25 * i);
            g2.drawString(String.valueOf(val), x + 15, gy + 4);
        }

        // Calculate average vehicles per iteration
        double[] avgs = new double[5];
        boolean hasData = false;
        for (int step = 0; step < 5; step++) {
            double sum = 0;
            int count = 0;
            for (String j : junctions) {
                int val = vehicleHistory.get(j)[step];
                if (val > 0) {
                    sum += val;
                    count++;
                }
            }
            avgs[step] = count > 0 ? (sum / count) : 0;
            if (avgs[step] > 0) {
                hasData = true;
            }
        }

        if (!hasData) {
            // Draw placeholder text
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(COLOR_MUTED);
            g2.drawString("Waiting for simulation data...", graphX + graphW/2 - 75, graphY + graphH/2);
            return;
        }

        // Plot data
        int[] px = new int[5];
        int[] py = new int[5];
        for (int i = 0; i < 5; i++) {
            px[i] = graphX + (graphW * i / 4);
            // Scale vehicle count (0 to 120 max)
            double val = avgs[i];
            double pct = val / 120.0;
            py[i] = graphY + graphH - (int) (graphH * pct);
        }

        // Draw filled Area under the line with gradient
        GeneralPath area = new GeneralPath();
        area.moveTo(px[0], graphY + graphH);
        for (int i = 0; i < 5; i++) {
            area.lineTo(px[i], py[i]);
        }
        area.lineTo(px[4], graphY + graphH);
        area.closePath();

        GradientPaint areaGrad = new GradientPaint(
            graphX, graphY, new Color(99, 102, 241, 100),
            graphX, graphY + graphH, new Color(99, 102, 241, 0)
        );
        g2.setPaint(areaGrad);
        g2.fill(area);

        // Draw Line
        g2.setColor(GRAPH_INDIGO);
        g2.setStroke(new BasicStroke(2.5f));
        for (int i = 0; i < 4; i++) {
            g2.drawLine(px[i], py[i], px[i+1], py[i+1]);
        }

        // Draw Dots and values
        for (int i = 0; i < 5; i++) {
            g2.setColor(Color.WHITE);
            g2.fillOval(px[i] - 4, py[i] - 4, 8, 8);
            g2.setColor(GRAPH_INDIGO);
            g2.drawOval(px[i] - 4, py[i] - 4, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.setColor(COLOR_TEXT_LIGHT);
            String valStr = String.format("%.1f", avgs[i]);
            g2.drawString(valStr, px[i] - 12, py[i] - 10);
            
            // X axis labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(COLOR_MUTED);
            g2.drawString("Step " + (i + 1), px[i] - 12, graphY + graphH + 16);
        }
    }

    private synchronized void drawAqiCard(Graphics2D g2, int x, int y, int w, int h) {
        drawCardFrame(g2, x, y, w, h, "AQI Comparison", "Current Air Quality Index per junction");

        int graphX = x + 50;
        int graphY = y + 60;
        int graphW = w - 70;
        int graphH = h - 90;

        // Draw grid
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i <= 3; i++) {
            int gy = graphY + (graphH * i / 3);
            g2.drawLine(graphX, gy, graphX + graphW, gy);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(COLOR_MUTED);
            int val = 300 - (100 * i);
            g2.drawString(String.valueOf(val), x + 20, gy + 4);
        }

        // Draw Bars
        int barW = Math.max(15, (graphW / 5) - 16);
        int barGap = (graphW - (barW * 5)) / 6;

        for (int i = 0; i < 5; i++) {
            String junction = junctions[i];
            int aqi = currentAqi.getOrDefault(junction, 0);

            int bx = graphX + barGap + i * (barW + barGap);
            double pct = aqi / 300.0;
            int barH = (int) (graphH * pct);
            int by = graphY + graphH - barH;

            // Bar Color based on AQI
            Color barColor = COLOR_GREEN;
            if (aqi > 150) barColor = COLOR_RED;
            else if (aqi > 100) barColor = COLOR_ORANGE;
            else if (aqi > 50) barColor = COLOR_YELLOW;

            if (aqi > 0) {
                // Gradient bar
                GradientPaint barGrad = new GradientPaint(
                    bx, by, barColor,
                    bx, graphY + graphH, barColor.darker()
                );
                g2.setPaint(barGrad);
                g2.fillRoundRect(bx, by, barW, barH, 4, 4);

                // Draw AQI value text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(COLOR_TEXT_LIGHT);
                g2.drawString(String.valueOf(aqi), bx + (barW/2) - 8, by - 6);
            } else {
                // Draw zero baseline
                g2.setColor(GRID_COLOR);
                g2.fillRect(bx, graphY + graphH - 2, barW, 2);
            }

            // Junction Labels (Truncated if necessary)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(COLOR_MUTED);
            String label = junction.length() > 8 ? junction.substring(0, 7) + "." : junction;
            int lWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, bx + (barW/2) - (lWidth/2), graphY + graphH + 16);
        }
    }

    private void drawPeakHoursCard(Graphics2D g2, int x, int y, int w, int h) {
        drawCardFrame(g2, x, y, w, h, "Peak Hours Congestion Profile", "Hourly average traffic density in metropolitan area");

        int graphX = x + 40;
        int graphY = y + 60;
        int graphW = w - 60;
        int graphH = h - 90;

        // Draw grid
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i <= 4; i++) {
            int gy = graphY + (graphH * i / 4);
            g2.drawLine(graphX, gy, graphX + graphW, gy);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(COLOR_MUTED);
            g2.drawString((100 - i * 25) + "%", x + 15, gy + 3);
        }

        // Draw Curve representing typical day
        // 6 AM to 10 PM (16 hours)
        // Peak hours: 9 AM-11 AM (x=3 to 5), 5 PM-8 PM (x=11 to 14)
        int steps = 20;
        int[] px = new int[steps];
        int[] py = new int[steps];
        
        for (int i = 0; i < steps; i++) {
            double pctX = (double) i / (steps - 1);
            px[i] = graphX + (int) (graphW * pctX);
            
            // Generate peak curve: y = f(x)
            double t = pctX * 16 + 6; // 6 AM to 10 PM
            double val = 15; // baseline traffic
            // Add morning peak centered at 9:30 AM
            val += 65 * Math.exp(-Math.pow((t - 9.5)/1.8, 2));
            // Add evening peak centered at 6:30 PM
            val += 75 * Math.exp(-Math.pow((t - 18.5)/2.2, 2));
            val = Math.min(95, val);

            py[i] = graphY + graphH - (int) (graphH * val / 100.0);
        }

        // Draw Area under spline
        GeneralPath area = new GeneralPath();
        area.moveTo(px[0], graphY + graphH);
        for (int i = 0; i < steps; i++) {
            area.lineTo(px[i], py[i]);
        }
        area.lineTo(px[steps-1], graphY + graphH);
        area.closePath();

        GradientPaint areaGrad = new GradientPaint(
            graphX, graphY, new Color(20, 184, 166, 80),
            graphX, graphY + graphH, new Color(20, 184, 166, 0)
        );
        g2.setPaint(areaGrad);
        g2.fill(area);

        // Draw Curve Line
        g2.setColor(GRAPH_TEAL);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < steps - 1; i++) {
            g2.drawLine(px[i], py[i], px[i+1], py[i+1]);
        }

        // Draw markers for 9 AM and 6 PM
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        g2.setColor(COLOR_MUTED);
        
        // 9 AM
        int m9x = graphX + (int)(graphW * 3.0 / 16.0);
        g2.drawLine(m9x, graphY, m9x, graphY + graphH);
        g2.drawString("9 AM Peak", m9x - 18, graphY - 4);
        
        // 6 PM
        int m6x = graphX + (int)(graphW * 12.0 / 16.0);
        g2.drawLine(m6x, graphY, m6x, graphY + graphH);
        g2.drawString("6 PM Peak", m6x - 18, graphY - 4);

        // X labels
        String[] hours = {"6AM", "10AM", "2PM", "6PM", "10PM"};
        for (int i = 0; i < hours.length; i++) {
            int hx = graphX + (graphW * i / 4);
            g2.drawString(hours[i], hx - 10, graphY + graphH + 14);
        }
    }

    private void drawVehicleGrowthCard(Graphics2D g2, int x, int y, int w, int h) {
        drawCardFrame(g2, x, y, w, h, "Vehicle Growth & SDG 9 Projection", "Cumulative registered vehicles in city (in Lakhs)");

        int graphX = x + 40;
        int graphY = y + 60;
        int graphW = w - 60;
        int graphH = h - 90;

        // Draw grid
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i <= 3; i++) {
            int gy = graphY + (graphH * i / 3);
            g2.drawLine(graphX, gy, graphX + graphW, gy);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(COLOR_MUTED);
            int val = 150 - (i * 50);
            g2.drawString(val + "L", x + 15, gy + 3);
        }

        // Draw projection data
        // Year 2020: 80L, 2022: 95L, 2024: 110L, 2026: 125L, 2028: 140L (projected), 2030: 155L (projected)
        int[] years = {2020, 2022, 2024, 2026, 2028, 2030};
        double[] growth = {80.0, 92.0, 108.0, 125.0, 142.0, 160.0};
        
        int[] px = new int[6];
        int[] py = new int[6];
        for (int i = 0; i < 6; i++) {
            px[i] = graphX + (graphW * i / 5);
            double val = growth[i];
            double pct = val / 180.0; // scale to 180L max
            py[i] = graphY + graphH - (int) (graphH * pct);
        }

        // Draw Line - Historical (Solid) and Projected (Dashed)
        g2.setStroke(new BasicStroke(2.0f));
        
        // 2020-2026 Solid
        g2.setColor(GRAPH_PINK);
        for (int i = 0; i < 3; i++) {
            g2.drawLine(px[i], py[i], px[i+1], py[i+1]);
        }

        // 2026-2030 Dashed (Projection)
        float[] dash = {4.0f, 4.0f};
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        for (int i = 3; i < 5; i++) {
            g2.drawLine(px[i], py[i], px[i+1], py[i+1]);
        }

        // Restore solid stroke
        g2.setStroke(new BasicStroke(1.0f));

        // Draw Dots
        for (int i = 0; i < 6; i++) {
            g2.setColor(i < 4 ? GRAPH_PINK : Color.LIGHT_GRAY);
            g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(COLOR_MUTED);
            g2.drawString(String.valueOf(years[i]), px[i] - 10, graphY + graphH + 14);
            
            // draw value label
            g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
            g2.setColor(COLOR_TEXT_LIGHT);
            g2.drawString((int)growth[i] + "L", px[i] - 8, py[i] - 8);
        }

        // Add a small badge label indicating "AI Prediction Era"
        g2.setColor(new Color(236, 72, 153, 30));
        g2.fillRoundRect(px[3], graphY + 10, graphW - (px[3] - graphX) - 5, 20, 4, 4);
        g2.setColor(GRAPH_PINK);
        g2.drawRoundRect(px[3], graphY + 10, graphW - (px[3] - graphX) - 5, 20, 4, 4);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2.drawString("AI INFRA ERA", px[3] + 8, graphY + 23);
    }

    private void drawCardFrame(Graphics2D g2, int x, int y, int w, int h, String title, String subtitle) {
        // Card background
        g2.setColor(CARD_BG);
        g2.fillRoundRect(x, y, w, h, 16, 16);

        // Border
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(x, y, w - 1, h - 1, 16, 16);

        // Header Title
        g2.setColor(COLOR_TEXT_LIGHT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString(title, x + 16, y + 25);

        // Header Subtitle
        g2.setColor(COLOR_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString(subtitle, x + 16, y + 38);
    }
}

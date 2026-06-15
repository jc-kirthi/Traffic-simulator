import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CityMapPanel extends JPanel {
    
    // Node representation for junctions
    private static class MapNode {
        String name;
        int x, y;
        Color currentColor = COLOR_MUTED;
        boolean isEmergency = false;
        
        MapNode(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
    
    private final Map<String, MapNode> nodes = new HashMap<>();
    private boolean flashState = false;
    private Timer flashTimer;

    // Design Colors
    private static final Color BG_COLOR = new Color(24, 24, 28);
    private static final Color BORDER_COLOR = new Color(63, 63, 70);
    private static final Color ROAD_COLOR = new Color(45, 45, 53);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 246);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 170);

    private static final Color COLOR_GREEN = new Color(16, 185, 129);
    private static final Color COLOR_YELLOW = new Color(245, 158, 11);
    private static final Color COLOR_RED = new Color(239, 68, 68);
    private static final Color COLOR_MUTED = new Color(75, 85, 99);
    private static final Color COLOR_BLUE = new Color(59, 130, 246);

    public CityMapPanel() {
        setPreferredSize(new Dimension(280, 220));
        setOpaque(false);
        setupNodes();
        startFlashingAnimation();
    }
    
    private void setupNodes() {
        // Staggered layout coordinates to fit organically within the card
        nodes.put("Hebbal", new MapNode("Hebbal", 120, 60));
        nodes.put("KR Puram", new MapNode("KR Puram", 220, 95));
        nodes.put("Marathahalli", new MapNode("Marathahalli", 210, 160));
        nodes.put("Silk Board", new MapNode("Silk Board", 100, 200));
        nodes.put("Electronic City", new MapNode("Electronic City", 110, 260));
    }
    
    private void startFlashingAnimation() {
        // Repaint every 400ms to allow emergency nodes to flash
        flashTimer = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flashState = !flashState;
                boolean hasEmergency = false;
                for (MapNode node : nodes.values()) {
                    if (node.isEmergency) {
                        hasEmergency = true;
                        break;
                    }
                }
                if (hasEmergency) {
                    repaint();
                }
            }
        });
        flashTimer.start();
    }
    
    public synchronized void updateJunctionState(String name, String trafficState, boolean hasEmergency, boolean active) {
        MapNode node = nodes.get(name);
        if (node != null) {
            node.isEmergency = active && hasEmergency;
            if (!active) {
                node.currentColor = COLOR_MUTED;
            } else {
                switch (trafficState) {
                    case "Severe":
                        node.currentColor = COLOR_RED;
                        break;
                    case "Heavy":
                        node.currentColor = COLOR_RED;
                        break;
                    case "Moderate":
                        node.currentColor = COLOR_YELLOW;
                        break;
                    case "Normal":
                    default:
                        node.currentColor = COLOR_GREEN;
                }
            }
        }
        repaint();
    }
    
    public synchronized void resetMap() {
        for (MapNode node : nodes.values()) {
            node.currentColor = COLOR_MUTED;
            node.isEmergency = false;
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int round = 16;
        
        // Background
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(0, 0, w, h, round, round);
        
        // Border
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, round, round);
        
        // Title
        g2.setColor(TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("City-Wide Heat Map", 16, 25);
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Real-Time Arterial Congestion", 16, 38);
        
        // Draw road network
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(ROAD_COLOR);
        
        // Connections
        MapNode hebbal = nodes.get("Hebbal");
        MapNode krpuram = nodes.get("KR Puram");
        MapNode marathahalli = nodes.get("Marathahalli");
        MapNode silkboard = nodes.get("Silk Board");
        MapNode ecity = nodes.get("Electronic City");
        
        // Draw main roads
        g2.drawLine(hebbal.x, hebbal.y, krpuram.x, krpuram.y); // ORR North
        g2.drawLine(krpuram.x, krpuram.y, marathahalli.x, marathahalli.y); // ORR East
        g2.drawLine(marathahalli.x, marathahalli.y, silkboard.x, silkboard.y); // ORR South
        g2.drawLine(silkboard.x, silkboard.y, ecity.x, ecity.y); // Hosur Rd / Flyover
        g2.drawLine(hebbal.x, hebbal.y, silkboard.x, silkboard.y); // Central Bypass (Cunningham/Hosur link)

        // Draw glowing road highlights (colored based on connected nodes congestion)
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawRoadHighlight(g2, hebbal, krpuram);
        drawRoadHighlight(g2, krpuram, marathahalli);
        drawRoadHighlight(g2, marathahalli, silkboard);
        drawRoadHighlight(g2, silkboard, ecity);
        drawRoadHighlight(g2, hebbal, silkboard);
        
        // Road Labels
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 8));
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("ORR East", 175, 120);
        g2.drawString("Hosur Flyover", 65, 235);
        
        // Draw Nodes
        int nodeSize = 16;
        for (MapNode node : nodes.values()) {
            Color nodeColor = node.currentColor;
            
            if (node.isEmergency) {
                // Siren effect: flash between Blue and Red
                nodeColor = flashState ? COLOR_BLUE : COLOR_RED;
            }
            
            // Node Glow
            if (nodeColor != COLOR_MUTED) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2.setColor(nodeColor);
                g2.fillOval(node.x - (nodeSize/2) - 4, node.y - (nodeSize/2) - 4, nodeSize + 8, nodeSize + 8);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            
            // Solid Center
            g2.setColor(nodeColor);
            g2.fillOval(node.x - (nodeSize/2), node.y - (nodeSize/2), nodeSize, nodeSize);
            
            // White outline for active nodes
            if (nodeColor != COLOR_MUTED) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(node.x - (nodeSize/2), node.y - (nodeSize/2), nodeSize, nodeSize);
            }
            
            // Node label
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.setColor(TEXT_PRIMARY);
            int labelWidth = g2.getFontMetrics().stringWidth(node.name);
            int lx = node.x - (labelWidth / 2);
            int ly = node.y - (nodeSize / 2) - 4;
            
            // Adjust label positioning so they don't overlap edges
            if (node.name.equals("Hebbal")) { ly = node.y + (nodeSize / 2) + 12; }
            if (node.name.equals("Silk Board")) { lx = node.x - labelWidth - 8; ly = node.y + 4; }
            if (node.name.equals("KR Puram")) { lx = node.x + 10; ly = node.y + 4; }
            if (node.name.equals("Marathahalli")) { lx = node.x + 10; ly = node.y + 4; }
            if (node.name.equals("Electronic City")) { ly = node.y + (nodeSize / 2) + 12; }

            g2.drawString(node.name, lx, ly);
        }
        
        // Draw Map Legend
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        int lx = 16;
        int ly = h - 22;
        
        g2.setColor(COLOR_GREEN);
        g2.fillOval(lx, ly, 6, 6);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Nominal", lx + 10, ly + 6);
        
        lx += 50;
        g2.setColor(COLOR_YELLOW);
        g2.fillOval(lx, ly, 6, 6);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Moderate", lx + 10, ly + 6);
        
        lx += 52;
        g2.setColor(COLOR_RED);
        g2.fillOval(lx, ly, 6, 6);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Severe", lx + 10, ly + 6);

        lx += 45;
        // Draw emergency indicator (dual blue/red dot)
        g2.setColor(COLOR_BLUE);
        g2.fillArc(lx, ly, 6, 6, 90, 180);
        g2.setColor(COLOR_RED);
        g2.fillArc(lx, ly, 6, 6, 270, 180);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Priority", lx + 10, ly + 6);
        
        g2.dispose();
    }
    
    private void drawRoadHighlight(Graphics2D g2, MapNode n1, MapNode n2) {
        if (n1.currentColor == COLOR_MUTED || n2.currentColor == COLOR_MUTED) {
            return;
        }
        // If either connected node is red, the road is colored yellow/red
        if (n1.currentColor == COLOR_RED || n2.currentColor == COLOR_RED) {
            g2.setColor(new Color(239, 68, 68, 120)); // Transparent red
        } else if (n1.currentColor == COLOR_YELLOW || n2.currentColor == COLOR_YELLOW) {
            g2.setColor(new Color(245, 158, 11, 120)); // Transparent yellow
        } else {
            g2.setColor(new Color(16, 185, 129, 120)); // Transparent green
        }
        g2.drawLine(n1.x, n1.y, n2.x, n2.y);
    }
    
    // Clean up timer if panel removed
    public void cleanup() {
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
        }
    }
}

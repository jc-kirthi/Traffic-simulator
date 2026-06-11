import javax.swing.*;
import java.util.Random;

public class JunctionMonitor implements Runnable {
    private final String junctionName;
    private final TrafficAnalyzerGUI gui;
    private final Random random = new Random();

    public JunctionMonitor(String junctionName, TrafficAnalyzerGUI gui) {
        this.junctionName = junctionName;
        this.gui = gui;
    }

    @Override
    public void run() {
        // Output start to stdout
        System.out.println("[START] Sensor active: " + junctionName);
        
        // Update GUI with initial active status
        SwingUtilities.invokeLater(() -> gui.appendLog("[START] Sensor active: " + junctionName, TrafficAnalyzerGUI.COLOR_BLUE));

        for (int i = 1; i <= 5; i++) {
            try {
                // Sleep to simulate time between sensor readings and allow interleaving
                Thread.sleep(800 + random.nextInt(700)); // 800ms to 1500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // Generate realistic readings based on specific junction profiles
            SensorReading reading = generateReadingForJunction();

            // Run anomaly detection pipeline (AI logic)
            AnomalyResult result = AnomalyDetector.analyze(junctionName, reading);

            // Log output to stdout in requested format
            printLogToConsole(reading, result);

            // Update GUI
            final SensorReading finalReading = reading;
            final AnomalyResult finalResult = result;
            SwingUtilities.invokeLater(() -> {
                gui.updateJunctionCard(junctionName, finalReading, finalResult, true);
                if (finalResult.isAbnormal()) {
                    gui.appendLog("[ALERT] " + junctionName + " → " + finalResult.getAlertsString(), TrafficAnalyzerGUI.COLOR_RED);
                } else {
                    gui.appendLog("[NORMAL] " + junctionName + " → " + finalReading.toString(), TrafficAnalyzerGUI.COLOR_GREEN);
                }
            });
        }

        // Complete monitoring
        System.out.println("[DONE] " + junctionName + " sensor finished");
        SwingUtilities.invokeLater(() -> {
            gui.updateJunctionCard(junctionName, null, null, false);
            gui.appendLog("[DONE] " + junctionName + " sensor finished", TrafficAnalyzerGUI.COLOR_MUTED);
            gui.reportThreadFinished(junctionName);
        });
    }

    private SensorReading generateReadingForJunction() {
        int vehicles, speed, aqi;

        switch (junctionName) {
            case "Silk Board":
                // Heavy traffic, slow speed, average air quality
                vehicles = 60 + random.nextInt(55); // 60 to 115
                speed = 5 + random.nextInt(20);     // 5 to 24 km/h
                aqi = 80 + random.nextInt(100);    // 80 to 179
                break;
            case "KR Puram":
                // Moderate-high traffic, slow-medium speed, poor air quality
                vehicles = 50 + random.nextInt(55); // 50 to 105
                speed = 10 + random.nextInt(25);    // 10 to 34 km/h
                aqi = 100 + random.nextInt(120);   // 100 to 219
                break;
            case "Hebbal":
                // Moderate traffic, medium-fast speed, average air quality
                vehicles = 40 + random.nextInt(45); // 40 to 85
                speed = 25 + random.nextInt(35);    // 25 to 59 km/h
                aqi = 70 + random.nextInt(90);     // 70 to 159
                break;
            case "Marathahalli":
                // High air quality issues, moderate traffic, medium speed
                vehicles = 45 + random.nextInt(50); // 45 to 95
                speed = 15 + random.nextInt(30);    // 15 to 44 km/h
                aqi = 120 + random.nextInt(130);   // 120 to 249
                break;
            case "Electronic City":
                // Generally normal traffic, high speed, clean air
                vehicles = 20 + random.nextInt(45); // 20 to 65
                speed = 40 + random.nextInt(35);    // 40 to 74 km/h
                aqi = 40 + random.nextInt(70);     // 40 to 109
                break;
            default:
                vehicles = 30 + random.nextInt(60);
                speed = 20 + random.nextInt(40);
                aqi = 50 + random.nextInt(100);
        }

        return new SensorReading(vehicles, speed, aqi);
    }

    private void printLogToConsole(SensorReading reading, AnomalyResult result) {
        if (result.isAbnormal()) {
            System.out.println("[ALERT] " + junctionName + " → " + result.getAlertsString());
        } else {
            System.out.println("[NORMAL] " + junctionName + " → " + reading.toString());
        }
    }
}

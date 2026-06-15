import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JunctionMonitor implements Runnable {
    private final String junctionName;
    private final TrafficAnalyzerGUI gui;
    private final Random random = new Random();
    private final List<SensorReading> history = new ArrayList<>();

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
                // Slowed down simulation loop to help audience follow the live analytics
                Thread.sleep(2500 + random.nextInt(1500)); // 2500ms to 4000ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // 1. Generate reading: first step is base, subsequent steps drift gradually
            int vehicles, speed, aqi;
            if (history.isEmpty()) {
                SensorReading baseReading = generateReadingForJunction();
                vehicles = baseReading.getVehicleCount();
                speed = baseReading.getAverageSpeed();
                aqi = baseReading.getAqi();
            } else {
                SensorReading lastReading = history.get(history.size() - 1);
                int prevVehicles = lastReading.getVehicleCount();
                int prevSpeed = lastReading.getAverageSpeed();
                int prevAqi = lastReading.getAqi();

                // Small incremental variations to look like gradual traffic progression
                int vDrift = random.nextInt(15) - 6; // -6 to +8 vehicles
                int sDrift = random.nextInt(9) - 4;  // -4 to +4 km/h
                int aDrift = random.nextInt(19) - 8; // -8 to +10 AQI

                int rawV = prevVehicles + vDrift;
                int rawS = prevSpeed + sDrift;
                int rawA = prevAqi + aDrift;

                // Clamp to realistic bounds based on junction profile
                switch (junctionName) {
                    case "Silk Board":
                        vehicles = Math.max(50, Math.min(120, rawV));
                        speed = Math.max(5, Math.min(30, rawS));
                        aqi = Math.max(80, Math.min(200, rawA));
                        break;
                    case "KR Puram":
                        vehicles = Math.max(45, Math.min(115, rawV));
                        speed = Math.max(8, Math.min(35, rawS));
                        aqi = Math.max(90, Math.min(230, rawA));
                        break;
                    case "Hebbal":
                        vehicles = Math.max(30, Math.min(90, rawV));
                        speed = Math.max(20, Math.min(60, rawS));
                        aqi = Math.max(60, Math.min(160, rawA));
                        break;
                    case "Marathahalli":
                        vehicles = Math.max(40, Math.min(100, rawV));
                        speed = Math.max(12, Math.min(45, rawS));
                        aqi = Math.max(100, Math.min(250, rawA));
                        break;
                    case "Electronic City":
                        vehicles = Math.max(15, Math.min(70, rawV));
                        speed = Math.max(35, Math.min(75, rawS));
                        aqi = Math.max(30, Math.min(110, rawA));
                        break;
                    default:
                        vehicles = Math.max(20, Math.min(100, rawV));
                        speed = Math.max(10, Math.min(60, rawS));
                        aqi = Math.max(50, Math.min(180, rawA));
                }
            }

            // 2. Retrieve global weather and modify reading variables
            String weather = gui.getWeatherCondition();
            if ("Heavy Rain".equals(weather)) {
                speed = Math.max(5, (int) (speed * 0.60)); // 40% Speed Reduction
            } else if ("Dense Fog".equals(weather)) {
                speed = Math.max(5, (int) (speed * 0.50)); // 50% Speed Reduction
                aqi = (int) (aqi * 1.30); // 30% increase in AQI (smog trapping)
            }

            // 3. Simulating emergency vehicle spawn (12% chance)
            String emergency = "None";
            if (random.nextInt(100) < 12) {
                String[] emergencyTypes = {"Ambulance", "Fire Truck", "Police Vehicle"};
                emergency = emergencyTypes[random.nextInt(emergencyTypes.length)];
                vehicles = Math.min(120, vehicles + 8); // Emergency vehicle creates sudden bunching
                speed = Math.max(5, speed - 5);
            }

            // Construct final reading
            SensorReading reading = new SensorReading(vehicles, speed, aqi, emergency);

            // 4. Calculate prediction trend based on history
            String predictedState;
            if (!history.isEmpty()) {
                SensorReading lastReading = history.get(history.size() - 1);
                int delta = reading.getVehicleCount() - lastReading.getVehicleCount();
                int projectedVehicles = reading.getVehicleCount() + 5 * delta; // projection for 15 mins (5 loops ahead)
                
                if (projectedVehicles > 100) {
                    predictedState = "Severe Congestion in 15 mins";
                } else if (projectedVehicles > 80) {
                    predictedState = "Heavy Congestion in 15 mins";
                } else if (projectedVehicles > 50) {
                    predictedState = "Moderate Traffic in 15 mins";
                } else {
                    predictedState = "Normal Traffic in 15 mins";
                }
            } else {
                // First reading: guess based on current vehicle count
                if (vehicles > 85) {
                    predictedState = "Heavy Congestion in 15 mins";
                } else if (vehicles > 55) {
                    predictedState = "Moderate Traffic in 15 mins";
                } else {
                    predictedState = "Normal Traffic in 15 mins";
                }
            }
            history.add(reading);

            // Run anomaly detection pipeline (AI logic)
            AnomalyResult result = AnomalyDetector.analyze(junctionName, reading, predictedState);

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
                vehicles = 60 + random.nextInt(55); // 60 to 115
                speed = 5 + random.nextInt(20);     // 5 to 24 km/h
                aqi = 80 + random.nextInt(100);    // 80 to 179
                break;
            case "KR Puram":
                vehicles = 50 + random.nextInt(55); // 50 to 105
                speed = 10 + random.nextInt(25);    // 10 to 34 km/h
                aqi = 100 + random.nextInt(120);   // 100 to 219
                break;
            case "Hebbal":
                vehicles = 40 + random.nextInt(45); // 40 to 85
                speed = 25 + random.nextInt(35);    // 25 to 59 km/h
                aqi = 70 + random.nextInt(90);     // 70 to 159
                break;
            case "Marathahalli":
                vehicles = 45 + random.nextInt(50); // 45 to 95
                speed = 15 + random.nextInt(30);    // 15 to 44 km/h
                aqi = 120 + random.nextInt(130);   // 120 to 249
                break;
            case "Electronic City":
                vehicles = 20 + random.nextInt(45); // 20 to 65
                speed = 40 + random.nextInt(35);    // 40 to 74 km/h
                aqi = 40 + random.nextInt(70);     // 40 to 109
                break;
            default:
                vehicles = 30 + random.nextInt(60);
                speed = 20 + random.nextInt(40);
                aqi = 50 + random.nextInt(100);
        }

        // Return a raw reading with "None" emergency, which will be updated in run()
        return new SensorReading(vehicles, speed, aqi, "None");
    }

    private void printLogToConsole(SensorReading reading, AnomalyResult result) {
        if (result.isAbnormal()) {
            System.out.println("[ALERT] " + junctionName + " → " + result.getAlertsString());
        } else {
            System.out.println("[NORMAL] " + junctionName + " → " + reading.toString());
        }
    }
}

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {

    public static AnomalyResult analyze(String junction, SensorReading reading) {
        List<String> alerts = new ArrayList<>();
        boolean abnormal = false;

        // Threshold checks
        if (reading.getVehicleCount() > 80) {
            abnormal = true;
            alerts.add("🚨 High vehicle count: " + reading.getVehicleCount() + " vehicles");
        }

        if (reading.getAverageSpeed() < 20) {
            abnormal = true;
            alerts.add("🐢 Traffic jam - Avg speed: " + reading.getAverageSpeed() + " km/h");
        }

        if (reading.getAqi() > 150) {
            abnormal = true;
            alerts.add("😷 Poor air quality - AQI: " + reading.getAqi());
        }

        return new AnomalyResult(abnormal, alerts);
    }
}

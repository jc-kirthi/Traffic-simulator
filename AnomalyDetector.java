import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {

    public static AnomalyResult analyze(String junction, SensorReading reading, String predictedState) {
        List<String> alerts = new ArrayList<>();
        boolean abnormal = false;

        // 1. Determine Traffic State
        int vehicles = reading.getVehicleCount();
        int speed = reading.getAverageSpeed();
        int aqi = reading.getAqi();
        String emergency = reading.getEmergencyVehicle();

        String trafficState = "Normal";
        if (vehicles > 100 || speed < 10) {
            trafficState = "Severe";
            abnormal = true;
        } else if (vehicles > 80 || speed < 20) {
            trafficState = "Heavy";
            abnormal = true;
        } else if (vehicles > 50 || speed < 35) {
            trafficState = "Moderate";
        } else {
            trafficState = "Normal";
        }

        // 2. Add alerts based on thresholds
        if (vehicles > 80) {
            alerts.add("🚨 High vehicle count: " + vehicles + " vehicles");
        }
        if (speed < 20) {
            alerts.add("🐢 Traffic jam - Avg speed: " + speed + " km/h");
        }
        if (aqi > 150) {
            abnormal = true;
            alerts.add("😷 Poor air quality - AQI: " + aqi);
        }

        // 3. Handle Emergency Vehicles
        if (!"None".equals(emergency)) {
            abnormal = true;
            alerts.add("🚨 " + emergency + " detected!");
        }

        // 4. Determine Signal Recommendation
        String signalRec;
        if (!"None".equals(emergency)) {
            signalRec = "Priority Signal Activation";
        } else if ("Severe".equals(trafficState)) {
            signalRec = "Increase Green Signal by 45 sec";
        } else if ("Heavy".equals(trafficState)) {
            signalRec = "Increase Green Signal by 30 sec";
        } else if ("Moderate".equals(trafficState)) {
            signalRec = "Increase Green Signal by 15 sec";
        } else {
            signalRec = "Keep default signal cycle";
        }

        // 5. Determine Route Diversion
        String routeDiversion = "No diversion needed";
        if ("Heavy".equals(trafficState) || "Severe".equals(trafficState)) {
            switch (junction) {
                case "Silk Board":
                    routeDiversion = "Use Electronic City Flyover";
                    break;
                case "KR Puram":
                    routeDiversion = "Diversion via Hennur Bypass";
                    break;
                case "Hebbal":
                    routeDiversion = "Reroute through Bellary Rd lanes";
                    break;
                case "Marathahalli":
                    routeDiversion = "Use HAL Old Airport Road";
                    break;
                case "Electronic City":
                    routeDiversion = "Reroute via NICE Expressway";
                    break;
                default:
                    routeDiversion = "Use alternate arterial road";
            }
            alerts.add("⚡ Diversion advised: " + routeDiversion);
        }

        return new AnomalyResult(abnormal, alerts, trafficState, signalRec, predictedState, routeDiversion);
    }
}

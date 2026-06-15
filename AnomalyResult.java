import java.util.ArrayList;
import java.util.List;

public class AnomalyResult {
    private final boolean abnormal;
    private final List<String> alerts;
    private final String trafficState;
    private final String signalRecommendation;
    private final String predictedState;
    private final String routeDiversion;

    public AnomalyResult(boolean abnormal, List<String> alerts, String trafficState, 
                         String signalRecommendation, String predictedState, String routeDiversion) {
        this.abnormal = abnormal;
        this.alerts = alerts != null ? alerts : new ArrayList<>();
        this.trafficState = trafficState != null ? trafficState : "Normal";
        this.signalRecommendation = signalRecommendation != null ? signalRecommendation : "None";
        this.predictedState = predictedState != null ? predictedState : "Normal Traffic";
        this.routeDiversion = routeDiversion != null ? routeDiversion : "No diversion needed";
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public String getTrafficState() {
        return trafficState;
    }

    public String getSignalRecommendation() {
        return signalRecommendation;
    }

    public String getPredictedState() {
        return predictedState;
    }

    public String getRouteDiversion() {
        return routeDiversion;
    }

    public String getAlertsString() {
        if (alerts.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String alert : alerts) {
            sb.append(alert).append(" | ");
        }
        // Remove trailing " | " if present
        if (sb.length() > 3) {
            sb.setLength(sb.length() - 3);
        }
        return sb.toString();
    }
}

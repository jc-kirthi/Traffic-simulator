import java.util.ArrayList;
import java.util.List;

public class AnomalyResult {
    private final boolean abnormal;
    private final List<String> alerts;

    public AnomalyResult(boolean abnormal, List<String> alerts) {
        this.abnormal = abnormal;
        this.alerts = alerts != null ? alerts : new ArrayList<>();
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public String getAlertsString() {
        if (alerts.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String alert : alerts) {
            sb.append(alert).append(" | ");
        }
        return sb.toString();
    }
}

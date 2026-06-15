public class SensorReading {
    private final int vehicleCount;
    private final int averageSpeed; // in km/h
    private final int aqi; // Air Quality Index
    private final String emergencyVehicle; // "None", "Ambulance", "Fire Truck", "Police Vehicle"

    public SensorReading(int vehicleCount, int averageSpeed, int aqi, String emergencyVehicle) {
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.aqi = aqi;
        this.emergencyVehicle = emergencyVehicle != null ? emergencyVehicle : "None";
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public int getAqi() {
        return aqi;
    }

    public String getEmergencyVehicle() {
        return emergencyVehicle;
    }

    @Override
    public String toString() {
        String base = "Vehicles: " + vehicleCount + " | Speed: " + averageSpeed + " km/h | AQI: " + aqi;
        if (!"None".equals(emergencyVehicle)) {
            base += " | Emergency: " + emergencyVehicle;
        }
        return base;
    }
}

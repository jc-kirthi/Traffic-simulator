public class SensorReading {
    private final int vehicleCount;
    private final int averageSpeed; // in km/h
    private final int aqi; // Air Quality Index

    public SensorReading(int vehicleCount, int averageSpeed, int aqi) {
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.aqi = aqi;
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

    @Override
    public String toString() {
        return "Vehicles: " + vehicleCount + " | Speed: " + averageSpeed + " km/h | AQI: " + aqi;
    }
}

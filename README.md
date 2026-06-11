# Bengaluru Traffic Junction Monitor 🚦
### Parallel Sensor Analytics Engine (SDG 9: Industry, Innovation & Infrastructure)

A real-time, multithreaded simulation dashboard designed to monitor and analyze traffic sensor data from five major junctions in Bengaluru: **Silk Board**, **KR Puram**, **Hebbal**, **Marathahalli**, and **Electronic City**. The application leverages parallel computing to process sensor data streams independently and highlights anomalies such as heavy traffic jams, high vehicle counts, and poor air quality.

---

## 📂 File Structure & Architecture

The codebase is organized as follows:

| File | Type | Description |
| :--- | :--- | :--- |
| [TrafficAnalyzerGUI.java](file:///d:/traffic/TrafficAnalyzerGUI.java) | **Main Controller / View** | The primary application entry point (`JFrame`). Coordinates the thread pool, manages overall simulation states, and displays a real-time visualization grid and log panel. |
| [JunctionCard.java](file:///d:/traffic/JunctionCard.java) | **UI Component** | A custom Swing `JPanel` representing a single traffic junction. Renders vehicle counts, average speeds, and AQI indices using graphical progress bars, color-coded thresholds, and active status glowing borders. |
| [JunctionMonitor.java](file:///d:/traffic/JunctionMonitor.java) | **Thread / Worker** | Implements `Runnable`. Each instance represents an active sensor monitor running in its own thread, simulating independent periodic data updates from its assigned junction. |
| [AnomalyDetector.java](file:///d:/traffic/AnomalyDetector.java) | **Service / Logic** | The rules engine containing traffic safety thresholds. Evaluates raw sensor data and returns categorized alert messages if safety parameters are breached. |
| [SensorReading.java](file:///d:/traffic/SensorReading.java) | **Model** | An immutable data carrier class encapsulating the telemetry parameters (vehicle density, speed, air quality index) at a given point in time. |
| [AnomalyResult.java](file:///d:/traffic/AnomalyResult.java) | **Model** | Holds the results of anomaly detection, containing the overall status (`abnormal`) and lists of specific alert logs. |

---

## ⚡ Key Features

1. **Multithreaded Simulation**:
   - Each junction sensor runs as an independent `Thread` (`JunctionThread-Name`).
   - Simulates asynchronous readings with varying sleep times, showcasing parallel executions and thread interleaving.

2. **Real-time Visualization**:
   - Live update of progress bars showing relative thresholds.
   - Dynamic UI responses, including color indicators (Green for nominal, Red for anomalies) and glowing borders.

3. **Traffic Anomaly Detection (AI/Rule-based Pipeline)**:
   - **🚨 High Traffic Volume**: Triggered when a junction exceeds `80 vehicles`.
   - **🐢 Traffic Jam**: Triggered when the average speed falls below `20 km/h`.
   - **😷 Toxic Air Quality**: Triggered when the Air Quality Index (AQI) exceeds `150`.

4. **Live Event Log Stream**:
   - Color-coded text area displaying instant thread startup, normal updates, specific warnings, and run completions.

---

## 🛠️ How to Compile and Run

To run the application locally, follow these steps:

### Prerequisites
- Java Development Kit (JDK 8 or higher) installed.
- Access to a command line.

### Compile
Navigate to the root directory and compile all Java source files:
```bash
javac *.java
```

### Run
Launch the application GUI:
```bash
java TrafficAnalyzerGUI
```

---

## 🎨 Technology & UI Details
- **GUI Framework**: Java Swing (utilizing standard components custom painted with `Graphics2D` for high-fidelity anti-aliasing and aesthetics).
- **Design Palette**: Sleek dark mode featuring specialized gray tones (`#121214`, `#1C1C1F`) contrasted with high-visibility feedback colors (Emerald Green, Crimson Red, Indigo Blue).

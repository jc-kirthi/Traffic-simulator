# Bengaluru Traffic Junction Monitor 🚦
### Smart AI Transport Operations & Analytics Dashboard (SDG 9: Industry, Innovation & Infrastructure)

A real-time, multithreaded simulation dashboard designed to monitor, analyze, and manage traffic sensor data from five major junctions in Bengaluru: **Silk Board**, **KR Puram**, **Hebbal**, **Marathahalli**, and **Electronic City**. 

The application utilizes parallel computing (multi-threaded concurrent tasks) to simulate edge sensors, runs real-time traffic prediction algorithms, models meteorological changes, intercepts emergency siren overrides, suggests alternate routing detours, and displays custom-drawn analytics curves.

---

## 📂 File Structure & Architecture

The codebase is organized as follows:

| File | Type | Description |
| :--- | :--- | :--- |
| [TrafficAnalyzerGUI.java](file:///d:/traffic/TrafficAnalyzerGUI.java) | **Main Controller / View** | Coordinates the thread pool, manages overall simulation states, contains the weather combobox, and handles the Event Dispatch Thread (EDT) updates. Features a split bottom console with a live event log stream and a real-time **AI Briefing Board**. |
| [CityMapPanel.java](file:///d:/traffic/CityMapPanel.java) | **UI Component (Map)** | Renders a node-based road network linking the 5 junctions with glowing colored congestion paths. Features a flashing blue/red animation to simulate emergency siren beacons. |
| [AnalyticsDashboardPanel.java](file:///d:/traffic/AnalyticsDashboardPanel.java) | **UI Component (Dashboard)** | Renders 4 custom Java 2D graphs (Traffic Volume Trend area-line, AQI Comparison bars, Peak Hours spline, and Vehicle Growth Projection area curve). |
| [JunctionCard.java](file:///d:/traffic/JunctionCard.java) | **UI Component (Card)** | Custom Swing panel representing a single traffic junction. Renders vehicle count, average speed, and AQI progress bars, glowing status badges, 15-min predictions, AI cycle times, emergency alerts, and detour labels. |
| [JunctionMonitor.java](file:///d:/traffic/JunctionMonitor.java) | **Thread / Worker** | Implements `Runnable`. Simulates independent, parallel sensor monitors. Features slower execution speeds (3s per cycle) for live audience tracking, weather-based physical coefficients, and emergency spawn triggers. |
| [AnomalyDetector.java](file:///d:/traffic/AnomalyDetector.java) | **Service / Logic** | The intelligence engine. Classifies traffic status (`Normal`, `Moderate`, `Heavy`, `Severe`), parses emergency signals, calculates optimized green-light cycle extensions, and suggests smart routing. |
| [SensorReading.java](file:///d:/traffic/SensorReading.java) | **Model** | Encapsulates telemetry variables (vehicle count, speed, AQI index, and emergency status). |
| [AnomalyResult.java](file:///d:/traffic/AnomalyResult.java) | **Model** | Holds evaluation results (abnormal flag, status alerts list, traffic state, signal cycle recommendation, predicted congestion state, and detour routes). |

---

## ⚡ Key Features

1. **Multithreaded Simulation & Audience Speed Control**:
   - Each junction sensor runs asynchronously in its own thread (`JunctionThread-Name`).
   - Loop cycles are slowed down to **2.5 - 4.0 seconds** per reading, allowing observers to comfortably understand data changes, predictions, and alerts as they occur.

2. **Signal Recommendation AI (🚦)**:
   - Evaluates active densities and dynamically advises signal adjustments to maximize throughput:
     - `Moderate` $\rightarrow$ Increase Green Signal by 15 sec
     - `Heavy` $\rightarrow$ Increase Green Signal by 30 sec
     - `Severe` $\rightarrow$ Increase Green Signal by 45 sec
     - `Emergency` $\rightarrow$ Priority Signal Activation (Green hold)

3. **Linear Traffic Prediction**:
   - Stores session readings and calculates a 15-minute future forecast based on vehicle count gradients:
     $$\Delta = \text{Vehicles}_{\text{current}} - \text{Vehicles}_{\text{previous}}$$
     $$\text{Projected} = \text{Vehicles}_{\text{current}} + 5 \times \Delta$$
   - Displays real-time forecasts (e.g., "Heavy Congestion in 15 mins") on the UI cards.

4. **Emergency Vehicle Priority Preemption**:
   - Detects sirens (`Ambulance`, `Fire Truck`, or `Police Vehicle`).
   - Flashes the junction card in neon blue, flashes map nodes in siren colors, logs emergency overrides, and commands signal controllers to activate priority green lanes.

5. **Weather-Aware Sensor Coefficients**:
   - Injects global weather conditions (Sunny, Heavy Rain, Dense Fog).
   - **Heavy Rain**: Scales maximum speeds down by **40%** to account for wet asphalt friction safety rules.
   - **Dense Fog**: Caps speeds at **50%** due to visibility limits and increases AQI by **30%** (modeling smog trapping).

6. **City-Wide Traffic Heat Map**:
   - Replaces traditional text outputs with a live visual grid representing junction nodes and connecting arterial roads. Nodes transition dynamically (🟢 nominal, 🟡 moderate, 🔴 severe, and flashing blue/red sirens).

7. **AI Briefing Explainer Board**:
   - A dedicated right-column pane explaining the core reasoning behind AI decisions in plain English for audience demonstrations and presentation clarity.

8. **Analytics Tab Dashboard**:
   - Plots four anti-aliased Java 2D vector graphs in real-time, providing managers with traffic flow, air quality, peak hours profiles, and a 10-year vehicle registration growth trend (SDG 9 validation).

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
- **Design Palette**: Sleek dark mode featuring specialized gray tones (`#121214`, `#1C1C1F`) contrasted with high-visibility feedback colors (Emerald Green, Crimson Red, Indigo Blue, Electric Purple).

package org.edgeoffload;

import org.fog.entities.Sensor;
import org.fog.entities.Actuator;
import org.fog.utils.distribution.DeterministicDistribution;

import java.util.ArrayList;
import java.util.List;

public class SensorActuatorManager {

    public static List<Sensor> createSensors(String appId, int userId) {
        List<Sensor> sensors = new ArrayList<>();
        switch (appId) {
            case "CameraApplication":
                sensors.add(new Sensor("camera", "SENSOR", userId, appId, new DeterministicDistribution(30)));
                break;
            case "TemperatureControl":
                sensors.add(new Sensor("tempsensor", "SENSOR2", userId, appId, new DeterministicDistribution(10)));
                break;
            // Add cases for other applications
            case "UnidirectionalApplication":
                sensors.add(new Sensor("unidir", "SENSOR3", userId, appId, new DeterministicDistribution(5)));
                break;
            default:
                break;
        }
        return sensors;
    }

    public static List<Actuator> createActuators(String appId, int userId) {
        List<Actuator> actuators = new ArrayList<>();
        switch (appId) {
            case "TemperatureControl":
                actuators.add(new Actuator("tempactuator", userId, appId, "FANACTUATOR"));
                break;
            // Add cases for other applications
            case "UnidirectionalApplication":
                actuators.add(new Actuator("unidiractuator", userId, appId, "ACTUATOR"));
            default:
                break;
        }
        return actuators;
    }

}

package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.utils.TimeKeeper;

import java.util.*;


public class FogTest {

    static int numOfFogDevices = 10;
    static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
    static Map<String, Integer> getIdByName = new HashMap<String, Integer>();


    public static void main(String[] args) throws Exception {

        Log.disable();
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        String appId = "ImageProcessorApplication";

        FogBroker broker = new FogBroker("broker");

        //create a list of tasks with depedent tasks
        ApplicationHandler applicationHandler = new ApplicationHandler(broker.getId());
        Application temp = applicationHandler.getCameraApplicaion();
        Application app = applicationHandler.getUniDirectionalApplication();
        temp.setUserId(broker.getId());
        app.setUserId(broker.getId());
        //create an instance of offloader class
        //based on resource required on availability the tasks are offloaded to suitable edge server

        EdgeServer edgeDevice = new EdgeServer();
        FogDevice cloud1 = edgeDevice.createCloud();
        FogDevice fd = edgeDevice.createEdgeDevice1();
List<FogDevice> fff = new ArrayList<>();
fff.add(cloud1);
fff.add(fd);

        ModuleMapping moduleMapping = createModuleMapping(fd.getName(), temp); // initializing a module mapping
        ModuleMapping moduleMapping_1 = createModuleMapping(fd.getName(), app);

       // moduleMapping.addModuleToDevice("client", "cloud");

        //ModuleMapping moduleMapping_1 = ModuleMapping.createModuleMapping();
       // moduleMapping_1.addModuleToDevice("object_tracker", "cloud");

        List<Sensor> sensor = new ArrayList<>();
        List<Actuator> actuators = new ArrayList<>();

        Controller controller = new Controller("master-controller", fff, new ArrayList<>(),
                new ArrayList<>());


        sensor.add(edgeDevice.setupSensor(temp, fd));
        actuators.add(edgeDevice.setupActuator(temp, fd));




        //moduleMapping.addModuleToDevice("connector", "cloud"); // fixing all instances of the Connector module to the Cloud



        boolean cloud = false;

        controller.setSensors(sensor);
        controller.setActuators(actuators);

        controller.submitApplication(temp, 0,
                (cloud)?(new ModulePlacementMapping(fff, temp, moduleMapping))
                        :(new ModulePlacementEdgewards(fff, sensor, actuators, temp, moduleMapping)));

        sensor.add(edgeDevice.setupSensor(app, fd));
        actuators.add(edgeDevice.setupActuator(app, fd));

        controller.setSensors(sensor);
        controller.setActuators(actuators);

        controller.submitApplication(app, 0,
                (cloud)?(new ModulePlacementMapping(fff, app, moduleMapping_1))
                        :(new ModulePlacementEdgewards(fff, sensor, actuators, app, moduleMapping_1)));

        TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

        CloudSim.startSimulation();

        CloudSim.stopSimulation();
    }
    //Map the application modules to each selected Edge device in offload algorithm
    private static ModuleMapping createModuleMapping(String deviceName ,Application application){
        ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping

        for(AppModule module: application.getModules()){
            moduleMapping.addModuleToDevice(module.getName(), "cloud");
            moduleMapping.addModuleToDevice(module.getName(), deviceName);
        }
        // fixing 1 instance of the application module to specified edge device

        return moduleMapping;
    }

    private static FogDevice getFogDevice(List<FogDevice> fogDevices, String deviceName){
        Optional<FogDevice> device = fogDevices.stream()
                .filter(fogDevice -> fogDevice.getName().equals(deviceName))
                .findFirst();

        return device.get();
    }
}


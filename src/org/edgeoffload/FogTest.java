package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.edgeoffload.model.EdgeDevice;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.*;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.utils.TimeKeeper;

import java.util.*;
import java.util.stream.Collectors;


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
        Application temp = applicationHandler.getDataApplication();
        Application app = applicationHandler.getImageProcessorApplication();
        temp.setUserId(broker.getId());
        app.setUserId(broker.getId());
        //create an instance of offloader class
        //based on resource required on availability the tasks are offloaded to suitable edge server

        EdgeServer edgeDevice = new EdgeServer();
        edgeDevice.createFogDevices();

        List<FogDevice> fogDevices = edgeDevice.getFogDevices();
        FogDevice cloud = fogDevices.stream().filter(a -> a.getName().equals("cloud")).collect(Collectors.toList()).get(0);
        FogDevice fd2 = fogDevices.stream().filter(a -> a.getName().equals("FogDevice1")).collect(Collectors.toList()).get(0);
        FogDevice fd3 = fogDevices.stream().filter(a -> a.getName().equals("FogDevice2")).collect(Collectors.toList()).get(0);
        FogDevice fd1 = fogDevices.stream().filter(a -> a.getName().equals("FogDevice3")).collect(Collectors.toList()).get(0);

    List<FogDevice> fff = new ArrayList<>();
    fff.add(cloud);
    fff.add(fd1);
    //fff.add(fd2);


        ModuleMapping moduleMapping = createModuleMapping(fd1.getName(), temp); // initializing a module mapping
     //   ModuleMapping moduleMapping_1 = createModuleMapping2(fd2.getName(), app);

       // moduleMapping.addModuleToDevice("client", "cloud");

        //ModuleMapping moduleMapping_1 = ModuleMapping.createModuleMapping();
       // moduleMapping_1.addModuleToDevice("object_tracker", "cloud");

        List<Sensor> sensor = new ArrayList<>();
        List<Actuator> actuators = new ArrayList<>();

        Controller controller = new Controller("master-controller", fff, new ArrayList<>(),
                new ArrayList<>());


        sensor.add(edgeDevice.setupSensor(temp, fd1));
        actuators.add(edgeDevice.setupActuator(temp, fd1));


        //moduleMapping.addModuleToDevice("connector", "cloud"); // fixing all instances of the Connector module to the Cloud



        boolean cloud1 = false;

        controller.setSensors(sensor);
        controller.setActuators(actuators);

        controller.submitApplication(temp, 0,
                (cloud1)?(new ModulePlacementMapping(fff, temp, moduleMapping))
                        :(new ModulePlacementEdgewards(fff, sensor, actuators, temp, moduleMapping)));


        TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());



        CloudSim.startSimulation();



        CloudSim.stopSimulation();
    }
    //Map the application modules to each selected Edge device in offload algorithm
    private static ModuleMapping createModuleMapping(String deviceName ,Application application){
        ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
        //moduleMapping.addModuleToDevice("slot-detector", "cloud");
        moduleMapping.addModuleToDevice(application.getModules().get(0).getName(), deviceName);
        moduleMapping.addModuleToDevice(application.getModules().get(1).getName(), deviceName);
        moduleMapping.addModuleToDevice(application.getModules().get(2).getName(), deviceName);
        // fixing 1 instance of the application module to specified edge device

        return moduleMapping;
    }

    private static ModuleMapping createModuleMapping2(String deviceName ,Application application){
        ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
        //moduleMapping.addModuleToDevice("slot-detector", "cloud");
            //moduleMapping.addModuleToDevice(module.getName(), "cloud");
            moduleMapping.addModuleToDevice(application.getModules().get(2).getName(), deviceName);
        moduleMapping.addModuleToDevice(application.getModules().get(3).getName(), deviceName);
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


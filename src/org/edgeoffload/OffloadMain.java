package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.application.DAG;
import org.fog.entities.Actuator;
import org.fog.entities.FogDevice;
import org.fog.entities.Sensor;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

public class OffloadMain {

    public static void main(String[] args) {

        Log.disable();
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        String appId = "edge_offload";


        ApplicationHandler applicationHandler = new ApplicationHandler();
        ArrayList<Application> applicationslist = applicationHandler.ListApplications();

        ApplicationDependency applicationDependency = new ApplicationDependency();

        org.fog.application.DAG dag = applicationDependency.createApplicationDepedency();

        Stack stack = dag.topologicalSort();

        List<List<Application>> applist =  applicationDependency.createApplicationDepdendencyList(applicationslist, stack);

        EdgeDevice edgeDevice = new EdgeDevice();
        List<FogDevice> fogDevices = edgeDevice.getFogDevicesList();

        OffloadAlgorithm.offloadingStrategy(applist, fogDevices);

        Application cameraApplication = applicationHandler.getCameraApplicaion();
        ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping

        for(FogDevice device : fogDevices){
            if(device.getName().startsWith("m"))
                moduleMapping.addModuleToDevice("image-capture", device.getName());  // fixing 1 instance of the Motion Detector module to each Smart Camera
        }

        //for(AppModule appModule: cameraApplication.getModules())
         //   moduleMapping.addModuleToDevice(appModule.getName(), edgeDevice.mobileDevice().getName());

        List<FogDevice> devices = new ArrayList<>();
        devices.add(edgeDevice.createMobileDevice());

        // Add the application to the fog devices
        Controller controller = new Controller("master-controller", devices, new ArrayList<>(), new ArrayList<>());
        ModulePlacementEdgewards placement = new ModulePlacementEdgewards(devices, new ArrayList<>(), new ArrayList<>(), applicationHandler.getCameraApplicaion(), moduleMapping);
        controller.submitApplication(cameraApplication, 0, placement);

        TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

        CloudSim.startSimulation();

        CloudSim.stopSimulation();
    }
}

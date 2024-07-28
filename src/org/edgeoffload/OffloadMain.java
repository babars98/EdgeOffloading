package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.edgeoffload.model.Task;
import org.fog.application.Application;
import org.fog.entities.FogDevice;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.utils.TimeKeeper;

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
        ArrayList<Application> applicationslist = new ArrayList<>();

        //independent tasks
        applicationslist.add(applicationHandler.getTempControlApplication());
        applicationslist.add(applicationHandler.getUniDirectionalApplication());

        //dependent tasks
        ApplicationDependency applicationDependency = new ApplicationDependency();

        org.fog.application.DAG dag = applicationDependency.createApplicationDepedency();

        Stack stack = dag.topologicalSort();

        List<Stack> stacks = new ArrayList<>();
        stacks.add(stack);

        List<Task> tasks =  applicationDependency.createApplicationDepdendencyList(applicationHandler.ListApplications(), applicationslist, stacks);

        EdgeServer edgeDevice = new EdgeServer();
        List<FogDevice> fogDevices = edgeDevice.getFogDevicesList();

        TaskOffloader offloader = new TaskOffloader(BL.mapEdgeDevicesToList(fogDevices), tasks);
        offloader.deployTask();

       // OffloadAlgorithm.offloadingStrategy(applist, fogDevices);

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

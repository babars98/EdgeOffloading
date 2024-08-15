package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.edgeoffload.model.EdgeDevice;
import org.edgeoffload.model.Task;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.Sensor;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.utils.TimeKeeper;

import java.util.*;

public class OffloadMain {

    public static void main(String[] args) {

        Log.disable();
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        String appId = "edge_offload";

        try{

            FogBroker broker = new FogBroker("broker");


            ApplicationHandler applicationHandler = new ApplicationHandler(broker.getId());
            List<Application> applications = applicationHandler.ListApplications();

            //create a list of tasks with depedent tasks
            List<Task> tasks = createApplicationsTasksList(applicationHandler, broker.getId());

            //get all the fog devices created
            EdgeServer edgeServer = new EdgeServer();
            List<FogDevice> edgeServers = edgeServer.getFogDevicesList();

            //create an instance of offloader class
            //based on resource required on availability the tasks are offloaded to suitable edge server
            TaskOffloader offloader = new TaskOffloader(BL.mapEdgeDevicesToList(edgeServers), tasks);
            List<EdgeDevice> edgeWithassignedTasks = offloader.assignTasksToEdge();


            Controller controller = new Controller("master-controller", edgeServers, new ArrayList<>(), new ArrayList<>());
            for(EdgeDevice device: edgeWithassignedTasks){
                // Add the application to the fog devices

                List<Sensor> sensors = new ArrayList<>();
                List<Actuator> actuators = new ArrayList<>();

                if(!device.getTasks().isEmpty()){

                    FogDevice fogDevice = getFogDevice(edgeServers, device.getName());

                    for(Task task: device.getTasks()){
                        Application application = getApplication(applications, task.getId());
                        //application.setAppId(appId);

                        sensors.add(edgeServer.setupSensor(application, fogDevice));
                        actuators.add(edgeServer.setupActuator(application, fogDevice));

                        //map each application modules to designated edge server, the decision is made in offloader algorithm
                        ModuleMapping moduleMapping = createModuleMapping(device.getName(), application); // initializing a module mapping
                       // ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
                       // moduleMapping.addModuleToDevice(application.getModules().get(0).getName(), "cloud");
                        //moduleMapping.addModuleToDevice(application.getModuleNames().get(0), "cloud");

                        List<FogDevice> fd = new ArrayList<>();
                        fd.add(fogDevice);
                        fd.add(getFogDevice(edgeServers, "cloud"));

                        boolean CLOUD = Objects.equals(fogDevice.getName(), "cloud");

                        controller.setSensors(sensors);
                        controller.setActuators(actuators);

                        controller.submitApplication(application,
                                (CLOUD)?(new ModulePlacementMapping(fd, application, moduleMapping))
                                        :(new ModulePlacementEdgewards(fd,
                                        sensors,
                                        actuators,
                                        application, moduleMapping)));
                    }
                }
            }

            TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

            CloudSim.startSimulation();

            CloudSim.stopSimulation();


        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }



    }

    //create application using IFogSim classes and add dependency
    private static List<Task> createApplicationsTasksList(ApplicationHandler applicationHandler, int userId){


        ArrayList<Application> applicationslist = new ArrayList<>();

        //independent tasks
        applicationslist.add(applicationHandler.getClientApplication());
        applicationslist.add(applicationHandler.getUniDirectionalApplication());

        //dependent tasks
        ApplicationDependency applicationDependency = new ApplicationDependency();

        org.fog.application.DAG dag = applicationDependency.createApplicationDependency(userId);

        //this sorts the tasks in a hierarchical way for application execution order
        Stack stack = dag.topologicalSort();

        List<Stack> stacks = new ArrayList<>();
        stacks.add(stack);

        List<Task> tasks =  applicationDependency.createApplicationDepdendencyList(applicationHandler.ListApplications(), applicationslist, stacks);

        return tasks;
    }

    //Map the application modules to each selected Edge device in offload algorithm
    private static ModuleMapping createModuleMapping(String deviceName ,Application application){
        ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping

        for(AppModule module: application.getModules()){
            //moduleMapping.addModuleToDevice(module.getName(), "cloud");
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

    private static Application getApplication(List<Application> applications, String appName){
        Optional<Application> application = applications.stream()
                .filter(app -> app.getAppId().equals(appName))
                .findFirst();

        return application.get();
    }
}

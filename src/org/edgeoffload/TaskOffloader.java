package org.edgeoffload;

import org.edgeoffload.model.EdgeDevice;
import org.edgeoffload.model.Task;
import org.fog.gui.core.Edge;

import java.util.*;
import java.util.stream.Collectors;

public class TaskOffloader {


    private final double resourceLimit = 0.9;
    private final List<EdgeDevice> edgeDevices;
    private final List<Task> taskList;
    private final List<Task> taskQueue;
    private final EdgeDevice cloud;
    private final String cloudName = "cloud";

    public TaskOffloader(List<EdgeDevice> edgeDevices, List<Task> tasks) {
        this.edgeDevices = edgeDevices;
        cloud = edgeDevices.stream().filter(a -> a.getName().equals(cloudName)).collect(Collectors.toList()).get(0);
        this.edgeDevices.remove(cloud);
        this.taskList = tasks;
        taskQueue = new ArrayList<>();
    }


    //Deploy tasks to edge
    public List<EdgeDevice> assignTasksToEdge() {

        while(!taskList.isEmpty()){
            Task task = taskList.get(0);
            if (task.isDependent()) {
                deployDependentTask(task);
            } else {
                deployIndependentTask(task);
            }
        }

        //Deploy all remaining tasks to Cloud, if no edge server with enough resources available
        if(!taskQueue.isEmpty()){
            //offload tasks to cloud if there is no device with enough resource available in the pool
            offloadTasksToCloud(taskQueue);
        }

        //
        edgeDevices.add(cloud);
        return edgeDevices;
    }

    //check if the edge device has enough resources
    private void deployIndependentTask(Task task) {

        EdgeDevice device = findSuitableServer(task);
        if (device != null) {
            device.addTask(task);
            taskList.remove(task);
            return;
        }

        //Add tasks to task queue if resources are not sufficient
        taskList.remove(task);
        taskQueue.add(task);
    }

    private void deployDependentTask(Task dependentTask) {

        List<Task> tasks = GroupDependentTask(dependentTask);
        boolean taskMigrated = false;

        //Check the edge devices for sufficient resource to offload the dependent tasks
        boolean isOffloaded = OffloadTasktoEdgeWithResourceAvailable(tasks);

        //If there is no edge device found with sufficient resource, check for total resource
        //to ensure if there is any independent application is deployed
        if(!isOffloaded) {

            EdgeDevice device = findSuitableServer(tasks);

            //if the device is available to host the task
            if(device != null)
                //Remove the assigned independent tasks from the edge device
                taskMigrated = migrateTasksToFit(device, tasks);

            ///if suitable device found, offload dependent tasks there
            if(taskMigrated){
                offloadTasksToServer(tasks, device);
            }
        }
        //if no suitable device found which can accommodate the dependent tasks, deploy tasks on separate edge devices
        if (!taskMigrated && !isOffloaded){
            for(Task task: tasks){
                //using the method will deploy the application to
                deployIndependentTask(task);
            }
        }
    }

    private boolean OffloadTasktoEdgeWithResourceAvailable(List<Task> tasks){

        EdgeDevice device = findSuitableServer(tasks);

        if(device != null){
            for (Task task : tasks){
                device.addTask(task);
                taskList.remove(task);
            }
        }

        return device != null;
    }

    //Function to perform Independent tasks migration to other edge to make
    //resource available for depedent tasks
    private boolean migrateTasksToFit(EdgeDevice targetDevice, List<Task> dependentTasks) {

        List<Task> tasksToMigrate = findTasksToMigrate(targetDevice, dependentTasks);
        boolean flage = false;
        if(tasksToMigrate.isEmpty()){
            flage = false;
        }
        else{
            for (Task task : tasksToMigrate) {
                targetDevice.removeTask(task);
                taskList.add(task);
                flage = true;
            }
        }
        return flage;
    }

    //Iterate over the list of tasks deployed on edge server and migrate one by one to make space
    private List<Task> findTasksToMigrate(EdgeDevice device, List<Task> dependentTasks){

        List<Task> tasksToMigrate = new ArrayList<>();
        boolean isSpacefreed = false;
        int requiredMips = BL.calculateTotalMips(dependentTasks);
        int requiredRam = BL.calculateTotalRam(dependentTasks);
        int requiredBW = BL.calculateTotalRam(dependentTasks);
        int availableMips = device.getAvailableMips();
        int availableRam = device.getAvailableRam();
        int availableBW = device.getAvailableBW();

        List<Task> tasks = device.getTasks();
        //sort tasks based on mips and ram asc
        sortTasksByMipsAndRam(tasks);

        for (Task task : tasks) {
            if (!task.isDependent()) {
                tasksToMigrate.add(task);
                availableMips += task.getMips();
                availableRam += task.getRam();
                availableBW += task.getBW();
                if (availableMips * resourceLimit >= requiredMips &&
                        availableRam * resourceLimit >= requiredRam &&
                        availableBW >= requiredBW) {
                    isSpacefreed = true;
                    break;
                }
            }
        }
        return isSpacefreed ? tasksToMigrate : new ArrayList<>();
    }

    //find the best available edge server which has sufficient resources and has the least latency
    private EdgeDevice findSuitableServer(Task task) {
        EdgeDevice selectedDevice = null;
        double lowestLatency = Double.MAX_VALUE;

        for (EdgeDevice device : edgeDevices) {
            // Check if the device can accommodate the task
            if (device.canAccommodate(task)) {
                double currentLatency = device.getLatency(); // Method to get the latency of the device

                // Check if this device has the lowest latency so far
                if (currentLatency < lowestLatency) {
                    lowestLatency = currentLatency;
                    selectedDevice = device;
                }
            }
        }

        return selectedDevice;
    }

    //find the best available edge server which has sufficient resources and has the least latency
    private EdgeDevice findSuitableServer(List<Task> tasks) {

        int totalMips = BL.calculateTotalMips(tasks);
        int totalRam = BL.calculateTotalRam(tasks);
        int totalBW = BL.calculateTotalBW(tasks);

        EdgeDevice selectedDevice = null;
        double lowestLatency = Double.MAX_VALUE;

        for (EdgeDevice device : edgeDevices) {

            if (device.getAvailableMips() * resourceLimit >= totalMips &&
                    device.getAvailableRam() * resourceLimit >= totalRam &&
                    device.getAvailableBW() > totalBW) {

                double currentLatency = device.getLatency(); // Method to get the latency of the device

                // Check if this device has the lowest latency so far
                if (currentLatency < lowestLatency) {
                    lowestLatency = currentLatency;
                    selectedDevice = device;

                }
            }
        }

        return  selectedDevice;
    }

    private void offloadTasksToServer(List<Task> tasks, EdgeDevice device){
        for (Task task : tasks){
            device.addTask(task);
            taskList.remove(task);
        }
    }

    private void offloadTasksToCloud(List<Task> tasks){
        offloadTasksToServer(tasks, cloud);
    }

    // Function to sort a list of tasks based on MIPS and RAM in descending order
    //This will help to choose tasks with high ram and mips usage
    private void sortTasksByMipsAndRam(List<Task> tasks) {
        tasks.sort((task1, task2) -> {
            int mipsComparison = Integer.compare(task2.getMips(), task1.getMips());
            if (mipsComparison != 0) {
                return mipsComparison;
            }
            return Integer.compare(task2.getRam(), task1.getRam());
        });
    }

    private List<Task> GroupDependentTask(Task task) {
        List<Task> dtasks = new ArrayList<>();
        for (Task t : taskList) {
            if (t.getUniqueID().equals(task.getUniqueID())) {
                    dtasks.add(t);
                }
        }
        return dtasks;
    }
}

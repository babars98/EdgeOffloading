package org.edgeoffload;

import org.edgeoffload.model.AppandServer;
import org.edgeoffload.model.ResourceRequirement;
import org.fog.application.Application;
import org.fog.entities.FogDevice;

import java.util.ArrayList;
import java.util.List;

public class OffloadAlgorithm {

    //Function for offlaoding strategy
    //Gets list of dependent/independent applications and fog devices and compares the resource required
    //and allocates the best available edge server for offloading
    public static List<AppandServer> offloadingStrategy(List<List<Application>> applicationsList, List<FogDevice> fogDevices){


        List<AppandServer> appandServers = new ArrayList<>();

        for(List<Application> apps: applicationsList){

            ResourceRequirement resourceRequirement = BL.calculateTotalResources(apps);

            FogDevice endDevice = findSuitableFogDevice(fogDevices, resourceRequirement);

            appandServers.add(new AppandServer(endDevice, apps));

        }
        return appandServers;
    }

    //Compares the resource required by application with available edge servers
    //chooses the best option with the least latency
    //Constraints: Resource usage should remain under 90% (RAM, CPU)
    private static FogDevice findSuitableFogDevice(List<FogDevice> fogDevices, ResourceRequirement totalResources) {
        FogDevice selectedDevice = null;
        double leastLatency = Double.MAX_VALUE;

        for (FogDevice device : fogDevices) {

            int mips = device.getVmAllocationPolicy().getHost(device.getHost().getId(), device.getId()).getTotalMips();
            int ram = device.getVmAllocationPolicy().getHost(device.getHost().getId(), device.getId()).getRam();
            int bw = device.getVmAllocationPolicy().getHost(device.getHost().getId(), device.getId()).getRam();;
            if (mips * 0.9 >= totalResources.getMips() &&
                    ram * 0.9 >= totalResources.getRam() &&
                    bw >= totalResources.getBandwidth() &&
                    device.getUplinkLatency() < leastLatency) {
                selectedDevice = device;
                leastLatency = device.getUplinkLatency();
            }
        }
        
        return selectedDevice; // Return the device with the least latency
    }

}

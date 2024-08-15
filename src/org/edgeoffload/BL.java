package org.edgeoffload;

import org.edgeoffload.model.EdgeDevice;
import org.edgeoffload.model.FogDeviceResource;
import org.edgeoffload.model.ResourceRequirement;
import org.edgeoffload.model.Task;
import org.fog.application.AppEdge;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.FogDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public  class BL {

    public static Application getApplicationbyName(List<Application> apps, String appId){
        for (Application app : apps) {
            if (app.getAppId().equals(appId)) {
                return app;
            }
        }
        return null;
    }

    //Calculate the resource required by an application on edge server
    public static ResourceRequirement calculateTotalResources(List<Application> applications) {
        long totalMips = 0;
        int totalRam = 0;
        long totalBandwidth = 0;

        for (Application app : applications) {
            for (AppModule module : app.getModules()) {
                totalMips += module.getMips();
                totalRam += module.getRam();
                totalBandwidth += module.getBw();
            }
        }

        return new ResourceRequirement(totalMips, totalRam, totalBandwidth);
    }

    public static int calculateMips(Application application) {
        long totalMips = 0;
            for (AppModule module : application.getModules()) {
                totalMips += module.getMips();
            }

        return (int)totalMips;
    }

    public static int calculateRam(Application application) {
        long totalRam = 0;
        for (AppModule module : application.getModules()) {
            totalRam += module.getRam();
        }

        return (int)totalRam;
    }

    public static int calculateBW(Application application) {
        long totalBW = 0;
        for (AppModule module : application.getModules()) {
            totalBW += module.getBw();
        }

        return (int)totalBW;
    }

    public static int calculateTotalMips(List<Task> tasks){
        int totalMips = 0;
        for (Task task : tasks) {
            totalMips += task.getMips();
        }

        return totalMips;
    }

    public static int calculateTotalRam(List<Task> tasks){
        int totalRam = 0;
        for (Task task : tasks) {
            totalRam += task.getMips();
        }

        return totalRam;
    }

    public static int calculateTotalBW(List<Task> tasks){
        int totalBW = 0;
        for (Task task : tasks) {
            totalBW += task.getBW();
        }

        return totalBW;
    }

    public static List<FogDeviceResource> createFogDeviceResourceList(List<FogDevice> fogDevices){
        List<FogDeviceResource> availableResource = new ArrayList<>();

        for(FogDevice fogDevice: fogDevices){
            int mips = fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getTotalMips();
            int ram = fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getRam();
            long bw = fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getBw();

            FogDeviceResource resource = new FogDeviceResource(fogDevice.getName(), mips, ram, bw, fogDevice.getUplinkLatency());

            availableResource.add(resource);
        }

        return availableResource;
    }

    public static List<EdgeDevice> mapEdgeDevicesToList(List<FogDevice> devices){
        List<EdgeDevice> edgeDevices = new ArrayList<>();

        sortFogDevicesByLevelDesc(devices);

        for(FogDevice fogDevice: devices){

            edgeDevices.add(new EdgeDevice(
                    fogDevice.getId(),
                    fogDevice.getName(),
                    fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getTotalMips(),
                    fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getRam(),
                    (int)fogDevice.getVmAllocationPolicy().getHost(fogDevice.getHost().getId(), fogDevice.getId()).getBw(),
                    (int)fogDevice.getUplinkLatency()
                    ));
        }
        return  edgeDevices;
    }

    private static void sortFogDevicesByLevelDesc(List<FogDevice> fogDevices) {
        Collections.sort(fogDevices, new Comparator<FogDevice>() {
            @Override
            public int compare(FogDevice device1, FogDevice device2) {
                return Integer.compare(device2.getLevel(), device1.getLevel());
            }
        });
    }
}

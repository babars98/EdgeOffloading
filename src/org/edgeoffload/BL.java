package org.edgeoffload;

import org.edgeoffload.model.FogDeviceResource;
import org.edgeoffload.model.ResourceRequirement;
import org.fog.application.AppEdge;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.entities.FogDevice;

import java.util.ArrayList;
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
}

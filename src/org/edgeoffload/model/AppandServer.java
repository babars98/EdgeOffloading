package org.edgeoffload.model;

import org.fog.application.Application;
import org.fog.entities.FogDevice;

import java.util.List;

public class AppandServer {

    private List<Application> applicationList;
    private FogDevice fogDevice;

    public AppandServer(FogDevice fogDevice, List<Application> applications) {
        this.fogDevice = fogDevice;
        this.applicationList = applications;
    }

    public FogDevice getFogDevice() {
        return fogDevice;
    }

    public List<Application> getApplicationList() {
        return applicationList;
    }

}

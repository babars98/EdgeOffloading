package org.edgeoffload.model;

public class FogDeviceResource {
    private String name;
    private long availableMips;
    private int availableRam;
    private long availableBandwidth;
    private double latency;

    public FogDeviceResource(String name, long availableMips, int availableRam, long availableBandwidth, double latency) {
        this.name = name;
        this.availableMips = availableMips;
        this.availableRam = availableRam;
        this.availableBandwidth = availableBandwidth;
        this.latency = latency;
    }

    public String getName() {
        return name;
    }

    public long getAvailableMips() {
        return availableMips;
    }

    public int getAvailableRam() {
        return availableRam;
    }

    public long getAvailableBandwidth() {
        return availableBandwidth;
    }

    public double getLatency() {
        return latency;
    }
}

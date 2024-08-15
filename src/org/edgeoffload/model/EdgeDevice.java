package org.edgeoffload.model;

import java.util.ArrayList;
import java.util.List;

public class EdgeDevice {

    private final double resourceLimit = 0.9;
    private int id;
    private String name;
    private int totalMips;
    private int totalRam;
    private int availableMips;
    private int availableRam;
    private int latency;
    private int totalBW;
    private int availableBW;
    private List<Task> tasks;

    public EdgeDevice(int id, String name, int mips, int ram, int bw, int latency) {
        this.id = id;
        this.name = name;
        this.totalMips = mips;
        this.totalRam = ram;
        this.availableMips = mips;
        this.availableRam = ram;
        this.totalBW = bw;
        this.availableBW = bw;
        this.latency = latency;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        availableMips -= task.getMips();
        availableRam -= task.getRam();
        availableBW -= task.getBW();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        availableMips += task.getMips();
        availableRam += task.getRam();
        availableBW += task.getBW();
    }

    public boolean canAccommodate(Task task) {
        return availableMips * resourceLimit >= task.getMips()
                && availableRam * resourceLimit >= task.getRam()
                && availableBW >= task.getBW();
    }

    public List<Task> getTasks() { return tasks; }
    public String getName(){ return name; }
    public int getTotalMips() { return totalMips; }
    public int getTotalRam() { return totalRam; }
    public int getAvailableMips() { return availableMips; }
    public int getAvailableRam() { return availableRam; }
    public int getAvailableBW() { return availableBW; }
    public int getTotalBW () { return totalBW; }
    public int getLatency() {return latency;}
    public int getId() { return id; }
}

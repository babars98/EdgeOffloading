package org.edgeoffload.model;

import java.util.ArrayList;
import java.util.List;

public class EdgeDevice {
    private String id;
    private int totalMips;
    private int totalRam;
    private int availableMips;
    private int availableRam;
    private int latency;
    private List<Task> tasks;

    public EdgeDevice(String id, int mips, int ram, int latency) {
        this.id = id;
        this.totalMips = mips;
        this.totalRam = ram;
        this.availableMips = mips;
        this.availableRam = ram;
        this.latency = latency;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        availableMips -= task.getMips();
        availableRam -= task.getRam();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        availableMips += task.getMips();
        availableRam += task.getRam();
    }

    public boolean canAccommodate(Task task) {
        return availableMips >= task.getMips() && availableRam >= task.getRam();
    }

    public List<Task> getTasks() { return tasks; }
    public int getTotalMips() { return totalMips; }
    public int getTotalRam() { return totalRam; }
    public int getAvailableMips() { return availableMips; }
    public int getAvailableRam() { return availableRam; }
    public int getLatency() {return latency;}
    public String getId() { return id; }
}

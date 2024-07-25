package org.edgeoffload.model;

public class Task {

    private String id;
    private String uniqueID;
    private int mips;
    private int ram;
    private boolean isDependent;

    public Task(String id, String uniqueID, int mips, int ram, boolean isDependent) {
        this.id = id;
        this.uniqueID = uniqueID;
        this.mips = mips;
        this.ram = ram;
        this.isDependent = isDependent;
    }

    private String getId() {return id;}
    private String getUniqueID() { return uniqueID; }
    public int getMips() { return mips; }
    public int getRam() { return ram; }
    public boolean isDependent() { return isDependent; }
}

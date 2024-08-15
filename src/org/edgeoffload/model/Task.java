package org.edgeoffload.model;

public class Task {

    private String id;
    private String uniqueID;
    private int mips;
    private int ram;
    private int bw;
    private boolean isDependent;

    public Task(String id, String uniqueID, int mips, int ram, int bw, boolean isDependent) {
        this.id = id;
        this.uniqueID = uniqueID;
        this.mips = mips;
        this.ram = ram;
        this.bw = bw;
        this.isDependent = isDependent;
    }

    public String getId() {return id;}
    public String getUniqueID() { return uniqueID; }
    public int getMips() { return mips; }
    public int getRam() { return ram; }
    public int getBW() { return bw; }
    public boolean isDependent() { return isDependent; }
}

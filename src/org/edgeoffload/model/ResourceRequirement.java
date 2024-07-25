package org.edgeoffload.model;

public class ResourceRequirement {
        private long mips;
        private int ram;
        private long bandwidth;

        public ResourceRequirement(long mips, int ram, long bandwidth) {
            this.mips = mips;
            this.ram = ram;
            this.bandwidth = bandwidth;
        }

        public long getMips() {
            return mips;
        }

        public int getRam() {
            return ram;
        }

        public long getBandwidth() {
            return bandwidth;
        }
}

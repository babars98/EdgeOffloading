package org.Offload;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import java.util.*;


public class FogTest {

    static int numOfFogDevices = 10;
    static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
    static Map<String, Integer> getIdByName = new HashMap<String, Integer>();
    private static void createFogDevices() {
        FogDevice cloud = createAFogDevice("cloud", 44800, 40000, 100, 10000, 0, 0.01, 16*103, 16*83.25);
        cloud.setParentId(-1);
        fogDevices.add(cloud);
        getIdByName.put(cloud.getName(), cloud.getId());
        for(int i=0;i<numOfFogDevices;i++){
            FogDevice device = createAFogDevice("FogDevice-"+i, (long)getValue(12000, 15000), (int)getValue(4000, 8000),
                    (long)getValue(200, 300), (long)getValue(500, 1000), 1, 0.01, getValue(100,120), getValue(70, 75));
            device.setParentId(cloud.getId());
            device.setUplinkLatency(10);
            fogDevices.add(device);
            getIdByName.put(device.getName(), device.getId());}
    }
    private static FogDevice createAFogDevice(String nodeName, long mips, int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));
        int hostId = FogUtils.generateEntityId();
        long storage = 1000000;
        int bw = 10000;
        PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerOverbooking(bw), storage, peList, new StreamOperatorScheduler(peList), new FogLinearPowerModel(busyPower, idlePower));
        List<Host> hostList = new ArrayList<Host>();
        hostList.add(host);
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<Storage>();
        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, cost,
                costPerMem, costPerStorage, costPerBw);
        FogDevice fogdevice = null;
        try {
            FogDevice fd = new FogDevice(nodeName, characteristics,
                    new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
            fogdevice = fd;
        }

        catch (Exception e) {
            e.printStackTrace();}
        fogdevice.setLevel(level);
        return fogdevice;
    }

    private static double getValue(int min, int max)
    {
        Random r = new Random();
        double randomValue = min + (max - min) * r.nextDouble();
        return randomValue;
    }

    public static void main(String[] args) {

        Log.disable();
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        String appId = "test_app";

        createFogDevices();

        for(FogDevice fogDevice : fogDevices){
            System.out.println("Device ID : " + fogDevice.getId());
            System.out.println("Device Name : " + fogDevice.getName());
            System.out.println("Device Level : " + fogDevice.getLevel());
            System.out.println("\n");
        }
    }
}


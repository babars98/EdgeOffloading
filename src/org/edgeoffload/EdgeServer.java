package org.edgeoffload;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.Application;
import org.fog.entities.Actuator;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.distribution.DeterministicDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EdgeServer {

    private final String sensorTupleType = "SENSOR";
    private final String acuatorType = "PTZ_CONTROL";
    private HashMap<String, Double> sensorLatency = new HashMap<String, Double>();
    private HashMap<String, Double> actuatorLatency = new HashMap<String, Double>();

    public EdgeServer(){
        setSensorLatencies();
    }

    public FogDevice createEdgeDevice1(){
        FogDevice dept = createAFogDevice("FogDevice1", 5000, 2048, 10000, 10000, 1, 0.0, 107.339, 83.4333);
        dept.setUplinkLatency(5);

        return dept;
    }

    public FogDevice createEdgeDevice2(){
        FogDevice dept = createAFogDevice("FogDevice2", 10000, 4096, 10000, 10000, 1, 0.0, 107.339, 83.4333);
        dept.setUplinkLatency(7);
        return dept;
    }

    public FogDevice createEdgeDevice3(){
        FogDevice dept = createAFogDevice("FogDevice3", 25000, 12000, 10000, 10000, 1, 0.0, 107.339, 83.4333);
        dept.setUplinkLatency(12);

        return dept;
    }

    public FogDevice createMobileDevice(){
        FogDevice mobile = createAFogDevice("mobile", 3000, 1024, 10000, 270, 3, 0, 87.53, 82.44);
        mobile.setUplinkLatency(1);

        return mobile;
    }

    public FogDevice createCloud() {
        org.fog.entities.FogDevice cloud = createAFogDevice("cloud", 44000, 32000, 10000, 10000, 0, 0.01, 16*103, 16*83.25); // creates the fog device Cloud at the apex of the hierarchy with level=0
        cloud.setParentId(-1);
        cloud.setUplinkLatency(100);
        return cloud;
    }

    public List<FogDevice> getFogDevicesList(){
        List<FogDevice> fogDevices = new ArrayList<>();
        fogDevices.add(createEdgeDevice1());
        fogDevices.add(createEdgeDevice2());
        fogDevices.add(createEdgeDevice3());
        fogDevices.add(createMobileDevice());
        fogDevices.add(createCloud());

        return fogDevices;
    }

    public Sensor setupSensor(Application app, FogDevice edgeDevice) {
        // Define and add sensors specific to this app
        Sensor sensor = new Sensor("sensor-"+app.getAppId(), sensorTupleType, app.getUserId(), app.getAppId(), new DeterministicDistribution(5.0));

        sensor.setGatewayDeviceId(edgeDevice.getId());
        sensor.setLatency(sensorLatency.get(edgeDevice.getName()));

        return sensor;
    }

    public Actuator setupActuator(Application app, FogDevice edgeDevice) {
        // Define and add actuators specific to this app
        Actuator actuator = new Actuator("actuator-"+app.getAppId(), app.getUserId(), app.getAppId(), acuatorType);

        actuator.setGatewayDeviceId(edgeDevice.getId());
        actuator.setLatency(actuatorLatency.get(edgeDevice.getName()));

        return actuator;
    }

    private void setSensorLatencies() {

        //sensor latency for each edge server
        sensorLatency.put("FogDevice1", 3.0);
        sensorLatency.put("FogDevice2", 5.0);
        sensorLatency.put("FogDevice3", 7.0);
        sensorLatency.put("mobile", 1.0);

        //actuator latency for each server
        actuatorLatency.put("FogDevice1", 3.0);
        actuatorLatency.put("FogDevice2", 5.0);
        actuatorLatency.put("FogDevice3", 7.0);
        actuatorLatency.put("mobile", 1.0);
    }

    private static org.fog.entities.FogDevice createAFogDevice(String nodeName, long mips, int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));
        int hostId = FogUtils.generateEntityId();
        long storage = 1000000;
        int bw = 100000;
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
        org.fog.entities.FogDevice fogdevice = null;
        try {
            org.fog.entities.FogDevice fd = new org.fog.entities.FogDevice(nodeName, characteristics,
                    new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
            fogdevice = fd;
        }

        catch (Exception e) {
            e.printStackTrace();}
        fogdevice.setLevel(level);
        return fogdevice;
    }
}

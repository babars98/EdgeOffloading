package org.edgeoffload;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.Application;
import org.fog.application.DAG;
import org.fog.entities.FogDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

public class OffloadMain {

    public static void main(String[] args) {

        Log.disable();
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        String appId = "edge_offload";


        ApplicationHandler applicationHandler = new ApplicationHandler();
        ArrayList<Application> applicationslist = applicationHandler.ListApplications();

        ApplicationDependency applicationDependency = new ApplicationDependency();

        org.fog.application.DAG dag = applicationDependency.createApplicationDepedency();

        Stack stack = dag.topologicalSort();

        // Print contents of stack
        while (stack.empty() == false)
            System.out.print(stack.pop() + " ");

        System.out.println("DAG Representation:");


    }
}

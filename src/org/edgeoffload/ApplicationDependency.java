package org.edgeoffload;

import org.fog.application.DAG;
import org.fog.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ApplicationDependency {

    public org.fog.application.DAG createApplicationDepedency(){


        ApplicationHandler applicationHandler = new ApplicationHandler();

        Application cameraApp = applicationHandler.getCameraApplicaion();
        Application imageProcesingApp = applicationHandler.getImageProcessorApplication();
        Application imageClassificationApp = applicationHandler.getImageClassificationApplication();

        List<String> appList = Arrays.asList(cameraApp.getAppId(), imageProcesingApp.getAppId(), imageClassificationApp.getAppId());

        DAG dag = new DAG(appList);


        // Define dependencies for Application 1
        dag.addEdge(cameraApp.getAppId(), imageProcesingApp.getAppId());

        // Define dependencies for Application 2
        dag.addEdge(imageProcesingApp.getAppId(), imageClassificationApp.getAppId());


        return dag;
    }

    public List<List<Application>> createApplicationDepdendencyList(List<Application> applications, Stack dag){

        List<List<Application>> appsList = new ArrayList<>();

        List<Application> list = new ArrayList<>();

        //Add list of dependent application the list
        while (dag.empty() == false){

            Application app = BL.getApplicationbyName(applications, dag.pop().toString());
            list.add(app);
            applications.remove(app);
        }
        appsList.add(list);

        //Add all the independent applications to the list
        for(Application app: applications){
            List<Application> application = new ArrayList<>();
            application.add(app);
            appsList.add(application);
        }

        return appsList;

    }

}

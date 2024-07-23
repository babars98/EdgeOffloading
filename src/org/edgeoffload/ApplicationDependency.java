package org.edgeoffload;

import org.fog.application.DAG;
import org.edgeoffload.model.Module;
import org.fog.application.Application;

import java.util.Arrays;
import java.util.List;

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

}

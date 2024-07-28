package org.edgeoffload;

import org.edgeoffload.model.Task;
import org.fog.application.DAG;
import org.fog.application.Application;

import java.util.*;

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

    public List<Task> createApplicationDepdendencyList(List<Application> applications, List<Application> independentApps, List<Stack> dag){

        List<Task> list = new ArrayList<>();

        //Add all the independent tasks to list first
        for(Application app: independentApps){

            list.add(new Task(
                    app.getAppId(),
                    UUID.randomUUID().toString(),
                    BL.calculateMips(app),
                    BL.calculateRam(app),
                    false
            ));
        }

        //Add list of dependent application the list
        for(Stack stack: dag){

            String uId =  UUID.randomUUID().toString();

            while (stack.empty() == false){
                Application app = BL.getApplicationbyName(applications, stack.pop().toString());

                list.add(new Task(
                        app.getAppId(),
                        uId,
                        BL.calculateMips(app),
                        BL.calculateRam(app),
                        true
                ));
            }
        }

        return list;

    }

}

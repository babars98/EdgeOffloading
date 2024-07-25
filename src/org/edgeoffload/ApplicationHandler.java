package org.edgeoffload;

import org.fog.application.Application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationHandler {

    private int user_id = 1;

    public ArrayList<Application> ListApplications(){

        ArrayList<Application> applicationslist = new ArrayList<Application>();

        ApplicationBuilder appBuilder = new ApplicationBuilder();

        //create applications
        Application camApp = appBuilder.createCameraApplication("CameraApplication", user_id);
        Application processorApp = appBuilder.createImageProcessorApplication("ImageProcessorApplication", user_id);
        Application classificationApp = appBuilder.createImageClassificationApplication("ImageClassificationApplication", user_id);
        Application tempApp = appBuilder.createTempControlApplication("TemperatureControl", user_id);
        Application uniDirApp = appBuilder.createUniDirectionalApplication("UnidirectionalApplication", user_id);

        //add all applications to a list
        applicationslist.add(camApp);
        applicationslist.add(processorApp);
        applicationslist.add(classificationApp);
        applicationslist.add(tempApp);
        applicationslist.add(uniDirApp);

        return applicationslist;

    }

    public Application getCameraApplicaion(){
        return new ApplicationBuilder().createCameraApplication("CameraApplication", user_id);
    }
    public Application getImageProcessorApplication(){
        return new ApplicationBuilder().createImageProcessorApplication("ImageProcessorApplication", user_id);
    }
    public Application getImageClassificationApplication(){
        return new ApplicationBuilder().createImageClassificationApplication("ImageClassificationApplication", user_id);
    }
    public Application getTempControlApplication(){
        return new ApplicationBuilder().createTempControlApplication("TemperatureControl", user_id);
    }
    public Application getUniDirectionalApplication(){
        return new ApplicationBuilder().createUniDirectionalApplication("UnidirectionalApplication", user_id);
    }

}

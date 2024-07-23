package org.edgeoffload;

import org.fog.application.Application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationHandler {

    public ArrayList<Application> ListApplications(){

        ArrayList<Application> applicationslist = new ArrayList<Application>();

        ApplicationBuilder appBuilder = new ApplicationBuilder();

        //create applications
        Application camApp = appBuilder.createCameraApplication("CameraApplication", 1);
        Application processorApp = appBuilder.createImageProcessorApplication("ImageProcessorApplication", 1);
        Application classificationApp = appBuilder.createImageClassificationApplication("ImageClassificationApplication", 1);
        Application tempApp = appBuilder.createTempControlApplication("TemperatureControl", 1);
        Application uniDirApp = appBuilder.createUniDirectionalApplication("UnidirectionalApplication", 1);

        //add all applications to a list
        applicationslist.add(camApp);
        applicationslist.add(processorApp);
        applicationslist.add(classificationApp);
        applicationslist.add(tempApp);
        applicationslist.add(uniDirApp);

        return applicationslist;

    }

    public Application getCameraApplicaion(){
        return new ApplicationBuilder().createCameraApplication("CameraApplication", 1);
    }
    public Application getImageProcessorApplication(){
        return new ApplicationBuilder().createImageProcessorApplication("ImageProcessorApplication", 1);
    }
    public Application getImageClassificationApplication(){
        return new ApplicationBuilder().createImageClassificationApplication("ImageClassificationApplication", 1);
    }
    public Application getTempControlApplication(){
        return new ApplicationBuilder().createTempControlApplication("TemperatureControl", 1);
    }
    public Application getUniDirectionalApplication(){
        return new ApplicationBuilder().createUniDirectionalApplication("UnidirectionalApplication", 1);
    }

}

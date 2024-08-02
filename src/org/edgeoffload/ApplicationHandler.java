package org.edgeoffload;

import org.fog.application.Application;

import java.util.ArrayList;

public class ApplicationHandler {

    private int user_id;

    public ApplicationHandler(int userId){
        this.user_id = userId;
    }

    public ArrayList<Application> ListApplications(){

        ArrayList<Application> applicationslist = new ArrayList<Application>();

        ApplicationBuilder appBuilder = new ApplicationBuilder();

        //create applications
        Application camApp = appBuilder.createCameraApplication("CameraApplication", user_id);
        Application processorApp = appBuilder.createImageProcessorApplication("ImageProcessorApplication", user_id);
        Application tempApp = appBuilder.createClientApplication("ClientApplication", user_id);
        Application uniDirApp = appBuilder.createUniDirectionalApplication("UnidirectionalApplication", user_id);

        //add all applications to a list
        applicationslist.add(camApp);
        applicationslist.add(processorApp);
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
    public Application getClientApplication(){
        return new ApplicationBuilder().createClientApplication("ClientApplication", user_id);
    }
    public Application getUniDirectionalApplication(){
        return new ApplicationBuilder().createUniDirectionalApplication("UnidirectionalApplication", user_id);
    }

}

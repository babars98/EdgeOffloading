package org.edgeoffload;

import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ApplicationBuilder {
    public Application createCameraApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("picture-capture", 256);
        application.addAppModule("slot-detector", 1024);

        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */
        application.addAppEdge("SENSOR", "picture-capture", 1000, 700, "CAMERA", Tuple.UP, AppEdge.SENSOR); // adding edge from CAMERA (sensor) to Motion Detector module carrying tuples of type CAMERA
        application.addAppEdge("picture-capture", "slot-detector",
                1500, 500, "slots",Tuple.UP, AppEdge.MODULE);
        // adding edge from Slot Detector to PTZ CONTROL (actuator)
        application.addAppEdge("slot-detector", "PTZ_CONTROL", 100,
                800, 500, "PTZ_PARAMS",
                Tuple.UP, AppEdge.ACTUATOR);


        application.addTupleMapping("picture-capture", "SENSOR", "slots",
                new FractionalSelectivity(1.0));
        application.addTupleMapping("slot-detector", "slots",
                "PTZ_PARAMS", new FractionalSelectivity(1.0));

        final AppLoop loop1 = new AppLoop(new ArrayList<String>()
        {{add("SENSOR");
            add("picture-capture");add("slot-detector");
            add("PTZ_CONTROL");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
        application.setLoops(loops);
        return application;

    }

    public Application createImageProcessorApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("object_detector", 512);
        application.addAppModule("motion_detector", 512);
        application.addAppModule("object_tracker", 512);
        application.addAppModule("user_interface", 1024);

        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */
        application.addAppEdge("SENSOR", "motion_detector", 1000, 2000, "CAMERA", Tuple.UP, AppEdge.SENSOR); // adding edge from CAMERA (sensor) to Motion Detector module carrying tuples of type CAMERA
        application.addAppEdge("motion_detector", "object_detector", 2000, 2000, "MOTION_VIDEO_STREAM", Tuple.UP, AppEdge.MODULE); // adding edge from Motion Detector to Object Detector module carrying tuples of type MOTION_VIDEO_STREAM
        application.addAppEdge("object_detector", "user_interface", 500, 2000, "DETECTED_OBJECT", Tuple.UP, AppEdge.MODULE); // adding edge from Object Detector to User Interface module carrying tuples of type DETECTED_OBJECT
        application.addAppEdge("object_detector", "object_tracker", 1000, 100, "OBJECT_LOCATION", Tuple.UP, AppEdge.MODULE); // adding edge from Object Detector to Object Tracker module carrying tuples of type OBJECT_LOCATION
        application.addAppEdge("object_tracker", "PTZ_CONTROL", 100, 300, 100, "PTZ_PARAMS", Tuple.DOWN, AppEdge.ACTUATOR); // adding edge from Object Tracker to PTZ CONTROL (actuator) carrying tuples of type PTZ_PARAMS

        /*
         * Defining the input-output relationships (represented by selectivity) of the application modules.
         */
        application.addTupleMapping("motion_detector", "SENSOR", "MOTION_VIDEO_STREAM", new FractionalSelectivity(1.0)); // 1.0 tuples of type MOTION_VIDEO_STREAM are emitted by Motion Detector module per incoming tuple of type CAMERA
        application.addTupleMapping("object_detector", "MOTION_VIDEO_STREAM", "OBJECT_LOCATION", new FractionalSelectivity(1.0)); // 1.0 tuples of type OBJECT_LOCATION are emitted by Object Detector module per incoming tuple of type MOTION_VIDEO_STREAM
        application.addTupleMapping("object_detector", "MOTION_VIDEO_STREAM", "DETECTED_OBJECT", new FractionalSelectivity(0.05)); // 0.05 tuples of type MOTION_VIDEO_STREAM are emitted by Object Detector module per incoming tuple of type MOTION_VIDEO_STREAM

        /*
         * Defining application loops (maybe incomplete loops) to monitor the latency of.
         * Here, we add two loops for monitoring : Motion Detector -> Object Detector -> Object Tracker and Object Tracker -> PTZ Control
         */
        final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("motion_detector");add("object_detector");add("object_tracker");}});
        final AppLoop loop2 = new AppLoop(new ArrayList<String>(){{add("object_tracker");add("PTZ_CONTROL");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);add(loop2);}};

        application.setLoops(loops);
        return application;

    }

    public Application createClientApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)

        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("client", 10); // adding module Client to the application model
        application.addAppModule("concentration_calculator", 10); // adding module Concentration Calculator to the application model
        application.addAppModule("connector", 10); // adding module Connector to the application model

        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */

        application.addAppEdge("SENSOR", "client", 3000, 500, "EEG", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("client", "concentration_calculator", 3500, 500, "_SENSOR", Tuple.UP, AppEdge.MODULE); // adding edge from Client to Concentration Calculator module carrying tuples of type _SENSOR
        application.addAppEdge("concentration_calculator", "connector", 100, 1000, 1000, "PLAYER_GAME_STATE", Tuple.UP, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Concentration Calculator to Connector module carrying tuples of type PLAYER_GAME_STATE
        application.addAppEdge("concentration_calculator", "client", 14, 500, "CONCENTRATION", Tuple.DOWN, AppEdge.MODULE);  // adding edge from Concentration Calculator to Client module carrying tuples of type CONCENTRATION
        application.addAppEdge("connector", "client", 100, 28, 1000, "GLOBAL_GAME_STATE", Tuple.DOWN, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Connector to Client module carrying tuples of type GLOBAL_GAME_STATE
        application.addAppEdge("client", "PTZ_CONTROL", 1000, 500, "SELF_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type SELF_STATE_UPDATE
        application.addAppEdge("client", "PTZ_CONTROL", 1000, 500, "GLOBAL_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type GLOBAL_STATE_UPDATE

        /*
         * Defining the input-output relationships (represented by selectivity) of the application modules.
         */
        application.addTupleMapping("client", "SENSOR", "_SENSOR", new FractionalSelectivity(0.9)); // 0.9 tuples of type _SENSOR are emitted by Client module per incoming tuple of type EEG
        application.addTupleMapping("client", "CONCENTRATION", "SELF_STATE_UPDATE", new FractionalSelectivity(1.0)); // 1.0 tuples of type SELF_STATE_UPDATE are emitted by Client module per incoming tuple of type CONCENTRATION
        application.addTupleMapping("concentration_calculator", "_SENSOR", "CONCENTRATION", new FractionalSelectivity(1.0)); // 1.0 tuples of type CONCENTRATION are emitted by Concentration Calculator module per incoming tuple of type _SENSOR
        application.addTupleMapping("client", "GLOBAL_GAME_STATE", "GLOBAL_STATE_UPDATE", new FractionalSelectivity(1.0)); // 1.0 tuples of type GLOBAL_STATE_UPDATE are emitted by Client module per incoming tuple of type GLOBAL_GAME_STATE

        /*
         * Defining application loops to monitor the latency of.
         * Here, we add only one loop for monitoring : EEG(sensor) -> Client -> Concentration Calculator -> Client -> DISPLAY (actuator)
         */
        final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("SENSOR");add("client");add("concentration_calculator");add("client");add("PTZ_CONTROL");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
        application.setLoops(loops);

        return application;
    }

    public Application createUniDirectionalApplication(String appId, int userId) {

        Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)

        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("clientModule", 128);
        application.addAppModule("mService1", 128);
        application.addAppModule("mService2", 128);
        application.addAppModule("mService3", 256);

        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */

        application.addAppEdge("SENSOR", "clientModule", 500, 200, "SENSOR", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("clientModule", "mService1", 500, 150, "RAW_DATA", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("mService1", "mService2", 300, 170, "FILTERED_DATA1", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("mService1", "mService3", 800, 200, "FILTERED_DATA2", Tuple.UP, AppEdge.MODULE);

        application.addAppEdge("mService2", "clientModule", 14, 200, "RESULT1", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("mService3", "clientModule", 28, 150, "RESULT2", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("clientModule", "PTZ_CONTROL", 14, 170, "RESULT1_DISPLAY", Tuple.DOWN, AppEdge.ACTUATOR);
        application.addAppEdge("clientModule", "PTZ_CONTROL", 14, 150, "RESULT2_DISPLAY", Tuple.DOWN, AppEdge.ACTUATOR);


        /*
         * Defining the input-output relationships (represented by selectivity) of the application modules.
         */
        application.addTupleMapping("clientModule", "SENSOR", "RAW_DATA", new FractionalSelectivity(0.9));
        application.addTupleMapping("mService1", "RAW_DATA", "FILTERED_DATA1", new FractionalSelectivity(1.0));
        application.addTupleMapping("mService1", "RAW_DATA", "FILTERED_DATA2", new FractionalSelectivity(1.0));
        application.addTupleMapping("mService2", "FILTERED_DATA1", "RESULT1", new FractionalSelectivity(1.0));
        application.addTupleMapping("mService3", "FILTERED_DATA2", "RESULT2", new FractionalSelectivity(1.0));
        application.addTupleMapping("clientModule", "RESULT1", "RESULT1_DISPLAY", new FractionalSelectivity(1.0));
        application.addTupleMapping("clientModule", "RESULT2", "RESULT2_DISPLAY", new FractionalSelectivity(1.0));

        final AppLoop loop1 = new AppLoop(new ArrayList<String>() {{
            add("SENSOR");
            add("clientModule");
            add("mService1");
            add("mService2");
            add("clientModule");
            add("PTZ_CONTROL");
        }});

        List<AppLoop> loops = new ArrayList<AppLoop>() {{
            add(loop1);
        }};
        application.setLoops(loops);

        return application;
    }

    public Application createDataApplication(String appId, int userId) {
        Application application = Application.createApplication(appId, userId);

        // Adding modules with RAM and MIPS requirements
        application.addAppModule("data_preprocessing", 512, 1000, 200); // 1500 MIPS, 512 MB RAM
        application.addAppModule("data_analysis", 512, 1000, 200); // 2500 MIPS, 1024 MB RAM
        application.addAppModule("data_storage", 256, 500, 300); // 1000 MIPS, 2048 MB RAM

        // Adding edges with bandwidth requirements
        application.addAppEdge("SENSOR", "data_preprocessing", 2000, 1000, "SENSOR_DATA", Tuple.UP, AppEdge.SENSOR); // 2000 bytes, 1000ms
        application.addAppEdge("data_preprocessing", "data_analysis", 5000, 500, "PROCESSED_DATA", Tuple.UP, AppEdge.MODULE); // 5000 bytes, 500ms
        application.addAppEdge("data_analysis", "data_storage", 10000, 200, "ANALYZED_DATA", Tuple.UP, AppEdge.MODULE); // 10000 bytes, 200ms

        // Tuple mapping
        application.addTupleMapping("data_preprocessing", "SENSOR_DATA", "PROCESSED_DATA", new FractionalSelectivity(1.0));
        application.addTupleMapping("data_analysis", "PROCESSED_DATA", "ANALYZED_DATA", new FractionalSelectivity(1.0));

        // Creating an application loop (optional, for monitoring purposes)
        final AppLoop loop = new AppLoop(new ArrayList<String>() {{
            add("SENSOR");
            add("data_preprocessing");
            add("data_analysis");
            add("data_storage");
        }});
        List<AppLoop> loops = new ArrayList<AppLoop>() {{
            add(loop);
        }};
        application.setLoops(loops);

        return application;
    }
}

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
        application.addAppModule("image-capture", 256);
        application.addAppModule("image-processor", 512);
        application.addAppModule("image-store", 128);
        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */
        application.addAppEdge("CAMERA", "image-capture", 700, 500, "CAMERA", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("image-capture", "image-processor",
                1500, 1000, "RAW_IMAGE",Tuple.UP, AppEdge.MODULE);
        // adding edge from Slot Detector to PTZ CONTROL (actuator)
        application.addAppEdge("image-processor", "image-store", 0,
                600, 500, "PROCESSED_IMAGE",
                Tuple.UP, AppEdge.MODULE);

        application.addTupleMapping("image-capture", "CAMERA", "SENSOR",
                new FractionalSelectivity(1.0));
        application.addTupleMapping("image-processor", "_SENSOR",
                "IMAGE", new FractionalSelectivity(1.0));
        application.addTupleMapping("image-store", "IMAGE",
                "IMAGE", new FractionalSelectivity(1.0));

        final AppLoop loop = new AppLoop(new ArrayList<String>()
        {{add("CAMERA");
            add("Image-capture");
            add("image-processor");
            add("image-store");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop);}};
        application.setLoops(loops);
        return application;
    }

    public Application createImageProcessorApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("image-retrieve", 256);
        application.addAppModule("image-processor", 512);
        application.addAppModule("image-store", 128);
        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */
        application.addAppEdge("image-retrieve", "image-processor", 800, 1500, "IMAGE", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("image-processor", "image-store",
                2000, 1500, "PROCESSED_IMAGE",Tuple.UP, AppEdge.MODULE);
        // adding edge from Slot Detector to PTZ CONTROL (actuator)

        application.addTupleMapping("image-retrieve", "IMAGE", "IMAGE",
                new FractionalSelectivity(1.0));
        application.addTupleMapping("image-processor", "IMAGE",
                "PROCESSED_IMAGE", new FractionalSelectivity(1.0));
        application.addTupleMapping("image-store", "PROCESSED_IMAGE",
                "PROCESSED_IMAGE", new FractionalSelectivity(1.0));

        final AppLoop loop = new AppLoop(new ArrayList<String>()
        {{
            add("Image-retrieve");
            add("image-processor");
            add("image-store");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop);}};
        application.setLoops(loops);
        return application;
    }

    public Application createImageClassificationApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("image-retrieve", 256);
        application.addAppModule("image-classifier", 2048);
        application.addAppModule("image-store", 128);
        /*
         * Connecting the application modules (vertices) in the application model (directed graph) with edges
         */
        application.addAppEdge("image-retrieve", "image-classifier", 800, 1500, "IMAGE", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("image-classifier", "image-store",
                3000, 1500, "CLASSIFIED_IMAGE",Tuple.UP, AppEdge.MODULE);
        // adding edge from Slot Detector to PTZ CONTROL (actuator)

        application.addTupleMapping("image-retrieve", "IMAGE", "IMAGE",
                new FractionalSelectivity(1.0));
        application.addTupleMapping("image-classifier", "IMAGE",
                "CLASSIFIED_IMAGE", new FractionalSelectivity(1.0));
        application.addTupleMapping("image-store", "CLASSIFIED_IMAGE",
                "CLASSIFIED_IMAGE", new FractionalSelectivity(1.0));

        final AppLoop loop = new AppLoop(new ArrayList<String>()
        {{
            add("Image-retrieve");
            add("image-classifier");
            add("image-store");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop);}};
        application.setLoops(loops);
        return application;
    }

    public Application createTempControlApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        /*
         * Adding modules (vertices) to the application model (directed graph)
         */
        application.addAppModule("temp-sensor", 128);
        application.addAppModule("temp-analyzer", 256);

        application.addAppEdge("temp-sensor", "temp-analyzer", 1000, 500, "SENSOR", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("temp-analyzer", "fan-controller", 1000, 500, "FAN_CONTROL", Tuple.UP, AppEdge.ACTUATOR);

        application.addTupleMapping("temp-sensor", "SENSOR", "TEMP_DATA",
                new FractionalSelectivity(1.0));
        application.addTupleMapping("temp-analyzer", "TEMP_DATA",
                "OUTPUT_DATA", new FractionalSelectivity(1.0));

        final AppLoop loop = new AppLoop(new ArrayList<String>()
        {{
            add("temp-sensor");
            add("temp-analyzer");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop);}};
        application.setLoops(loops);
        return application;
    }

    public Application createUniDirectionalApplication(String appId, int userId) {

        Application application = Application.createApplication(appId, userId);

        application.addAppModule("Module1", 256);
        application.addAppModule("Module2", 256);
        application.addAppModule("Module3", 512);
        application.addAppModule("Module4", 1024);

        application.addAppEdge("Sensor", "Module1", 500, 500, "Sen-sor", Tuple.UP, AppEdge.SENSOR);
        application.addAppEdge("Module1", "Module2", 700, 1000, "Pro-cessedData-1", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("Module2", "Module3", 1000, 1000, "Pro-cessedData-2", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("Module3", "Module4", 1500, 1000, "Pro-cessedData-3", Tuple.UP, AppEdge.MODULE);
        application.addAppEdge("Module4", "Module1", 2000, 1000, "Pro-cessedData-4", Tuple.DOWN, AppEdge.MODULE);
        application.addAppEdge("Module1", "Actuators", 800, 50, "Out-putData", Tuple.DOWN, AppEdge.ACTUATOR);

        application.addTupleMapping("Module1", "Sensor", "Processed-Data-1", new FractionalSelectivity(1.0));
        application.addTupleMapping("Module2", "ProcessedData-1", "ProcessedData-2", new FractionalSelectivity(1.0));
        application.addTupleMapping("Module3", "ProcessedData-2", "ProcessedData-3", new FractionalSelectivity(1.0));
        application.addTupleMapping("Module4", "ProcessedData-3", "ProcessedData-4", new FractionalSelectivity(1.0));
        application.addTupleMapping("Module1", "ProcessedData-4", "OutputData", new FractionalSelectivity(1.0));

        final AppLoop loop = new AppLoop(new ArrayList<String>() {
            {
                add("Sensor");
                add("Module1");
                add("Mod-ule2");
                add("Module3");
                add("Module4");
                add("Module1");
                add("Actuator");
            }
        });
        List<AppLoop> loops = new ArrayList<AppLoop>() {{
            add(loop);
        }};
        application.setLoops(loops);
        return application;
    }
}

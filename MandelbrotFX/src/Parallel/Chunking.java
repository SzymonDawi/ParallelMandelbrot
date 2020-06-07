package Parallel;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Chunking implements Runnable{
    //the class controls the chunking and initialises the scheduler.
    private ArrayList<chunk> chunk_array;
    private String schedulingPolicy = "block";
    private final String string_num_threads;
    private int num_threads;
    private final int chunkSize;
    private final String chunkMethod;
    private final String viewSelection;
    private final int height = 720;
    private final int width = 1280;
    private WritableImage mandelbrotImage;
    private PixelWriter mandelbrotPixelWriter;
    private WritableImage visualisationImage;
    private PixelWriter visualisationPixelWriter;
    private long startTime;
    private Label actualTimeElapsed;
    private double timeElapsed;
    private long endTime;
    private int numberOfIterations;
    private String colours[] = {"0xE3170A", "0xF75C03", "0xFAA613", "0xF3DE2C", "0xF0F66E"};
    private Button startButton;

    private volatile static boolean exit = false;

    public Chunking(Button startButton, int numberOfIterations, Label actualTimeElapsed, WritableImage mandelbrotImage, WritableImage visualisationImage, String schedulingPolicy, String string_num_threads, int chunkSize, String chunkMethod, String viewSelection) {
        this.mandelbrotImage = mandelbrotImage;
        this.visualisationImage = visualisationImage;
        this.schedulingPolicy = schedulingPolicy;
        this.string_num_threads = string_num_threads;
        this.chunkMethod = chunkMethod;
        this.chunkSize = chunkSize;
        this.viewSelection = viewSelection;
        this.actualTimeElapsed = actualTimeElapsed;
        this.numberOfIterations = numberOfIterations;
        this.startButton = startButton;
        mandelbrotPixelWriter = mandelbrotImage.getPixelWriter();
        visualisationPixelWriter = visualisationImage.getPixelWriter();
        if(string_num_threads.equals("True Sequential")) {
            //TODO: true sequential code
        } else {
            this.num_threads = Integer.parseInt(string_num_threads);
            chunk_array = new ArrayList<chunk>();
        }
    }

    @Override
    public void run(){
        startTime = System.nanoTime();
        if(string_num_threads.equals("True Sequential")) {
            Sequential();
        } else {
                createChunks(chunkMethod);
                Scheduler Schedule = new Scheduler(numberOfIterations, num_threads,"static",chunk_array);
                Schedule.run();
                List<Future<?>> futures = Schedule.getFutures();
                boolean running = true;
                int done_task = 0;
                while(running){
                    if(done_task == futures.size()){
                        running = false;
                    }

                    done_task = 0;
                    for (Future<?> future : futures) {
                        if (future.isDone()) {
                            done_task++;
                        }
                    }

                    try {
                        Thread.sleep(1);
                        Platform.runLater(() -> {
                            synchronized(chunk_array) {
                                endTime = System.nanoTime();
                                timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
                                actualTimeElapsed.setText("Running... " + timeElapsed + "s");
                            }
                        });

                    } catch (Exception ignored) {

                    }
                        synchronized(chunk_array) {
                            for (Parallel.chunk chunk : chunk_array) {
                                for (int j = 0; j < chunk.getSize(); j++) {
                                    if (!chunk.getPixel(j).getColour().equals("0xFFFFFF")) {
                                        try{
                                            visualisationPixelWriter.setColor(chunk.getPixel(j).getX(), chunk.getPixel(j).getY(), Color.web(chunk.getPixel(j).getVisualisationColour()));
                                            mandelbrotPixelWriter.setColor(chunk.getPixel(j).getX(), chunk.getPixel(j).getY(), Color.web(chunk.getPixel(j).getColour()));
                                        }catch (Exception e){

                                        }
                                    }
                                }
                            }
                        }
                }
                for (Parallel.chunk chunk : chunk_array) {
                    for (int j = 0; j < chunk.getSize(); j++) {
                        try{
                            visualisationPixelWriter.setColor(chunk.getPixel(j).getX(), chunk.getPixel(j).getY(), Color.web(chunk.getPixel(j).getVisualisationColour()));
                            mandelbrotPixelWriter.setColor(chunk.getPixel(j).getX(), chunk.getPixel(j).getY(), Color.web(chunk.getPixel(j).getColour()));
                        }catch (Exception e){

                        }
                    }
                }
                //tells the javaFX GUI thread to update the image.
                try {
                    Platform.runLater(() -> {
                        synchronized(chunk_array) {
                            endTime = System.nanoTime();
                            timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
                            actualTimeElapsed.setText("Running... " + timeElapsed + "s");
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        try {
            Platform.runLater(() -> {
                actualTimeElapsed.setText("Finished after " + timeElapsed + "s");
                startButton.setDisable(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Sequential(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double c_re = (j - width / 2.0) * 4.0 / width;
                double c_im = (i - height / 2.0) * 4.0 / width;
                double x = 0, y = 0;
                int iteration = 0;
                while (x * x + y * y <= 4 && iteration < numberOfIterations) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }

                if (iteration < numberOfIterations) {
                    if (iteration < 2) {
                        mandelbrotPixelWriter.setColor(j, i, Color.web(colours[0]));
                    } else if (iteration < 10) {
                        mandelbrotPixelWriter.setColor(j, i, Color.web(colours[1]));
                    } else if (iteration < 15) {
                        mandelbrotPixelWriter.setColor(j, i, Color.web(colours[2]));
                    } else if (iteration < 25) {
                        mandelbrotPixelWriter.setColor(j, i, Color.web(colours[3]));
                    } else {
                        mandelbrotPixelWriter.setColor(j, i, Color.web(colours[4]));
                    }
                } else {
                    mandelbrotPixelWriter.setColor(j, i, Color.web("0x000000"));
                }
                visualisationPixelWriter.setColor(j, i, Color.LIGHTBLUE);
            }
            try {
                Platform.runLater(() -> {
                        endTime = System.nanoTime();
                        timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
                        actualTimeElapsed.setText("Running... " + timeElapsed + "s");
                });
            }catch(Exception e){

            }
        }
    }

    public void createChunks(String type){
        switch (schedulingPolicy) {
            case "Static-block":
                blockChunking(type);
                break;
            case "Static-Cyclic":
                cyclicChunking(type, chunkSize);
                break;
            case "Dynamic":
                dynamicChunking(type, chunkSize);
                break;
            case "Guided":
                guidedChunking(type);
                break;
        }
    }

    public ArrayList<chunk> getChunk(){
        return chunk_array;
    }

    private chunk chunkType(String type, int currentPos) {
        chunk currentChunk = new chunk();
        if (type.equals("by Row")) {
            for (int j = 0; j < width; j++) {
                currentChunk.add(j, currentPos);
            }
        } else {
            for (int i = 0; i < height; i++) {
                currentChunk.add(currentPos, i);
            }
        }
        return currentChunk;
    }


    private void blockChunking(String type){
        int length_1 = 0, length_2 = 0;
        for (int c=0;c<num_threads;c++){
            chunk_array.add(new chunk());
        }

        if(type.equals("by Row")){
            length_1 = height;
            length_2 = width;
        }else {
            length_1 = width;
            length_2 = height;
        }

        for(int k =0; k <num_threads; k++) {
            System.out.println("creating chunk "+k);
            for (int i =0; i < length_1 / num_threads; i++) {
                for (int j = 0; j < length_2; j++) {
                    if(length_1 == height) {
                        chunk_array.get(k).add(j, i + ((height / num_threads) * k));
                    }else {
                        chunk_array.get(k).add(i + ((width / num_threads) * k), j);
                    }
                }
            }
        }
    }

    private void cyclicChunking(String type, int chunkSize){
        int currentCore = 0;
        int max;
        if(type.equals("by Row"))max = height;else max = width;

        for (int c=0;c<num_threads;c++){
            chunk_array.add(new chunk());
        }
        for(int i =0;i< max; i++){
            for(int j =0; j < chunkSize; j++) {
                chunk_array.get(currentCore).appendChunks(chunkType(type, i));
                i++;
            }
            currentCore++;
            if (currentCore >= num_threads) {
                currentCore = 0;
            }
            i--;
        }
    }

    private void dynamicChunking(String type, int chunkSize){
        int max;
        int index =0;
        int count = 0;
        int chunkArrayPos = 0;
        if(type.equals("by Row")) max = height; else max = width;
        while (index<max){
            while(count<chunkSize){
                if(count == 0 ){
                    chunk_array.add(new chunk());
                }
                chunk_array.get(chunkArrayPos).appendChunks(chunkType(type,index));
                System.out.println(chunkArrayPos);
                index++;
                count++;
                if(index>=max) break;
            }

            chunkArrayPos++;
            count = 0;
        }
    }


    private void guidedChunking(String type){
        int max;
        int index =0;
        int chunkSize = 30;
        int count = 0;
        int i = 0;
        int chunkArrayPos = 0;
        if(type.equals("by Row")) max = height; else max = width;
        while (index<max){
            while(count<chunkSize){
                if(count ==0){
                    chunk_array.add(new chunk());

                }
                chunk_array.get(chunkArrayPos).appendChunks(chunkType(type,index));
                System.out.println(chunkArrayPos);
                index++;
                count++;
                if(index>=max) break;
            }
            i++;
            if(i>=num_threads && chunkSize>1){
                chunkSize--;
                i = 0;
            }
            chunkArrayPos++;
            count = 0;
        }
    }
}

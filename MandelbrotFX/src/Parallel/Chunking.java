package Parallel;

import MandelbrotGUI.MandelbrotGUIController;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Chunking implements Runnable{
    //the class controls the chunking and initialises the scheduler.
    chunk chunk_array[];
    ImageView imageView;
    private List<Future<?>> futures = new ArrayList<Future<?>>();
    public Chunking(ImageView imageView) {
        synchronized(imageView) {
            this.imageView = imageView;
        }
    }

    @Override
    public void run(){
        WritableImage img = new WritableImage(1280, 720);
        PixelWriter pw  = img.getPixelWriter();
        int cores = 10;
        int chunkingMethod = 1;
        chunk_array = createChunks(cores, 1280, 720, chunkingMethod);
        Scheduler Schedule = new Scheduler(cores,"static",chunk_array);
        Schedule.run();
        int test = 0;
        futures = Schedule.getFutures();
        //blocking until the threads finish
        synchronized(chunk_array) {
            for (int i = 0; i < futures.size(); i++) {
                while (!futures.get(i).isDone()) {
                    //updates the local image.
                    try {
                        for (int k = 0; k < chunk_array.length; k++) {
                            for (int j = 0; j < chunk_array[k].getsize(); j++) {
                                pw.setColor(chunk_array[k].getPixel(j).getX(), chunk_array[k].getPixel(j).getY(), Color.web(chunk_array[k].getPixel(j).getColour()));
                            }
                        }
                    } catch (Exception e) {

                    }
                    try{
                        Thread.sleep(600);
                    }catch (Exception e){

                    }

                    //tells the javaFX GUI thread to update the image.
                    try {
                        Platform.runLater(() -> {
                            imageView.setImage(img);
                        });
                    }catch (Exception e){
                        System.out.println("test");
                    }
                }
            }
        }
        for(Future<?> future : Schedule.getFutures()){
            try{
                future.get();
            }catch(Exception e){
                System.out.println("test4");
            }
        }

        try {
            for (int k = 0; k < chunk_array.length; k++) {
                for (int j = 0; j < chunk_array[k].getsize(); j++) {
                    pw.setColor(chunk_array[k].getPixel(j).getX(), chunk_array[k].getPixel(j).getY(), Color.web(chunk_array[k].getPixel(j).getColour()));
                }
            }
        } catch (Exception e) {

        }
        //tells the javaFX GUI thread to update the image.
        try {
            Platform.runLater(() -> {
                imageView.setImage(img);
            });
        }catch (Exception e){
            System.out.println("test");
        }
    }

    public static chunk[] createChunks(int numCores, int width, int height,int chunkingMethod){
        //chunks the data.
        int i,j;
        int currentCore = 0;
        int xCounter;
        chunk chunkArray[] = new chunk[numCores];
        int totalPixels = width*height;
        for (i=0;i<numCores;i++){
            chunkArray[i] = new chunk();
        }

        for(i=0;i<height;i++){
            for(j=0;j<width;j++){
                chunkArray[currentCore].add(j,i);
            }
            currentCore++;
            if (currentCore>= numCores){currentCore =0;}
        }

        return chunkArray;
    }

    public chunk[] getChunk(){
        return chunk_array;
    }
}

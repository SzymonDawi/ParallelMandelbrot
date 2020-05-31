package Parallel;

import MandelbrotGUI.MandelbrotGUIController;
import MandelbrotGUI.MandelbrotMain;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.scene.image.*;

import javax.imageio.ImageIO;
import javafx.scene.paint.Color;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Chunking implements Runnable{
    //the class controls the chunking and initialises the scheduler.
    private chunk chunk_array[];
    private String chunkMethod = "block";
    private int num_threads;
    private int height = 720;
    private int width = 1280;
    private  ImageView imageView;
    private WritableImage img = new WritableImage(1280, 720);
    private PixelWriter pw  = img.getPixelWriter();

    private List<Future<?>> futures = new ArrayList<Future<?>>();

    public Chunking(ImageView imageView, String chunkMethod, int num_threads) {
        this.imageView = imageView;
        this.chunkMethod = chunkMethod;
        this.num_threads = num_threads;
        chunk_array = new chunk[num_threads];
    }

    @Override
    public void run(){

        createChunks();
        Scheduler Schedule = new Scheduler(num_threads,"static",chunk_array);
        Schedule.run();
        futures = Schedule.getFutures();
        boolean running = true;
        int done_task = 0;
        while(running){
            /*try {
                Thread.sleep(100);
            }catch (Exception e){

            }*/


            if(done_task == futures.size()){
                running = false;
            }

            done_task = 0;
            for (int i = 0; i < futures.size(); i++) {
                if(futures.get(i).isDone()){
                    done_task++;
                }
            }

            try {
                Thread.sleep(1);
                Platform.runLater(() -> {
                    synchronized(chunk_array) {
                        imageView.setImage(img);
                    }
                });

            } catch (Exception e) {

            }

            synchronized(chunk_array) {
                for (int k = 0; k < chunk_array.length; k++) {
                    for (int j = 0; j < chunk_array[k].getsize(); j++) {
                        if (chunk_array[k].getPixel(j).getColour() == "0xFFFFFF") {
                            continue;
                        } else {
                            pw.setColor(chunk_array[k].getPixel(j).getX(), chunk_array[k].getPixel(j).getY(), Color.web(chunk_array[k].getPixel(j).getColour()));
                        }
                    }
                }
            }

            /*try {
                Platform.runLater(() -> {
                    synchronized(chunk_array) {
                        imageView.setImage(img);
                    }
                });

            } catch (Exception e) {

            }*/
        }

        //tells the javaFX GUI thread to update the image.
        try {
            Platform.runLater(() -> {
                synchronized(chunk_array) {
                    imageView.setImage(img);
                }
            });
        }catch (Exception e){

        }
    }

    public void createChunks(){
        //chunks the data.
        for (int i=0;i<num_threads;i++){
            chunk_array[i] = new chunk();
        }

        if(chunkMethod == "Static-block"){
            blockChunking();
        }else if(chunkMethod == "Static-Cyclic"){
            cyclicChunking();
        }
    }

    public chunk[] getChunk(){
        return chunk_array;
    }

    private void blockChunking(){
        for(int k =0; k <num_threads; k++) {
            System.out.println("creating chunk "+k);
            for (int i =0; i < height / num_threads; i++) {
                for (int j = 0; j < width; j++) {
                    chunk_array[k].add(j, i+((height/num_threads)*k));
                }
            }
        }
    }

    private void cyclicChunking(){
        int currentCore = 0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                chunk_array[currentCore].add(j,i);
            }
            currentCore++;
            if (currentCore>= num_threads){currentCore =0;}
        }
    }
}

package Parallel;

import MandelbrotGUI.MandelbrotGUIController;
import javafx.application.Platform;
import javafx.scene.image.*;

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
    String chunkMethod = "block";
    int num_threads;
    int height = 720;
    int width = 1280;
    ImageView imageView;
    private List<Future<?>> futures = new ArrayList<Future<?>>();

    public Chunking(ImageView imageView, String chunkMethod, int num_threads) {
        this.imageView = imageView;
        this.chunkMethod = chunkMethod;
        this.num_threads = num_threads;
        chunk_array = new chunk[num_threads];
    }

    @Override
    public void run(){
        WritableImage img = new WritableImage(1280, 720);
        PixelWriter pw  = img.getPixelWriter();

        int chunkingMethod = 1;
        createChunks();
        Scheduler Schedule = new Scheduler(num_threads,"static",chunk_array);
        Schedule.run();
        int test = 0;
        futures = Schedule.getFutures();
        //blocking until the threads finish

        boolean running = true;
        int done_task = 0;
        while(running){
            if(done_task == futures.size()){
                running = false;
            }

            done_task = 0;
            for (int i = 0; i < futures.size(); i++) {
                if(futures.get(i).isDone()){
                    done_task++;
                }
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
                try {
                    Thread.sleep(200);
                    Platform.runLater(() -> {
                        imageView.setImage(img);
                    });

                } catch (Exception e) {

                }
            }
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

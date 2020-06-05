package Parallel;

import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Chunking implements Runnable{
    //the class controls the chunking and initialises the scheduler.
    private ArrayList<chunk> chunk_array;
    private String chunkMethod = "block";
    private final String string_num_threads;
    private int num_threads;
    private final int height = 720;
    private final int width = 1280;
    private final ImageView imageView;
    private final WritableImage img = new WritableImage(1280, 720);
    private final PixelWriter pw  = img.getPixelWriter();

    private volatile static boolean exit = false;

    public Chunking(ImageView imageView, String chunkMethod, String string_num_threads) {
        this.imageView = imageView;
        this.chunkMethod = chunkMethod;
        this.string_num_threads = string_num_threads;
        if(string_num_threads.equals("True Sequential")) {
            //TODO: true sequential code
        } else {
            this.num_threads = Integer.parseInt(string_num_threads);
            chunk_array = new ArrayList<chunk>();
        }
    }

    //allow us to exit loop when user presses stop
    public static void setExit(boolean exitStatus) { exit = exitStatus;}

    @Override
    public void run(){
        if(string_num_threads.equals("True Sequential")) {
            System.out.println("Ran True Sequential");
        } else {
            exit = false;
            while(!exit) {
                createChunks("by col"); //TODO: not hardcoded
                Scheduler Schedule = new Scheduler(num_threads,"static",chunk_array);
                Schedule.run();
                List<Future<?>> futures = Schedule.getFutures();
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
                    for (Future<?> future : futures) {
                        if (future.isDone()) {
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

                    } catch (Exception ignored) {

                    }

                    synchronized(chunk_array) {
                        for (Parallel.chunk chunk : chunk_array) {
                            for (int j = 0; j < chunk.getSize(); j++) {
                                if (!chunk.getPixel(j).getColour().equals("0xFFFFFF")) {

                                    pw.setColor(chunk.getPixel(j).getX(), chunk.getPixel(j).getY(), Color.web(chunk.getPixel(j).getColour()));
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
                }catch (Exception e) {
                    e.printStackTrace();
                }
                exit = true;
            }
        }
    }

    public void createChunks(String type){
        //chunks the data.

        switch (chunkMethod) {
            case "Static-block":
                blockChunking(type);
                break;
            case "Static-Cyclic":
                cyclicChunking(type);
                break;
            case "Dynamic":
                dynamicChunking(type,2);  //TODO: not hardcoded
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
        if (type.equals("by row")) {
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

        for (int c=0;c<num_threads;c++){
            chunk_array.add(new chunk());
        }

        for(int k =0; k <num_threads; k++) {
            System.out.println("creating chunk "+k);

            //TODO: add code for row or col
            for (int i =0; i < height / num_threads; i++) {
                for (int j = 0; j < width; j++) {
                    chunk_array.get(k).add(j, i+((height/num_threads)*k));
                }
            }
        }
    }

    private void cyclicChunking(String type){
        int currentCore = 0;
        int max;
        if(type.equals("by row"))max = height;else max = width;

        for (int c=0;c<num_threads;c++){
            chunk_array.add(new chunk());
        }
        for(int i =0;i< max; i++){
            chunk_array.get(currentCore).appendChunks(chunkType(type,i));
            currentCore++;
            if (currentCore>= num_threads){currentCore =0;}
        }
    }

    private void dynamicChunking(String type, int chunkSize){
        int max;
        int index =0;
        int count = 0;
        int chunkArrayPos = 0;
        if(type.equals("by row")) max = height; else max = width;
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
        if(type.equals("by row")) max = height; else max = width;
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

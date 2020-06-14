package Parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.CountDownLatch;

public class Scheduler implements Runnable{
    private final ArrayList<chunk> chunk_array;
    private int num_jobs = 0;
    private final ScheduledExecutorService scheduler;
    private final List<Future<?>> futures = new ArrayList<Future<?>>();
    private int numberOfIterations;
    private double zoom;
    private double offset_y;
    private double offset_x;

    public Scheduler(int numberOfIterations, int num_threads, String type, ArrayList<chunk> chunkArray, double zoom, double offset_y, double offset_x){
        chunk_array = chunkArray;
        scheduler = Executors.newScheduledThreadPool(num_threads);
        this.numberOfIterations = numberOfIterations;
        this.zoom = zoom;
        this.offset_y = offset_y;
        this.offset_x = offset_x;
    }

    @Override
    public void run(){
        //assigns the jobs to threads and starts them all at the same time.
        CountDownLatch latch = new CountDownLatch(1);
        try{
            synchronized(chunk_array) {
                System.out.println(chunk_array.size());
                for (Parallel.chunk chunk : chunk_array) {
                    Jobs task = new Jobs(num_jobs, numberOfIterations, chunk, latch, zoom, offset_y, offset_x);
                    Future<?> f = scheduler.submit(task);
                    futures.add(f);
                    num_jobs++;
                }
            }
            latch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<Future<?>> getFutures(){
        return futures;
    }
}

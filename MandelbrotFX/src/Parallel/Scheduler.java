package Parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.CountDownLatch;

public class Scheduler implements Runnable{
    private int num_threads = 0;
    private chunk chunk_array[];
    private int num_jobs = 0;
    private ScheduledExecutorService scheduler;
    private List<Future<?>> futures = new ArrayList<Future<?>>();

    public Scheduler(int num_threads, String type, chunk chunkArray[]){
        this.num_threads = num_threads;
        chunk_array = chunkArray;
        scheduler = Executors.newScheduledThreadPool(num_threads);
    }

    @Override
    public void run(){
        //assigns the jobs to threads and starts them all at the same time.
        CountDownLatch latch = new CountDownLatch(1);
        for(int i = 0; i < chunk_array.length; i++){
            Jobs task = new Jobs(num_jobs, 1000000, chunk_array[i],latch);
            Future<?> f= scheduler.submit(task);
            futures.add(f);
            num_jobs++;
        }
        latch.countDown();
    }

    public List<Future<?>> getFutures(){
        return futures;
    }
}

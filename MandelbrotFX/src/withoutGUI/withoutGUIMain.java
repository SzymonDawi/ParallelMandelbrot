package withoutGUI;


import Parallel.Chunking;

import java.util.concurrent.TimeUnit;

public class withoutGUIMain {
    public static void main(String[] args) {
        long startTime;
        double timeElapsed;
        long endTime;
        int numberOfIterations = 10000;
        String schedulingPolicy = "Static-block";
        String stringNumberOfThreads = "1";
        int chunkSize = 1;
        String chunkMethod = "by Row";
        startTime = System.nanoTime();
        Chunking t1 = new Chunking( numberOfIterations, schedulingPolicy, stringNumberOfThreads, chunkSize,
                chunkMethod);
        Thread th = new Thread(t1);
        th.setDaemon(true);
        th.start();
        try {
            th.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
        endTime = System.nanoTime();
        timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
        System.out.println("Number of iterations selected: " + numberOfIterations);
        System.out.println("Scheduling policy selected: " + schedulingPolicy);
        System.out.println("Number of threads selected: " + stringNumberOfThreads);
        System.out.println("Chunk size selected: " + chunkSize);
        System.out.println("Chunking method selected: " + chunkMethod);
        System.out.println("Time taken: " + timeElapsed + "s");
        System.exit(0);
    }
}

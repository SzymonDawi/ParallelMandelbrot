package withoutGUI;


import Parallel.Chunking;

import java.util.concurrent.TimeUnit;

public class withoutGUIMain {
    public static void main(String[] args) {
        long startTime;
        double timeElapsed;
        long endTime;
        //Choose number of iterations, a number greater than zero. The higher the number, the longer the process will take.
        int numberOfIterations = 10000;
        //Choose a scheduling policy: "Static-block", "Static-Cyclic", "Dynamic", "Guided".
        String schedulingPolicy = "Static-cyclic";
        //Choose a number of threads to choose from, between 1 and the total number of threads your system has.
        String stringNumberOfThreads = "1";
        //Choose a chunk size between 1 and 1280, this will be the number of rows or columns in each chunk.
        int chunkSize = 720;
        //Choose whether you want to chunk by row or column: "by Row", "by Column".
        String chunkMethod = "by Row";
        //Choose which fractal you want to process, choose between 1, 2 or 3.
        int fractal = 1;
        startTime = System.nanoTime();
        Chunking t1 = new Chunking( numberOfIterations, schedulingPolicy, stringNumberOfThreads, chunkSize,
                chunkMethod, fractal);
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
        System.out.println("Fractal selected: " + fractal);
        System.out.println("Time taken: " + timeElapsed + "s");
        System.exit(0);
    }
}

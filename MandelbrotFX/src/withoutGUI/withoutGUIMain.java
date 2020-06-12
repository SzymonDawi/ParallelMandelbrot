package withoutGUI;


import Parallel.Chunking;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class withoutGUIMain {
    public static void main(String[] args) {
        long startTime;
        double timeElapsed;
        long endTime;
        double timeElapsed1 = 0;
        double timeElapsed2 = 0;
        //Choose number of iterations, a number greater than zero. The higher the number, the longer the process will take.
        int numberOfIterationsArray[] = {1000, 10000, 30000};
        //Choose a scheduling policy: "Static-block", "Static-Cyclic", "Dynamic", "Guided".
        //String schedulingPolicy = "Guided";
        //String schedulingPolicyArray[] = {"Static-block", "Static-Cyclic", "Dynamic", "Guided"};
        String schedulingPolicyArray[] = {"Static-block", "Static-Cyclic", "Dynamic", "Guided"};
        //Choose a number of threads to choose from, between 1 and the total number of threads your system has.
        //String stringNumberOfThreads = "6";
        //String stringNumberOfThreadsArray[] = {"2", "4", "6", "8", "10", "12"};
        String stringNumberOfThreadsArray[] = {"True Sequential", "1"};
        //Choose a chunk size between 1 and 1280, this will be the number of rows or columns in each chunk.
        int chunkSize = 720;
        //int chunkSizeArray[] = {1, 2, 3};
        //Choose whether you want to chunk by row or column: "by Row", "by Column".
        //String chunkMethod = "by Row";
        String chunkMethodArray[] = {"by Row", "by Column"};
        //Choose which fractal you want to process, choose between 1, 2 or 3.
        //int fractal = 2;
        int fractalArray[] = {1, 2, 3};
        double columns = 1280;
        double rows = 720;
        double averageTime;
        DecimalFormat df = new DecimalFormat("#.000");
        Chunking t1;
        Thread th;
        for (String stringNumberOfThreads : stringNumberOfThreadsArray) {
            for(int fractal : fractalArray) {
                for(String schedulingPolicy : schedulingPolicyArray) {
                    for (String chunkMethod : chunkMethodArray) {
                        //for(int chunkSizeIndicator : chunkSizeArray) {
                            for (int numberOfIterations : numberOfIterationsArray) {
                                for(int timeCount = 1; timeCount <= 4; timeCount++) {
//                                    if(chunkSizeIndicator == 1) {
//                                        chunkSize = chunkSizeIndicator;
//                                    } else if (chunkSizeIndicator == 2){
//                                        if(chunkMethod.equals("by Row")) {
//                                            chunkSize = (int) Math.ceil(rows/(4.00 * Integer.parseInt(stringNumberOfThreads)));
//                                        } else {
//                                            chunkSize = (int) Math.ceil(columns/(4.00 * Integer.parseInt(stringNumberOfThreads)));
//                                        }
//                                    } else {
//                                        if(chunkMethod.equals("by Row")) {
//                                            chunkSize = (int) Math.ceil(rows/(2.00 * Integer.parseInt(stringNumberOfThreads)));
//                                        } else {
//                                            chunkSize = (int) Math.ceil(columns/(2.00 * Integer.parseInt(stringNumberOfThreads)));
//                                        }
//                                    }
                                    startTime = System.nanoTime();
                                    t1 = new Chunking( numberOfIterations, schedulingPolicy, stringNumberOfThreads, chunkSize,
                                            chunkMethod, fractal);
                                    th = new Thread(t1);
                                    th.setDaemon(true);
                                    th.start();
                                    try {
                                        th.join();
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                    endTime = System.nanoTime();
                                    timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
                                    if(timeCount == 1 ) {
                                    } else if (timeCount == 2) {
                                        timeElapsed1 = timeElapsed;
                                    } else if (timeCount == 3) {
                                        timeElapsed2 = timeElapsed;
                                    } else {
                                        averageTime = (timeElapsed1 + timeElapsed2 + timeElapsed)/3.000;
                                        averageTime = Double.parseDouble(df.format(averageTime));
                                        System.out.println(stringNumberOfThreads + ", " + fractal + ", " + schedulingPolicy + ", " + chunkMethod + ", " + "N/A" + ", " + numberOfIterations + ", " + timeElapsed1 + ", " + timeElapsed2 + ", " + timeElapsed + ", " + averageTime);
                                    }
                                }
                            }
                       // }
                    }
                }
            }
        }

//        System.out.println("Number of iterations selected: " + numberOfIterations);
//        System.out.println("Scheduling policy selected: " + schedulingPolicy);
//        System.out.println("Number of threads selected: " + stringNumberOfThreads);
//        System.out.println("Chunk size selected: " + chunkSize);
//        System.out.println("Chunking method selected: " + chunkMethod);
//        System.out.println("Fractal selected: " + fractal);
//        System.out.println("Time taken: " + timeElapsed + "s");
        System.exit(0);
    }
}

package Parallel;

import MandelbrotGUI.MandelbrotGUIController;

import java.util.concurrent.CountDownLatch;

public class Jobs implements Runnable{
    private int id_task;
    private int iteration_num = 1000;
    private chunk c;
    private int width = 1280;
    private int height = 720;
    private String colours[] = {"0xE3170A", "0xF75C03", "0xFAA613", "0xF3DE2C", "0xF0F66E"};
    private CountDownLatch latch;
    private int size = 0;

    public Jobs(int id, int iteration_num, chunk c, CountDownLatch latch){
        id_task = id;
        this.iteration_num = iteration_num;
        this.c = c;
        size = c.getSize();
        this.latch = latch;
    }

    @Override
    public void run(){
        try{
            //waiting for the 'GO' signal from the scheduler.
            latch.await();
        } catch(Exception e){
            System.out.println("ERROR while awaiting latch");
        }

        //runs mandelbrot on the chunk.
        System.out.println("Task "+id_task+" is running");
        for (int i = 0; i < size; i++) {
            synchronized(c) {
                double c_re = (c.getPixel(i).getX() - width / 2.0) * 4.0 / width;
                double c_im = (c.getPixel(i).getY() - height / 2.0) * 4.0 / width;
                double x = 0, y = 0;
                int iteration = 0;
                while (x * x + y * y <= 4 && iteration < iteration_num) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }

                if (iteration < iteration_num) {
                    //System.out.println("iteration " + iteration );
                    if (iteration < 2) {
                        c.getPixel(i).setColour(colours[0]);
                    } else if (iteration < 10) {
                        c.getPixel(i).setColour(colours[1]);
                    } else if (iteration < 15) {
                        c.getPixel(i).setColour(colours[2]);
                    } else if (iteration < 25) {
                        c.getPixel(i).setColour(colours[3]);
                    } else {
                        c.getPixel(i).setColour(colours[4]);
                    }

                } else {
                    c.getPixel(i).setColour("0x000000");
                }
            }
        }
        System.out.println("Task " + id_task + " is done");
    }
}

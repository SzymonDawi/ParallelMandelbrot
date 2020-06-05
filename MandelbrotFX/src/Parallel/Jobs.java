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
    private long numberOfThread;


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
        numberOfThread = Thread.currentThread().getId();
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
                //System.out.println("ActiveThread" + Thread.currentThread().getId());
                c.getPixel(i).setVisualisationColour(getThreadNumberColour((int)numberOfThread));
            }
        }
        System.out.println("Task " + id_task + " is done");
    }

    private String getThreadNumberColour(int threadNumber) {
        switch ((threadNumber) % 32) {
            case 0:
                return "#000000";
            case 1:
                return "#2F4F4F";
            case 2:
                return "#708090";
            case 3:
                return "#696969";
            case 4:
                return "#800000";
            case 5:
                return "#A0522D";
            case 6:
                return "#808000";
            case 7:
                return "#DAA520";
            case 8:
                return "#BC8F8F";
            case 9:
                return "#FFDEAD";
            case 10:
                return "#000080";
            case 11:
                return "#4169E1";
            case 12:
                return "#ADD8E6";
            case 13:
                return "#00FFFF";
            case 14:
                return "#556B2F";
            case 15:
                return "#9ACD32";
            case 16:
                return "#98FB98";
            case 17:
                return "#FFFF00";
            case 18:
                return "#FF8C00";
            case 19:
                return "#DC143C";
            case 20:
                return "#CD5C5C";
            case 21:
                return "#4B0082";
            case 22:
                return "#483D8B";
            case 23:
                return "#8B008B";
            case 24:
                return "#BA55D3";
            case 25:
                return "#D8BFD8";
            case 26:
                return "#FF69B4";
            case 27:
                return "#FFC0CB";
            case 28:
                return "#BDB76B";
            case 29:
                return "#7CFC00";
            case 30:
                return "#B0C4DE";
            case 31:
                return "#FAEBD7";
        }
        //unreachable
        return "#ffffff";
    }
}

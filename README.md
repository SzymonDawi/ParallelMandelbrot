# README
## Java Mandelbrot Parallelisation
This project supplies the user with a Graphical User Interface to show the impact of parallelisation techniques and aspects when considering the Mandelbrot set. This project is based on the work [here](https://math.hws.edu/eck/js/mandelbrot/java/MB-java.html), but does not use any of their work in the implementation.  
### Motivation
Several concepts regarding parallelisation can be hard to understand. This progrma should make it easy to see the differences that are made when the number of threads or parallelisation technique is changed. This will impact the time and show how the image work is split up. The overall motivation is to reinforce parallelisation techniques, especially regarding the Mandelbrot set.  
### Run the Application
To run this application please start the provided jar file: MandelbrotGuiMain.jar  
You will then be presented with a GUI, select your preferred settings and click "Start Parallelising Mandelbrot".  
### Options
* Number of Threads  
    * Here you decide how many different threads you would like to use to complete the parallelisation of the Mandelbrot set.  
    * The number of threads will range from 1 to the number available in your CPU.  
    * You can select "True Sequential" here and this will run the Mandelbrot set on one thread without any of the scheduling.  
* Scheduling Policy
    * Here you can decide which scheduling policy you will use to parallelise the Mandelbrot set.
    * Static-block, Static-cyclic, Dynamic and Guided are currently supported.
* Chunk Size
    * Here you can decide what chunk size you will use to parallelise the Mandelbrot set.
    * All images are currently 1280x720 therefore you have 921,600 pixels that could be parallelised. As one thread will always have this chunk size, when you have two or more threads you will have the option from 1 to 460,800 pixels as your chunk size.
* Change Image View
    * Here you will decide if you will see the finished image or a related image that shows what sections are being worked on at each point in time.
    * Selecting "Mandelbrot" will show the Mandelbrot image, selecting "Parallel Visualiation" will show what threads are working on what part of the image.
### Technology
* Maven
* Java Executor and ExecutorService
* JavaFX
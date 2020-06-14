# README
## Java Mandelbrot Parallelisation
This project supplies the user with a Graphical User Interface to show the impact of parallelisation techniques and aspects when considering the Mandelbrot set. This project is based on the work [here](https://math.hws.edu/eck/js/mandelbrot/java/MB-java.html), but does not use any of their work in the implementation.  
### Motivation
Several concepts regarding parallelisation can be hard to understand. This program should make it easy to see the differences that are made when the number of threads or parallelisation technique is changed. This will impact the time and show how the image work is split up. The overall motivation is to reinforce parallelisation techniques, especially regarding the Mandelbrot set.  
### Run the Application
* Clone the repository onto your local environment
* Open the project in your IDE of choice
* Run maven install
* Run main class: MandelbrotFX.src.main.Main
* You will then be presented with a GUI, select your preferred settings and click "Start Parallelising Mandelbrot".  
### Options
* Number of Threads  
    * Here you decide how many different threads you would like to use to complete the parallelisation of the Mandelbrot set.  
    * The number of threads will range from 1 to the number available in your CPU.  
    * You can select "True Sequential" here and this will run the Mandelbrot set on one thread without any of the scheduling.  
* Scheduling Policy
    * Here you can decide which scheduling policy you will use to parallelise the Mandelbrot set.
    * Static-block, Static-cyclic, Dynamic and Guided are currently supported.
* Chunk Method
    * Here you can select between chunking by column or chunking by row.
    * This will change how work is assigned to threads.
* Chunk Size
    * Here you can decide what chunk size you will use to parallelise the Mandelbrot set.
    * The work is chunked by column or row.
    * All images are currently 1280x720 therefore you have either 1280 or 720 columns or rows that could be parallelised.
    * You can can choose any number between 1 and 1280 or 720.
* Change Image View
    * Here you will decide if you will see the finished image or a related image that shows what sections are being worked on at each point in time.
    * Selecting "Mandelbrot" will show the Mandelbrot image, selecting "Parallel Visualisation" will show what threads are working on what part of the image.
    * At this stage colours are not set to specific threads and the different colours are maxed out 32 threads, if you use more threads, colours will repeat.
* Change Number of Iterations
    * Here you can select from a range between 10 and 100000 iterations to pass through the Mandelbrot set.
    * This will be the maximum number of iterations a pixel will complete the Mandelbrot set (if not finished before then).
    * The number of iterations will impact the time it takes to complete the tasks and the overall colours shown by the Mandelbrot Image.
* Change Fractal
    * Here you can select between fractal 1, 2 or 3.
    * This will provide different fractals to run the mandelbrot set on.
    * The output image will look different and take a varied amount of time when the change the fractal.

### Technology
* Maven
* Java Executor and ExecutorService
* JavaFX
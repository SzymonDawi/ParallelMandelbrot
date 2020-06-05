package MandelbrotGUI;

import Parallel.Chunking;
import Parallel.Scheduler;
import Parallel.chunk;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MandelbrotGUIController implements Initializable {

    //Setting up various ComboBoxes with their Labels
    @FXML private ComboBox threadsComboBox;
    @FXML private ComboBox schedulingComboBox;
    private String threadsComboBoxSelection;
    private String schedulingComboBoxSelection;
    @FXML private Label threadsLabel;
    @FXML private Label schedulingLabel;

    //setting up chunk size elements
    @FXML private ComboBox chunkMethodComboBox;
    @FXML private ComboBox chunkSizeComboBox;
    @FXML private Label chunkMethodLabel;
    @FXML private Label chunkSizeLabel;
    //@FXML private Label labelPlaceHolder;
    private String chunkMethodSelection;
    private int chunkSizeSelection;

    //Setting up view
    @FXML private Label viewLabel;
    @FXML private ComboBox viewComboBox;
    private String viewSelection;

    //Setting up time
    @FXML private Label timeElapsedLabel;
    @FXML private Label actualTimeElapsed;
    private double timeElapsed;
    private long startTime;
    private long endTime;

    //Setting up start button
    @FXML private Button startButton;

    //set up the image that will be shown
    @FXML private ImageView shownImage;

    public void pressStartButton(ActionEvent event) {
        if(startButton.getText().equals("Start Parallelising Mandelbrot")) {
            //timeElapsed = "RUNNING...";
            startButton.setText("Stop");
            startButton.setStyle("-fx-background-color: #eb4034; -fx-border-style: solid; -fx-border-radius: 3 3 3 3;");
            chunkSizeSelection = Integer.parseInt(chunkSizeComboBox.getValue().toString());
            WritableImage img = new WritableImage(1280, 720);
            PixelWriter pw  = img.getPixelWriter();

            for(int i = 0; i < 720; i++){
                for(int j = 0; j <1280; j++){
                    pw.setColor(j, i, Color.WHITE);
                }
            }

            shownImage.setImage(img);

            //this creates a new chunking instance and puts it on separate thread
            Chunking t1 = new Chunking(shownImage, schedulingComboBoxSelection, threadsComboBoxSelection, chunkSizeSelection, chunkMethodSelection, viewSelection);
            Thread th = new Thread(t1);
            th.setDaemon(true);
            startTime = System.nanoTime();
            th.start();
            try {
                //notify when thread has ended
                th.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            endTime = System.nanoTime();
            timeElapsed = (TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/1000.000);
            setElapsedTime();

            System.out.println("view selection: " + viewSelection);
            System.out.println("chunk size: " + chunkSizeSelection);
            System.out.println("scheduling policy: " + schedulingComboBoxSelection);
            System.out.println("number of threads: " + threadsComboBoxSelection);
        } else {
            Parallel.Chunking.setExit(true);
            //timeElapsed = "DNF";
        }
        startButton.setText("Start Parallelising Mandelbrot");
        startButton.setStyle("-fx-background-color: #48c400; -fx-border-style: solid; -fx-border-radius: 3 3 3 3;");
    }
    public void updateViewSelection(ActionEvent event) {
        viewSelection = viewComboBox.getValue().toString();
    }

    public void updateChunkSizeSelection(ActionEvent event) {
        chunkSizeSelection = Integer.parseInt(chunkSizeComboBox.getValue().toString());
    }

    public void updateChunkMethodSelection(ActionEvent event) {
        chunkMethodSelection = chunkMethodComboBox.getValue().toString();
        addItemsToChunkingMethodComboBox();
    }

    public void updateSchedulingComboBoxSelection(ActionEvent event)
    {
        schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
        if(schedulingComboBoxSelection.equals("Guided")) {
            chunkMethodComboBox.setVisible(false);
            chunkSizeComboBox.setVisible(false);
            chunkMethodLabel.setVisible(false);
            chunkSizeLabel.setVisible(false);
        } else {
            chunkMethodComboBox.setVisible(true);
            chunkSizeComboBox.setVisible(true);
            chunkMethodLabel.setVisible(true);
            chunkSizeLabel.setVisible(true);
        }
    }

    public void updateThreadsComboBoxSelection(ActionEvent event)
    {
        threadsComboBoxSelection = threadsComboBox.getValue().toString();
    }
    private void setElapsedTime() {
        actualTimeElapsed.setText(timeElapsed + "s");
    }

    private int findNumberOfCores() {
        int numberOfCores;
        numberOfCores = Runtime.getRuntime().availableProcessors();
        return numberOfCores;
    }

    private void addItemsToChunkingMethodComboBox() {
        if(chunkMethodSelection.equals("by Row")) {
            for(int i = 1; i <=720; i++) {
                chunkSizeComboBox.getItems().add(i);
            }
        } else {
            for(int i = 1; i <=1280; i++) {
                chunkSizeComboBox.getItems().add(i);
            }
        }
        chunkSizeComboBox.setValue(1);
    }

    @Override
    @FXML public void initialize(URL url, ResourceBundle rb) {
        //initialise labels
        threadsLabel.setText("Number of Threads");
        schedulingLabel.setText("Scheduling Policy");
        chunkSizeLabel.setText("Chunk Size:");
        chunkMethodLabel.setText("Chunk Method:");
        timeElapsedLabel.setText("Time elapsed:");
        viewLabel.setText("Change Image View");
        actualTimeElapsed.setText("");

        //initialise threadsChoiceBox options
        threadsComboBox.getItems().add("True Sequential");
        for(int i = 1; i <= findNumberOfCores(); i++) {
            threadsComboBox.getItems().add(i);
        }
        threadsComboBox.setValue(1);

        //initialise schedulingChoiceBox
        schedulingComboBox.getItems().addAll("Static-block","Static-Cyclic", "Dynamic", "Guided");
        schedulingComboBox.setValue("Static-block");

        //initialise chunkMethodComboBox
        chunkMethodComboBox.getItems().addAll("by Row", "by Column");
        chunkMethodComboBox.setValue("by Row");

        //initialise chunkSizeComboBox
        for(int i = 1; i <=720; i++) {
            chunkSizeComboBox.getItems().add(i);
        }
        chunkSizeComboBox.setValue(1);

        //initialise viewCheckBox
        viewComboBox.getItems().addAll("Mandelbrot", "Parallel Visualisation");
        viewComboBox.setValue("Mandelbrot");

        //initialise variables
        threadsComboBoxSelection = threadsComboBox.getValue().toString();
        schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
        //chunkSizeSelection = (int)chunkSizeSlider.getValue();
        viewSelection = viewComboBox.getValue().toString();
        chunkMethodSelection = chunkMethodComboBox.getValue().toString();
        chunkSizeSelection = Integer.parseInt(chunkSizeComboBox.getValue().toString());

        //initialise startButton
        startButton.setText("Start Parallelising Mandelbrot");

        //File file = new File("mandelbrot.png");
        //Image image = new Image(file.toURI().toString());
        //shownImage.setImage(image);
    }
}

package MandelbrotGUI;

import Parallel.Chunking;
import Parallel.Scheduler;
import Parallel.chunk;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MandelbrotGUIController implements Initializable {

    //Setting up various ComboBoxes with their Labels
    @FXML private ComboBox threadsComboBox;
    @FXML private ComboBox schedulingComboBox;
    private String threadsComboBoxSelection;
    private String schedulingComboBoxSelection;
    @FXML private Label threadsLabel;
    @FXML private Label schedulingLabel;

    //setting up chunk size elements
    @FXML private Slider chunkSizeSlider;
    @FXML private Label chunkLabel;
    @FXML private Label labelPlaceHolder;
    private int chunkSizeSelection;

    //Setting up view
    @FXML private Label viewLabel;
    @FXML private ComboBox viewComboBox;
    private String viewSelection;

    //Setting up time
    @FXML private Label timeElapsedLabel;
    @FXML private Label actualTimeElapsed;
    private double timeElapsed;

    //Setting up start button
    @FXML private Button startButton;

    //set up the image that will be shown
    @FXML private ImageView shownImage;
    public void setImageView(ImageView shownImage){
        this.shownImage = shownImage;
    }
    public void pressStartButton(ActionEvent event) {
        chunkSizeSelection = (int)chunkSizeSlider.getValue();

        //this creates a new chunking instance and puts it on separate thread
        Chunking t1 = new Chunking(shownImage);
        Thread th = new Thread(t1);
        th.setDaemon(true);
        th.start();

        System.out.println("view selection: " + viewSelection);
        System.out.println("chunk size: " + chunkSizeSelection);
        System.out.println("scheduling policy: " + schedulingComboBoxSelection);
        System.out.println("number of threads: " + threadsComboBoxSelection);

    }
    public void updateViewSelection(ActionEvent event) {
        //viewSelection = viewComboBox.getValue().toString();
    }

    public void updateChunkSizeSelection(ActionEvent event) {
        //chunkSizeSelection = (int)chunkSizeSlider.getValue();
    }

    public void updateSchedulingComboBoxSelection(ActionEvent event)
    {
        //schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
    }

    public void updateThreadsComboBoxSelection(ActionEvent event)
    {
        ///threadsComboBoxSelection = threadsComboBox.getValue().toString();
    }

    private int findNumberOfCores() {
        int numberOfCores;
        numberOfCores = Runtime.getRuntime().availableProcessors();
        return numberOfCores;
    }
    @Override
    @FXML public void initialize(URL url, ResourceBundle rb) {
        //initialise labels
        threadsLabel.setText("Number of Threads");
        schedulingLabel.setText("Scheduling Policy");
        chunkLabel.setText("Chunk Size: ");
        timeElapsedLabel.setText("Time elapsed:");
        viewLabel.setText("Change Image View");

        //initialise threadsChoiceBox options
        for(int i = 1; i <= findNumberOfCores(); i++) {
            threadsComboBox.getItems().add(i);
        }
        threadsComboBox.setValue(1);

        //initialise schedulingChoiceBox
        schedulingComboBox.getItems().addAll("Static", "Dynamic", "Cyclic", "Guided");
        schedulingComboBox.setValue("Static");

        //initialise chunkSizeSlider
        chunkSizeSlider.setMin(1);
        chunkSizeSlider.setMax(460800);
        chunkSizeSlider.setValue(1);
        chunkSizeSlider.setShowTickLabels(true);
        chunkSizeSlider.setShowTickMarks(true);
        chunkSizeSlider.setMajorTickUnit(76800);
        //chunkSizeSlider.setMinorTickCount(10);
        chunkSizeSlider.setBlockIncrement(10);

        //initialise viewCheckBox
        viewComboBox.getItems().addAll("Mandelbrot", "Parallel Visualisation");
        viewComboBox.setValue("Mandelbrot");

        //initialise variables
        threadsComboBoxSelection = threadsComboBox.getValue().toString();
        schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
        chunkSizeSelection = (int)chunkSizeSlider.getValue();
        viewSelection = viewComboBox.getValue().toString();

        labelPlaceHolder.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        chunkSizeSlider.valueProperty()
                )
        );

        //initialise startButton
        startButton.setText("Start Parallelising Mandelbrot");

        //File file = new File("mandelbrot.png");
        //Image image = new Image(file.toURI().toString());
        //shownImage.setImage(image);
    }
}

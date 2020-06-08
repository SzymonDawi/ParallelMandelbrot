package MandelbrotGUI;

import Parallel.Chunking;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;


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
    @FXML private ComboBox chunkMethodComboBox;
    @FXML private ComboBox chunkSizeComboBox;
    @FXML private Label chunkMethodLabel;
    @FXML private Label chunkSizeLabel;
    //@FXML private Label labelPlaceHolder;
    private String chunkMethodSelection;
    private int chunkSizeSelection;

    //Setting up view
    @FXML private Label numberOfIterationsLabel;
    @FXML private ComboBox numberOfIterationsComboBox;
    private int numberOfIterationsSelection;

    //Setting up view
    @FXML private Label viewLabel;
    @FXML private ComboBox viewComboBox;
    private String viewSelection;

    //Setting up time
    @FXML private Label timeElapsedLabel;
    @FXML private Label actualTimeElapsed;

    //Setting up start button
    @FXML private Button startButton;

    //set up the image that will be shown
    @FXML private ImageView shownImage;

    public void pressStartButton(ActionEvent event) {
        if(startButton.getText().equals("Start Parallelising Mandelbrot")) {
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
            Chunking t1 = new Chunking(numberOfIterationsSelection, actualTimeElapsed, shownImage, schedulingComboBoxSelection, threadsComboBoxSelection, chunkSizeSelection, chunkMethodSelection, viewSelection);
            Thread th = new Thread(t1);
            th.setDaemon(true);
            th.start();
            System.out.println("view selection: " + viewSelection);
            System.out.println("chunk size: " + chunkSizeSelection);
            System.out.println("scheduling policy: " + schedulingComboBoxSelection);
            System.out.println("number of threads: " + threadsComboBoxSelection);
        } else {
            Parallel.Chunking.setExit(true);
        }
        startButton.setText("Start Parallelising Mandelbrot");
        startButton.setStyle("-fx-background-color: #48c400; -fx-border-style: solid; -fx-border-radius: 3 3 3 3;");
    }
    public void updateViewSelection(ActionEvent event) {
        viewSelection = viewComboBox.getValue().toString();
    }

    public void updateChunkSizeSelection(ActionEvent event) {
        if(!(chunkSizeComboBox.getValue() == null))  {
            chunkSizeSelection = Integer.parseInt(chunkSizeComboBox.getValue().toString());
        } else {

        }
    }

    public void updateChunkMethodSelection(ActionEvent event) {
        chunkMethodSelection = chunkMethodComboBox.getValue().toString();
        addItemsToChunkSizeComboBox();
    }

    public void updateSchedulingComboBoxSelection(ActionEvent event)
    {
        schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
        if(schedulingComboBoxSelection.equals("Guided")) {
            chunkSizeComboBox.setVisible(false);
            chunkSizeLabel.setVisible(false);
        } else {
            chunkSizeComboBox.setVisible(true);
            chunkSizeLabel.setVisible(true);
        }
    }

    public void updateThreadsComboBoxSelection(ActionEvent event)
    {
        threadsComboBoxSelection = threadsComboBox.getValue().toString();
        addItemsToChunkSizeComboBox();
    }

    public void updateNumberOfIterationsComboBoxSelection(ActionEvent event)
    {
        numberOfIterationsSelection = Integer.parseInt(numberOfIterationsComboBox.getValue().toString());
    }

    private int findNumberOfCores() {
        int numberOfCores;
        numberOfCores = Runtime.getRuntime().availableProcessors();
        return numberOfCores;
    }

    private void addItemsToChunkSizeComboBox() {
        int threadsComboBoxSelectionInt = 1;
        if(!threadsComboBoxSelection.equals("True Sequential")) {
            threadsComboBoxSelectionInt = Integer.parseInt(threadsComboBoxSelection);
        }
        chunkSizeComboBox.getItems().clear();
        double maxRow = (720.00 / (double) threadsComboBoxSelectionInt);
        double maxColumn = (1280.00 / (double) threadsComboBoxSelectionInt);
        int maxRowCeiling = (int) Math.ceil(maxRow);
        int maxColumnCeiling = (int) Math.ceil(maxColumn);
        if(chunkMethodSelection.equals("by Row")) {
            for(int i = 1; i <= maxRowCeiling; i++) {
                chunkSizeComboBox.getItems().add(i);
            }
        } else {
            for(int i = 1; i <= maxColumnCeiling; i++) {
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
        numberOfIterationsLabel.setText("Number of Iterations");

        //initialise threadsComboBox options
        threadsComboBox.getItems().add("True Sequential");
        for(int i = 1; i <= findNumberOfCores(); i++) {
            threadsComboBox.getItems().add(i);
        }
        threadsComboBox.setValue(1);

        //initialise numberOfIterationsComboBox options
        numberOfIterationsComboBox.getItems().addAll(10, 50, 100, 500, 1000, 5000, 10000, 50000, 100000);
        numberOfIterationsComboBox.setValue(10000);

        //initialise schedulingComboBox
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

        //initialise viewComboBox
        viewComboBox.getItems().addAll("Mandelbrot", "Parallel Visualisation");
        viewComboBox.setValue("Mandelbrot");

        //initialise variables
        threadsComboBoxSelection = threadsComboBox.getValue().toString();
        schedulingComboBoxSelection = schedulingComboBox.getValue().toString();
        viewSelection = viewComboBox.getValue().toString();
        chunkMethodSelection = chunkMethodComboBox.getValue().toString();
        chunkSizeSelection = Integer.parseInt(chunkSizeComboBox.getValue().toString());
        numberOfIterationsSelection = Integer.parseInt(numberOfIterationsComboBox.getValue().toString());

        //initialise startButton
        startButton.setText("Start Parallelising Mandelbrot");

        //File file = new File("mandelbrot.png");
        //Image image = new Image(file.toURI().toString());
        //shownImage.setImage(image);
    }
}

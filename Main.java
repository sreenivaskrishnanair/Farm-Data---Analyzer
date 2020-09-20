//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: Main.java
// Files: None
// Course: CS-400
//
// Authors: Sreenivas Krishna Nair, Sion Boguszewicz, Shivish Makkar, Jason
// Lecturer's Name: Deb Deppeler
//
// Device: MacBook Pro
// OS: MacOs Catalina
// Version: 10.15.2 (19C57)
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import static javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn;


/**
 * Creates a program enviroment for the analysis of csv entered data for analysis of milk production
 *
 *
 */
public class Main extends Application {
    private static final String COMMA = "\\s*,\\s*";
    private String farmIdInput; // used to store farmId user entered
    private String yearInput; // used to store year user entered
    private String monthInput; // used to store month user entered
    private String startDateInput;// used to store the user input of start date range
    private String endDateInput;// used to store the user input of end date range

    // field variables used to store info for new data entry
    private String newFarmID;
    private String newDate;
    private String newWeight;

    Scene scene;// stores the primary active scene of the gui

    private TableView<FarmData> table;// used to store data in an organized fashion for display
    private Set<FarmData> data = new HashSet<FarmData>();// hashset for storing our data on backend
    private List<FarmData> formatedData = new ArrayList<>();// stores list of dates in formatted
    // function
    private Label minMaxOrAve = new Label();// label for determing min max avg
    private boolean validFile = true;// true if inputted file is valid format false if not


    private static final int WINDOW_WIDTH = 1000;// Width of stage
    private static final int WINDOW_HEIGHT = 700;// height of stage
    private int leftCompWidth = 222;// stores values for use in gui spacing
    private int prefButtonWidth = 200;// stores values for use in gui spacing
    private static final String APP_TITLE = "Milk Weight Analyzer";// name of program
    Button printButton = new Button();// print button for printing out to file

    private VBox reportHolder;// stores vertical box containing the reports which are generated

    /**
     * Launch method to generate the base state of the program
     *
     *
     *
     * @param primaryStage - primary stage which gui draws on
     *
     * @throws Exception - on bad file input
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // load button for top component
        VBox loadContaner = new VBox();
        Label dragFile = makeDragLabel();

        // defines and formats top components of gui
        Separator separator1 = new Separator();
        separator1.setMaxWidth(400);
        minMaxOrAve.setId("minMaxOrAve");
        minMaxOrAve.setPadding(new Insets(5, 0, 0, 30));

        // adds components to load container for displaying
        loadContaner.getChildren().addAll(dragFile, minMaxOrAve, separator1);

        // make button for printing
        printButton = new Button("Download all Data");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        printButton.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                // No Directory selected
            } else {
                try { // handles potential IOException on bad file input
                    printAllData(new File(selectedDirectory.getAbsolutePath() + "\\MilkData.csv"));
                    Alert invalidFile = new Alert(Alert.AlertType.CONFIRMATION,
                            "Your data was successfully downloaded in the selected folder");
                    invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
                } catch (IOException e) {
                    Alert invalidFile =
                            new Alert(Alert.AlertType.WARNING, "Unknown error the file did not download");
                    invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
                }

            }
        });

        // make left component
        TabPane leftComponent = makeLeftComponent();
        leftComponent.setPrefWidth(leftCompWidth);

        leftComponent.setOnMouseMoved(event -> { // code for dragging left component to the right or
            // left
            if (event.getX() < leftCompWidth - 15 || event.getX() > leftCompWidth + 15)
                scene.setCursor(Cursor.DEFAULT);
            else
                scene.setCursor(Cursor.H_RESIZE);
        });
        leftComponent.setOnMouseDragged(event -> { // code for dragging left component to the right or
            // left
            if (scene.getCursor().equals(Cursor.H_RESIZE) && event.getX() > 10
                    && event.getX() < WINDOW_WIDTH - 10)
                leftCompWidth = (int) event.getX();
            leftComponent.setPrefWidth(leftCompWidth);


        });

        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // add top, left, center, right, and bottom components
        root.setTop(makeTopComponents(loadContaner));

        root.setLeft(leftComponent);
        makeTable();
        table.setOnMouseMoved(event -> {
            if (event.getX() > 15)
                scene.setCursor(Cursor.DEFAULT);
        }); // for expanding tabpane
        root.setCenter(table);

        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());// sets fx
        // styling
        // based off
        // css file
        // inputs


        // Add the nodes and set the primary stage to show
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
     * private helper for creating top components of gui
     *
     * @Params loadContainer - vbox for storing top nodes
     *
     * @return HBox returns hbox of correctly formatted nodes
     *
     */
    private HBox makeTopComponents(VBox loadContainer) {
        // stores and organizes nodes
        HBox top = new HBox();
        VBox clearStack = new VBox();

        Button originalData = new Button("Display Original Data");

        Button clearData = new Button("Clear Loaded Data");

        Region spacer = new Region();
        spacer.setPrefWidth(370);
        HBox.setHgrow(spacer, Priority.ALWAYS);// dynamically updates spacer size to match expanded
        // window

        // css controlling for quick updates
        originalData.setStyle("-fx-focus-color: lightblue;");
        clearData.setStyle("-fx-focus-color: lightblue;");

        clearData.setMaxWidth(Double.MAX_VALUE);
        originalData.setMaxWidth(Double.MAX_VALUE);

        // creates alert to warm of large file deletion, and handles if it occurs
        Alert clearAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete all data that you have loaded?");

        clearData.setOnAction(event -> {

            Optional<ButtonType> action = clearAlert.showAndWait();
            if (action.get() == ButtonType.OK) {
                data.clear();
                table.setItems(FXCollections.observableArrayList(data));
            }
        });

        // handler for original data button even, hides and shows specefic columns of our table
        originalData.setOnAction(event -> {
            table.getColumns().get(0).setVisible(true);
            table.getColumns().get(1).setVisible(false);
            table.getColumns().get(2).setVisible(true);
            table.getColumns().get(3).setVisible(true);
            table.setItems(FXCollections.observableArrayList(data));
        });

        clearStack.getChildren().addAll(originalData, clearData);
        clearStack.setSpacing(5);
        clearStack.setPadding(new Insets(5, 0, 0, 10));

        top.getChildren().addAll(loadContainer, spacer, clearStack);

        return top;

    }

    /**
     * method used to create the label that is used to drag and drop a file
     *
     * @return Label
     */
    private Label makeDragLabel() {
        // defines drag box for file loading
        Label dragFile = new Label("Drag and Drop File(s) Here");
        Tooltip dragTip = new Tooltip("addition files will add to the data set");
        Tooltip.install(dragFile, dragTip);
        dragFile.setId("drop");
        dragFile.setPadding(new Insets(10, 10, 0, 10));

        // handlers for events involving drag file both when it is over the load box and when it is
        // loaded
        dragFile.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles())
                event.acceptTransferModes(TransferMode.COPY);
        });
        dragFile.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            String filePath;

            // add data in provided files to data set
            for (int i = 0; i < files.size(); i++) {
                filePath = files.get(i).getAbsolutePath();

                if (validFile) {
                    data.addAll(loadFile(filePath));
                }
            }
            validFile = true;

            table.setItems(FXCollections.observableArrayList(data));
            table.getColumns().get(0).setVisible(true);
            table.getColumns().get(1).setVisible(false);
            table.getColumns().get(2).setVisible(true);
            table.getColumns().get(3).setVisible(true);
            table.getColumns().get(4).setVisible(false);
        });

        return dragFile;
    }

    /**
     * used to load a file dragged and dropped the the appropriate location
     *
     * @param inputFilePath defines file path to desired file
     * @return List<FarmData> loaded from file stores value
     */
    private List<FarmData> loadFile(String inputFilePath) {

        List<FarmData> inputList = new ArrayList<>();
        try {
            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            Alert invalidFile = new Alert(Alert.AlertType.WARNING,
                    "One or more of the provided files had invalid csv format");
            invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
            validFile = false;
        }


        return inputList;
    }

    /**
     * defines function for mapping and decomposing csv
     *
     */
    private Function<String, FarmData> mapToItem = (line) -> {

        String[] curFarm = line.split(COMMA);// a CSV has comma separated lines


        String[] dateParts = curFarm[0].split("-"); // use to get month which will be at index 1

        return new FarmData(curFarm[0], getMonth(Integer.parseInt(dateParts[1])), curFarm[1],
                Integer.parseInt(curFarm[2]));


    };

    /**
     * private method used to return month given an int
     *
     * @param month represented as numerical int value 1-12
     * @return string representation of month number input
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    /**
     * helper method to generate table from our data set
     */
    private void makeTable() {
        table = new TableView<FarmData>();

        TableColumn<FarmData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("date"));

        TableColumn<FarmData, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("month"));

        TableColumn<FarmData, String> farmIDCol = new TableColumn<>("FarmID");
        farmIDCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("farmID"));

        TableColumn<FarmData, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<FarmData, Integer>("weight"));


        TableColumn<FarmData, String> percentCol = new TableColumn<>("Percent");
        percentCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("percent"));


        table.getColumns().setAll(dateCol, monthCol, farmIDCol, weightCol, percentCol);
        table.getColumns().get(1).setVisible(false);
        table.getColumns().get(4).setVisible(false);

        // make tooltip for sorting table
        Tooltip t = new Tooltip("click column title to sort");
        Tooltip.install(table, t);

        // set table properties
        table.setPrefWidth(400);
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        table.setPadding(new Insets(20, 20, 20, 20));

    }

    /*
     * private helper for determing the max first ordering
     */
    private void determineMax() {
        try {
            FarmData max = formatedData.stream().max(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText(max.toString());
        } catch (NoSuchElementException ignored) {

        }

    }

    /*
     * private helper for detemining min first ordering
     */
    private void determineMin() {
        try {
            FarmData min = formatedData.stream().min(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText(min.toString());
        } catch (NoSuchElementException ignored) {

        }
    }

    /**
     * private helper for determing average ordering
     */
    private void determineAverage() {

        if (data.size() == 0)
            return;

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        minMaxOrAve.setText("Average weight : " + sum / data.size());
    }

    /*
     * called when percent option is ticked for display
     *
     * @param list of farm data
     */
    private void setPercent(List<FarmData> farmData) {

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        for (FarmData farm : farmData) {
            farm.setPercent(df.format((farm.getWeight() / sum) * 100) + "%"); // format decimal for %
        }

    }

    /*
     * private method for creating list for farm report with month organization
     *
     * @return List of Farm data
     */
    private List<FarmData> makeListForFarmReport() {
        List<FarmData> weightByMonths = new ArrayList<>();
        weightByMonths.add(new FarmData("Jan"));
        weightByMonths.add(new FarmData("Feb"));
        weightByMonths.add(new FarmData("March"));
        weightByMonths.add(new FarmData("April"));
        weightByMonths.add(new FarmData("May"));
        weightByMonths.add(new FarmData("June"));
        weightByMonths.add(new FarmData("July"));// list of all months
        weightByMonths.add(new FarmData("Aug"));
        weightByMonths.add(new FarmData("Sep"));
        weightByMonths.add(new FarmData("Oct"));
        weightByMonths.add(new FarmData("Nov"));
        weightByMonths.add(new FarmData("Dec"));

        return weightByMonths;
    }


    /**
     * handles controlling and displaying of farm report to screen when option is chosen
     *
     * @throws IOException
     */
    private void farmReport() throws IOException {
        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(true); // make month visible
        table.getColumns().get(2).setVisible(false); // make farmID not visible
        table.getColumns().get(3).setVisible(true); // make weight Visible

        // data stream for processing
        formatedData = data.stream()
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
                        && farmData.getFarmID().equalsIgnoreCase(farmIdInput))
                .collect(Collectors.toList());


        List<FarmData> weightByMonths = makeListForFarmReport();

        String[] dateParts;

        for (FarmData farm : formatedData) {

            dateParts = farm.getDate().split("-");
            switch (Integer.parseInt(dateParts[1])) {
                case 1:
                    weightByMonths.get(0).addWeight(farm.getWeight());

                    break;
                case 2:
                    weightByMonths.get(1).addWeight(farm.getWeight());
                    break;
                case 3:
                    weightByMonths.get(2).addWeight(farm.getWeight());
                    break;
                case 4:
                    weightByMonths.get(3).addWeight(farm.getWeight());
                    break;
                case 5:
                    weightByMonths.get(4).addWeight(farm.getWeight());
                    break;
                case 6:
                    weightByMonths.get(5).addWeight(farm.getWeight());
                    break;
                case 7:
                    weightByMonths.get(6).addWeight(farm.getWeight());
                    break;
                case 8:
                    weightByMonths.get(7).addWeight(farm.getWeight());
                    break;
                case 9:
                    weightByMonths.get(8).addWeight(farm.getWeight());
                    break;
                case 10:
                    weightByMonths.get(9).addWeight(farm.getWeight());
                    break;
                case 11:
                    weightByMonths.get(10).addWeight(farm.getWeight());
                    break;
                case 12:
                    weightByMonths.get(11).addWeight(farm.getWeight());
                    break;

            }
        }

        setPercent(weightByMonths);


        table.setItems(FXCollections.observableArrayList(weightByMonths));

    }

    /**
     * method that handles the annual report
     *
     * @throws IOException
     */
    private void annualReport() throws IOException {

        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(false); // make month visible
        table.getColumns().get(2).setVisible(true); // make farmID not visible
        table.getColumns().get(3).setVisible(true); // make weight Visible
        // data.add(new FarmData("Sion's farm", 4558858));

        List<FarmData> annualFarmWeight = new ArrayList<>();


        formatedData = data.stream()// sream to filter data for given year
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput))
                .collect(Collectors.toList());
        // Set to load into unique farm IDs into
        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }
        // load the required ID, weight pair that meets the condition
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }
        // parse the list and add weights
        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }
        // load into the table display element
        for (String e : IDs) {
            annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
        }


        setPercent(annualFarmWeight);// method to display percentages
        // load into taboe display
        table.setItems(FXCollections.observableArrayList(annualFarmWeight));
    }

    /**
     * used to generate monthly report
     *
     * @throws IOException
     */
    private void monthlyReport() throws IOException {


        table.getColumns().get(0).setVisible(false); // make date
        table.getColumns().get(1).setVisible(false); // make month
        table.getColumns().get(2).setVisible(true); // make farmID
        table.getColumns().get(3).setVisible(true); // make weight

        List<FarmData> annualFarmWeight = new ArrayList<>();


        formatedData = data.stream()
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
                        && farmData.getDate().substring(5, 6).equalsIgnoreCase(monthInput))
                .collect(Collectors.toList());
        // Set to load into unique farm IDs into
        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }
        // load the required ID, weight pair that meets the condition
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }
        // populate with correct weights
        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }
        // load into display element
        for (String farm : IDs) {
            annualFarmWeight.add(new FarmData(farm, hashtable.get(farm)));
        }

        // load percentage
        setPercent(annualFarmWeight);
        // load into table
        table.setItems(FXCollections.observableArrayList(annualFarmWeight));


    }

    /**
     * method to handlr the date range report
     *
     * @throws IOException
     */
    private void dateRangeReport() throws IOException {
        // paramters for the date range
        String[] startDate = startDateInput.split("-");
        String[] endDate = endDateInput.split("-");

        Alert invalidDate =
                new Alert(Alert.AlertType.WARNING, "There entered date was not in the correct format");

        // three distinct numbers were not entered
        if (startDate.length != 3 || endDate.length != 3) {
            invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
            clearInputFields();
            return;
        }

        // check that a zero was not entered before the day of the month
        if (startDate[1].charAt(0) == '0' || endDate[1].charAt(0) == '0'
                || startDate[2].charAt(0) == '0' || endDate[2].charAt(0) == '0') {
            invalidDate.setContentText("Please do not enter a 0 in front of the day or the month");
            invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
            clearInputFields();
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (!isNumeric(startDate[i]) && !isNumeric(endDate[i])) {
                invalidDate.setContentText("part of your date was not a number");
                invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
                clearInputFields();
                return;
            }
        }


        // make date variables from string dates
        Date d1 = stringToDate(startDateInput);
        Date d2 = stringToDate(endDateInput);

        // set table visibility
        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(false); // make month visible
        table.getColumns().get(2).setVisible(true); // make farmID not visible
        table.getColumns().get(3).setVisible(true); // make weight Visible
        // data.add(new FarmData("Sion's farm", 4558858));

        // list for table display
        List<FarmData> annualFarmWeight = new ArrayList<>();
        // filter according to the date range that was required
        formatedData =
                data.stream().filter(farmData -> dateInRange(d1, d2, stringToDate(farmData.getDate())))
                        .collect(Collectors.toList());
        // filter for uique ids
        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }
        // load id, weight paors into the hash table
        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }
        // load into the hash table weights
        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }

        // load into the display
        for (String e : IDs) {
            annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
        }

        // ask for percentage
        setPercent(annualFarmWeight);
        // annualFarmWeight.load into table
        table.setItems(FXCollections.observableArrayList(annualFarmWeight));

    }

    /**
     * check if the string is number
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * function to check if date is in the required range
     *
     * @param d1 start date
     * @param d2 end date
     * @param d3 date in range
     * @return true if in the range
     */
    private boolean dateInRange(Date d1, Date d2, Date d3) {
        return d3.after(d1) && d3.before(d2);
    }

    /**
     * method to convert string to date
     *
     * @param s
     * @return
     */
    private Date stringToDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        try {
            d = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * print all the data
     *
     * @param destFile file destination
     * @throws IOException
     */
    private void printAllData(File destFile) throws IOException {
        FileWriter writer = new FileWriter(destFile);
        writer.write("date,farm_id,weight\n");

        // write each data element in formatted
        for (FarmData farm : data) {
            writer.write(farm.printToCsvFile() + "\n");
        }
        writer.close();
    }

    /**
     * private helper method to clear all red required labels
     */
    private void hideAllRequired(TextField farmIdInField, Label farmId, TextField monthInField,
                                 Label month, TextField yearInField, Label year, TextField startDateInField, Label start,
                                 TextField endDateInField, Label end) {
        // required field hide
        hideRequired(farmIdInField, farmId, "FarmID");
        hideRequired(monthInField, month, "Month");
        hideRequired(yearInField, year, "Year");
        hideRequired(startDateInField, start, "Start");
        hideRequired(endDateInField, end, "End");
    }

    /**
     * method to set all input to null
     */
    private void clearInputFields() {
        farmIdInput = null;
        yearInput = null;
        monthInput = null;
        startDateInput = null;
        endDateInput = null;
    }

    /**
     * create the gui for the left component
     *
     * @return
     */
    private TabPane makeLeftComponent() {
        TabPane modesHolder = new TabPane(); // holds view and Edit button
        Tab view = new Tab("view");
        Tab edit = new Tab("edit");
        // set view
        view.setContent(makeViewTab());
        edit.setContent(makeEditTab());
        modesHolder.getTabs().addAll(view, edit);
        modesHolder.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return modesHolder;
    }

    /**
     * Vbox to return make view tab
     *
     * @return VBox
     */
    private VBox makeViewTab() {

        VBox viewComponent = new VBox(); // holds view and Edit button
        viewComponent.setStyle("-fx-background-color: #FFFFFF;");
        // viewComponent.setPrefWidth(200);
        viewComponent.setMaxHeight(600);
        viewComponent.setPadding(new Insets(0, 10, 10, 10));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Insets requiredPadd = new Insets(10, 0, 0, 5);
        Label farmId = new Label("Farm ID");
        TextField farmIdInField = new TextField();
        farmIdInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        farmIdInField.textProperty().addListener(event -> {
            farmIdInput = farmIdInField.getText();
        });


        Label start = new Label("Start Date");
        TextField endDateInField = new TextField();
        Label end = new Label("End Date");
        TextField startDateInField = new TextField();


        Label year = new Label("Year");
        year.setPadding(inputPadd);

        TextField yearInField = new TextField();
        yearInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        yearInField.textProperty().addListener(event -> {
            yearInput = yearInField.getText();
        });


        Label month = new Label("Month");
        month.setPadding(inputPadd);
        TextField monthInField = new TextField();


        // set listener for reading
        monthInField.textProperty().addListener(event -> {
            monthInput = monthInField.getText();
        });


        VBox searchDataHolder = new VBox();
        searchDataHolder.getChildren().addAll(farmId, farmIdInField, year, yearInField, month,
                monthInField);
        searchDataHolder.setAlignment(Pos.CENTER_LEFT);
        searchDataHolder.setPadding(new Insets(0, 60, 0, 20));


        // make radio buttons for diff views
        VBox hboxHolder = new VBox();
        HBox percentHolder = new HBox();
        HBox minHolder = new HBox();
        HBox maxHolder = new HBox();
        HBox averageHolder = new HBox();

        Label percentL = new Label("display %");
        Label minL = new Label("Min");
        Label maxL = new Label("Max");
        Label averageL = new Label("Average");

        ToggleButton percentTog = new ToggleButton();
        percentTog.setOnAction(event -> {
            if (percentTog.isSelected())
                table.getColumns().get(4).setVisible(true);
            else
                table.getColumns().get(4).setVisible(false);
        });


        ToggleButton minTog = new ToggleButton();
        minTog.setOnAction(event -> {// set behaviour of min button
            if (minTog.isSelected())
                determineMin();
            else
                minMaxOrAve.setText("");
        });

        ToggleButton maxTog = new ToggleButton();
        maxTog.setOnAction(event -> {// set behaviour od max button
            if (maxTog.isSelected())
                determineMax();
            else
                minMaxOrAve.setText("");
        });

        ToggleButton averageTog = new ToggleButton();
        averageTog.setOnAction(event -> {// set behaviour of average button
            if (averageTog.isSelected())
                determineAverage();
            else
                minMaxOrAve.setText("");
        });

        // creating region for spacing of label and button
        Region spacing1 = new Region();
        Region spacing2 = new Region();
        Region spacing3 = new Region();
        Region spacing4 = new Region();
        HBox.setHgrow(spacing1, Priority.ALWAYS);
        HBox.setHgrow(spacing2, Priority.ALWAYS);
        HBox.setHgrow(spacing3, Priority.ALWAYS);
        HBox.setHgrow(spacing4, Priority.ALWAYS);

        percentHolder.getChildren().addAll(percentL, spacing1, percentTog);
        percentHolder.setPadding(new Insets(10, 10, 15, 10));
        minHolder.getChildren().addAll(minL, spacing2, minTog);
        minHolder.setPadding(new Insets(0, 10, 0, 10));
        maxHolder.getChildren().addAll(maxL, spacing3, maxTog);
        maxHolder.setPadding(new Insets(0, 10, 0, 10));
        averageHolder.getChildren().addAll(averageL, spacing4, averageTog);
        averageHolder.setPadding(new Insets(0, 10, 0, 10));

        hboxHolder.getChildren().addAll(percentHolder, minHolder, maxHolder, averageHolder);
        hboxHolder.setPadding(new Insets(20));
        hboxHolder.setSpacing(5);


        // add min max and average to group
        ToggleGroup group = new ToggleGroup();
        minTog.setToggleGroup(group);
        maxTog.setToggleGroup(group);
        averageTog.setToggleGroup(group);


        // report type buttons
        reportHolder = new VBox(5);
        reportHolder.setPadding(new Insets(50, 0, 10, 0));

        Button farmReport = new Button("Farm Report");

        farmReport.setMaxWidth(Double.MAX_VALUE);
        farmReport.setOnAction(event -> {
            try {
                if (farmIdInput != null && yearInput != null) {
                    {
                        farmReport();
                        hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year,
                                startDateInField, start, endDateInField, end);
                        monthInField.clear();
                        startDateInField.clear();
                        endDateInField.clear();
                        monthInput = null;
                        startDateInput = null;
                        endDateInput = null;
                    }
                } else {
                    showRequired(farmIdInField, farmId, "FarmID *Required");
                    showRequired(yearInField, year, "Year *Required");

                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                    hideRequired(monthInField, month, "Month");
                    if (farmIdInput != null)
                        hideRequired(farmIdInField, farmId, "FarmID");
                    if (yearInput != null)
                        hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // Annual report button behavior
        Button annualReport = new Button("Annual Report");
        annualReport.setMaxWidth(Double.MAX_VALUE);
        annualReport.setOnAction(event -> {
            try {
                if (yearInput != null) {// check if year null
                    annualReport();
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year,
                            startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    startDateInField.clear();
                    endDateInField.clear();
                    monthInField.clear();
                    farmIdInput = null;
                    monthInput = null;
                    startDateInput = null;
                    endDateInput = null;
                } else {
                    showRequired(yearInField, year, "Year *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(monthInField, month, "Month");
                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });

        Button monthReport = new Button("Monthly Report");
        monthReport.setMaxWidth(Double.MAX_VALUE);
        monthReport.setOnAction(event -> {
            try {
                if (monthInput != null && yearInput != null) {
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year,
                            startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    startDateInField.clear();
                    endDateInField.clear();
                    monthlyReport();

                    farmIdInput = null;
                    startDateInput = null;
                    endDateInput = null;
                } else {
                    showRequired(monthInField, month, "Month *Required");
                    showRequired(yearInField, year, "Year *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                    if (monthInput != null)
                        hideRequired(monthInField, month, "Month");
                    if (yearInput != null)
                        hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });


        reportHolder.getChildren().addAll(farmReport, annualReport, monthReport);
        // HBox.setHgrow(farmReport, Priority.ALWAYS);


        // make start date and end date textfields
        VBox startHolder = new VBox(5);
        VBox endHolder = new VBox(5);
        HBox datesHold = new HBox(5);
        VBox d = new VBox(5);

        Label exampleDate = new Label("yyyy-mm-dd");

        startDateInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        startDateInField.textProperty().addListener(event -> {
            startDateInput = startDateInField.getText();
        });


        endDateInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading end date
        endDateInField.textProperty().addListener(event -> endDateInput = endDateInField.getText());


        Label dash = new Label("-");
        Label filler = new Label("-");

        Label exampleDate2 = new Label("yyyy-mm-dd");

        // set up region where user enters dates
        filler.setVisible(false);
        dash.setPadding(new Insets(0, 5, 0, 5));
        d.getChildren().addAll(filler, dash);
        endDateInField.setMaxWidth(Double.MAX_VALUE);
        startHolder.getChildren().addAll(start, exampleDate, startDateInField);
        startHolder.setPadding(new Insets(0, 0, 0, 10));
        endHolder.getChildren().addAll(end, exampleDate2, endDateInField);
        endHolder.setPadding(new Insets(0, 10, 0, 0));
        datesHold.getChildren().addAll(startHolder, d, endHolder);
        datesHold.setAlignment(Pos.BASELINE_CENTER);
        datesHold.setMaxWidth(Double.MAX_VALUE);


        // report data range button
        VBox rangeHolder = new VBox();
        Button rangeReport = new Button("Date Range Report");
        rangeReport.setMaxWidth(Double.MAX_VALUE);
        rangeHolder.getChildren().add(rangeReport);
        rangeHolder.setPadding(new Insets(5, 0, 0, 0));
        rangeReport.setOnAction(event -> {
            try {
                if (startDateInput != null) {
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year,
                            startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    monthInField.clear();
                    yearInField.clear();
                    dateRangeReport();
                } else {
                    showRequired(startDateInField, start, "Start *Required");
                    showRequired(endDateInField, end, "End *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(monthInField, month, "Month");
                    hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });

        viewComponent.getChildren().addAll(searchDataHolder, hboxHolder, reportHolder, datesHold,
                rangeHolder);
        return viewComponent;
    }

    /**
     * make the edit tab
     *
     * @return VBox
     */
    private VBox makeEditTab() {
        VBox editComponent = new VBox(); // holds view and Edit button
        editComponent.setStyle("-fx-background-color: #FFFFFF;");
        editComponent.setPrefWidth(200);
        editComponent.setMaxHeight(600);
        editComponent.setPadding(new Insets(0, 10, 10, 30));
        Label instr = new Label("yyyy-mm-dd");
        instr.setPadding(new Insets(50, 0, 0, 0));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdIn = new TextField();
        farmId.setPadding(new Insets(50, 0, 0, 0));
        farmIdIn.setMaxWidth(Double.MAX_VALUE);
        farmIdIn.textProperty().addListener(event -> {
            newFarmID = farmIdIn.getText();
        });

        // label for date
        Label date = new Label("Date");
        date.setPadding(inputPadd);
        TextField dateIn = new TextField();
        dateIn.setMaxWidth(Double.MAX_VALUE);
        dateIn.textProperty().addListener(event -> {
            newDate = dateIn.getText();
        });

        // label for weight
        Label weight = new Label("Weight");
        weight.setPadding(inputPadd);
        TextField weightIn = new TextField();
        weightIn.setMaxWidth(Double.MAX_VALUE);
        weightIn.textProperty().addListener(event -> {
            newWeight = weightIn.getText();
        });

        // report data range button
        VBox addHolder = new VBox();
        Button addData = new Button("Add Data");
        Button removeData = new Button("Remove Data");

        // set action for adding data including required labels
        Alert badData =
                new Alert(Alert.AlertType.WARNING, "There entered date was not in the correct format");

        addData.setOnAction(event -> {
            if (newFarmID != null && newDate != null && newWeight != null) {
                if (!processNewData(farmIdIn, farmId, dateIn, date, weightIn, weight, badData))
                    return; // return if data was not processed correctly

                data.add(new FarmData(newDate, newFarmID, Integer.parseInt(newWeight)));
                table.setItems(FXCollections.observableArrayList(data));

                // set up for another entry
                farmIdIn.clear();
                dateIn.clear();
                weightIn.clear();
                newFarmID = null;
                newDate = null;
                newWeight = null;
            } else {
                showRequired(farmIdIn, farmId, "FarmID *Required");
                showRequired(dateIn, date, "Date *Required");
                showRequired(weightIn, weight, "Weight *Required");

                // some values may have already been entered
                if (newFarmID != null)
                    hideRequired(farmIdIn, farmId, "FarmID");
                if (newDate != null)
                    hideRequired(dateIn, date, "Date");
                if (newWeight != null)
                    hideRequired(weightIn, weight, "Weight");

            }
        });

        removeData.setOnAction(event -> {
            if (newFarmID != null && newDate != null && newWeight != null) {
                if (!processNewData(farmIdIn, farmId, dateIn, date, weightIn, weight, badData))
                    return; // return if data was not processed correctly

                FarmData farmToRemove = new FarmData(newDate, newFarmID, Integer.parseInt(newWeight));
                data.removeIf(farmData -> farmData.equals(farmToRemove));
                table.setItems(FXCollections.observableArrayList(data));

                // set up for another entry
                farmIdIn.clear();
                dateIn.clear();
                weightIn.clear();
                newFarmID = null;
                newDate = null;
                newWeight = null;
            } else {
                showRequired(farmIdIn, farmId, "FarmID *Required");
                showRequired(dateIn, date, "Date *Required");
                showRequired(weightIn, weight, "Weight *Required");

                // some values may have already been entered
                if (newFarmID != null)
                    hideRequired(farmIdIn, farmId, "FarmID");
                if (newDate != null)
                    hideRequired(dateIn, date, "Date");
                if (newWeight != null)
                    hideRequired(weightIn, weight, "Weight");

            }
        });

        addHolder.getChildren().addAll(addData, removeData, printButton);
        addHolder.setSpacing(10);
        addHolder.setPadding(new Insets(30, 0, 0, 0));

        editComponent.getChildren().addAll(instr, farmId, farmIdIn, date, dateIn, weight, weightIn,
                addHolder);
        editComponent.setPadding(new Insets(0, 60, 0, 30));

        return editComponent;
    }

    /**
     * return true if the new data entered was in the expected format
     *
     * @param farmIdIn input
     * @param farmId   input
     * @param dateIn   input
     * @param date     input
     * @param weightIn input
     * @param weight   input
     * @param badData  input
     * @return true if success
     */
    private boolean processNewData(TextField farmIdIn, Label farmId, TextField dateIn, Label date,
                                   TextField weightIn, Label weight, Alert badData) {
        if (newFarmID != null && newDate != null && newWeight != null) {
            hideRequired(farmIdIn, farmId, "FarmID");
            hideRequired(dateIn, date, "Date");
            hideRequired(weightIn, weight, "Weight");

            // check that data entered is parable
            String[] dateParts = newDate.split("-");


            // three distinct numbers were not entered
            if (dateParts.length != 3) {
                badData.showAndWait();
                return false;
            }

            // check that a zero was not entered before the day of the month
            if (dateParts[1].charAt(0) == '0' || dateParts[2].charAt(0) == '0') {
                badData.setContentText("Please do not enter a 0 in front of the day or the month");
                badData.showAndWait();
                return false;
            }

            for (int i = 0; i < 3; i++) {
                if (!isNumeric(dateParts[i])) {
                    badData.setContentText("part of your date was not a number");
                    badData.showAndWait();
                    return false;
                }
            }

            if (!isNumeric(newWeight)) {
                badData.setContentText("please enter a number");
                badData.showAndWait();
                return false;
            }
        }
        return true;
    }

    /**
     * show required fields
     *
     * @param t
     * @param l1
     * @param label
     */
    private void showRequired(TextField t, Label l1, String label) {
        l1.setText(label);
        t.setStyle("-fx-text-inner-color: red");
        t.setStyle("-fx-text-box-border: red ;");
        t.setStyle("-fx-focus-color: red ;");
        l1.setTextFill(Color.web("#FF0000")); // make boarder read
    }

    /**
     * hide required fields
     *
     * @param t
     * @param l1
     * @param required
     */
    private void hideRequired(TextField t, Label l1, String required) {
        l1.setText(required);
        t.setStyle("-fx-text-inner-color: black ;");
        t.setStyle("-fx-text-box-border: black ;");
        t.setStyle("-fx-focus-color: black ;");
        l1.setTextFill(Color.web("#000000")); // make boarder black
    }



    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


}

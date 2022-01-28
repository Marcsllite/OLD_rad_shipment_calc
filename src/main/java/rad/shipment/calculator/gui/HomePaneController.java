package rad.shipment.calculator.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import rad.shipment.calculator.helpers.Isotope;
import rad.shipment.calculator.helpers.PTableColumn;
import rad.shipment.calculator.helpers.Shipment;
import rad.shipment.calculator.helpers.TableEditor;
import rad.shipment.calculator.view.FXMLView;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePaneController {
    // Declaring FXML objects
    @FXML private GridPane homePane;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnRemove;
    @FXML private TableView<Isotope> tableView;
    @FXML private PTableColumn<Isotope, Label> tableColIsotope;
    @FXML private PTableColumn<Isotope, Label> tableColHalfLife;
    @FXML private PTableColumn<Isotope, Label> tableColActivity;
    @FXML private PTableColumn<Isotope, Label> tableColRefDate;
    @FXML private PTableColumn<Isotope, Label> tableColMass;
    @FXML private Button btnCalculate;

    // Declaring variables
    private static final Logger logr = Logger.getLogger(HomePaneController.class.getName());  // getting logger
    // Integer property to keep track of the number of selected rows in the table at any given time
    private static final IntegerProperty selectedTableRows = new SimpleIntegerProperty();
    private static int repeatIndex = Integer.parseInt(Main.getString("defaultInt"));  // variable to keep track of where the duplicate device is located
    private static Shipment shipment = new Shipment();
    private final TableEditor tableEditor = new TableEditor(this);  // creating object to make any changes to the table
    private MainController mainController;

    /*///////////////////////////////////////////////// START/SETUP //////////////////////////////////////////////////*/

    /**
     * FXML Function to initialize GUI (run when the edit.fxml file is loaded by the FXMLLoader)
     */
    @FXML private void initialize(){
        initTable();

        // Setting onMouseEntered property
        btnAdd.setOnMouseEntered(event -> mouseAction());
        btnEdit.setOnMouseEntered(event -> mouseAction());
        btnRemove.setOnMouseEntered(event -> mouseAction());
        btnCalculate.setOnMouseEntered(event -> mouseAction());

        // Setting onMouseExited property
        btnAdd.setOnMouseExited(event -> mouseAction());
        btnEdit.setOnMouseExited(event -> mouseAction());
        btnRemove.setOnMouseExited(event -> mouseAction());
        btnCalculate.setOnMouseExited(event -> mouseAction());

        // Allowing the user to press enter to select a button if focused on it
        MainController.setBtnFireOnEnter(btnAdd);
        MainController.setBtnFireOnEnter(btnEdit);
        MainController.setBtnFireOnEnter(btnRemove);
        MainController.setBtnFireOnEnter(btnCalculate);

        //disabling edit button if more than 1 or no rows are selected
        btnEdit.disableProperty().bind(
                Bindings.or(Bindings.isEmpty(getSelectedRows()), selectedTableRows.greaterThan(1)));

        // disabling remove button if no row is selected
        btnRemove.disableProperty().bind(Bindings.isEmpty(getSelectedRows()));

        // disabling the calculate button if there are no rows in the table
        btnCalculate.disableProperty().bind(Bindings.size(TableEditor.getIsotopes()).isEqualTo(0));
    }

    /**
     * Function to connect the Menu Pane Controller with the current MainController
     * (Allows the Menu Pane Controller to have access to the other controllers in the MainController through this link)
     *
     * @param mainController the instance of the current MainController
     */
    void injectMainController(MainController mainController){ setMainController(mainController); }

    /**
     * Helper function to initialize table columns to display their respective info correctly
     */
    protected void initTable(){
        // Setting the tableView cell value properties to the variables from the Isotope Class
        // NOTE: the names are the same as the variables names in the DeviceInfo class
        tableColIsotope.setCellValueFactory(new PropertyValueFactory<>("_Name"));
        tableColHalfLife.setCellValueFactory(new PropertyValueFactory<>("_HalfLife"));
        tableColActivity.setCellValueFactory(new PropertyValueFactory<>("_AToday"));
        tableColRefDate.setCellValueFactory(new PropertyValueFactory<>("_RefDate"));
        tableColMass.setCellValueFactory(new PropertyValueFactory<>("_Mass"));

        // allowing the user to select more than one row in the table
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // keeping track of the number of rows the user has selected using the tableView's getSelectedRows function
        getSelectedRows().addListener((ListChangeListener<Isotope>) row -> {
            while(row.next()){
                if(row.wasAdded() || row.wasRemoved()) selectedTableRows.set(getSelectedRows().size());
            }
        });

        // allowing the user to press the delete or backspace key to remove selected isotope rows from the table
        tableView.setOnKeyPressed( event -> {
            if ((event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE)) removeBtnHandler();
        });

        // un-selecting selected rows if user clicks the add button or the calculate button
        tableView.focusedProperty().addListener((observable, oldV, newV) -> {
            if(Boolean.FALSE.equals(newV) && (btnAdd.isPressed() || btnCalculate.isPressed())) tableView.getSelectionModel().clearSelection();
        });
    }

    /*///////////////////////////////////////////// MENU PANE CONTROLLER /////////////////////////////////////////////*/

    /**
     * FXML function to handle any button pressed on the home pane
     *
     * @param event the action performed
     */
    @FXML protected void homePaneHandler(ActionEvent event) throws InvalidParameterException {
        if(event == null) throw new InvalidParameterException("action event cannot be null");

        else if(event.getSource() == btnAdd) addBtnHandler();
        else if(event.getSource() == btnEdit) editBtnHandler();
        else if(event.getSource() == btnRemove) removeBtnHandler();
        else if(event.getSource() == btnCalculate) calculateBtnHandler();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     * Helper function to hide the Home Pane
     */
    void hide(){
        homePane.setVisible(false);
        homePane.toBack();
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Helper function to show the Home Pane
     */
    void show(){
        homePane.setVisible(true);
        homePane.toFront();
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Helper function to handle the add button being pressed
     */
    protected void addBtnHandler(){
        logr.info(Main.replaceBundleString("userBtnClick", btnAdd.getText()));

        // asking the user for the shipment details before letting them add the first isotope
        // to the list
        if(tableView.getItems().isEmpty()) {
            try {
                openPage(FXMLView.SHIPMENT_DETAILS);
            } catch (Exception e) {
                logr.log(Level.SEVERE, "Failed to show Shipment Details page. Error: ", e);
            }
        } else {
            try {
                openPage(FXMLView.ADD);
            } catch (Exception e) {
                logr.log(Level.SEVERE, "Failed to show Add page. Error: ", e);
            }
        }
    }

    /**
     * Helper function to handle the edit button being pressed
     */
    protected void editBtnHandler(){
        logr.info(Main.replaceBundleString("userBtnClick", btnEdit.getText()));

        try {
            openPage(FXMLView.EDIT);
        } catch (Exception e){
            logr.log(Level.SEVERE, "Failed to show Edit page. Error: ", e);
        }
    }

    /**
     * Helper function to handle the remove button being pressed
     */
    protected void removeBtnHandler(){
        logr.info(Main.replaceBundleString("userBtnClick", btnRemove.getText()));

        List<Isotope> toDelete = new ArrayList<>(getSelectedRows());  // getting selected isotopes

        tableView.getSelectionModel().clearSelection();  // clearing the selection

        TableEditor.getIsotopes().removeAll(toDelete);  // removing the selected isotopes from the tableEditor

        tableView.setItems(TableEditor.getIsotopes());  // updating GUI with edited list of isotopes
    }

    /**
     * Helper function to handle the calculate button being pressed
     */
    protected void calculateBtnHandler(){
        logr.info(Main.replaceBundleString("userBtnClick", btnCalculate.getText()));
    }

        /**
     * Helper function to open open and initialize a page
     * 
     * @param view the view for the page to be opened
     * @throws IOException
     */
    void openPage(@NotNull FXMLView view) throws IOException {
        var stage = setupStage(view);
        if (ModifyController.isModifyInit()) {
            stage.show();
            logr.info("Opened " + view.getName());
        } else {
            logr.info("Failed to initialize " + view.getName());
        }

        // Sets the settings for closing the Modify Window
        stage.setOnCloseRequest(e -> {
            e.consume();
            logr.info("Closing the " + view.getName());
            ModifyController.resetModifyInit();
            ModifyController.resetStart();
            stage.close();
        });
    }

    /**
     * Helper function to set up the stage for the Add and Edit Pane
     *
     * @param view the FXMLView that contains more details about the fxml file
     *             (title, icon image, width of stage, height of stage, name of the file itself)
     *
     * @return the Stage setup to show the Add or Edit Pane
     */
    public Stage setupStage(@NotNull final FXMLView view) throws java.io.IOException {
        var fxmlLoader = new FXMLLoader(getClass().getResource( view.getFxmlLoc()), Main.getBundle());

        Parent root1 = fxmlLoader.load();
        var stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(view.getTitle());
        stage.getIcons().add(view.getIconImage());
        stage.setScene(new Scene(root1, view.getWidth(), view.getHeight()));
        stage.setResizable(false);

        if(view == FXMLView.ADD || view == FXMLView.EDIT) {
            ModifyController controller = fxmlLoader.getController();
            controller.injectHomePaneController(this, view.getTitle());
        } else if (view == FXMLView.SHIPMENT_DETAILS) {
            ShipmentDetailsController controller = fxmlLoader.getController();
            controller.injectHomePaneController(this);
        }

        return stage;
    }

    /**
     * Helper function to make sure home btn css is activated
     */
    protected void mouseAction() throws InvalidParameterException {
        btnAdd.getStyleClass().removeAll("topHomeBtns");
        btnEdit.getStyleClass().removeAll("topHomeBtns");
        btnRemove.getStyleClass().removeAll("topHomeBtns");
        btnCalculate.getStyleClass().removeAll("calculateBtn");

        btnAdd.getStyleClass().add("topHomeBtns");
        btnEdit.getStyleClass().add("topHomeBtns");
        btnRemove.getStyleClass().add("topHomeBtns");
        btnCalculate.getStyleClass().add("calculateBtn");
    }

    /**
     * Helper function to get the list of selected rows
     *
     * @return the Observable list of DeviceInfo which represents
     *          all the selected Devices in teh table at that time
     */
    ObservableList<Isotope> getSelectedRows(){
        return tableView.getSelectionModel().getSelectedItems();
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/
    /**
     * Getter function to get the shipment instance
     *
     * @return the main controller instance
     */
    protected MainController getMainController() { return mainController; }

    /**
     * Getter function to get the shipment instance
     *
     * @return the shipment instance
     */
    public static synchronized Shipment getShipment() { return shipment; }

    /**
     * Getter function to get the TableEditor instance
     *
     * @return the current instance of the TableEditor object linked to the Home Pane
     */
    public TableEditor getTableEditor() { return tableEditor;}

    /**
     * Getter function to get the TableView object
     *
     * @return the current TableView instance from the Home Pane
     */
    public TableView<Isotope> getTableView() { return tableView;}

    /**
     * Getter function to get the table index of the Device that is already in the table
     *
     * @return the index of where the duplicate Device is in the table
     */
    @Contract(pure = true) public static int getRepeatIndex() { return repeatIndex; }

    /**
     * Setter function to set the location of the duplicate device in the table
     *
     * @param newIndex the index of the duplicate Device (0 based)
     */
    public static void setRepeatIndex(int newIndex) { repeatIndex = newIndex; }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/
    /**
     * Setter function to set the shipment instance
     *
     * @param mainController the new shipment instance
     */
    protected void setMainController(MainController mainController) throws InvalidParameterException {
        if(mainController == null) throw new InvalidParameterException("mainController cannot be null");
        this.mainController = mainController;
    }

    /**
     * Setter function to set the shipment instance
     *
     * @param shipment the new shipment instance
     */
    public static synchronized void setShipment(Shipment shipment) {
        HomePaneController.shipment = shipment;
    }
}

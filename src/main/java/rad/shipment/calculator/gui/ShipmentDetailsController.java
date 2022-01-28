package rad.shipment.calculator.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import rad.shipment.calculator.helpers.Conversions;
import rad.shipment.calculator.helpers.DatePickerConverter;
import rad.shipment.calculator.helpers.Shipment;
import rad.shipment.calculator.view.FXMLView;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShipmentDetailsController {
    // Declaring FXML objects
    @FXML private VBox vBoxShipmentDetails;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtFieldMass;
    @FXML private ChoiceBox<String> choiceBoxMassUnit;
    @FXML private ChoiceBox<String> choiceBoxMassName;
    @FXML private ChoiceBox<String> choiceBoxNature;
    @FXML private ChoiceBox<String> choiceBoxState;
    @FXML private ChoiceBox<String> choiceBoxForm;
    @FXML private Button btnSave;

    // Declaring variables
    private static final Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);  // getting logger
    private static boolean shipmentDetailsInit = false;  // boolean to check if the initialize function completed successfully
    private static final BooleanProperty start = new SimpleBooleanProperty(true);  // boolean to check if user just started inputting the ip address
    private HomePaneController homePaneController;

    /*///////////////////////////////////////////////// START/SETUP //////////////////////////////////////////////////*/

    /**
     * FXML Function to initialize GUI (run when the modify.fxml file is loaded by the FXMLLoader)
     */
    @FXML private void initialize() {
        // setting up the date picker
        datePicker.setValue(LocalDate.now()); // setting the date picker to today's date
        datePicker.setEditable(true);
        datePicker.setPromptText(Main.getString("datePattern").toLowerCase());
        datePicker.setConverter(new DatePickerConverter());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // disabling all the dates in the future
                // -1 = first date is before second date (second date is in the future)
                // 0  = dates are the same
                // 1  = first date is after second date (second date is in the past)
                setDisable(empty || LocalDate.now().compareTo(date) < 0 );
            }
        });

        // adding values to the choiceboxes
        choiceBoxMassUnit.setItems(Conversions.getSiPrefixes());
        choiceBoxMassName.setItems(Conversions.getMassUnits());
        choiceBoxNature.setItems(Main.getNatures());
        choiceBoxState.setItems(Main.getStates());
        choiceBoxForm.setItems(Main.getForms());

        // setting the default values for the choiceBoxes
        choiceBoxMassUnit.getSelectionModel().clearAndSelect(Conversions.DEFAULT_MASS_SI_INDEX);
        choiceBoxMassName.getSelectionModel().clearAndSelect(Conversions.DEFAULT_MASS_INDEX);
        choiceBoxNature.getSelectionModel().clearAndSelect(Main.DEFAULT_NATURE_INDEX);
        choiceBoxState.getSelectionModel().clearAndSelect(Main.DEFAULT_STATE_INDEX);
        choiceBoxForm.getSelectionModel().clearAndSelect(Main.DEFAULT_FORM_INDEX);

        bindNextBtnDisableProp();  // Disabling the Next button

        // Making sure only numbers can be input into the textfield
        txtFieldMass.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // if user inputs any non numerical characters, remove them
                    if (!newValue.matches("\\d*")) txtFieldMass.setText(newValue.replaceAll("[^\\d]", ""));
                }
        );

        // making sure if user selects liters mass name, state is changed to liquid
        choiceBoxMassName.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if("liters".equals(newValue)) choiceBoxState.getSelectionModel().select("Liquid");
                    else choiceBoxState.getSelectionModel().select("Solid");
                }
        );

        // making sure if user selects liquid state, mass name is changed to liters
        choiceBoxState.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if("Liquid".equals(newValue)) choiceBoxMassName.getSelectionModel().select("liters");
                    else choiceBoxMassName.getSelectionModel().select("grams");
                }
        );

        // Allowing the user to press enter to select a button if focused on it
        MainController.setBtnFireOnEnter(btnSave);

        shipmentDetailsInit = true;  // noting that the fxml initialize function completed successfully
    }

    /**
     * Function to connect the Add Controller with the current HomePaneController
     * (Allows the Add Controller to have access to anything on the HomePaneController through this link)
     *
     * @param homePaneController the instance of the current HomePaneController
     */
    void injectHomePaneController(HomePaneController homePaneController) { this.homePaneController = homePaneController; }

    /*///////////////////////////////////////////// MENU PANE CONTROLLER /////////////////////////////////////////////*/

    /**
     * FXML function to handle any button pressed on the add pane
     *
     * @param event the action performed
     */
    @FXML protected void shipmentDetailsHandler(ActionEvent event) throws InvalidParameterException {
        if(event == null) throw new InvalidParameterException("action event cannot be null");

        else if(event.getSource() == btnSave) saveBtnHandler();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to handle the add button being pressed
     */
    protected void saveBtnHandler(){
        logr.info(Main.replaceBundleString("usrBtnClick", btnSave.getText()));

        // saving the users settings
        float mass = Conversions.convertToBase(Float.parseFloat(txtFieldMass.getText()), choiceBoxMassUnit.getSelectionModel().getSelectedIndex());

        HomePaneController.setShipment(
                new Shipment(mass,
                        choiceBoxNature.getValue(),
                        choiceBoxState.getValue(),
                        choiceBoxForm.getValue(),
                        datePicker.getValue())
        );

        try {
            FXMLView view = FXMLView.ADD;
            Stage stage = homePaneController.setupStage(view);
            if (ModifyController.isModifyInit()) {
                stage.show();
                logr.info("Opened Add Page");
            } else {
                logr.info("Failed to initialize Add Page");
            }

            // Sets the settings for closing the Modify Window
            stage.setOnCloseRequest(e -> {
                e.consume();
                logr.info("Closing the Add Page");
                ModifyController.resetModifyInit();
                ModifyController.resetStart();
                stage.close();
            });
        } catch (Exception e) {
            logr.log(Level.SEVERE, "Failed to show Add page. Error: ", e);
        }

        ((Stage)btnSave.getScene().getWindow()).close();  // closing the window
    }

    /**
     * Helper function to bind the disable property of the Next button
     * Button will be enabled if Isotope Name TextField and Initial Activity PassField contain valid data
     */
    private void bindNextBtnDisableProp() {
        btnSave.disableProperty().unbind();  // unbinding button if already bound


        btnSave.disableProperty().bind(Bindings.isEmpty(txtFieldMass.textProperty()));
    }

    /*///////////////////////////////////////////// GETTERS AND SETTERS //////////////////////////////////////////////*/

    /**
     * Getter function to figure out if the initialize function completed
     *
     * @return true if the initialize function ran successfully
     *         false if the initialize function has not run yet or failed to complete (error occurred)
     */
    @Contract(pure = true) static boolean isShipmentDetailsInit() { return shipmentDetailsInit; }

    /**
     * Setter Function to reset shipmentDetailsInit to its declared value
     */
    static void resetShipmentDetailsInit() { shipmentDetailsInit = false; }

    /**
     * Setter Function to reset start to its declared value
     */
    static void resetStart() { start.set(true); }
}

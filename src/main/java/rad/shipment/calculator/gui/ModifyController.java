package rad.shipment.calculator.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import rad.shipment.calculator.helpers.Conversions;
import rad.shipment.calculator.helpers.DatePickerConverter;
import rad.shipment.calculator.helpers.Isotope;
import rad.shipment.calculator.view.FXMLView;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.logging.Logger;

public class ModifyController {
    // Declaring FXML objects
    @FXML private VBox vBoxFirstPage;
    @FXML private TextField txtFieldIsoName;
    @FXML private TextField txtFieldA0;
    @FXML private ChoiceBox<String> choiceBoxA0Unit;
    @FXML private ChoiceBox<String> choiceBoxA0Name;
    @FXML private HBox hBoxAddInfoTop;
    @FXML private VBox vBoxShortLong;
    @FXML private ToggleGroup toggleGrpShortLong;
    @FXML private RadioButton radioBtnShortLived;
    @FXML private RadioButton radioBtnLongLived;
    @FXML private VBox vBoxLungAbs;
    @FXML private ToggleGroup toggleGrpLungAbs;
    @FXML private RadioButton radioBtnSlowLungAbs;
    @FXML private RadioButton radioBtnMediumLungAbs;
    @FXML private RadioButton radioBtnFastLungAbs;
    @FXML private Text txtFirstPageStatus;
    @FXML private Button btnNext;

    @FXML private VBox vBoxSecondPage;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtFieldMass;
    @FXML private ChoiceBox<String> choiceBoxMassUnit;
    @FXML private ChoiceBox<String> choiceBoxMassName;
    @FXML private ChoiceBox<String> choiceBoxNature;
    @FXML private ChoiceBox<String> choiceBoxState;
    @FXML private ChoiceBox<String> choiceBoxForm;
    @FXML private CheckBox chckBoxSameMass;
    @FXML private CheckBox chckBoxSameNSF;
    @FXML private Button btnBack;
    @FXML private Button btnFinish;
    @FXML private Text txtSecondPageStatus;

    // Declaring variables
    private static final Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);  // getting logger
    private final BooleanProperty validName =  new SimpleBooleanProperty();  // Boolean property to know if the current isotope name is valid
    private static boolean modifyInit = false;  // boolean to check if the initialize function completed successfully
    private static final BooleanProperty start = new SimpleBooleanProperty(true);  // boolean to check if user just started inputting the ip address
    private HomePaneController homePaneController;
    private boolean isFirstPage;
    private Isotope isotope;  // the isotope to be edited
    private boolean isAddPage;

    /*///////////////////////////////////////////////// START/SETUP //////////////////////////////////////////////////*/

    /**
     * FXML Function to initialize GUI (run when the modify.fxml file is loaded by the FXMLLoader)
     */
    @FXML private void initialize() {
        showPage(1);  // showing the first page

        // adding values to the choiceboxes
        choiceBoxA0Unit.setItems(Conversions.getSiPrefixes());
        choiceBoxA0Name.setItems(Conversions.getRadioactivityUnits());
        choiceBoxMassUnit.setItems(Conversions.getSiPrefixes());
        choiceBoxMassName.setItems(Conversions.getMassUnits());
        choiceBoxNature.setItems(Main.getNatures());
        choiceBoxState.setItems(Main.getStates());
        choiceBoxForm.setItems(Main.getForms());


        // setting the default values for the choiceBoxes
        choiceBoxA0Unit.getSelectionModel().clearAndSelect(Conversions.DEFAULT_RAD_SI_INDEX);
        choiceBoxA0Name.getSelectionModel().clearAndSelect(Conversions.DEFAULT_RAD_INDEX);
        choiceBoxMassUnit.getSelectionModel().select(Conversions.DEFAULT_MASS_SI_INDEX);

        // setting the values to be the same as the shipment details
        datePicker.setValue(HomePaneController.getShipment().getRefDate());
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
        txtFieldMass.setText(String.valueOf(HomePaneController.getShipment().getMass()));
        choiceBoxMassName.getSelectionModel().select(
                ("Liquid".equals(HomePaneController.getShipment().getState()))? "liters" : "grams"
        );
        choiceBoxNature.getSelectionModel().select(HomePaneController.getShipment().getNature());
        choiceBoxState.getSelectionModel().select(HomePaneController.getShipment().getState());
        choiceBoxForm.getSelectionModel().select(HomePaneController.getShipment().getForm());

        // binding the managed properties to the visibility of the node
        // link: https://stackoverflow.com/questions/28558165/javafx-setvisible-doesnt-hide-the-element
        hBoxAddInfoTop.managedProperty().bind(hBoxAddInfoTop.visibleProperty());
        vBoxShortLong.managedProperty().bind(vBoxShortLong.visibleProperty());
        vBoxLungAbs.managedProperty().bind(vBoxLungAbs.visibleProperty());
        txtFirstPageStatus.managedProperty().bind(txtFirstPageStatus.visibleProperty());
        txtSecondPageStatus.managedProperty().bind(txtSecondPageStatus.visibleProperty());
        btnBack.managedProperty().bind(btnBack.visibleProperty());

        // Hiding the additional info section
        showShortLong(false);
        showLungAbs(false);

        validName.set(!isAddPage);

        // Adding a listener to the Isotope textfield to check when any changes are made
        txtFieldIsoName.textProperty().addListener((observable, oldValue, newValue) -> {
            // checking if isotope is in te valid isotopes table and updating boolean property
            if(!"".equals(newValue)) validName.setValue(!"".equals(Main.getDBEditor().getAbbr(newValue)));  // checking if text in the Isotope field is valid on every keystroke
            else validName.setValue(false);

            needAdditionalInfo(newValue);  // checking to see if the additional info section needs to be enabled

            if(!vBoxShortLong.isVisible() && !vBoxLungAbs.isVisible() && !"".equals(txtFieldA0.getText())) start.set(false);  // If the Initial Activity field contains a value, no longer starting form.
            else if((vBoxShortLong.isVisible() ||  vBoxLungAbs.isVisible()) &&
                    (toggleGrpShortLong.getSelectedToggle() == null || toggleGrpLungAbs.getSelectedToggle() == null)) {
                start.set(true);
                if(validName.get()) {
                    setValidRegion(txtFieldIsoName, true);
                    hideModifyError();
                } else {
                    setValidRegion(txtFieldIsoName, false);
                    showModifyError("Isotope name is invalid or cannot calculate classification for given isotope");
                }
            }

            /* if the user has already focused on the isotope name and initial activity (the user has clicked on both fields at least once)
             * check the form and highlight fields as necessary */
            if(!start.get())  checkForm(txtFieldA0.getText(), false);  // check form and highlight as needed but don't move to next page
        });

        // Allowing the user to press enter while focused on the isotope name field to move to the password field
        txtFieldIsoName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // if the user clicks enter and the ip is valid, move to the password field
                if (validName.getValue())  {
                    txtFieldA0.requestFocus();  // moving cursor to initial activity field
                    if(!vBoxShortLong.isVisible() && !vBoxLungAbs.isVisible()) start.set(false);
                } else {
                    setValidRegion(txtFieldIsoName, false);
                    showModifyError("Isotope name is invalid or cannot calculate classification for given isotope");
                }

                // checking the entire form if we haven't just started the form
                if(!start.get()) checkForm(txtFieldA0.getText(), false);  // check form and highlight as needed but don't move to next page
            }
        });

        // Adding a listener to the Initial Activity textfield to check when any changes are made
        // also making sure only numbers can be input into the textfield
        txtFieldA0.textProperty().addListener((observable, oldValue, newValue) -> {
            // if user inputs any non numerical characters, remove them
            if (!newValue.matches("\\d*")) txtFieldA0.setText(newValue.replaceAll("[^\\d]", ""));

            /* if user clicks on the initial activity field, and none of the radio buttons are enabled
             * since that is the last input in the form
             * they are no longer at the start of the form
             * now any errors made on the form before will be highlighted in red */
            if(!vBoxShortLong.isVisible() && !vBoxLungAbs.isVisible()) start.set(false);

            if(!start.get()) checkForm(txtFieldA0.getText(), false);  // check form and highlight as needed but don't move to next page
        });

        // Allowing the user to press enter while focused on the initial activity field to start the Add Device task
        txtFieldA0.setOnKeyPressed(event -> {
            // checking the form before starting the add process
            if(event.getCode() == KeyCode.ENTER) checkForm(txtFieldA0.getText(), true);
        });

        // Adding a listener to the short/long lived radio buttons toggle group to check when changes are made
        toggleGrpShortLong.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // if the user selected a radio button, they have reached the end of the form and
                    // so we set the start boolean property ro false
                    if(newValue != null) start.set(false);

                    checkForm(txtFieldA0.getText(), false);  // check form and highlight as needed but don't move to next page
                }
        );

        // Adding a listener to the short/long lived radio buttons toggle group to check when changes are made
        toggleGrpLungAbs.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // if the user selected a radio button, they have reached the end of the form and
                    // so we set the start boolean property ro false
                    if(newValue != null) start.set(false);

                    checkForm(txtFieldA0.getText(), false);  // check form and highlight as needed but don't move to next page
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

        bindNextBtnDisableProp();  // Disabling the Next button

        // Making sure only numbers can be input into the textfield
        txtFieldMass.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // if user inputs any non numerical characters, remove them
                    if (!newValue.matches("\\d*")) txtFieldMass.setText(newValue.replaceAll("[^\\d]", ""));
                }
        );

        // Allowing the user to press enter to select a button if focused on it
        MainController.setBtnFireOnEnter(btnNext);

        modifyInit = true;  // noting that the fxml initialize function completed successfully
    }

    /**
     * Function to connect the Add Controller with the current HomePaneController
     * (Allows the Add Controller to have access to anything on the HomePaneController through this link)
     *
     * @param homePaneController the instance of the current HomePaneController
     */
    void injectHomePaneController(HomePaneController homePaneController, String title) {
        this.homePaneController = homePaneController;

        if("".equals(title) || title == null) throw new InvalidParameterException("Invalid modify stage title");
        else isAddPage = title.equals(FXMLView.ADD.getTitle());

        if(!isAddPage) {
            isotope = homePaneController.getSelectedRows().get(0);  // getting the selected device
            populateForm();  // adding all the given isotope's information to the form
        }
    }

    /*///////////////////////////////////////////// MENU PANE CONTROLLER /////////////////////////////////////////////*/

    /**
     * FXML function to handle any button pressed on the add pane
     *
     * @param event the action performed
     */
    @FXML protected void modifyPaneHandler(ActionEvent event) throws InvalidParameterException {
        if(event == null) throw new InvalidParameterException("action event cannot be null");

        else if(event.getSource() == btnNext) nextBtnHandler();
        else if(event.getSource() == chckBoxSameMass) sameMassChckBoxHandler();
        else if(event.getSource() == chckBoxSameNSF) sameNSFChckBoxHandler();
        else if(event.getSource() == btnBack) backBtnHandler();
        else if(event.getSource() == btnFinish) finishBtnHandler();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to handle the next button being pressed
     */
    protected void nextBtnHandler(){
        logr.info(Main.replaceBundleString("usrBtnClick", btnNext.getText()));

        showPage(2);  // showing the second page
    }

    /**
     * Helper function to handle the consistent mass checkbox
     */
    protected void sameMassChckBoxHandler() {
        if(chckBoxSameMass.isSelected()) disableMassInput(true);
        else disableMassInput(false);
    }

    /**
     * Helper function to handle the consistent nature,state,form checkbox
     */
    protected void sameNSFChckBoxHandler() {
        if(chckBoxSameNSF.isSelected()) disableNSFInput(true);
        else disableNSFInput(false);
    }

    /**
     * Helper function to enable or disable the choiceBoxes for
     * changing the mass of the isotope
     *
     * @param value true if the choiceBoxes should be disabled
     *              false otherwise
     */
    protected void disableMassInput(boolean value) {
        txtFieldMass.setDisable(value);
        choiceBoxMassUnit.setDisable(value);
        choiceBoxMassName.setDisable(value);
    }

    /**
     * Helper function to enable or disable the choiceBoxes for
     * changing the nature, state, and form of the isotope
     *
     * @param value true if the choiceBoxes should be disabled
     *              false otherwise
     */
    protected void disableNSFInput(boolean value) {
        choiceBoxNature.setDisable(value);
        choiceBoxState.setDisable(value);
        choiceBoxForm.setDisable(value);
    }

    /**
     * Helper function to handle the add button being pressed
     */
    protected void finishBtnHandler(){
        logr.info(Main.replaceBundleString("usrBtnClick", btnFinish.getText()));

        // converting the mass to either grams or liters
        float mass = Conversions.convertToBase(Float.parseFloat(txtFieldMass.getText()), choiceBoxMassUnit.getSelectionModel().getSelectedIndex());

        Isotope isotope = new Isotope(txtFieldIsoName.getText(),
                mass,
                choiceBoxMassName.getValue(),
                convertToMicroCuries(Float.parseFloat(txtFieldA0.getText())),
                choiceBoxNature.getValue(),
                choiceBoxState.getValue(),
                choiceBoxForm.getValue(),
                datePicker.getValue());  // creating a new isotope

        if(isAddPage) {
            // checking for short/long or lung absorption
            if(vBoxShortLong.isVisible())  isotope.set_ShortLong(toggleGrpShortLong.getSelectedToggle().toString());  // adding short/long
            else if(vBoxLungAbs.isVisible()) isotope.set_LungAbs(toggleGrpLungAbs.getSelectedToggle().toString());  // adding lung absorption

            // adding the new isotope to the shipment
            if(!homePaneController.getTableEditor().addRow(isotope)) showModifyError("Isotope is already in shipment");
            else ((Stage)btnFinish.getScene().getWindow()).close();  // closing the window
        } else {
            // editing the isotope in the shipment
            if(!homePaneController.getTableEditor().editRow(isotope)) showModifyError("Isotope Already in Shipment!");
            else ((Stage)btnFinish.getScene().getWindow()).close();
        }
    }

    /**
     * Helper function to handle the add button being pressed
     */
    protected void backBtnHandler(){
        logr.info(Main.replaceBundleString("usrBtnClick", btnBack.getText()));
        showPage(1);
    }

    protected float convertToMicroCuries(float value) {
        // checking if the user selected becquerels or curies
        switch(choiceBoxA0Name.getSelectionModel().getSelectedIndex()) {
            case 0:  // user selected becquerel, converting to microCuries
                return Conversions.baseToMicro(
                        Conversions.BqToCi(
                                Conversions.convertToBase(value,
                                        choiceBoxA0Unit.getSelectionModel().getSelectedIndex())));
            case 1:  // user selected curie, converting to microCuries
                return Conversions.baseToMicro(
                        Conversions.convertToBase(value,
                                choiceBoxA0Unit.getSelectionModel().getSelectedIndex()));
            default:
                return value;
        }
    }

    /**
     * Helper function to populate the edit page with all the information from
     * the selected isotope in the table
     */
    protected void populateForm() {
        txtFieldIsoName.setText(isotope.get_AbbrName());
        txtFieldA0.setText(String.valueOf(isotope.get_A0()));
        txtFieldMass.setText(String.valueOf(isotope.getMassValue()));

        choiceBoxMassUnit.getSelectionModel().clearAndSelect(Conversions.DEFAULT_MASS_SI_INDEX);
        choiceBoxMassName.getSelectionModel().clearAndSelect(
                ("Liquid".equals(isotope.get_State()))? 1 : 0
        );

        choiceBoxNature.setValue(isotope.get_Nature());
        choiceBoxState.setValue(isotope.get_State());
        choiceBoxForm.setValue(isotope.get_Form());
    }

    /**
     * Helper function to show the given add page
     *
     * @param pageNum the number of the page to be shown
     */
    protected void showPage(int pageNum) {
        if(pageNum <= 1) {
            // hiding second page
            vBoxSecondPage.setVisible(false);
            vBoxSecondPage.toBack();

            // showing first page
            vBoxFirstPage.setVisible(true);
            vBoxFirstPage.toFront();
            btnNext.requestFocus();

            isFirstPage = true;
        } else {
            // hiding first page
            vBoxFirstPage.setVisible(false);
            vBoxFirstPage.toBack();

            // making sure consistent mass/nature, state, form checkboxes are checked
            chckBoxSameMass.setSelected(true);
            chckBoxSameNSF.setSelected(true);

            // making sure the user cannot edit values while checkboxes are selected
            disableMassInput(true);
            disableNSFInput(true);

            if(isAddPage) {
                // Making sure the finish button says add
                btnFinish.setText("Add");

                // hiding the back button
                btnBack.setVisible(false);
            } else {
                // Making sure the finish button says edit
                btnFinish.setText("Edit");

                // showing the back button
                btnBack.setVisible(true);
            }

            // showing second page
            vBoxSecondPage.setVisible(true);
            vBoxSecondPage.toFront();
            btnFinish.requestFocus();

            isFirstPage = false;
        }
        hideModifyError();
    }

    /**
     * Helper function to hide or show the Short/Long lived radio buttons
     *
     * @param isShown true if the radio buttons should be visible
     *                false if the radio buttons should be hidden
     */
    protected void showShortLong(boolean isShown) {
        if(isShown){
            hBoxAddInfoTop.setVisible(true);
            vBoxShortLong.setVisible(true);
        } else {
            hBoxAddInfoTop.setVisible(vBoxLungAbs.isVisible());
            vBoxShortLong.setVisible(false);
        }
    }

    /**
     * Helper function to hide or show the Slow/Medium/Fast lung absorption radio buttons
     *
     * @param isShown true if the radio buttons should be visible
     *                false if the radio buttons should be hidden
     */
    protected void showLungAbs(boolean isShown) {
        if(isShown){
            hBoxAddInfoTop.setVisible(true);
            vBoxLungAbs.setVisible(true);
        } else {
            hBoxAddInfoTop.setVisible(vBoxShortLong.isVisible());
            vBoxLungAbs.setVisible(false);
        }
    }

    /**
     * Helper function to make a region valid or invalid (highlighted in red)
     *
     * @param region the region to make invalid or valid
     * @param isValid true if region should be valid (no highlight)
     *                false if region should be invalid (red highlight)
     */
    private void setValidRegion(Region region, Boolean isValid) throws InvalidParameterException {
        if(region.equals(txtFieldIsoName)) {
            txtFieldIsoName.getStyleClass().removeAll("validRegion", "invalidRegion");
            txtFieldIsoName.getStyleClass().add(isValid ? "validRegion" : "invalidRegion");
        } else if(region.equals(txtFieldA0)) {
            txtFieldA0.getStyleClass().removeAll("validRegion", "invalidRegion");
            txtFieldA0.getStyleClass().add(isValid ? "validRegion" : "invalidRegion");
        } else if(region.equals(vBoxShortLong)){
            vBoxShortLong.getStyleClass().removeAll("validRegion", "invalidRegion");
            vBoxShortLong.getStyleClass().add(isValid ? "validRegion" : "invalidRegion");
        } else if(region.equals(vBoxLungAbs)){
            vBoxLungAbs.getStyleClass().removeAll("validRegion", "invalidRegion");
            vBoxLungAbs.getStyleClass().add(isValid ? "validRegion" : "invalidRegion");
        }
        else throw new InvalidParameterException("invalid region");
    }

    /**
     * Helper function to bind the disable property of the Next button
     * Button will be enabled if Isotope Name TextField and Initial Activity PassField contain valid data
     */
    private void bindNextBtnDisableProp() {
        btnNext.disableProperty().unbind();  // unbinding button if already bound

        // A = valid Isotope Name !A = invalid Isotope Name
        // B = entered initial activity !B = initial activity is empty
        // C = Short/Long Lived radio buttons are visible !C = Short/Long Lived radio buttons are invisible
        // D = selected a Short/Long Lived radio button !D = did not select a Short/Long Lived radio button
        // E = Slow/Medium/Fast Lung Absorption radio buttons are visible !E = Slow/Medium/Fast Lung Absorption radio radio buttons are invisible
        // F = selected a Slow/Medium/Fast Lung Absorption radio button !F = did not select a Slow/Medium/Fast Lung Absorption radio button
        // Next is disabled when this is true !A || !B || (E!F) || (C!D)
        // Bindings.and( ,)
        btnNext.disableProperty().bind(
                Bindings.or(Bindings.not(validName),
                        Bindings.or(txtFieldA0.textProperty().isEmpty(),
                                Bindings.or(Bindings.and(vBoxShortLong.visibleProperty(),toggleGrpShortLong.selectedToggleProperty().isNull()),
                                        Bindings.and(vBoxLungAbs.visibleProperty(), toggleGrpLungAbs.selectedToggleProperty().isNull())
                                )
                        )
                )
        );
    }

    /**
     * Helper function to show or hide the additional info radio buttons based
     * on the given isotope name
     *
     * @param isoName the name of the isotope
     */
    private void needAdditionalInfo(String isoName) {
        if(isoName == null || "".equals(isoName)) {
            // making sure additional info section is invisible
            showShortLong(false);
            showLungAbs(false);

            // unselecting all the radio buttons
            if(toggleGrpShortLong.getSelectedToggle() != null) toggleGrpShortLong.getSelectedToggle().setSelected(false);
            if(toggleGrpLungAbs.getSelectedToggle() != null) toggleGrpLungAbs.getSelectedToggle().setSelected(false);

            return;
        }

        String abbr = Main.getDBEditor().getAbbr(isoName);

        if("".equals(abbr)) {
            // making sure additional info section is invisible
            showShortLong(false);
            showLungAbs(false);

            // unselecting all the radio buttons
            if(toggleGrpShortLong.getSelectedToggle() != null) toggleGrpShortLong.getSelectedToggle().setSelected(false);
            if(toggleGrpLungAbs.getSelectedToggle() != null) toggleGrpLungAbs.getSelectedToggle().setSelected(false);

            return;
        }

        // checking if short/long lived needs to be enabled
        if(Main.getDBEditor().getShortLong().contains(abbr)) showShortLong(true);
        else showShortLong(false);

        // checking if lung absorption needs to be enabled
        if(abbr.contains("U-23")) showLungAbs(true);
        else showLungAbs(false);

    }

    /**
     * Helper Function to let the user know that the device they are trying
     * to add is already in the table
     *
     * @param errStr the string to be shown in red on the add pane
     */
    private void showModifyError(String errStr) {
        if(isFirstPage) {
            txtFirstPageStatus.setFill(Color.RED);  // making status text red
            txtFirstPageStatus.setText(errStr);  // setting the string to the given value
            txtFirstPageStatus.setVisible(true);  // making sure text is visible

            // disabling the next button
            btnNext.disableProperty().unbind();
            btnNext.setDisable(true);
        } else {
            txtSecondPageStatus.setFill(Color.RED);  // making status text red
            txtSecondPageStatus.setText(errStr);  // setting the string to the given value
            txtSecondPageStatus.setVisible(true);  // making sure text is visible
        }
    }

    /**
     * Helper Function to hide previous error message
     */
    private void hideModifyError() {
        if (isFirstPage) {
            txtFirstPageStatus.setFill(Color.BLACK);  // reverting the status text color back to black
            txtFirstPageStatus.setText("");  // removing text just in case
            txtFirstPageStatus.setVisible(false);  // making sure text is invisible

            bindNextBtnDisableProp();  // rebinding the add button to be disabled based on user input
        } else {
            txtSecondPageStatus.setFill(Color.BLACK);  // reverting the status text color back to black
            txtSecondPageStatus.setText("");  // removing text just in case
            txtSecondPageStatus.setVisible(false);  // making sure text is invisible
        }
    }

    /**
     * Helper function to check the textFields to make sure they contain valid data
     * and if the data is valid move to teh next page of the add pane
     *
     * @param A0Text the text from the Initial Activity Field
     * @param fireBtn true if should move to next page
     *                false if should stay on this page
     */
    private void checkForm(String A0Text, boolean fireBtn) {
        // if user clicks the enter key and the isotope name is valid and there is text in the initial activity field,
        // remove any red borders around fields and move to next page
        if (validName.get() && !"".equals(A0Text)) {
            setValidRegion(txtFieldIsoName, true);
            setValidRegion(txtFieldA0, true);

            if(!vBoxShortLong.isVisible() && !vBoxLungAbs.isVisible()) {
                hideModifyError();
                if (fireBtn) {
                    btnNext.requestFocus();  // moving to next button
                    btnNext.fire();  // clicking the next button
                }
            }

            // checking if short/long lived radio buttons are active
            if(vBoxShortLong.isVisible() && toggleGrpShortLong.getSelectedToggle() == null) {
                setValidRegion(vBoxShortLong, false);
                showModifyError("Please indicate if this isotope is short lived or long lived");
            }
            if(vBoxShortLong.isVisible() && toggleGrpShortLong.getSelectedToggle() != null) {
                setValidRegion(vBoxShortLong, true);
                hideModifyError();
                if (fireBtn) {
                    btnNext.requestFocus();  // moving to next button
                    btnNext.fire();  // clicking the next button
                }
            }

            // checking if lung absorption radio buttons are active
            if(vBoxLungAbs.isVisible() && toggleGrpLungAbs.getSelectedToggle() == null) {
                setValidRegion(vBoxLungAbs, false);
                showModifyError("Please select a lung absorption rate");
            }
            if(vBoxLungAbs.isVisible() && toggleGrpLungAbs.getSelectedToggle() != null) {
                setValidRegion(vBoxLungAbs, true);
                hideModifyError();
                if (fireBtn) {
                    btnNext.requestFocus();  // moving to next button
                    btnNext.fire();  // clicking the next button
                }
            }
        }

        // if user clicks the enter key and the isotope name is not valid and there is no text in the initial activity field,
        // add red borders around both fields
        else if (!validName.get() && "".equals(A0Text)) {
            setValidRegion(txtFieldIsoName, false);
            setValidRegion(txtFieldA0, false);
            showShortLong(false);
            showLungAbs(false);
            showModifyError("Please enter values for highlighted fields");
        }

        // if user clicks the enter key and the isotope name is not valid but there is text in the initial activity field,
        // highlight the isotope name field and unhighlight the initial activity field
        else if (!validName.get()) {
            setValidRegion(txtFieldIsoName, false);
            setValidRegion(txtFieldA0, true);
            showShortLong(false);
            showLungAbs(false);
            showModifyError("Isotope name is invalid or cannot calculate classification for given isotope");
        }

        // if the user clicks the enter key and the initial activity field is empty but the isotope name is valid,
        // highlight the initial activity field and unhighlight the isotope name field
        else if ("".equals(A0Text)) {
            setValidRegion(txtFieldIsoName, true);
            setValidRegion(txtFieldA0, false);

            if(!vBoxShortLong.isVisible() && !vBoxLungAbs.isVisible()) showModifyError("Initial Activity is required");

            // checking if short/long lived radio buttons are active
            if(vBoxShortLong.isVisible() && toggleGrpShortLong.getSelectedToggle() == null) {
                setValidRegion(vBoxShortLong, false);
                showModifyError("Please enter values for highlighted fields");
            }
            if(vBoxShortLong.isVisible() && toggleGrpShortLong.getSelectedToggle() != null) {
                setValidRegion(vBoxShortLong, true);
                showModifyError("Initial Activity is required");
            }

            // checking if lung absorption radio buttons are active
            if(vBoxLungAbs.isVisible() && toggleGrpLungAbs.getSelectedToggle() == null) {
                setValidRegion(vBoxLungAbs, false);
                showModifyError("Please select a lung absorption rate");
            }
            if(vBoxLungAbs.isVisible() && toggleGrpLungAbs.getSelectedToggle() != null) {
                setValidRegion(vBoxLungAbs, true);
                showModifyError("Initial Activity is required");
            }
        }
    }

    /*///////////////////////////////////////////// GETTERS AND SETTERS //////////////////////////////////////////////*/

    /**
     * Getter function to figure out if the initialize function completed
     *
     * @return true if the initialize function ran successfully
     *         false if the initialize function has not run yet or failed to complete (error occurred)
     */
    @Contract(pure = true) static boolean isModifyInit() { return modifyInit; }

    /**
     * Setter Function to reset modifyInit to its declared value
     */
    static void resetModifyInit() { modifyInit = false; }

    /**
     * Setter Function to reset start to its declared value
     */
    static void resetStart() { start.set(true); }
}

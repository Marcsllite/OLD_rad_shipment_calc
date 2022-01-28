package rad.shipment.calculator.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.security.InvalidParameterException;

public class ReferencePaneController {
    // Declaring FXML objects
    @FXML GridPane referencePane;

    // Declaring variables
    private MainController mainController;

    /*///////////////////////////////////////////////// START/SETUP //////////////////////////////////////////////////*/

    /**
     * FXML Function to initialize GUI (run when the edit.fxml file is loaded by the FXMLLoader)
     */
    @FXML private void initialize(){ }

    /**
     * Function to connect the Reference Pane Controller with the current MainController
     * (Allows the Reference Pane Controller to have access to the other controllers in the MainController through this link)
     *
     * @param mainController the instance of the current MainController
     */
    void injectMainController(MainController mainController){ setMainController(mainController); }

    /*////////////////////////////////////////// REFERENCE PANE CONTROLLER ///////////////////////////////////////////*/

    /**
     * FXML function to handle any button pressed on the reference pane
     *
     * @param event the action performed
     */
    @FXML protected void referencePaneHandler(ActionEvent event) throws InvalidParameterException {
        if(event == null) throw new InvalidParameterException("action event cannot be null");
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     * Helper function to hide the Home Pane
     */
    void hide(){
        referencePane.setVisible(false);
        referencePane.toBack();
    }

    /**
     * Helper function to show the Home Pane
     */
    void show(){
        referencePane.setVisible(true);
        referencePane.toFront();
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/
    /**
     * Getter function to get the mainController instance
     *
     * @return the main controller instance
     */
    protected MainController getMainController() { return mainController; }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/
    /**
     * Setter function to set the mainController instance
     *
     * @param mainController the new mainController instance
     */
    protected void setMainController(MainController mainController) throws InvalidParameterException {
        if(mainController == null) throw new InvalidParameterException("mainController cannot be null");
        this.mainController = mainController;
    }
}

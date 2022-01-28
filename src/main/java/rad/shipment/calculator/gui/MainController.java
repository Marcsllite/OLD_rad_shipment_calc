package rad.shipment.calculator.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;

import java.security.InvalidParameterException;

public class MainController {
    // DECLARING FXML CONTROLLERS
    @FXML private MenuPaneController menuPaneController;
    @FXML private HomePaneController homePaneController;
    @FXML private ReferencePaneController referencePaneController;

    /**
     * FXML Function to initialize GUI (run when the edit.fxml file is loaded by the FXMLLoader)
     */
    @FXML private void initialize(){
        // injecting MainController in menuPaneController to allow
        // the menuPaneController to have access to the other Controllers using the MainController
        menuPaneController.injectMainController(this);
        homePaneController.injectMainController(this);
        referencePaneController.injectMainController(this);

        // making sure home page is on top
        showHomePane();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to allow the given button to be fired
     * when the enter key is pressed and the btn is in focus
     *
     * @param btn the btn to add the listener to
     */
    static void setBtnFireOnEnter(Button btn) throws InvalidParameterException {
        if(btn == null) throw new InvalidParameterException("button cannot be null");

        btn.setOnKeyPressed(
            event -> {
                if(event.getCode() == KeyCode.ENTER && btn.isFocused())
                    btn.fire();
            }
        );
    }

    /**
     * Helper function to allow the given Link to be fired
     * when the enter key is pressed and the btn is in focus
     *
     * @param link the link to add the listener to
     */
    static void setLinkFireOnEnter(Hyperlink link) throws InvalidParameterException {
        if(link == null) throw new InvalidParameterException("link cannot be null");

        link.setOnKeyPressed(
            event -> {
                if(event.getCode() == KeyCode.ENTER && link.isFocused())
                    link.fire();
            }
        );
    }

    void showHomePane() {
        homePaneController.show();
        referencePaneController.hide();
    }

    void showReferencePane() {
        referencePaneController.show();
        homePaneController.hide();
    }
}

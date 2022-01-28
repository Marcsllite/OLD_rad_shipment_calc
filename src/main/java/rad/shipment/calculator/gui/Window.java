package rad.shipment.calculator.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import rad.shipment.calculator.view.FXMLView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Window {
    // Declaring class variables
    private final static Logger logr = Logger.getLogger( Window.class.getName() );  // getting logger
    private final SimpleStringProperty title = new SimpleStringProperty(Main.getString("defaultWindowTitle"));
    private final SimpleStringProperty message = new SimpleStringProperty(Main.getString("defaultMessage"));
    private final SimpleBooleanProperty ret = new SimpleBooleanProperty(false);  // boolean to know if user selected yes or no
    private final List<Button> buttons = new ArrayList<>();

    private static Stage window;

    /**
     * Function to display the Window
     *
     * @param title the title of the window
     * @param message the message to be displayed inside the window
     * @param buttons the buttons to be displayed inside the window
     * @return true if user selected a positive button otherwise false
     */
    public boolean display(String title, String message, Button... buttons) {
        if(title == null || "".equals(title)) title = Main.getString("defaultWindowTitle");

        if(message == null) message = Main.getString("defaultMessage");

        setStage(createWindow(title));
        setScene(window, createScene(message, buttons));
        show(window);

        return getRet();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to show the window
     *
     * @param window the stage to be shown
     */
    protected void show(Stage window) throws InvalidParameterException {
        if(window == null) throw new InvalidParameterException("window cannot be null");
        window.showAndWait();  // showing window and waiting for user input
    }

    /**
     * Helper function to create a new scene with the given title
     *
     * @param title the title of the scene to be created
     */
    protected Stage createWindow(String title){
        if(title == null || "".equals(title)) title = Main.getString("defaultWindowTitle");

        Stage window = new Stage();
        this.title.set(title);  // setting the title simple string value

        // Making sure user can only interact with this window
        // must close this window to interact with previous window
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);  // setting title of window
        window.getIcons().add(FXMLView.MAIN.getIconImage());  // setting the window icon to the same icon as the home pane
        window.setMinWidth(Main.getInt("windowMinWidth"));  // setting the minimum size (horizontally) for the window
        window.centerOnScreen();  // centering the new window in the middle of the screen

        // noting that the user closed the window
        window.setOnCloseRequest(e -> {
            logr.info(Main.getString("windowCloseMsg"));
            window.close();
        });

        logr.info("Created Window titled " + title);
        return window;
    }

    /**
     * Helper function to create a scene with given message and 2 buttons
     *
     * @param message the message to be displayed
     *
     * @return the created Scene
     */
    public Scene createScene(String message, Button... buttons){
        setButtons(buttons);

        // (StackPanes automatically centers and stack their children)
        StackPane line1 = new StackPane(createLabel(message));  // creating a StackPane that contains the message
        VBox layout;

        if(this.buttons.size() <= 0) layout = new VBox(10, line1);
        else {
            Button[] btns = new Button[this.buttons.size()];
            HBox line2 = new HBox(50, this.buttons.toArray(btns));  // creating a horizontal box to display the buttons
            line2.setAlignment(Pos.CENTER);  // making sure the buttons are centered in the horizontal box
            layout = new VBox(10, line1, line2);
        }
        layout.setAlignment(Pos.CENTER);  // making sure the lines in the vertical box are centered (centering everything vertically on the page)

        return new Scene(layout);  // setting the scene to show the layout just created
    }

    /**
     * Helper function to setup a scene in this window
     *
     * @param window the stage where the scene will be set
     * @param scene the scene to be set for this window
     */
    protected void setScene(Stage window, Scene scene) throws InvalidParameterException {
        if(window == null) throw new InvalidParameterException("window cannot be null");
        if(scene == null) throw new InvalidParameterException("scene cannot be null");

        window.setScene(scene);  // setting the window to show the scene
        window.setResizable(false);  // stopping the user from resizing the window
    }

    /**
     * Helper function to create label to hold the message to be shown on the window
     *
     * @param message the message to be displayed
     *
     * @return the created label
     */
    protected Label createLabel(String message){
        if(message == null) message = Main.getString("defaultMessage");

        this.message.set(message);  // setting the SimpleStringProperty to the message

        Label labelMessage = new Label(message);  // creating a label that contains the message
        labelMessage.setAlignment(Pos.CENTER);  // making sure the label is center aligned
        labelMessage.setTextAlignment(TextAlignment.CENTER);  // making sure the text is center aligned in the label
        labelMessage.setMaxWidth(Main.getInt("windowMaxWidth"));  // setting maximum size for the label
        labelMessage.setWrapText(true);  // making sure that any text longer than the maximum size gets wrapped to new line

        return labelMessage;
    }

    /**
     * Helper function to create a button
     *
     * @param buttonText the text ot be displayed on the button
     * @param buttonID the id of the button
     * @param positiveBtn whether or not clicking the button will set the return value to true
     *
     * @return the created button
     */
    public Button createButton(String buttonText, String buttonID, boolean positiveBtn){
        if(buttonText == null || "".equals(buttonText)) buttonText = Main.getString("defaultBtn");
        if(buttonID == null || "".equals(buttonID)) buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(buttons.size()));

        Button btn = new Button(buttonText);
        btn.setId(buttonID);

        // Allowing the user to press enter to select a button if focused on it
        MainController.setBtnFireOnEnter(btn);

        // closing the window when the user clicks the button
        btn.setOnAction(e -> setButtonAction(btn, positiveBtn));

        // adding button to array list
        buttons.add(btn);

        return btn;
    }

    /**
     * Helper function to find a button on the window
     *
     * @param btnID the id of the button in the window
     *
     * @return the button that corresponds to that button id
     */
    public Button findBtn(String btnID) throws InvalidParameterException {
        if(btnID == null || "".equals(btnID)) throw new InvalidParameterException("btnID cannot be null");

        for (Button btn: buttons) {
            if(btn.getId().equals(btnID))
                return btn;
        }

        return null;
    }

    /**
     * Helper function to set the action of the button
     *
     * @param btn the button to set the action of
     * @param positiveBtn whether clicking the button is
     */
    protected void setButtonAction(Button btn, boolean positiveBtn) throws InvalidParameterException {
        if(btn == null) throw new InvalidParameterException("button cannot be null");

        if(positiveBtn) setRet(true);
        else setRet(false);

        logr.info(btn.getText() + " was pressed");
        window.close();
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/

    /**
     * Getter function to get the return value
     *
     * @return the return value of the confirmation window
     *          (if the user clicked the positive button or not)
     */
    public boolean getRet() { return ret.get(); }

    /**
     * Getter function to get stage
     *
     * @return the stage
     */
    public Stage getStage() { return window; }

    /**
     * Getter function to get the title of this window
     *
     * @return the title of this window
     */
    public String getTitle() { return title.get(); }

    /**
     * Getter function to get the message of this window
     *
     * @return the message of this window
     */
    public String getMessage() { return message.get(); }

    /**
     * Getter function to get the buttons in this window
     *
     * @return the list of buttons in this window
     */
    public List<Button> getButtons() { return buttons; }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/

    /**
     * Setter function to set the return value
     *
     * @param ret the new return value
     */
    public void setRet(boolean ret) { this.ret.set(ret); }

    /**
     * Setter function to set the stage
     *
     * @param stage the new stage
     */
    public void setStage(Stage stage) throws InvalidParameterException {
        if(stage == null) throw new InvalidParameterException("stage cannot be null");

        window = stage;
    }

    /**
     * Setter function to set the title of this window
     *
     * @param title the title to be set
     */
    public void setTitle(String title) {
        if(title == null || "".equals(title)) this.title.set(Main.getString("defaultWindowTitle"));
        else this.title.set(title);
    }

    /**
     * Setter function to set the message of this window
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        if(message == null || "".equals(message)) this.message.set(Main.getString("defaultMessage"));
        else this.message.set(message);
    }

    /**
     * Setter function to add the buttons that will be shown in this window
     *
     * @param buttons the buttons to be shown in this window
     */
    protected void setButtons(Button... buttons) {
        if(buttons != null)
            for (Button button : buttons){
                if(button != null && this.buttons.indexOf(button) == -1) this.buttons.add(button);  // adding the buttons to the list
            }
    }
}

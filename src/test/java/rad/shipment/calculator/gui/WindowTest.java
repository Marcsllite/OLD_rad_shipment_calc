package rad.shipment.calculator.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class WindowTest extends ApplicationTest {
    private static final Logger logr = Logger.getLogger(Window.class.getName()); // matches the logger in the affected class
    @Rule public final ExpectedException expectedException = ExpectedException.none();  // expected exception
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;
    //private Stage primaryStage;

    public String getTestCapturedLog(@NotNull OutputStream logCapturingStream, @NotNull StreamHandler customLogHandler) {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }

    @Before
    public void beforeEachTest() {
        /*
         * @Author Tommy Tynjä http://blog.diabol.se/?p=474
         *
         * Creating a custom log handler attached to desired logger
         * Then creating a SteamHandler attached to logger and an OutputStream.
         * Using the OutputStream to get the log contents
         */
        logCapturingStream = new ByteArrayOutputStream();
        Handler[] handlers = logr.getParent().getHandlers();
        customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
        logr.addHandler(customLogHandler);
    }

    @After
    public void afterEachTest() throws Exception {
        FxToolkit.hideStage();  // getting rid of current stage
        release(new KeyCode[]{});  // release key events (resource management)
        release(new MouseButton[]{});  // release mouse events (resource management)
    }

    @Test
    public void display_NullTitle_NullMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(null, null);

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                }
        );
    }

    @Test
    public void display_NullTitle_EmptyStringMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(null, "");

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals("", window.getMessage());
                }
        );
    }

    @Test
    public void display_NullTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(null, Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                }
        );
    }

    @Test
    public void display_EmptyStringTitle_NullMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display("", null);

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                }
        );
    }

    @Test
    public void display_EmptyStringTitle_EmptyStringMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display("", "");

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals("", window.getMessage());
                }
        );
    }

    @Test
    public void display_EmptyStringTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display("", Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                }
        );
    }

    @Test
    public void display_ProperTitle_NullMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(Main.getString("properTitle"), null);

                    Assert.assertEquals(Main.getString("properTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                }
        );
    }

    @Test
    public void display_ProperTitle_EmptyStringMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(Main.getString("properTitle"), "");

                    Assert.assertEquals(Main.getString("properTitle"), window.getTitle());
                    Assert.assertEquals("", window.getMessage());
                }
        );
    }

    @Test
    public void display_ProperTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.display(Main.getString("properTitle"), Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("properTitle"), window.getTitle());
                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                }
        );
    }

    @Test public void show_NullWindow(){
        Window window = new Window();

        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("window cannot be null");
        window.show(null);
    }

    //TODO: finish show_ProperWindow test function
    // @Test public void show_ProperWindow() {
    // }

    @Test
    public void createWindow_NullTitle() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createWindow(null);

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                }
        );
    }

    @Test
    public void createWindow_EmptyStringTitle() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createWindow("");

                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());
                }
        );
    }

    @Test
    public void createWindow_ProperTitle() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createWindow(Main.getString("properTitle"));

                    Assert.assertEquals(Main.getString("properTitle"), window.getTitle());
                }
        );
    }

    @Test
    public void createScene_NullMessage_NullButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createScene(null, (Button[])null);

                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                    Assert.assertEquals(0, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_EmptyStringMessage_NullButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createScene("", (Button[])null);

                    Assert.assertEquals("", window.getMessage());
                    Assert.assertEquals(0, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_ProperMessage_NullButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.createScene(Main.getString("properMessage"), (Button[])null);

                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                    Assert.assertEquals(0, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_NullMessage_OneButton(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button = new Button(Main.getString("properBtnText"));

                    window.createScene(null, button);

                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                    Assert.assertEquals(1, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_EmptyStringMessage_OneButton(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button = new Button(Main.getString("properBtnText"));

                    window.createScene("", button);

                    Assert.assertEquals("", window.getMessage());
                    Assert.assertEquals(1, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_ProperMessage_OneButton(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button = new Button(Main.getString("properBtnText"));

                    window.createScene(Main.getString("properMessage"), button);

                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                    Assert.assertEquals(1, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_NullMessage_ManyButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button1 = new Button(Main.getString("properBtnText"));
                    Button button2 = new Button(Main.getString("properBtnText"));
                    Button button3 = new Button(Main.getString("properBtnText"));
                    Button button4 = new Button(Main.getString("properBtnText"));

                    window.createScene(null, button1, button2);

                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                    Assert.assertEquals(2, window.getButtons().size());

                    window.createScene(null, button1, button2, button3);

                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                    Assert.assertEquals(3, window.getButtons().size());

                    window.createScene(null, button1, button2, button3, button4);

                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());
                    Assert.assertEquals(4, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_EmptyStringMessage_ManyButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button1 = new Button(Main.getString("properBtnText"));
                    Button button2 = new Button(Main.getString("properBtnText"));
                    Button button3 = new Button(Main.getString("properBtnText"));
                    Button button4 = new Button(Main.getString("properBtnText"));

                    window.createScene("", button1, button2);

                    Assert.assertEquals("", window.getMessage());
                    Assert.assertEquals(2, window.getButtons().size());

                    window.createScene("", button1, button2, button3);

                    Assert.assertEquals("", window.getMessage());
                    Assert.assertEquals(3, window.getButtons().size());

                    window.createScene("", button1, button2, button3, button4);

                    Assert.assertEquals("", window.getMessage());
                    Assert.assertEquals(4, window.getButtons().size());
                }
        );
    }

    @Test
    public void createScene_ProperMessage_ManyButtons(){
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Button button1 = new Button(Main.getString("properBtnText"));
                    Button button2 = new Button(Main.getString("properBtnText"));
                    Button button3 = new Button(Main.getString("properBtnText"));
                    Button button4 = new Button(Main.getString("properBtnText"));

                    window.createScene(Main.getString("properMessage"), button1, button2);

                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                    Assert.assertEquals(2, window.getButtons().size());

                    window.createScene(Main.getString("properMessage"), button1, button2, button3);

                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                    Assert.assertEquals(3, window.getButtons().size());

                    window.createScene(Main.getString("properMessage"), button1, button2, button3, button4);

                    Assert.assertEquals(Main.getString("properMessage"), window.getMessage());
                    Assert.assertEquals(4, window.getButtons().size());
                }
        );
    }

    @Test
    public void setScene_Null_Window_NullScene() {
        Window window = new Window();

        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("window cannot be null");
        window.setScene(null, null);
    }

    @Test
    public void setScene_Null_Window_ProperScene() {
        Window window = new Window();
        Scene properScene = new Scene(new Label("This is a proper scene"));

        expectedException.expect(InvalidParameterException.class);
        expectedException.expectMessage("window cannot be null");
        window.setScene(null, properScene);
    }

    @Test
    public void setScene_ProperWindow_NullScene() {
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Stage properWindow = window.createWindow("This is a proper window");

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("scene cannot be null");
                    window.setScene(properWindow, null);
                }
        );
    }

    @Test
    public void setScene_ProperWindow_ProperScene() {
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Stage properWindow = window.createWindow("This is a proper window");
                    Scene properScene = new Scene(new Label("This is a proper scene"));

                    window.setScene(properWindow, properScene);

                    Assert.assertEquals(properScene, properWindow.getScene());
                    Assert.assertFalse(properWindow.isResizable());
                }
        );
    }

    @Test
    public void createLabel_NullMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Label label = window.createLabel(null);

                    Assert.assertEquals(Main.getString("defaultMessage"), label.getText());
                }
        );
    }

    @Test
    public void createLabel_EmptyStringMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Label label = window.createLabel("");

                    Assert.assertEquals("", label.getText());
                }
        );
    }

    @Test
    public void createLabel_ProperMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Label label = window.createLabel(Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("properMessage"), label.getText());
                }
        );
    }

    @Test
    public void createButton_NullText_NullID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(null,null, true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_NullText_NullID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(null,null,  false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_NullID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton("", null,true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_NullID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton("", null, false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_NullID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(Main.getString("properBtnText"), null, true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_NullID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(Main.getString("properBtnText"), null,false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_NullText_EmptyStringID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(null, "", true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_NullText_EmptyStringID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(null, "", false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_EmptyStringID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton("", "", true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_EmptyStringID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton("", "", false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_EmptyStringID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(Main.getString("properBtnText"), "", true);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_EmptyStringID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.createButton(Main.getString("properBtnText"), "", false);
                    String buttonID = Main.replaceBundleString("defaultBtnID", String.valueOf(window.getButtons().size()));

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(buttonID, button.getId());
                }
        );
    }

    @Test
    public void createButton_NullText_ProperID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton(null, properID, true);

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void createButton_NullText_ProperID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton(null, properID,false);

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_ProperID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton("", properID, true);

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void createButton_EmptyStringText_ProperID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton("", properID,  false);

                    Assert.assertEquals(Main.getString("defaultBtn"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_ProperID_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton(Main.getString("properBtnText"), properID,  true);

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void createButton_ProperText_ProperID_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    String properID = Main.getString("properBtnID");
                    Button button = window.createButton(Main.getString("properBtnText"),properID, false);

                    Assert.assertEquals(Main.getString("properBtnText"), button.getText());
                    Assert.assertEquals(properID, button.getId());
                }
        );
    }

    @Test
    public void findBtn_NullID(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("btnID cannot be null");
                    window.findBtn(null);
                }
        );
    }

    @Test
    public void findBtn_EmptyStringID(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("btnID cannot be null");
                    window.findBtn("");
                }
        );
    }

    @Test
    public void findBtn_ProperID(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button positiveBtn = window.createButton(Main.getString("properBtnText"), Main.getString("properBtnID"), true);

                    Button button = window.findBtn(Main.getString("properBtnID"));

                    Assert.assertEquals(positiveBtn, button);
                }
        );
    }

    @Test
    public void findBtn_ProperID_InvalidBtnID(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    Button button = window.findBtn(Main.getString("defaultBtn"));

                    Assert.assertNull(button);
                }
        );
    }

    @Test
    public void setButtonAction_NullBtn_TruePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("button cannot be null");
                    window.setButtonAction(null, true);
                }
        );
    }

    @Test
    public void setButtonAction_NullBtn_FalsePosBtn(){
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("button cannot be null");
                    window.setButtonAction(null, false);
                }
        );
    }

    @Test
    public void setRetAndGetRet() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    window.setRet(true);
                    Assert.assertTrue(window.getRet());

                    window.setRet(false);
                    Assert.assertFalse(window.getRet());
                }
        );
    }

    @Test
    public void setStage_NullStage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();

                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("stage cannot be null");
                    window.setStage(null);
                }
        );
    }

    @Test
    public void setStageAndGetStage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    Stage stage = new Stage();

                    window.setStage(stage);
                    Assert.assertEquals(stage, window.getStage());
                }
        );
    }

    @Test
    public void setTitleAndGetTitle() {
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    String properTitle = Main.getString("properTitle");

                    window.setTitle(null);
                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());

                    window.setTitle("");
                    Assert.assertEquals(Main.getString("defaultWindowTitle"), window.getTitle());

                    window.setTitle(properTitle);
                    Assert.assertEquals(properTitle, window.getTitle());
                }
        );
    }

    @Test
    public void setMessageAndGetMessage() {
        Platform.runLater(
                () -> {
                    Window window = new Window();
                    String properMessage = Main.getString("properMessage");

                    window.setMessage(null);
                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());

                    window.setMessage("");
                    Assert.assertEquals(Main.getString("defaultMessage"), window.getMessage());

                    window.setMessage(properMessage);
                    Assert.assertEquals(properMessage, window.getMessage());
                }
        );
    }
}
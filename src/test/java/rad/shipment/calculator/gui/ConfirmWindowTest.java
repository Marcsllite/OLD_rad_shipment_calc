package rad.shipment.calculator.gui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class ConfirmWindowTest extends ApplicationTest {
    private static final Logger logr = Logger.getLogger(ConfirmWindow.class.getName()); // matches the logger in the affected class
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();  // expected exception
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
    public void display_NullTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    ConfirmWindow confirmWindow = new ConfirmWindow();

                    confirmWindow.display(null, Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultConfirmTitle"), confirmWindow.getTitle());
                    Assert.assertEquals(2, confirmWindow.getButtons().size());
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("positiveBtnID")));
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("negativeBtnID")));
                }
        );
    }

    @Test
    public void display_EmptyStringTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    ConfirmWindow confirmWindow = new ConfirmWindow();

                    confirmWindow.display("", Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultConfirmTitle"), confirmWindow.getTitle());
                    Assert.assertEquals(2, confirmWindow.getButtons().size());
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("positiveBtnID")));
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("negativeBtnID")));
                }
        );
    }

    @Test
    public void display_ProperTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    ConfirmWindow confirmWindow = new ConfirmWindow();

                    confirmWindow.display(Main.getString("properTitle"), Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("properTitle"), confirmWindow.getTitle());
                    Assert.assertEquals(2, confirmWindow.getButtons().size());
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("positiveBtnID")));
                    Assert.assertNotNull(confirmWindow.findBtn(Main.getString("negativeBtnID")));
                }
        );
    }
}
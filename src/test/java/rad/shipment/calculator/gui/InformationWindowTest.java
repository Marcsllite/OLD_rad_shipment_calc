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

public class InformationWindowTest extends ApplicationTest {
    private static final Logger logr = Logger.getLogger(InformationWindow.class.getName()); // matches the logger in the affected class
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
    public void display_NullTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    InformationWindow informationWindow = new InformationWindow();

                    informationWindow.display(null, Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultInformationTitle"), informationWindow.getTitle());
                    Assert.assertEquals(1, informationWindow.getButtons().size());
                    Assert.assertNotNull(informationWindow.findBtn(Main.getString("okayBtnID")));
                }
        );
    }

    @Test
    public void display_EmptyStringTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    InformationWindow informationWindow = new InformationWindow();

                    informationWindow.display("", Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("defaultInformationTitle"), informationWindow.getTitle());
                    Assert.assertEquals(1, informationWindow.getButtons().size());
                    Assert.assertNotNull(informationWindow.findBtn(Main.getString("okayBtnID")));
                }
        );
    }

    @Test
    public void display_ProperTitle_ProperMessage() {
        Platform.runLater(
                () -> {
                    InformationWindow informationWindow = new InformationWindow();

                    informationWindow.display(Main.getString("properTitle"), Main.getString("properMessage"));

                    Assert.assertEquals(Main.getString("properTitle"), informationWindow.getTitle());
                    Assert.assertEquals(1, informationWindow.getButtons().size());
                    Assert.assertNotNull(informationWindow.findBtn(Main.getString("okayBtnID")));
                }
        );
    }
}
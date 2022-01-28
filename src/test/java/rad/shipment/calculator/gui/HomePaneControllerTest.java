package rad.shipment.calculator.gui;

import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import rad.shipment.calculator.helpers.Isotope;
import rad.shipment.calculator.panes.HomePane;
import rad.shipment.calculator.panes.StartWithHomePane;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class HomePaneControllerTest extends ApplicationTest {

    private static final Logger logr = Logger.getLogger(HomePaneController.class.getName()); // matches the logger in the affected class
    @Rule public final ExpectedException expectedException = ExpectedException.none();  // expected exception
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;
    private final HomePaneController homePaneController = new HomePaneController();
    private HomePane homePane;
    private Stage primaryStage;

    public String getTestCapturedLog(@NotNull OutputStream logCapturingStream, @NotNull StreamHandler customLogHandler) {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }

    /**
     * @author MVP Java https://www.youtube.com/channel/UCrgOYeQyZ_V62XDYKCfh8TQ
     * Helper method to retrieve Java FX GUI components. */
    public <T extends Node> T find(final String query) { return (T) lookup(query).queryAll().iterator().next(); }

    public Stage getPrimaryStage() { return primaryStage; }

    @Override
    public void start (@NotNull Stage stage) {
        // loading and showing the main parent node
        primaryStage = stage;
        stage.show();
    }

    /**
     * Override from parent in order to load the Home Pane only
     * We only want to test the Home Pane (not the entire app)
     */
    @Before
    public void beforeEachTest() throws Exception {
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

        FxToolkit.setupApplication(StartWithHomePane.class);
        homePane = new HomePane(this);
    }

    @After
    public void afterEachTest() throws Exception {
        FxToolkit.hideStage();  // getting rid of current stage
        release(new KeyCode[]{});  // release key events (resource management)
        release(new MouseButton[]{});  // release mouse events (resource management)
    }

    @Test
    public void initialize(){
        // making sure buttons have an OnKeyPressed event handler
        Assert.assertNotNull(homePane.getBtnAdd().getOnKeyPressed());
        Assert.assertNotNull(homePane.getBtnEdit().getOnKeyPressed());
        Assert.assertNotNull(homePane.getBtnRemove().getOnKeyPressed());
        Assert.assertNotNull(homePane.getBtnCalculate().getOnKeyPressed());

        //making sure buttons have an OnMouseEntered event handler
        Assert.assertNotNull(homePane.getBtnAdd().getOnMouseEntered());
        Assert.assertNotNull(homePane.getBtnEdit().getOnMouseEntered());
        Assert.assertNotNull(homePane.getBtnRemove().getOnMouseEntered());
        Assert.assertNotNull(homePane.getBtnCalculate().getOnMouseEntered());

        //making sure buttons have an OnMouseExited event handler
        Assert.assertNotNull(homePane.getBtnAdd().getOnMouseExited());
        Assert.assertNotNull(homePane.getBtnEdit().getOnMouseExited());
        Assert.assertNotNull(homePane.getBtnRemove().getOnMouseExited());
        Assert.assertNotNull(homePane.getBtnCalculate().getOnMouseExited());

        // making sure edit button's disable property is bound
        Assert.assertTrue(homePane.getBtnEdit().disableProperty().isBound());

        // making sure remove button's disable property is bound
        Assert.assertTrue(homePane.getBtnRemove().disableProperty().isBound());

        // making sure calculate button's disable property is bound
        Assert.assertTrue(homePane.getBtnCalculate().disableProperty().isBound());

        // making sure initTable() worked properly
        TableView<Isotope> tableView =  homePane.getTableView();

        // Making sure the selection model is in multiple mode
        Assert.assertEquals(SelectionMode.MULTIPLE,tableView.getSelectionModel().getSelectionMode());

        // Making sure that the tableView has a OnKeyPressed event handler
        Assert.assertNotNull(tableView.getOnKeyPressed());
    }

    @Test
    public void injectMainController() {
        MainController testMainController = new MainController();

        homePaneController.injectMainController(testMainController);

        Assert.assertEquals(testMainController, homePaneController.getMainController());
    }

    @Test
    public void homePaneHandler_NullActionEvent(){
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("action event cannot be null");
        homePaneController.homePaneHandler(null);
    }

    // TODO: finish create add btn handler test case

    @Test
    public void editBtnHandler(){
        Assert.assertTrue(homePane.getBtnEdit().isDisabled());
    }

    @Test
    public void removeBtnHandler(){
        Assert.assertTrue(homePane.getBtnRemove().isDisabled());
    }

    @Test
    public void calculateBtnHandler(){
        Assert.assertTrue(homePane.getBtnCalculate().isDisabled());
    }

    @Test
    public void setMainController_Null_Controller(){
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("mainController cannot be null");
        homePaneController.setMainController(null);
    }

    @Test
    public void setAndGetMainController_Proper_Controller(){
        MainController mainController = new MainController();

        Assert.assertNull(homePaneController.getMainController());

        homePaneController.setMainController(mainController);

        Assert.assertEquals(mainController, homePaneController.getMainController());
    }
}
package rad.shipment.calculator.helpers;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import rad.shipment.calculator.gui.Main;
import rad.shipment.calculator.panes.StartWithHomePane;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static rad.shipment.calculator.gui.Main.replaceBundleString;

public class DatabaseEditorTest extends ApplicationTest {

    private static final Logger logr = Logger.getLogger(CommandExecutor.class.getName()); // matches the logger in the affected class
    @Rule public final ExpectedException expectedException = ExpectedException.none();  // expected exception
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;
    DatabaseEditor databaseEditor = Main.getDBEditor();

    public String getTestCapturedLog(OutputStream logCapturingStream, StreamHandler customLogHandler) {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }

    @Override public void start(Stage primaryStage) {
        // checking if successfully connected to the database
        if(databaseEditor.startConnection()) {
            try { databaseEditor.setupEmbeddedDB(); }
            catch (RuntimeException e) { Assert.fail("Failed to setup database"); }
        } else Assert.fail("Failed to start database");
    }

    @Before
    public void beforeEachTest() throws TimeoutException {
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
    }

    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();  // getting rid of current stage
    }

    @Test
    public void DatabaseEditor_NullDriver(){
        Platform.runLater(
            () -> {
                expectedException.expect(RuntimeException.class);
                expectedException.expectMessage("Database driver cannot be null or empty string");
                new DatabaseEditor(null,
                        Main.getString("h2DB_Path"),
                        Main.getString("h2DB_Settings"),
                        Main.getString("DB_User"),
                        Main.getString("DB_Pass"));
            }
        );
    }

    @Test
    public void DatabaseEditor_EmptyStringDriver(){
        Platform.runLater(
                () -> {
                    expectedException.expect(RuntimeException.class);
                    expectedException.expectMessage("Database driver cannot be null or empty string");
                    new DatabaseEditor("",
                            Main.getString("h2DB_Path"),
                            Main.getString("h2DB_Settings"),
                            Main.getString("DB_User"),
                            Main.getString("DB_Pass"));
                }
        );
    }

    @Test
    public void DatabaseEditor_EmptyStringPath(){
        Platform.runLater(
                () -> {
                    DatabaseEditor databaseEditor2 = new DatabaseEditor(Main.getString("h2DB_Driver"),
                            "",
                            Main.getString("h2DB_Settings"),
                            Main.getString("DB_User"),
                            Main.getString("DB_Pass"));

                    Assert.assertNotEquals("", databaseEditor2.getDateTime());

                    String expected =  replaceBundleString("defaultDBPath" +  databaseEditor2.getDateTime());
                    Assert.assertEquals(expected, databaseEditor2.getPath());
                }
        );
    }

    @Test
    public void DatabaseEditor_NullPath(){
        Platform.runLater(
                () -> {
                    DatabaseEditor databaseEditor2 = new DatabaseEditor(Main.getString("h2DB_Driver"),
                            null,
                            Main.getString("h2DB_Settings"),
                            Main.getString("DB_User"),
                            Main.getString("DB_Pass"));

                    Assert.assertNotEquals("", databaseEditor2.getDateTime());

                    String expected =  replaceBundleString("defaultDBPath" +  databaseEditor2.getDateTime());
                    Assert.assertEquals(expected, databaseEditor2.getPath());
                }
        );
    }

    @Test
    public void DatabaseEditor_NullSettings(){
        Platform.runLater(
                () -> {
                    DatabaseEditor databaseEditor2 = new DatabaseEditor(Main.getString("h2DB_Driver"),
                            Main.getString("h2DB_Path"),
                            null,
                            Main.getString("DB_User"),
                            Main.getString("DB_Pass"));

                    Assert.assertEquals("", databaseEditor2.getSettings());
                }
        );
    }

    @Test
    public void startConnection_FakeDriver() {
        Platform.runLater(
            () -> {
                DatabaseEditor databaseEditor = new DatabaseEditor("fakeDriver",
                        Main.getString("h2DB_Path"),
                        Main.getString("h2DB_Settings"),
                        Main.getString("DB_User"),
                        Main.getString("DB_Pass"));

                boolean ret = databaseEditor.startConnection();

                Assert.assertFalse(ret);
            }
        );
    }

    @Test
    public void startConnection_NoSettings() {
        DatabaseEditor databaseEditor = new DatabaseEditor(Main.getString("h2DB_Driver"),
                Main.getString("testDB_Path"),
                null, null, null);

        boolean ret = databaseEditor.startConnection();

        Assert.assertTrue(ret);

        databaseEditor.closeConnection();
    }

    @Test
    public void startConnection_NoUserPass() {
        DatabaseEditor databaseEditor = new DatabaseEditor(Main.getString("h2DB_Driver"),
                            Main.getString("testDB_Path"),
                            Main.getString("h2DB_Settings"), null, null);

        boolean ret = databaseEditor.startConnection();

        Assert.assertTrue(ret);

        databaseEditor.closeConnection();
                
    }

    @Test
    public void getResultSetSize_NullResultSet() {
        Assert.assertEquals(0, databaseEditor.getResultSetSize(null));
    }

    @Test
    public void getResultSetSize_ImproperStatementSetup() {
        try {
            Statement db = DatabaseEditor.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
            ResultSet rs = db.executeQuery("select * from Isotopes");

            int result = databaseEditor.getResultSetSize(rs);

            Assert.assertEquals(386, result);
//          Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Failed to reset the result set."
//                )
//        );

        } catch (SQLException e) { Assert.fail("Failed to create Statement"); }
    }

    @Test
    public void getResultSetSize_ProperResultSet() {
        try {
            Statement db = DatabaseEditor.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
            ResultSet rs = db.executeQuery("select * from Isotopes");

            int result = databaseEditor.getResultSetSize(rs);

            Assert.assertEquals(386, result);
        } catch (SQLException e) { Assert.fail("Failed to create Statement"); }
    }

    @Test
    public void getAbbr_NullParam(){
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Isotope name cannot be null or empty string");
        databaseEditor.getAbbr(null);
    }

    @Test
    public void getAbbr_NullConnection(){
        DatabaseEditor.con = null;
        String retVal = databaseEditor.getAbbr("invalidName");

        Assert.assertEquals("", retVal);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getAbbr_EmptyStringParam(){
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Isotope name cannot be null or empty string");
        databaseEditor.getAbbr("");
    }

    @Test
    public void getAbbr_InvalidName(){
        String retVal = databaseEditor.getAbbr("invalidName");

        Assert.assertEquals("", retVal);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Failed to query the database for invalidName."
//                )
//        );
    }

    @Test
    public void getAbbr_ValidNameAbbr(){
        String retVal = databaseEditor.getAbbr("Au-198");

        Assert.assertEquals("Au-198", retVal);
    }

    @Test
    public void getAbbr_ValidName(){
        String retVal = databaseEditor.getAbbr("Gold-198");

        Assert.assertEquals("Au-198", retVal);
    }

    @Test
    public void getA1_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA1(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getA1_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA1(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getA1_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA1("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }
    
    @Test
    public void getA1_ValidName() {
        float expected = (float) 1.00e+00;
        float retVal = databaseEditor.getA1("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getA2_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA2(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getA2_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA2(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getA2_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getA2("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getA2_ValidName() {
        float expected = (float) 6.00e-01;
        float retVal = databaseEditor.getA2("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getDecayConstant_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getDecayConstant(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getDecayConstant_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getDecayConstant(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getDecayConstant_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getDecayConstant("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getDecayConstant_ValidName() {
        float expected = (float) 0.256721178;
        float retVal = databaseEditor.getDecayConstant("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getExemptConcentration_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptConcentration(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getExemptConcentration_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptConcentration(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
                
    }

    @Test
    public void getExemptConcentration_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptConcentration("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
                
    }

    @Test
    public void getExemptConcentration_ValidName() {
        float expected = (float) 1.00e+02;
        float retVal = databaseEditor.getExemptConcentration("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getExemptLimit_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptLimit(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getExemptLimit_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptLimit(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getExemptLimit_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getExemptLimit("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getExemptLimit_ValidName() {
        float expected = (float) 1.00e+06;
        float retVal = databaseEditor.getExemptLimit("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getHalfLife_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getHalfLife(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getHalfLife_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getHalfLife(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getHalfLife_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getHalfLife("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getHalfLife_ValidName() {
        float expected = (float) 2.7;
        float retVal = databaseEditor.getHalfLife("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }
    
    @Test
    public void getIALimitedMultiplier_NullState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier(null, null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_EmptyStringState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier("", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_InvalidState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier("invalidState", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_InvalidState_EmptyStringForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier("invalidState", "");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_InvalidState_InvalidForm() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIALimitedMultiplier("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIALimitedMultiplier_ValidState_ValidForm() {
        float expected = (float)1.00e-02;
        float retVal = databaseEditor.getIALimitedMultiplier("solid", "normal");

        Assert.assertEquals(expected, retVal, 0);
    }
    
    @Test
    public void getIAPackageLimit_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getIAPackageLimit_NullState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit(null, null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIAPackageLimit_EmptyStringState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit("", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIAPackageLimit_InvalidState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit("invalidState", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIAPackageLimit_InvalidState_EmptyStringForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit("invalidState", "");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIAPackageLimit_InvalidState_InvalidForm() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getIAPackageLimit("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getIAPackageLimit_ValidState_ValidForm() {
        float expected = (float)1.00e+00;
        float retVal = databaseEditor.getIAPackageLimit("solid", "normal");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLicenseLimit_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLicenseLimit(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getLicenseLimit_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLicenseLimit(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
                
    }

    @Test
    public void getLicenseLimit_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLicenseLimit("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
                
    }

    @Test
    public void getLicenseLimit_ValidName() {
        float expected = (float) 100;
        float retVal = databaseEditor.getLicenseLimit("Au-198");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getLimitedLimit_NullState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit(null, null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_EmptyStringState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("state cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit("", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_InvalidState_NullForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit("invalidState", null);

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_InvalidState_EmptyStringForm() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("form cannot be null or empty string");
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit("invalidState", "");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_InvalidState_InvalidForm() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getLimitedLimit("invalidState", "invalidForm");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getLimitedLimit_ValidState_ValidForm() {
        float expected = (float)1.00e-03;
        float retVal = databaseEditor.getLimitedLimit("solid", "normal");

        Assert.assertEquals(expected, retVal, 0);
    }

    @Test
    public void getReportableQuantity_NullName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getReportableQuantity(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
    }

    @Test
    public void getReportableQuantity_NullConnection() {
        DatabaseEditor.con = null;
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getReportableQuantity(null);

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "Cannot connect to database, connection is null"
//                )
//        );
    }

    @Test
    public void getReportableQuantity_EmptyStringName() {
        float expected = (float)Main.getInt("defaultInt");
        float retVal = databaseEditor.getReportableQuantity("");

        Assert.assertEquals(expected, retVal, 0);
//        Assert.assertTrue(
//                getTestCapturedLog(logCapturingStream, customLogHandler).contains(
//                        "name is invalid"
//                )
//        );
                
    }

    @Test
    public void getReportableQuantity_ValidName() {
        float expected = (float) 3.7;
        float retVal = databaseEditor.getReportableQuantity("Au-198");

        Assert.assertEquals(expected, retVal, 0);
                
    }

    @Test
    public void getDriver() {
        Assert.assertEquals(Main.getString("h2DB_Driver"), databaseEditor.getDriver());
    }

    @Test
    public void getPath() {
        Assert.assertEquals(Main.getString("h2DB_Path"), databaseEditor.getPath());
    }

    @Test
    public void getSettings() {
        Assert.assertEquals(Main.getString("h2DB_Settings"), databaseEditor.getSettings());
    }

    @Test
    public void getUser() {
        Assert.assertEquals(Main.getString("DB_User"), databaseEditor.getUser());
    }

    @Test
    public void getPass() {
        Assert.assertEquals(Main.getString("DB_Pass"), databaseEditor.getPass());
    }
}
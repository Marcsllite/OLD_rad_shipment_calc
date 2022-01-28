package rad.shipment.calculator.helpers;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rad.shipment.calculator.gui.Main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class CommandExecutorTest {

    private static final Logger logr = Logger.getLogger(CommandExecutor.class.getName()); // matches the logger in the affected class
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;

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

    @Test
    public void runCommands_NullCommand() {
        CommandExecutor commandExecutor = new CommandExecutor(null, (String[])null);

        int ret = commandExecutor.runCommands();

        Assert.assertEquals(Main.getInt("defaultInt"), ret);
                Assert.assertTrue(
                getTestCapturedLog(logCapturingStream, customLogHandler).contains("There are no commands to be run")
        );
    }

    @Test
    public void runCommands_EmptyStringCommand() {
        CommandExecutor commandExecutor = new CommandExecutor(null, "");

        int ret = commandExecutor.runCommands();

        Assert.assertEquals(Main.getInt("defaultInt"), ret);
        Assert.assertTrue(
                getTestCapturedLog(logCapturingStream, customLogHandler).contains("There are no commands to be run")
        );
    }

    @Test
    public void runCommands_ProperCommand() {
        String[] commands = new String[3];
        String currentOS = Main.findCurrentOS();

        if(currentOS.equals(Main.getString("windows"))) {
            commands[0] = "cmd.exe";
            commands[1] = "/c";
            commands[2] = "dir";
        } else if(currentOS.equals(Main.getString("mac")) ||
                currentOS.equals(Main.getString("unix")) ||
                currentOS.equals(Main.getString("solaris"))) {
            commands[0] = "bash";
            commands[1] = "-c";
            commands[2] = "ls";
        } else Assert.fail("Operating system is not supported");

        CommandExecutor commandExecutor = new CommandExecutor(null, commands);

        int ret = commandExecutor.runCommands();

        Assert.assertEquals(0, ret);
        Assert.assertNotNull(commandExecutor.getOutput());
    }

    @Test
    public void runCommands_ImproperCommand() {
        CommandExecutor commandExecutor = new CommandExecutor(null, "improper command");

        int ret = commandExecutor.runCommands();

        Assert.assertNotEquals(0, ret);
        Assert.assertEquals("", commandExecutor.getOutput());
        Assert.assertTrue(
                getTestCapturedLog(logCapturingStream, customLogHandler).contains("Failed to run command. Error: ")
        );
    }

    @Test
    public void allEmptyCommands_NullCommand(){
        CommandExecutor commandExecutor = new CommandExecutor();

        Assert.assertTrue(commandExecutor.allEmptyCommands((String[])null));
    }

    @Test
    public void allEmptyCommands_EmptyStringCommand(){
        CommandExecutor commandExecutor = new CommandExecutor();

        Assert.assertTrue(commandExecutor.allEmptyCommands(""));
        Assert.assertTrue(commandExecutor.allEmptyCommands("", ""));
        Assert.assertTrue(commandExecutor.allEmptyCommands("", "", ""));
    }
    @Test
    public void allEmptyCommands_ProperCommand(){
        CommandExecutor commandExecutor = new CommandExecutor();

        Assert.assertFalse(commandExecutor.allEmptyCommands("properCommand"));
    }


    @Test
    public void getAndSetExecDir_NullPath() {
        CommandExecutor commandExecutor = new CommandExecutor(null);

        Assert.assertEquals(System.getProperty("user.dir"), commandExecutor.getExecDir());
    }

    @Test
    public void getAndSetExecDir_EmptyStringPath() {
        CommandExecutor commandExecutor = new CommandExecutor("");

        Assert.assertEquals(System.getProperty("user.dir"), commandExecutor.getExecDir());
    }

    @Test
    public void getAndSetExecDir_ProperPath() {
        String properPath = System.getProperty("user.home");
        CommandExecutor commandExecutor = new CommandExecutor(properPath);

        Assert.assertEquals(properPath, commandExecutor.getExecDir());
    }

    @Test
    public void getAndSetExecDir_FilePath() {
        String filePath = "images/color_home.png";
        CommandExecutor commandExecutor = new CommandExecutor(filePath);

        Assert.assertEquals(System.getProperty("user.dir"), commandExecutor.getExecDir());
    }

    @Test
    public void getAndSetExecDir_FakePath() {
        String fakePath = System.getProperty("user.home") + File.separator + "fakeDir";
        CommandExecutor commandExecutor = new CommandExecutor(fakePath);

        Assert.assertEquals(System.getProperty("user.dir"), commandExecutor.getExecDir());
    }

    @Test
    public void getAndSetProcessBuilder_NullParam() {
        CommandExecutor commandExecutor = new CommandExecutor();

        commandExecutor.setProcessBuilder(null);

        Assert.assertNotNull(commandExecutor.getProcessBuilder());
    }

    @Test
    public void getAndSetProcessBuilder_ProperParam() {
        CommandExecutor commandExecutor = new CommandExecutor();
        ProcessBuilder processBuilder = new ProcessBuilder();

        commandExecutor.setProcessBuilder(processBuilder);

        Assert.assertEquals(processBuilder, commandExecutor.getProcessBuilder());
    }

    @Test
    public void getAndSetCommands_NullCommand() {
        CommandExecutor commandExecutor = new CommandExecutor(null, (String[])null);

        Assert.assertEquals(0, commandExecutor.getCommands().size());
    }

    @Test
    public void getAndSetCommands_EmptyStringCommand() {
        CommandExecutor commandExecutor = new CommandExecutor(null, (String[])null);

        Assert.assertEquals(0, commandExecutor.getCommands().size());
    }

    @Test
    public void getAndSetCommands_ProperCommand() {
        String[] properCommands = new String[] {"This", "is", "a", "proper", "command"};

        CommandExecutor commandExecutor = new CommandExecutor(null, properCommands);

        Assert.assertEquals(5, commandExecutor.getCommands().size());
        Assert.assertEquals(properCommands[0], commandExecutor.getCommands().get(0));
        Assert.assertEquals(properCommands[1], commandExecutor.getCommands().get(1));
        Assert.assertEquals(properCommands[2], commandExecutor.getCommands().get(2));
        Assert.assertEquals(properCommands[3], commandExecutor.getCommands().get(3));
        Assert.assertEquals(properCommands[4], commandExecutor.getCommands().get(4));
    }
}
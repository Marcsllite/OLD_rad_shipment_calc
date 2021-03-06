package rad.shipment.calculator.helpers;

import rad.shipment.calculator.gui.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CommandExecutor {

    // Declaring variables
    private static final Logger logr = Logger.getLogger(CommandExecutor.class.getName());  // getting logger
    private String execDir;
    private ProcessBuilder processBuilder;
    private final List<String> commands;
    private final StringBuilder commandOutput;

    /*////////////////////////////////////////////// COMMAND EXECUTOR ////////////////////////////////////////////////*/

    /**
     * Constructor for CommandExecutor class
     * Sets the execution directory as the current directory
     */
    public CommandExecutor() {
        commands = new ArrayList<>();
        commandOutput = new StringBuilder();
        setProcessBuilder(new ProcessBuilder());
        setExecDir(System.getProperty("user.dir"));
    }

    /**
     * Constructor for CommandExecutor class
     *
     * @param execDir the path of the directory to run the commands in
     */
    public CommandExecutor(String execDir) {
        commands = new ArrayList<>();
        commandOutput = new StringBuilder();
        setProcessBuilder(new ProcessBuilder());
        setExecDir(execDir);
    }

    /**
     * Constructor for CommandExecutor class
     *
     * @param execDir the path of the directory to run the commands in
     * @param commands the list of commands to run
     */
    public CommandExecutor(String execDir, String... commands) {
        if(commands == null || allEmptyCommands(commands)){
            this.commands = new ArrayList<>();
            this.processBuilder = new ProcessBuilder();
        } else {
            this.commands = new ArrayList<>(commands.length);
            setProcessBuilder(new ProcessBuilder(this.commands));  // setting process builder with commands
            setCommands(commands);
        }

        commandOutput = new StringBuilder();
        setExecDir(execDir);
    }

    /**
     * @author Mkyong.com https://www.mkyong.com/java/java-processbuilder-examples/
     * Function to run the commands in the given execution directory
     *
     * @return the exit code given after running the functions
     */
    public int runCommands(){
        int exitCode = Main.getInt("defaultInt");
        if(!commands.isEmpty()){
            try {
                Process process = processBuilder.start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    commandOutput.append(line);
                }

                exitCode = process.waitFor();  // getting exit code from execution
            } catch (IOException | InterruptedException e) {
                logr.severe("Failed to run command. Error: " + e.getMessage());  // logging errors
            }
        } else { logr.warning("There are no commands to be run"); }  // logging errors
        return exitCode;
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     * Helper function to figure out if the elements in the commands are all empty strings
     *
     * @param commands the commands to be checked
     * @return true if all the elements in the commands array are empty strings
     *          false otherwise
     */
    protected boolean allEmptyCommands(String... commands) {
        if(commands == null) return true;

        for (String command: commands) {
            if(!"".equals(command)) return false;
        }
        return true;
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/

    /**
     * Getter function to get the path of the execution directory where
     * the commands will be run
     *
     * @return the path of the execution directory where the commands will be run
     */
    public String getExecDir() { return execDir; }

    /**
     * Getter function to get the process builder
     *
     * @return the process builder
     */
    public ProcessBuilder getProcessBuilder() { return processBuilder; }

    /**
     * Getter function to get the commands to be run
     *
     * @return the commands to be run
     */
    public List<String> getCommands() { return commands; }

    /**
     * Getter function to get the output of the command(s) that were run
     *
     * @return the output of the command(s) that were run
     *          or an empty string if there is no output
     */
    public String getOutput() {
        if(commandOutput.length() == 0) return "";
        else return commandOutput.toString();
    }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/

    /**
     * Setter function to set the path of the directory to run the commands in
     *
     * @param execDir the new path to run the
     */
    public void setExecDir(String execDir) {
        // if execDir is invalid, setting the execution directory to the current working directory
        if(execDir == null || "".equals(execDir)) this.execDir = System.getProperty("user.dir");

        else {
            File execDirLoc = new File(execDir);
            if (!Files.isDirectory(execDirLoc.toPath()) || Files.notExists(execDirLoc.toPath()))
                this.execDir = System.getProperty("user.dir");
            else this.execDir = execDir;
        }

        getProcessBuilder().directory(new File(getExecDir()));  // setting the process builder directory
    }

    /**
     * Setter function to set the process builder
     *
     * @param processBuilder the new process builder
     */
    public void setProcessBuilder(ProcessBuilder processBuilder) {
        if(processBuilder == null) this.processBuilder = new ProcessBuilder();
        else this.processBuilder = processBuilder;
    }

    /**
     * Setter function to set the commands to be run
     *
     * @param commands the commands to be run
     */
    public void setCommands(String... commands) {
        if(commands != null)
            for (String comm : commands){
                if(!"".equals(comm))this.commands.add(comm);  // adding the commands to the list
            }
        getProcessBuilder().command(this.commands);
    }
}

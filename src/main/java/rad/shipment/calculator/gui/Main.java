package rad.shipment.calculator.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.apache.commons.validator.UrlValidator;
import rad.shipment.calculator.helpers.CommandExecutor;
import rad.shipment.calculator.helpers.DatabaseEditor;
import rad.shipment.calculator.view.FXMLView;
import rad.shipment.calculator.view.StageManager;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    // Declaring variables
    private static final Logger logr = Logger.getLogger(Main.class.getName());  // getting logger
    private static String dataFolder;
    private static String defaultDir;  // Documents folder where any created files are saved
    private static final ResourceBundle bundle = ResourceBundle.getBundle("bundle");  // getting resource bundle
    private final InformationWindow informationWindow = new InformationWindow();
    protected static StageManager stageManager;  // creating stage manager object to switch fxml views
    private static final DatabaseEditor H2DBEditor = new DatabaseEditor(getString("h2DB_Driver"),
                                                                getString("h2DB_Path"),
                                                                getString("h2DB_Settings"),
                                                                getString("DB_User"),
                                                                getString("DB_Pass"));
    public static final int DEFAULT_NATURE_INDEX = 0;
    public static final int DEFAULT_STATE_INDEX = 0;
    public static final int DEFAULT_FORM_INDEX = 1;
    private static final ObservableList<String> natures = FXCollections.observableArrayList("Regular", "Instrument", "Article");
    private static final ObservableList<String> states = FXCollections.observableArrayList("Solid", "Liquid", "Gas");
    private static final ObservableList<String> forms = FXCollections.observableArrayList("Special", "Normal");


    /**
     * Main function to run application
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Function to run at the start of application
     */
    @Override public void start(Stage primaryStage) {
        // checking if successfully connected to the database
        if(H2DBEditor.startConnection()) {
            try {
                H2DBEditor.setupEmbeddedDB();

                setUpDefaultDirectory(); // Setting up default Directory in documents folder

                setUpDataFolder(findCurrentOS());  // Setting up data folder

//                setupLogger();  // setting up the logging

                Main.stageManager = new StageManager(primaryStage);  // using StageManager to setup stage
                displayInitialScene();  // displaying the initialScene (main.fxml)
            } catch (RuntimeException e) {
                logr.log(Level.SEVERE, "Failed to start application. Error: ", e);
                informationWindow.display("An Error Occurred", "Failed to start application. Error: " + e);
                Platform.exit();  // closing application
            }
        } else Platform.exit();  // terminating application if database connection failed
    }

    /**
     * Function to run before application closes
     */
    @Override public void stop() {
        H2DBEditor.closeConnection(); // Closing main database connection
        logr.info("Closing application.");  // logging that application is closing
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to display first screen
     *
     * Useful to override this method by sub-classes wishing to change the first
     * Scene to be displayed on startup. Example: Functional tests on add window.
     */
    protected void displayInitialScene() { stageManager.switchScene(FXMLView.MAIN); }

    /**
     * Helper function to set the location of the default directory
     */
    protected static void setUpDefaultDirectory() throws RuntimeException {
        // logging that the main folder was created
        if ((new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + getString("appMainFolder"))).mkdirs()) {
            logr.info(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + getString("appMainFolder") + " directory was created");
        }

        // checking to see if default directory was created
        if((new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + getString("appMainFolder"))).exists())
            defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + getString("appMainFolder");
        else throw new RuntimeException("Failed to set up default directory");
    }

    /**
     * Helper function to set the location of the data folder
     *
     * @param currentOS the operating system of the computer
     */
    protected static void setUpDataFolder(String currentOS) {
        // sets the dataFolder (folder where the logging is kept) to
        //      Windows: C:/Users/[username]/AppData/Local/[appFolderName]/logs
        //      MacOS/Unix/Solaris: ~/[appFolderName]/logs

        String dirLoc = null;

        if(currentOS == null) logr.warning("Data Folder was not created");

        else if(currentOS.equals(bundle.getString("windows"))) {
            dirLoc = System.getProperty("user.home") + File.separator +
                    "AppData" + File.separator +
                    "Local" + File.separator +
                    getString("appFolderName") + File.separator +
                    "logs";
        }

        else if(currentOS.equals(bundle.getString("mac")) ||
                currentOS.equals(bundle.getString("unix")) ||
                currentOS.equals(bundle.getString("solaris"))) {
            dirLoc = System.getProperty("user.home") + File.separator +
                    getString("appFolderName") + File.separator +
                    "logs";
        }

        if(dirLoc == null) logr.warning("Data Folder was not created");
        else if((new File(dirLoc)).mkdirs()) logr.info( dirLoc + " directory was created");

        dataFolder = dirLoc;
    }

    // TODO: figure out a better logging system

    /*///////////////////////////////////////////////// CONVENIENCE //////////////////////////////////////////////////*/

    /**
     * @author Mkyong.com https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
     * Convenience function to figure out the current operating system
     *
     * @return the current operating system
     */
    public static String findCurrentOS(){
        String OS = System.getProperty("os.name").toLowerCase();

        if (OS.contains("win")) return bundle.getString("windows");
        else if (OS.contains("mac")) return bundle.getString("mac");
        else if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0) return bundle.getString("unix");
        else if (OS.contains("sunos")) return bundle.getString("solaris");
        else return bundle.getString("noSupport");
    }

    /**
     * @author Alvin Alexander https://alvinalexander.com/blog/post/java/read-text-file-from-jar-file
     * Convenience function to open the given file and return its
     * contents to a string
     *
     * @param resourceFilePath the relative path of the resource file to get the text of
     * @return the contents of the file or teh empty string if errors occurred
     */
    public static String getFileText(String resourceFilePath) throws InvalidParameterException {
        try {
            if(resourceFilePath == null || "".equals(resourceFilePath))
                throw new InvalidParameterException("resourceFilepath (" + resourceFilePath + ") is invalid");

            InputStream is = Main.class.getResourceAsStream(resourceFilePath);  // getResourceAsStream() may throw NullPointerException
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) { sb.append(line).append("\n"); } // readLine() may throw IOException
            br.close();  // close() may throw IOException
            isr.close();  // close() may throw IOException
            is.close();  // close() may throw IOException
            return sb.toString();
        } catch (IOException | NullPointerException | InvalidParameterException e) {
            logr.log(Level.WARNING, "Failed to read file (" + resourceFilePath + ") and turn to String. Error: ", e);
        }
        return "";
    }

    /**
     * Convenience function to help make resource bundle string dynamic
     * replaces {0}, {1}, {3}, .. strings with given parameter values
     *
     * @param bundleKey the name of the key from the resource bundle
     * @param newValues the newValues to change the braced values in the bundle
     */
    public static String replaceBundleString(String bundleKey, String... newValues) throws InvalidParameterException {
        String bundleString;
        List<String> replacements;

        // checking if the key exists in the bundle
        try {
            if(bundleKey == null) throw new InvalidParameterException("bundleKey cannot be null");
            if("".equals(bundleKey)) throw new InvalidParameterException("bundleKey is empty string");
            bundleString = getString(bundleKey);
            replacements = parseStringsToReplace(bundleString);
        } catch (MissingResourceException | InvalidParameterException e) {
            return "";  // if key does not exist return empty string
        }

        // if newValues is null or was omitted, return value from ResourceBundle
        if(newValues == null || newValues.length == 0 || newValues[0] == null) return bundleString;
        else {
            String ret = bundleString;
            if(replacements.isEmpty()) return ret;
            else {
                int loopCount = (newValues.length > replacements.size())? replacements.size() : newValues.length;
                for(int i = 0; i < loopCount; i++){
                    ret = bundleString.replace(replacements.get(i), newValues[i]);
                    bundleString = ret;
                }
                return ret;
            }
        }
    }

    /**
     * Convenience function to create a list of the substrings to be changed
     * finds strings that match the regular expression: \\{\\d+}
     *
     * @param searchString the string in which to search for the substring
     * @return a List of substrings to replace in the order they were found
     */
    protected static List<String> parseStringsToReplace(String searchString) {
        List<String> ret = new ArrayList<>();

        // making sure searchString is not null
        if(searchString == null || "".equals(searchString)) return ret;
        else {
            Pattern pattern = Pattern.compile(getString("replaceBundleStringRegex"));
            Matcher matcher = pattern.matcher(searchString);
            // adding \\{\\d+} regex to list
            while(matcher.find()){ ret.add(matcher.group()); }
        }

        return ret;
    }

    /**
     * Convenience function to navigate to the desired link
     * @ author Mkyong https://www.mkyong.com/java/open-browser-in-java-windows-or-linux/
     *          edited to use CommandExecutor instead of Runtime
     * @param link the link to navigate to
     * @return true if successful
     */
    public static boolean navigateToLink(String link) throws RuntimeException {
        if(link == null) throw new InvalidParameterException("link is null");
        if("".equals(link)) throw new InvalidParameterException("link is empty string");

        String currentOS = findCurrentOS();  // getting the current operating system
        CommandExecutor commandExecutor = new CommandExecutor();
        UrlValidator urlValidator = new UrlValidator();

        if(!urlValidator.isValid(link)) {
            logr.warning("Failed to navigate to " + link + ". Invalid URL");
            throw new InvalidParameterException("link is malformed");
        }

        if(currentOS.equals(getString("noSupport"))) {
            logr.warning("Operating system is not supported! Failed to open " + link);  // logging errors
            throw new RuntimeException("Operating system is not supported");
        } else if(currentOS.equals(getString("windows"))) {
            commandExecutor.setCommands("rundll32", "url.dll,FileProtocolHandler", link);
            commandExecutor.runCommands();
        } else if(currentOS.equals(getString("mac"))) {
            commandExecutor.setCommands("open", link);
            commandExecutor.runCommands();
        } else if(currentOS.equals(getString("unix")) || currentOS.equals(getString("solaris"))) {
            commandExecutor.setCommands("xdg-open", link, "&");
            commandExecutor.runCommands();
        }
        return true;
    }

    /**
     * Convenience function to get the value at the specified key in the project's ResourceBundle file
     *
     * @param key a key in the projects bundle file
     * @return the value at that key
     */
    public static String getString(String key){
        if(key == null || "".equals(key)) return "";

        // checking if the key exists in the bundle
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "";  // if key does not exist return empty string
        }
    }

    /**
     * Convenience function to get the integer value at the specified key in the project's ResourceBundle file
     *
     * @param key a key in the projects bundle file
     * @return the integer value at that key
     */
    public static int getInt(String key) throws RuntimeException {
        if(key == null) throw new InvalidParameterException("key is null");
        if("".equals(key)) throw new InvalidParameterException("key is empty string");

        // checking if the key exists in the bundle and parsing value if it exists
        try {
            String bundleString = bundle.getString(key);
            return Integer.parseInt(bundleString);
        } catch (MissingResourceException e) {
            throw new InvalidParameterException("Key does not exist");  // if key does not exist return empty string
        } catch (NumberFormatException ee) {
            throw new RuntimeException("Value is not a number");
        }
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to get the names of the natures of the shipment
     *
     * @return a list of the natures of the shipment
     */
    public static ObservableList<String> getNatures() { return natures; }

    /**
     * Helper function to get the names of the states of the shipment
     *
     * @return a list of the states of the shipment
     */
    public static ObservableList<String> getStates() { return states; }

    /**
     * Helper function to get the names of the forms of the shipment
     *
     * @return a list of the forms of the shipment
     */
    public static ObservableList<String> getForms() { return forms; }

    /**
     * Getter function to get the resource bundle
     *
     * @return the resource bundle
     */
    public static ResourceBundle getBundle() {return bundle; }

    /**
     * Getter function to get the database editor for the embedded database
     *
     * @return the database editor for the embedded database
     */
    public static DatabaseEditor getDBEditor(){ return H2DBEditor; }

    /**
     * Getter function to get the main folder where logging data resides
     *
     * @return data folder for further used by the Open logs Button
     */
    static String getDataFolder() { return dataFolder; }

    /**
     * Getter function to get the folder where this application saves its files
     *
     * @return the application's default folder
     */
    public static String getDefaultFolder() { return defaultDir; }
}

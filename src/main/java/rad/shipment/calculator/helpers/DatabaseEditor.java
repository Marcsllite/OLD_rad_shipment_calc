package rad.shipment.calculator.helpers;

import rad.shipment.calculator.gui.InformationWindow;
import rad.shipment.calculator.gui.Main;

import java.security.InvalidParameterException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rad.shipment.calculator.gui.Main.getString;
import static rad.shipment.calculator.gui.Main.replaceBundleString;

public class DatabaseEditor {

    // Declaring variables
    private static final Logger logr = Logger.getLogger(DatabaseEditor.class.getName());  // getting logger
    private final String DRIVER;  // database driver
    private final String PATH;  // the path to the database file
    private final String SETTINGS;  // any settings for the database
    private final String USER;  // username for database
    private final String PASS;  // password for database
    private final String DATETIME;  // the date and time in the filename of the database if the user did not provide a valid path
    protected static Connection con;  // Connection to the database to be edited

    /*/////////////////////////////////////////////// DATABASE EDITOR ////////////////////////////////////////////////*/

    /**
     * Constructs a database editor object that can manipulate and view entries in a database
     *
     * @param driver the driver for the database
     * @param path the path to create/open the database (default path in bundle properties)
     * @param settings any additional settings to add to the end of the database path
     * @param user the username of the database
     * @param pass the password of the database
     */
    public DatabaseEditor(String driver, String path, String settings, String user, String pass) throws InvalidParameterException {
        // making sure values are valid
        if(driver == null || "".equals(driver)) throw new InvalidParameterException("Database driver cannot be null or empty string");

        DateTimeFormatter fileNameForm = DateTimeFormatter.ofPattern(Main.getString("dateFileNameFormat"));
        if(path == null || "".equals(path)) {
            DATETIME = fileNameForm.format(LocalDateTime.now());
            path = replaceBundleString("defaultDBPath" + DATETIME);
        } else DATETIME = "";
        if(settings == null) settings = "";
        if(user == null) user = "";
        if(pass == null) pass = "";

        // saving values
        DRIVER = driver;
        PATH = path;
        SETTINGS = settings;
        USER = user;
        PASS = pass;
    }

    /**
     * Function to start connection to main database
     *
     * @return true if a connection was started
     *          false if an something went wrong
     */
    public boolean startConnection() {
        try {
            Class.forName(DRIVER);  // registering database driver
            String conURL;

            if ("".equals(getSettings())) conURL = PATH;
            else conURL = PATH + SETTINGS;

            if ("".equals(getUser()))
                con = DriverManager.getConnection(conURL);
            else con = DriverManager.getConnection(conURL, USER, PASS);

            logr.info("Successfully connected to the database");
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            logr.log(Level.SEVERE, "Failed to setup/connect to database. Error: ", e);
            new InformationWindow().display("Error", "Connection to database failed. Error: " + e);
            return false;
        }
    }

    /**
     * Function to close the database connection
     */
    public void closeConnection() {
        try {
            if(con != null) {
                con.close();
                logr.info("closed database connection");
            } else {
                logr.warning("Failed to close db connection: connection is null");
            }
        } catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to close database connection. Error: ", e);
        }
    }

    /**
     * Function to populate the data in the database with the proper information if there is no data
     */
    public void setupEmbeddedDB() throws RuntimeException {
        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            // creating tables
            db.executeUpdate(replaceBundleString("createIsotopesTableCSV", FileHandler.getValidIsotopesCSVPath()));
            db.executeUpdate(replaceBundleString("createShortLongTableCSV", FileHandler.getShortLongCSVPath()));
            db.executeUpdate(replaceBundleString("createA1TableCSV", FileHandler.getA1CSVPath()));
            db.executeUpdate(replaceBundleString("createA2TableCSV", FileHandler.getA2CSVPath()));
            db.executeUpdate(replaceBundleString("createDecayConstTableCSV", FileHandler.getDecayConstCSVPath()));
            db.executeUpdate(replaceBundleString("createExemptConTableCSV", FileHandler.getExemptConCSVPath()));
            db.executeUpdate(replaceBundleString("createExemptLimTableCSV", FileHandler.getExemptLimCSVPath()));
            db.executeUpdate(replaceBundleString("createHalfLifeTableCSV", FileHandler.getHalfLifeCSVPath()));
            db.executeUpdate(replaceBundleString("createInstrArtLimLimTableCSV", FileHandler.getIALimLimCSVPath()));
            db.executeUpdate(replaceBundleString("createInstrArtPackageLimTableCSV", FileHandler.getIAPackageLimCSVPath()));
            db.executeUpdate(replaceBundleString("createLicLimTableCSV", FileHandler.getLicLimCSVPath()));
            db.executeUpdate(replaceBundleString("createLimLimitTableCSV", FileHandler.getLimLimCSVPath()));
            db.executeUpdate(replaceBundleString("createReportQTableCSV", FileHandler.getReportQCSVPath()));

            db.close();
        } catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to create tables in database. Error: ", e);  // logging any errors
        }
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to get the size of a ResultSet
     *
     * @param result the ResultSet to find the size of
     * @return the size of the ResultSet
     */
    protected int getResultSetSize(ResultSet result){
        int size = 0;
        if (result != null) {
            try {
                result.last();    // moves cursor to the last row
                size = result.getRow(); // get row id
                result.beforeFirst();
            } catch (SQLException e) {
                logr.log(Level.SEVERE, "Failed to reset the result set. Error: ", e);
            }
        }
        return size;
    }

    /**
     * Helper function to get the abbreviated name of the given isotope name
     *
     * @param name the name of the isotope to get the abbreviation of
     * @return the abbreviated name of the isotope or an empty string if an error occurred
     */
    public String getAbbr(String name) throws RuntimeException {
        if(name == null || "".equals(name)) throw new InvalidParameterException("Isotope name cannot be null or empty string");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("isotopesTableQuery",
                    getString("abbrCol"),
                    "Upper(" + getString("abbrCol") + ") = Upper('" + name + "') or Upper(" + getString("nameCol") + ") = Upper('" + name + "')")
            );

            if(result.next()) return result.getString(getString("abbrCol"));
            else return "";
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for " + name + ". Error: ", e);  // logging any errors
        }
        return "";
    }

    /**
     * Helper function to get the full name of the given isotope name
     *
     * @param name the name of the isotope to get the abbreviation of
     * @return the full name of the isotope or an empty string if an error occurred
     */
    public String getFullName(String name) throws RuntimeException {
        if(name == null || "".equals(name)) throw new InvalidParameterException("Isotope name cannot be null or empty string");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("isotopesTableQuery",
                    getString("nameCol"),
                    "Upper(" + getString("abbrCol") + ") = Upper('" + name + "') or Upper(" + getString("nameCol") + ") = Upper('" + name + "')")
            );

            if(result.next()) return result.getString(getString("nameCol"));
            else return "";
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for " + name + ". Error: ", e);  // logging any errors
        }
        return "";
    }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/

//    /**
//     * Setter function to set/add the given isotope's A1 value
//     * into the A1 table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateA1(String name, Float value) throws RuntimeException {
//        if (!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if (value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateA1TableValue", String.valueOf(value), name));
//            db.executeUpdate(replaceBundleString("updateA1TableCSV", FileHandler.getA1CSVPath()));
//
//            db.close();
//        } catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's A2 value
//     * into the A2 table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateA2(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateA2TableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateA2TableCSV", FileHandler.getA2CSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Decay Constant value
//     * into the Decay Constant table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateDecayConstant(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateDecayConstTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateDecayConstTableCSV", FileHandler.getDecayConstCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Exempt Concentration value
//     * into the Exempt Concentration table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateExemptConcentration(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateExemptConTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateExemptConTableCSV", FileHandler.getExemptConCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Exempt Limit value
//     * into the Exempt Limit table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateExemptLimit(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateExemptLimTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateExemptLimTableCSV", FileHandler.getExemptLimCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Half Life value
//     * into the Half Life table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateHalfLife(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateHalfLifeTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateHalfLifeTableCSV", FileHandler.getHalfLifeCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Instruments/Articles Limited Multiplier value
//     * into the Instruments/Articles Limited Limit Multiplier table in the database
//     *
//     * @param state the state of the instrument/article
//     * @param form the form of the instrument/article
//     * @param value the value of the instrument/article
//     */
//    public void updateIALimitedMultiplier(String state, String form, Float value) throws RuntimeException {
//        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
//        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateInstrArtLimLimTableValue", state, form, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateInstrArtLimLimTableCSV", FileHandler.getIALimLimCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + state + "', '" + form + ", " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Instruments/Articles Package Limit value
//     * into the Instruments/Articles Package Limit table in the database
//     *
//     * @param state the state of the instrument/article
//     * @param form the form of the instrument/article
//     * @param value the value of the instrument/article
//     */
//    public void updateIAPackageLimit(String state, String form, Float value) throws RuntimeException {
//        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
//        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateInstrArtPackageLimTableValue", state, form, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateInstrArtPackageLimTableCSV", FileHandler.getIAPackageLimCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + state + "', '" + form + ", " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's License Limit value
//     * into the Info table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateLicenseLimit(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateLicLimTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateLicLimTableCSV", FileHandler.getLicLimCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Limited Limit value
//     * into the Limited Limit table in the database
//     *
//     * @param state the state of the limited isotope
//     * @param form the form of the limited isotope
//     * @param value the value of the limited isotope
//     */
//    public void updateLimitedLimit(String state, String form, Float value) throws RuntimeException {
//        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
//        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateLimLimitTableValue", state, form, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateLimLimitTableCSV", FileHandler.getLicLimCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + state + "', '" + form + ", " + value + "). Error: ", e);  // logging any errors
//        }
//    }
//
//    /**
//     * Setter function to set/add the given isotope's Reportable Quantity value
//     * into the Reportable Quantity table in the database
//     *
//     * @param name the name of the isotope
//     * @param value the value of the isotope
//     */
//    public void updateReportableQuantity(String name, Float value) throws RuntimeException {
//        if(!isValidIso(name)) throw new InvalidParameterException("Isotope is invalid");
//        if(value <= 0) throw new InvalidParameterException("value cannot be 0 or negative");
//
//        name = getAbbr(name);
//
//        try {
//            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");
//
//            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands
//
//            db.executeUpdate(replaceBundleString("updateReportQTableValue", name, String.valueOf(value)));
//            db.executeUpdate(replaceBundleString("updateReportQTableCSV", FileHandler.getReportQCSVPath()));
//
//            db.close();
//        }  catch (SQLException | RuntimeException e) {
//            logr.log(Level.SEVERE, "Failed to update the database and csv file with the new value('" + name + "', " + value + "). Error: ", e);  // logging any errors
//        }
//    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/

    /**
     * Getter function to get the list of isotopes that have different A1 and A2 values for their short live and long lived
     * versions
     *
     * @return a list of the isotopes from the shortLong table
     */
    public List<String> getShortLong() throws RuntimeException {
        List<String> ret = new ArrayList<>();

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("shortLongTableQuery", getString("abbrCol")));

            while(result.next()) ret.add(result.getString(getString("abbrCol")));
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database. Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's A1 value 
     * from the A1 table in the database
     * 
     * @param name the name of the isotope
     * @return the A1 value of that isotope
     */
    public float getA1(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("A1TableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the A1 value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's A2 value 
     * from the A2 table in the database
     *
     * @param name the name of the isotope
     * @return the A2 value of that isotope
     */
    public float getA2(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("A2TableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the A2 value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Decay Constant value 
     * from the Decay Constant table in the database
     *
     * @param name the name of the isotope
     * @return the Decay Constant value of that isotope
     */
    public float getDecayConstant(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("decayConstTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the decay constant value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Exempt Concentration value 
     * from the Exempt Concentration table in the database
     *
     * @param name the name of the isotope
     * @return the Exempt Concentration value of that isotope
     */
    public float getExemptConcentration(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("exemptConTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the exempt concentration value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Exempt Limit value 
     * from the Exempt Limit table in the database
     *
     * @param name the name of the isotope
     * @return the Exempt Limit value of that isotope
     */
    public float getExemptLimit(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("exemptLimTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the exempt limit value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Half Life value 
     * from the Half Life table in the database
     *
     * @param name the name of the isotope
     * @return the Half Life value of that isotope
     */
    public float getHalfLife(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("halfLifeTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the half life value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Instruments/Articles Limited Multiplier value 
     * from the Instruments/Articles Limited Limit Multiplier table in the database
     *
     * @param state the state of the instrument/article
     * @param form the form of the instrument/article
     * @return the Instruments/Articles Limited Multiplier value of that isotope
     */
    public float getIALimitedMultiplier(String state, String form) throws RuntimeException {
        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("instrArtLimLimTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("stateCol") + ") = upper('" + state + "') and upper(" + getString("formCol") + ") = upper('" + form + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the instrument/article limited limit multiplier value of state:" + state + " and form:" + form + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Instruments/Articles Package Limit value 
     * from the Instruments/Articles Package Limit table in the database
     *
     * @param state the state of the instrument/article
     * @param form the form of the instrument/article
     * @return the Instruments/Articles Package Limit value of that isotope
     */
    public float getIAPackageLimit(String state, String form) throws RuntimeException {
        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("instrArtPackageLimTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("stateCol") + ") = upper('" + state + "') and upper(" + getString("formCol") + ") = upper('" + form + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the instrument/article package limit value of state:" + state + " and form:" + form + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's License Limit value 
     * from the Info table in the database
     *
     * @param name the name of the isotope
     * @return the License Limit value of that isotope
     */
    public float getLicenseLimit(String name) {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("licLimTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the license limit value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Limited Limit value 
     * from the Limited Limit table in the database
     *
     * @param state the state of the limited isotope
     * @param form the form of the limited isotope
     * @return the Limited Limit value of that isotope
     */
    public float getLimitedLimit(String state, String form) throws RuntimeException {
        if(state == null || "".equals(state)) throw new InvalidParameterException("state cannot be null or empty string");
        if(form == null || "".equals(form)) throw new InvalidParameterException("form cannot be null or empty string");
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("limLimitTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("stateCol") + ") = upper('" + state + "') and upper(" + getString("formCol") + ") = upper('" + form + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the limited limit value of state:" + state + " and form:" + form + ". Error: ", e);  // logging any errors
        }
        return ret;
    }

    /**
     * Getter function to get the given isotope's Reportable Quantity value
     * from the Reportable Quantity table in the database
     *
     * @param name the name of the isotope
     * @return the Reportable Quantity value of that isotope
     */
    public float getReportableQuantity(String name) throws RuntimeException {
        float ret = (float)Main.getInt("defaultInt");

        try {
            if (con == null) throw new RuntimeException("Cannot connect to database, connection is null");

            name = getAbbr(name);
            if("".equals(name)) throw new RuntimeException("name is invalid");

            Statement db = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  // creating statement to execute SQL commands

            ResultSet result = db.executeQuery(replaceBundleString("reportQTableQuery",
                    getString("valueCol"),
                    "upper(" + getString("abbrCol") + ") = upper('" + name + "')")
            );

            if(result.next()) ret = result.getFloat(getString("valueCol"));

            db.close();
        }  catch (SQLException | RuntimeException e) {
            logr.log(Level.SEVERE, "Failed to query the database for the reportable quantity value of " + name + ". Error: ", e);  // logging any errors
        }
        return ret;
    }
    
    /**
     *  Getter function to get the driver for the database
     *
     * @return the driver for the database
     */
    public String getDriver() { return DRIVER; }

    /**
     *  Getter function to get the path for the database
     *
     * @return the path for the database
     */
    public String getPath() { return PATH; }

    /**
     *  Getter function to get the settings for the database
     *
     * @return the settings for the database
     */
    public String getSettings() { return SETTINGS; }

    /**
     *  Getter function to get the username for the database
     *
     * @return the username for the database
     */
    public String getUser() { return USER; }

    /**
     *  Getter function to get the password for the database
     *
     * @return the password for the database
     */
    public String getPass() { return PASS; }

    /**
     *  Getter function to get the password for the database
     *
     * @return the password for the database
     */
    public String getDateTime() { return DATETIME; }
}

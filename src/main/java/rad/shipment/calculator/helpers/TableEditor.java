package rad.shipment.calculator.helpers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import rad.shipment.calculator.gui.HomePaneController;
import rad.shipment.calculator.gui.Main;

import java.util.List;
import java.util.ResourceBundle;

public class TableEditor {

    // Declaring variables
    private static final ResourceBundle BUNDLE = Main.getBundle();
    private static final ObservableList<Isotope> ISOTOPES = FXCollections.observableArrayList();
    final HomePaneController homePaneController;
    
    @Contract(pure = true) public TableEditor(HomePaneController homePaneController){ this.homePaneController = homePaneController;}

    /**
     * Getter function to get the Observable list containing all the isotopes
     *
     * @return an Observable list of Isotope
     */
    @Contract(pure = true) public static ObservableList<Isotope> getIsotopes() { return ISOTOPES;}
    
    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/

    /**
     * Helper function to add a row from the database (in a Isotope object)
     * to the table in the HomePaneController
     *
     * @param newRow the row to be added to the GUI table
     */
    public void addDBRowToGUI(Isotope newRow){
        ISOTOPES.add(newRow);
        homePaneController.getTableView().setItems(ISOTOPES);
    }

    /**
     * Helper function to update the info in the ISOTOPES observable list
     *
     * @param newRow the Isotope object containing the values to be set
     */
    private void updateRowInfo(Isotope newRow){
        // getting the list of isotopes in the shipment
        List<Isotope> isotopes = HomePaneController.getShipment().getIsotopes();

        // updating the isotope at the repeatIndex
        isotopes.set(HomePaneController.getRepeatIndex(), newRow);
    }

    /**
     * Helper function to figure out if a row already exists in the table
     *
     * @param newIso the isotope to check
     * @return true if the isotope is already in the table otherwise false
     */
    @Contract(pure = true) private boolean isRepeat(Isotope newIso){
        // searching through all the ISOTOPES in the table
        // (same as the Isotope objects in the HomePaneController Observable list)
        for(Isotope isotope: ISOTOPES){
            // checking if the ip or hostname are the same
            if(isotope.equals(newIso)) {
                HomePaneController.setRepeatIndex(ISOTOPES.indexOf(isotope));
                return true;
            }
        }
        HomePaneController.setRepeatIndex(Integer.parseInt(BUNDLE.getString("defaultInt")));  // resetting repeatIndex to show that device is not in table
        return false;
    }

    /*//////////////////////////////////////////////// TABLE EDITOR //////////////////////////////////////////////////*/

    /**
     * Function used by the Add Pane and Import Button to add a new Device to the table
     *
     * @param newRow the new device to be added
     *
     * @return true if the device was added to the table (new Device)
     *         false if the ISOTOPES was not added to the table (duplicate)
     */
    public boolean addRow(Isotope newRow){
        // the device is already in the table return false
        if(isRepeat(newRow)) return false;
        else {
//            Main.getDBEditor().addRowToDB(newRow);  // calling function to add the row to the database
            ISOTOPES.add(newRow);  // adding the row to the Observable List
            homePaneController.getTableView().setItems(ISOTOPES);  // updating the TableView with the observable list
            return true;
        }
    }

    /**
     * Function used by the Edit Pane to change the info of a device in the database
     * and in the ISOTOPES observable list
     *
     * @param editedRow the edited Device to replace the old one in the table
     * @return true if no errors occurred and the row was changed
     *          false if the row is a duplicate of another row already in the table
     */
    public boolean editRow(Isotope editedRow) {
        if(isRepeat(editedRow)) {
            updateRowInfo(editedRow);
//            Main.getDBEditor().updateDBInfo(editedRow);
            homePaneController.getTableView().refresh();  // refreshing table to show updated values
            return true;
        } else { return false; }
    }

    /**
     * Function to refresh the values of the ISOTOPES in the tableview
     *
     * @param shipment an Observable list containing the new values for all of the ISOTOPES in the table
     */
    public void refresh(@NotNull ObservableList<Isotope> shipment) {
        for( Isotope device: shipment) { editRow(device); }
    }
}

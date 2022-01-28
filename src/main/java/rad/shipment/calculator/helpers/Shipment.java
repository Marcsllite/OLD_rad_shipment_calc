package rad.shipment.calculator.helpers;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import rad.shipment.calculator.gui.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shipment {
    // Declaring Variables
    private final float defaultVal = (float) Main.getInt("defaultInt");  // default float value for uninitialized variables
    private final List<Isotope> isotopes;
    private final LocalDate _refDate;                  // the reference date of the shipment
    private final SimpleFloatProperty _mass;                    // the mass of the shipment (grams for solids or liters for liquids)
    private final SimpleStringProperty _nature;                  // the nature of the shipment (regular,  instrument, article)
    private final SimpleStringProperty _state;                  // the state of the shipment (solid, liquid, gas)
    private final SimpleStringProperty _form;                   // the form of the shipment (normal, special, tritium)

    // Constant values from database

    /*/////////////////////////////////////////////////// SHIPMENT ///////////////////////////////////////////////////*/
    /**
     * Constructs an empty Shipment object
     */
    public Shipment() {
        this.isotopes = new ArrayList<>();

        // saving isotope reference date
        _refDate = LocalDate.now();

        _mass = new SimpleFloatProperty(defaultVal);
        _nature = new SimpleStringProperty();
        _state = new SimpleStringProperty();
        _form = new SimpleStringProperty();
    }

    /**
     * Constructs an empty Shipment object
     */
    public Shipment(float mass, String nature, String state, String form, LocalDate refDate) {
        this.isotopes = new ArrayList<>();

        // saving isotope reference date
        _refDate = refDate;

        _mass = new SimpleFloatProperty(mass);
        _nature = new SimpleStringProperty(nature);
        _state = new SimpleStringProperty(state);
        _form = new SimpleStringProperty(form);
    }

    /**
     * Constructs an Shipment object with the given isotopes
     *
     * @param isotopes the isotopes in the shipment
     */
    public Shipment(Isotope... isotopes) {
        this.isotopes = new ArrayList<>();
        setIsotopes(isotopes);

        // saving isotope reference date
        _refDate = LocalDate.now();

        _mass = new SimpleFloatProperty(defaultVal);
        _nature = new SimpleStringProperty();
        _state = new SimpleStringProperty();
        _form = new SimpleStringProperty();
    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     *  Helper function to figure out if the given mass is the same as the shipment mass
     *
     * @param mass the mass (in liters or grams) to checked against the shipment mass
     * @param state the state (solid, liquid, gas) to be checked against the shipment state
     * @return true if the masses are the same
     *          false if they are different
     */
    public boolean isMassConsistent(float mass, String state) { return _mass.getValue() == mass && 
                                                         state.equals(_state.get()); }

    /**
     *  Helper function to figure out if the given nature, state, form is the same as the 
     *  shipment nature, state form
     *
     * @param nature the nature (regular, instrument, article) to be checked against the shipment nature
     * @param state the state (solid, liquid, gas) to be checked against the shipment state
     * @param form the form (special, normal) to be checked against the shipment form
     * @return true if the nature, state, and form are the same
     *          false if they are different
     */
    public boolean isNSFConsistent(String nature, String state, String form) { 
        return nature.equals(_nature.get()) &&
                state.equals(_state.get()) && 
                form.equals(_form.get()); 
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/
    /**
     * Getter function to get the isotopes in this shipment
     *
     * @return the isotopes in this shipment
     */
    public List<Isotope> getIsotopes() { return isotopes; }

    /**
     * Getter function to get the mass of this shipment
     *
     * @return the mass of this shipment
     */
    public Float getMass() { return _mass.get(); }
    
    /**
     * Getter function to get the nature of this shipment
     *
     * @return the nature of this shipment
     */
    public String getNature() { return _nature.get(); }

    /**
     * Getter function to get the state of this shipment
     *
     * @return the state of this shipment
     */
    public String getState() { return _state.get(); }
    
    /**
     * Getter function to get the form of this shipment
     *
     * @return the reference date of this shipment
     */
    public String getForm() { return _form.get(); }
    
    
    /**
     * Getter function to get the reference date of this shipment
     *
     * @return the reference date of this shipment
     */
    public LocalDate getRefDate() { return _refDate; }

    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/
    /**
     * Setter function to set the isotopes in the shipment
     *
     * @param isotopes the isotopes to be put into the shipment object
     */
    public void setIsotopes(Isotope... isotopes) {
        if(isotopes != null)
            for (Isotope isotope : isotopes){
                if(isotope != null)this.isotopes.add(isotope);  // adding the isotope to the list
            }
    }
}

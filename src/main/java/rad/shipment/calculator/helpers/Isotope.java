package rad.shipment.calculator.helpers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.Contract;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import rad.shipment.calculator.gui.Main;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Isotope {
    // Declaring Variables
    private static final Logger logr = Logger.getLogger(Isotope.class.getName());  // getting logger
    private final float defaultVal = (float)Main.getInt("defaultInt");  // default float value for uninitialized variables
    private final float DPM = (float)2.22e+6;
    private final DatabaseEditor dbEditor = Main.getDBEditor();  // getting the database editor from the main class
    private LocalDate _RefDate;                  // the reference date of the isotope
    private final SimpleStringProperty _Name;                    // The name of the isotope
    private final SimpleStringProperty _DBName;                 // The database search name of the isotope (including short/long or lung absorption)
    private final SimpleFloatProperty _A0;                      // Initial Activity (microCi) of isotope
    private final SimpleFloatProperty _AToday;                      // Today's Activity (microCi) of isotope
    private final SimpleFloatProperty _ConcentrationToday;      // Today's concentration (microCi/gram or microCi/liter) of isotope
    private final SimpleFloatProperty _DecaysPerMinute;         // Decays per minute of isotope
    private final SimpleFloatProperty _ActivityConcentration;   // Activity Concentration (Bq) of isotope (Activity of isotope / sum of entire package concentration)
    private final SimpleFloatProperty _ActivityFraction;        // Activity Fraction (Bq) of isotope (Activity fraction of isotope / sum of entire package activity)
    private final SimpleFloatProperty _LimitedQuanMultiplier;   // Limited Quantities limit multiplier  (see 173.425_Table 4)
    private final SimpleFloatProperty _ReportableQuanFraction;  // Fraction of Reportable Quantity of isotope (Activity Today (microCi) / Reportable Quantity (microCuries))
    private final SimpleFloatProperty _LimitPercentage;         // Activity Percentage of isotope limit (activity of isotope / activity limit of isotope)
    private final SimpleFloatProperty _ConcentrationPercentage;  // Activity concentration of isotope (activity concentration of isotope / activity concentration limit of isotope)
    private final SimpleFloatProperty _LicensePercentage;       // Percentage of licensing limit (activity of isotope / license_limit from Info table in database)
    private final SimpleFloatProperty _ALimit;                  // A1 value (TBq) of isotope if Special form, A2 value (TBq) of isotope if Normal Form
    private final SimpleFloatProperty _HRCQLimit;               // Highway Route Control Limit (either 1000 * TBq or 3000 * A1/A2 whichever is the lowest number)
    private final SimpleBooleanProperty _IsFissile;              // true if isotope is fissile
    private final SimpleBooleanProperty _IsReportableQuan;      // true if isotope is a reportable quantity
    private final SimpleStringProperty _ShortLong;              // for isotopes with different half lives
    private final SimpleStringProperty _LungAbs;                // the lung absorption speed if the isotope is Uranium (f = fast, m = medium, s = slow)
    private final SimpleIntegerProperty _IsotopeClass;          // Classification of isotope as an integer
    private final SimpleFloatProperty _Mass;                    // the mass of the isotope (grams for solids or liters for liquids)
    private final SimpleStringProperty _MassUnit;               // the mass unit for the isotope (grams or liters)
    private final SimpleStringProperty _Nature;                  // the nature of the isotope (regular,  instrument, article)
    private final SimpleStringProperty _State;                  // the state of the isotope (solid, liquid, gas)
    private final SimpleStringProperty _Form;                   // the form of the isotope (normal, special, tritium)
                                                                // (0 = Exempt, 1 = Excepted, 2 = Type A, 4 = Type B, 8 = Type B: Highway Route Control)
    // Constant values from database
    private final SimpleFloatProperty _A1;                  // A1 (TBq) of isotope (from Info table in database)
    private final SimpleFloatProperty _A2;                  // A2 (TBq) of isotope (from Info table in database)
    private final SimpleFloatProperty _DecayConstant;       // Decay Constant (1 / halflife(days)) of isotope (from Info table in database)
    private final SimpleFloatProperty _ExemptConcentration; // Exempt Concentration (Bq/gram) of isotope (from Info table in database)
    private final SimpleFloatProperty _ExemptLimit;         // Exempt Limit (Bq)of isotope (from Info table in database)
    private final SimpleFloatProperty _HalfLife;            // Halflife (days) of isotope (from Info table in database)
    private final SimpleFloatProperty _IALimitedMultiplier; // Instruments/Articles multiplier (see 173.425_Table 4) of isotope (from Info table in database)
    private final SimpleFloatProperty _LicenseLimit;        // Licensing Limit (microCi) of isotope (from Info table in database)
    private final SimpleFloatProperty _LimitedLimit;        // limited Limit (TBq) of isotope (from Info table in database)
    private final SimpleFloatProperty _ReportableQuan;      // Reportable Quantity (Ci) of isotope (from Info table in database)

    /*/////////////////////////////////////////////////// ISOTOPE ////////////////////////////////////////////////////*/

    /**
     * Constructs an Isotope object with the given name and initial activity
     *
     * @param name the name of the isotope
     * @param referenceDate the reference date of the isotope
     */
    public Isotope(String name, java.time.LocalDate referenceDate) throws RuntimeException {
        try {
            // getting the values from the database
            _A1 = new SimpleFloatProperty(dbEditor.getA1(name));
            _A2 = new SimpleFloatProperty(dbEditor.getA2(name));
            _DecayConstant = new SimpleFloatProperty(dbEditor.getDecayConstant(name));
            _ExemptConcentration = new SimpleFloatProperty(dbEditor.getExemptConcentration(name));
            _ExemptLimit = new SimpleFloatProperty(dbEditor.getExemptLimit(name));
            _HalfLife = new SimpleFloatProperty(dbEditor.getHalfLife(name));
            _LicenseLimit = new SimpleFloatProperty(dbEditor.getLicenseLimit(name));
            _ReportableQuan = new SimpleFloatProperty(dbEditor.getReportableQuantity(name));
        } catch (InvalidParameterException e) {
            logr.log(Level.SEVERE, "Failed to create isotope named " + name + ". Error: ", e);
            throw new RuntimeException("Failed to create isotope named " + name);
        }

        // saving isotope name
        _Name = new SimpleStringProperty(dbEditor.getAbbr(name));

        // saving isotope database name
        _DBName = new SimpleStringProperty(dbEditor.getAbbr(name));

        // saving isotope reference date
        _RefDate = new LocalDate(referenceDate.getYear(), referenceDate.getMonthValue(), referenceDate.getDayOfMonth());

        // making other values the default value
        _A0 = new SimpleFloatProperty(defaultVal);
        _AToday = new SimpleFloatProperty(calculateAToday());
        _Mass = new SimpleFloatProperty(defaultVal);
        _MassUnit = new SimpleStringProperty();
        _Nature = new SimpleStringProperty();
        _State = new SimpleStringProperty();
        _Form = new SimpleStringProperty();
        _ConcentrationToday = new SimpleFloatProperty(defaultVal);
        _DecaysPerMinute = new SimpleFloatProperty(defaultVal);
        _ActivityConcentration = new SimpleFloatProperty(defaultVal);
        _ActivityFraction = new SimpleFloatProperty(defaultVal);
        _LimitedQuanMultiplier = new SimpleFloatProperty(defaultVal);
        _ReportableQuanFraction = new SimpleFloatProperty(defaultVal);
        _LimitPercentage = new SimpleFloatProperty(defaultVal);
        _ConcentrationPercentage = new SimpleFloatProperty(defaultVal);
        _LicensePercentage = new SimpleFloatProperty(defaultVal);
        _ALimit = new SimpleFloatProperty(defaultVal);
        _HRCQLimit = new SimpleFloatProperty(defaultVal);
        _IALimitedMultiplier = new SimpleFloatProperty(defaultVal);
        _LimitedLimit = new SimpleFloatProperty(defaultVal);
        _IsFissile = new SimpleBooleanProperty(false);
        _IsReportableQuan = new SimpleBooleanProperty(false);
        _LungAbs = new SimpleStringProperty();
        _ShortLong = new SimpleStringProperty();
        _IsotopeClass = new SimpleIntegerProperty((int)defaultVal);
    }

    /**
     * Constructs an Isotope object with the given name and initial activity
     *
     * @param name the name of the isotope
     * @param mass the mass of the isotope
     * @param A0 the initial activity (microCi) of the isotope
     * @param nature the state (regular, instrument or article) of the isotope
     * @param state the state (solid, liquid, gas) of the isotope
     * @param form the form (special, normal) of the isotope
     * @param referenceDate the reference date of the isotope
     */
    public Isotope(String name, float mass, String massUnit, float A0, String nature, String state, String form, java.time.LocalDate referenceDate) throws RuntimeException {
        try {
            // getting the values from the database
            _A1 = new SimpleFloatProperty(dbEditor.getA1(name));
            _A2 = new SimpleFloatProperty(dbEditor.getA2(name));
            _DecayConstant = new SimpleFloatProperty(dbEditor.getDecayConstant(name));
            _ExemptConcentration = new SimpleFloatProperty(dbEditor.getExemptConcentration(name));
            _ExemptLimit = new SimpleFloatProperty(dbEditor.getExemptLimit(name));
            _HalfLife = new SimpleFloatProperty(dbEditor.getHalfLife(name));
            _IALimitedMultiplier = new SimpleFloatProperty(dbEditor.getIALimitedMultiplier(state, form));
            _LicenseLimit = new SimpleFloatProperty(dbEditor.getLicenseLimit(name));
            _LimitedLimit = new SimpleFloatProperty(dbEditor.getLimitedLimit(state, form));
            _ReportableQuan = new SimpleFloatProperty(dbEditor.getReportableQuantity(name));
        } catch (InvalidParameterException e) {
            logr.log(Level.SEVERE, "Failed to create isotope named " + name + ". Error: ", e);
            throw new RuntimeException("Failed to create isotope named " + name);
        }

        // saving isotope name
        _Name = new SimpleStringProperty(dbEditor.getAbbr(name));

        // saving isotope database name
        _DBName = new SimpleStringProperty(dbEditor.getAbbr(name));

        // saving isotope reference date
        _RefDate = new LocalDate(referenceDate.getYear(), referenceDate.getMonthValue(), referenceDate.getDayOfMonth());

        // saving isotope mass
        _Mass = new SimpleFloatProperty(mass);

        // saving isotope mass unit
        _MassUnit = new SimpleStringProperty(massUnit);

        // saving isotope nature
        _Nature = new SimpleStringProperty(nature);
        
        // saving isotope state
        _State = new SimpleStringProperty(state);

        // saving isotope form
        _Form = new SimpleStringProperty(form);

        // making sure initial activity is valid
        if(A0 <= 0) throw new InvalidParameterException("Initial Activity of isotope cannot be less than or equal to 0");

        _A0 = new SimpleFloatProperty(A0);
        _AToday = new SimpleFloatProperty(calculateAToday());
        _ConcentrationToday = new SimpleFloatProperty(defaultVal);
        _DecaysPerMinute = new SimpleFloatProperty(defaultVal);
        _ActivityConcentration = new SimpleFloatProperty(defaultVal);
        _ActivityFraction = new SimpleFloatProperty(defaultVal);
        _LimitedQuanMultiplier = new SimpleFloatProperty(defaultVal);
        _ReportableQuanFraction = new SimpleFloatProperty(defaultVal);
        _LimitPercentage = new SimpleFloatProperty(defaultVal);
        _ConcentrationPercentage = new SimpleFloatProperty(defaultVal);
        _LicensePercentage = new SimpleFloatProperty(defaultVal);
        _ALimit = new SimpleFloatProperty(defaultVal);
        _HRCQLimit = new SimpleFloatProperty(defaultVal);
        _IsFissile = new SimpleBooleanProperty(false);
        _IsReportableQuan = new SimpleBooleanProperty(false);
        _LungAbs = new SimpleStringProperty();
        _ShortLong = new SimpleStringProperty();
        _IsotopeClass = new SimpleIntegerProperty((int)defaultVal);
    }

    /**
     * Overridden Objects toString function
     *
     * @return the string representation of the isotope
     */
    @SuppressWarnings("ImplicitArrayToString") @Override public String toString(){
        return "Isotope: {\nName: " + get_Name() +
                "),\nDBName: " + get_DBName() +
                ",\nInitial Activity: " + get_A0() + " \u00B5Ci" +
                ",\nMass: " + get_Mass() +
                ",\nNature: " + get_Nature() +
                ",\nState: " + get_State() +
                ",\nForm: " + get_Form() +
                ",\nReference Date: " + get_RefDate() + "}";
    }

    /**
     * Overridden Objects equals function
     *
     * @param obj the object to check
     * @return whether the object is a Isotope object and has
     *          the same name, database name, initial activity,
     *          mass, mass unit, nature, state, and form
     */
    @Contract(value = "null -> false", pure = true) @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || obj.getClass() != getClass()) return false;

        Isotope other = (Isotope) obj;

        return other.get_Name().equals(this.get_Name()) &&
                other.get_DBName().equals(this.get_DBName()) &&
                other.get_A0() == this.get_A0() &&
                other.getMassValue() == this.getMassValue() &&
                other.get_MassUnit().equals(this.get_MassUnit()) &&
                other.get_Nature().equals(this.get_Nature()) &&
                other.get_State().equals(this.get_State()) &&
                other.get_Form().equals(this.get_Form());
    }

    /**
     * Overridden Objects hasCode function
     * hashCode value is determined by turning the abbreviated device name into an integer
     * and adding the values of the halfLife, initial Activity, and Mass
     *
     * @return the hasCode value for this object
     */
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((get_Name() == null) ? 0 : get_Name().hashCode()) +
                ((get_DBName() == null) ? 0 : get_DBName().hashCode()) +
                ((get_MassUnit() == null) ? 0 : get_MassUnit().hashCode()) +
                ((get_Nature() == null) ? 0 : get_Nature().hashCode()) +
                ((get_State() == null) ? 0 : get_State().hashCode()) +
                ((get_Form() == null) ? 0 : get_Form().hashCode()) +
                ((int)get_A0()) + ((int)getMassValue());
        return result;    }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     * Helper function to get this isotope's decay date
     *
     * @return the decay date of this isotope
     */
    public LocalDate getDecayDate() {
        float multiplier = DPM / 500;

        return getLocalDateRefDate().plusDays((int) (Math.log(get_A0() * multiplier) / get_DecayConstant()));
    }

    /**
     * Helper function to get this isotope's current Activity in microCuries
     *
     * @return the current activity of this isotope
     */
    public float calculateAToday() throws IllegalStateException {
        Interval interval;
        // getting the interval between _RefDate and LocalDate.now()
        // making sure that _RefDate is not after LocalDate.now()
        // -1 = first date is before second date (second date is in the future)
        // 0  = dates are the same
        // 1  = first date is after second date (second date is in the past)
        if(LocalDate.now().toDate().compareTo(getLocalDateRefDate().toDate()) >= 0)
            interval =  new Interval(getLocalDateRefDate().toDate().getTime(), LocalDate.now().toDate().getTime());
        else throw new IllegalStateException("reference date cannot be after today's date");
        Period duration = interval.toPeriod();

        return get_A0() * (float)Math.exp(-getHalfLifeValue() * duration.getDays());
    }

    /**
     * Helper function to get this isotope's decays per minute
     *
     * @return this isotope's decays per minute
     */
    public float calculateDPM() throws IllegalStateException { return calculateAToday() * DPM; }

    /**
     * Helper function to get the date this isotope will be exempt from licensing
     *
     * @return the date this isotope will be exempt from licensing
     */
    public LocalDate calculateDateForExemptLicensing() { return getLocalDateRefDate().plusDays((int) (Math.log(get_A0() * get_LicenseLimit()) / get_DecayConstant())); }

    /**
     * Helper function to get the date this isotope will be exempt from shipping
     *
     * @return the date this isotope will be exempt from shipping
     */
    public LocalDate calculateDateForExemptShipping() {
        // TODO: finish calculateDateForExemptShipping function
        return null;
    }

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/

    /**
     * Getter function to get this isotope's reference date
     *
     * @return the reference date of this isotope
     */
    public String get_RefDate() { return _RefDate.toString(Main.getString("tableDatePattern")); }

    /**
     * Getter function to get this isotope's reference date
     *
     * @return the reference date of this isotope
     */
    public LocalDate getLocalDateRefDate() { return _RefDate; }

    /**
     * Getter function to get both the abbreviated and full name of this isotope
     *
     * @return the abbreviated and full name of this isotope
     */
    public String get_Name() { return get_FullName() + " (" + get_AbbrName() + ")"; }

    /**
     * Getter function to get this isotope's database search name
     *
     * @return the database search name of this isotope
     */
    public String get_DBName() { return _DBName.get(); }

    /**
     * Getter function to get this isotope's abbreviated name
     *
     * @return the abbreviated name of this isotope
     */
    public String get_AbbrName() { return dbEditor.getAbbr(_Name.get()); }

    /**
     * Getter function to get this isotope's full name
     *
     * @return the full name of this isotope
     */
    public String get_FullName() { return dbEditor.getFullName(_Name.get()); }

    /**
     * Getter function to get this isotope's initial activity in microCi
     * 
     * @return the initial activity of this isotope in microCi
     */
    public float get_A0() { return _A0.get(); }

    /**
     * Getter function to get today's activity in microCi
     *
     * @return today's activity of this isotope in microCi
     */
    public String get_AToday() { return calculateAToday() + " \u00B5Ci"; }


    /**
     * Getter function to get this isotope's mass in grams/liters
     *
     * @return the mass of this isotope in grams/liters
     */
    public float getMassValue() { return _Mass.get(); }

    /**
     * Getter function to get this isotope's mass in grams/liters
     *
     * @return the mass of this isotope in grams/liters
     */
    public String get_Mass() { return _Mass.get() + " (" + get_MassUnit() + ")"; }

    /**
     * Getter function to get this isotope's mass unit
     *
     * @return the mass unit of this isotope either grams or liters
     */
    public String get_MassUnit() { return _MassUnit.get(); }

    /**
     * Getter function to get this isotope's nature
     *
     * @return the nature of this isotope
     */
    public String get_Nature() { return _Nature.get(); }

    /**
     * Getter function to get this isotope's state
     *
     * @return the state of this isotope
     */
    public String get_State() { return _State.get(); }

    /**
     * Getter function to get this isotope's form
     *
     * @return the form of this isotope
     */
    public String get_Form() { return _Form.get(); }
    
    /**
     * Getter function to get this isotope's current concentration value (as of today)
     * in microCi/gram or microCi/liter
     *
     * @return the current concentration as of today of this isotope in microCi/gram or microCi/liter
     */
    public float get_ConcentrationToday() { return _ConcentrationToday.get(); }

    /**
     * Getter function to get this isotope's decays per minute
     *
     * @return the decays per minute of this isotope
     */
    public float get_DecaysPerMinute() { return _DecaysPerMinute.get(); }

    /**
     * Getter function to get this isotope's activity concentration in (Bq)
     * Activity Concentration = Activity of isotope / sum of entire package concentration
     *
     * @return the activity concentration of this isotope
     */
    public float get_ActivityConcentration() { return _ActivityConcentration.get(); }

    /**
     * Getter function to get this isotope's initial activity fraction in Bq
     * Activity Fraction = Activity fraction of isotope / sum of entire package activity
     *
     * @return the activity fraction of this isotope
     */
    public float get_ActivityFraction() { return _ActivityFraction.get(); }

    /**
     * Getter function to get this isotope's limited quantity multiplier
     * from 173.425_Table 4
     *
     * @return the limited quantity multiplier of this isotope
     */
    public float get_LimitedQuanMultiplier() { return _LimitedQuanMultiplier.get(); }

    /**
     * Getter function to get this isotope's reportable quantity fraction
     * Reportable Quantity Fraction = Activity Today (microCi) / Reportable Quantity (microCuries)
     *
     * @return the reportable quantity fraction of this isotope
     */
    public float get_ReportableQuanFraction() { return _ReportableQuanFraction.get(); }

    /**
     * Getter function to get this isotope's activity percentage of the limit
     * Limit Percentage = (activity of isotope / activity limit of isotope)
     *
     * @return the activity percentage of this isotope
     */
    public float get_LimitPercentage() { return _LimitPercentage.get(); }

    /**
     * Getter function to get this isotope's activity concentration of the limit
     * Concentration Percentage = activity concentration of isotope / activity concentration limit of isotope
     *
     * @return the activity concentration percentage of this isotope
     */
    public float get_ConcentrationPercentage() { return _ConcentrationPercentage.get(); }

    /**
     * Getter function to get this isotope's percentage of licensing limit
     * License percentage activity of isotope / license_limit (from Info table in database)
     *
     * @return the percentage of licensing limit of this isotope
     */
    public float get_LicensePercentage() { return _LicensePercentage.get(); }

    /**
     * Getter function to get this isotope's A1 or A2 value (TBq)
     * For Special Form Isotopes: A1 value (TBq)
     * For Normal Form Isotopes: A2 value (TBq)
     *
     * @return the A1 or A2 value (TBq) of this isotope
     */
    public float get_ALimit() { return _ALimit.get(); }

    /**
     * Getter function to get this isotope's Highway Route Control Quantity Limit
     * Highway Route Control Quantity Limit = 1000 * TBq
     *                                            or
     *                                       3000 * A1/A2
     *                                 (whichever is the lowest number)
     *
     * @return the Highways Route Control Quantity Limit of this isotope
     */
    public float get_HRCQLimit() { return _HRCQLimit.get(); }

    /**
     * Getter function to get whether this isotope is fissile or not
     *
     * @return true if isotope is fissile, else false
     */
    public boolean get_IsFissile() { return _IsFissile.get(); }

    /**
     * Getter function to get whether this isotope is a reportable quantity or not
     *
     * @return true if isotope is a reportable quantity, else false
     */
    public boolean get_IsReportableQuan() { return _IsReportableQuan.get(); }

    /**
     * Getter function to get if this is the isotope with the longer half life or the shorter one
     *
     * @return if this is the isotope with the longer half life or the shorter one
     */
    public String get_ShortLong() { return _ShortLong.get(); }

    /**
     * Getter function to get this isotope's lung absorption rate (slow/medium/fast)
     *
     * @return this isotope's lung absorption rate
     */
    public String get_LungAbs() { return _LungAbs.get(); }

    /**
     * Getter function to get this isotope's classification as an integer
     *  0 = Exempt Classification
     *  1 = Excepted/Limited Classification
     *  2 = Type A Classification
     *  4 = Type B Classification
     *  8 = Type B: Highway Route Control Classification
     *
     * @return the classification of this isotope as an integer
     */
    public int get_IsotopeClass() { return _IsotopeClass.get(); }

    /**
     * Getter function to get this isotope's A1 value (from Info table in database)
     *
     * @return the A1 value of this isotope (from Info table in database)
     */
    public float get_A1() { return _A1.get(); }

    /**
     * Getter function to get this isotope's A2 value (from Info table in database)
     *
     * @return the A2 value of this isotope (from Info table in database)
     */
    public float get_A2() { return _A2.get(); }

    /**
     * Getter function to get this isotope's decay constant (from Info table in database)
     *
     * @return the decay constant of this isotope (from Info table in database)
     */
    public float get_DecayConstant() { return _DecayConstant.get(); }

    /**
     * Getter function to get this isotope's exempt concentration (from Info table in database)
     *
     * @return the exempt concentration of this isotope (from Info table in database)
     */
    public float get_ExemptConcentration() { return _ExemptConcentration.get(); }

    /**
     * Getter function to get this isotope's exempt limit (from Info table in database)
     *
     * @return the exempt limit of this isotope (from Info table in database)
     */
    public float get_ExemptLimit() { return _ExemptLimit.get(); }

    /**
     * Getter function to get this isotope's halflife (from Info table in database)
     *
     * @return the halflife of this isotope (from Info table in database)
     */
    public String get_HalfLife() { return _HalfLife.get() + " days"; }

    /**
     * Getter function to get this isotope's halflife (from Info table in database)
     *
     * @return the halflife of this isotope (from Info table in database)
     */
    public float getHalfLifeValue() { return _HalfLife.get(); }

    /**
     * Getter function to get this isotope's instruments/articles limited limit (from Info table in database)
     *
     * @return the instruments/articles limited limit of this isotope (from Info table in database)
     */
    public float get_IALimitedMultiplier() { return _IALimitedMultiplier.get(); }

    /**
     * Getter function to get this isotope's license limit (from Info table in database)
     *
     * @return the license limit of this isotope (from Info table in database)
     */
    public float get_LicenseLimit() { return _LicenseLimit.get(); }

    /**
     * Getter function to get this isotope's limited limit (from Info table in database)
     *
     * @return the limited limit of this isotope (from Info table in database)
     */
    public float get_LimitedLimit() { return _LimitedLimit.get(); }

    /**
     * Getter function to get this isotope's reportable quantity limit (from Info table in database)
     *
     * @return the reportable quantity limit of this isotope (from Info table in database)
     */
    public float get_ReportableQuan() { return _ReportableQuan.get(); }
    
    /*/////////////////////////////////////////////////// SETTERS ////////////////////////////////////////////////////*/

    /**
     * Setter function to set this isotope's name
     *
     * @param name the new name of this isotope
     */
    public void set_Name(String name) { _Name.set(name); }

    /**
     * Setter function to set this isotope's name
     *
     * @param dbName the new name of the isotope
     */
    public void set_DBName(String dbName) { _DBName.set(dbName); }

    /**
     * Setter function to set this isotope's reference date
     *
     * @param date the new reference date of this isotope
     */
    public void set_RefDate(java.time.LocalDate date) { _RefDate = new LocalDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth()); }

    /**
     * Setter function to set this isotope's initial activity in microCi
     *
     * @param A0 the new initial activity of this isotope in microCi
     */
    public void set_A0(float A0) { _A0.set(A0); }

    /**
     * Setter function to set this isotope's mass unit
     *
     * @param massUnit the new mass unit (grams/liters) of this isotope
     */
    public void set_MassUnit(String massUnit) { _MassUnit.set(massUnit); }

    /**
     * Setter function to set this isotope's current concentration value (as of today)
     * in microCi/gram or microCi/liter
     *
     * @param CToday the NEW current concentration as of today of this isotope in microCi/gram or microCi/liter
     */
    public void set_ConcentrationToday(float CToday) { _ConcentrationToday.set(CToday); }

    /**
     * Setter function to set this isotope's decays per minute
     *
     * @param dpm the new decays per minute of this isotope
     */
    public void set_DecaysPerMinute(float dpm) { _DecaysPerMinute.set(dpm); }

    /**
     * Setter function to set this isotope's activity concentration in (Bq)
     * Activity Concentration = Activity of isotope / sum of entire package concentration
     *
     * @param ACon the new activity concentration of this isotope
     */
    public void set_ActivityConcentration(float ACon) { _ActivityConcentration.set(ACon); }

    /**
     * Setter function to set this isotope's initial activity fraction in Bq
     * Activity Fraction = Activity fraction of isotope / sum of entire package activity
     *
     * @param Afrac the new activity fraction of this isotope
     */
    public void set_ActivityFraction(float Afrac ) { _ActivityFraction.set(Afrac); }

    /**
     * Setter function to set this isotope's limited quantity multiplier
     * from 173.425_Table 4
     *
     * @param LimMult the new limited quantity multiplier of this isotope
     */
    public void set_LimitedQuanMultiplier(float LimMult) { _LimitedQuanMultiplier.set(LimMult); }

    /**
     * Setter function to set this isotope's reportable quantity fraction
     * Reportable Quantity Fraction = Activity Today (microCi) / Reportable Quantity (microCuries)
     *
     * @param RQFrac the new reportable quantity fraction of this isotope
     */
    public void set_ReportableQuanFraction(float RQFrac) { _ReportableQuanFraction.set(RQFrac); }

    /**
     * Setter function to set this isotope's activity percentage of the limit
     * Limit Percentage = (activity of isotope / activity limit of isotope)
     *
     * @param LimPer the new activity percentage of this isotope
     */
    public void set_LimitPercentage(float LimPer) { _LimitPercentage.set(LimPer); }

    /**
     * Setter function to set this isotope's activity concentration of the limit
     * Concentration Percentage = activity concentration of isotope / activity concentration limit of isotope
     *
     * @param ConPer the new activity concentration percentage of this isotope
     */
    public void set_ConcentrationPercentage(float ConPer) { _ConcentrationPercentage.set(ConPer); }

    /**
     * Setter function to set this isotope's percentage of licensing limit
     * License percentage activity of isotope / license_limit (from Info table in database)
     *
     * @param LicPer the new percentage of licensing limit of this isotope
     */
    public void set_LicensePercentage(float LicPer) { _LicensePercentage.set(LicPer); }

    /**
     * Setter function to set this isotope's A1 or A2 value (TBq)
     * For Special Form Isotopes: A1 value (TBq)
     * For Normal Form Isotopes: A2 value (TBq)
     *
     * @param ALim the new A1 or A2 value (TBq) of this isotope
     */
    public void set_ALimit(float ALim) { _ALimit.set(ALim); }

    /**
     * Setter function to set this isotope's Highway Route Control Quantity Limit
     * Highway Route Control Quantity Limit = 1000 * TBq
     *                                            or
     *                                       3000 * A1/A2
     *                                 (whichever is the lowest number)
     *
     * @param HRCQLim the new Highways Route Control Quantity Limit of this isotope
     */
    public void set_HRCQLimit(float HRCQLim) { _HRCQLimit.set(HRCQLim); }

    /**
     * Setter function to set whether this isotope is fissile or not
     *
     * @param isFissile whether this isotope is fissile or not
     */
    public void set_IsFissile(boolean isFissile) { _IsFissile.set(isFissile); }

    /**
     * Setter function to set whether this isotope is a reportable quantity or not
     *
     * @param isRQ whether this isotope is a reportable quantity or not
     */
    public void set_IsReportableQuan(boolean isRQ) { _IsReportableQuan.set(isRQ); }

    /**
     * Setter function to set if this is the isotope with the longer half life or the shorter one
     *
     * @param shortLong if this is the isotope with the longer half life or the shorter one
     */
    public void set_ShortLong(String shortLong) { _ShortLong.set(shortLong); }

    /**
     * Setter function to set this isotope's lung absorption rate (slow/medium/fast)
     *
     * @param lungAbs the new lung absorption rate for this isotope (slow/medium/fast)
     */
    public void set_LungAbs(String lungAbs) { _LungAbs.set(lungAbs); }

    /**
     * Setter function to set this isotope's classification as an integer
     *  0 = Exempt Classification
     *  1 = Excepted/Limited Classification
     *  2 = Type A Classification
     *  4 = Type B Classification
     *  8 = Type B: Highway Route Control Classification
     *
     * @param isoClass the new classification of this isotope as an integer
     */
    public void set_IsotopeClass(int isoClass) { _IsotopeClass.set(isoClass); }

    /**
     * Setter function to set this isotope's A1 value (from Info table in database)
     *
     * @param A1 the new A1 value of this isotope (from Info table in database)
     */
    public void set_A1(float A1) { _A1.set(A1); }

    /**
     * Setter function to set this isotope's A2 value (from Info table in database)
     *
     * @param A2 the new A2 value of this isotope (from Info table in database)
     */
    public void set_A2(float A2) { _A2.set(A2); }

    /**
     * Setter function to set this isotope's decay constant (from Info table in database)
     *
     * @param decayCon he new decay constant of this isotope (from Info table in database)
     */
    public void set_DecayConstant(float decayCon) { _DecayConstant.set(decayCon); }

    /**
     * Setter function to set this isotope's exempt concentration (from Info table in database)
     *
     * @param ExemptCon the new exempt concentration of this isotope (from Info table in database)
     */
    public void set_ExemptConcentration(float ExemptCon) { _ExemptConcentration.set(ExemptCon); }

    /**
     * Setter function to set this isotope's exempt limit (from Info table in database)
     *
     * @param ExemptLim the new exempt limit of this isotope (from Info table in database)
     */
    public void set_ExemptLimit(float ExemptLim) { _ExemptLimit.set(ExemptLim); }

    /**
     * Setter function to set this isotope's halflife (from Info table in database)
     *
     * @param halfLife the new halflife of this isotope (from Info table in database)
     */
    public void set_HalfLife(float halfLife) { _HalfLife.set(halfLife); }

    /**
     * Setter function to set this isotope's instruments/articles limited limit (from Info table in database)
     *
     * @param IAMult the new instruments/articles limited limit of this isotope (from Info table in database)
     */
    public void set_IALimitedMultiplier(float IAMult) { _IALimitedMultiplier.set(IAMult); }

    /**
     * Setter function to set this isotope's license limit (from Info table in database)
     *
     * @param LicLim the new license limit of this isotope (from Info table in database)
     */
    public void set_LicenseLimit(float LicLim) { _LicenseLimit.set(LicLim); }

    /**
     * Setter function to set this isotope's limited limit (from Info table in database)
     *
     * @param LimLim the new limited limit of this isotope (from Info table in database)
     */
    public void set_LimitedLimit(float LimLim) { _LimitedLimit.set(LimLim); }

    /**
     * Setter function to set this isotope's reportable quantity limit (from Info table in database)
     *
     * @param RQ the new reportable quantity limit of this isotope (from Info table in database)
     */
    public void set_ReportableQuan(float RQ) { _ReportableQuan.set(RQ); }
}

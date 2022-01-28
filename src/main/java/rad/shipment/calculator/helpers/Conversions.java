package rad.shipment.calculator.helpers;

/* source: REMM(Radiation Emergency Medical Management) https://www.remm.nlm.gov/radmeasurement.htm
 * #################|           SI Units            |      Common Units     |
 * Radioactivity    |   becquerel           (Bq)    |   curie       (Ci)    |
 * Absorbed Dose    |   gray                (Gy)    |   rad                 |
 * Dose Equivalent  |   sievert             (Sv)    |   rem                 |
 * Exposure         |   coulomb/kilogram    (C/kg)  |   roentgen    (R)     |
 * ##########################################################################
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.InvalidParameterException;

public class Conversions {
    // Declaring variables
    private static final int _BASE = 10;
    private static final float YOTTA = (float)Math.pow(_BASE, 24);   // Y
    private static final float ZETTA = (float)Math.pow(_BASE, 21);   // Z
    private static final float EXA = (float)Math.pow(_BASE, 18);   // E
    private static final float PETA = (float)Math.pow(_BASE, 15);   // P
    private static final float TERA = (float)Math.pow(_BASE, 12);   // T
    private static final float GIGA = (float)Math.pow(_BASE, 9);    // G
    private static final float MEGA = (float)Math.pow(_BASE, 6);    // M
    private static final float KILO = (float)Math.pow(_BASE, 3);    // k
    private static final float HECTO = (float)Math.pow(_BASE, 2);    // h
    private static final float DEKA = (float)Math.pow(_BASE, 1);    // da
    private static final float DECI = (float)Math.pow(_BASE, -1);   // d
    private static final float CENTI = (float)Math.pow(_BASE, -2);   // c
    private static final float MILLI = (float)Math.pow(_BASE, -3);   // m
    private static final float MICRO = (float)Math.pow(_BASE, -6);   // MICRO
    private static final float NANO = (float)Math.pow(_BASE, -9);   // n
    private static final float PICO = (float)Math.pow(_BASE, -12);  // p
    private static final float FEMTO = (float)Math.pow(_BASE, -15);  // f
    private static final float ATTO = (float)Math.pow(_BASE, -18);  // a
    private static final float ZEPTO = (float)Math.pow(_BASE, -21);  // z
    private static final float YOCTO = (float)Math.pow(_BASE, -24);  // y
    public static final int DEFAULT_RAD_SI_INDEX = 14;
    public static final int DEFAULT_RAD_INDEX = 1;
    public static final int DEFAULT_MASS_SI_INDEX = 10;
    public static final int DEFAULT_MASS_INDEX = 0;
    private static final ObservableList<String> siPrefixes =
            FXCollections.observableArrayList("Yotta (Y)",
                                                    "Zetta (Z)",
                                                    "Exa (E)",
                                                    "Peta (P)",
                                                    "Tera (T)",
                                                    "Giga (G)",
                                                    "Mega (M)",
                                                    "Kilo (k)",
                                                    "Hecto (h)",
                                                    "Deka (da)",
                                                    "----",
                                                    "Deci (d)",
                                                    "Centi (c)",
                                                    "Milli (m)",
                                                    "Micro (\u00B5)",
                                                    "Milli (m)",
                                                    "Pico (p)",
                                                    "Femto (f)",
                                                    "Atto (a)",
                                                    "Zepto (z)",
                                                    "Yocto (y)"
            );
    private static final ObservableList<String> radioactivityUnits = FXCollections.observableArrayList("Becquerel (Bq)", "Curie (Ci)");
    private static final ObservableList<String> massUnits = FXCollections.observableArrayList("grams", "liters");


    /*////////////////////////////////////////////////// HELPERS /////////////////////////////////////////////////////*/
    /**
     * Helper function to get the names of all the si prefixes that can be converted
     *
     * @return a list of all the si prefixes that can be converted
     */
    public static ObservableList<String> getSiPrefixes() { return siPrefixes; }

    /**
     * Helper function to get the names of all the si units that can be converted
     *
     * @return a list of all the si units that can be converted
     */
    public static ObservableList<String> getRadioactivityUnits() { return radioactivityUnits; }

    /**
     * Helper function to get the names of all the si units that can be converted
     *
     * @return a list of all the si units that can be converted
     */
    public static ObservableList<String> getMassUnits() { return massUnits; }

    /**
     * Helper function to convert a given value to it's base value using its starting si prefix
     *
     * @param value the value to be converted
     * @param prefixIndex the index of the selected si prefix
     * @return the converted base value
     */
    public static float convertToBase(float value, int prefixIndex) {
        switch(prefixIndex) {
            case 0:  // user selected yotta
                return Conversions.YottaToBase(value);
            case 1:  // user selected zetta
                return Conversions.ZettaToBase(value);
            case 2:  // user selected exa
                return Conversions.ExaToBase(value);
            case 3:  // user selected peta
                return Conversions.PetaToBase(value);
            case 4:  // user selected tera
                return Conversions.TeraToBase(value);
            case 5:  // user selected giga
                return Conversions.GigaToBase(value);
            case 6:  // user selected mega
                return Conversions.MegaToBase(value);
            case 7:  // user selected kilo
                return Conversions.KiloToBase(value);
            case 8:  // user selected hecto
                return Conversions.HectoToBase(value);
            case 9:  // user selected deka
                return Conversions.DekaToBase(value);
            case 10:  // user selected base
                return value;
            case 11:  // user selected deci
                return Conversions.DeciToBase(value);
            case 12:  // user selected centi
                return Conversions.CentiToBase(value);
            case 13:  // user selected milli
                return Conversions.MilliToBase(value);
            case 14:  // user selected micro
                return Conversions.MicroToBase(value);
            case 15:  // user selected nano
                return Conversions.NanoToBase(value);
            case 16:  // user selected pico
                return Conversions.PicoToBase(value);
            case 17:  // user selected femto
                return Conversions.FemtoToBase(value);
            case 18:  // user selected atto
                return Conversions.AttoToBase(value);
            case 19:  // user selected zepto
                return Conversions.ZeptoToBase(value);
            case 20:  // user selected yocto
                return Conversions.YoctoToBase(value);
            default:
                return value;
        }
    }

    /**
     * Helper function to convert a given value to it's micro value using its starting si prefix
     *
     * @param value the value to be converted
     * @param siPrefix the current siprefix of the given value
     * @return the converted micro value
     */
    public static float convertToMicro(float value, String siPrefix) {
        if("".equals(siPrefix) || siPrefix == null) throw new InvalidParameterException("Invalid SI Prefix");

        switch(siPrefix) {
            case "Yotta (Y)": return Conversions.baseToMicro(Conversions.YottaToBase(value));
            case "Zetta (Z)": return Conversions.baseToMicro(Conversions.ZettaToBase(value));
            case "Exa (E)": return Conversions.baseToMicro(Conversions.ExaToBase(value));
            case "Peta (P)": return Conversions.baseToMicro(Conversions.PetaToBase(value));
            case "Tera (T)": return Conversions.baseToMicro(Conversions.TeraToBase(value));
            case "Giga (G)": return Conversions.baseToMicro(Conversions.GigaToBase(value));
            case "Mega (M)": return Conversions.baseToMicro(Conversions.MegaToBase(value));
            case "Kilo (k)": return Conversions.baseToMicro(Conversions.KiloToBase(value));
            case "Hecto (h)": return Conversions.baseToMicro(Conversions.HectoToBase(value));
            case "Deka (da)": return Conversions.baseToMicro(Conversions.DekaToBase(value));
            case "Deci (d)": return Conversions.baseToMicro(Conversions.DeciToBase(value));
            case "Centi (c)": return Conversions.baseToMicro(Conversions.CentiToBase(value));
            case "Milli (m)": return Conversions.baseToMicro(Conversions.MilliToBase(value));
            case "Nano (n)": return Conversions.baseToMicro(Conversions.NanoToBase(value));
            case "Pico (p)": return Conversions.baseToMicro(Conversions.PicoToBase(value));
            case "Femto (f)": return Conversions.baseToMicro(Conversions.FemtoToBase(value));
            case "Atto (a)": return Conversions.baseToMicro(Conversions.AttoToBase(value));
            case "Zepto (z)": return Conversions.baseToMicro(Conversions.ZeptoToBase(value));
            case "Yocto (y)": return Conversions.baseToMicro(Conversions.YoctoToBase(value));
            default: return value;
        }
    }
    /*//////////////////////////////////////////////// CONVERSIONS ///////////////////////////////////////////////////*/
    /**
     * Function to convert the given becquerel to curies
     * 
     * @param bq the becquerel value to be converted
     * @return the converted curie value
     */
    public static float BqToCi(float bq) { return bq * (float)(2.7 * (float)Math.pow(_BASE, -11)); }

    /**
     * Function to convert the given becquerel to becquerels
     *
     * @param ci the curie value to be converted
     * @return the converted becquerel value
     */
    public static float CiToBq(float ci) { return ci * (float)(3.7 * (float)Math.pow(_BASE, _BASE));}

    /**
     * Function to convert the given gray to rads
     *
     * @param gy the gray value to be converted
     * @return the converted rad value
     */
    public static float GyToRad(float gy) { return gy * (float)Math.pow(_BASE, 2); }

    /**
     * Function to convert the given rad to grays
     *
     * @param rad the rad value to be converted
     * @return the converted gray value
     */
    public static float RadToGy(float rad) { return rad * (float)Math.pow(_BASE, -2); }

    /**
     * Function to convert the given sievert to rems
     *
     * @param sv the sievert value to be converted
     * @return the converted rem value
     */
    public static float SvToRem(float sv) { return sv * (float)Math.pow(_BASE, 2); }

    /**
     * Function to convert the given rem to sieverts
     *
     * @param rem the rem value to be converted
     * @return the converted sievert value
     */
    public static float RemToSv(float rem) { return rem * (float)Math.pow(_BASE, -2); }

    /**
     * Function to convert the given coulomb/kilogram to roentgens
     *
     * @param c_kg the coulomb/kilogram value to be converted
     * @return the converted roentgen value
     */
    public static float C_kgToR(float c_kg) { return c_kg * (float)(3.88 * (float)Math.pow(_BASE, 3)); }

    /**
     * Function to convert the given roentgen to coulombs/kilogram
     *
     * @param R the roentgen value to be converted
     * @return the converted coulomb/kilogram value
     */
    public static float RToC_kg(float R) { return R * (float)(2.58 * (float)Math.pow(_BASE, -4)); }

    /*/////////////////////////////////////////////////// HELPERS ////////////////////////////////////////////////////*/
    /**
     * Helper function to convert a _BASE unit value to a YOTTA unit value
     * 
     * @param base the value of the _BASE to be converted
     * @return the YOTTA unit value of the given _BASE
     */
    public static float baseToYotta(float base) { return base * YOCTO; }

    /**
     * Helper function to convert a _BASE unit value to a ZETTA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the ZETTA unit value of the given _BASE
     */
    public static float baseToZetta(float base) { return base * ZEPTO; }

    /**
     * Helper function to convert a _BASE unit value to a EXA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the EXA unit value of the given _BASE
     */
    public static float baseToExa(float base) { return base * ATTO; }

    /**
     * Helper function to convert a _BASE unit value to a PETA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the PETA unit value of the given _BASE
     */
    public static float baseToPeta(float base) { return base * FEMTO; }

    /**
     * Helper function to convert a _BASE unit value to a TERA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the TERA unit value of the given _BASE
     */
    public static float baseToTera(float base) { return base * PICO; }

    /**
     * Helper function to convert a _BASE unit value to a GIGA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the GIGA unit value of the given _BASE
     */
    public static float baseToGiga(float base) { return base * NANO; }

    /**
     * Helper function to convert a _BASE unit value to a MEGA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the MEGA unit value of the given _BASE
     */
    public static float baseToMega(float base) { return base * MICRO; }

    /**
     * Helper function to convert a _BASE unit value to a KILO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the KILO unit value of the given _BASE
     */
    public static float baseToKilo(float base) { return base * MILLI; }

    /**
     * Helper function to convert a _BASE unit value to a HECTO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the HECTO unit value of the given _BASE
     */
    public static float baseToHecto(float base) { return base * CENTI; }

    /**
     * Helper function to convert a _BASE unit value to a DEKA unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the DEKA unit value of the given _BASE
     */
    public static float baseToDeka(float base) { return base * DECI; }

    /**
     * Helper function to convert a _BASE unit value to a DECI unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the DECI unit value of the given _BASE
     */
    public static float baseToDeci(float base) { return base * DEKA; }

    /**
     * Helper function to convert a _BASE unit value to a CENTI unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the CENTI unit value of the given _BASE
     */
    public static float baseToCenti(float base) { return base * HECTO; }

    /**
     * Helper function to convert a _BASE unit value to a MILLI unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the MILLI unit value of the given _BASE
     */
    public static float baseToMilli(float base) { return base * KILO; }

    /**
     * Helper function to convert a _BASE unit value to a MICRO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the MICRO unit value of the given _BASE
     */
    public static float baseToMicro(float base) { return base * MEGA; }

    /**
     * Helper function to convert a _BASE unit value to a NANO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the NANO unit value of the given _BASE
     */
    public static float baseToNano(float base) { return base * GIGA; }

    /**
     * Helper function to convert a _BASE unit value to a PICO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the PICO unit value of the given _BASE
     */
    public static float baseToPico(float base) { return base * TERA; }

    /**
     * Helper function to convert a _BASE unit value to a FEMTO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the FEMTO unit value of the given _BASE
     */
    public static float baseToFemto(float base) { return base * PETA; }

    /**
     * Helper function to convert a _BASE unit value to a ATTO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the ATTO unit value of the given _BASE
     */
    public static float baseToAtto(float base) { return base * EXA; }

    /**
     * Helper function to convert a _BASE unit value to a ZEPTO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the ZEPTO unit value of the given _BASE
     */
    public static float baseToZepto(float base) { return base * ZETTA; }

    /**
     * Helper function to convert a _BASE unit value to a YOCTO unit value
     *
     * @param base the value of the _BASE to be converted
     * @return the YOCTO unit value of the given _BASE
     */
    public static float baseToYocto(float base) { return base * YOTTA; }

    /**
     * Helper function to convert a YOTTA unit value to a _BASE unit value
     * 
     * @param yotta the YOTTA unit value to be converted
     * @return the _BASE unit value of the given YOTTA
     */
    public static float YottaToBase(float yotta) { return yotta * Conversions.YOTTA; }

    /**
     * Helper function to convert a ZETTA unit value to a _BASE unit value
     *
     * @param zetta the ZETTA unit value to be converted
     * @return the _BASE unit value of the given ZETTA
     */
    public static float ZettaToBase(float zetta) { return zetta * Conversions.ZETTA; }

    /**
     * Helper function to convert a EXA unit value to a _BASE unit value
     *
     * @param exa the EXA unit value to be converted
     * @return the _BASE unit value of the given EXA
     */
    public static float ExaToBase(float exa) { return exa * Conversions.EXA; }

    /**
     * Helper function to convert a PETA unit value to a _BASE unit value
     *
     * @param peta the PETA unit value to be converted
     * @return the _BASE unit value of the given PETA
     */
    public static float PetaToBase(float peta) { return peta * Conversions.PETA; }

    /**
     * Helper function to convert a TERA unit value to a _BASE unit value
     *
     * @param tera the TERA unit value to be converted
     * @return the _BASE unit value of the given TERA
     */
    public static float TeraToBase(float tera) { return tera * Conversions.TERA; }

    /**
     * Helper function to convert a GIGA unit value to a _BASE unit value
     *
     * @param giga the GIGA unit value to be converted
     * @return the _BASE unit value of the given GIGA
     */
    public static float GigaToBase(float giga) { return giga * Conversions.GIGA; }

    /**
     * Helper function to convert a MEGA unit value to a _BASE unit value
     *
     * @param mega the MEGA unit value to be converted
     * @return the _BASE unit value of the given MEGA
     */
    public static float MegaToBase(float mega) { return mega * Conversions.MEGA; }

    /**
     * Helper function to convert a KILO unit value to a _BASE unit value
     *
     * @param kilo the KILO unit value to be converted
     * @return the _BASE unit value of the given KILO
     */
    public static float KiloToBase(float kilo) { return kilo * Conversions.KILO; }

    /**
     * Helper function to convert a HECTO unit value to a _BASE unit value
     *
     * @param hecto the HECTO unit value to be converted
     * @return the _BASE unit value of the given HECTO
     */
    public static float HectoToBase(float hecto) { return hecto * Conversions.HECTO; }

    /**
     * Helper function to convert a DEKA unit value to a _BASE unit value
     *
     * @param deka the DEKA unit value to be converted
     * @return the _BASE unit value of the given DEKA
     */
    public static float DekaToBase(float deka) { return deka * Conversions.DEKA; }

    /**
     * Helper function to convert a DECI unit value to a _BASE unit value
     *
     * @param deci the DECI unit value to be converted
     * @return the _BASE unit value of the given DECI
     */
    public static float DeciToBase(float deci) { return deci * Conversions.DECI; }

    /**
     * Helper function to convert a CENTI unit value to a _BASE unit value
     *
     * @param centi the CENTI unit value to be converted
     * @return the _BASE unit value of the given CENTI
     */
    public static float CentiToBase(float centi) { return centi * Conversions.CENTI; }

    /**
     * Helper function to convert a MILLI unit value to a _BASE unit value
     *
     * @param milli the MILLI unit value to be converted
     * @return the _BASE unit value of the given MILLI
     */
    public static float MilliToBase(float milli) { return milli * Conversions.MILLI; }

    /**
     * Helper function to convert a MICRO unit value to a _BASE unit value
     *
     * @param micro the MICRO unit value to be converted
     * @return the _BASE unit value of the given MICRO
     */
    public static float MicroToBase(float micro) { return micro * Conversions.MICRO; }

    /**
     * Helper function to convert a NANO unit value to a _BASE unit value
     *
     * @param nano the NANO unit value to be converted
     * @return the _BASE unit value of the given NANO
     */
    public static float NanoToBase(float nano) { return nano * Conversions.NANO; }

    /**
     * Helper function to convert a PICO unit value to a _BASE unit value
     *
     * @param pico the PICO unit value to be converted
     * @return the _BASE unit value of the given PICO
     */
    public static float PicoToBase(float pico) { return pico * Conversions.PICO; }

    /**
     * Helper function to convert a FEMTO unit value to a _BASE unit value
     *
     * @param femto the FEMTO unit value to be converted
     * @return the _BASE unit value of the given FEMTO
     */
    public static float FemtoToBase(float femto) { return femto * Conversions.FEMTO; }

    /**
     * Helper function to convert a ATTO unit value to a _BASE unit value
     *
     * @param atto the ATTO unit value to be converted
     * @return the _BASE unit value of the given ATTO
     */
    public static float AttoToBase(float atto) { return atto * Conversions.ATTO; }

    /**
     * Helper function to convert a ZEPTO unit value to a _BASE unit value
     *
     * @param zepto the ZEPTO unit value to be converted
     * @return the _BASE unit value of the given ZEPTO
     */
    public static float ZeptoToBase(float zepto) { return zepto * Conversions.ZEPTO; }

    /**
     * Helper function to convert a YOCTO unit value to a _BASE unit value
     *
     * @param yocto the YOCTO unit value to be converted
     * @return the _BASE unit value of the given YOCTO
     */
    public static float YoctoToBase(float yocto) { return yocto * Conversions.YOCTO; }
}

package rad.shipment.calculator.helpers;

public class FileHandler {
    public static String getValidIsotopesCSVPath() { return "classpath:csv/ValidIsotopes.csv"; }
    public static String getShortLongCSVPath() { return "classpath:csv/ShortLong.csv"; }
    public static String getA1CSVPath() { return "classpath:csv/A1(TBq).csv"; }
    public static String getA2CSVPath() { return "classpath:csv/A2(TBq).csv"; }
    public static String getDecayConstCSVPath() { return "classpath:csv/Decay_Constant.csv"; }
    public static String getExemptConCSVPath() { return "classpath:csv/Exempt_concentration(Bq-g).csv"; }
    public static String getExemptLimCSVPath() { return "classpath:csv/Exempt_limit(Bq).csv"; }
    public static String getHalfLifeCSVPath() { return "classpath:csv/Half_Life(days).csv"; }
    public static String getIALimLimCSVPath() { return "classpath:csv/IA_Limited_limit.csv"; }
    public static String getIAPackageLimCSVPath() { return "classpath:csv/IA_Package_limit.csv"; }
    public static String getLicLimCSVPath() { return "classpath:csv/Licensing_Limit(microCi).csv"; }
    public static String getLimLimCSVPath() { return "classpath:csv/Limited_limit.csv"; }
    public static String getReportQCSVPath() { return "classpath:csv/Reportable_Quantity(TBq).csv"; }
}

package rad.shipment.calculator.helpers;

import org.junit.Assert;
import org.junit.Test;

public class FileHandlerTest {

    @Test
    public void getValidIsotopesCSVPath() {
        Assert.assertEquals(
                "classpath:csv/ValidIsotopes.csv",
                FileHandler.getValidIsotopesCSVPath()
        );
    }

    @Test
    public void getA1CSVPath() {
        Assert.assertEquals(
                "classpath:csv/A1(TBq).csv",
                FileHandler.getA1CSVPath()
        );
    }

    @Test
    public void getA2CSVPath() {
        Assert.assertEquals(
                "classpath:csv/A2(TBq).csv",
                FileHandler.getA2CSVPath()
        );
    }

    @Test
    public void getDecayConstCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Decay_Constant.csv",
                FileHandler.getDecayConstCSVPath()
        );
    }

    @Test
    public void getExemptConCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Exempt_concentration(Bq-g).csv",
                FileHandler.getExemptConCSVPath()
        );
    }

    @Test
    public void getExemptLimCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Exempt_limit(Bq).csv",
                FileHandler.getExemptLimCSVPath()
        );
    }

    @Test
    public void getHalfLifeCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Half_Life(days).csv",
                FileHandler.getHalfLifeCSVPath()
        );
    }

    @Test
    public void getIALimLimCSVPath() {
        Assert.assertEquals(
                "classpath:csv/IA_Limited_limit.csv",
                FileHandler.getIALimLimCSVPath()
        );
    }

    @Test
    public void getIAPackageLimCSVPath() {
        Assert.assertEquals(
                "classpath:csv/IA_Package_limit.csv",
                FileHandler.getIAPackageLimCSVPath()
        );
    }

    @Test
    public void getLicLimCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Licensing_Limit(microCi).csv",
                FileHandler.getLicLimCSVPath()
        );
    }

    @Test
    public void getLimLimCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Limited_limit.csv",
                FileHandler.getLimLimCSVPath()
        );
    }

    @Test
    public void getReportQCSVPath() {
        Assert.assertEquals(
                "classpath:csv/Reportable_Quantity(TBq).csv",
                FileHandler.getReportQCSVPath()
        );
    }
}
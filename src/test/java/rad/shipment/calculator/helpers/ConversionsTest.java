package rad.shipment.calculator.helpers;

import org.junit.Assert;
import org.junit.Test;

public class ConversionsTest {
    @Test
    public void bqToCi() {
        float expected = (float)2.7e-11;
        float actual = Conversions.BqToCi(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void ciToBq() {
        float expected = (float)3.7e10;
        float actual = Conversions.CiToBq(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void gyToRad() {
        float expected = (float)1e2;
        float actual = Conversions.GyToRad(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void radToGy() {
        float expected = (float)1e-2;
        float actual = Conversions.RadToGy(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void svToRem() {
        float expected = (float)1e2;
        float actual = Conversions.SvToRem(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void remToSv() {
        float expected = (float)1e-2;
        float actual = Conversions.RemToSv(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void c_kgToR() {
        float expected = (float)3.88e3;
        float actual = Conversions.C_kgToR(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void RToC_kg() {
        float expected = (float)2.58e-4;
        float actual = Conversions.RToC_kg(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToYotta() {
        float expected = (float)1e-24;
        float actual = Conversions.baseToYotta(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToZetta() {
        float expected = (float)1e-21;
        float actual = Conversions.baseToZetta(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToExa() {
        float expected = (float)1e-18;
        float actual = Conversions.baseToExa(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToPeta() {
        float expected = (float)1e-15;
        float actual = Conversions.baseToPeta(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToTera() {
        float expected = (float)1e-12;
        float actual = Conversions.baseToTera(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToGiga() {
        float expected = (float)1e-9;
        float actual = Conversions.baseToGiga(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToMega() {
        float expected = (float)1e-6;
        float actual = Conversions.baseToMega(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToKilo() {
        float expected = (float)1e-3;
        float actual = Conversions.baseToKilo(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToHecto() {
        float expected = (float)1e-2;
        float actual = Conversions.baseToHecto(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToDeka() {
        float expected = (float)1e-1;
        float actual = Conversions.baseToDeka(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToDeci() {
        float expected = (float)1e1;
        float actual = Conversions.baseToDeci(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToCenti() {
        float expected = (float)1e2;
        float actual = Conversions.baseToCenti(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToMilli() {
        float expected = (float)1e3;
        float actual = Conversions.baseToMilli(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToMicro() {
        float expected = (float)1e6;
        float actual = Conversions.baseToMicro(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToNano() {
        float expected = (float)1e9;
        float actual = Conversions.baseToNano(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToPico() {
        float expected = (float)1e12;
        float actual = Conversions.baseToPico(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToFemto() {
        float expected = (float)1e15;
        float actual = Conversions.baseToFemto(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToAtto() {
        float expected = (float)1e18;
        float actual = Conversions.baseToAtto(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToZepto() {
        float expected = (float)1e21;
        float actual = Conversions.baseToZepto(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void baseToYocto() {
        float expected = (float)1e24;
        float actual = Conversions.baseToYocto(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void yottaToBase() {
        float expected = (float)1e24;
        float actual = Conversions.YottaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void zettaToBase() {
        float expected = (float)1e21;
        float actual = Conversions.ZettaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void exaToBase() {
        float expected = (float)1e18;
        float actual = Conversions.ExaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void petaToBase() {
        float expected = (float)1e15;
        float actual = Conversions.PetaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void teraToBase() {
        float expected = (float)1e12;
        float actual = Conversions.TeraToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void gigaToBase() {
        float expected = (float)1e9;
        float actual = Conversions.GigaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void megaToBase() {
        float expected = (float)1e6;
        float actual = Conversions.MegaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void kiloToBase() {
        float expected = (float)1e3;
        float actual = Conversions.KiloToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void hectoToBase() {
        float expected = (float)1e2;
        float actual = Conversions.HectoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void dekaToBase() {
        float expected = (float)1e1;
        float actual = Conversions.DekaToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void deciToBase() {
        float expected = (float)1e-1;
        float actual = Conversions.DeciToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void centiToBase() {
        float expected = (float)1e-2;
        float actual = Conversions.CentiToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void milliToBase() {
        float expected = (float)1e-3;
        float actual = Conversions.MilliToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void microToBase() {
        float expected = (float)1e-6;
        float actual = Conversions.MicroToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void nanoToBase() {
        float expected = (float)1e-9;
        float actual = Conversions.NanoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void picoToBase() {
        float expected = (float)1e-12;
        float actual = Conversions.PicoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void femtoToBase() {
        float expected = (float)1e-15;
        float actual = Conversions.FemtoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void attoToBase() {
        float expected = (float)1e-18;
        float actual = Conversions.AttoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void zeptoToBase() {
        float expected = (float)1e-21;
        float actual = Conversions.ZeptoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void yoctoToBase() {
        float expected = (float)1e-24;
        float actual = Conversions.YoctoToBase(1);

        Assert.assertEquals(expected, actual, 0);
    }
}
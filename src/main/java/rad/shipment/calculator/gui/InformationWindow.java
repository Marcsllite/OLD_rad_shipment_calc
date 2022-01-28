package rad.shipment.calculator.gui;

public class InformationWindow extends Window {
    public boolean display(String title, String message) {
        if(title == null || "".equals(title)) title = Main.getString("defaultInformationTitle");

        var btnOkay = super.createButton("Okay", Main.getString("okayBtnID"), true);  // creating Yes button

        return super.display(title, message, btnOkay);
    }
}
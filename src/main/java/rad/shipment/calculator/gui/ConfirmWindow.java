package rad.shipment.calculator.gui;

public class ConfirmWindow extends Window {

    public boolean display(String title, String message) {
        if(title == null || "".equals(title)) title = Main.getString("defaultConfirmTitle");

        var btnYes = super.createButton("Yes", Main.getString("positiveBtnID"), true);  // creating Yes button
        var btnNo = super.createButton("No", Main.getString("negativeBtnID"), false);  // creating No button

        return super.display(title, message, btnYes, btnNo);
    }
}
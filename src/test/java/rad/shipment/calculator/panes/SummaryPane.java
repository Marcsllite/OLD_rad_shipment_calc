package rad.shipment.calculator.panes;

import rad.shipment.calculator.gui.Main;
import rad.shipment.calculator.gui.SummaryPaneControllerTest;

public class SummaryPane {
    private final SummaryPaneControllerTest driver;

    public SummaryPane(SummaryPaneControllerTest driver) {
        this.driver = driver;
        if (!driver.getPrimaryStage().getTitle().equals(Main.getString("summaryPane"))) {
            throw new IllegalArgumentException("Did not receive the Summary Pane to test Summary");
        }
    }

    /*//////////////////////////////////////////////// SUMMARY PANE //////////////////////////////////////////////////*/

    /*///////////////////////////////////////////////// CONVENIENCE //////////////////////////////////////////////////*/

    /*/////////////////////////////////////////////////// GETTERS ////////////////////////////////////////////////////*/
}

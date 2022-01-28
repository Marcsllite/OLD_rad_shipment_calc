package rad.shipment.calculator.helpers;

import javafx.util.StringConverter;
import rad.shipment.calculator.gui.Main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerConverter extends StringConverter<LocalDate> {
    // Default Date Pattern
    private String pattern = Main.getString("datePattern");
    // The Date Time Converter
    private DateTimeFormatter dtFormatter;

    public DatePickerConverter()
    {
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    public DatePickerConverter(String pattern)
    {
        this.pattern = pattern;
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    // Change String to LocalDate
    public LocalDate fromString(String text)
    {
        LocalDate date = null;

        if (text != null && !text.trim().isEmpty())
        {
            date = LocalDate.parse(text, dtFormatter);
        }

        return date;
    }

    // Change LocalDate to String
    public String toString(LocalDate date)
    {
        String text = null;

        if (date != null)
        {
            text = dtFormatter.format(date);
        }

        return text;
    }
}

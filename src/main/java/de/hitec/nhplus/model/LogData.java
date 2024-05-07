package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class LogData {
    private LocalDate date;

    private String changes;

    /**
     * Constructor to initiate an object of class <code>LogData</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a treatment id (tid).
     *
     * @param date Date of the changes.
     * @param changes Changes that were made.
     */

    public LogData(LocalDate date, String changes){
        this.date = date;
        this.changes = changes;
    }

    //Getter
    public LocalDate getDate() {return date;}

    public String getChanges() {return changes;}

    //Setter
    public void setDate(String date) {this.date = DateConverter.convertStringToLocalDate(date);}

    public void setChanges(String changes) {this.changes = changes; }

    public String toString(){
        return "\nDatum" + this.date +
                "\nÄnderungen" + this.changes + "\n";
    }
}

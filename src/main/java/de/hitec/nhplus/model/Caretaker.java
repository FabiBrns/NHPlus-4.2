package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Caretakers work in a NURSING home and are treating patients.
 */
public class Caretaker extends Person {
    private SimpleLongProperty cid;

    private SimpleStringProperty phoneNumber;

    private final List<Treatment> allTreatments = new ArrayList<>();

    /**
     * Constructor to initiate an object of class <code>Caretaker</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a caretaker id (cid).
     *
     * @param firstName First name of the caretaker.
     * @param surname   Last name of the caretaker.
     */
    public Caretaker(String firstName, String surname, String phoneNumber, LocalDateTime timeUpdated, boolean locked) {
        super(firstName, surname, timeUpdated, locked);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    /**
     * Constructor to initiate an object of class <code>Caretaker</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a caretaker id (cid).
     *
     * @param cid         caretaker id.
     * @param firstName   First name of the caretaker.
     * @param surname     Last name of the caretaker.
     * @param phoneNumber phoneNumber of the caretaker.
     */
    public Caretaker(long cid, String firstName, String surname, String phoneNumber, LocalDateTime timeUpdated, boolean locked) {
        super(firstName, surname, timeUpdated, locked);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public long getCid() {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public List<Treatment> getAllTreatments() {
        return allTreatments;
    }

    /**
     * Adds a treatment to the list of treatments, if the list does not already contain the treatment.
     *
     * @param treatment Treatment to add.
     * @return False, if the treatment was already part of the list, else true.
     */

    public boolean add(Treatment treatment) {
        if (this.allTreatments.contains(treatment)) {
            return false;
        }
        this.allTreatments.add(treatment);
        return true;
    }

    public String toString() {
        return "Caretaker" +
               "\nID: " + this.cid +
               "\nFirstname: " + this.getFirstName() +
               "\nSurname: " + this.getSurname() +
               "\nPhone: " + this.phoneNumber +
               "\n";
    }
}
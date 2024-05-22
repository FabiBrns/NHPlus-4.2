package de.hitec.nhplus.model;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.DefaultConstants;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Person {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty surname;
    private final SimpleStringProperty timeUpdated;
    private final SimpleBooleanProperty locked;


    public Person(String firstName, String surname, LocalDateTime timeUpdated, boolean locked) {
        this.firstName = new SimpleStringProperty(firstName);
        this.surname = new SimpleStringProperty(surname);
        this.timeUpdated = new SimpleStringProperty(DateConverter.convertLocalDateTimeToString(timeUpdated));
        this.locked = new SimpleBooleanProperty(locked);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getTimeUpdated() {
        return timeUpdated.get();
    }

    public SimpleStringProperty timeUpdatedProperty() {
        return timeUpdated;
    }

    public void setTimeUpdated(LocalDateTime timeUpdated) {
        this.timeUpdated.set(DateConverter.convertLocalDateTimeToString(timeUpdated));
    }

    public boolean isLocked() {
        return locked.get();
    }

    public SimpleBooleanProperty lockedProperty() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    public void checkForDeletion() {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime dateTimeLastUpdated = DateConverter
                .convertStringToLocalDateTime(String.valueOf(this.timeUpdated.getValue()));

        if (dateTimeLastUpdated.until(dateTimeNow, ChronoUnit.YEARS) >= DefaultConstants.TIME_UNTIL_DELETION_OF_DATA_IN_YEARS) {
            if (this instanceof Patient) {
                try {
                    DaoFactory.getDaoFactory().createPatientDAO().deleteById(((Patient) this).getPid());
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if (this instanceof Caretaker) {
                try {
                    DaoFactory.getDaoFactory().createCaretakerDAO().deleteById(((Caretaker) this).getCid());
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}

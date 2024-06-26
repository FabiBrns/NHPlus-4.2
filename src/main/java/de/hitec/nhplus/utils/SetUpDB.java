package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static de.hitec.nhplus.utils.DateConverter.*;

/**
 * Call static class provides to static methods to set up and wipe the database. It uses the class ConnectionBuilder
 * and its path to build up the connection to the database. The class is executable. Executing the class will build
 * up a connection to the database and calls setUpDb() to wipe the database, build up a clean database and fill the
 * database with some test data.
 */
public class SetUpDB {

    /**
     * This method wipes the database by dropping the tables. Then the method calls DDL statements to build it up from
     * scratch and DML statements to fill the database with hard coded test data.
     */
    public static void setUpDb() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpDB.wipeDb(connection);
        SetUpDB.setUpTableUser(connection);
        SetUpDB.setUpTablePatient(connection);
        SetUpDB.setUpTableCaretaker(connection);
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpUsers();
        SetUpDB.setUpPatients();
        SetUpDB.setUpCaretakers();
        SetUpDB.setUpTreatments();
    }

    /**
     * This method wipes the database by dropping the tables.
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE user");
            statement.execute("DROP TABLE patient");
            statement.execute("DROP TABLE caretaker");
            statement.execute("DROP TABLE treatment");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTableUser(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS user (uid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL, password TEXT NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTablePatient(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS patient (pid INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT NOT NULL, surname TEXT NOT NULL, dateOfBirth TEXT NOT NULL, carelevel TEXT NOT NULL, roomnumber TEXT NOT NULL, timeUpdated TEXT NOT NULL, locked INTEGER NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTableCaretaker(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS caretaker (cid INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT NOT NULL, surname TEXT NOT NULL, phonenumber TEXT NOT NULL, timeUpdated TEXT NOT NULL, locked INTEGER NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTableTreatment(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS treatment (tid INTEGER PRIMARY KEY AUTOINCREMENT, pid INTEGER NOT NULL, cid INTEGER NOT NULL, treatment_date TEXT NOT NULL, begin TEXT NOT NULL, end TEXT NOT NULL, description TEXT NOT NULL, remark TEXT NOT NULL, FOREIGN KEY (pid) REFERENCES patient (pid) ON DELETE CASCADE FOREIGN KEY (cid) REFERENCES caretaker (cid) ON DELETE CASCADE);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpUsers() {
        try {
            UserDao dao = DaoFactory.getDaoFactory().createUserDAO();
            dao.create(new User("Fabian", "1234"));
            dao.create(new User("Jannik", "1234"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpPatients() {
        try {
            PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
            dao.create(new Patient("Seppl", "Herberger", convertStringToLocalDate("1945-12-01"), "4", "202", convertStringToLocalDateTime("1990-02-22 11:01:59"), false));
            dao.create(new Patient("Martina", "Gerdsen", convertStringToLocalDate("1954-08-12"), "5", "010", convertStringToLocalDateTime("2012-12-04 15:12:43"), false));
            dao.create(new Patient("Gertrud", "Franzen", convertStringToLocalDate("1949-04-16"), "3", "002", convertStringToLocalDateTime("2007-05-09 09:47:52"), false));
            dao.create(new Patient("Ahmet", "Yilmaz", convertStringToLocalDate("1941-02-22"), "3", "013", convertStringToLocalDateTime("2013-02-26 17:08:22"), false));
            dao.create(new Patient("Hans", "Neumann", convertStringToLocalDate("1955-12-12"), "2", "001", convertStringToLocalDateTime("2004-01-20 19:06:54"), false));
            dao.create(new Patient("Elisabeth", "Müller", convertStringToLocalDate("1958-03-07"), "5", "110", convertStringToLocalDateTime("2018-08-21 03:34:25"), false));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpCaretakers() {
        try {
            CaretakerDao dao = DaoFactory.getDaoFactory().createCaretakerDAO();
            dao.create(new Caretaker("Fabian", "Bruens", "+49123456789", convertStringToLocalDateTime("2003-02-22 11:01:59"), false));
            dao.create(new Caretaker("Abdul Latif", "Islambouli", "+49234567891", convertStringToLocalDateTime("2024-01-22 01:01:01"), false));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTreatments() {
        try {
            TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
            dao.create(new Treatment(1, 1, 1, convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Der Patient hat enorme Angstgefühle und glaubt, er sei überfallen worden. Ihm seien alle Wertsachen gestohlen worden.\nPatient beruhigt sich erst, als alle Wertsachen im Zimmer gefunden worden sind."));
            dao.create(new Treatment(2, 1, 1, convertStringToLocalDate("2023-06-05"), convertStringToLocalTime("11:00"), convertStringToLocalTime("12:30"), "Gespräch", "Patient irrt auf der Suche nach gestohlenen Wertsachen durch die Etage und bezichtigt andere Bewohner des Diebstahls.\nPatient wird in seinen Raum zurückbegleitet und erhält Beruhigungsmittel."));
            dao.create(new Treatment(3, 2, 1, convertStringToLocalDate("2023-06-04"), convertStringToLocalTime("07:30"), convertStringToLocalTime("08:00"), "Waschen", "Patient mit Waschlappen gewaschen und frisch angezogen. Patient gewendet."));
            dao.create(new Treatment(4, 1, 2, convertStringToLocalDate("2023-06-06"), convertStringToLocalTime("15:10"), convertStringToLocalTime("16:00"), "Spaziergang", "Spaziergang im Park, Patient döst  im Rollstuhl ein"));
            dao.create(new Treatment(8, 1, 2, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("16:00"), "Spaziergang", "Parkspaziergang; Patient ist heute lebhafter und hat klare Momente; erzählt von seiner Tochter"));
            dao.create(new Treatment(9, 2, 2, convertStringToLocalDate("2023-06-07"), convertStringToLocalTime("11:00"), convertStringToLocalTime("11:30"), "Waschen", "Waschen per Dusche auf einem Stuhl; Patientin gewendet;"));
            dao.create(new Treatment(12, 5, 2, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("15:30"), "Physiotherapie", "Übungen zur Stabilisation und Mobilisierung der Rückenmuskulatur"));
            dao.create(new Treatment(14, 4, 1, convertStringToLocalDate("2023-08-24"), convertStringToLocalTime("09:30"), convertStringToLocalTime("10:15"), "KG", "Lympfdrainage"));
            dao.create(new Treatment(16, 6, 2, convertStringToLocalDate("2023-08-31"), convertStringToLocalTime("13:30"), convertStringToLocalTime("13:45"), "Toilettengang", "Hilfe beim Toilettengang; Patientin klagt über Schmerzen beim Stuhlgang. Gabe von Iberogast"));
            dao.create(new Treatment(17, 6, 1, convertStringToLocalDate("2023-09-01"), convertStringToLocalTime("16:00"), convertStringToLocalTime("17:00"), "KG", "Massage der Extremitäten zur Verbesserung der Durchblutung"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SetUpDB.setUpDb();
    }
}

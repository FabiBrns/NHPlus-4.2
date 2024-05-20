package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Implements the Interface <code>DaoImp</code>. Overrides methods to generate specific <code>PreparedStatements</code>,
 * to execute the specific SQL Statements.
 */
public class CaretakerDao extends DaoImp<Caretaker> {

    /**
     * The constructor initiates an object of <code>PatientDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public CaretakerDao(Connection connection) {
        super(connection);
    }

    /**
     * Generates a <code>PreparedStatement</code> to persist the given object of <code>Caretaker</code>.
     *
     * @param caretaker Object of <code>Caretaker</code> to persist.
     * @return <code>PreparedStatement</code> to insert the given caretaker.
     */
    @Override
    protected PreparedStatement getCreateStatement(Caretaker caretaker) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO caretaker (firstname, surname, phonenumber, timeUpdated, locked) " +
                               "VALUES (?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caretaker.getFirstName());
            preparedStatement.setString(2, caretaker.getSurname());
            preparedStatement.setString(3, caretaker.getPhoneNumber());
            preparedStatement.setString(4, caretaker.getTimeUpdated());
            preparedStatement.setBoolean(5, caretaker.isLocked());

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to query a caretaker by a given caretaker id (cid).
     *
     * @param cid Caretaker id to query.
     * @return <code>PreparedStatement</code> to query the caretaker.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long cid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caretaker WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, cid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Maps a <code>ResultSet</code> of one caretaker to an object of <code>Caretaker</code>.
     *
     * @param result ResultSet with a single row. Columns will be mapped to an object of class <code>Caretaker</code>.
     * @return Object of class <code>Caretaker</code> with the data from the resultSet.
     */
    @Override
    protected Caretaker getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Caretaker(
                result.getInt(1),
                result.getString(2),
                result.getString(3),
                result.getString(4),
                DateConverter.convertStringToLocalDateTime(result.getString(5)),
                result.getBoolean(6));
    }

    /**
     * Generates a <code>PreparedStatement</code> to query all caretakers.
     *
     * @return <code>PreparedStatement</code> to query all caretakers.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM caretaker";
            statement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    /**
     * Maps a <code>ResultSet</code> of all patients to an <code>ArrayList</code> of <code>Caretaker</code> objects.
     *
     * @param result ResultSet with all rows. The Columns will be mapped to objects of class <code>Caretaker</code>.
     * @return <code>ArrayList</code> with objects of class <code>Caretaker</code> of all rows in the
     * <code>ResultSet</code>.
     */
    @Override
    protected ArrayList<Caretaker> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Caretaker> list = new ArrayList<>();
        while (result.next()) {
            LocalDateTime timeUpdated = DateConverter.convertStringToLocalDateTime(result.getString(5));
            Caretaker caretaker = new Caretaker(
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    timeUpdated,
                    result.getBoolean(6));
            list.add(caretaker);
        }
        return list;
    }

    /**
     * Generates a <code>PreparedStatement</code> to update the given caretaker, identified
     * by the id of the caretaker (cid).
     *
     * @param caretaker Caretaker object to update.
     * @return <code>PreparedStatement</code> to update the given caretaker.
     */
    @Override
    protected PreparedStatement getUpdateStatement(Caretaker caretaker) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL =
                    "UPDATE caretaker SET " +
                    "firstname = ?, " +
                    "surname = ?, " +
                    "phonenumber = ?, " +
                    "timeUpdated = ?, " +
                    "locked = ? " +
                    "WHERE cid = ? AND locked = false";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caretaker.getFirstName());
            preparedStatement.setString(2, caretaker.getSurname());
            preparedStatement.setString(3, caretaker.getPhoneNumber());
            preparedStatement.setString(4, caretaker.getTimeUpdated());
            preparedStatement.setBoolean(5, caretaker.isLocked());
            preparedStatement.setLong(6, caretaker.getCid());

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to delete a caretaker with the given id.
     *
     * @param cid ID of the caretaker to delete.
     * @return <code>PreparedStatement</code> to delete caretaker with the given id.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long cid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM caretaker WHERE cid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, cid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }
}

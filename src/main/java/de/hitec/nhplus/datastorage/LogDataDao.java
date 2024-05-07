package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.LogData;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Interface <code>DaoImp</code>. Overrides methods to generate specific <code>PreparedStatements</code>,
 * to execute the specific SQL Statements.
 */
public class LogDataDao extends DaoImp<LogData> {

    /**
     * The constructor initiates an object of <code>TreatmentDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public LogDataDao(Connection connection) {
        super(connection);
    }

    @Override
    protected LogData getInstanceFromResultSet(ResultSet set) throws SQLException {
        return null;
    }

    @Override
    protected ArrayList<LogData> getListFromResultSet(ResultSet set) throws SQLException {
        return null;
    }

    @Override
    protected PreparedStatement getCreateStatement(LogData logData) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO Log (date, changes) " +
                    "VALUES (?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setDate(1, Date.valueOf(logData.getDate()));
            preparedStatement.setString(2, logData.getChanges());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        return null;
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM Log";
            statement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    @Override
    protected PreparedStatement getUpdateStatement(LogData logData) {
        return null;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        return null;
    }
}
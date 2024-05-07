package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.LogDataDao;
import de.hitec.nhplus.model.LogData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class LogDataController {

    @FXML
    private TableView<LogData> tableView;

    @FXML
    private TableColumn<LogData, LocalDate> columnDate;

    @FXML
    private TableColumn<LogData, String> columnchanges;

    private final ObservableList<LogData> logData = FXCollections.observableArrayList();
    private LogDataDao dao;

    public void initialize() {
        readAllAndShowInTableView();

        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnchanges.setCellValueFactory(new PropertyValueFactory<>("changes"));

        this.tableView.setItems(this.logData);
    }

    public void readAllAndShowInTableView() {
        this.logData.clear();
        this.dao = DaoFactory.getDaoFactory().createLogDataDao();
        try {
            this.logData.addAll(dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}

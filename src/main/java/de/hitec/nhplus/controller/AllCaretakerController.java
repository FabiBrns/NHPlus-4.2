package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaretakerDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.model.Patient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.time.LocalDateTime;


/**
 * The <code>AllPatientController</code> contains the entire logic of the patient view. It determines which data is displayed and how to react to events.
 */
public class AllCaretakerController {

    @FXML
    private TableView<Caretaker> tableView;

    @FXML
    private TableColumn<Caretaker, Integer> columnId;

    @FXML
    private TableColumn<Caretaker, String> columnFirstName;

    @FXML
    private TableColumn<Caretaker, String> columnSurname;

    @FXML
    private TableColumn<Caretaker, String> columnPhoneNumber;

    @FXML
    private TableColumn<Patient, String> columnTimeUpdated;

    @FXML
    private Button buttonLock;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldPhoneNumber;

    private final ObservableList<Caretaker> caretakers = FXCollections.observableArrayList();
    private CaretakerDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));

        // CellValueFactory to show property values in TableView
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // CellFactory to write property values from with in the TableView
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        this.columnPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnTimeUpdated.setCellValueFactory(new PropertyValueFactory<>("timeUpdated"));
        this.columnTimeUpdated.setCellFactory(TextFieldTableCell.forTableColumn());

        //Anzeigen der Daten
        this.tableView.setItems(this.caretakers);

        this.buttonLock.setDisable(true); // LOCK NOT DELETE
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Caretaker>() {
            @Override
            public void changed(ObservableValue<? extends Caretaker> observableValue, Caretaker oldCaretaker, Caretaker newCaretaker) {
                AllCaretakerController.this.buttonLock.setDisable(newCaretaker == null /* LOCK NOT DELETE!! */);
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCaretakerListener = (observableValue, oldText, newText) -> AllCaretakerController.this.buttonAdd.setDisable(!AllCaretakerController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputNewCaretakerListener);
        this.textFieldFirstName.textProperty().addListener(inputNewCaretakerListener);
    }

    /**
     * When a cell of the column with first names was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<Caretaker, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with surnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Caretaker, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with assets was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditPhoneNumber(TableColumn.CellEditEvent<Caretaker, String> event) {
        event.getRowValue().setPhoneNumber(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * Updates a patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private void doUpdate(TableColumn.CellEditEvent<Caretaker, String> event) {
        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reloads all patients to the table by clearing the list of all patients and filling it again by all persisted
     * patients, delivered by {@link PatientDao}.
     */
    private void readAllAndShowInTableView() {
        this.caretakers.clear();
        this.dao = DaoFactory.getDaoFactory().createCaretakerDAO();
        try {
            this.caretakers.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles events fired by the button to delete patients. It calls {@link PatientDao} to delete the
     * patient from the database and removes the object from the list, which is the data source of the
     * <code>TableView</code>.
     */
    @FXML
    public void handleLock() {
        Caretaker selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createCaretakerDAO().deleteById(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * This method handles the events fired by the button to add a patient. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Caretaker</code> of it and passes the object to
     * {@link PatientDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String phoneNumber = this.textFieldPhoneNumber.getText();
        LocalDateTime timeUpdated = LocalDateTime.now();
        try {
            this.dao.create(new Caretaker(firstName, surname, phoneNumber, timeUpdated));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldPhoneNumber.clear();
    }

    private boolean areInputDataValid() {
        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() && !this.textFieldPhoneNumber.getText().isBlank();
    }
}

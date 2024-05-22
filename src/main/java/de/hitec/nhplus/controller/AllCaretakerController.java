package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaretakerDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Person;
import javafx.beans.value.ChangeListener;
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
import java.util.List;


/**
 * The <code>AllCaretakerController</code> contains the entire logic of the caretaker view. It determines which data is displayed and how to react to events.
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

        // Check for deletion-time + delete if necessary
        this.caretakers.forEach(Person::checkForDeletion);

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

        this.buttonLock.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldCaretaker, newCaretaker) -> {
            AllCaretakerController.this.buttonLock.setDisable(newCaretaker == null);
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
        this.updateTimeUpdated(event);
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
        this.updateTimeUpdated(event);
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
        this.updateTimeUpdated(event);
        this.doUpdate(event);
    }

    /**
     * sets the parameter lastUpdated of the current caretaker to LocalDateTime.now().
     *
     * @param event Event including the changed object and the change.
     */
    private void updateTimeUpdated(TableColumn.CellEditEvent<Caretaker, String> event) {
        event.getRowValue().setTimeUpdated(LocalDateTime.now());
    }

    /**
     * Updates a caretaker by calling the method <code>update()</code> of {@link CaretakerDao}.
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
     * Reloads all caretakers to the table by clearing the list of all caretakers and filling it again by all persisted, not locked
     * caretakers, delivered by {@link CaretakerDao}.
     */
    private void readAllAndShowInTableView() {
        this.caretakers.clear();
        this.dao = DaoFactory.getDaoFactory().createCaretakerDAO();
        try {
            List<Caretaker> allUnlockedCaretakers = this.dao.readAll().stream()
                    .filter(c -> !c.isLocked())
                    .toList();

            this.caretakers.addAll(allUnlockedCaretakers);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles events fired by the button to lock caretakers. It calls {@link CaretakerDao} to set the
     * parameter "locked" in the db on true and removes the object from the list, which is the data source of the
     * <code>TableView</code>.
     */
    @FXML
    public void handleLock() {
        Caretaker selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                selectedItem.setLocked(true);
                this.tableView.getItems().remove(selectedItem);
                dao.update(selectedItem);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method handles the events fired by the button to add a caretaker. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Caretaker</code> of it and passes the object to
     * {@link CaretakerDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String phoneNumber = this.textFieldPhoneNumber.getText();
        LocalDateTime timeUpdated = LocalDateTime.now();
        // locked = false on default
        boolean locked = false;
        try {
            this.dao.create(new Caretaker(firstName, surname, phoneNumber, timeUpdated, locked));
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

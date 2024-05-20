package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaretakerDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class NewTreatmentController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelSurname;

    @FXML
    private ComboBox<String> comboBoxCaretakerSelection;

    @FXML
    private TextField textFieldBegin;

    @FXML
    private TextField textFieldEnd;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextArea textAreaRemarks;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button buttonAdd;

    private final ObservableList<String> caretakerSelection = FXCollections.observableArrayList();
    private AllTreatmentController controller;
    private Patient patient;
    private Caretaker caretaker;
    private Stage stage;
    private ArrayList<Caretaker> caretakerList;

    public void initialize(AllTreatmentController controller, Stage stage, Patient patient) {
        comboBoxCaretakerSelection.setItems(caretakerSelection);
        comboBoxCaretakerSelection.getSelectionModel().select(0);

        this.controller = controller;
        this.patient = patient;
        this.stage = stage;
        this.caretaker = null;

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) -> NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid());
        this.textFieldBegin.textProperty().addListener(inputNewPatientListener);
        this.textFieldEnd.textProperty().addListener(inputNewPatientListener);
        this.textFieldDescription.textProperty().addListener(inputNewPatientListener);
        this.textAreaRemarks.textProperty().addListener(inputNewPatientListener);
        this.datePicker.valueProperty().addListener((observableValue, localDate, t1) -> NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid()));
        this.datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                return (localDate == null) ? "" : DateConverter.convertLocalDateToString(localDate);
            }

            @Override
            public LocalDate fromString(String localDate) {
                return DateConverter.convertStringToLocalDate(localDate);
            }
        });
        this.showPatientData();
        this.createComboBoxData();
    }

    private void showPatientData() {
        this.labelFirstName.setText(patient.getFirstName());
        this.labelSurname.setText(patient.getSurname());
    }

    private Caretaker searchInCaretakerList(String surname) {
        for (Caretaker caretaker : this.caretakerList) {
            if (caretaker.getSurname().equals(surname)) {
                return caretaker;
            }
        }
        return null;
    }

    @FXML
    public void handleCaretakerComboBox() {
        try {
            String selectedPatient = this.comboBoxCaretakerSelection.getSelectionModel().getSelectedItem();
            this.caretaker = searchInCaretakerList(selectedPatient);
        } catch (NullPointerException exception) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Patient für die Behandlung fehlt!");
            alert.setContentText("Wählen Sie über die Combobox einen Patienten aus!");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleAdd() {
        LocalDate date = this.datePicker.getValue();
        LocalTime begin = DateConverter.convertStringToLocalTime(textFieldBegin.getText());
        LocalTime end = DateConverter.convertStringToLocalTime(textFieldEnd.getText());
        String description = textFieldDescription.getText();
        String remarks = textAreaRemarks.getText();
        Treatment treatment = new Treatment(patient.getPid(), caretaker.getCid(), date, begin, end, description, remarks);

        // update timestamps till deletion when new treatment created
        patient.setTimeUpdated(LocalDateTime.now());
        caretaker.setTimeUpdated(LocalDateTime.now());

        createTreatment(treatment);


        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void createTreatment(Treatment treatment) {
        TreatmentDao tDao = DaoFactory.getDaoFactory().createTreatmentDao();
        PatientDao pDao = DaoFactory.getDaoFactory().createPatientDAO();
        CaretakerDao cDao = DaoFactory.getDaoFactory().createCaretakerDAO();
        try {
            tDao.create(treatment);
            pDao.update(patient);
            cDao.update(caretaker);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void createComboBoxData() {
        CaretakerDao dao = DaoFactory.getDaoFactory().createCaretakerDAO();
        try {
            caretakerList = (ArrayList<Caretaker>) dao.readAll();
            this.caretakerSelection.add("alle");
            for (Caretaker caretaker : caretakerList) {
                this.caretakerSelection.add(caretaker.getSurname());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel() {
        stage.close();
    }

    private boolean areInputDataInvalid() {
        if (this.textFieldBegin.getText() == null || this.textFieldEnd.getText() == null) {
            return true;
        }
        try {
            LocalTime begin = DateConverter.convertStringToLocalTime(this.textFieldBegin.getText());
            LocalTime end = DateConverter.convertStringToLocalTime(this.textFieldEnd.getText());
            if (!end.isAfter(begin)) {
                return true;
            }
        } catch (Exception exception) {
            return true;
        }
        return this.textFieldDescription.getText().isBlank() || this.datePicker.getValue() == null;
    }
}
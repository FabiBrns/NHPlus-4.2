package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaretakerDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Caretaker;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TreatmentController {

    @FXML
    private Label labelPatientName;

    @FXML
    private Label labelCareLevel;

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

    private AllTreatmentController controller;
    private Stage stage;
    private Patient patient;
    private Caretaker caretaker;
    private Treatment treatment;
    private PatientDao pDao;
    private CaretakerDao cDao;

    public void initializeController(AllTreatmentController controller, Stage stage, Treatment treatment) {
        this.stage = stage;
        this.controller = controller;
        pDao = DaoFactory.getDaoFactory().createPatientDAO();
        cDao = DaoFactory.getDaoFactory().createCaretakerDAO();
        try {
            this.patient = pDao.read((int) treatment.getPid());
            this.caretaker = cDao.read((int) treatment.getCid());
            this.treatment = treatment;
            showData();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void showData() {
        this.labelPatientName.setText(patient.getSurname() + ", " + patient.getFirstName());
        this.labelCareLevel.setText(patient.getCareLevel());
        LocalDate date = DateConverter.convertStringToLocalDate(treatment.getDate());
        this.datePicker.setValue(date);
        this.textFieldBegin.setText(this.treatment.getBegin());
        this.textFieldEnd.setText(this.treatment.getEnd());
        this.textFieldDescription.setText(this.treatment.getDescription());
        this.textAreaRemarks.setText(this.treatment.getRemarks());
    }

    @FXML
    public void handleChange() {
        this.treatment.setDate(this.datePicker.getValue().toString());
        this.treatment.setBegin(textFieldBegin.getText());
        this.treatment.setEnd(textFieldEnd.getText());
        this.treatment.setDescription(textFieldDescription.getText());
        this.treatment.setRemarks(textAreaRemarks.getText());

        // update timestamps till deletion when new treatment created
        this.patient.setTimeUpdated(LocalDateTime.now());
        this.caretaker.setTimeUpdated(LocalDateTime.now());

        doUpdate();

        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void doUpdate() {
        TreatmentDao tDao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            tDao.update(treatment);
            pDao.update(patient);
            cDao.update(caretaker);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel() {
        stage.close();
    }
}
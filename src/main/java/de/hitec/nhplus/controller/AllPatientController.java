package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
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
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.utils.DateConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AllPatientController</code> contains the entire logic of the patient view. It determines which data is displayed and how to react to events.
 */
public class AllPatientController extends AllTreatmentController{

    @FXML
    private TableView<Patient> tableView;

    @FXML
    private TableColumn<Patient, Integer> columnId;

    @FXML
    private TableColumn<Patient, String> columnFirstName;

    @FXML
    private TableColumn<Patient, String> columnSurname;

    @FXML
    private TableColumn<Patient, String> columnDateOfBirth;

    @FXML
    private TableColumn<Patient, String> columnCareLevel;

    @FXML
    private TableColumn<Patient, String> columnRoomNumber;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldDateOfBirth;

    @FXML
    private TextField textFieldCareLevel;

    @FXML
    private TextField textFieldRoomNumber;

    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private PatientDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    public void initialize() {
        this.readAllPatientAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("pid"));

        // CellValueFactory to show property values in TableView
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // CellFactory to write property values from with in the TableView
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        this.columnDateOfBirth.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnCareLevel.setCellValueFactory(new PropertyValueFactory<>("careLevel"));
        this.columnCareLevel.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        this.columnRoomNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        //Anzeigen der Daten
        this.tableView.setItems(this.patients);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Patient>() {
            @Override
            public void changed(ObservableValue<? extends Patient> observableValue, Patient oldPatient, Patient newPatient) {;
                AllPatientController.this.buttonDelete.setDisable(newPatient == null);
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                AllPatientController.this.buttonAdd.setDisable(!AllPatientController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputNewPatientListener);
        this.textFieldFirstName.textProperty().addListener(inputNewPatientListener);
        this.textFieldDateOfBirth.textProperty().addListener(inputNewPatientListener);
        this.textFieldCareLevel.textProperty().addListener(inputNewPatientListener);
        this.textFieldRoomNumber.textProperty().addListener(inputNewPatientListener);
    }

    /**
     * When a cell of the column with first names was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with surnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with dates of birth was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditDateOfBirth(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setDateOfBirth(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with care levels was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditCareLevel(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setCareLevel(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with room numbers was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditRoomNumber(TableColumn.CellEditEvent<Patient, String> event){
        event.getRowValue().setRoomNumber(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * Updates a patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private void doUpdate(TableColumn.CellEditEvent<Patient, String> event) {
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
    private void readAllPatientAndShowInTableView() {
        this.patients.clear();
        this.dao = DaoFactory.getDaoFactory().createPatientDAO();
        try {
            this.patients.addAll(this.dao.readAll());
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
    public void handleDelete() {
        Patient selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createPatientDAO().deleteById(selectedItem.getPid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * This method handles the events fired by the button to add a patient. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Patient</code> of it and passes the object to
     * {@link PatientDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String birthday = this.textFieldDateOfBirth.getText();
        LocalDate date = DateConverter.convertStringToLocalDate(birthday);
        String careLevel = this.textFieldCareLevel.getText();
        String roomNumber = this.textFieldRoomNumber.getText();
        try {
            this.dao.create(new Patient(firstName, surname, date, careLevel, roomNumber));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllPatientAndShowInTableView();
        clearTextfields();
    }

    /**
     *This methode gets the data from the Patient and his Treatments and returns them as an ArrayList.
     */
    public List GetExportData()
    {
        //Get the Patient data from the selected Patient
        long selectedPatient = 0;
        Patient selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createPatientDAO().read(selectedItem.getPid());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        ArrayList PatientAndTreatments = new ArrayList<>();

        //Adding the Patient to the ArrayList
        PatientAndTreatments.add(selectedItem);

        //saving the Patient ID
        selectedPatient = selectedItem.getPid();

        //
        AllTreatmentController allTreatmentController = new AllTreatmentController();
        //allTreatmentController.GetTretmentsByPid(selectedPatient);

        //Adding the Treatments to the ArrayLIst
        PatientAndTreatments.add(allTreatmentController.GetTretmentsByPid(selectedPatient));


        //System.out.println(PatientAndTreatments);
        return PatientAndTreatments;
    }

    public void handleExportPDF()
    {
        List data = GetExportData();
        //System.out.println(data);
    }


    public void handleExportCSV()
    {
        List data = GetExportData();
        //System.out.println(data);

        boolean patient= false;
        boolean treatments = false;
        int count_treatments = 0;
        int count = 0;

        //get number of treatments
        for(int i = 0; i < data.size(); i++)
        {
            if(data.get(i).toString().contains("Behandlung"))
            {
                var data_1 = data.get(i);
                String[] data_ = data_1.toString().replace(" ","").replace("[","").replace("]","").split("\n");

                for(int s = 0; s < data_.length; s++)
                {
                    if(data_[s].contains("Behandlung"))
                    {
                        count_treatments++;
                    }
                }
            }
        }

        String[][][] treatments_ = new String[count_treatments][2][7];
        String[][] patient_ = new String[2][6];

        for(int i = 0; i < data.size(); i++)
        {
            //System.out.println(data.get(i));

            //Check if Patient or Treatments data
            if(data.get(i).toString().contains("Patient"))
            {
                patient = true;
                treatments = false;
                count_treatments = 0;
            }
            if(data.get(i).toString().contains("Behandlung"))
            {
                count = 0;
                count_treatments++;
                patient = false;
                treatments = true;
            }

            if(patient == true)
            {
                var data_1 = data.get(i);
                String[] data_ = data_1.toString().replace("[","").replace("]","").split("\n");

                for(int s = 0; s < data_.length; s++)
                {
                    String[] split_ = data_[s].split(":");

                    //System.out.println(split_.length);

                    if(split_.length == 2)
                    {
                        patient_[0][count] = split_[0];
                        patient_[1][count] = split_[1];
                        System.out.println(2);
                        count++;
                    } else if (split_.length== 3) {
                        System.out.println(3);
                        patient_[0][count] = split_[0];
                        patient_[1][count] = split_[2];
                        count++;
                    }
                }
            }

            if(treatments == true)
            {
                var data_1 = data.get(i);
                String[] data_ = data_1.toString().replace("[","").replace("]","").replace(".\n"," ").split("\n");
                int t = -1;

                    for(int s = 0; s < data_.length; s++)
                    {
                        String[] split_ = data_[s].split(":");

                        if(split_[0].equals("Behandlung"))
                        {
                            t++;
                            count = 0;
                        }

                        if(split_.length == 2)
                        {
                            if(t >= 1)
                            {
                                treatments_[t][1][count] = split_[1];
                            }
                            else
                            {
                                treatments_[t][0][count] = split_[0];
                                treatments_[t][1][count] = split_[1];
                            }

                            count++;
                        }
                        else if (split_.length== 3)
                        {
                            if(t >= 1)
                            {
                                treatments_[t][1][count] = split_[2];
                            }
                            else
                            {
                                treatments_[t][0][count] = split_[0];
                                treatments_[t][1][count] = split_[2];
                            }

                            count++;
                        }


                    }
            }
        }

        //Get save Path
        String user_path = System.getProperty("user.home");

        //Generate file path
        File file = new File(user_path + "\\Downloads\\Patienten daten_" +patient_[1][2] + " " + patient_[1][1]);
        if (!file.exists()){
            file.mkdirs();
        }

        String csv = "";

        //String for patient
        for(int i = 0; i <= 1; i++)
        {
            for(int y = 0; y <= 5; y++)
            {
                csv += patient_[i][y];
                if(y+1 != 6)
                {
                    csv += ";";
                }
                else
                {
                    csv += "\n";
                }
            }
        }

        //Patient daten werden in die Patient CSV datei geschrieben und die Patient.csv wird gespeichert
        try {
            FileWriter fw = new FileWriter(file.getPath() + "\\Patient.csv");
            fw.write(csv);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        csv = "";
        //String for Treatments
        for(int t = 0; t < treatments_.length; t++)
        {
            for(int i = 0; i <= 1; i++)
            {
                for(int y = 0; y <= 6; y++)
                {
                    if(treatments_[t][i][y] != null)
                    {
                        csv += treatments_[t][i][y];
                        if(y+1 != 7)
                        {
                            csv += ";";
                        }
                        else
                        {
                            csv += "\n";
                        }
                    }
                }
            }
        }

        //Treatments daten werden in die Treatments CSV datei geschrieben und die Treatments.csv wird gespeichert
        try {
            FileWriter fw = new FileWriter(file.getPath() + "\\Treatments.csv");
            fw.write(csv);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldDateOfBirth.clear();
        this.textFieldCareLevel.clear();
        this.textFieldRoomNumber.clear();
    }

    private boolean areInputDataValid() {
        if (!this.textFieldDateOfBirth.getText().isBlank()) {
            try {
                DateConverter.convertStringToLocalDate(this.textFieldDateOfBirth.getText());
            } catch (Exception exception) {
                return false;
            }
        }

        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldDateOfBirth.getText().isBlank() && !this.textFieldCareLevel.getText().isBlank() &&
                !this.textFieldRoomNumber.getText().isBlank();
    }
}

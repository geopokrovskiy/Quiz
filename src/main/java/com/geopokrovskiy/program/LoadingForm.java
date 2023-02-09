package com.geopokrovskiy.program;

import java.util.prefs.Preferences;
import com.geopokrovskiy.util.Repository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadingForm {

    public TextField numberOfQuestions;
    public ComboBox<String> category;
    public ComboBox<String> difficulty;
    public Button startButton;
    public ComboBox<String> questionType;

    private HashMap<String, Integer> categories = new HashMap<>();

    private HashMap<String, String> questionTypes = new HashMap<>();

    private Repository repository;

    private Preferences preferences = Preferences.userRoot();

    @FXML
    public void initialize() {
        ArrayList<String> difficultyLevels = new ArrayList<>();
        this.categories.put("History", 23);
        this.categories.put("Geography", 22);
        this.categories.put("Maths", 19);
        this.categories.put("Computers", 18);
        difficultyLevels.add("Easy");
        difficultyLevels.add("Medium");
        difficultyLevels.add("Hard");
        this.questionTypes.put("True/False", "boolean");
        this.questionTypes.put("Multiple Choice", "multiple");
        this.category.setItems(FXCollections.observableList(categories.keySet().stream().toList()));
        this.difficulty.setItems(FXCollections.observableList(difficultyLevels));
        this.questionType.setItems(FXCollections.observableList(questionTypes.keySet().stream().toList()));
    }

    @FXML
    private void saveToFile(ActionEvent actionEvent) throws IOException {
        try {
            if (Integer.parseInt(this.numberOfQuestions.getText()) > 10 || Integer.parseInt(this.numberOfQuestions.getText()) < 1) {
                App.showAlert("Incorrect format", "Number of questions should be between 1 and 10!", Alert.AlertType.ERROR);
            } else {
                String sourcePage = String.format("https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s&type=%s",
                        Integer.parseInt(this.numberOfQuestions.getText()),
                        this.categories.get(this.category.getValue()),
                        this.difficulty.getValue().toLowerCase(),
                        this.questionTypes.get(this.questionType.getValue()));
                this.repository = new Repository(sourcePage);
                this.loadToFile();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            App.showAlert("No data", "Create the quiz before saving it!", Alert.AlertType.ERROR);
        }
    }

    public void loadToFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJson = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        String defaultStr = "default";
        String preferenceDirectory = this.preferences.get(defaultStr, null);
        if(preferenceDirectory != null){
            File defaultInitialDirectory = new File(preferenceDirectory);
            fileChooser.setInitialDirectory(defaultInitialDirectory);
        }
        fileChooser.getExtensionFilters().add(extFilterJson);
        fileChooser.getExtensionFilters().add(extFilterCSV);
        File file = fileChooser.showSaveDialog(null);
        this.repository.save(file);
        this.setPreferences(defaultStr, file);
    }

    public void startQuiz(ActionEvent actionEvent) throws IOException {
        try {
            if (Integer.parseInt(this.numberOfQuestions.getText()) > 10 || Integer.parseInt(this.numberOfQuestions.getText()) < 1) {
                App.showAlert("Incorrect format", "Number of questions should be between 1 and 10!", Alert.AlertType.ERROR);
                return;
            } else {
                String sourcePage = String.format("https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s&type=%s",
                        Integer.parseInt(this.numberOfQuestions.getText()),
                        this.categories.get(this.category.getValue()),
                        this.difficulty.getValue().toLowerCase(),
                        this.questionTypes.get(this.questionType.getValue()));
                this.repository = new Repository(sourcePage);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            App.showAlert("Incorrect format", "Fill the parameters of the quiz before get started!", Alert.AlertType.ERROR);
            return;
        }
        if (this.repository.getResults() != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("GameForm.fxml"));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(
                    new Scene(fxmlLoader.load())
            );
            stage.setTitle("Game Menu");

            GameForm controller = fxmlLoader.getController();
            controller.initData(this.repository);

            Stage oldStage = (Stage) this.category.getScene().getWindow();
            oldStage.close();
            stage.show();
        } else {
            App.showAlert("No Quiz created", "Create the quiz before get started!", Alert.AlertType.ERROR);
        }

    }

    private void setPreferences(String key, File file){
        this.preferences.put(key, file.getParent());
    }
}

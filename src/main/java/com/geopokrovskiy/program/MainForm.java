package com.geopokrovskiy.program;

import com.geopokrovskiy.util.FormData;
import com.geopokrovskiy.util.Repository;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class MainForm {

    public Button fromInternetButton;
    public Button fromFileButton;
    public CheckBox showCorrectAnswersCheckBox;

    private static boolean showCorrectAnswersSelected;

    private Repository repository;

    private Preferences preferences = Preferences.userRoot();


    public void loadFromInternet(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("LoadingForm.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(
                new Scene(fxmlLoader.load())
        );
        stage.setTitle("Loading Menu");

        Stage oldStage = (Stage) this.fromFileButton.getScene().getWindow();
        oldStage.close();
        stage.show();
    }

    public void loadFromFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJson = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilterJson);
        fileChooser.getExtensionFilters().add(extFilterCSV);
        String defaultStr = "default";
        String preferenceDirectory = this.preferences.get(defaultStr, null);
        if(preferenceDirectory != null){
            File defaultInitialDirectory = new File(preferenceDirectory);
            fileChooser.setInitialDirectory(defaultInitialDirectory);
        }
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            this.repository = new Repository(file);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("GameForm.fxml"));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(
                    new Scene(fxmlLoader.load())
            );
            stage.setTitle("Game Menu");
            FormData<Repository> controller = fxmlLoader.getController();
            controller.initData(this.repository);

            Stage oldStage = (Stage) this.fromFileButton.getScene().getWindow();
            oldStage.close();
            stage.show();
            this.setPreferences(defaultStr, file);
        }
    }

    public void showCorrectAnswers(ActionEvent actionEvent) {
        if(this.showCorrectAnswersCheckBox.isSelected()){
            showCorrectAnswersSelected = true;
        }
        else{
            showCorrectAnswersSelected = false;
        }
    }

    public static boolean isShowCorrectAnswersSelected(){
        return showCorrectAnswersSelected;
    }

    private void setPreferences(String key, File file){
        preferences.put(key, file.getParent());
    }
}

package com.geopokrovskiy.program;

import com.geopokrovskiy.util.FormData;
import com.geopokrovskiy.util.Repository;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameForm implements FormData<Repository>{
    public TabPane questionsTabPane;

    private int tabCount;

    private int numberOfIncorrectAnswers;
    private String[] correctAnswers;

    private boolean[] isCorrect;

    private Button checkButton = new Button("Check");

    private Repository repository;

    @Override
    public void initData(Repository repository) {
        this.repository = repository;
        this.tabCount = this.repository.getResults().size();
        this.correctAnswers = new String[tabCount];
        this.isCorrect = new boolean[tabCount];
        this.numberOfIncorrectAnswers = this.repository.getResults().get(0).getIncorrectAnswers().size();
        for(int i = 0; i < this.tabCount; i++){
            correctAnswers[i] = this.repository.getResults().get(i).getCorrectAnswer();
        }
        for(int i = 0; i < this.tabCount - 1; i++){
            Tab tab = new Tab("Question " + (i + 2));
            this.questionsTabPane.getTabs().add(tab);
        }
        Tab tab = new Tab("Results");
        this.questionsTabPane.getTabs().add(tab);
        this.fillTabPane();
        this.fillResultsPane();
        this.checkButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                checkAnswers(mouseEvent);
            }
        });
    }

    private void fillTabPane(){
        for(int i = 0; i < this.tabCount; i++) {
            String question = this.repository.getResults().get(i).getQuestion();
            Label text = new Label(question + "\n");
            text.setFont(Font.font ("Verdana", 15));
            text.maxWidth(20);
            text.setWrapText(true);
            text.maxHeight(5);
            text.prefHeight(5);

            List<RadioButton> radioButtons = new ArrayList<>();
            for(int j = 0; j < this.numberOfIncorrectAnswers; j++){
                RadioButton radioButton = new RadioButton(this.repository.getResults().get(i).getIncorrectAnswers().get(j));
                radioButtons.add(radioButton);
            }
            RadioButton radioButtonCorrect = new RadioButton(this.repository.getResults().get(i).getCorrectAnswer());
            radioButtons.add(radioButtonCorrect);
            ToggleGroup group = new ToggleGroup();
            for(int k = 0; k < radioButtons.size(); k++){
                radioButtons.get(k).setToggleGroup(group);
            }
            Collections.shuffle(radioButtons);
            VBox vBox = new VBox();
            Label label = new Label();
            vBox.getChildren().addAll(radioButtons);
            VBox globalPane = new VBox();
            globalPane.getChildren().addAll(text);
            globalPane.getChildren().add(label);
            globalPane.getChildren().addAll(vBox);
            globalPane.setTranslateX(20);
            questionsTabPane.getTabs().get(i).setContent(globalPane);
        }
    }

    private void fillResultsPane(){
        questionsTabPane.getTabs().get(this.tabCount).setContent(this.checkButton);
    }

    private void checkAnswers(MouseEvent mouseEvent){
        for(int i = 0; i < this.tabCount; i++){
            VBox question = ((VBox) questionsTabPane.getTabs().get(i).getContent());
            boolean atLeastOneSelected = false;
            for(int j = 0; j < this.numberOfIncorrectAnswers + 1; j++){
                VBox vBox = (VBox) question.getChildren().get(2);
                RadioButton button = (RadioButton) vBox.getChildren().get(j);
                if(button.isSelected()){
                    this.isCorrect[i] = (button.getText().equals(this.repository.getResults().get(i).getCorrectAnswer()));
                }
                atLeastOneSelected = atLeastOneSelected || button.isSelected();
            }
            if(!atLeastOneSelected){
                App.showAlert("Answer the questions!", "Not all the questions have been answered!", Alert.AlertType.ERROR);
                return;
            }
        }
        Label statistics = new Label("     Statistics:");
        statistics.setFont(Font.font ("Verdana", 13));
        statistics.setTranslateY(5);
        statistics.setTranslateX(5);
        Label[] answers = new Label[this.tabCount];
        int correctAnswers = 0;
        for(int i = 0 ; i < this.tabCount; i++){
            String correct = (this.isCorrect[i]) ?  "+" : "-";
            if(correct.equals("+")){
                correctAnswers++;
            }
            if(MainForm.isShowCorrectAnswersSelected()){
                correct += " " + this.repository.getResults().get(i).getCorrectAnswer();
            }
            String answer = "Question " + (i + 1) + ": " + correct;
            answers[i] = new Label(answer);
            answers[i].setTranslateY(20 * (i + 1));
            answers[i].setTranslateX(15);
            answers[i].setFont(Font.font("Verdana", 12));
        }
        Label correctIncorrect = new Label("Correct/Incorrect " + correctAnswers + "/" + (this.tabCount - correctAnswers));
        correctIncorrect.setFont(Font.font("Verdana", 12));
        Label rate = new Label("Correct Answer Rate " + ((int)((((double) correctAnswers / (double) this.tabCount) * 1000) / 10) + " %"));
        rate.setFont(Font.font("Verdana", 12));

        correctIncorrect.setTranslateX(15);
        correctIncorrect.setTranslateY(20 * (this.tabCount + 1));
        rate.setTranslateX(15);
        rate.setTranslateY(20 * (this.tabCount + 2));

        VBox vBox = new VBox();
        vBox.getChildren().add(statistics);
        vBox.getChildren().addAll(answers);
        vBox.getChildren().add(correctIncorrect);
        vBox.getChildren().add(rate);
        this.questionsTabPane.getTabs().get(this.tabCount).setContent(vBox);
    }
}

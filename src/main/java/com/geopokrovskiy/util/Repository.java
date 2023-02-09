package com.geopokrovskiy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.model.Questions;
import com.geopokrovskiy.model.Result;
import com.geopokrovskiy.program.App;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private List<Result> results;

    public List<Result> getResults() {
        return this.results;
    }

    public Repository(File file) throws IOException {

        if (file.getName().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                List<Result> encodedResults = objectMapper.readValue(bufferedReader, new TypeReference<>() {
                });
                for (Result result : encodedResults) {
                    String question = XorEncrypting.encryptString(result.getQuestion());
                    question = URLDecoder.decode(question, StandardCharsets.UTF_8);
                    result.setQuestion(question);

                    String correctAnswer = XorEncrypting.encryptString((result.getCorrectAnswer()));
                    correctAnswer = URLDecoder.decode(correctAnswer, StandardCharsets.UTF_8);
                    result.setCorrectAnswer(correctAnswer);

                    List<String> incorrectAnswers = new ArrayList<>();
                    for (String answer : result.getIncorrectAnswers()) {
                        String incorrectAnswer = XorEncrypting.encryptString(answer);
                        incorrectAnswer = URLDecoder.decode(incorrectAnswer, StandardCharsets.UTF_8);
                        incorrectAnswers.add(incorrectAnswer);
                    }
                    result.setIncorrectAnswers(incorrectAnswers);
                }
                this.results = encodedResults;
            }
        } else if (file.getName().endsWith(".csv")) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                List<Result> results = new ArrayList<>();
                while (bufferedReader.ready()) {
                    try {
                        String line = XorEncrypting.encryptString(bufferedReader.readLine());
                        line = URLDecoder.decode(line, StandardCharsets.UTF_8);
                        String[] lines = (line.split(";"));
                        String category = lines[0];
                        String type = lines[1];
                        String level = lines[2];
                        String question = lines[3];
                        String correctAnswer = lines[4];
                        ArrayList<String> incorrectAnswers = new ArrayList<>();
                        for (int i = 5; i < lines.length; i++) {
                            incorrectAnswers.add(lines[i]);
                        }
                        Result result = new Result(category, type, level, question, correctAnswer, incorrectAnswers);
                        results.add(result);
                    }
                    catch (IllegalArgumentException exception){

                    }

                }
                this.results = results;
            }
        }
    }

    public Repository(String link) throws IOException {

        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        ObjectMapper objectMapper = new ObjectMapper();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Questions questions = objectMapper.readValue(bufferedReader, Questions.class);
        if (questions.getResponseCode() != 0) {
            App.showAlert("Database error", "The question database does not contain so many questions. " +
                    "\n Please try with less quantity.", Alert.AlertType.INFORMATION);
        } else {
            List<Result> results = questions.getResults();
            for (Result result : results) {
                String question = URLDecoder.decode(result.getQuestion(), StandardCharsets.UTF_8);
                result.setQuestion(question);
                String correctAnswer = URLDecoder.decode(result.getCorrectAnswer(), StandardCharsets.UTF_8);
                result.setCorrectAnswer(correctAnswer);
                List<String> incorrectAnswers = new ArrayList<>();
                for (String answer : result.getIncorrectAnswers()) {
                    String incorrectAnswer = URLDecoder.decode(answer, StandardCharsets.UTF_8);
                    incorrectAnswers.add(incorrectAnswer);
                }
                result.setIncorrectAnswers(incorrectAnswers);
            }
            this.results = results;
        }
    }

    public void save(File file) throws IOException {

        for (Result result : this.results) {
            String question = URLEncoder.encode(result.getQuestion(), StandardCharsets.UTF_8);
            question = XorEncrypting.encryptString(question);
            result.setQuestion(question);
            String correctAnswer = URLEncoder.encode(result.getCorrectAnswer(), StandardCharsets.UTF_8);
            correctAnswer = XorEncrypting.encryptString(correctAnswer);
            result.setCorrectAnswer(correctAnswer);
            List<String> incorrectAnswers = new ArrayList<>();
            for (String answer : result.getIncorrectAnswers()) {
                String incorrectAnswer = URLEncoder.encode(answer, StandardCharsets.UTF_8);
                incorrectAnswer = XorEncrypting.encryptString(incorrectAnswer);
                incorrectAnswers.add(incorrectAnswer);
            }
            result.setIncorrectAnswers(incorrectAnswers);
        }
        if (file.getName().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, this.results);
        } else if (file.getName().endsWith(".csv")) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                for (Result result : this.results) {
                    bufferedWriter.write(XorEncrypting.encryptString(result.getCategory() + ";"));
                    bufferedWriter.write(XorEncrypting.encryptString(result.getType() + ";"));
                    bufferedWriter.write(XorEncrypting.encryptString(result.getDifficulty() + ";"));
                    bufferedWriter.write(result.getQuestion() + XorEncrypting.encryptString(";"));
                    bufferedWriter.write(result.getCorrectAnswer() + XorEncrypting.encryptString(";"));
                    for (var incorrectAnswer : result.getIncorrectAnswers()) {
                        bufferedWriter.write(incorrectAnswer + XorEncrypting.encryptString(";"));
                    }
                    bufferedWriter.write("\n");
                }
            }
        }
    }




}

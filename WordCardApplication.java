import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordCardApplication extends Application {

    private List<Word> words;
    private Set<Integer> knownWords;
    private int currentIndex;
    private int correctCount;
    private int totalCount;
    private boolean clicked;
    private boolean isEnglishToTurkish;

    private Label label;
    private Label scoreLabel;
    private TextField answerField;
    private Button submitButton;
    private Button understoodButton;
    private Button swapButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Word Card Application");
        stage.setWidth(800);
        stage.setHeight(600);

        words = readWords("words.txt");
        Collections.shuffle(words);
        knownWords = new HashSet<>();
        totalCount = words.size();
        currentIndex = 0;
        correctCount = 0;
        clicked = false;
        isEnglishToTurkish = true;

        VBox root = new VBox(20);
        root.setPadding(new Insets(50));

        HBox cardFrame = new HBox();
        cardFrame.getStyleClass().add("cardFrame");
        cardFrame.setPadding(new Insets(20));
        cardFrame.setAlignment(Pos.CENTER);

        label = new Label(words.get(currentIndex).getEnglish());
        label.getStyleClass().add("englishLabel");
        label.setWrapText(true);
        label.setMaxWidth(400);
        cardFrame.getChildren().add(label);

        answerField = new TextField();
        answerField.setPromptText("Enter the Turkish translation");

        submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button");
        submitButton.setOnAction(event -> checkAnswer());

        HBox bottomFrame = new HBox(20);
        bottomFrame.getStyleClass().add("bottomFrame");
        bottomFrame.setAlignment(Pos.CENTER);

        understoodButton = new Button("Understood");
        understoodButton.getStyleClass().addAll("button", "understoodButton");
        understoodButton.setVisible(false);
        understoodButton.setOnAction(event -> nextCard());

        swapButton = new Button("Swap");
        swapButton.getStyleClass().addAll("button", "swapButton");
        swapButton.setOnAction(event -> swapLanguage());

        bottomFrame.getChildren().addAll(swapButton, understoodButton);

        scoreLabel = new Label("Score: " + correctCount + "/" + totalCount);
        scoreLabel.getStyleClass().add("scoreLabel");

        root.getChildren().addAll(cardFrame, answerField, submitButton, bottomFrame, scoreLabel);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private List<Word> readWords(String fileName) {
        List<Word> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    words.add(new Word(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase()));
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private void checkAnswer() {
        if (!clicked) {
            String userAnswer = answerField.getText().trim().toUpperCase();
            boolean isCorrect;
            if (isEnglishToTurkish) {
                isCorrect = userAnswer.equals(words.get(currentIndex).getTurkish());
            } else {
                isCorrect = userAnswer.equals(words.get(currentIndex).getEnglish());
            }

            if (isCorrect) {
                clicked = true;
                knownWords.add(currentIndex);
                correctCount++;
                scoreLabel.setText("Score: " + correctCount + "/" + totalCount);
                nextCard();
            } else {
                String correctAnswer = isEnglishToTurkish ? words.get(currentIndex).getTurkish() : words.get(currentIndex).getEnglish();
                label.setText("Incorrect! Correct answer: " + correctAnswer);
                label.getStyleClass().add("turkishLabel");
                understoodButton.setVisible(true);
                swapButton.setVisible(false);
                submitButton.setVisible(false);
            }
        }
    }

    private void swapLanguage() {
        isEnglishToTurkish = !isEnglishToTurkish;
        updateCard();
    }

    private void nextCard() {
        clicked = false;

        if (knownWords.size() == words.size()) {
            congratulationsScreen();
        } else {
            Random random = new Random();
            List<Integer> remainingIndexes = new ArrayList<>();

            for (int i = 0; i < words.size(); i++) {
                if (!knownWords.contains(i)) {
                    remainingIndexes.add(i);
                }
            }

            if (!remainingIndexes.isEmpty()) {
                currentIndex = remainingIndexes.get(random.nextInt(remainingIndexes.size()));
                updateCard();
            } else {
                congratulationsScreen();
            }
        }
    }

    private void updateCard() {
        if (isEnglishToTurkish) {
            label.setText(words.get(currentIndex).getEnglish());
            answerField.setPromptText("Enter the Turkish translation");
        } else {
            label.setText(words.get(currentIndex).getTurkish());
            answerField.setPromptText("Enter the English translation");
        }

        answerField.clear();
        label.getStyleClass().remove("turkishLabel");
        label.getStyleClass().add("englishLabel");
        understoodButton.setVisible(false);
        swapButton.setVisible(true);
        submitButton.setVisible(true);
    }

    private void congratulationsScreen() {
        VBox congratulationsLayout = new VBox(20);
        congratulationsLayout.setPadding(new Insets(50));
        congratulationsLayout.getStyleClass().add("congratulationsLayout");

        Label congratulationsLabel = new Label("Congratulations! You completed the game.");
        congratulationsLabel.getStyleClass().add("congratulationsLabel");

        congratulationsLayout.getChildren().addAll(congratulationsLabel);

        Stage congratulationsStage = new Stage();
        Scene congratulationsScene = new Scene(congratulationsLayout);

        congratulationsScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        congratulationsStage.setScene(congratulationsScene);
        congratulationsStage.show();

        Stage mainStage = (Stage) label.getScene().getWindow();
        mainStage.close();
    }

    private static class Word {
        private String english;
        private String turkish;

        public Word(String english, String turkish) {
            this.english = english;
            this.turkish = turkish;
        }

        public String getEnglish() {
            return english;
        }

        public String getTurkish() {
            return turkish;
        }
    }
}

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private Label label;
    private Label scoreLabel;
    private Button understoodButton;
    private Button knewButton;
    private Button didNotKnowButton;

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

        HBox bottomFrame = new HBox(20);
        bottomFrame.getStyleClass().add("bottomFrame");
        bottomFrame.setAlignment(Pos.CENTER);
        
        knewButton = new Button("Knew");
        knewButton.getStyleClass().addAll("button", "knewButton");
        knewButton.setOnAction(event -> knew());

        didNotKnowButton = new Button("Did Not Know");
        didNotKnowButton.getStyleClass().addAll("button", "didNotKnowButton");
        didNotKnowButton.setOnAction(event -> didNotKnow());

        understoodButton = new Button("Understood");
        understoodButton.getStyleClass().addAll("button", "understoodButton");
        understoodButton.setVisible(false);
        understoodButton.setOnAction(event -> nextCard());

        bottomFrame.getChildren().addAll(knewButton, understoodButton, didNotKnowButton);

        scoreLabel = new Label("Score: " + correctCount + "/" + totalCount);
        scoreLabel.getStyleClass().add("scoreLabel");

        root.getChildren().addAll(cardFrame, bottomFrame, scoreLabel);

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
                words.add(new Word(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private void knew() {
        if (!clicked) {
            clicked = true;
            knownWords.add(currentIndex);
            correctCount++;
            scoreLabel.setText("Score: " + correctCount + "/" + totalCount);
            nextCard();
        }
    }

    private void didNotKnow() {
        if (!clicked) {
            clicked = true;
            scoreLabel.setText("Score: " + correctCount + "/" + totalCount);
            label.setText(words.get(currentIndex).getTurkish());
            label.getStyleClass().add("turkishLabel");
            understoodButton.setVisible(true);
            knewButton.setVisible(false);
            didNotKnowButton.setVisible(false);
        }
    }

    private void nextCard() {
        clicked = false;
    
        if (knownWords.size() == words.size()) {
            congratulationsScreen();
        } else {
            // Select the next word randomly
            Random random = new Random();
            List<Integer> remainingIndexes = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                if (!knownWords.contains(i)) {
                    remainingIndexes.add(i);
                }
            }
            currentIndex = remainingIndexes.get(random.nextInt(remainingIndexes.size()));
            label.setText(words.get(currentIndex).getEnglish());
    
            label.getStyleClass().remove("turkishLabel");
    
            label.getStyleClass().add("englishLabel");
            understoodButton.setVisible(false);
            knewButton.setVisible(true);
            didNotKnowButton.setVisible(true);
        }
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



import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordCardController {
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
    private Button understoodButton;
    private Button swapButton;
    private Button submitButton;

    public WordCardController(List<Word> words, Label label, Label scoreLabel, TextField answerField, Button understoodButton, Button swapButton, Button submitButton) {
        this.words = words;
        this.knownWords = new HashSet<>();
        this.totalCount = words.size();
        this.currentIndex = 0;
        this.correctCount = 0;
        this.clicked = false;
        this.isEnglishToTurkish = true;

        this.label = label;
        this.scoreLabel = scoreLabel;
        this.answerField = answerField;
        this.understoodButton = understoodButton;
        this.swapButton = swapButton;
        this.submitButton = submitButton;

        updateCard();
    }

    public void checkAnswer() {
        if (!clicked) {
            String userAnswer = answerField.getText().trim().toUpperCase();
            boolean isCorrect = isEnglishToTurkish
                    ? userAnswer.equals(words.get(currentIndex).getTurkish())
                    : userAnswer.equals(words.get(currentIndex).getEnglish());

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

    public void swapLanguage() {
        isEnglishToTurkish = !isEnglishToTurkish;
        updateCard();
    }

    public void nextCard() {
        clicked = false;

        if (knownWords.size() == words.size()) {
            // Add your congratulations logic here
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
                // Add your congratulations logic here
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
}

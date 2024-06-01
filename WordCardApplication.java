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

public class WordCardApplication extends Application {
    private WordCardController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Word Card Application");
        stage.setWidth(800);
        stage.setHeight(600);

        WordManager wordManager = new WordManager("words.txt");

        VBox root = new VBox(20);
        root.setPadding(new Insets(50));

        HBox cardFrame = new HBox();
        cardFrame.getStyleClass().add("cardFrame");
        cardFrame.setPadding(new Insets(20));
        cardFrame.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.getStyleClass().add("englishLabel");
        label.setWrapText(true);
        label.setMaxWidth(400);
        cardFrame.getChildren().add(label);

        TextField answerField = new TextField();
        answerField.setPromptText("Enter the Turkish translation");

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button");

        HBox bottomFrame = new HBox(20);
        bottomFrame.getStyleClass().add("bottomFrame");
        bottomFrame.setAlignment(Pos.CENTER);

        Button understoodButton = new Button("Understood");
        understoodButton.getStyleClass().addAll("button", "understoodButton");
        understoodButton.setVisible(false);

        Button swapButton = new Button("Swap");
        swapButton.getStyleClass().addAll("button", "swapButton");

        bottomFrame.getChildren().addAll(swapButton, understoodButton);

        Label scoreLabel = new Label("Score: 0/" + wordManager.getWords().size());
        scoreLabel.getStyleClass().add("scoreLabel");

        root.getChildren().addAll(cardFrame, answerField, submitButton, bottomFrame, scoreLabel);

        controller = new WordCardController(wordManager.getWords(), label, scoreLabel, answerField, understoodButton, swapButton, submitButton);

        submitButton.setOnAction(event -> controller.checkAnswer());
        understoodButton.setOnAction(event -> controller.nextCard());
        swapButton.setOnAction(event -> controller.swapLanguage());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}

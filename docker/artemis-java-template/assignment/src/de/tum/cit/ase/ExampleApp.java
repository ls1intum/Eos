package de.tum.cit.ase;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import javafx.scene.control.Label;
import javafx.scene.Scene;

/**
 * A simple JavaFX application that counts the number of characters in a text field.
 */
public class ExampleApp extends Application {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
    private static final int VERTICAL_GAP = 10;

    /**
     * Launches the JavaFX application.
     * @param args Arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextField textField = new TextField();
        textField.setPromptText("Enter text here");

        Label charCountLabel = new Label("Character count: 0");
        Button countButton = new Button("Count Characters");

        countButton.setOnAction(event -> {
            charCountLabel.setText("Character count: " + textField.getText().length());
        });

        VBox root = new VBox(VERTICAL_GAP, textField, countButton, charCountLabel);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Character Counter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

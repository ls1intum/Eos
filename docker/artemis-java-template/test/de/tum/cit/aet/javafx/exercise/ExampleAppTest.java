package de.tum.cit.aet.javafx.exercise;

import de.tum.cit.ase.ares.api.Policy;
import de.tum.cit.aet.eos.JavaFXTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

@Policy(value = "test/SecurityPolicy.yaml", withinPath = "classes/java/main/de/tum/cit/aet/javafx/exercise")
public class ExampleAppTest extends JavaFXTest {
    private Button button;
    private TextField textField;
    private Label label;

    @Override
    public String getAppClassName() {
        return "de.tum.cit.aet.javafx.exercise.ExampleApp";
    }

    @BeforeEach
    public void initializeObjects() {
        var buttonOptional = lookup(".button").tryQueryAs(Button.class);
        if (buttonOptional.isEmpty()) {
            fail("Button not found");
        }
        button = buttonOptional.get();

        var textFieldOptional = lookup(".text-field").tryQueryAs(TextField.class);
        if (textFieldOptional.isEmpty()) {
            fail("TextField not found");
        }
        textField = textFieldOptional.get();

        var labelOptional = lookup(".label").tryQueryAs(Label.class);
        if (labelOptional.isEmpty()) {
            fail("Label not found");
        }
        label = labelOptional.get();
    }

    @Test
    public void testNoInput() {
        assertEquals("Enter text here", textField.getPromptText());

        verifyThat(button, hasText("Count Characters"));
        verifyThat(label, hasText("Character count: 0"));

        clickOn(button);

        verifyThat(label, hasText("Character count: 0"));
    }

    @Test
    public void testInput() {
        String input = "Hello, World!";

        clickOn(textField).write(input);
        clickOn(button);
        verifyThat(label, hasText("Character count: " + input.length()));

        clickOn(textField).eraseText(input.length());

        clickOn(textField).write("a");
        clickOn(button);
        verifyThat(label, hasText("Character count: 1"));

        clickOn(textField).eraseText(1);

        clickOn(textField).write("");
        clickOn(button);
        verifyThat(label, hasText("Character count: 0"));
    }
}

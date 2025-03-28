package de.tum.cit.ase.javafx.exercise;

import de.tum.cit.ase.eos.JavaFXTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

@CustomAnnotations
public class ExampleAppTest extends JavaFXTest {
	private Button button;
	private TextField textField;
	private Label label;

	@Override
	public String getAppClassName() {
		return "de.tum.cit.ase.javafx.exercise.ExampleApp";
	}

	@BeforeEach
	public void initializeObjects() {
		button = getNodeOfType(Button.class, ".button");
		textField = getNodeOfType(TextField.class, ".text-field");
		label = getNodeOfType(Label.class, ".label");
	}

	@Test
	public void testInput() {
		assertEquals("Enter text here", textField.getPromptText());

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

	@Test
	public void testLayout() {
		// You should only pass the layout test if the input test is passed. Otherwise, this test would also pass for the template.
		testInput();
		checkForCommonVBox(textField, label, button);
	}
}

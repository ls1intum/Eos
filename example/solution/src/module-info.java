module ExampleApp {
	exports de.tum.cit.aet.javafx.exercise;

	requires javafx.controls;
	requires javafx.graphics;

	opens de.tum.cit.aet.javafx.exercise to javafx.controls, javafx.graphics;
}

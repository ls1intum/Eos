module ExampleApp {
	exports de.tum.cit.ase.javafx.exercise;

	requires javafx.controls;
	requires javafx.graphics;

	opens de.tum.cit.ase.javafx.exercise to javafx.controls, javafx.graphics;
}

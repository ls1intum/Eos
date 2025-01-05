module ExampleApp {
    exports de.tum.cit.ase;

    requires javafx.controls;
    requires javafx.graphics;

    opens de.tum.cit.ase to javafx.controls, javafx.graphics;
}

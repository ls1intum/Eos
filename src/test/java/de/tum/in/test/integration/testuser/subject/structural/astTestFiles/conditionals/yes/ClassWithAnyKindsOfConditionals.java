package de.tum.in.test.integration.testuser.subject.structural.astTestFiles.conditionals.yes;

public class ClassWithAnyKindsOfConditionals {

	public void ifStatement() {
		int x = 3;
		if (x == 1) {
			System.out.println("Hello");
		} else if (x == 0) {
			System.out.println("World");
		} else {
			System.out.println("!");
		}
	}

	public void ifExpression() {
		int x = 3;
		System.out.println(x == 1 ? "Hello" : (x == 0 ? "World" : "!"));
	}

	public void switchStatement() {
		String output;
		switch (3) {
		case 1:
			output = "Hello";
			break;
		case 0:
			output = "World";
			break;
		default:
			output = "!";
			break;
		}
		System.out.println(output);
	}
}

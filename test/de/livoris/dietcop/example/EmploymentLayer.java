package de.livoris.dietcop.example;

import java.util.concurrent.Callable;

import de.livoris.dietcop.Layer;

public class EmploymentLayer extends Layer {
	@AppliesTo(Person.class)
	public String toString(Callable<String> proceed, Person self) throws Exception {
		return proceed.call() + "; [Employer] " + self.getEmployer();
	}
}

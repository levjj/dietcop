package de.livoris.dietcop.example;

import java.util.concurrent.Callable;

import de.livoris.dietcop.Layer;

public class AddressLayer extends Layer {
	@AppliesTo(Person.class)
	public String toString(Callable<String> proceed, Person self) throws Exception {
		return proceed.call() + "; Address: " + self.getAddress();
	}
	
	@AppliesTo(Employer.class)
	public String toString(Callable<String> proceed, Employer self) throws Exception {
		return proceed.call() + "; Address: " + self.getAddress();
	}
}

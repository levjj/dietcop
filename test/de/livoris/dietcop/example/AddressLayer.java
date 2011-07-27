package de.livoris.dietcop.example;

import java.util.concurrent.Callable;

import de.livoris.dietcop.AppliesTo;
import de.livoris.dietcop.Layer;

public class AddressLayer extends Layer {
	@AppliesTo(Person.class)
	public String toString(Callable<String> proceed, Person p) throws Exception {
		return proceed.call() + "; Address: " + p.getAddress();
	}
	
	@AppliesTo(Employer.class)
	public String toString(Callable<String> proceed, Employer e) throws Exception {
		return proceed.call() + "; Address: " + e.getAddress();
	}
}

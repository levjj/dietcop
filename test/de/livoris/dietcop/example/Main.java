package de.livoris.dietcop.example;

import static de.livoris.dietcop.Context.with;
import static de.livoris.dietcop.Context.wrap;

public class Main {

	public static void main(String[] args) {
		final Employer acme = wrap(new EmployerImpl("ACME Inc.", "100 Main St"));
		final Person bernd = wrap(new PersonImpl("Bernd", "123 Fake St", acme));
		
		System.out.println(bernd.toString());
		
		with(AddressLayer.class).eval(new Runnable() {
			public void run() {
				System.out.println(bernd.toString());
			}
		});
		
		with(EmploymentLayer.class).eval(new Runnable() {
			public void run() {
				System.out.println(bernd.toString());
			}
		});

		with(AddressLayer.class, EmploymentLayer.class).eval(new Runnable() {
			public void run() {
				System.out.println(bernd.toString());
			}
		});
	}

}

package de.livoris.dietcop.example;

import static de.livoris.dietcop.Context.with;
import static de.livoris.dietcop.Context.wrap;

public class Main {

	public static void main(String[] args) {
		final Employer acme = wrap(new EmployerImpl("ACME Inc.", "100 Main St"));
		final Person john = wrap(new PersonImpl("John", "123 Fake St", acme));
		
		System.out.println(john.toString());
		
		with(new AddressLayer()).eval(new Runnable() {
			public void run() {
				System.out.println(john.toString());
			}
		});
		
		with(new EmploymentLayer()).eval(new Runnable() {
			public void run() {
				System.out.println(john.toString());
			}
		});

		with(new AddressLayer(), new EmploymentLayer()).eval(new Runnable() {
			public void run() {
				System.out.println(john.toString());
			}
		});
	}

}

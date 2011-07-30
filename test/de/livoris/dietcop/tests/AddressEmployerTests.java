package de.livoris.dietcop.tests;


import static de.livoris.dietcop.Context.with;
import static de.livoris.dietcop.Context.wrap;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.livoris.dietcop.example.AddressLayer;
import de.livoris.dietcop.example.Employer;
import de.livoris.dietcop.example.EmployerImpl;
import de.livoris.dietcop.example.EmploymentLayer;
import de.livoris.dietcop.example.Person;
import de.livoris.dietcop.example.PersonImpl;

public class AddressEmployerTests {

	private Employer acme;
	private Person john;

	@Before
	public void setUp() throws Exception {
		acme = wrap(new EmployerImpl("ACME Inc.", "100 Main St"));
		john = wrap(new PersonImpl("John", "123 Fake St", acme));
	}
	
	@Test
	public void testNormal() {
		assertEquals("Name: John", john.toString());
	}
	
	
	@Test
	public void testAddress() {
		with(new AddressLayer()).eval(new Runnable() {
			public void run() {
				assertEquals("Name: John; Address: 123 Fake St", john.toString());
			}
		});
	}
	
	
	@Test
	public void testEmployer() {
		with(new EmploymentLayer()).eval(new Runnable() {
			public void run() {
		assertEquals("Name: John; [Employer] Name: ACME Inc.", john.toString());
			}
		});
	}
	
	@Test
	public void testAddressEmployer() {
		with(new AddressLayer(), new EmploymentLayer()).eval(new Runnable() {
			public void run() {
		assertEquals("Name: John; Address: 123 Fake St; [Employer] Name: ACME Inc.; Address: 100 Main St", john.toString());
			}
		});
	}
	
	@Test
	public void testAddressOnAndOff() {
		assertEquals("Name: John", john.toString());
		with(new AddressLayer()).eval(new Runnable() {
			public void run() {
				assertEquals("Name: John; Address: 123 Fake St", john.toString());
			}
		});
		assertEquals("Name: John", john.toString());
	}
	
	@Test
	public void testNested() {
		with(new AddressLayer()).eval(new Runnable() {
			public void run() {
				with(new EmploymentLayer()).eval(new Runnable() {
					public void run() {
						assertEquals("Name: John; Address: 123 Fake St; [Employer] Name: ACME Inc.; Address: 100 Main St", john.toString());
					}
				});
			}
		});
	}
}

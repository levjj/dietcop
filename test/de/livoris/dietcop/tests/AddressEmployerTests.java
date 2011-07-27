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
	private Person bernd;

	@Before
	public void setUp() throws Exception {
		acme = wrap(new EmployerImpl("ACME Inc.", "100 Main St"));
		bernd = wrap(new PersonImpl("Bernd", "123 Fake St", acme));
	}
	
	@Test
	public void testNormal() {
		assertEquals("Name: Bernd", bernd.toString());
	}
	
	
	@Test
	public void testAddress() {
		with(AddressLayer.class, new Runnable() {
			public void run() {
				assertEquals("Name: Bernd; Address: 123 Fake St", bernd.toString());
			}
		});
	}
	
	
	@Test
	public void testEmployer() {
		with(EmploymentLayer.class, new Runnable() {
			public void run() {
		assertEquals("Name: Bernd; [Employer] Name: ACME Inc.", bernd.toString());
			}
		});
	}
	
	@Test
	public void testAddressEmployer() {
		with(AddressLayer.class, EmploymentLayer.class, new Runnable() {
			public void run() {
		assertEquals("Name: Bernd; Address: 123 Fake St; [Employer] Name: ACME Inc.; Address: 100 Main St", bernd.toString());
			}
		});
	}
}

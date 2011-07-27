package de.livoris.dietcop.example;

public class PersonImpl implements Person {
	private String name, address;
	private Employer employer;
	
	public PersonImpl(String name, String address, Employer employer) {
		this.name = name;
		this.address = address;
		this.employer = employer;
	}

	@Override
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Employer getEmployer() {
		return employer;
	}
	
	public void setEmployer(Employer employer) {
		this.employer = employer;
	}
	
	@Override
	public String toString() {
		return "Name: " + getName();
	}
}

package de.livoris.dietcop.example;

public class EmployerImpl implements Employer {
	private String name, address;
	
	public EmployerImpl(String name, String address) {
		this.name = name;
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
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "Name: " + getName();
	}
}

package ru.mail.kdallas777.mylabs.barber;

public class Customer implements Runnable {
	// Номер посетителя
	private static int id = 1;
	
	// Имя посетителя
	private String customerName;
	
	// Парикмахерская
	private BarberShop barberShop;

	public Customer(BarberShop bShop, String name) {
		customerName = name + id;
		barberShop = bShop;
		id++;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	@Override
	public void run() {
		barberShop.sitInWorkspace(this);
	}
}
	

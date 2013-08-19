package ru.mail.kdallas777.mylabs.barber;

public class BarbaerShopTest {

	public static void main(String args[]) {
		// ������� �������������� � ������������
		BarberShop barberShopSim = new BarberShop();
		
		Thread barberThread = new Thread(barberShopSim.getBarber());
		barberThread.start();	
		
		while(true) {
			Thread customerThread = new Thread(new Customer(barberShopSim, "���������� "));
			customerThread.start();
			
			try {
				Thread.sleep( (int)(Math.random()*10000) );
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		 	
	}

}

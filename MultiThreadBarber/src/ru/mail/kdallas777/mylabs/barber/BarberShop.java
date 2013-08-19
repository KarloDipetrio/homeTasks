package ru.mail.kdallas777.mylabs.barber;

import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BarberShop {
	// Num chairs in waiting room
	public static final int NUM_CHAIRS = 3;
	
	// Num workspace
	public static final int NUM_WORKSPACES = 1;
	
	// Working time
	public static final int WORK_TIME = 10000;
			
	// Barbers workspace
	private Customer barberWorkspace;
	
	private enum BarberState {
		SLEEP, WORK, NOTHING
	}
	
	// Barbers state
	BarberState stateFlag;
	
	// Num of customers served
	private int customersCount;
	
	// Num of customers not served
	private int leftCustomersCount;
	
	// Barber
	private Barber barberMan;
	
	// Customers list in waiting room
	private Queue<Customer> customerList = new LinkedList<Customer>();
	
	public BarberShop() {
		customersCount = 0;
		leftCustomersCount = 0;
		
		barberMan = new Barber();
	}
	
	//=============================================
	// Data control methods
	//=============================================
	public Queue<Customer> getCustomerList() {
		return customerList;
	}
	//---------------------------------------------
	public Barber getBarber() {
		return barberMan;
	}
	//=============================================
	// Customers methods
	//=============================================
	// Sit in waiting room
	private void sitInWaitingRoom(Customer customer) {
		if( customerList.size() < NUM_CHAIRS ) {
			customerList.add(customer);
			System.out.println(customer.getCustomerName() + " sit in waiting room\n");
		} else {
			leftCustomersCount++;
			System.out.println(customer.getCustomerName() + " left from barbershop, because no free chairs, num of customers not served: "+ leftCustomersCount + "\n");
		}
	}
	//---------------------------------------------
	// Wake barber and sit in workspace
	public synchronized void sitInWorkspace(Customer customer) {
		if( checkBarber(customer) == BarberState.SLEEP ) {
			System.out.println(customer.getCustomerName() + " wake barber and sit in workspace\n");
			barberWorkspace = customer;
			stateFlag = BarberState.WORK;
		} else {
			sitInWaitingRoom(customer);
		}
		
		try {
			notify();
			wait();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	//---------------------------------------------
	// Check barbers state
	// 0 - sleep
	// 1 - work
	public BarberState checkBarber(Customer customer) {
		System.out.print(customer.getCustomerName() + " check barbers state:");
		
		if( stateFlag == BarberState.SLEEP ){
			System.out.println(" barber sleeps\n");
		} else {
			System.out.println(" barber works\n");
		}
		
		return stateFlag;
	}
	//=============================================
	// Barbers methods
	//=============================================
	// Check customers
	public synchronized boolean checkCustomers() {
		System.out.printf("Barber checks customers: in waiting room %d from %d\n\n", customerList.size(), NUM_CHAIRS);
		return !customerList.isEmpty();
	}
	//---------------------------------------------
	public synchronized void work() {
		while( isWorkspaceEmpty() ) {
			try {
				sleep();
				wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		while( !isWorkspaceEmpty() ) {
			if( stateFlag != BarberState.WORK)
				stateFlag = BarberState.WORK;
			
			System.out.printf("Barber shears customer: %s\n\n", barberWorkspace.getCustomerName());
			
			try {
				wait(WORK_TIME);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.printf("Barber finished haircut: %s\n\n", barberWorkspace.getCustomerName());
			customersCount++;
			stateFlag = BarberState.NOTHING;
			resetBarberWorkspace();
			callCustomer();
		}
	}
	//---------------------------------------------
	public synchronized void sleep() {
		if( stateFlag != BarberState.SLEEP ) {
			stateFlag = BarberState.SLEEP;
			
			System.out.println("Barber sleeps\n");
		}	
	}
	//---------------------------------------------
	private boolean isWorkspaceEmpty() {
		return barberWorkspace == null;
	}
	//---------------------------------------------
	private void resetBarberWorkspace() {
		barberWorkspace = null;
	}
	//---------------------------------------------
	// Call customer from waiting room
	private synchronized void callCustomer() {
		if( checkCustomers() ) {
			barberWorkspace = customerList.poll();
		} 
	}
	//---------------------------------------------
	public class Barber implements Runnable {
		public Barber() {
			stateFlag = BarberState.NOTHING;
		}
		
		@Override
		public void run() {
			while(true) {
				work();
			}
		}
		
	}

}



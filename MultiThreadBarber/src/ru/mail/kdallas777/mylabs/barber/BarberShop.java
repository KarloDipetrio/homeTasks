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
			System.out.println(customer.getCustomerName() + " занял место в приемной\n");
		} else {
			leftCustomersCount++;
			System.out.println(customer.getCustomerName() + " ушел из парикмахерской, так как нет мест, количество необслужанных клиентов: "+ leftCustomersCount + "\n");
		}
	}
	//---------------------------------------------
	// Wake barber and sit in workspace
	public synchronized void sitInWorkspace(Customer customer) {
		if( checkBarber(customer) == BarberState.SLEEP ) {
			System.out.println(customer.getCustomerName() + " разбудил парикмахера и сел на стрижку\n");
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
		System.out.print(customer.getCustomerName() + " проверяет состояние парикмахера:");
		
		if( stateFlag == BarberState.SLEEP ){
			System.out.println(" парикмахер спит\n");
		} else {
			System.out.println(" парикмахер занят работой\n");
		}
		
		return stateFlag;
	}
	//=============================================
	// Barbers methods
	//=============================================
	// Check customers
	public synchronized boolean checkCustomers() {
		System.out.printf("Парикмахер проверяет наличие клиентов: В очереди %d из %d\n\n", customerList.size(), NUM_CHAIRS);
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
			
			System.out.printf("Парикмахер стрижет посетителя: %s\n\n", barberWorkspace.getCustomerName());
			
			try {
				wait(WORK_TIME);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.printf("Парикмахер закончил стричь посетителя: %s\n\n", barberWorkspace.getCustomerName());
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
			
			System.out.println("Парикмахер спит\n");
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



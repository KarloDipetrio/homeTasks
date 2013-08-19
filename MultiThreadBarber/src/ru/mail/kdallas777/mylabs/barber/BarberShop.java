package ru.mail.kdallas777.mylabs.barber;

import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BarberShop {
	// Количество мест в приемной
	public static final int NUM_CHAIRS = 3;
	
	// Количество рабочих мест(парикмахеров)
	public static final int NUM_WORKSPACES = 1;
	
	// Время одной стрижки в мс
	public static final int WORK_TIME = 10000;
			
	// Рабочее место парикмахера
	private Customer barberWorkspace;
	
	private enum BarberState {
		SLEEP, WORK, NOTHING
	}
	
	// Состояние парикмахера
	BarberState stateFlag;
	
	// Количетсво обслуженных клиентов
	private int customersCount;
	
	// Количество необслужанных клиентов
	private int leftCustomersCount;
	
	// Парикмахер
	private Barber barberMan;
	
	// Места в приемной
	private Queue<Customer> customerList = new LinkedList<Customer>();
	
	public BarberShop() {
		customersCount = 0;
		leftCustomersCount = 0;
		
		barberMan = new Barber();
	}
	
	//=============================================
	// Методы управления данными
	//=============================================
	public Queue<Customer> getCustomerList() {
		return customerList;
	}
	//---------------------------------------------
	public Barber getBarber() {
		return barberMan;
	}
	//=============================================
	// Методы посетителей
	//=============================================
	// Занять место в приемной посетителем если есть свободные, возвращает true если удалось
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
	// Разбудить парикмахера и сесть на рабочее место если парикмахер спит
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
	// Проверка состояния парикмахера со стороны клиента
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
	// Методы парикмахера
	//=============================================
	// Проверка наличия посетителей со стороны парикмахера
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
	// Позвать клиента из очереди в приемной если есть, иначе спать, возвращает true если удалось
	private synchronized void callCustomer() {
		if( checkCustomers() ) {
			barberWorkspace = customerList.poll();
		} 
	}
	//---------------------------------------------
	// Класс парикмахер
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



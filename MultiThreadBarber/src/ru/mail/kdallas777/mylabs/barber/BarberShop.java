package ru.mail.kdallas777.mylabs.barber;

import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BarberShop {
	// ���������� ���� � ��������
	public static final int NUM_CHAIRS = 3;
	
	// ���������� ������� ����(������������)
	public static final int NUM_WORKSPACES = 1;
	
	// ����� ����� ������� � ��
	public static final int WORK_TIME = 10000;
			
	// ������� ����� �����������
	private Customer barberWorkspace;
	
	private enum BarberState {
		SLEEP, WORK, NOTHING
	}
	
	// ��������� �����������
	BarberState stateFlag;
	
	// ���������� ����������� ��������
	private int customersCount;
	
	// ���������� ������������� ��������
	private int leftCustomersCount;
	
	// ����������
	private Barber barberMan;
	
	// ����� � ��������
	private Queue<Customer> customerList = new LinkedList<Customer>();
	
	public BarberShop() {
		customersCount = 0;
		leftCustomersCount = 0;
		
		barberMan = new Barber();
	}
	
	//=============================================
	// ������ ���������� �������
	//=============================================
	public Queue<Customer> getCustomerList() {
		return customerList;
	}
	//---------------------------------------------
	public Barber getBarber() {
		return barberMan;
	}
	//=============================================
	// ������ �����������
	//=============================================
	// ������ ����� � �������� ����������� ���� ���� ���������, ���������� true ���� �������
	private void sitInWaitingRoom(Customer customer) {
		if( customerList.size() < NUM_CHAIRS ) {
			customerList.add(customer);
			System.out.println(customer.getCustomerName() + " ����� ����� � ��������\n");
		} else {
			leftCustomersCount++;
			System.out.println(customer.getCustomerName() + " ���� �� ��������������, ��� ��� ��� ����, ���������� ������������� ��������: "+ leftCustomersCount + "\n");
		}
	}
	//---------------------------------------------
	// ��������� ����������� � ����� �� ������� ����� ���� ���������� ����
	public synchronized void sitInWorkspace(Customer customer) {
		if( checkBarber(customer) == BarberState.SLEEP ) {
			System.out.println(customer.getCustomerName() + " �������� ����������� � ��� �� �������\n");
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
	// �������� ��������� ����������� �� ������� �������
	// 0 - sleep
	// 1 - work
	public BarberState checkBarber(Customer customer) {
		System.out.print(customer.getCustomerName() + " ��������� ��������� �����������:");
		
		if( stateFlag == BarberState.SLEEP ){
			System.out.println(" ���������� ����\n");
		} else {
			System.out.println(" ���������� ����� �������\n");
		}
		
		return stateFlag;
	}
	//=============================================
	// ������ �����������
	//=============================================
	// �������� ������� ����������� �� ������� �����������
	public synchronized boolean checkCustomers() {
		System.out.printf("���������� ��������� ������� ��������: � ������� %d �� %d\n\n", customerList.size(), NUM_CHAIRS);
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
			
			System.out.printf("���������� ������� ����������: %s\n\n", barberWorkspace.getCustomerName());
			
			try {
				wait(WORK_TIME);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.printf("���������� �������� ������ ����������: %s\n\n", barberWorkspace.getCustomerName());
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
			
			System.out.println("���������� ����\n");
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
	// ������� ������� �� ������� � �������� ���� ����, ����� �����, ���������� true ���� �������
	private synchronized void callCustomer() {
		if( checkCustomers() ) {
			barberWorkspace = customerList.poll();
		} 
	}
	//---------------------------------------------
	// ����� ����������
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



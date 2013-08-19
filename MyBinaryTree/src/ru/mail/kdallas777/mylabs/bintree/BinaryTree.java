package ru.mail.kdallas777.mylabs.bintree;

public interface BinaryTree<T extends Comparable<T>>
{
	/**
	 * add new element
	 * @param elem
	 * @return true if success
	 */
	boolean add(T elem);
 
	/**
	 * delete element
	 * @param elem
	 * @return true if success
	 */
	boolean remove(T elem);
	
	/**
	 * check for the presence of the element
	 * @param elem
	 * @return
	 */
	boolean contains(T elem);
}
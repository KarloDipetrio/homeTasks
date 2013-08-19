package ru.mail.kdallas777.mylabs.bintree;

import java.util.*;

public class MyBinaryTree<T extends Comparable<T> > 
    implements BinaryTree<T>, Iterable<T>, Iterator<T> {
	
	// Node
	private class BTree<T> {
		T key;
		BTree<T> left, right;
		
		BTree(T key)
		{
			this.key = key;
		}
	}
	
	private BTree<T> root = null;
	private BTree<T> currentNode = null;
	private Queue<BTree<T> > queue = new LinkedList<BTree<T> >();
	
	public MyBinaryTree() {
		
	}
	
	@Override
	public Iterator<T> iterator() {
		currentNode = root;
		inOrderWalk(root);
		return this;
	}
	
	@Override
	public T next() {
		currentNode = queue.poll();
		if(currentNode == null)
			throw new NoSuchElementException();
		
		return currentNode.key;
	}
	
	@Override
	public boolean hasNext() {	
		return !queue.isEmpty();
	}
	
	@Override
	public void remove() {
		remove(currentNode.key);
	}
	
	@Override
	public boolean add(T elem) {
		BTree<T> current = root, parent = null;
		
		while(current != null)
			{
			    parent = current;
				int cmp = elem.compareTo(current.key);
				if(cmp < 0) {
					current = current.left;
			    } else {
			    	current = current.right;
			    }
			}
		
		BTree<T> newNode = new BTree<T>(elem);
		if(parent == null) {
			root = newNode;
		} else { 
			if(elem.compareTo(parent.key) < 0) {
				parent.left = newNode;
			} else {
				parent.right = newNode;
			}
		}
			
		return true;
	}
	
	@Override
	public boolean remove(T elem) {
		BTree<T> current = root, parent = null;
		
		while(current != null) {
			int cmp = elem.compareTo(current.key);
			
			if(cmp == 0) {
				break;
			} else {
				parent = current;
				if(cmp < 0) {
					current = current.left;
				} else {
					current = current.right;
				}
				
			}
		}
		
		if(current == null)
			return true;
		
		if(current.right == null) {
			if(parent == null) {
				root = current.left;
			} else {
				if(current == parent.left) {
					parent.left = current.left;
				} else {
					parent.right = current.left;
				}
			}
		} else {
			BTree<T> leftMost = current.right;
			parent = null;
			while(leftMost != null) {
				parent = leftMost;
				leftMost = leftMost.left;
			}
			if(parent != null) {
				parent.left = leftMost.right;
			} else {
				current.right = leftMost.right;
			}
			current.key = leftMost.key;
		}
		
		return true;
	}
	
	@Override
	public boolean contains(T elem) {
		BTree<T> current = root;
		
		while(current != null)
		{
			int cmp = elem.compareTo(current.key);
			if(cmp == 0)
				return true;
			
			if(cmp < 0) {
				current = current.left;
			} else {
				current = current.right;
			}	
		}
		
		return false;
	}
	
	private void inOrderWalk(BTree<T> node) {
		if(node != null) {
			queue.add(node);
			inOrderWalk(node.left);
			inOrderWalk(node.right);
		}			
	}
}

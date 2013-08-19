package ru.mail.kdallas777.mylabs.bintree;

public class BinaryTreeTest {
	public static void main(String[] args) {
		MyBinaryTree<Double> doubleTree = new MyBinaryTree<Double>();
		doubleTree.add(1.1);
		doubleTree.add(1.2);
		doubleTree.add(1.3);
		doubleTree.add(0.1);
		doubleTree.add(0.3);
		doubleTree.remove(1.3);
		
		for(Double elem : doubleTree)
		{
			System.out.println(elem);
		}
	}

}

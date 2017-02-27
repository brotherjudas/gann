package gann;

import java.util.*;

public class Main {

	public static void main(String[] args) {
		
		BitSet test = new BitSet(6);
		test.set(3,6);
		
		for(int i=1;i<=test.length();i++)
			if(test.get(i))
				System.out.print(1);
			else
				System.out.print(0);
		
		System.out.println();

	}

}

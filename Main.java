
public class Main {
	
	
	public static void main(String[] args){
		
		//(OR;(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ))
		//(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))
		//(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))
		//(AND;(OR;3;5);1)
		//(AND;(AND;6;6);5)
		//(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))
		//(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6))
		//(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))
		//(OR;(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)));(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))))";
		//(OR;(AND;2;2;3;3);(AND;2;2;3;3);(AND;2;2;3;3))
		//(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))
		
		
		double doubleTest = (double) ((double) 1 -(1/Math.pow(10, 10)));// 
		long longtest = (long) Math.pow(10, 10);
		System.out.println(doubleTest);
		System.out.println(longtest);
		System.out.println(Integer.MAX_VALUE);
		
		String TRIPLE = "(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))";
		String PAIR = "(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6))";
		
//		String str = "(OR;(AND;" + PAIR + ";" + PAIR + ";" + PAIR + ";(OR;1;5);" + TRIPLE + ";(AND;(OR;" + TRIPLE + ";" + TRIPLE + "))))";
		String str = "(OR;" +
				"(OR;1;5);" +
				"(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));" +
//				"(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)));" +
				"(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))" +
				")";
		
//		String str = "(OR;1)";
		
		WishTree test = WishTree.stringToTree(str);
		System.out.println("tree import...");
		System.out.println(test.toString());
		System.out.println("tree imported.");
		System.out.println("");
		
		test.simplifyTree(test);
		System.out.println("simplifying tree...");
		System.out.println(test.toString());
		System.out.println("tree simplified");
		System.out.println("");
		
		test.developpTree(test);
		System.out.println("developping tree...");
		System.out.println(test.toString());
		System.out.println("tree developped");
		
		test.normaliseTerminalBranches(test);
		System.out.println("normalising tree...");
		System.out.println(test.toString());
		System.out.println("tree normalised");
		
		test.removeRedundancy(test);
		System.out.println("removing redundency...");
		System.out.println(test.toString());
		System.out.println("redundencies removed");
//		
		Throw testThrow = new Throw(test, 6, 6); // TODO seems to be a pb if a goal contains a number bigger than nrfaces

		
		System.out.println("the proba is " +  testThrow.ProbaComplete(test));
		

	}

}

//Throw testThrow2 = new Throw(test, 2, 6);
//Throw testThrow3 = new Throw(test, 3, 6);
//Throw testThrow4 = new Throw(test, 4, 6);
//Throw testThrow9 = new Throw(test, 9, 6);
//Throw testThrow10 = new Throw(test, 10, 6);
//Throw testThrow11 = new Throw(test, 11, 6);
//Throw testThrow12 = new Throw(test, 12, 6);
//Throw testThrow13 = new Throw(test, 13, 6);
//Throw testThrow15 = new Throw(test, 15, 6);
//System.out.println("the proba 2 is " + testThrow2.ProbaComplete(test));
//System.out.println("the proba 3 is " + testThrow3.ProbaComplete(test));
//System.out.println("the proba 4 is " + testThrow4.ProbaComplete(test));
//System.out.println("the proba 9 is " + testThrow9.ProbaComplete(test));
//System.out.println("the proba 10 is " + testThrow10.ProbaComplete(test));
//System.out.println("the proba 11 is " + testThrow11.ProbaComplete(test));
//System.out.println("the proba 12 is " + testThrow12.ProbaComplete(test));
//System.out.println("the proba 13 is " + testThrow13.ProbaComplete(test));
//System.out.println("the proba 15 is " + testThrow15.ProbaComplete(test));
//
//int k = 6;
//for (int i = 0; i<20; i++){
//	System.out.println("1/" + i + "^" + k + " = " + (1/Math.pow(i, k)));
//}
//
//
//
//

//for (int i = 0; i<15; i++){
//System.out.println("the number " + i + " writes in binary : " + Integer.toBinaryString(i));
//}

//( OR ; (OR;1;5) ; ( OR;(AND;1;1;1);(AND;2;2;2);(AND,3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6) ) )

//(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))
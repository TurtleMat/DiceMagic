public class Main {


	public static void main(String[] args) {
		
//		currentTest();
		
		computation();
		


	}
	
	private static void computation(){
		
		int nrFaces = 6;
		int nrDice = 2;
		
		
//		WishTree toCompute = WishTree.Street(nrFaces);
//		WishTree toCompute = WishTree.zehnTausend();
//		WishTree toCompute = WishTree.nTupel(nrFaces, 3);
//		WishTree toCompute = WishTree.fastStrasseOrStrasse(nrFaces);
		
//		WishTree toCompute = WishTree.nTupel(nrFaces, 6, new int[] {1, 5});
		
//		String encodingString = "(AND;(OR;(AND;1;1;1);SDGWe;rg;(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)))";
//		String encodingString = "SDGWe;rg";
//		String encodingString = "(OR;(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))";
		String encodingString = "(OR;(AND;1;5);(AND;1;1);(AND;5;5))";

//		WishTree toCompute = WishTree.zehnTausend();
		
		
		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, true);
		if (toCompute == null){
			System.out.println("imput tree could not be interpretated or is null.");
		}else {
			
			WishTree.prepareTree(toCompute, true);
			Throw toThrow = new Throw(toCompute, nrDice, nrFaces);
			
			double res = toThrow.probaCompleteRec(toCompute)/ Math.pow(nrFaces, nrDice);
			

			System.out.println("the proba rec is " + res);
			System.out.println("-----------------------------------------------------------------------------");
		}

		
		
	}
	
	
	private static void currentTest(){
		

		int nrFaces = 6;
		WishTree testTree = WishTree.zehnTausend();
		System.out.println(testTree.toString());
		WishTree.prepareTree(testTree, true);
		
//		System.out.println(WishTree.andCopy(WishTree.nTupel(nrFaces, 2),3).toString());
		
//		
//		int nrDice = 6;
//		int nrFaces = 6;
//		
//		int[] toKeep = {1, 3};
//		WishTree testpairs = WishTree.nTupel(nrFaces, 2);
//		WishTree.prepareTree(testpairs, true);
//		
//		WishTree test = WishTree.importAndPrepareTree(irgendwas10000, true);
//		
//		Throw testThrow = new Throw(test, nrDice, nrFaces); // TODO seems to be a pb if a goal contains a number  bigger than nrfaces
//		double res = (double) testThrow.probaCompleteRec(test) / Math.pow(nrFaces, nrDice);
//		
//
//		System.out.println("the proba rec is " + res);
//		System.out.println("-----------------------------------------------------------------------------");
//		

	}

//	private static void compareProbCompRecOrNot() {
//		String[] wishesToTest = new String[16];
//
//		String str0 = "(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))";
//		String str1 = "(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))";
//		String str2 = "(OR;(AND;1;2;3;4;5;6))";
//		String str3 = "(OR;(OR;1;5);(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6))))";
//		String str4 = "(OR;(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)));(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6))))";
//		String str5 = "(OR;(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ))";
//		String str6 = "(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))";
//		String str7 = "(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))";
//		String str8 = "(AND;(OR;3;5);1)";
//		String str9 = "(AND;(AND;6;6);5)";
//		String str10 = "(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))";
//		String str11 = "(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6))";
//		String str12 = "(OR;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))";
//		String str13 = "(OR;(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)));(OR;1;5);(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6));(AND;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6)) ;(OR;(AND;1;1;1);(AND;2;2;2);(AND;3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6))))";
//		String str14 = "(OR;(AND;2;2;3;3);(AND;2;2;3;3);(AND;2;2;3;3))";
//		String str15 = "(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))";
//		// String str16 =
//		// "(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))";
//
//		wishesToTest[0] = str0;
//		wishesToTest[1] = str1;;
//		wishesToTest[2] = str2;
//		wishesToTest[3] = str3;
//		wishesToTest[4] = str4;
//		wishesToTest[5] = str5;
//		wishesToTest[6] = str6;
//		wishesToTest[7] = str7;
//		wishesToTest[8] = str8;
//		wishesToTest[9] = str9;
//		wishesToTest[10] = str10;
//		wishesToTest[11] = str11;
//		wishesToTest[12] = str12;
//		wishesToTest[13] = str13;
//		wishesToTest[14] = str14;
//		wishesToTest[15] = str15;
//
//		int nrDice = 6;
//		int nrFaces = 6;
//		boolean verbose = false;
//
//		WishTree testTree = WishTree.importAndPrepareTree(wishesToTest[15],
//				true);
//		Throw testThrow = new Throw(testTree, nrDice, nrFaces);
//		long resRec = testThrow.probaCompleteRec(testTree);
//		long resNoRec = testThrow.ProbaComplete(testTree);
//
//		if (resRec != resNoRec) {
//			System.out
//					.println("ERROR : the proba should be the same. problem with tree : ");
//			System.out.println(wishesToTest[12]);
//			System.out.println("--------------------------");
//			System.out.println(testTree.toString());
//		}
//
//		for (int i = 0; i < wishesToTest.length; i++) {
//			System.out.println("testing tree nr " + i);
//
//			WishTree currTree = WishTree.importAndPrepareTree(wishesToTest[i],
//					verbose);
//
//			// WishTree currTree = WishTree.stringToTree(wishesToTest[i]);
//			// currTree.simplifyTree();
//			// currTree.developpTree();
//			// currTree.normaliseTerminalBranches();
//			// currTree.removeRedundancy();
//
//			Throw currThrow = new Throw(currTree, nrDice, nrFaces);
//
//			long iterProba = currThrow.ProbaComplete(currTree);
//			long recProba = currThrow.probaCompleteRec(currTree);
//
//			if (iterProba != recProba) {
//				System.out
//						.println("ERROR : the proba should be the same. problem with tree : ");
//				System.out.println(wishesToTest[i]);
//				System.out.println("--------------------------");
//				System.out.println(currTree.toString());
//			} else {
//				System.out.println("comparaison nr " + i + " is OK");
//			}
//		}
//	}

	private void testnumberLimits() {

		Integer[] pointer = new Integer[6];
		pointer[3] = 56;
		int truc = pointer[3];
		truc = 89;
		System.out.println(truc);
		System.out.println(pointer[3]);

		double doubleTest = (double) ((double) 1 - (1 / Math.pow(10, 10)));//
		long longtest = (long) Math.pow(10, 10);
		System.out.println(doubleTest);
		System.out.println(longtest);
		System.out.println(Integer.MAX_VALUE);
	}

}
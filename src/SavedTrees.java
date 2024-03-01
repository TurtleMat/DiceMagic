
public class SavedTrees {

	String triplePaar = "(AND;" +
			"(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));" +
			"(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));" +
			"(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))"
			+ ")";
	String Paar = "(OR;" +
			"(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)" +
			");";
	
	 String wrongFastStrasse = "(OR;" +
	 "(AND;2;3;4;5;6);" +
	 "(AND;1;3;4;5;6);" +
	 "(AND;1;2;4;5;6);" +
	 "(AND;1;2;3;5;6);" +
	 "(AND;1;2;3;4;6);" +
	 "(AND;1;2;3;4;5);" +
	 ")";
	 
	 String temp2 = "(AND;1;2;3;5;6)";
	 String irgendwas10000 = "(OR;" + 
	 "1;"+
	 "5;" + 
	 WishTree.nTupel(6, 3).toString() + 
	 triplePaar +
	 ")";
}

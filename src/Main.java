public class Main {


	public static void main(String[] args) {
		computation();
	}
	
	private static void computation(){
		
		int nrFaces = 6;
		int nrDice = 5;
		
//		String encodingString = "(AND;pair;(OR;6;4);(OR;(AND;5;2);(AND;(AND;4;5;6);pair))))";
		String encodingString = "(AND;(OR;ntupel3;pair);(OR;ntupel3;pair))";
//		String encodingString = "(AND;tenthousand)";
//		String encodingString = "(AND;street)";

		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, nrFaces, nrDice, true);
		if (toCompute == null){
			System.out.println("imput tree could not be interpretated or is null.");
		}else {
			
			Throw toThrow = new Throw(toCompute, nrDice, nrFaces);
			
			double res = toThrow.probaCompleteRec(toCompute)/ Math.pow(nrFaces, nrDice);
			
			System.out.println("the proba rec is " + res);
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
}
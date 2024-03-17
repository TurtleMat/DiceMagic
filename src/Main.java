import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Main {


	public static void main(String[] args) {
		
		computation();
//		useGui();
	}
	
	private static void useGui(){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream old = System.out;
		PrintStream consoleRedirect = new PrintStream(baos);
		
		
		Gui Gui1 = new Gui();
		
		//This was in constructor. test if it breaks.
		Gui1.setBounds(); 
		Gui1.addToMainframe();
		Gui1.initialise();
		Gui1.createContextMenu();
		Gui1.setupActionListeners();
		
//		Gui1.getConsoleOutput().setText(baos.toString());
		
		
		
		System.out.flush();
		System.setOut(old);
	}
	
	private static void computation(){
		
		int nrFaces = 6;
		int nrDice = 6;
		
//		String encodingString = "(AND;pair;(OR;6;4);(OR;(AND;5;2);(AND;(AND;4;5;6);pair))))";
//		String encodingString = "(AND;(OR;ntupel3;pair);(OR;ntupel3;pair))";
		String encodingString = "(AND;tenthousand)";
//		String encodingString = "(OR:5;(AND:10;(OR:20;2;4);2);1;3)";
//		String encodingString = "(OR;1:100;5:50)";

		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, nrFaces, nrDice, true);
		if (toCompute == null){
			System.out.println("imput tree could not be interpretated or is null.");
		}else {
			
			CalculationsForSingleThrow toThrow = new CalculationsForSingleThrow(toCompute, nrDice, nrFaces);
			
			long[] pairProbaExpect = toThrow.probaCompleteRec(toCompute);
			
//			/ Math.pow(nrFaces, nrDice);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("the probability is " + pairProbaExpect[0]/ Math.pow(nrFaces, nrDice));
			System.out.println("the expectancy for greedy strategy is : "+pairProbaExpect[1]/ Math.pow(nrFaces, nrDice));
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	
	public static void say(String toSay, boolean always, boolean verbose, boolean debug){
		
	}
}
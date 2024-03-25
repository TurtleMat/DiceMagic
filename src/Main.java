import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Main {
	
	static boolean verbose;


	static boolean debug;
	static boolean all;
	static boolean hasGui;
	
	static Gui gui;

	public static void main(String[] args) {
		
		Main.setVerbose(true);
		Main.setDebug(true);
		Main.setAll(false);
		Main.hasGui= false;
		
		if (hasGui()){
			useGui();
		}else{
			computation();
		}
		
		
	}
	

	private static void useGui(){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream old = System.out;
		PrintStream consoleRedirect = new PrintStream(baos);
		
		gui = new Gui();
		Main.hasGui = true;
		
		//This was in constructor. test if it breaks.
		Main.getGui().setBounds(); 
		Main.getGui().addToMainframe();
		Main.getGui().initialise();
		Main.getGui().createContextMenu();
		Main.getGui().setupActionListeners();
		
//		Gui1.getConsoleOutput().setText(baos.toString());
		
		
		
		System.out.flush();
		System.setOut(old);
	}
	



	private static void computation(){
		
		int nrFaces = 6;
		int nrDice = 6;
		boolean allowMultiplePatterns = true;
		Rules ruleset = new Rules(allowMultiplePatterns, true, false);
		
		String encodingString = "(OR;1:100;5:50;(AND:1000;1;1;1);(AND:2000;1;1;1;1);(AND:4000;1;1;1;1;1);(AND:8000;1;1;1;1;1;1);(AND:200;2;2;2);(AND:400;2;2;2;2);(AND:800;2;2;2;2;2);(AND:1600;2;2;2;2;2;2);(AND:300;3;3;3);(AND:600;3;3;3;3);(AND:1200;3;3;3;3;3);(AND2400;3;3;3;3;3;3);(AND:400;4;4;4);(AND:800;4;4;4;4);(AND:1600;4;4;4;4;4);(AND:3200;4;4;4;4;4;4);(AND:500;5;5;5);(AND:1000;5;5;5;5);(AND:2000;5;5;5;5;5);(AND:4000;5;5;5;5;5;5);(AND:600;6;6;6);(AND:1200;6;6;6;6);(AND:2400;6;6;6;6;6);(AND:4800;6;6;6;6;6;6);(AND:3000;1;2;3;4;5;6);(AND:500;pair;pair;pair))";
		//this should be 10 000 with gain
		
//		String encodingString = "(AND;pair;(OR;6;4);(OR;(AND;5;2);(AND;(AND;4;5;6);pair))))";
//		String encodingString = "(OR;ntupel3)";
//		String encodingString = "(OR;pair:10;ntupel3:15)";
//		String encodingString = "(OR;(AND:10;1;1;1);(AND:20;2;2);(AND:20;3;3);1:10)";
//		String encodingString = "(OR;(AND;1;1;1)";
//		String encodingString = "(AND;tenthousand)";
//		String encodingString = "(OR;(AND:5;(OR:20;2;4);2);1:10;3:10)";
//		String encodingString = "(OR;(AND;1;2;4;8);(OR;1;2;4;5;5;6;7);(AND;3;;3;3;3;3;3;3;3;3;3;3))";
		

		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, nrFaces, nrDice, true, ruleset);
		if (toCompute == null){
			Main.say("imput tree could not be interpretated or is null.");
		}else {
			
			CalculationsForSingleThrow toThrow = new CalculationsForSingleThrow(toCompute, nrDice, nrFaces);
			
			long[] pairProbaExpect = toThrow.probaCompleteRec(toCompute);
			
//			/ Math.pow(nrFaces, nrDice);
//			Main.say("-----------------------------------------------------------------------------");
//			Main.say("the probability is " + pairProbaExpect[0]/ Math.pow(nrFaces, nrDice));
//			Main.say("the expectancy for greedy strategy is : "+pairProbaExpect[1]/ Math.pow(nrFaces, nrDice));
//			Main.say("-----------------------------------------------------------------------------");
		}
	}
	
	
	public static void say(String toSay){
			Main.say(toSay, true, true, true);
	}
	
	public static void say(String toSay, boolean always, boolean verbose, boolean debug){
		
			if (always || Main.isAll() || (verbose&&Main.isVerbose()) || (debug&&Main.isDebug())){
				System.out.println(toSay);
				if (Main.hasGui()){
					Main.getGui().say(toSay+"\n");
				}
				
			}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	public static boolean isVerbose() {
		return verbose;
	}

	public static void setVerbose(boolean verbose) {
		Main.verbose = verbose;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Main.debug = debug;
	}

	public static boolean isAll() {
		return all;
	}

	public static void setAll(boolean all) {
		Main.all = all;
	}
	
	private static boolean hasGui() {
		return Main.hasGui;
	}
	
	public static Gui getGui() {
		return gui;
	}
	
}
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class CalculationsForSingleThrow { 

	public int nrDice;
	public int nrFaces;
	public boolean verbose= true;
	
	public Rules ruleset = new Rules(true, false, false);
	public Strategy strat = new Strategy("ValuePerDice");
//	public Strategy strat = new Strategy("Greedy");

	public WishTree wish;

	private long[] factoriels;
	private Vector<StackWithProba> stacksWithProbas;
	
	public CalculationsForSingleThrow(WishTree tree, int nrDice, int nrFaces) {
		this.nrDice = nrDice;
		this.nrFaces = nrFaces;
		this.wish = tree;

		this.stacksWithProbas = new Vector<StackWithProba>();

		this.initialiseFactoriels();
	}

	private void initialiseFactoriels() {

		this.factoriels = new long[this.nrDice + 1];

		this.factoriels[0] = 1;
		int i = 1;
		while (i <= this.nrDice) {
			this.factoriels[i] = i * this.factoriels[i - 1];
			i++;
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public long[] probaCompleteRec_new(WishTree tree) {
		
		Main.say("starting to compute with following tree:", false, false, true);
		Main.say(tree.toString(), false, false, true);
		
		int nrBranch = tree.getChildren().size();
		if (nrBranch == 0){
			Main.say("this tree has no branches. Probable cause : no wish was doable. setting probability to 0");
			return new long[] {0,0};
		}
		
		boolean[] currIntersectBool = new boolean[nrBranch];
		currIntersectBool[0] = true;
		for (int i = 1; i < nrBranch; i++) {
			currIntersectBool[i] = false;
		}
		
		long[] pairProbaExpect = new long[] {0,0};
		
		Main.say("sorting tree for strategy: " + this.strat.getName());
		
		this.strat.sortTerminalBranchesAscending(tree);
		
		Main.say("sorted. new tree : ");
		Main.say(tree.toString());
		Main.say("-----------------------------------------------------------------------------");
		
		Vector<Vector<WishTree>> alphabeticallySortedIntersections = new Vector<Vector<WishTree>>();
		for (int i =0;i<nrBranch;i++) {
			Vector<WishTree> currFirstLetter = new Vector<WishTree>();
			currFirstLetter.add(tree.getChildren().get(i));
			alphabeticallySortedIntersections.add(currFirstLetter);
		}
		
		pairProbaExpect = probaCompleteNextIntersectionRec_new(false, pairProbaExpect, tree, alphabeticallySortedIntersections);
		
		Main.say("-----------------------------------------------------------------------------");
		Main.say("the probability is " + pairProbaExpect[0]/ Math.pow(nrFaces, nrDice));
		Main.say("the expectancy for strategy : "+ this.strat.getName()+ "is : "+pairProbaExpect[1]/ Math.pow(nrFaces, nrDice));
		Main.say("-----------------------------------------------------------------------------");
		
		
		return pairProbaExpect;
	}
	
	private long[] probaCompleteNextIntersectionRec_new(boolean flipSign, long[] pairProbaExpect, WishTree tree,  Vector<Vector<WishTree>> dictionaryOfIntersections) {
		
		//the naming convention hier is that all terminal branches from the tree are letters
		//intersections are a word formed by these letters.
		
		Vector<Vector<WishTree>> newDictionaryOfIntersections = new Vector<Vector<WishTree>>();
		for (int i =0;i<dictionaryOfIntersections.size()-1;i++) {
			newDictionaryOfIntersections.add(new Vector<WishTree>());//vector for new Words of size n+1size should be 1 less than previous one	
		}
		
//		Main.say("next level of intersection");
//		Main.say(alphabeticallySortedIntersections.toString());
//		Main.say("pair ProbaExpect is : ");
//		Main.say("curr Proba : " + pairProbaExpect[0] + " curr expect : " + pairProbaExpect[1]);
		
		int currStartLetter = 0;
		Vector<WishTree> chapterCurrLeter = dictionaryOfIntersections.get(currStartLetter);
		boolean endOfDictionary = (chapterCurrLeter == null || chapterCurrLeter.isEmpty());
				
		while (! endOfDictionary ) {
			long toAdd =0;
			
			for (WishTree wordStartingWCurrLetter : chapterCurrLeter) {
				//calculate the intersection
				Integer[] currGoal = terminalBranchToGoal(wordStartingWCurrLetter, nrFaces);
				toAdd += probaBranchOpitForRepetitions(currGoal);
				
				//add new Words(=intersections) to new Dictionary
				int currLetterFromTreeIndex = 0;
				WishTree currTreeLetter = tree.getChildren().get(currLetterFromTreeIndex);
				while (currLetterFromTreeIndex< currStartLetter && currStartLetter<=dictionaryOfIntersections.size()) {// will add the n+1elements intersections at the right place
					
					WishTree newWord =  intersectTwoBranches(currTreeLetter, wordStartingWCurrLetter);
					
					if (newWord != null && !newWord.getChildren().isEmpty()) {
						newDictionaryOfIntersections.get(currLetterFromTreeIndex).add(newWord);
					}
					currLetterFromTreeIndex++;
					currTreeLetter = tree.getChildren().get(currLetterFromTreeIndex);
				}
			}
			if (flipSign) {
				pairProbaExpect[0] -= toAdd;
				pairProbaExpect[1] -= toAdd*tree.getChildren().get(currStartLetter).getGain();//should work, assumes tree is sorted for strategy.
			}else {
				pairProbaExpect[0] += toAdd;
				pairProbaExpect[1] += toAdd*tree.getChildren().get(currStartLetter).getGain();//should work, assumes tree is sorted for strategy.
			}
			currStartLetter++;
			if (currStartLetter<dictionaryOfIntersections.size()) {
				chapterCurrLeter = dictionaryOfIntersections.get(currStartLetter);
			}else {
//				chapterCurrLeter = null;
				endOfDictionary = true;
			}
		}
		
		if (newDictionaryOfIntersections.size()!= 0) {
			pairProbaExpect = probaCompleteNextIntersectionRec_new(!flipSign, pairProbaExpect, tree, newDictionaryOfIntersections);
		}else {
			return pairProbaExpect;
		}
		return pairProbaExpect;
	}
	
	private WishTree intersectTwoBranches(WishTree branch1, WishTree branch2) {
		WishTree newIntersection = new WishTree(true, false);
		
		for (int i =1;i<=nrFaces;i++) {
			int newNrChildren = Math.max(occurencesOf(i, branch1),occurencesOf(i, branch2));
			for (int j=0;j<newNrChildren;j++) {
				newIntersection.addChild(new WishTree(i));
			}
		}
		
		if (newIntersection.getChildren().size() > nrDice) {
			return null;
		}else {
//			Main.say("branch1:");
//			Main.say(branch1.toString());
//			
//			Main.say("branch2:");
//			Main.say(branch2.toString());
//			
//			Main.say("intersection:");
//			Main.say(newIntersection.toString());
//			
			return newIntersection;
		}
	}
	
	
	public long[] probaCompleteRec(WishTree tree) {
		
		Main.say("starting to compute with following tree:", false, false, true);
		Main.say(tree.toString(), false, false, true);
		
		int nrBranch = tree.getChildren().size();
		if (nrBranch == 0){
			Main.say("this tree has no branches. Probable cause : no wish was doable. setting probability to 0");
			return new long[] {0,0};
		}
		
		boolean[] currIntersectBool = new boolean[nrBranch];
		currIntersectBool[0] = true;
		for (int i = 1; i < nrBranch; i++) {
			currIntersectBool[i] = false;
		}
		
		long[] pairProbaExpect = new long[] {0,0};
		
		Main.say("sorting tree for strategy" + this.strat.getName());
		
		this.strat.sortTerminalBranchesAscending(tree);
		
		Main.say("sorted. new tree : ");
		Main.say(tree.toString());
		Main.say("-----------------------------------------------------------------------------");
		
		pairProbaExpect = probaCompleteNextIntersectionRec(0, currIntersectBool, pairProbaExpect, tree);
		
		Main.say("-----------------------------------------------------------------------------");
		Main.say("the probability is " + pairProbaExpect[0]/ Math.pow(nrFaces, nrDice));
		Main.say("the expectancy for strategy : "+ this.strat.getName()+ "is : "+pairProbaExpect[1]/ Math.pow(nrFaces, nrDice));
		Main.say("-----------------------------------------------------------------------------");
		
		
		return pairProbaExpect;
	}

	private long[] probaCompleteNextIntersectionRec(int recLevel, boolean[] currIntersec, long[] pairProbaExpect,  WishTree tree) {

		Integer[] currGoal = intersectionToGoal(currIntersec, tree);
		boolean intersectionIsValid;
		intersectionIsValid = !(currGoal == null);

		boolean lastDigit = (recLevel == tree.getChildren().size() - 1);
		
		if (intersectionIsValid ) {
			
			if (!lastDigit) {
				pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
								
				currIntersec[recLevel + 1]= true;
				pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
			}else {
				pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
				
				return pairProbaExpect;
			}
		}
		
		if (!intersectionIsValid && lastDigit) {//
			return pairProbaExpect;
		}

		if (currIntersec[recLevel] == false) {
			return pairProbaExpect;
		}
		
		if (currIntersec[recLevel] == true) {
				currIntersec[recLevel] = false;
				currIntersec[recLevel + 1] = true;
				
				pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
				
				currIntersec[recLevel + 1] = false;
				currIntersec[recLevel]= true;
		}
		
		if (currIntersec[recLevel] != false && currIntersec[recLevel] != true) {
			Main.say("ERROR : currIntersec of anything should be 0 or 1");
		}

		return pairProbaExpect;
	}

	private long[] calculateOneIntersection(boolean[] currIntersec, long[] pairProbaExpect, WishTree tree, Integer[] currGoal, int recLevel) {
		
		
		
		int nrBranchesInIntersection = nrBranchesInIntersection(currIntersec);
		
		double toAdd = (probaBranchOpitForRepetitions(currGoal)) * Math.pow((-1), nrBranchesInIntersection + 1);
//		int gainForIntersection = getGainForIntersection(tree, currIntersec);
		int gainForIntersection = getGainForIntersection_New(tree, currIntersec);
		
		pairProbaExpect[0] += toAdd;
		pairProbaExpect[1] += toAdd*gainForIntersection;
		
		//writing this takes ca 6 sec out of 48 for tenthousands full
//		Main.say("");
////		Main.say("current intersection : " + intArrayToString(currIntersec) + ". Rec level : "+ recLevel);
//		Main.say("current intersection : " + intersectionToString(currIntersec) + ". Rec level : "+ recLevel);
////		if (toAdd == 15 || toAdd == -15) {
////			Main.say("current goal : " +intArrayToString(currGoal) + " Favourable cases : " + toAdd );
////		}
//		Main.say("current goal : " +intArrayToString(currGoal) + " Favourable cases : " + toAdd );
//		Main.say("--Adding " + (double) toAdd / Math.pow(this.nrFaces, this.nrDice) + " to the proba (" + toAdd + " favourable cases)");
//		Main.say("--Multiplying with Gain (" + gainForIntersection + ", strategy : "+ this.strat.getName()+ ") : adding " + toAdd*gainForIntersection  / Math.pow(this.nrFaces, this.nrDice) + " to the expectancy");

//		outputIntersectionResults(currIntersec, currGoal, toAdd, gainForIntersection);
		
		return pairProbaExpect;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	public long ProbaBranch(Integer[] goal) { // changed the calculations for extra dice, TODO check if it breaks things

		long res;

		int goalSize = 0;
		for (int i = 0; i < this.nrFaces; i++) {
			goalSize += goal[i];
		}

		// TODO needs a safeguard somewhere, nrdice can not be bigger than goalsize
		int extraDice = this.nrDice - goalSize; 

		if (extraDice == 0) {

			long tempProd = 1;
			for (int i = 0; i < nrFaces; i++) {
				tempProd *= factorielle(goal[i]);
			}

			oldTest(nrDice, tempProd);
			
			
			res = ((factorielle(this.nrDice)) / (tempProd));
			
			return res;

		} else { // if some dice do not have constraints, the proba of the goal
					// is the sum of the probas of all possibilities with those extra dice

			Vector<Integer[]> sortedExtraDice = sortingExtraDice(extraDice);
			int tempSum = 0;

			for (Integer[] epsilon : sortedExtraDice) {
				Integer[] newGoal = new Integer[this.nrFaces];
				for (int i = 0; i < this.nrFaces; i++) {
					newGoal[i] = goal[i] + epsilon[i];
				}
				tempSum += ProbaBranch(newGoal);
			}
			return tempSum;
		}

	}

	public long probaBranchOpitForRepetitions(Integer[] goal) { 
		long favourableCases;
		// TODO (when finished) check if that helps
//		this.dbgNrCallsprobaBranch++;
//		Main.say(intArrayToString(goal));
		
		for (StackWithProba stack : this.stacksWithProbas) {
			if (hasStack(goal, stack.stacks)) {
				return stack.favourableCases;
//				favourableCases = stack.favourableCases;
			}
		}
		favourableCases = ProbaBranch(goal);
		addToStacksWithProbas(goal, favourableCases);

//		Main.say("Intersection : " + intArrayToString(goal) + " favourable cases : " + favourableCases);
		
		return favourableCases;
	}

	private void addToStacksWithProbas(Integer[] goal, long favourableCases) {
		Integer[] stacks = new Integer[this.nrFaces];
		for (int i = 0; i < this.nrFaces; i++) {
			stacks[i] = 0;
		}

		for (int i = 0; i < this.nrFaces; i++) {
			for (int j = 0; j < this.nrFaces; j++) {
				if (goal[j] == i) { // (goal1[j] == null && i == 0 ) ||
					stacks[i]++;
				}
			}
		}

		StackWithProba newStack = new StackWithProba(stacks, favourableCases);

		this.stacksWithProbas.add(newStack);

	}
	
	private boolean hasStack(Integer[] goal, Integer[] stack) {
		Integer[] stacks1 = new Integer[this.nrFaces];
		for (int i = 0; i < this.nrFaces; i++) {
			stacks1[i] = 0;
		}

		for (int i = 0; i < this.nrFaces; i++) {
			for (int j = 0; j < this.nrFaces; j++) {
				if (goal[j] == i) { // (goal1[j] == null && i == 0 ) ||
					stacks1[i]++;
				}
			}
		}

		boolean res = true;

		for (int i = 0; i < this.nrFaces; i++) {
			if (stacks1[i] != stack[i]) {
				res = false;
			}
		}

		return res;

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private int getGainForIntersection_New(WishTree tree, boolean[] currIntersec) {
		if ( !currIntersec[0] && !currIntersec[1] && currIntersec[2]) {
			boolean tmp = false;
		}
		Vector<WishTree> branches = intersectionToVectorBranches(tree, currIntersec);
		WishTree worstBranch = this.strat.giveWorstBranch(branches);
		return worstBranch.getGain();
	}
	
	private int getGainForIntersection(WishTree tree, boolean[] currIntersec) {
		//this can depend on strategy, for instance, you might want to sacrifice points to keep more dice.
		//this one is the greedy strategy, I always keep the highest gain.
		Vector<Integer> gainVector = getBranchGains(tree, currIntersec); 
		int gainToKeep;

		gainToKeep = Collections.min(gainVector);
		
		return gainToKeep;
	}

	private Vector<Integer> getBranchGains(WishTree tree, boolean[] currIntersec) {
//		Integer[] gainArray = new Integer[nrBranchesInIntersection(currIntersec)];
		Vector<Integer> gainArray = new Vector<>();
//		Main.say("Curr elements in intersection : ");
		for (int i = 0;i<currIntersec.length;i++){
			if (currIntersec[i]==true){
				WishTree currBranch = tree.getChildren().get(i);
//				Main.say(currBranch.toString());
				gainArray.add(currBranch.getGain());
			}
		}
		return gainArray;
	}
	
	public static Vector<Integer> getBranchGains(Vector<WishTree> branches) {
		Vector<Integer> gainArray = new Vector<>();
		for (WishTree currBranch : branches){
			gainArray.add(currBranch.getGain());
		}
		return gainArray;
	}

	private Integer[] intersectionToGoal(boolean[] currIntersec,
			WishTree tree) {

		List<WishTree> tempChildren = childrenToIntersectArray(currIntersec, tree);
		Integer[] currGoal = new Integer[this.nrFaces];
		for (int i = 0; i < this.nrFaces; i++) {
			currGoal[i] = 0;
		}

		int i = 0;
		boolean weiter = true;
		while ((i < tempChildren.size()) && weiter) {
			Integer[] tempGoal = terminalBranchToGoal(tempChildren.get(i), nrFaces);
			Integer[] testGoal = intersectTwoGoals(currGoal, tempGoal);
			int testSize = sizeOfGoal(testGoal);
			if (testSize > this.nrDice) {
				return null;
			} else {
				currGoal = testGoal;
				i++;
			}
		}

		return currGoal;
	}
	
	private void oldTest(int nrDice, long tempProd) {
		double test = ((double) (factorielle(this.nrDice)) / (tempProd));
		if (test != (long) test) {
			Main.say("" + test + " should be an whole number");
			Main.say("possible problem : nrDice! might be bigger than MAX INT ");
			Main.say(" max int = " + Integer.MAX_VALUE + "; and (nrDice-1)! = " + factorielle(nrDice - 1));
		}
	}

	private Vector<WishTree> intersectionToVectorBranches(WishTree tree, boolean[] currIntersection){
		Vector<WishTree> res = new Vector<WishTree>();
		for (int i =0;i<currIntersection.length;i++) {
			if (currIntersection[i]) {
				res.add(tree.getChildren().get(i));
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	private Vector<Integer[]> sortingExtraDice(int extraDice) { 
		// return all possible combinations of faces for extraDice number of dice
		
		Vector<Integer[]> res = new Vector<Integer[]>();

		Integer[] startState = new Integer[nrFaces];
		startState[0] = extraDice;
		for (int i = 1; i < nrFaces; i++) {
			startState[i] = 0;
		}

		sortingExtraDiceRec(1, extraDice, this.nrFaces, startState, res);

		return res;
	}

	private void sortingExtraDiceRec(int recLevel, int maxRecLevel,
			int currMaxRank, Integer[] currState, Vector<Integer[]> toFill) {

		int k = 0;
		while (k < currMaxRank) {
			toFill.add(currState.clone());
			currState[k]--;
			k++;
			if (!(k < this.nrFaces)) {// the only moment where this should
										// happen is when the vector is
										// complete, and reclevel = 1
				break;
			} else {
				currState[k]++;
			}
			for (int i = 1; i < k; i++) {
				currState[0] += currState[i];
				currState[i] = 0;
			}

			if (recLevel < maxRecLevel) {
				sortingExtraDiceRec(recLevel + 1, maxRecLevel, k, currState,
						toFill);
			}

		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public int sizeOfGoal(Integer[] goal) {
		int res = 0;
		for (int i = 0; i < this.nrFaces; i++) {
			res += goal[i];
		}
		return res;
	}

	public int nrBranchesInIntersection(boolean[] array) {
		int res = 0;
		int i = 0;
		while (i < array.length) {
			if (array[i] == true) {
				res ++;
			}
//			res += array[i];
			i++;
		}
		return res;
	}

	public Integer[] intersectTwoGoals(Integer[] goal1, Integer[] goal2) {
		for (int i = 0; i < this.nrFaces; i++) {
			goal1[i] = Math.max(goal1[i], goal2[i]);
		}

		return goal1;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	private List<WishTree> childrenToIntersectArray(boolean[] intersection,
			WishTree tree) {
		List<WishTree> res = new Vector<WishTree>();

		int k = 0;
		while (k < tree.getChildren().size()) { // TODO check border cases
			if (intersection[k] == true) {
				res.add(tree.getChildren().get(k));
			}
			k++;
		}

		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static Integer[] terminalBranchToGoal(WishTree terminalBranch, int nrFaces) {
		Integer[] goal = new Integer[nrFaces];
		for (int i = 0; i < nrFaces; i++) {
			goal[i] = occurencesOf(i + 1, terminalBranch);
		}

		return goal;
	}

	private static int occurencesOf(int i, WishTree lastBranch) {
		int res = 0;
		for (WishTree child : lastBranch.getChildren(false, false, true)) {
			if (child.getNr() == i) {
				res++;
			}
		}
		return res;
	}

	public double divFact(int n, int k) { // returns n!/k! only call with k<n
		int res = 1;
		while (n > k) {
			res *= n;
			n--;
		}
		return res;
	}

	private long factorielle(int i) {

		if (i <= this.nrDice) {
			return this.factoriels[i];
		} else {
			Main.say("computing " + i + "!, but nrDice is "
					+ this.nrDice);
			long res = 1;

			while (i > 1) {
				res *= i;
				i--;
			}

			return res;
		}

	}

	private String intersectionToString(boolean[] currIntersec) {
		String res = "{";
		for (int i=0;i<currIntersec.length;i++) {
			if (currIntersec[i]) {
				res += "" + (i+1) + ",";
			}
		}
		res += "}";
		return res;
		
//		String res = "[";
//		for (int i=0;i<currIntersec.length-1;i++) {
//			if (currIntersec[i]) {
//				res += "1,";
//			}else {
//				res += "0,";
//			}
//		}
//		int i=currIntersec.length-1;
//		if (currIntersec[i]) {
//			res += "1,";
//		}else {
//			res += "0,";
//		}
//		res += "]";
//		return res;
	}

	
	public String intArrayToString(Integer[] array){
		String res = "[";
		int i = 0;
		while (i<array.length && array[i] != null){
			res += "" + array[i]+",";
			i++;
		}
		res = res.substring(0, res.length() - 1) + "]";
		return res;
	}

	
}


//private long[] probaCompleteNextIntersectionRec_New(int recLevel, boolean[] currIntersec, long[] pairProbaExpect,  WishTree tree) {
//
//	Integer[] currGoal = intersectionToGoal(currIntersec, tree);
//	boolean intersectionIsValid;
//	intersectionIsValid = !(currGoal == null);
//
//	boolean lastDigit = (recLevel == tree.getChildren().size() - 1);
//	
//	if (intersectionIsValid ) {//2024-03 modification && !lastDigit
//		
//		if (!lastDigit) {
//			pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//			
//			currIntersec[recLevel + 1]= true;
//			pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
//		}else {
//			pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//			
//			return pairProbaExpect;
//		}
//	}
//	
//	if (!intersectionIsValid && lastDigit) {//
//		return pairProbaExpect;
//	}
//
//	if (currIntersec[recLevel] == false) {
//		return pairProbaExpect;
//	}
//	
//	if (currIntersec[recLevel] == true) {
//
//		currIntersec[recLevel] = false;
//		currIntersec[recLevel + 1] = true;
//
//		pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
//
//		currIntersec[recLevel + 1] = false;
//		currIntersec[recLevel]= true;
//
//	}
//	
//	if (currIntersec[recLevel] != false && currIntersec[recLevel] != true) {
//		Main.say("ERROR : currIntersec of anything should be 0 or 1");
//	}
//
//	return pairProbaExpect;
//}

//private void removeBigValues(WishTree developpedNormalisedTree){ 
//	Vector<WishTree> toRemove = new Vector<WishTree>();
//	
//	Vector<WishTree> andChildren = developpedNormalisedTree.getChildren(true, false, false);
//	Vector<WishTree> orChildren = developpedNormalisedTree.getChildren(false, true, false);
//	Vector<WishTree> numberChildren = developpedNormalisedTree.getChildren(false, false, true);
//	
//	for (WishTree child : numberChildren){
//		if (child.getNr() > this.nrFaces){
//			toRemove.add(child);
//		}
//	}
//	for (WishTree child : andChildren){
//		boolean removeChild = false;
//		for (WishTree grandchild : child.getChildren()){
//			if (grandchild.getNr()>this.nrFaces && !removeChild){
//				toRemove.add(child);
//				removeChild = true;
//			}
//		}
//	}
//	
//	for (WishTree child : orChildren){
//		Vector<WishTree> toRemoveFromOr = new Vector<WishTree>();
//		for (WishTree grandChild : child.getChildren()){
//			if (grandChild.getNr()>this.nrFaces){
//				toRemoveFromOr.add(grandChild);
//			}
//		}
//		for (WishTree grandChildToRemove : toRemoveFromOr){
//			child.getChildren().remove(grandChildToRemove);
//		}
//	}
//	
//	for (WishTree child : toRemove){
//		Main.say("this node : " + child.toString() + "is greater than number of faces (" + nrFaces + "). Removing...");
//		developpedNormalisedTree.getChildren().remove(child);
//		Main.say("removed");
//	}
//}



//public String goalToString(Integer[] goal) {
//	String res = "[";
//	for (int i = 0; i < this.nrFaces - 1; i++) {
//		res += "" + goal[i] + ",";
//	}
//	res += "" + goal[this.nrFaces - 1] + "]";
//	return res;
//}


//
//private WishTree sortTerminalBranchesAscendingDiceUsed(WishTree treeToSort) {
//	//lazy for now. TODO later : reasonable sort
//	int nrBranches = treeToSort.getChildren().size();
//	Vector<WishTree> sortedChildren = new Vector<>();
//	
//	int i = 0;
//	while (treeToSort.getChildren() != null && treeToSort.getChildren().size()!= 0) {
//		
//		WishTree nextToAdd = treeToSort.getChildren().get(0);
//		int currNrDice = nextToAdd.getChildren().size();
//		for (WishTree child : treeToSort.getChildren()) {
//			if (child.getChildren().size()	< currNrDice) {
//				nextToAdd = child;
//				currNrDice = child.getChildren().size();
//			}
//		}
//		treeToSort.getChildren().remove(nextToAdd);
//		sortedChildren.add(nextToAdd);
//	}
//	treeToSort.resetChildren();
//	treeToSort.addChildren(sortedChildren);
//	Main.say("Sorted Tree : ");
//	Main.say(treeToSort.toString());
//	return treeToSort;
//}
//
//private long[] probaCompleteNextIntersectionRec_NewAssumeSortedBranches(int recLevel, Integer[] currIntersec, long[] pairProbaExpect,  WishTree tree) {
//	//TODO wrong because the intersections take the max and not the sum of each face.
//	Integer[] currGoal = intersectionToGoal(currIntersec, tree);
//	boolean intersectionIsNotNull;
//	intersectionIsNotNull = !(currGoal == null);
//
//	boolean lastDigit = (recLevel == tree.getChildren().size() - 1);
//	
//	if (intersectionIsNotNull && !lastDigit) {//2024-03 modification
//		
//		pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//		
//		currIntersec[recLevel + 1]++;
//		pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
//	}
//
//	if (intersectionIsNotNull && lastDigit) {
//
//		pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//		
//		return pairProbaExpect;
//	}
//	
//	if (!intersectionIsNotNull && lastDigit) {
//		return pairProbaExpect;
//	}
//
//	if (currIntersec[recLevel] == 0) {
//		return pairProbaExpect;
//	}
//	
//	if (currIntersec[recLevel] == 1) {
//		currIntersec[recLevel] = 0;
//		currIntersec[recLevel + 1] = 1;
//		
//		pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
//		
//		currIntersec[recLevel + 1]--;
//		currIntersec[recLevel]++;
//	}
//	
//	if (currIntersec[recLevel] != 0 && currIntersec[recLevel] != 1) {
//		Main.say("ERROR : currIntersec of anything should be 0 or 1");
//	}
////	
////	if (!calculateIntersection && !lastDigit) {
////		// currIntersec[recLevel] = 0;
////		// return res;
////	}
//
//	return pairProbaExpect;
//}



//public long ProbaBranch_new(Integer[] goal) {
//	long res;
//
//	
//	long tempProd = 1;
//	int remainingDice = this.nrDice;
//	for (int i = 0; i < nrFaces; i++) {
//		if (goal[i] != 0) {
//			tempProd *= bernouilliNrCases(remainingDice, goal[i], nrFaces);
//			remainingDice -= goal[i];
//		}
//		
//	}
////	tempProd *= Math.pow(nrFaces, remainingDice);
//	
//	return tempProd;
//	
//	
//}
//
//public long bernouilliNrCases(int n, int k, int nrFaces) {
//	//Warning : this never divides with the total nr cases and is therefore no proba but a nr of cases
//	long res = (long) (divFact(n, k)/factorielle(n-k));
//	res *= Math.pow(nrFaces-1, n-k);
//	
//	return res;
//}


//private void outputIntersectionResults(Integer[] currIntersec,	Integer[] currGoal, double toAdd, int gainForIntersection) {
//	Main.say();
//	Main.say("current intersection : " + intArrayToString(currIntersec) + ". Rec level : "+ recLevel);
//	Main.say("current goal : " +intArrayToString(currGoal));
//	Main.say("--Adding " + (double) toAdd / Math.pow(this.nrFaces, this.nrDice) + " to the proba (" + toAdd + " favourable cases)");
//	Main.say("--Multiplying with Gain (" + gainForIntersection + ", greedy stragety) : adding " + toAdd*gainForIntersection  / Math.pow(this.nrFaces, this.nrDice) + " to the expectancy");
//}
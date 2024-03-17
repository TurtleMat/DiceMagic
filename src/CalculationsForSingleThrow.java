import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.lang.model.type.ArrayType;

public class CalculationsForSingleThrow { // this is wrong !!

	public int nrDice;
	public int nrFaces;
	public boolean verbose= true;

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

	public long[] probaCompleteRec(WishTree tree) {
		if (verbose) {
			System.out.println("starting to compute");
		}
//		removeBigChildren(tree);
		removeBigValues(tree);
		if (verbose){
			System.out.println("new tree :");
			System.out.println(tree.toString());
		}
		
		int nrBranch = tree.getChildren().size();
		if (nrBranch == 0){
			System.out.println("this tree has no branches. Probable cause : no wish was doable. setting probability to 0");
			return new long[] {0,0};
		}
		Integer[] currIntersec = new Integer[nrBranch];
		currIntersec[0] = 1;
		for (int i = 1; i < nrBranch; i++) {
			currIntersec[i] = 0;
		}
		
		long[] pairProbaExpect = new long[] {0,0};
		pairProbaExpect = probaCompleteNextIntersectionRec(0, currIntersec, pairProbaExpect, tree);

		return pairProbaExpect;
	}

	private long[] probaCompleteNextIntersectionRec(int recLevel, Integer[] currIntersec, long[] pairProbaExpect,  WishTree tree) {

		Integer[] currGoal = intersectionToGoal(currIntersec, tree);
		boolean intersectionIsNotNull;
		intersectionIsNotNull = !(currGoal == null);
		boolean lastDigit = (recLevel == tree.getChildren().size() - 1);
		
		if (intersectionIsNotNull && !lastDigit) {//2024-03 modification
			
			pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
			
			currIntersec[recLevel + 1]++;
			pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
		}

		if (intersectionIsNotNull && lastDigit) {

			pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
			
			return pairProbaExpect;
		}
		
		if (!intersectionIsNotNull && lastDigit) {
			return pairProbaExpect;
		}

		if (currIntersec[recLevel] == 0) {
			return pairProbaExpect;
		}
		
		if (currIntersec[recLevel] == 1) {
			currIntersec[recLevel] = 0;
			currIntersec[recLevel + 1] = 1;
			
			pairProbaExpect = probaCompleteNextIntersectionRec(recLevel + 1, currIntersec, pairProbaExpect, tree);
			
			currIntersec[recLevel + 1]--;
			currIntersec[recLevel]++;
		}
		
		if (currIntersec[recLevel] != 0 && currIntersec[recLevel] != 1) {
			System.out.println("ERROR : currIntersec of anything should be 0 or 1");
		}
//		
//		if (!calculateIntersection && !lastDigit) {
//			// currIntersec[recLevel] = 0;
//			// return res;
//		}

		return pairProbaExpect;
	}

	private long[] calculateOneIntersection(Integer[] currIntersec, long[] pairProbaExpect, WishTree tree, Integer[] currGoal, int recLevel) {
		
		int nrBranchesInIntersection = nrBranchesInIntersection(currIntersec);
		
		double toAdd = (probaBranchOpitForRepetitions(currGoal)) * Math.pow((-1), nrBranchesInIntersection + 1);
		int gainForIntersection = getGainForIntersection(tree, currIntersec, nrBranchesInIntersection);
		pairProbaExpect[0] += toAdd;
		pairProbaExpect[1] += toAdd*gainForIntersection;
		
		System.out.println();
		System.out.println("current intersection : " + intArrayToString(currIntersec) + ". Rec level : "+ recLevel);
		System.out.println("current goal : " +intArrayToString(currGoal));
		System.out.println("--Adding " + (double) toAdd / Math.pow(this.nrFaces, this.nrDice) + " to the proba (" + toAdd + " favourable cases)");
		System.out.println("--Multiplying with Gain (" + gainForIntersection + ", greedy stragety) : adding " + toAdd*gainForIntersection  / Math.pow(this.nrFaces, this.nrDice) + " to the expectancy");

//		outputIntersectionResults(currIntersec, currGoal, toAdd, gainForIntersection);
		
		return pairProbaExpect;
	}

//	private void outputIntersectionResults(Integer[] currIntersec,	Integer[] currGoal, double toAdd, int gainForIntersection) {
//		System.out.println();
//		System.out.println("current intersection : " + intArrayToString(currIntersec) + ". Rec level : "+ recLevel);
//		System.out.println("current goal : " +intArrayToString(currGoal));
//		System.out.println("--Adding " + (double) toAdd / Math.pow(this.nrFaces, this.nrDice) + " to the proba (" + toAdd + " favourable cases)");
//		System.out.println("--Multiplying with Gain (" + gainForIntersection + ", greedy stragety) : adding " + toAdd*gainForIntersection  / Math.pow(this.nrFaces, this.nrDice) + " to the expectancy");
//	}
	
	private int getGainForIntersection(WishTree tree, Integer[] currIntersec, int nrBranchesInIntersection) {
		//this can depend on strategy, for instance, you might want to sacrifice points to keep more dice.
		//this one is the greedy strategy, I always keep the highest gain.
		Vector<Integer> gainVector = getGainFromIntersection(tree, currIntersec); 
		int gainToKeep;
		
		if (nrBranchesInIntersection%2 == 1){ //if we add the value, we want the highest gain 
//			gainToKeep = Collections.max(gainVector);
			gainToKeep = Collections.min(gainVector);
		}
		else{
			gainToKeep = Collections.min(gainVector);
		}
		
		return gainToKeep;
	}

	private Vector<Integer> getGainFromIntersection(WishTree tree, Integer[] currIntersec) {
//		Integer[] gainArray = new Integer[nrBranchesInIntersection(currIntersec)];
		Vector<Integer> gainArray = new Vector<>();
//		System.out.println("Curr elements in intersection : ");
		for (int i = 0;i<currIntersec.length;i++){
			if (currIntersec[i]==1){
				WishTree currBranch = tree.getChildren().get(i);
//				System.out.println(currBranch.toString());
				gainArray.add(currBranch.getGain());
			}
		}
		return gainArray;
	}

	private Integer[] intersectionToGoal(Integer[] currIntersec,
			WishTree tree) {

		List<WishTree> tempChildren = childrenToIntersectArray(currIntersec, tree);
		Integer[] currGoal = new Integer[this.nrFaces];
		for (int i = 0; i < this.nrFaces; i++) {
			currGoal[i] = 0;
		}

		int i = 0;
		boolean weiter = true;
		while ((i < tempChildren.size()) && weiter) {
			Integer[] tempGoal = terminalBranchToGoal(tempChildren.get(i));
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

	public long ProbaBranch(Integer[] goal) {

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

			double test = ((double) (factorielle(this.nrDice)) / (tempProd));
			if (test != (long) test) {
				System.out.println("" + test + " should be an whole number");
				System.out.println("possible problem : nrDice! might be bigger than MAX INT ");
				System.out.println(" max int = " + Integer.MAX_VALUE + "; and (nrDice-1)! = " + factorielle(nrDice - 1));
			}
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
		// TODO (when finished) check if that helps

		for (StackWithProba stack : this.stacksWithProbas) {
			if (hasStack(goal, stack.stacks)) {
				return stack.favourableCases;
			}
		}
		// System.out.println("calculating new : ");
		long favourableCases = ProbaBranch(goal);
		addToStacksWithProbas(goal, favourableCases);

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

	private void removeBigValues(WishTree developpedNormalisedTree){ 
		Vector<WishTree> toRemove = new Vector<WishTree>();
		
		Vector<WishTree> andChildren = developpedNormalisedTree.getChildren(true, false, false);
		Vector<WishTree> orChildren = developpedNormalisedTree.getChildren(false, true, false);
		Vector<WishTree> numberChildren = developpedNormalisedTree.getChildren(false, false, true);
		
		for (WishTree child : numberChildren){
			if (child.getNr() > this.nrFaces){
				toRemove.add(child);
			}
		}
		for (WishTree child : andChildren){
			boolean removeChild = false;
			for (WishTree grandchild : child.getChildren()){
				if (grandchild.getNr()>this.nrFaces && !removeChild){
					toRemove.add(child);
					removeChild = true;
				}
			}
		}
		
		for (WishTree child : orChildren){
			Vector<WishTree> toRemoveFromOr = new Vector<WishTree>();
			for (WishTree grandChild : child.getChildren()){
				if (grandChild.getNr()>this.nrFaces){
					toRemoveFromOr.add(grandChild);
				}
			}
			for (WishTree grandChildToRemove : toRemoveFromOr){
				child.getChildren().remove(grandChildToRemove);
			}
		}
		
		for (WishTree child : toRemove){
			System.out.println("this node : " + child.toString() + "is greater than number of faces (" + nrFaces + "). Removing...");
			developpedNormalisedTree.getChildren().remove(child);
			System.out.println("removed");
		}
	}

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

	public int nrBranchesInIntersection(Integer[] array) {
		int res = 0;
		int i = 0;
		while (i < array.length) {
			res += array[i];
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
	// ------------------------------------------------------------------------------------------------------------------------------------

	private List<WishTree> childrenToIntersectArray(Integer[] intersection,
			WishTree tree) {
		List<WishTree> res = new Vector<WishTree>();

		int k = 0;
		while (k < tree.getChildren().size()) { // TODO check border cases
			if (intersection[k] == 1) {
				res.add(tree.getChildren().get(k));
			}
			k++;
		}

		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public Integer[] terminalBranchToGoal(WishTree terminalBranch) {
		Integer[] goal = new Integer[nrFaces];
		for (int i = 0; i < nrFaces; i++) {
			goal[i] = occurencesOf(i + 1, terminalBranch);
		}

		return goal;
	}

	private int occurencesOf(int i, WishTree lastBranch) {
		int res = 0;
		for (WishTree child : lastBranch.getChildren()) {
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
			System.out.println("computing " + i + "!, but nrDice is "
					+ this.nrDice);
			long res = 1;

			while (i > 1) {
				res *= i;
				i--;
			}

			return res;
		}

	}

//	public String goalToString(Integer[] goal) {
//		String res = "[";
//		for (int i = 0; i < this.nrFaces - 1; i++) {
//			res += "" + goal[i] + ",";
//		}
//		res += "" + goal[this.nrFaces - 1] + "]";
//		return res;
//	}
	
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

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.FactoryConfigurationError;


public class Throw { // this is wrong !!
	
	public int nrDice;
	public int nrFaces;
	
	public WishTree wish;
	
	private long[] factoriels;
	
	private Vector<StackWithProba> stacksWithProbas;
	
	

	public Throw (WishTree tree, int nrDice, int nrFaces){
		this.nrDice = nrDice;
		this.nrFaces = nrFaces;
		this.wish = tree;
		
		this.stacksWithProbas = new Vector<StackWithProba>();
		
		this.initialiseFactoriels();
	}
	
	private void initialiseFactoriels() {
		
		this.factoriels = new long[this.nrDice +1];
		
		this.factoriels[0] = 1;
		int i = 1;
		while (i<=this.nrDice){
			this.factoriels[i] = i*this.factoriels[i-1];
			i++;
		}

	}

//------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------	
	public long ProbaComplete (WishTree tree){// only call on developp and normalised trees
		//at this point, node should be a OR with terminal AND children
		
		
		removeBigChildren(tree);
		
		
		
//		float res = 0;
		long res = 0;
		int nrBranch = tree.getChildren().size(); 
		
		for (int i = 1; i<Math.pow(2, nrBranch); i++){ // the binary representation of i tells which child to consider
			// TODO opti : can delete the values of i that are superfluous (because biger intersection than some that were already rejected)
//			//TODO opti : the for has to be replaces with a recursive thing that doesn't go deeper if the intersection is not to be calculated
			
			System.out.println(Integer.toBinaryString(i) + " is intersection i in binary");
			
			List<WishTree> tempChildren;
//			WishTree currIntersec;
			tempChildren = childrenToIntersect( i, tree);
			boolean calculateTheIntersection = true;
//			currIntersec = intersectBranches(tempChildren);
			
			Integer[] currGoal = new Integer[this.nrFaces];
			for (int j =0; j<this.nrFaces;j++){
				currGoal[j] = 0;
			}
			
			int j = 0;
			int tempSize = 0;
			boolean weiter = true;
			while ((j<tempChildren.size()) && weiter ){//cardinal(currGoal) < this.nrDice
				Integer[] tempGoal = terminalBranchToIntArray(tempChildren.get(j));
//				int tempSize2 = sizeOfGoal(tempGoal);
				
				Integer[] testGoal = intersectGoals(currGoal, tempGoal);
				int testSize = sizeOfGoal(testGoal);
				if (testSize > this.nrDice){// this condition is false
					calculateTheIntersection = false;
					weiter = false;
				} else {
					currGoal = testGoal;
					tempSize = testSize;
					j++;
				}
			}
			
			if (calculateTheIntersection){
//				Integer[] currGoal = terminalBranchToIntArray(currIntersec);
				
//				int toAdd = (int) ( ProbaBranch(currGoal) * Math.pow( (-1),tempChildren.size()+1 ) );
				int toAdd = (int) ( probaBranchOpitForRepetitions((currGoal)) * Math.pow( (-1),tempChildren.size()+1 ) );
				
//				if (toAdd != toAdd2){
//					System.out.println("ERROR in probaBranchOpitForRepetitions");
//				}
				 
				System.out.println("Adding " + (double)toAdd/Math.pow(this.nrFaces, this.nrDice) + " to the proba (" + toAdd + " favourable cases)");
				System.out.println(goalToString(currGoal));
				
				long testMaxRes = res;
				
				res += toAdd;
				if (res<0){
					System.out.println("The result is " + res);
					System.out.println("previous result was : " + testMaxRes);
				}
			}
		}
		
//		return (double) (res*(1/Math.pow(this.nrFaces, this.nrDice)));
		return res;
	}

	public long ProbaBranch (Integer[] goal){ 
//		if (!terminalBranch.getNode().isAND()){
//			System.out.println("ERROR lastbranch called on a non AND node");
//		}
		long res;
		
		int goalSize = 0;
		for (int i = 0; i<this.nrFaces; i++){
			goalSize += goal[i];
		}
		
		int extraDice = this.nrDice - goalSize; // TODO needs a safeguard somewhere, nrdice can not be bigger than goalsize
		
		if ( extraDice==0){
			
			long tempProd = 1;
			for (int i = 0; i<nrFaces; i++){
				tempProd *= factorielle(goal[i]);
			}
			
			double test = ((double)(factorielle(this.nrDice)) / (tempProd)) ;
			if (test!= (long) test){
				System.out.println("" + test + " should be an whole number");
				System.out.println("possible problem : nrDice! might be bigger than MAX INT ");
				System.out.println(" max int = " + Integer.MAX_VALUE + "; and (nrDice-1)! = " + factorielle(nrDice -1));
			}
			res = ((factorielle(this.nrDice)) / (tempProd)) ;
			return res;
			
		}else { // if some dice do not have constraints, the proba of the goal is the sum of the probas of sll possibilities with those extra dice

			Vector<Integer[]> sortedExtraDice = sortingExtraDice(extraDice);
			int tempSum = 0;

			for (Integer[] epsilon : sortedExtraDice){
				Integer[] newGoal = new Integer[this.nrFaces];
				for (int i = 0; i<this.nrFaces; i++){
					newGoal[i] = goal[i] + epsilon[i];
				}
				tempSum += ProbaBranch(newGoal);
			}
			return tempSum;
			
		}

	}
	
	public long probaBranchOpitForRepetitions (Integer[] goal){ // TODO (when finished) check if that helps
		
		for (StackWithProba stack : this.stacksWithProbas){
			if (hasStack(goal, stack.stacks)){
				System.out.println("using saved stacks with proba : (saved probas : " + this.stacksWithProbas.size() + ")");
				return stack.favourableCases;
			}
		}
		//only comes till here if the stack doesn't have a proba yet
		System.out.println("calculating new : ");
		long favourableCases = ProbaBranch(goal);
		addToStacksWithProbas(goal, favourableCases);
		
		return favourableCases;
	}
	
	private void addToStacksWithProbas(Integer[] goal, long favourableCases) {
		Integer[] stacks = new Integer[this.nrFaces];
		for (int i =0;i<this.nrFaces;i++){
			stacks[i] = 0;
		}
		
		for (int i = 0;i<this.nrFaces;i++){
			for (int j = 0;j<this.nrFaces;j++){
				if ( goal[j] == i){ //(goal1[j] == null && i == 0 ) ||
					stacks[i]++;
				}
			}
		}
		
		StackWithProba newStack = new StackWithProba(stacks, favourableCases);
		
		this.stacksWithProbas.add(newStack);
		
	}

	private void removeBigChildren(WishTree developpNormalisedTree){
		
		Vector<WishTree> toRemove = new Vector<WishTree>();// supresses children that have more numbers than this.nrDice
		for (WishTree child : developpNormalisedTree.getChildren()){
			if (child.getChildren().size()>this.nrDice){
				toRemove.add(child);
			}
		}
		developpNormalisedTree.getChildren().removeAll(toRemove);
		
		if (toRemove.size()>0){
			System.out.println("removed children that were too big. new tree : ");
			System.out.println(developpNormalisedTree.toString());
		}
		
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------

	private Vector<Integer[]> sortingExtraDice(int extraDice) { // return all possible combinations of faces for extraDice number of dice
		Vector<Integer[]> res = new Vector<Integer[]>();
		
		Integer[] startState = new Integer[nrFaces];
		startState[0] = extraDice;
		for (int i = 1; i<nrFaces; i++){
			startState[i] = 0;
		}

		sortingExtraDiceRec(1, extraDice, this.nrFaces, startState, res);
		
		return res;
	}
		
	private void sortingExtraDiceRec (int recLevel, int maxRecLevel, int currMaxRank, Integer[] currState, Vector<Integer[]> toFill){

		int k = 0;
		while (k<currMaxRank)	{
			toFill.add(currState.clone());
			currState[k]--;
			k++;
			if (!(k<this.nrFaces)){//the only moment where this should happen is when the vector is complete, and reclevel = 1
				break;
			} else {
				currState[k]++;
			}
			for (int i =1;i<k;i++){
				currState[0] += currState[i];
				currState[i] = 0;
			}
			
			if (recLevel<maxRecLevel){
				sortingExtraDiceRec(recLevel +1, maxRecLevel, k, currState, toFill);
			}
			
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------

	public int sizeOfGoal(Integer[] goal){
		int res = 0;
		for (int i =0;i<this.nrFaces;i++){
			res += goal[i];
		}
		return res;
	}
	
	public Integer[] intersectGoals(Integer[] goal1, Integer[] goal2){
		for (int i = 0; i < this.nrFaces;i++){
			goal1[i] = Math.max(goal1[i], goal2[i]);
		}
		
		return goal1;
	}

	private boolean areSimilar(Integer[] goal1, Integer[] goal2){ // TODO check if goal[i] can be null or if it's initialised to 0
		Integer[] stacks1 = new Integer[this.nrFaces];
		for (int i =0;i<this.nrFaces;i++){
			stacks1[i] = 0;
		}
		Integer[] stacks2 = new Integer[this.nrFaces];
		for (int i =0;i<this.nrFaces;i++){
			stacks2[i] = 0;
		}
		
		for (int i = 0;i<this.nrFaces;i++){
			for (int j = 0;j<this.nrFaces;j++){
				if ( goal1[j] == i){ //(goal1[j] == null && i == 0 ) ||
					stacks1[i]++;
				}
			}
		}
		
		for (int i = 0;i<this.nrFaces;i++){
			for (int j = 0;j<this.nrFaces;j++){
				if ( goal2[j] == i){ //(goal1[j] == null && i == 0 ) ||
					stacks2[i]++;
				}
			}
		}
		
		boolean res = true;
		
		for (int i = 0; i<this.nrFaces;i++){
			if (stacks1[i] != stacks2[i]){
				res = false;
			}
		}
		
		return res;
	}
	
	private boolean hasStack(Integer[] goal, Integer[] stack){
		Integer[] stacks1 = new Integer[this.nrFaces];
		for (int i =0;i<this.nrFaces;i++){
			stacks1[i] = 0;
		}

		for (int i = 0;i<this.nrFaces;i++){
			for (int j = 0;j<this.nrFaces;j++){
				if ( goal[j] == i){ //(goal1[j] == null && i == 0 ) ||
					stacks1[i]++;
				}
			}
		}

		boolean res = true;

		for (int i = 0; i<this.nrFaces;i++){
			if (stacks1[i] != stack[i]){
				res = false;
			}
		}

		return res;

	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------

	private List<WishTree> childrenToIntersect(int binRepres, WishTree tree) {
		List<WishTree> res = new Vector<WishTree>();
		
		int k = 0;
		while (Math.pow(2,k)<= binRepres*2){ //TODO check border cases
			if ( getBit(binRepres, k) == 1 ){
				res.add(tree.getChildren().get(k));
			}
			k++;
		}
		
		return res;
	}
	
//
//	private WishTree intersectBranches(List<WishTree> branchesToIntersect) {
//		WishTree intersection = new WishTree(true, false);
//		
//		branchesToIntersect = NormaliseChildrenForIntersect(branchesToIntersect);
//		
//		if ( !(branchesToIntersect.size()==0) ){ // if no element go direct to return with an empty AND tree
//			if (branchesToIntersect.size()==1){
//				return branchesToIntersect.get(0);
//			}else { // at least 2 elements in branchesToIntersect
//				
//				intersection = branchesToIntersect.get(0);
//				branchesToIntersect.remove(intersection);
//				
//				while (branchesToIntersect.size()>0){
//					intersection = intersectTwoBranches(branchesToIntersect.get(0), intersection );
//					if (intersection.getChildren().size() > this.nrDice){
//						return null;
//					}
//					branchesToIntersect.remove(0);
//				}
//				
//			}
//		}
//		
//		return intersection;
//	}
//
//	private WishTree intersectTwoBranches(WishTree branch1, WishTree branch2){
//		WishTree intersection = new WishTree(true, false);
//		WishTree clone2 = branch2.Clone(branch2);
//		
//		for (WishTree child : branch1.getChildren()){
//			intersection.addChild(child);
//			clone2.removeOneOccurenceOf(child.getNode().getNr());
//		}
//		if (clone2.getChildren().size() > 0){
//			intersection.addChildren(clone2.getChildren());
//		}
//		
//		return intersection;
//		
//		
//	}
//
//	private List<WishTree> NormaliseChildrenForIntersect(List<WishTree> tempChildren) {
//		List<WishTree> normalisedChildren = new Vector<WishTree>();
//		
//		for (WishTree child : tempChildren){
//			if (child.getNode().isNR()){
//				WishTree normChild = new WishTree(true, false);
//				normChild.addChild(child);
//				normalisedChildren.add(normChild);
//			} else if (child.getNode().isAND()){
//				normalisedChildren.add(child);
//			} else {
//				System.out.println("you should not see that, NormaliseChildrenForIntersect called with wrong arguments ");
//			}
//		}
//		
//		return normalisedChildren;
//	}


	
	//------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------
	
	
	public Integer[] terminalBranchToIntArray(WishTree terminalBranch){
		Integer[] goal = new Integer[nrFaces];
		for (int i = 0; i< nrFaces; i++){
			goal[i]= occurencesOf(i+1, terminalBranch);
		}
		
		return goal;
	}
	
	private int occurencesOf(int i, WishTree lastBranch) {
		int res = 0;
		for (WishTree child : lastBranch.getChildren()){
			if (child.getNode().getNr()==i){
				res ++;
			}
		}
		return res;
	}

	public double divFact(int n, int k){ // returns n!/k! only call with k<n
		int res = 1;
		while (n>k){
			res *= n;
			n--;
		}
		return res;
	}
	
	private long factorielle(int i) {
		
		if (i<= this.nrDice){
			return this.factoriels[i];
		}else{
			System.out.println("computing " + i + "!, but nrDice is " + this.nrDice);
			long res = 1;
			
			while (i>1){
				res *= i;
				i--;
			}
			
			return res;
		}
		
		
	}

	long getBit(long n, int k) { // return the kth bit of the number n
		if (k > Integer.toBinaryString((int)n).length()){
			System.out.println("ERROR the number " + n + "has less than " + k + "bits");
		}
	    return (n >> k) & 1;
	}

	public String goalToString(Integer[] goal){
		String res = "[";
		for (int i = 0;i<this.nrFaces-1;i++){
			res += "" + goal[i] + ",";
		}
		res += "" + goal[this.nrFaces-1] + "]";
		return res;
	}


}

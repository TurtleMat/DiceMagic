import java.util.Vector;

public class Strategy {

	String name;
//	String compareOrder;
	Vector<String> compareOrder;


	public Strategy(String name) {
		super();
		this.name = name;
		this.compareOrder = new Vector<String>();
		if (name.equalsIgnoreCase("greedy")) {
			compareOrder.add("gain");
		} 
		if (name.equalsIgnoreCase("ValuePerDice")) {
			compareOrder.add("valuePerDice");
			compareOrder.add("gain");
		}
	}
	
	public Strategy(String name, Vector<String> compareOrder) {
		super();
		this.name = name;
		this.compareOrder = compareOrder;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	public static WishTree worstOfTwoBranchesOneComparator(WishTree branch1, WishTree branch2, String comparator) {
		float value1=0;
		float value2=0;
		
		switch (comparator) {
		case "gain":
			value1 = branch1.getGain();
			value2 = branch2.getGain();
			break;
		case "valuePerDice":
			value1 = branch1.getGain()/branch1.getChildren().size();
			value2 = branch2.getGain()/branch2.getChildren().size();
			break;
		case "numberUsedDice" :
			value1 = branch1.getChildren().size();
			value2 = branch2.getChildren().size();
			break;
		}
		
		if (value1<value2) {
			return branch1;
		}else if (value2<value1){
			return branch2;
		}else {
			if (comparator.equalsIgnoreCase("gain")) {
				return branch1;
			}else {
				return null;
			}
		}
	}
	
	public WishTree worstOfTwoBranches_new(WishTree branch1, WishTree branch2) {
		WishTree branchToKeep=null;
		int i=0;
		while (branchToKeep==null){
			branchToKeep = worstOfTwoBranchesOneComparator(branch1, branch2, this.compareOrder.get(i));
			i++;
		}
		return branchToKeep;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public void sortTerminalBranchesAscending(WishTree tree) {
		Vector<WishTree> sortedChildren = new Vector<WishTree>();
		while (tree.getChildren()!= null && tree.getChildren().size()!=0) {
			WishTree currChildToAdd = tree.getChildren().get(0);
			int i=1;
			while (i<tree.getChildren().size()) {
				WishTree currToCompare = tree.getChildren().get(i);
				currChildToAdd = this.worstOfTwoBranches_new(currChildToAdd, currToCompare);
				i++;
			}
			sortedChildren.add(currChildToAdd);
			tree.getChildren().remove(currChildToAdd);
		}
		tree.resetChildren();
		tree.getChildren().addAll(sortedChildren);
//		return tree;
	}
	
	public void sortTerminalBranchesDescending(WishTree tree) {
		Vector<WishTree> sortedChildren = new Vector<WishTree>();
		while (tree.getChildren()!= null && tree.getChildren().size()!=0) {
			WishTree currChildToAdd = tree.getChildren().get(0);
			int i=1;
			while (i<tree.getChildren().size()) {
				WishTree currToCompare = tree.getChildren().get(i);
				currChildToAdd = this.worstOfTwoBranches_new(currChildToAdd, currToCompare);
				i++;
			}
//			sortedChildren.add(currChildToAdd);
			sortedChildren.add(0, currChildToAdd);
			tree.getChildren().remove(currChildToAdd);
		}
		tree.resetChildren();
		tree.getChildren().addAll(sortedChildren);
//		return tree;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public WishTree giveWorstBranch(Vector<WishTree> branchesToCompare) {
		WishTree branchToKeep= branchesToCompare.get(0);
		for (WishTree currBranch : branchesToCompare) {
			branchToKeep = worstOfTwoBranches_new(currBranch, branchToKeep);
		}
		return branchToKeep;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

//
//public WishTree worstOfTwoBranches(WishTree branch1, WishTree branch2) {
//	if (this.getName().equalsIgnoreCase("greedy")) {
//		return worstOfTwoBranchesGreedy(branch1, branch2);
//	}else if (this.getName().equalsIgnoreCase("valuePerDice")) {
//		return worstOfTwoBranchesValuePerDice(branch1, branch2);
//	}else {
//		return null;
//	}
//}
//
//public static WishTree worstOfTwoBranchesGreedy(WishTree branch1, WishTree branch2) {
//	int value1 = branch1.getGain();
//	int value2 = branch2.getGain();
//	if (value1<=value2) {
//		return branch1;
//	}else {
//		return branch2;
//	}
//}
//
//public static WishTree worstOfTwoBranchesValuePerDice(WishTree branch1, WishTree branch2) {
//	float value1 = branch1.getGain()/branch1.getChildren().size();
//	float value2 = branch2.getGain()/branch2.getChildren().size();
//	if (value1<=value2) {
//		return branch1;
//	}else {
//		return branch2;
//	}
//}


//	public void sortTerminalBranchesAscending(WishTree tree) {
//	if (this.getName().equalsIgnoreCase("greedy")) {
//		sortTerminalBranchesGreedyAscending(tree);
//	}else if (this.getName().equalsIgnoreCase("valuePerDice")) {
//		sortTerminalBranchesValuePerDiceAscending(tree);
//	}		
//}
//
//public static void sortTerminalBranchesGreedyAscending(WishTree tree) {
//	Vector<WishTree> sortedChildren = new Vector<WishTree>();
//	while (tree.getChildren()!= null && tree.getChildren().size()!=0) {
//		WishTree currChildToAdd = tree.getChildren().get(0);
//		int i=1;
//		while (i<tree.getChildren().size()) {
//			WishTree currToCompare = tree.getChildren().get(i);
//			currChildToAdd = worstOfTwoBranchesGreedy(currChildToAdd, currToCompare);
//			i++;
//		}
//		sortedChildren.add(currChildToAdd);
//		tree.getChildren().remove(currChildToAdd);
//	}
//	tree.resetChildren();
//	tree.getChildren().addAll(sortedChildren);
////	return tree;
//}
//
//public static void sortTerminalBranchesValuePerDiceAscending(WishTree tree) {
//	Vector<WishTree> sortedChildren = new Vector<WishTree>();
//	while (tree.getChildren()!= null && tree.getChildren().size()!=0) {
//		WishTree currChildToAdd = tree.getChildren().get(0);
//		int i=1;
//		while (i<tree.getChildren().size()) {
//			WishTree currToCompare = tree.getChildren().get(i);
//			currChildToAdd = worstOfTwoBranchesValuePerDice(currChildToAdd, currToCompare);
//			i++;
//		}
//		sortedChildren.add(currChildToAdd);
//		tree.getChildren().remove(currChildToAdd);
//	}
//	tree.resetChildren();
//	tree.getChildren().addAll(sortedChildren);
////	return tree;
//}


//public WishTree giveWorstBranch(Vector<WishTree> branchesToCompare) {
//if (this.getName().equalsIgnoreCase("greedy")) {
//	return giveWorstBranchGreedy(branchesToCompare);
//}else if (this.getName().equalsIgnoreCase("valuePerDice")) {
//	return giveWorstBranchValuePerDice(branchesToCompare);
//}else {
//	return null;
//}
//
//}
//
//public WishTree giveWorstBranchGreedy(Vector<WishTree> branchesToCompare) {
//
//	WishTree branchToKeep= branchesToCompare.get(0);
//	int currGain=branchToKeep.getGain();
//	for (WishTree currBranch : branchesToCompare) {
//		if (currBranch.getGain()<currGain) {
//			branchToKeep = currBranch;
//			currGain = currBranch.getGain();
//		}
//	}
//	return branchToKeep;
//}
//
//public WishTree giveWorstBranchValuePerDice(Vector<WishTree> branchesToCompare) {
//WishTree branchToKeep= branchesToCompare.get(0);
//float valueToKeep=branchToKeep.getGain()/branchToKeep.getChildren().size();
//for (WishTree currBranch : branchesToCompare) {
//	float branchValue = currBranch.getGain()/currBranch.getChildren().size();
//	if (branchValue<valueToKeep) {
//		branchToKeep = currBranch;
//		valueToKeep = branchValue;
//	}
//}
//
//return branchToKeep;
//}

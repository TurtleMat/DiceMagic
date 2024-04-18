import java.util.List;
import java.util.Vector;

public class WishTree {

	private static String[] availableSimplePresets = new String[] { "pair", "street", "tenthousand", "fullhouse" }; // ,"partialstreet"

	private static String[] availableAdvancedPresets = new String[] { "ntupel", "allthesame" };

	private boolean PRESET;
	private boolean AND;
	private boolean OR;
	private boolean NR;
	private List<WishTree> children;

	private String preset;
	private int number;
	private int gain = 0;
	private boolean fixable = true;

	public WishTree() {
		this.children = new Vector<WishTree>();
	}

	public WishTree(String nodeString) {
		this.children = new Vector<WishTree>();
		if (nodeString.equalsIgnoreCase("AND")) {
			this.setValue(true, false, false, -1);
		} else if (nodeString.equalsIgnoreCase("OR")) {
			this.setValue(false, true, false, -1);
		} else {
			int i;
			try {
				i = Integer.parseInt(nodeString);
				this.setValue(false, false, true, i);
			} catch (NumberFormatException e) {

				this.setValue(nodeString);
			}
		}
	}

	public WishTree(int i) {
		// this.node = new Wish(false, false, true, i);
		this.setValue(false, false, true, i);
		this.children = null;
	}

	private void setValue(boolean AND, boolean OR, boolean NR, int i) {
		this.AND = AND;
		this.OR = OR;
		this.NR = NR;
		this.number = i;
	}

	private void setValue(String preset) {
		this.AND = false;
		this.OR = false;
		this.NR = false;
		this.number = -1;
		this.PRESET = true;
		this.preset = preset;
	}

	public WishTree(boolean AND, boolean OR) {
		// this.node = new Wish(AND, OR, false, -1);
		this.setValue(AND, OR, false, -1);
		this.children = new Vector<WishTree>();
	}

	public WishTree(boolean AND, boolean OR, Vector<WishTree> children) {
		// this.node = new Wish(AND, OR, false, -1);
		this.setValue(AND, OR, false, -1);
		this.children = children;
	}

	public WishTree Clone() { // Recursively clones tree
		WishTree newTree = new WishTree(this);
		if (this.children != null) {
			for (WishTree child : this.children) {
				newTree.addChild(child.Clone());
			}
		}
		return newTree;
	}

	public WishTree(WishTree tree) {
		// this.node = new Wish(tree.node);
		this.AND = tree.AND;
		this.OR = tree.OR;
		this.NR = tree.NR;
		this.number = tree.number;

		this.children = new Vector<WishTree>();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static WishTree importAndPrepareTree(String encodingString, int nrFaces, int nrDice, boolean verbose,
			Rules ruleset) {

		Main.say("tree import...", true, verbose, false);

		String firstTestResult = firstTest(encodingString);
		if (firstTestResult == "") {
			return null;
		}
		WishTree tree = WishTree.validStringToTree(firstTestResult, nrFaces, nrDice);
		if (tree == null) {
			return null;
		}
		Main.say(tree.toString(), false, verbose, false);
		Main.say("tree imported.", true, verbose, false);
		Main.say("", true, verbose, false);

		prepareTree(tree, verbose, nrDice, nrFaces, ruleset);

		return tree;

	}

	private static String firstTest(String encodingString) {
		int i = 0;
		int nrParenthesis = 0;
		String res = encodingString;
		if (!(res.charAt(i) == '(')) {
			System.out.println("this should start with a parehthesis. please use following format : ");
			Main.say("(OPERATOR;TREE;...;TREE)");
			Main.say("adding a parenthesis and trying to proceed.");
			res = "(" + res;
		}

		boolean containsSemicolon = false;

		for (int j = 0; j < res.length(); i++) {
			char currChar = res.charAt(j);
			if (currChar == ';') {
				containsSemicolon = true;
			}
			if (currChar == '(') {
				nrParenthesis++;
				if (res.charAt(j + 1) == '(') {
					res = res.substring(0, j + 1) + res.substring(j + 2);
					j--;
					nrParenthesis--;
					System.out.println(
							"you cann't have several opening parenthesis in a raw. please use following format : ");
					Main.say("(OPERATOR;TREE;...;TREE)");
					System.out.println("removing a parenthesis and trying to proceed.");
				}
			}
			if (currChar == ')') {
				nrParenthesis--;
			}
			j++;
		}

		if (!containsSemicolon) {
			System.out.println("this does not contain any semicolon (;). please use following format : ");
			Main.say("(OPERATOR;TREE;...;TREE)");
			Main.say("cann't fix that (yet?). Aborting.");
			return "";
		}

		if (nrParenthesis > 0) {
			System.out.println("not enough closing parenthesis. adding this amount at the end");
			while (nrParenthesis > 0) {
				res += ")";
				nrParenthesis--;
			}
		}
		// this segment should be redundant
		i = res.length() - 1;
		if (!(res.charAt(i) == ')')) {
			System.out.println("this should end with a parehthesis. please use following format : ");
			Main.say("(OPERATOR;TREE;...;TREE)");
			Main.say("adding a parenthesis and trying to proceed.");
			res = res + ")";
		}

		return res;
	}

	public static WishTree presetStringToTree(String preset, int nrFaces, int nrDice) {
		preset.toLowerCase();

		switch (preset) {
		case "pair":
			return pair(nrFaces);
		case "tenthousand":
			return zehnTausend();
		case "street": // TODO remove partialstreet if safe.
			return partialStreet(nrFaces, Math.min(nrFaces, nrDice));
		// case "partialstreet":
		// return partialStreet(nrFaces, nrDice);
		case "fullhouse":
			return fullHouse(nrFaces);
		}

		String tupelcheck = preset.substring(0, 6);
		if (tupelcheck.equalsIgnoreCase("ntupel")) {
			int tupelSize;
			try {
				tupelSize = Integer.parseInt(preset.substring(6));
				return nTupel(nrFaces, tupelSize);
			} catch (final NumberFormatException e) {
				System.out
						.println("imput error : ntupel string should be for instance ntupel3, this yould be a triple ");
				return null;
			}

		}

		String allSameCheck = preset.substring(0, 10);
		if (allSameCheck.equalsIgnoreCase("allthesame")) {
			int whichFace;
			try {
				whichFace = Integer.parseInt(preset.substring(10));
				return allTheSame(nrDice, whichFace);
			} catch (final NumberFormatException e) {
				System.out.println(
						"imput error : all the same should be written 'allthesame5' if you want all the dice to show 5 ");
			}
		}

		return null;
	}

	private static WishTree handleImputLeaf(String encodingString, int nrFaces, int nrDice) {
		int res;
		int gainStartIndex = encodingString.indexOf(":");
		int gain = 0;

		if (gainStartIndex != -1) {
			try {
				gain = Integer.parseInt(encodingString.substring(gainStartIndex + 1, encodingString.length()));
				encodingString = encodingString.substring(0, gainStartIndex);
			} catch (final NumberFormatException e) {
				System.out.println("Gain was not an integer or unexpected double points. Discarding leaf/preset.");
				return null;
			}

		}

		WishTree resTree = null;
		try {
			res = Integer.parseInt(encodingString);
			resTree = new WishTree(res);

			// return resTree; // TODO the child still have no parent
		} catch (final NumberFormatException e) {
			resTree = presetStringToTree(encodingString, nrFaces, nrDice);
			if (resTree != null) {
				// return resTree;
			} else {
				Main.say("this leaf (" + encodingString + ") is not an integer or a valid preset. removed.");
				// return null;
			}
		}

		if (gain != 0) {
			resTree.setGain(gain);
		}
		return resTree;

	}

	public static WishTree validStringToTree(String encodingString, int nrFaces, int nrDice) {

		int i = 0;
		if (!(encodingString.charAt(i) == '(')) {
			// if it doesn't start with a parenthesis, it must be a number or a
			// preset

			WishTree resTree = handleImputLeaf(encodingString, nrFaces, nrDice);
			return resTree;

		} else {
			String[] childStrings = new String[encodingString.length()];
			// length overkill but wathever for now
			String operator;
			i++;

			int j = encodingString.indexOf(";", i);
			if (j == -1) {
				Main.say("this (sub-)Tree :" + encodingString + " is invalid. Removing it.");
				Main.say("possible mistake : leaves don't use parenthesis, or you forgot a ';'");
				return null;
			}

			int branchGain = 0;
			int k = encodingString.indexOf(":");
			if (k != -1 && k < j) {
				operator = encodingString.substring(i, k);
				try {
					branchGain = Integer.parseInt(encodingString.substring(k + 1, j));
				} catch (final NumberFormatException e) {
					Main.say("syntax says there is a gain but is invalid");
				}
			} else {
				operator = encodingString.substring(i, j);
				// at this point, operator should be AND or OR. substring should be exclusive
				// for the max bound
			}

			i = j + 1;
			int childNr = 0;
			int parenthesis = 1;

			while (parenthesis > 0) {
				char c = encodingString.charAt(i);
				String s = String.valueOf(c);
				if (c == '(') {
					parenthesis++;
					if (parenthesis == 2) {
						childStrings[childNr] = s;
					} else {
						childStrings[childNr] += s;
					}
				} else if (c == ')') {
					parenthesis--;
					if (!(parenthesis == 0)) {
						childStrings[childNr] += s;
					}

				} else if (c == ';' && parenthesis == 1) {
					childNr++;
				} else {
					if (childStrings[childNr] == null) {
						childStrings[childNr] = s;
					} else {
						childStrings[childNr] += s;
					}

				}

				i++;
			} // at the end of this while, every child should be stored as a
				// valid string in the childStrinds vector

			WishTree newTree = new WishTree();
			if (operator.equalsIgnoreCase("AND")) {
				newTree.setValue(true, false, false, -1);
			} else if (operator.equalsIgnoreCase("OR")) {
				newTree.setValue(false, true, false, -1);
			} else {
				Main.say("invalid operator (" + operator + "). removing it and its children");
				return null;
			}
			if (childStrings != null) {
				for (String childString : childStrings) {
					if (childString != null) {
						newTree.addChild(validStringToTree(childString, nrFaces, nrDice));
					}
				}
			}
			if (branchGain != 0) {
				newTree.setGain(branchGain);
			}

			return newTree;
		}
	}

	public static WishTree prepareTree(WishTree tree, boolean verbose, int nrDice, int nrFaces, Rules ruleset) {
		// should be void or not have tree as argument?

		if (verbose) {
			Main.say("imput tree : ");
			Main.say(tree.toString());
		}

		boolean fixable = tree.checkAndRepairTree();
		if (verbose) {
			Main.say("checking and reparing tree if possible/necessary...");
			if (fixable) {
				Main.say(tree.toString());
				Main.say("tree is valid and repaired");
			} else {
				Main.say("Tree is invalid. setting to null");
				return null;
			}
		}

		Main.say("removing operator without children...", true, verbose, false);
		tree.removeOperatorLeaves();
		Main.say(tree.toString(), false, verbose, false);
		Main.say("removed.", true, verbose, false);
		Main.say("", true, verbose, false);

		Main.say("simplifying tree...", true, verbose, false);
		tree.collapseIdenticalFollowingOperators();
		tree.tryRemovingBigAnd(nrDice);
		tree.removeBigValues(nrFaces);
		Main.say(tree.toString(), false, verbose, false);
		Main.say("tree simplified", true, verbose, false);
		Main.say("", true, verbose, false);

		Main.say("developping tree...", true, verbose, false);
		tree.developpTree(nrDice);
		Main.say(tree.toString(), false, verbose, false);
		Main.say("tree developped. Nr Branches :" + tree.getChildren().size(), true, verbose, false);
		Main.say("", true, verbose, false);

		Main.say("normalising tree...", true, verbose, false);
		tree.normaliseTerminalBranches();
		Main.say(tree.toString(), false, verbose, false);
		Main.say("tree normalised. Nr Branches :" + tree.getChildren().size(), true, verbose, false);
		Main.say("", true, verbose, false);

		if (ruleset != null && ruleset.isCanKeepMultipleGoalsPerThrow()) {
			Main.say("adding cases for multiple goals and removing redundancies...", true, verbose, false);

//			tree.sortTerminalBranchesDescendingGain();// wins 3to4 sec from 24 for fulltenthousand
			
			
			Vector<String> tmpCmpOrder = new Vector<String>();
			tmpCmpOrder.add("gain");
			tmpCmpOrder.add("numberUsedDice");
			Strategy tmpStratForMultipleCases = new Strategy("tmp", tmpCmpOrder);
			tmpStratForMultipleCases.sortTerminalBranchesDescending(tree);
			
			tree.addBranchesForMultipleGoalAndRemoveRedundancies(tree, nrDice, nrFaces);
			
			Main.say(tree.toString(), false, verbose, false);
			Main.say("cases added. redundancies removed. Nr Branches :" + tree.getChildren().size(), true, verbose,
					false);
			Main.say("", true, verbose, false);
		} else {
			Main.say("removing redundency...", true, verbose, false);
//			tree.removeUselessCases(ruleset, nrFaces);
			tree.removeRedundancy_New(nrFaces);
			Main.say(tree.toString(), false, verbose, false);
			Main.say("redundencies removed. Nr Branches :" + tree.getChildren().size(), true, verbose, false);
			Main.say("", true, verbose, false);
		}

		return tree;

	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------



	public boolean checkAndRepairTree() {
		// this.checkAndRepairNode(null);
		this.checkAndRepairNode(null);
		return this.isFixable();
	}

	public void checkAndRepairNode(WishTree parent) { // TODO find better name
		if (fixable) {
			List<WishTree> children = this.getChildren();
			Vector<WishTree> toRemove = new Vector<WishTree>();
			Vector<WishTree> toAdd = new Vector<WishTree>();
			if (children != null) {
				for (WishTree child : children) {
					List<WishTree> grandChildren = child.getChildren();
					if (grandChildren != null && ((Integer) grandChildren.size()).equals(1)) {
						// remove operators with only one child (child is pushed up)
						int newGain = Math.max(child.getGain(), grandChildren.get(0).getGain());
//						grandChildren.get(0).setGain(newGain);
						grandChildren.get(0).gain = newGain;
						toRemove.add(child);
						toAdd.addAll(grandChildren);
					}
					if ((grandChildren == null || ((Integer) grandChildren.size()).equals(0)) && !child.isNR()) {
						// remove operator with no children
						toRemove.add(child);
					}
					if (grandChildren != null && child.isNR()) {
						// remove children of leaves
						child.setChildren(null);
					}

				}
				if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
					children.addAll(toAdd);
					children.removeAll(toRemove);
					this.checkAndRepairNode(parent);
				} else {
					for (WishTree child : children) {
						child.checkAndRepairNode(this);
					}
				}
			}

			//

		} else {
			if (!this.isFixable() && parent != null) {
				parent.setFixable(false);
			}
		}

	}

	private void setChildren(List<WishTree> children) {
		this.children = children;

	}

	private void setFixable(boolean b) {
		this.fixable = b;

	}

	private boolean isFixable() {
		return this.fixable;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------------------------------------

	private void removeOperatorLeaves() {
		Vector<WishTree> toremove = new Vector<>();
		for (WishTree operatorChild : this.getChildren(true, true, false)) {

			operatorChild.removeOperatorLeaves();

			if (operatorChild.getChildren() == null || operatorChild.getChildren().size() == 0) {
				toremove.add(operatorChild);
			}
		}
		this.getChildren().removeAll(toremove);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public void tryRemovingBigAnd(int nrDice) {
		// should be safe to call after collapsing. does not remove all cases.
		// TODO can be done in simplifynode

		Vector<WishTree> operatorChildren = this.getChildren(true, true, false);
		Vector<WishTree> toRemove = new Vector<>();

		for (WishTree operatorChild : operatorChildren) {
			Vector<WishTree> grandChildren = operatorChild.getChildren(true, true, true); // the OR have at least one
																							// leaf they count as
																							// 1
			if (grandChildren != null && grandChildren.size() != 0) {
				if (operatorChild.isAND()) {
					if (grandChildren.size() > nrDice) {
						toRemove.add(operatorChild);
					}
				}
			}
		}
		this.getChildren().removeAll(toRemove);
		for (WishTree operatorChild : this.getChildren(true, true, false)) {
			operatorChild.tryRemovingBigAnd(nrDice);
		}

	}

	public void removeBigValues(int nrFaces) {
		Vector<WishTree> toRemove = new Vector<WishTree>();

		if (this.isOR()) {
			for (WishTree child : this.getChildren(false, false, true)) {
				if (child.getNr() > nrFaces) {
					toRemove.add(child);
				}
			}
			for (WishTree andChild : this.getChildren(true, false, false)) {// And children
				for (WishTree grandChild : andChild.getChildren(false, false, true)) {
					if (grandChild.getNr() > nrFaces) {
						toRemove.add(andChild);
					}
				}
			}
		}

		this.getChildren().removeAll(toRemove);
		toRemove.removeAllElements();

		for (WishTree child : this.getChildren(true, true, false)) {
			child.removeBigValues(nrFaces);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------//
	// ------------------------------------------------------------------------------------------------------------------------------------

	public void collapseIdenticalFollowingOperators() {
		// supress unnecessary parenthesis ex : (A and B) and C becomes A and B
		// and C
		if (this.children != null) {
			for (WishTree child : this.children) {
				child.collapseIdenticalFollowingOperators();
			}
			this.collapseNode();
		}

	} // TODO could ignore leaves

	public void collapseNode() {
		Vector<WishTree> tempWrite;
		tempWrite = new Vector<WishTree>();

		if (this.isAND()) {
			while (scanChildren(this, true, false, false)) {
				for (WishTree child : this.children) {
					if (child.isAND()) {
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}
				this.children = tempWrite;
			}
		}

		if (this.isOR()) {
			while (scanChildren(this, false, true, false)) {
				for (WishTree child : this.children) {
					if (child.isOR()) {
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}
				this.children = tempWrite;
			}
		}
		// at this point an AND can not have an AND as children and a OR can not
		// have a OR
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------------------------------------//
	// ------------------------------------------------------------------------------------------------------------------------------------

	public void developpTree(int nrDice) {
		// developps the remaining parenthesis Ex : (A or B) and C becomes (A
		// and C) OR (B and C)
		if (this.children != null) {
			// TODO check if all trees have an OR root after that

			this.developpNode(nrDice);

			for (WishTree child : this.children) {
				child.developpTree(nrDice);
			}
		}
		this.collapseNode();
	}

	public void developpNode(int nrDice) {

		if (this.isAND()) {
			int gain = this.getGain();
			Vector<WishTree> orChildren = this.getChildren(false, true, false);
			if (!orChildren.isEmpty()) { // a AND node that has at least one OR
											// child

				WishTree currOrChild = orChildren.firstElement();
				Vector<WishTree> newChildren = new Vector<WishTree>();

				for (WishTree grandChild : currOrChild.children) {

					WishTree newAndChild = new WishTree();
					newAndChild.setValue(true, false, false, -1);

					if (grandChild.isAND()) { // this if should ensure that the
												// tree remains simplified
						newAndChild.addChildren(grandChild.getChildren());
					} else {
						newAndChild.addChild(grandChild);
					}

					this.children.remove(currOrChild);
					for (WishTree currSiblings : this.children) {
						newAndChild.addChild(currSiblings);
						// newChild.parent = this;
					}

					// since the tree is simplified, discarding the big AND
					// should not be a problem
					// there are no two following AND, revoving a big AND from a
					// OR should not break anything.
					// this most likely still doesn't remove all of them. it
					// might though.
					if (newAndChild.getChildren().size() <= nrDice) {
						newChildren.add(newAndChild);
					}

				}

				this.setToOr();
				this.children = newChildren;
				if (gain != 0) {
					this.setGain(gain);
				}
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public void normaliseTerminalBranches() {
		// transforms nr nodes in and nodes with the number. Ex : 1 -> (AND;1)

		if (this.isAND()) { // should only happen when the whole tree
							// is (AND;leaves)
			WishTree copy = this.Clone();
			this.resetChildren();
			// this.node = new Wish(false, true, false, -1);
			this.setValue(false, true, false, -1);
			this.addChild(copy);
		}
		if (this.isOR()) {
			Vector<WishTree> normalisedChildren = new Vector<WishTree>();

			for (WishTree child : this.getChildren()) {
				if (child.isNR()) {
					WishTree newChild = new WishTree(true, false);
					newChild.addChild(child);
					normalisedChildren.add(newChild);
					newChild.setGain(child.getGain());
				} else if (child.isAND()) {
					if (child.getGain() == 0) {
						int newGain = 0;
						for (WishTree grandChild : child.getChildren()) {
							newGain+= grandChild.getGain();
						}
						child.gain =newGain;
					}
					normalisedChildren.add(child);
				} else {
					System.out
							.println("you should not see that, normaliseTerminalBranche called with wrong arguments ");
				}
			}

			this.resetChildren();
			this.addChildren(normalisedChildren);
			for (WishTree child : this.getChildren()) {
				child.setGain(child.getGain());
			}

		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	private void sortTerminalBranchesDescendingGain() {
		Vector<WishTree> newOrder = new Vector<WishTree>();
//		int i = 0;
		while (this.getChildren() != null && this.getChildren().size() != 0) {
			WishTree nextToAdd = this.getChildren().get(0);
			int currGain = nextToAdd.getGain();
			for (WishTree child : this.getChildren()) {
				if (child.getGain() > currGain || ((child.getGain() == currGain
						&& child.getChildren().size() < nextToAdd.getChildren().size()))) {
					// better gain or same gain and less dice used
					nextToAdd = child;
					currGain = nextToAdd.getGain();
				}
			}
			this.getChildren().remove(nextToAdd);
			newOrder.add(nextToAdd);
		}
		this.resetChildren();
		this.addChildren(newOrder);
		Main.say("Sorted Tree : ");
		Main.say(this.toString());
	}

	private void addBranchesForMultipleGoalAndRemoveRedundancies(WishTree tree, int nrDice, int nrFaces) {
		Vector<WishTree> newChildrenWithDescendency = new Vector<WishTree>();
		Vector<WishTree> toRemoveVect = new Vector<WishTree>();
//		Vector<WishTree> multGoalsToAdd = new Vector<WishTree>();

//		boolean keepChildA = true;
		while (tree.getChildren() != null && tree.getChildren().size() != 0) {
			boolean keepChildA = true;
			
//			Main.say("new Children :" + newChildrenWithDescendency);
//			Main.say("tree : " + tree.toString());
			
			WishTree childA = tree.getChildren().get(0); // sould gt the first element, don't forget to remove
			for (WishTree newChild : newChildrenWithDescendency) {
				WishTree toRemove = branchContainsWithGainReturnsToRemove(childA, newChild, nrFaces);

				if (toRemove == childA) {
					keepChildA = false;//TODO if i don't keep child A, no need to sum it with remaining branches
				}
				if (toRemove == newChild) {
					toRemoveVect.add(newChild);
				}
				if (toRemove == null) {
					WishTree newMultipleGoal = terminalBranchesSum(childA, newChild, nrDice);
					tree.addChild(newMultipleGoal);
					// WARNING here under the children have an other meaning. any AND child of a
					// terminal branch is a sum containing them.
					childA.addChild(newMultipleGoal);
					newChild.addChild(newMultipleGoal);
				}
			}
			if (!toRemoveVect.isEmpty()) {

				for (WishTree toRemove : toRemoveVect) {
					tree.getChildren().removeAll(toRemove.getChildren(true, false, false)); // these are the sums
																							// rendered useless
					toRemove.setAllSumDescendentsToNull();// TODO see if setting to null is the same than deleting
				}

				newChildrenWithDescendency.removeAll(toRemoveVect);

			}
			if (keepChildA) {
				newChildrenWithDescendency.add(childA);
				WishTree newMultipleGoal = terminalBranchesSum(childA, childA, nrDice);
								
				tree.addChild(newMultipleGoal);
				// WARNING here under the children have an other meaning. any AND child of a
				// terminal branch is a sum containing them.
				childA.addChild(newMultipleGoal);

			}

			tree.getChildren().remove(childA);
		}

		this.resetChildren();
		Vector<WishTree> newChildren = new Vector<WishTree>();
		for (WishTree newChild : newChildrenWithDescendency) {
			newChild.setChildren(newChild.getChildren(false, false, true));
			newChildren.add(newChild);
		}
		this.addChildren(newChildren);
	}

	private void setAllSumDescendentsToNull() {
		for (WishTree sumChild : this.getChildren(true, false, false)) {
			sumChild.setAllSumDescendentsToNull();
			sumChild = null;
		}

	}
	
	public void removeRedundancy_New(int nrFaces) {
		Vector<WishTree> newChildren = new Vector<WishTree>();
		Vector<WishTree> toRemoveVect = new Vector<WishTree>();

		for (WishTree childA : this.getChildren()) {
			boolean keepChildA = true;

			for (WishTree newChild : newChildren) {
				WishTree toRemove = branchContainsWithGainReturnsToRemove(childA, newChild, nrFaces); // if A in B
																										// returns 1; if
																										// Bin A returns
																										// 2, if A=B
																										// returns 0,
																										// otherwise -1
				// TODO breaks if a goal contains a big number (>nrFace)
				if (toRemove == childA) {
					keepChildA = false;
				}
				if (toRemove == newChild) {
					toRemoveVect.add(toRemove);
				}
			}
			if (!toRemoveVect.isEmpty()) {
				newChildren.removeAll(toRemoveVect);
			}
			if (keepChildA) {
				newChildren.add(childA);
			}
		}

		this.resetChildren();
		this.addChildren(newChildren);
	}

	public WishTree branchContainsWithGainReturnsToRemove(WishTree branchA, WishTree branchB, int nrFaces) {

		Integer[] goalA = CalculationsForSingleThrow.terminalBranchToGoal(branchA, nrFaces);
		int gainA = branchA.getGain();
		Integer[] goalB = CalculationsForSingleThrow.terminalBranchToGoal(branchB, nrFaces);
		int gainB = branchB.getGain();
		Integer[] diff = new Integer[nrFaces];
		int diffGain = gainA - gainB;

		boolean isBiggerBranchA = false;
		boolean isBiggerBranchB = false;

//		diff[0] = goalA[0] - goalB[0]; //if diff >0 then B can not be bigger than A

		int i = 0;
		boolean stillPossible = true;
		while (stillPossible && i < nrFaces) {
			diff[i] = goalA[i] - goalB[i];
			if (diff[i] > 0) {
				isBiggerBranchA = true;
			}
			if (diff[i] < 0) {
				isBiggerBranchB = true;
			}

			stillPossible = !(isBiggerBranchA && isBiggerBranchB); // if diff has both positive and negative values, no
																	// branch is contained in the other
			i++;
		}
		if (stillPossible) {
			if (isBiggerBranchA && diffGain <= 0) { // B in A and gainB>=gainA. should remove A
				return branchA;
			}
			if (isBiggerBranchB && diffGain >= 0) { // A in B and gainB<=gainA. should remove B
				return branchB;
			}
			if (!isBiggerBranchA && !isBiggerBranchB) { // A = B
				if (diffGain < 0) {// B has a better gain. remove A
					return branchA;
				} else {// A has a bigger gain or both branches are identical. remove B
					return branchB;
				}
			}
		}
		return null;

	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public String nodeToString(WishTree tree, int depth) {
		String res = "";
		for (int i = 0; i <= depth; i++) {
			res += "| ";
		}
		res += this.nodeValueToString();
		if (this.getGain() != 0) {
			res += ":" + this.getGain();
		}
		res += "\n";
		return res;
	}

	public String nodeValueToString() {
		if (this.isAND()) {
			return "And";
		}
		if (this.isOR()) {
			return "Or";
		}
		if (this.isNR()) {
			return "" + number;
		}
		if (this.isPRESET()) {
			return "" + preset;
		}
		if (!(this.isAND() || this.isOR() || this.isNR() || this.isPRESET())) {
			return "this node is crap";
		}
		return " you should never see this";

	}

	
	
	public String toString() {
		String res = "";
		res = this.toString(this, 0, res);
		return res;
	}

	public String toString(WishTree tree, int depth, String res) {
		res += tree.nodeToString(tree, depth);
		if (tree.children != null) {
			for (WishTree child : tree.children) {
				if (child == null) {
					return "0";
				} else {
					res = toString(child, depth + 1, res);
				}
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public void addChild(WishTree tree) {
		if (tree != null) {
			this.children.add(tree);
		}
		// tree.parent = this;
	}

	public void addChildren(List<WishTree> children2) {
		this.children.addAll(children2);
	}

	public void resetChildren() {
		this.children = new Vector<WishTree>();

	}

	public boolean removeOneOccurenceOf(int nr) { // returns true if something
													// was removed

		WishTree toRemove = null;
		int k = 0;
		while (toRemove == null && k < this.children.size()) {
			if (this.children.get(k).getNr() == nr) {
				toRemove = this.children.get(k);
			}
			k++;
		}
		if (toRemove != null) {
			this.children.remove(toRemove);
			return true;
		} else {
			return false;
		}

	}

	public WishTree terminalBranchesSum(WishTree tree1, WishTree tree2, int nrDice) {
		WishTree res = new WishTree(true, false);
		res.getChildren().addAll(tree1.getChildren(false, false, true));
		res.getChildren().addAll(tree2.getChildren(false, false, true));
		res.setGain(tree1.getGain() + tree2.getGain());
		if (res.getChildren(false, false, true).size() <= nrDice) {
			return res;
		} else {
			return null;
		}
	}

	public boolean scanChildren(WishTree tree, boolean AND, boolean OR, boolean NR) { // scanns for children that have
																						// one of the TRUE
																						// values in common with
																						// arguments
		for (WishTree child : tree.children) {
			if ((AND && child.isAND()) || (OR && child.isOR()) || (NR && child.isNR())) {
				return true;
			}
		}
		return false;
	}// ------------------------------------------------------------------------------------------------------------------------------------

	public boolean isAND() {
		return AND;
	}

	public boolean isOR() {
		return OR;
	}

	public boolean isNR() {
		return NR;
	}

	private boolean isPRESET() {
		return this.PRESET;
	}

	public void setToOr() {
		this.AND = false;
		this.OR = true;
		this.NR = false;
		this.number = -1;

	}

	public int getNr() {
		return this.number;
	}

	public int getGain() {
		return gain;
	}

	public void setGain(int gain) {
		if (this.isAND()) {
			if (this.getGain() != 0) {
				System.out.println("Warning, overriding a non 0 gain for branch :");
				Main.say(this.toString());
				Main.say("old gain : " + this.getGain() + ". New gain : " + gain + ".");
			}
			this.gain = gain;
			for (WishTree child : this.getChildren()) {
				child.setGain(0);
			}
		}
		if (this.isNR()) {
			if (this.getGain() != 0) {
				System.out.println("Warning, overriding a non 0 gain for branch :");
				Main.say(this.toString());
				Main.say("old gain : " + this.getGain() + ". New gain : " + gain + ".");
			}
			this.gain = gain;
		}
		if (this.isOR()) {
			for (WishTree child : this.getChildren()) {
				child.setGain(gain);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public List<WishTree> getChildren() {
		return children;
	}

	public Vector<WishTree> getChildren(boolean AND, boolean OR, boolean NR) {
		Vector<WishTree> res = new Vector<WishTree>();
		if (!(this.children == null)) {
			for (WishTree child : this.children) {
				if ((AND && child.isAND()) || (OR && child.isOR()) || (NR && child.isNR())) {
					res.add(child);
				}
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static WishTree allTheSame(int nrDice, int whichFace) {
		WishTree newTree = new WishTree(true, false);
		for (int i = 0; i < nrDice; i++) {
			WishTree newChild = new WishTree(whichFace);
			newTree.addChild(newChild);
		}
		return newTree;
	}

	public static WishTree nTupel(int nrFaces, int n) {
		WishTree newTree = new WishTree(false, true);
		for (int i = 1; i <= nrFaces; i++) {
			WishTree newChild = new WishTree(true, false);
			for (int j = 1; j <= n; j++) {
				newChild.addChild(new WishTree(i));
			}
			newTree.addChild(newChild);
		}
		return newTree;
	}

	public static WishTree street(int nrFaces) {
		WishTree newTree = new WishTree(true, false);
		for (int i = 1; i <= nrFaces; i++) {
			WishTree child = new WishTree(i);
			newTree.addChild(child);
		}
		return newTree;
	}

	public static WishTree partialStreet(int nrFaces, int streetSize) {
		WishTree newTree = new WishTree(false, true);
		int freeDice = nrFaces - streetSize;

		for (int i = 1; i <= freeDice + 1; i++) {
			WishTree tmpStreet = new WishTree(true, false);
			for (int j = i; j < i + streetSize; j++) {
				WishTree child = new WishTree(j);
				tmpStreet.addChild(child);
			}
			newTree.addChild(tmpStreet);
		}

		return newTree;

	}

	public static WishTree zehnTausend() {

		WishTree result = new WishTree(false, true);
		result.addChild(new WishTree(1));
		result.addChild(new WishTree(5));
		result.addChild(street(6));
		result.addChild(nTupel(6, 3));
		result.addChild(andCopy(WishTree.nTupel(6, 2), 3)); // triple paar 6
															// faces

		return result;
	}

	public static WishTree andCopy(WishTree original, int nrCopies) {
		WishTree result = new WishTree(true, false);
		for (int i = 1; i <= nrCopies; i++) {
			result.addChild(original.Clone());
		}
		return result;
	}

	public static WishTree pair(int nrFaces) {
		WishTree newTree = new WishTree(false, true);
		for (int i = 1; i <= nrFaces; i++) {
			WishTree newChild = new WishTree(true, false);
			newChild.addChild(new WishTree(i));
			newChild.addChild(new WishTree(i));
			newTree.addChild(newChild);
		}
		return newTree;

	}

	public static WishTree fullHouse(int nrFaces) {
		WishTree newTree = new WishTree(true, false);
		WishTree bigChild = WishTree.nTupel(nrFaces, 3);
		WishTree smallChild = WishTree.nTupel(nrFaces, 2);
		newTree.addChild(bigChild);
		newTree.addChild(smallChild);
		return newTree;
	}

	public static String[] getAvailablePresets() {
		int totalLenght = availableAdvancedPresets.length + availableSimplePresets.length;
		String[] availablePresets = new String[totalLenght];
		int i = 0;
		while (i < availableAdvancedPresets.length) {
			availablePresets[i] = availableAdvancedPresets[i];
			i++;
		}
		int j = 0;
		while (i < totalLenght) {
			availablePresets[i] = availableSimplePresets[j];
			i++;
			j++;
		}

		return availablePresets;
	}

	public static String[] getAvailableSimplePresets() {
		return availableSimplePresets;
	}

	public static String[] getAvailableAdvancedPresets() {
		return availableAdvancedPresets;
	}

	public boolean isLeaf() {
		return false;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

}

//public void removeRedundancy() { 
//	// removes branches that contains other branches. Ex : if there is a branch
//	// (AND;1) and a branch (AND;5;2;1) it will remove the latter
//	// there is no need for anyhting that contains a 1 (it will already be
//	// calculated)
//	Vector<WishTree> newChildren = new Vector<WishTree>();
//	Vector<WishTree> toRemove = new Vector<WishTree>();
//
//	for (WishTree childA : this.getChildren()) {
//		boolean keepChildA = true;
//
//		for (WishTree newChild : newChildren) {
//			int test = branchContains(childA, newChild); // if A in B returns 1; if Bin A returns 2, if A=B returns 0, otherwise -1
//			if (test == 0 || test == 2) {
//				keepChildA = false;
//			}
//			if (test == 1) {
//				toRemove.add(newChild);
//			}
//		}
//		if (!toRemove.isEmpty()) {
//			newChildren.removeAll(toRemove);
//		}
//		if (keepChildA) {
//			newChildren.add(childA);
//		}
//	}
//
//	this.resetChildren();
//	this.addChildren(newChildren);
//}
//
//public int branchContains(WishTree branchA, WishTree branchB) {
//	// if A in B returns 1; if Bin A returns 2, if A=B returns 0, otherwise -1
//
////	if (branchA.getGain() != branchB.getGain()) {
////		return -1;
////	}
//
//	WishTree tempA = new WishTree();
//	WishTree tempB = new WishTree();
//
//	boolean swiched = false;
//	if (branchA.getChildren().size() < branchB.getChildren().size()) {
//		// this avoids terminating after not finding an element of the
//		// bigger set in the subset.
//		tempA = branchA.Clone();
//		tempB = branchB.Clone();
//	} else {
//		tempB = branchA.Clone();
//		tempA = branchB.Clone();
//		swiched = true; // this is a bit dirty TODO better if possible
//	}
//
//	int childRemovedFormB = 0;
//	boolean childAIsPresent;
//	for (WishTree childA : tempA.getChildren()) {
//		childAIsPresent = tempB.removeOneOccurenceOf(childA.getNr());
//		if (!childAIsPresent) {
//			return -1; // -1 : no branch contains the other
//		} else { // sth was removed
//			childRemovedFormB++;
//			if (tempB.getChildren().size() == 0
//					&& tempA.getChildren().size() > childRemovedFormB) {
//				if (!swiched) {
//					return 2;// branch B is containes in A
//				} else {
//					return 1;
//				}
//			}
//		}
//	}
//	if (tempB.getChildren().size() == 0
//			&& tempA.getChildren().size() == childRemovedFormB) {
//		return 0;// branches are equivalent
//	}
//
//	if (!swiched) {
//		return 1;// branch B is containes in A
//	} else {
//		return 2;
//	}
//
//}

//private void removeUselessCases(Rules ruleset, int nrFaces) {
//
//	if (ruleset == null || !ruleset.isCanKeepMultipleGoalsPerThrow()) {//this test should be true if gain is not relevant. removeRedundancy breaks gain.
//		this.removeRedundancy();//might be obsolote, the new should work the same in the same cases
//	}else {
//		this.removeRedundancy_New(nrFaces);
//	}
//	
//}

//
//private void addBranchesForMultipleGoal(WishTree tree, int nrDice) {
//	int nrBranches = tree.getChildren().size();
//	boolean[] currBranchesSum = new boolean[tree.getChildren().size()];
//	currBranchesSum[0] = true;
//	for (int i=1;i<nrBranches;i++) {
//		currBranchesSum[i] = false;
//	}
//	
//	Vector<WishTree> newBranches = new Vector<WishTree>();
//	newBranches = branchSumsToAddRec(0, currBranchesSum, newBranches, tree, nrDice);
//	tree.getChildren().addAll(newBranches);
//}

//
//private Vector<WishTree>  branchSumsToAddRec(int recLevel, boolean[] currBranchesSum, Vector<WishTree> newBranchesToAdd,  WishTree tree, int nrDice) {
//
////	Integer[] currGoal = intersectionToGoal(currIntersec, tree);
//	WishTree newBranch = new WishTree(true, false);
//	int gain = 0;
//	for (int i=0;i<currBranchesSum.length;i++) {
//		if (currBranchesSum[i]) {
//			WishTree currBranch = tree.getChildren().get(i);
//			
//			newBranch.getChildren().addAll(currBranch.Clone().getChildren());
//			gain += tree.getChildren().get(i).getGain();
//		}
//	}
//	newBranch.setGain(gain);
//	
//	boolean sumIsNotNull= (newBranch.getChildren().size()<=nrDice);
//	int nrBranch = tree.getChildren().size();
//	boolean lastDigit = (recLevel == tree.getChildren().size() -1);
//	if (lastDigit) {
//		System.out.println("this is a breakpoint");
//	}
//	System.out.println("rec level : " + recLevel);
//	
//	if (sumIsNotNull && !lastDigit) {//2024-03 modification
//		
////		pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//		newBranchesToAdd.add(newBranch);
//		
////		currIntersec[recLevel + 1]++;
//		currBranchesSum[recLevel + 1]= true;
//		newBranchesToAdd = branchSumsToAddRec(recLevel + 1, currBranchesSum, newBranchesToAdd, tree, nrDice);
//	}
//
//	if (sumIsNotNull && lastDigit) {
//
////		pairProbaExpect = calculateOneIntersection(currIntersec, pairProbaExpect, tree, currGoal, recLevel);
//		newBranchesToAdd = branchSumsToAddRec(recLevel + 1, currBranchesSum, newBranchesToAdd, tree, nrDice);
//		
//		return newBranchesToAdd;
//	}
//	
//	if (!sumIsNotNull && lastDigit) {
//		return newBranchesToAdd;
//	}
//
//	if (!currBranchesSum[recLevel]) {
//		return newBranchesToAdd;
//	}
//	
//	if (currBranchesSum[recLevel]) {
//		currBranchesSum[recLevel] = false;
//		currBranchesSum[recLevel + 1] = true;
//		
//		newBranchesToAdd = branchSumsToAddRec(recLevel + 1, currBranchesSum, newBranchesToAdd, tree, nrDice);
//		
//		currBranchesSum[recLevel + 1]= false;
//		currBranchesSum[recLevel] = true;
//	}
//	
////	if (currBranchesSum[recLevel] != 0 && currBranchesSum[recLevel] != 1) {
////		Main.say("ERROR : currIntersec of anything should be 0 or 1");
////	}
////	
////	if (!calculateIntersection && !lastDigit) {
////		// currIntersec[recLevel] = 0;
////		// return res;
////	}
//
//	return newBranchesToAdd;
//}


//if (newMultipleGoal != null) {
//int newGain = newMultipleGoal.getGain();
//int i = 0;
//while (tree.getChildren().get(i).getGain() > newGain) {
//	i++;
//}
//tree.getChildren().add(i, newMultipleGoal);
//tree.getChildren().add(tree.getChildren().size()/2, newMultipleGoal);
//}


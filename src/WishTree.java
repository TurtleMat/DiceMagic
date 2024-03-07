import java.util.List;
import java.util.Vector;

public class WishTree {
	
	private static String[] availablePresets=new String[]{"pair", "ntupel", "street", "tenthousand", "fullhouse", "allthesame"}; // ,"partialstreet"
	

	private boolean AND;
	private boolean OR;
	private boolean NR;

	private int number;
	
	
	private boolean fixable= true;

	private List<WishTree> children;

	public WishTree() {
		this.children = new Vector<WishTree>();
	}
	
	public WishTree(String nodeString) {
		this.children = new Vector<WishTree>();
		if (nodeString.equalsIgnoreCase("AND")){
			this.setValue(true, false, false, -1);
		} else if (nodeString.equalsIgnoreCase("OR")){
			this.setValue(false, true, false, -1);
		} else {
			int i;
			try {
				i = Integer.parseInt(nodeString);
				this.setValue(false, false, true, i);
			}
			catch (NumberFormatException e) {
				this.setValue(false, false, false, -2);
			}
		}
	}

	public WishTree(int i) {
//		this.node = new Wish(false, false, true, i);
		this.setValue(false, false, true, i);
		this.children = null;
	}

	private void setValue(boolean AND, boolean OR, boolean NR, int i) {
		this.AND = AND;
		this.OR = OR;
		this.NR = NR;
		this.number = i;
	}

	public WishTree(boolean AND, boolean OR) {
//		this.node = new Wish(AND, OR, false, -1);
		this.setValue(AND, OR, false, -1);
		this.children = new Vector<WishTree>();
	}
	
	public WishTree(boolean AND, boolean OR, Vector<WishTree> children) {
//		this.node = new Wish(AND, OR, false, -1);
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
//		this.node = new Wish(tree.node);
		this.AND = tree.AND;
		this.OR = tree.OR;
		this.NR = tree.NR;
		this.number = tree.number;
		
		this.children = new Vector<WishTree>();
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static WishTree importAndPrepareTree(String encodingString, int nrFaces, int nrDice,	boolean verbose) {
		String firstTestResult = firstTest(encodingString); 
		if (firstTestResult == ""){
			return null;
		}
		WishTree tree = WishTree.validStringToTree(firstTestResult, nrFaces, nrDice);
		if (tree==null){
			return null;
		}
		if (verbose) {
			System.out.println("tree import...");
			System.out.println(tree.toString());
			System.out.println("tree imported.");
			System.out.println("");
		}
		
		prepareTree(tree, verbose, nrDice);

		return tree;

	}
	
	private static String firstTest(String encodingString) {
		int i = 0;
		int nrParenthesis = 0;
		String res = encodingString;
		if (!(res.charAt(i) == '(')) {
			System.out.println("this should start with a parehthesis. please use following format : ");
			System.out.println("(OPERATOR;TREE;...;TREE)");
			System.out.println("adding a parenthesis and trying to proceed.");
			res = "("+res;
		}
		
		boolean containsSemicolon = false;
		
		for (int j=0; j < res.length(); i++){
			char currChar = res.charAt(j);
			if (currChar == ';'){
				containsSemicolon = true;
			}
			if (currChar == '('){
				nrParenthesis++;
				if (res.charAt(j+1) == '('){
					res = res.substring(0, j+1) + res.substring(j+2);
					j--;
					nrParenthesis--;
					System.out.println("you cann't have several opening parenthesis in a raw. please use following format : ");
					System.out.println("(OPERATOR;TREE;...;TREE)");
					System.out.println("removing a parenthesis and trying to proceed.");
				}
			}
			if (currChar == ')'){
				nrParenthesis--;
			}
			j++;
		}
		
		
		
		if (!containsSemicolon){
			System.out.println("this does not contain any semicolon (;). please use following format : ");
			System.out.println("(OPERATOR;TREE;...;TREE)");
			System.out.println("cann't fix that (yet?). Aborting.");
			return "";
		}
		
		if (nrParenthesis > 0){
			System.out.println("not enough closing parenthesis. adding this amount at the end");
			while (nrParenthesis>0){
				res += ")";
				nrParenthesis--;
			}
		}
		//this segment should be redundant
		i = res.length()-1;
		if (!(res.charAt(i) == ')')) {
			System.out.println("this should end with a parehthesis. please use following format : ");
			System.out.println("(OPERATOR;TREE;...;TREE)");
			System.out.println("adding a parenthesis and trying to proceed.");
			res = res + ")";
		}
		
		return res;
	}
	
	public static WishTree presetStringToTree(String preset, int nrFaces, int nrDice){
		preset.toLowerCase();
		
		
		switch (preset){
		case "pair":
			return pair(nrFaces);
		case "tenthousand":
			return zehnTausend();
		case "street": //TODO remove partialstreet if safe.
			return partialStreet(nrFaces, Math.min(nrFaces, nrDice));
//		case "partialstreet":
//			return partialStreet(nrFaces, nrDice);
		case "fullhouse":
			return fullHouse(nrFaces);
		} 
		
		String tupelcheck = preset.substring(0, 6);
		if (tupelcheck.equalsIgnoreCase("ntupel")){
			int tupelSize;
			try{
				tupelSize = Integer.parseInt(preset.substring(6));
				return nTupel(nrFaces, tupelSize); 
			} catch  (final NumberFormatException e){
				System.out.println("imput error : ntupel string should be for instance ntupel3, this yould be a triple ");
				return null;
			}
			
		}
		
		String allSameCheck = preset.substring(0, 10);
		if (allSameCheck.equalsIgnoreCase("allthesame")){
			int whichFace;
			try {
				whichFace = Integer.parseInt(preset.substring(10));
				return allTheSame(nrDice, whichFace);
			} catch (final NumberFormatException e){
				System.out.println("imput error : all the same should be written 'allthesame5' if you want all the dice to show 5 ");
			}
		}
		
		return null;
	}

	private static WishTree handleImputLeaf(String encodingString, int nrFaces, int nrDice){
		int res;
		try {
			res = Integer.parseInt(encodingString);
			return new WishTree(res); // TODO the child still have no parent
		} catch (final NumberFormatException e){
			
			WishTree resTree = presetStringToTree(encodingString, nrFaces, nrDice);
			if (resTree != null){
				return resTree;
			}else {
				System.out.println("this leaf (" + encodingString + ") is not an integer or a valid preset. removed.");
				return null;
			}
			
			
		}
	}
	
	public static WishTree validStringToTree(String encodingString, int nrFaces, int nrDice) {
		
		int i = 0;
		if (!(encodingString.charAt(i) == '(')) { // if it doesn't start with a
													// parenthesis, it must be a
													// number or a preset
			
			WishTree resTree = handleImputLeaf(encodingString, nrFaces, nrDice);
			return resTree;
//			int res;
//			try {
//				res = Integer.parseInt(encodingString);
//				return new WishTree(res); // TODO the child still have no parent
//			} catch (final NumberFormatException e){
//				System.out.println("this leaf (" + encodingString + ") is not an integer. removed.");
//				return null;
//			}

		} else {
			i++;
			int j = encodingString.indexOf(";", i);
			
			if (j==-1){
				System.out.println("this (sub-)Tree :"+ encodingString  +" is invalid. Removing it.");
				System.out.println("possible mistake : leaves don't use parenthesis, or you forgot a ';'");
				return null;
			}
			
			String[] childStrings = new String[encodingString.length()]; // length  overkill  but  wathever for now
			String operator = encodingString.substring(i, j); // at this point, operator should be AND  or OR. substring should be exclusive for the max bound
			

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
//				newTree.node = new Wish(true, false, false, -1);
				newTree.setValue(true, false, false, -1);
			} else if(operator.equalsIgnoreCase("OR")) {
//				newTree.node = new Wish(false, true, false, -1);
				newTree.setValue(false, true, false, -1);
			} else {
				System.out.println("invalid operator (" + operator + "). removing it and its children");
				return null;
			}
			if (childStrings != null){
				for (String childString : childStrings) {
					if (childString != null) {

						newTree.addChild(validStringToTree(childString, nrFaces, nrDice));
					}
				}
			}
			
			return newTree;
		}
	}

	public static WishTree prepareTree(WishTree tree, boolean verbose, int nrDice) {  //should be void or not have tree as argument?
		
		if (verbose) {
			System.out.println("imput tree : ");
			System.out.println(tree.toString());
		}
		
		boolean fixable = tree.checkAndRepairTree();
		if (verbose){
			System.out.println("checking and reparing tree if possible/necessary...");
			if (fixable){
				System.out.println(tree.toString());
				System.out.println("tree is valid and repaired");
			} else{
				System.out.println("Tree is invalid. setting to null");
				return null;
			}
		}
		
		
		
//		tree.calculateMinNrDiceForBranches_old();
//		tree.suppressBigChildren(nrDice);
//		if (verbose){
//			System.out.println("calculating branches size and supressing impossible branches");
//			System.out.println(tree.toString());
//			System.out.println("removed.");
//			System.out.println("");
//			
//		}
		
		
		tree.removeOperatorLeaves();
		if (verbose) {
			System.out.println("removing operator without children...");
			System.out.println(tree.toString());
			System.out.println("removed.");
			System.out.println("");
		}
		
		tree.simplifyTree();
		tree.tryRemovingBigAnd(nrDice);
		if (verbose) {
			System.out.println("simplifying tree...");
			System.out.println(tree.toString());
			System.out.println("tree simplified");
			System.out.println("");
		}

		tree.developpTree(nrDice);
		if (verbose) {
			System.out.println("developping tree...");
			System.out.println(tree.toString());
			System.out.println("tree developped");
		}

		tree.normaliseTerminalBranches();
		if (verbose) {
			System.out.println("normalising tree...");
			System.out.println(tree.toString());
			System.out.println("tree normalised");
		}

		tree.removeRedundancy();
		if (verbose) {
			System.out.println("removing redundency...");
			System.out.println(tree.toString());
			System.out.println("redundencies removed");
		}
		
		return tree;

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	public boolean checkAndRepairTree(){
//		this.checkAndRepairNode(null);
		this.checkAndRepairNode(null);
		return this.isFixable();
	}
	
	public void checkAndRepairNode(WishTree parent){ //TODO find better name
		if (fixable){
			List<WishTree> children = this.getChildren();
			Vector<WishTree> toRemove = new Vector<WishTree>();
			Vector<WishTree> toAdd = new Vector<WishTree>();
			if (children != null){
				for (WishTree child : children){
					List<WishTree> childChildren = child.getChildren();
					if ( childChildren != null && ((Integer)childChildren.size()).equals(1)){
						toRemove.add(child);
						toAdd.addAll(childChildren);
					}
					if ( (childChildren == null || ((Integer)childChildren.size()).equals(0))  && !child.isNR() ){
						toRemove.add(child);
					}
					if (childChildren!= null && child.isNR()){
						child.setChildren(null);
					}
					
				}
				if (!toAdd.isEmpty()||!toRemove.isEmpty()){
					children.addAll(toAdd);
					children.removeAll(toRemove);
					this.checkAndRepairNode(parent);
				} else{
					for (WishTree child : children){
						child.checkAndRepairNode(this);
					}
				}
			}
			
//			

		} else {
			if (!this.isFixable() && parent != null){
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
	
	private void removeOperatorLeaves(){
		Vector<WishTree> toremove = new Vector<>();
		for (WishTree operatorChild : this.getChildren(true, true, false)){
			
			operatorChild.removeOperatorLeaves();
			
			if (operatorChild.getChildren() == null || operatorChild.getChildren().size() == 0){
				toremove.add(operatorChild);
			}
		}
		this.getChildren().removeAll(toremove);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	
	public void tryRemovingBigAnd(int nrDice){ // should be safe to call after simplify. does not remove all cases.
												// TODO can be done in simplifynode
		
		Vector<WishTree> operatorChildren = this.getChildren(true, true, false);
		Vector<WishTree> toRemove = new Vector<>();
		
		for (WishTree operatorChild : operatorChildren){
			Vector<WishTree> grandChildren = operatorChild.getChildren(true, true, true); //the OR have at least one leaf they count as 1
			if (grandChildren != null && grandChildren.size()!= 0){
				if (operatorChild.isAND()){
					if (grandChildren.size() > nrDice ){
						toRemove.add(operatorChild);
					}
				}
			}
		}
		this.getChildren().removeAll(toRemove);
		for (WishTree operatorChild : this.getChildren(true, true, false)){
			operatorChild.tryRemovingBigAnd(nrDice);
		}
		
		
	}
	
	public void simplifyTree() { // supress unnecessary parenthesis ex : (A and
									// B) and C becomes A and B and C
		if (this.children != null) {
			for (WishTree child : this.children) {
				child.simplifyTree();
			}
			this.simplifyNode();
		}
	
	} //TODO could ignore leaves

	public void simplifyNode() {
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
			;// while (scanChildren(this, true, false, false))
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
			;// while (scanChildren(this, false, true, false))
		}
		// at this point an AND can not have an AND as children and a OR can not
		// have a OR
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public void developpTree(int nrDice) { // developps the remaining parenthesis Ex : (A
									// or B) and C becomes (A and C) OR (B and
									// C)
		if (this.children != null) { // TODO check if all trees have an OR root
										// after that

			this.developpNode(nrDice);
//			this.simplifyTree(); 

			for (WishTree child : this.children) {
				child.developpTree(nrDice);
			}
		}
//		this.simplifyTree(); //overkill, should only have to check problems around root
		this.simplifyNode();
	}

	public void developpNode( int nrDice) {

		if (this.isAND()) {
			Vector<WishTree> orChildren = this.getChildren(false, true, false);
			if (!orChildren.isEmpty()) { // a AND node that has at least one OR
											// child

				WishTree currOrChild = orChildren.firstElement();
				Vector<WishTree> newChildren = new Vector<WishTree>();
				
				for (WishTree grandChild : currOrChild.children) {

					WishTree newAndChild = new WishTree();
					newAndChild.setValue(true, false, false, -1);
					
					if (grandChild.isAND()){ //this if should ensure that the tree remains simplified
						newAndChild.addChildren(grandChild.getChildren()); 
					}else {
						newAndChild.addChild(grandChild);
					}

					this.children.remove(currOrChild);
					for (WishTree currSiblings : this.children) {
						newAndChild.addChild(currSiblings);
						// newChild.parent = this;
					}
					
					
					// since the tree is simplified, discarding the big AND should not be a problem
					// there are no two following AND, revoving a big AND from a OR should not break anything.
					// this most likely still doesn't remove all of them. it might though.
					if (newAndChild.getChildren().size() <= nrDice){ 
						newChildren.add(newAndChild);
					}
					
				}

				this.setToOr();
				this.children = newChildren;
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------


	public void normaliseTerminalBranches() { // transforms nr nodes in and
												// nodes with the number. Ex : 1
												// -> (AND;1)

		if (this.isAND()) { // should only happen when the whole tree
										// is (AND;leaves)
			WishTree copy = this.Clone();
			this.resetChildren();
//			this.node = new Wish(false, true, false, -1);
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
				} else if (child.isAND()) {
					normalisedChildren.add(child);
				} else {
					System.out.println("you should not see that, normaliseTerminalBranche called with wrong arguments ");
				}
			}

			this.resetChildren();
			this.addChildren(normalisedChildren);

		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public void removeRedundancy() { // removes branches that contains other
										// branches. Ex : if there is a branch
										// (AND;1) and a branch (AND;5;2;1) it
										// will remove the latter
		// there is no need for anyhting that contains a 1 (it will already be
		// calculated)
		Vector<WishTree> newChildren = new Vector<WishTree>();
		Vector<WishTree> toRemove = new Vector<WishTree>();

		for (WishTree childA : this.getChildren()) {
			boolean keepChildA = true;

			for (WishTree newChild : newChildren) {
				int test = branchContains(childA, newChild);
				if (test == 0 || test == 2) {
					keepChildA = false;
				}
				if (test == 1) {
					toRemove.add(newChild);
				}
			}
			if (!toRemove.isEmpty()) {
				newChildren.removeAll(toRemove);
			}
			if (keepChildA) {
				newChildren.add(childA);
			}
		}

		this.resetChildren();
		this.addChildren(newChildren);
	}

	public int branchContains(WishTree branchA, WishTree branchB) { 
		// if A in B returns 1; if Bin A returns 2, if A=B returns  0, otherwise -1

		WishTree tempA = new WishTree();
		WishTree tempB = new WishTree();

		boolean swiched = false;
		if (branchA.getChildren().size() < branchB.getChildren().size()) { 
			// this avoids terminating after not finding an element of the bigger set in the subset.
			tempA = branchA.Clone();
			tempB = branchB.Clone();
		} else {
			tempB = branchA.Clone();
			tempA = branchB.Clone();
			swiched = true; // this is a bit dirty TODO better if possible
		}

		int childRemovedFormB = 0;
		boolean childAIsPresent;
		for (WishTree childA : tempA.getChildren()) {
			childAIsPresent = tempB.removeOneOccurenceOf(childA.getNr());
			if (!childAIsPresent) {
				return -1; // -1 : no branch contains the other
			} else { // sth was removed
				childRemovedFormB++;
				if (tempB.getChildren().size() == 0
						&& tempA.getChildren().size() > childRemovedFormB) {
					if (!swiched) {
						return 2;// branch B is containes in A
					} else {
						return 1;
					}
				}
			}
		}
		if (tempB.getChildren().size() == 0
				&& tempA.getChildren().size() == childRemovedFormB) {
			return 0;// branches are equivalent
		}

		if (!swiched) {
			return 1;// branch B is containes in A
		} else {
			return 2;
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public String nodeToString(WishTree tree, int depth) {
		String res = "";
		for (int i = 0; i <= depth; i++) {
			res += "| ";
		}
		res += this.nodeValueToString();
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
			return ""+number;
		}
		if (!(this.isAND() || this.isOR() || this.isNR())) {
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
				if (child == null){
					return "0";
				}else {
					res = toString(child, depth + 1, res);
				}
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public List<WishTree> getChildren() {
		return children;
	}

	public Vector<WishTree> getChildren(boolean AND, boolean OR, boolean NR) {
		Vector<WishTree> res = new Vector<WishTree>();
		if (!(this.children == null)) {
			for (WishTree child : this.children) {
				if ((AND && child.isAND()) || (OR && child.isOR())
						|| (NR && child.isNR())) {
					res.add(child);
				}
			}
		}
		return res;
	}

	public void addChild(WishTree tree) {
		if (tree != null){
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


	public boolean scanChildren(WishTree tree, boolean AND, boolean OR,
			boolean NR) { // scanns for children that have one of the TRUE
							// values in common with arguments
		for (WishTree child : tree.children) {
			if ((AND && child.isAND()) || (OR && child.isOR())
					|| (NR && child.isNR())) {
				return true;
			}
		}
		return false;
	}

	public boolean isAND() {
		return AND;
	}

	public boolean isOR() {
		return OR;
	}

	public boolean isNR() {
		return NR;
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
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static WishTree allTheSame (int nrDice, int whichFace){
		WishTree newTree = new WishTree(true, false);
		for (int i = 0; i<nrDice;i++){
			WishTree newChild = new WishTree(whichFace);
			newTree.addChild(newChild);
		}
		return newTree;
	}
	
	public static WishTree nTupel(int nrFaces, int n){
		WishTree newTree = new WishTree(false, true);
		for (int i= 1; i <= nrFaces; i++){
			WishTree newChild = new WishTree(true, false);
			for (int j = 1; j<=n; j++){
				newChild.addChild(new WishTree(i));
			}
			newTree.addChild(newChild);
		}
		return newTree;
	}
	
	public static WishTree street(int nrFaces){
		WishTree newTree = new WishTree(true, false);
		for (int i= 1; i <= nrFaces; i++){
			WishTree child = new WishTree(i);
			newTree.addChild(child);
		}
		return newTree;
	}
		
	public static WishTree partialStreet(int nrFaces, int streetSize){
		WishTree newTree = new WishTree(false, true);
		int freeDice = nrFaces-streetSize;
		
		for (int i = 1; i<=freeDice+1; i++){
			WishTree tmpStreet = new WishTree(true, false);
			for (int j= i; j < i + streetSize; j++){
				WishTree child = new WishTree(j);
				tmpStreet.addChild(child);
			}
			newTree.addChild(tmpStreet);
		}
		
		return newTree;
	
	}
	
	public static WishTree andCopy(WishTree original, int nrCopies){
		WishTree result = new WishTree(true, false);
		for (int i = 1; i<= nrCopies; i++){
			result.addChild(original.Clone());
		}
		return result;
	}
	
	public static WishTree zehnTausend(){
		
		WishTree result = new WishTree(false, true);
		result.addChild(new WishTree(1));
		result.addChild(new WishTree(5));
		result.addChild(street(6));
		result.addChild(nTupel(6, 3));
		result.addChild(andCopy(WishTree.nTupel(6, 2), 3)); //triple paar 6 faces
		
		return result;
	}
	
	public static WishTree pair(int nrFaces){
		WishTree newTree = new WishTree(false, true);
		for (int i= 1; i <= nrFaces; i++){
			WishTree newChild = new WishTree(true, false);
			newChild.addChild(new WishTree(i));
			newChild.addChild(new WishTree(i));
			newTree.addChild(newChild);
		}
		return newTree;
		
	}
		
	public static WishTree fullHouse(int nrFaces){
		WishTree newTree = new WishTree(true, false);
		WishTree bigChild = WishTree.nTupel(nrFaces, 3);
		WishTree smallChild = WishTree.nTupel(nrFaces, 2);
		newTree.addChild(bigChild);
		newTree.addChild(smallChild);
		return newTree;
	}
	
	public static String[] getAvailablePresets(){

		return availablePresets;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	
}


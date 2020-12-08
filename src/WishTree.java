import java.util.List;
import java.util.Vector;


public class WishTree {


	private Wish node;
//	private WishTree parent;
	private List<WishTree> children;


	public WishTree() {
		this.children = new Vector<WishTree>();
	}
	
	public WishTree(int i){
		this.node = new Wish(false, false, true, i);
		this.children = null;
	}
	
	public WishTree (boolean AND, boolean OR){
		this.node = new Wish(AND, OR, false, -1);
		this.children = new Vector<WishTree>(); 
	}

	public static WishTree stringToTree(String encodingString){
		int i = 0;
		if (!(encodingString.charAt(i) =='(')){ // if it doesn't start with a parenthesis, it must be a number
			int res;
			res = Integer.parseInt(encodingString);
			return new WishTree(res); // TODO the child still have no parent
			
		}else{
			i++; 
			int j =  encodingString.indexOf(";", i);
			String[] childStrings = new String[encodingString.length()]; // length overkill but wathever for now
			String operator = encodingString.substring(i, j); // at this point, operator should be AND or OR. substring should be exclusive for the max bound

			i = j+1; 
			int childNr = 0;
			int parenthesis = 1;
			
			while (parenthesis >0){// big while probably full of mistakes
				char c = encodingString.charAt(i);
				String s=String.valueOf(c);  
				if (c == '('){
					parenthesis++;
					if (parenthesis == 2){
						childStrings[childNr] = s;
					}else{
						childStrings[childNr] += s;
					}
				} else if (c == ')'){
					parenthesis--;
					if (!(parenthesis==0)){
						childStrings[childNr] += s;
					}
					
				} else if (c == ';' && parenthesis == 1){
					childNr++;
				}else {
					if (childStrings[childNr] == null){
						childStrings[childNr] = s;
					}else{
						childStrings[childNr] += s;
					}
					
				}
				
				i++;
			} // at the end of this while, every child should be stored as a valid string in the childStrinds vector
			
			WishTree newTree = new WishTree();
			if (operator.equals("AND")){
				newTree.node = new Wish(true, false, false, -1);
			}
			if (operator.equals("OR")){
				newTree.node = new Wish(false, true, false, -1);
			} 
			for (String childString : childStrings){
				if (childString != null){

					newTree.addChild(stringToTree(childString));
				}
			}
			return newTree;
		}
	}
	
	public WishTree Clone(){ // Recursively clones tree
		WishTree newTree = new WishTree(this);
		if (this.children != null) {
			for (WishTree child : this.children){
				newTree.addChild(child.Clone());
			}
		}
		return newTree;
	}
	
	public WishTree(WishTree tree) {
		this.node = new Wish(tree.node);
		this.children = new Vector<WishTree>();
	}
	
	public static WishTree importAndPrepareTree(String encodingString, boolean verbose){
		
		WishTree tree = WishTree.stringToTree(encodingString);
		if (verbose){
			System.out.println("tree import...");
			System.out.println(tree.toString());
			System.out.println("tree imported.");
			System.out.println("");
		}

		tree.simplifyTree();
		if (verbose){
			System.out.println("simplifying tree...");
			System.out.println(tree.toString());
			System.out.println("tree simplified");
			System.out.println("");
		}

		tree.developpTree();
		if (verbose){
			System.out.println("developping tree...");
			System.out.println(tree.toString());
			System.out.println("tree developped");
		}

		tree.normaliseTerminalBranches();
		if (verbose){
			System.out.println("normalising tree...");
			System.out.println(tree.toString());
			System.out.println("tree normalised");
		}

		tree.removeRedundancy();
		if (verbose){
			System.out.println("removing redundency...");
			System.out.println(tree.toString());
			System.out.println("redundencies removed");
		}

		return tree;
		
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------	

	public void simplifyTree (){ 		// supress unnecessary parenthesis  ex : (A and B) and C becomes A and B and C
		if (this.children != null){
			for (WishTree child : this.children){
				child.simplifyTree();
			}
			this.simplifyNode();
		}
	}

	public void simplifyNode (){
		Vector<WishTree> tempWrite;
		tempWrite = new Vector<WishTree>();


		if (this.node.isAND()){
			while (scanChildren(this, true, false, false)){
				for (WishTree child : this.children){
					if (child.node.isAND()){
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}	
				this.children = tempWrite;
			} ;// while (scanChildren(this, true, false, false))
		}

		if (this.node.isOR()){
			while (scanChildren(this, false, true, false)){
				for (WishTree child : this.children){
					if (child.node.isOR()){
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}
				this.children = tempWrite;
			};// while (scanChildren(this, false, true, false))
		}
		// at this point an AND can not have an AND as children and a OR can not have a OR
	}

	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public void developpTree(){ 		// developps the remaining parenthesis  Ex : (A or B) and C becomes (A and C) OR (B and C)
		if (this.children != null){		// TODO check if all trees have an OR root after that
			
			this.developpNode();
			this.simplifyTree();
			
			for (WishTree child : this.children){
				child.developpTree();
			}
		}
		this.simplifyTree();
	}
	
	public void developpNode (){
		
		if (this.node.isAND()){
			Vector<WishTree> orChildren = this.getChildren(false, true, false);
			if (!orChildren.isEmpty()){ // a AND node that has at least one OR child
				
				WishTree currOrChild = orChildren.firstElement();   
				Vector<WishTree> newChildren = new Vector<WishTree>();
				for (WishTree grandChild : currOrChild.children){
					
					WishTree newChild = new WishTree();
					newChild.node = new Wish(true, false, false, -1);
					newChild.addChild(grandChild);
					
					this.children.remove(currOrChild);
					for (WishTree currSiblings : this.children){
						newChild.addChild(currSiblings);
//						newChild.parent = this;
					}			
					
					newChildren.add(newChild);
				}
				
				this.node.setToOr();
				this.children = newChildren;
			}
		} 
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public void normaliseTerminalBranches_old(){ // transforms nr nodes in and nodes with the number. Ex : 1  -> (AND;1)
		int i = 0;
		Vector<WishTree> normalisedChildren = new Vector<WishTree>();
//		WishTree leaves = new WishTree(this.getNode().isAND(), this.getNode().isOR()); // TODO : check if this works
		WishTree leaves = new WishTree(true, false);
		for (WishTree child : this.getChildren()){
			if (child.getNode().isNR()){
//				WishTree normChild = new WishTree(true, false);
				leaves.addChild(child);
//				normalisedChildren.add(normChild);
			} else if (child.getNode().isAND()){
				normalisedChildren.add(child);
			} else {
				System.out.println("you should not see that, normaliseTerminalBranche called with wrong arguments ");
			}
		}
		if (!(leaves.getChildren() == null || leaves.getChildren().isEmpty())){

			normalisedChildren.add(leaves);
		}
		this.resetChildren();
		this.addChildren(normalisedChildren);
		
	}
	public void normaliseTerminalBranches(){ // transforms nr nodes in and nodes with the number. Ex : 1  -> (AND;1)
		
		if (this.getNode().isAND()){ // should only happen when the whole tree is (AND;leaves)
			WishTree copy = this.Clone();
			this.resetChildren();
			this.node = new Wish(false, true, false, -1);
			this.addChild(copy);
		}
		if (this.getNode().isOR()) {
			int i = 0;
			Vector<WishTree> normalisedChildren = new Vector<WishTree>();
			
			for (WishTree child : this.getChildren()){
				if (child.getNode().isNR()){
					WishTree newChild = new WishTree(true, false);
					newChild.addChild(child);
					normalisedChildren.add(newChild);
				} else if (child.getNode().isAND()){
					normalisedChildren.add(child);
				} else {
					System.out.println("you should not see that, normaliseTerminalBranche called with wrong arguments ");
				}
			}
			
			this.resetChildren();
			this.addChildren(normalisedChildren);
			
		}
		
	}
	
	
	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public void removeRedundancy(){ // removes branches that contains other branches. Ex : if there is a branch (AND;1) and a branch (AND;5;2;1) it will remove the latter
		//there is no need for anyhting that contains a 1 (it will already be calculated)
		Vector<WishTree> newChildren = new Vector<WishTree>();
		Vector<WishTree> toRemove = new Vector<WishTree>();
		
		for (WishTree childA : this.getChildren()){
			boolean keepChildA = true;
			
			for (WishTree newChild : newChildren){
				int test = branchContains(childA, newChild);
				if (test == 0 || test == 2){
					keepChildA = false;
				}
				if (test == 1){
					toRemove.add(newChild);
				}
			}
			if (!toRemove.isEmpty()){
				newChildren.removeAll(toRemove);
			}
			if (keepChildA){
				newChildren.add(childA);
			}
		}
		
		this.resetChildren();
		this.addChildren(newChildren);
	}

	public int branchContains(WishTree branchA, WishTree branchB){ // if A in B returns 1; if B in A returns 2, if A=B returns 0, otherwise -1
		
		WishTree tempA = new WishTree();
		WishTree tempB = new WishTree();
		
		boolean swiched = false;
		if (branchA.getChildren().size()<branchB.getChildren().size()){ // this avoids terminating after not finding an element of the bigger set in the sebset.
			tempA = branchA.Clone();
			tempB = branchB.Clone();
		} else {
			tempB = branchA.Clone();
			tempA = branchB.Clone();
			swiched = true; // this is a bit dirty TODO better if possible
		}
		
		int childRemovedFormB = 0;
		boolean childAIsPresent;
		for (WishTree childA : tempA.getChildren()){
			childAIsPresent = tempB.removeOneOccurenceOf(childA.getNode().getNr());
			if (!childAIsPresent){
				return -1; // -1 : no branch contains the other
			}else{ // sth was removed
				childRemovedFormB++;
				if (tempB.getChildren().size() == 0 && tempA.getChildren().size() > childRemovedFormB){
					if (!swiched){
						return 2;//branch B is containes in A
					}else {
						return 1;
					}
				}
			}
		}
		if (tempB.getChildren().size() == 0 && tempA.getChildren().size() == childRemovedFormB){
			return 0;//branches are equivalent
		}
		
		if (!swiched){
			return 1;//branch B is containes in A
		}else {
			return 2;
		}
		
	}
	

	
	//------------------------------------------------------------------------------------------------------------------------------------	
	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public String nodeToString(WishTree tree, int depth){
		String res = "";
		for (int i = 0; i <= depth; i++){
			res += "--";
		}
		res += tree.node.toString();
		res += "\n";
		return res;
	}

	public  String toString (){
		String res = "";
		res = this.toString(this, 0, res);
		return res;
	}

	public String toString (WishTree tree, int depth, String res){
		res += tree.nodeToString(tree, depth);
		if (tree.children != null){
			for (WishTree child : tree.children){
				res = toString(child, depth + 1 , res);
			}
		}
		return res;
	}

	//------------------------------------------------------------------------------------------------------------------------------------	

	public Wish getNode() {
		return node;
	}

	public List<WishTree> getChildren() {
		return children;
	}

	public Vector<WishTree> getChildren(boolean AND, boolean OR, boolean NR){
		Vector<WishTree> res = new Vector<WishTree>();
		if (!(this.children == null)){
			for (WishTree child : this.children){
				if ( (AND && child.node.isAND()) || (OR && child.node.isOR()) || (NR && child.node.isNR())) {
					res.add(child);
				}
			}
		}
		return res;
	}
	
	public void addChild(WishTree tree) {
		this.children.add(tree);
//		tree.parent = this;
	}

	public void addChildren(List<WishTree> children2) {
		this.children.addAll(children2);
		for (WishTree child : children2){
//			child.parent = this;
		}
		
	}

	public void resetChildren() {
		this.children = new Vector<WishTree>();
		
	}

	public boolean removeOneOccurenceOf(int nr) { // returns true if something was removed
		
		WishTree toRemove = null;
		int k = 0;
		while (toRemove == null && k < this.children.size()){
			if (this.children.get(k).getNode().getNr() == nr){
				toRemove = this.children.get(k);
			}
			k++;
		}
		if (toRemove != null) {
			this.children.remove(toRemove);
			return true;
		}else{
			return false;
		}
		
	}

	public void SetParent(WishTree parent){
//		this.parent = parent;
		parent.children.add(this);
	}

	public boolean scanChildren (WishTree tree, boolean AND, boolean OR, boolean NR){ // scanns for children that have one of the TRUE values in common with arguments
		for (WishTree child : tree.children){
			if ( (AND && child.node.isAND()) || (OR && child.node.isOR()) || (NR && child.node.isNR())) {
				return true;
			}
		}
		return false;
	}
	
	
}

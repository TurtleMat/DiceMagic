import java.util.List;
import java.util.Vector;


public class WishTree {


	private Wish node;
	private WishTree parent;
	private List<WishTree> children;


	public WishTree(WishTree tree) {
		this.node = new Wish(tree.node);
//		this.parent = tree.parent;
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

	public WishTree() {
		this.children = new Vector<WishTree>();
	}

	public static WishTree stringToTree(String encodingString){// (OR;(OR;1;5);( OR;(AND;1;1;1);(AND;2;2;2);(AND,3;3;3);(AND;4;4;4);(AND;5;5;5);(AND;6;6;6) ) ) should be valid
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
				String temp;
				
				if (c == '('){
					parenthesis++;
					if (parenthesis == 2){
//						childNr++;
						childStrings[childNr] = s;
						
//						System.out.println( childStrings.elementAt(childNr) ); // test if that prints s
//						System.out.println(s);
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
//					int k = encodingString.indexOf(";", i);// TODO : check if indexof works like intended (first occurence of str after index)
//					String toAdd = encodingString.substring(i, k);
//					childStrings[childNr] = toAdd;
					
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
	
	public WishTree Clone(WishTree tree){ // should recursively clone tree
		WishTree newTree = new WishTree(tree);
		if (tree.children != null) {
			for (WishTree child : tree.children){
//				this.children.add(Clone(child));
				newTree.addChild(Clone(child));
			}
		}
		
		return newTree;

	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------------------------	

	
	public void simplifyTree (WishTree tree){ 		// supress unnecessary parenthesis  ex : (A and B) and C becomes A and B and C
		if (tree.children != null){
			for (WishTree child : tree.children){
				simplifyTree(child);
			}
			simplifyNode(tree);
		}
	}

	public void simplifyNode (WishTree tree){
		Vector<WishTree> tempWrite;
		tempWrite = new Vector<WishTree>();


		if (tree.node.isAND()){
			while (scanChildren(tree, true, false, false)){
				for (WishTree child : tree.children){
					if (child.node.isAND()){
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}	
				tree.children = tempWrite;
			} ;// while (scanChildren(this, true, false, false))
		}

		if (tree.node.isOR()){
			while (scanChildren(tree, false, true, false)){
				for (WishTree child : tree.children){
					if (child.node.isOR()){
						tempWrite.addAll(child.getChildren());
					} else {
						tempWrite.add(child);
					}
				}
				tree.children = tempWrite;
			};// while (scanChildren(this, false, true, false))
		}
		// at this point an AND can not have an AND as children and a OR can not have a OR
	}

	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public void developpNode_Old (WishTree tree){  // always on a non root OR node ; always on a simplified tree ; should conserv property of being simplified
		if (!tree.node.isOR()){
			System.out.println("called on an AND or a number, won't do anything. this is to be expected");
		}else{
			if (!tree.parent.node.isAND()){
				System.out.println("error, tree is not simplified");
			}else{
				tree.parent.children.remove(tree);

				if (!(tree.parent.parent == null)){ // not in a border case for the root of the tree

					for (WishTree child : tree.children){		//	for every child of the curr node, creates a and child on new tree corresponding to distributing multiplically
						
						WishTree newChild = new WishTree();
						newChild.node = new Wish(true, false, false, -1); 
						newChild.children.add(child);						// original child
						for (WishTree cousin : tree.parent.getChildren()){
							newChild.children.add(cousin);				// AND the remaining product
						}
						
						newChild.SetParent(tree.parent.parent);

					}
					tree.parent.parent.children.remove(tree.parent);

				}else { // there is no grandparent
					WishTree newTree = new WishTree();
					newTree.node = new Wish(false,true, false, -1); // new tree is a or, will be the new root
//					tree.parent.SetParent(newTree);
					
					for (WishTree child : tree.children){		//	for every child of the curr node, creates a and child on new tree corresponding to distributing multiplically
						
						WishTree newChild = new WishTree();
						newChild.node = new Wish(true, false, false, -1); 
						newChild.children.add(child);						// original child
						
						for (WishTree cousin : tree.parent.getChildren()){
							newChild.children.add(cousin);				// AND the remaining product
						}
						
//						newChild.children.add(Clone(parent));				// AND the remaining product
						newChild.SetParent(newTree);
					}
				}
			}
		}
	}

	public WishTree developpNode (WishTree tree){
		
//		System.out.println("*****************Developping node : ************");
//		System.out.println(tree);
		
		if (tree.node.isAND()){
			Vector<WishTree> orChildren = tree.getChildren(false, true, false);
			if (!orChildren.isEmpty()){ // a AND node that has at least one OR child
				
				WishTree currOrChild = orChildren.firstElement();   
				Vector<WishTree> newChildren = new Vector<WishTree>();
				for (WishTree grandChild : currOrChild.children){
					
					WishTree newChild = new WishTree();
					newChild.node = new Wish(true, false, false, -1);
					newChild.addChild(grandChild);
					
					tree.children.remove(currOrChild);
					for (WishTree currSiblings : tree.children){
						newChild.addChild(currSiblings);
						newChild.parent = tree;
					}			
					
					newChildren.add(newChild);
				}
				
				tree.node.setToOr();
				tree.children = newChildren;
//				return tree;
			}
		} 
		return tree;
	}
	
	public WishTree developpTree(WishTree tree){ 
		if (tree.children != null){
			
//			System.out.println("*****************before developp node************");
//			System.out.println(tree);
			
			tree = developpNode(tree);
			
//			System.out.println("*****************after developp before simplify************");
//			System.out.println(tree);
			
			simplifyTree(tree);
			
//			System.out.println("*****************after developp after simplify************");
//			System.out.println(tree);
			
			
			for (WishTree child : tree.children){
				
				developpTree(child);
				
			
			}
//			developpNode(tree);
		}
		
		simplifyTree(tree);
		return tree;
	}
	
	public boolean isDevelopped(WishTree tree){
		
		
		
		return true;
	}

	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public WishTree normaliseTerminalBranches(WishTree developpedTree){ // transforms nr nodes in and nodes with the number. Ex : 1  -> (AND;1)
		int i = 0;
		Vector<WishTree> normalisedChildren = new Vector<WishTree>();
		for (WishTree child : developpedTree.getChildren()){
			if (child.getNode().isNR()){
				WishTree normChild = new WishTree(true, false);
				normChild.addChild(child);
				normalisedChildren.add(normChild);
			} else if (child.getNode().isAND()){
				normalisedChildren.add(child);
			} else {
				System.out.println("you should not see that, normaliseTerminalBranche called with wrong arguments ");
			}
		}
		developpedTree.resetChildren();
		developpedTree.addChildren(normalisedChildren);
		
		return developpedTree;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------	
	
	public int branchContains(WishTree branchA, WishTree branchB){ // if A in B returns 1; if B in A returns 2, if A=B returns 0, otherwise -1
		
		WishTree tempA = new WishTree();
		WishTree tempB = new WishTree();
		boolean swiched = false;
		if (branchA.getChildren().size()<branchB.getChildren().size()){ // this avoids terminating after not finding an element of the bigger set in the sebset.
			tempA = branchA.Clone(branchA);
			tempB = branchB.Clone(branchB);
		} else {
			tempB = branchA.Clone(branchA);
			tempA = branchB.Clone(branchB);
			swiched = true; // this is a bit dirty TODO better if possible
		}
//		WishTree tempA = branchA.Clone(branchA);
//		WishTree tempB = branchB.Clone(branchB);
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
	
	public WishTree removeRedundancy(WishTree normalisedTree){
		Vector<WishTree> newChildren = new Vector<WishTree>();
		Vector<WishTree> toRemove = new Vector<WishTree>();
		
		for (WishTree childA : normalisedTree.getChildren()){
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
		
		normalisedTree.resetChildren();
		normalisedTree.addChildren(newChildren);
		return normalisedTree;
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
//		System.out.println(res);
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

	public void addChild(WishTree tree) {
		this.children.add(tree);
		tree.parent = this;
		
	}

	public void addChildren(List<WishTree> children2) {
		this.children.addAll(children2);
		for (WishTree child : children2){
			child.parent = this;
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
		this.parent = parent;
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
	
}

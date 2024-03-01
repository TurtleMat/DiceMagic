import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
//import java.awt.event.WindowEvent;    
//import java.awt.event.WindowListener;    

public class Gui extends JFrame{
	JFrame mainFrame = new JFrame("Dice Magic");  
//	ActionListener popupMenuActionListener;
	
	JLabel nrDiceLabel = new JLabel("number of dice");
	JTextField nrDiceTextField = new JTextField();

	JLabel nrFacesLabel = new JLabel("number of faces");
	JTextField nrFacesTextfield = new JTextField();

	JLabel importStringLabel = new JLabel("import following string : ");
	JTextField importStringTextField = new JTextField();
	JButton imporsStringButton = new JButton("import");

	WishTree currentTree;
	JLabel currentTreeLabel = new JLabel("current Wish :");
	
//	JTextArea currentTreeDisplay = new JTextArea();
//	DefaultMutableTreeNode currentTreeForDisplay = new DefaultMutableTreeNode();
	
	JTree displayTree = new JTree();
	JScrollPane scrollPanel =new JScrollPane();
	JPopupMenu popup = new JPopupMenu();
	
	Wish currentNode;

//	
	JButton clearTree = new JButton("clear");
	JButton calculate = new JButton("Calculate");
	JButton close = new JButton("Close");

	JLabel resultLabel = new JLabel("The probability is : ");
	JLabel result = new JLabel("here is the output text");
//	Label diverseOutput = new Label();

	
	
	JPopupMenu nodeEditMenu;
	JMenu addChildMenu, changeToMenu, removeChildMenu;
	JMenuItem remove;
//	JMenuItem menuItem;
	ActionListener popupMenuAddActionListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent arg0) {
			addChild(arg0.getActionCommand().toString());
			
		}
	};
	ActionListener popupMenuChangeActionListener= new ActionListener() {
		
		public void actionPerformed(ActionEvent arg0) {			
			changeNodeTo(arg0.getActionCommand().toString());
			
		}
	};


	
	// ------------------------------------------------------------------------------------------------------------------------------------

	
	public Gui(){
		this.mainFrame.setSize(1200, 2000);
		this.mainFrame.setLayout(null);
		
		
		setBounds();
		addToMainframe();
		initialise();
		createContextMenu();

		setupActionListeners();
		
		
		scrollPanel.setViewportView(this.displayTree);
		this.mainFrame.setVisible(true); // this seems to break jalbel result bounds.
		
		
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		
		Gui Gui1 = new Gui();
//		Gui1.result.setText("test");
		
	}  
	
	// ------------------------------------------------------------------------------------------------------------------------------------


	
	private void createContextMenu(){

				
//		nodeEditMenu = new JPopupMenu();
		nodeEditMenu = this.popup;
		this.displayTree.setComponentPopupMenu(popup); // TODO 

		
		addChildMenu = new JMenu("Add a child:");
		nodeEditMenu.add(addChildMenu);
		
		remove = new JMenuItem("remove");
		nodeEditMenu.add(remove);
		
		changeToMenu = new JMenu("change to :");
		nodeEditMenu.add(changeToMenu);
		
		int nrFaces = Integer.parseInt(this.nrFacesTextfield.getText());//todo user imput handling
		
		JMenuItem[] possibleChildrenToAdd = new JMenuItem[nrFaces+5];
		JMenuItem[] possibleChildrenToChange = new JMenuItem[nrFaces+5];
		possibleChildrenToAdd[0] = new JMenuItem("AND");
		possibleChildrenToAdd[1] = new JMenuItem("OR");
		possibleChildrenToChange[0] = new JMenuItem("AND");
		possibleChildrenToChange[1] = new JMenuItem("OR");
		for (int i = 0; i<nrFaces;i++){
			possibleChildrenToAdd[i+2] = new JMenuItem(""+(i+1));
			possibleChildrenToChange[i+2] =new JMenuItem(""+(i+1));
		}
		
		int i = 0;
		while (possibleChildrenToAdd[i] != null){
			possibleChildrenToAdd[i].addActionListener(popupMenuAddActionListener);
			possibleChildrenToChange[i].addActionListener(popupMenuChangeActionListener);
			i++;
		}
		
		
		for (int k = 0; k<nrFaces+2;k++){
			addChildMenu.add(possibleChildrenToAdd[k]);
			changeToMenu.add(possibleChildrenToChange[k]);
		}
		
	}

	private void initialise() {
		this.nrDiceTextField.setText("6");
		this.nrFacesTextfield.setText("6");
		this.importStringTextField.setText("(OR;(AND;1;2;3;4;5;6))");
		this.importUserString(this.importStringTextField.getText());
		
		
	}

	private void addToMainframe() {
		this.mainFrame.add(close);
		
		this.mainFrame.add(nrDiceLabel);
		this.mainFrame.add(nrDiceTextField);
		this.mainFrame.add(nrFacesLabel);
		this.mainFrame.add(nrFacesTextfield);
		
		this.mainFrame.add(imporsStringButton);
		this.mainFrame.add(importStringTextField);
		this.mainFrame.add(importStringLabel);
		
		this.mainFrame.add(scrollPanel);
		this.mainFrame.add(currentTreeLabel);
//		this.mainFrame.add(lineDown);
//		this.mainFrame.add(lineUp);
//		this.mainFrame.add(modify);
		this.mainFrame.add(clearTree);
//		this.mainFrame.add(toAND);
//		this.mainFrame.add(toOR);
//		this.mainFrame.add(toNr);
//		this.mainFrame.add(addChild);
//		this.mainFrame.add(newNumberTextField);
		
//		this.toAND.setVisible(false);
		
		this.mainFrame.add(calculate);
		this.mainFrame.add(resultLabel);
		this.mainFrame.add(result);
		
	}

	private void setBounds(){
		
		int shiftDown = 50;
		int labelWidth = 150;
		int labelHeight = 20;
		int margin = 5;
		
		this.nrDiceLabel.setBounds(margin, 
				margin+shiftDown, 
				labelWidth, 
				labelHeight);		
		this.nrDiceTextField.setBounds(margin + labelWidth + margin,
				margin+shiftDown, 
				labelWidth, 
				labelHeight);

		this.nrFacesLabel.setBounds(3*margin + 2*labelWidth,
				margin+shiftDown,
				labelWidth, 
				labelHeight);
		this.nrFacesTextfield.setBounds(4*margin + 3*labelWidth,
				margin+shiftDown, 
				labelWidth, 
				labelHeight);

		this.importStringLabel.setBounds(margin,
				margin*2 +labelHeight+shiftDown, 
				labelWidth, 
				labelHeight);
		this.importStringTextField.setBounds(margin*2+labelWidth,
				margin*2 +labelHeight+shiftDown, 
				labelWidth*3, 
				labelHeight);
		this.imporsStringButton.setBounds(margin*3 + labelWidth*4, 
				margin*2 +labelHeight+shiftDown, 
				labelWidth, 
				labelHeight); 


		this.currentTreeLabel.setBounds(margin, 
				labelHeight*2+margin*3+shiftDown, 
				labelWidth, 
				labelHeight);
//		this.lineDown.setBounds(margin*2+labelWidth,
//				labelHeight*2+margin*3+shiftDown, 
//				labelWidth, 
//				labelHeight); 
//		this.lineUp.setBounds(margin*3+labelWidth*2,
//				labelHeight*2+margin*3+shiftDown,
//				labelWidth, 
//				labelHeight);
//		this.modify.setBounds(margin*4+labelWidth*3, 
//				labelHeight*2+margin*3+shiftDown, 
//				labelWidth, 
//				labelHeight);
//		this.clearTree.setBounds(margin*5+labelWidth*4, 
//				labelHeight*2+margin*3+shiftDown,  
//				labelWidth,
//				labelHeight);
		
		this.clearTree.setBounds(margin*2+labelWidth,
				labelHeight*2+margin*3+shiftDown, 
				labelWidth, 
				labelHeight);

		this.scrollPanel.setBounds(margin, 
				labelHeight*3+margin*4+shiftDown, 
				labelWidth*3, 
				600);
//		this.toOR.setBounds(margin*2+labelWidth*3, 
//				labelHeight*3+margin*4+shiftDown, 
//				labelWidth, 
//				labelHeight);
//		this.toAND.setBounds(margin*2+labelWidth*3, 
//				labelHeight*4+margin*5+shiftDown, 
//				labelWidth, 
//				labelHeight);
//		this.toNr.setBounds(margin*2+labelWidth*3, 
//				labelHeight*5+margin*6+shiftDown, 
//				labelWidth, 
//				labelHeight);
//		this.addChild.setBounds(margin*2+labelWidth*3, 
//				labelHeight*6+margin*7+shiftDown, 
//				labelWidth, 
//				labelHeight);
//		this.newNumberTextField.setBounds(margin*3+labelWidth*4, 
//				labelHeight*5+margin*6+shiftDown, 
//				labelWidth, 
//				labelHeight);
		
		this.calculate.setBounds(margin, 
				600+labelHeight*3+margin*5+shiftDown, 
				labelWidth, 
				labelHeight); //TODO actionListeners
		this.resultLabel.setBounds(margin,
				600+labelHeight*6+shiftDown,
				labelWidth,
				labelHeight);
		this.result.setBounds(margin*2+labelWidth,
				600+labelHeight*6+shiftDown,
				labelWidth*3,
				labelHeight);
		this.close.setBounds(10, 
				20,
				labelWidth, 
				labelHeight);
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private void setupActionListeners() {
		
		

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.dispose();
			}
		});
		
		imporsStringButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				importUserString(importStringTextField.getText());
				
			}
		});
//		
//		toAND.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				changeNodeToAND();
//			}
//
//			
//		});
//		
//		toOR.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				changeNodeToOR();
//			}
//			
//		});
//		
//		toNr.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				changeNodeToNr(newNumberTextField.getText());
//			}
//			
//		});
//		
//		addChild.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				addChild(newNumberTextField.getText());
//			}
//
//			
//		});
//		

		clearTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearUserTree();
			}
		});

		calculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				calculateUserProba();
			}

		});
		
//		displayTree.addMouseListener(new MouseAdapter() {
//	        @Override
//	        public void mousePressed(MouseEvent mouseEvent)
//	        {
////	            handleContextMenu(mouseEvent);
//	        }
//	        @Override
//	        public void mouseReleased(MouseEvent mouseEvent)
//	        {
////	            handleContextMenu(mouseEvent);
//	        }
//		});
		
		
		addChildMenu.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				result.setText(arg0.toString());
				
			}
		});
		
		remove.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
				if (node != null){
					node.removeFromParent();
					redrawTree();
				}else{
					result.setText("please select a node before right clicking");
				}
				
				
			}
		});	
		
	}

//
//
//	protected void handleContextMenu(MouseEvent mouseEvent) {
//
//		if (displayTree!= null){
//			if (mouseEvent.getButton() == 3 ){
//				this.result.setText("this was a right clic I think");
//				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
//				if (selectedNode != null){
//					popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
//				}
//			}
////			if (mouseEvent.getButton() == 1 ){
////				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
////				this.result.setText(mouseEvent.getComponent().getName());
////				
////			}
//		}
//	}
//	

	
	// ------------------------------------------------------------------------------------------------------------------------------------

	

	// ------------------------------------------------------------------------------------------------------------------------------------

	private WishTree importDisplayedTree(DefaultMutableTreeNode node ){  //(DefaultMutableTreeNode node) {
		
//		KIDZ, DON'T COMPARE STRINGS WITH ==    !!!!!!!
//		
		String nodeString =  node.getUserObject().toString();

		WishTree res = new WishTree(Wish.wishFromString(nodeString));
		
		
		int nrChildren = node.getChildCount();
		int i = 0;
		while (i<nrChildren){
			System.out.println("child at i : " + node.getChildAt(i));
			res.addChild(importDisplayedTree((DefaultMutableTreeNode) node.getChildAt(i)));
			i++;
		}
		
		System.out.println("res is : " + res.toString());
		
		return res;
	}
	
	protected void importUserString(String string) {
		try {
//			this.currentTree = WishTree.stringToTree(string); //string -> tree -> displaytree. not efficient. currentTree only calculated on calculate
			
//			this.displayTree = new JTree(this.currentTreeToDisplay (currentTree));
			WishTree transitionTree = WishTree.importAndPrepareTree(string, true);
			DefaultMutableTreeNode root = wishTreeToDisplay(transitionTree);
			
//			DefaultMutableTreeNode root = stringToDisplayTreeNoLink(string);
			this.displayTree = new JTree(root) ;
			
			this.displayTree.setEditable(true);
			scrollPanel.setViewportView(this.displayTree);
			this.expandDisplayTree(displayTree);
			this.displayTree.setComponentPopupMenu(popup);
			
			
//			updateDisplayTreeListener(); //TODO uncomment if sed

			
//			scrollPanel.setViewportView(this.currentTree);
			
		} catch (Exception e){
			this.currentTree = null;
//			this.currentTreeForDisplay = null;
			this.result.setText("Input string invalid!");
		}

	}
	
	private DefaultMutableTreeNode wishTreeToDisplay(WishTree tree) {
		DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(tree.getNode().toString());
		
		if (tree.getChildren() != null){
			for (WishTree child : tree.getChildren()){
				currentNode.add(wishTreeToDisplay(child));
				
			}
		}
		
		return currentNode;
	}
	// ------------------------------------------------------------------------------------------------------------------------------------


	
	private void changeNodeTo(String text){
		if (isValidNode(text)){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
			
			if (node.getChildCount() != 0 && !text.equalsIgnoreCase("AND") && !text.equalsIgnoreCase("or")){

				node.removeAllChildren();
			}
			node.setUserObject(text);
			
			redrawTree();
		}
	}

	private void addChild(String target) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
		if (node != null){
			if (Gui.isValidNode(target)){
				if (( (String) node.getUserObject()).equalsIgnoreCase("OR") || (((String) node.getUserObject()).equalsIgnoreCase("AND") )) { 
					node.add(new DefaultMutableTreeNode(target));
					redrawTree();
				}
			}
		}else{
			result.setText("please select a node before right clicking");
		}
		
		
		
		
	}
	
	protected void clearUserTree() {
		this.currentTree= null;
		this.displayTree = null;
		scrollPanel.setViewportView(displayTree);
	}
	
	protected void modifyLine() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
		
	}

	protected void lineDown() {

	}

	protected void calculateUserProba() {
		
		if (this.displayTree == null){
			this.result.setText("there is currently no tree selected");
			return;
		}

		currentTree = importDisplayedTree((DefaultMutableTreeNode) this.displayTree.getModel().getRoot());
//		currentTree = importDisplayedTree( this.displayTree);
		
		System.out.println(currentTree);
		
		int nrDice;
		try{
			nrDice = Integer.parseInt(this.nrDiceTextField.getText());
		}catch (Exception e){
			this.result.setText("number of dice must be an integer");
			return;
		}
		if(nrDice >12){
			this.result.setText("number of dice must be smaller than 12");
			return;
		}
		int nrFaces;
		try{
			nrFaces = Integer.parseInt(this.nrFacesTextfield.getText());
		}catch (Exception e){
			this.result.setText("number of faces must be an integer");
			return;
		}
		if (currentTree==null) {
			this.result.setText("there is currently no wish");
			return;
		}
		
		
		
		WishTree tree = WishTree.prepareTree(currentTree, true);
		if (currentTree != null){

			Throw currentThrow = new Throw(tree, nrDice, nrFaces); // TODO seems to be a pb if a goal contains a number  bigger than nrfaces
			double res = (double) currentThrow.probaCompleteRec(tree) / Math.pow(nrFaces, nrDice);
			this.result.setText(""+res);
		}

		
		currentTree = null;

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------


	private static boolean isValidNode(String target){
		boolean res;
		if (target.equals("AND") || target.equals("OR")){
			return true;
		}else {
			try {
				int i = Integer.parseInt(target);
				return true;
			} catch (final NumberFormatException e){
				return false;
			}
		}
	}
		
	private void redrawTree() {
		DefaultTreeModel model = (DefaultTreeModel) displayTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		model.reload(root);
		expandDisplayTree(displayTree);
	}
	
	private void expandDisplayTree(JTree tree){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
		expandDisplayNode(tree, node);
	}

	private void expandDisplayNode(JTree tree, DefaultMutableTreeNode node) {
		ArrayList<DefaultMutableTreeNode> children = Collections.list(node.children());
		for (DefaultMutableTreeNode child : children){
			expandDisplayNode(tree, child);
		}
		TreePath path = new TreePath(node.getPath());
		tree.expandPath(path);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	


}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
//private void updateDisplayTreeListener(){
//this.displayTree.addMouseListener(new MouseAdapter() {
//    @Override
//    public void mousePressed(MouseEvent mouseEvent)
//    {
//        handleContextMenu(mouseEvent);
//    }
//    @Override
//    public void mouseReleased(MouseEvent mouseEvent)
//    {
////        handleContextMenu(mouseEvent);
//    }
//});
//}


//
//
//private void changeNodeToAND() {
//	DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
//	if (node != null){
//		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//		int index = parent.getIndex(node);
////		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new Wish(true, false, false, -1));
//		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("AND");
//		parent.insert(newNode, index);
//		node.removeFromParent();			
//	
//	int nrChildren = node.getChildCount();
//	if (nrChildren == 0){
////			newNode.add(node);
//			newNode.add(new DefaultMutableTreeNode("1"));
//			newNode.add(new DefaultMutableTreeNode("1"));
//		}else{
//			int i = 0;
//			while (i<nrChildren){
////			newNode.add((DefaultMutableTreeNode) node.getChildAt(i));
//				newNode.add((DefaultMutableTreeNode) node.getFirstChild());
//				i++;
//			}
//		}	
//
//	}
//	
//	redrawTree();
//	
//}
//
//private void changeNodeToOR() {
//	DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
//	if (node != null){
//		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//		int index = parent.getIndex(node);
//		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("OR");
//		parent.insert(newNode, index);
//		node.removeFromParent();			
//	
//	int nrChildren = node.getChildCount();
//	if (nrChildren == 0){
////			newNode.add(node);
////			newNode.add(new DefaultMutableTreeNode(new Wish(false, false, true, 1)));
////			newNode.add(new DefaultMutableTreeNode(new Wish(false, false, true, 1)));
//			newNode.add(new DefaultMutableTreeNode("1"));
//			newNode.add(new DefaultMutableTreeNode("1"));
//		}else{
//			int i = 0;
//			while (i<nrChildren){
////			newNode.add((DefaultMutableTreeNode) node.getChildAt(i));
//				newNode.add((DefaultMutableTreeNode) node.getFirstChild());
//				i++;
//			}
//		}	
//
//	}
//	
//	redrawTree();
//	
//}
//
//protected void changeNodeToNr(String text) {
//	
//	if (isValidNode(text)){
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
//		if (node != null){
//			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//			int index = parent.getIndex(node);
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(text);
//			parent.insert(newNode, index);
//			node.removeFromParent();			
//		
//			int nrChildren = node.getChildCount();
//			if (nrChildren != 0){
//				node.removeAllChildren();
//			}
//		}
//
//		redrawTree();
//	}
//	
//	
//}

//JButton lineUp = new JButton("UP");
//JButton lineDown = new JButton("DOWN");
//JButton modify = new JButton("modify");
//
//JButton toOR = new JButton("Change to OR");
//JButton toAND = new JButton("Change to AND");
//JButton toNr = new JButton("Change to following number");
//JTextField newNumberTextField = new JTextField();
//JButton addChild = new JButton("Add a child");


//	private WishTree displayTreeToCurrent(JTree tree) {
//		TreeModel model = tree.getModel();
//		System.out.println(model.toString());
//		
////		Wish currentNode = new Wish(node)
////		if (tree != null)
////		
////		DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(tree.getNode().toString());
////		
////		if (tree.getChildren() != null){
////			for (WishTree child : tree.getChildren()){
////				currentNode.add(currentTreeToDisplay(child));
////				
////			}
////		}
////		
//		return null;
//	}
//
//	private DefaultMutableTreeNode stringToDisplayTree(String userString) {
//		
//		int i = 0;
//		if (!(userString.charAt(i) == '(')) { // if it doesn't start with a
//													// parenthesis, it must be a
//													// number
//			int res;
//			res = Integer.parseInt(userString);
//			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(new Wish(false, false, true, res));
//			return leaf;//
//
//		} else {
//			i++;
//			int j = userString.indexOf(";", i);
//			String[] childStrings = new String[userString.length()]; // length  overkill  but  wathever for now
//			String operator = userString.substring(i, j); // at this point, operator should be AND  or OR. substring should be exclusive for the max bound
//			
//
//			i = j + 1;
//			int childNr = 0;
//			int parenthesis = 1;
//
//			while (parenthesis > 0) {
//				char c = userString.charAt(i);
//				String s = String.valueOf(c);
//				if (c == '(') {
//					parenthesis++;
//					if (parenthesis == 2) {
//						childStrings[childNr] = s;
//					} else {
//						childStrings[childNr] += s;
//					}
//				} else if (c == ')') {
//					parenthesis--;
//					if (!(parenthesis == 0)) {
//						childStrings[childNr] += s;
//					}
//
//				} else if (c == ';' && parenthesis == 1) {
//					childNr++;
//				} else {
//					if (childStrings[childNr] == null) {
//						childStrings[childNr] = s;
//					} else {
//						childStrings[childNr] += s;
//					}
//
//				}
//
//				i++;
//			} // at the end of this while, every child should be stored as a
//				// valid string in the childStrinds vector
//
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
//			if (operator.equals("AND")) {
////				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new Wish(true, false, false, -1));
//				newNode.setUserObject(new Wish(true, false, false, -1));
//			}
//			if (operator.equals("OR")) {
////				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new Wish(false, true, false, -1));
//				newNode.setUserObject(new Wish(false, true, false, -1));
//			}
//			for (String childString : childStrings) {
//				if (childString != null) {
//
//					newNode.add(stringToDisplayTree(childString));
//				}
//			}
//			return newNode;
//		}
//	}
//	
//	private DefaultMutableTreeNode stringToDisplayTreeNoLink(String userString) {
//		
//		int i = 0;
//		if (!(userString.charAt(i) == '(')) { // if it doesn't start with a
//													// parenthesis, it must be a
//													// number
////			int res;
////			res = Integer.parseInt(userString);
//			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(userString);
//			return leaf;//
//
//		} else {
//			i++;
//			int j = userString.indexOf(";", i);
//			String[] childStrings = new String[userString.length()]; // length  overkill  but  wathever for now
//			String operator = userString.substring(i, j); // at this point, operator should be AND  or OR. substring should be exclusive for the max bound
//			
//
//			i = j + 1;
//			int childNr = 0;
//			int parenthesis = 1;
//
//			while (parenthesis > 0) {
//				char c = userString.charAt(i);
//				String s = String.valueOf(c);
//				if (c == '(') {
//					parenthesis++;
//					if (parenthesis == 2) {
//						childStrings[childNr] = s;
//					} else {
//						childStrings[childNr] += s;
//					}
//				} else if (c == ')') {
//					parenthesis--;
//					if (!(parenthesis == 0)) {
//						childStrings[childNr] += s;
//					}
//
//				} else if (c == ';' && parenthesis == 1) {
//					childNr++;
//				} else {
//					if (childStrings[childNr] == null) {
//						childStrings[childNr] = s;
//					} else {
//						childStrings[childNr] += s;
//					}
//
//				}
//
//				i++;
//			} // at the end of this while, every child should be stored as a
//				// valid string in the childStrinds vector
//
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(operator);
////			if (operator.equals("AND")) {
//////				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new Wish(true, false, false, -1));
//////				newNode.setUserObject("AND");
////			}
////			if (operator.equals("OR")) {
//////				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new Wish(false, true, false, -1));
//////				newNode.setUserObject("OR");
////			}
//			for (String childString : childStrings) {
//				if (childString != null) {
//
//					newNode.add(stringToDisplayTreeNoLink(childString));
//				}
//			}
//			return newNode;
//		}
//	}
//

	


//protected void lineUp() {
//	if (this.currentLine >-1 ){//&& this.currentTreeDisplay.getText().split("[\n|\r]").length < currentLine
//		String res = "";
//		Scanner scanner = new Scanner(this.currentTreeDisplay.getText());
//		int i = 0;
//		while (scanner.hasNextLine()) {
//			String line = scanner.nextLine();
//			if (i==currentLine +1 ){
//				String newLine = "[" + line + "]";
//				res += newLine + "\n";
//			} else if (i == currentLine && scanner.hasNextLine()){
//				String newline = line.substring(1, line.length()); // bounds prbly wrong, should remove the []
//				res += newline+ "\n";
//
//			}else {
//				res += line+ "\n";
//			}
//			this.currentLine++;
//		}
//		scanner.close();
//		this.currentTreeDisplay.setText(res);
//	}
//}


//protected void lineDown() {
//		if (this.currentLine >0){
//			this.currentLine--;
//			String res = "";
//			Scanner scanner = new Scanner(this.currentTreeDisplay.getText());
//			int i = 0;
//			while (scanner.hasNextLine()) {
//			  String line = scanner.nextLine();
//			  if (i==currentLine){
//				  String newLine = "[" + line + "]";
//				  res += newLine + "\n";
//			  } else if (i == currentLine+1){
//				  String newline = line.substring(1, line.length()); // bounds prbly wrong, should remove the []
//				  res += newline+ "\n";
//				  
//			  }else {
//				  res += line+ "\n";
//			  }
//			}
//			scanner.close();
//			this.currentTreeDisplay.setText(res);
//		}
//		
//	}





//private void changeNodeTo(String target) {
//	
//	Wish wish = Wish.wishFromString(target);
//	
////	if (target=="AND" || target == "OR" || i>0){ //doesn't check if target < nrFaces
//	if (wish != null){
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
//		if (node != null){
//			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//			int index = parent.getIndex(node);
//			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(wish);
//			parent.insert(newNode, index);
//			newNode.setParent(parent);
//			
//			node.removeFromParent();
//			int nrChildren = node.getChildCount();
//			if (nrChildren == 0){
////				newNode.add(node);
//				newNode.add(new DefaultMutableTreeNode(new Wish(false, false, true, 1)));
//				newNode.add(new DefaultMutableTreeNode(new Wish(false, false, true, 1)));
//			}else{
//				int i = 0;
//				while (i<nrChildren){
////					newNode.add((DefaultMutableTreeNode) node.getChildAt(i));
//					newNode.add((DefaultMutableTreeNode) node.getFirstChild());
//					i++;
//				}
//			}
//			
//			
//			
//					
//			redrawTree();
//		}
//	}
//	
//	
//	
//}

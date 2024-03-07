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

public class Gui extends JFrame{
	
	final int MAX_NR_FACES = 50;
	final int MAX_NR_DICE = 12;
	
	JFrame mainFrame = new JFrame("Dice Magic");  

	JLabel nrDiceUserText = new JLabel("number of dice");
	JLabel nrDiceLabel = new JLabel();
	JButton increaseDice = new JButton("+1");
	JButton decreaseDice = new JButton("-1");

	JLabel nrFacesUserText = new JLabel("number of faces");
	JLabel nrFacesLabel = new JLabel();
	JButton increaseFaces = new JButton("+1");
	JButton decreaseFaces = new JButton("-1");

	JLabel importStringLabel = new JLabel("import following string : ");
	JTextField importStringTextField = new JTextField();
	JButton imporsStringButton = new JButton("import");

	WishTree currentTree;
	JLabel currentTreeLabel = new JLabel("current Wish :");
		
	JTree displayTree = new JTree();
	JScrollPane scrollPanel =new JScrollPane();
	JPopupMenu popup = new JPopupMenu();
	
	JButton clearTree = new JButton("clear");
	JButton calculate = new JButton("Calculate");
	JButton close = new JButton("Close");

	JLabel resultLabel = new JLabel("The probability is : ");
	JLabel result = new JLabel("here is the output text");

	JPopupMenu nodeEditMenu;
	JMenu addChildMenu, changeToMenu;
	JMenuItem remove;
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
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		scrollPanel.setViewportView(this.displayTree);
		this.mainFrame.setVisible(true); // this seems to break jalbel result bounds.
	
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		
		Gui Gui1 = new Gui();
		
		//This was in constructor. test if it breaks.
		Gui1.setBounds(); 
		Gui1.addToMainframe();
		Gui1.initialise();
		Gui1.createContextMenu();
		Gui1.setupActionListeners();
		
	}  
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private void setupClicableMenuItems(String label, JMenu addMenu, JMenu changeMenu){
		JMenuItem tmp;
		
		tmp = new JMenuItem(label);
		addMenu.add(tmp);
		tmp.addActionListener(popupMenuAddActionListener);
		
		tmp = new JMenuItem(label);
		changeMenu.add(tmp);
		tmp.addActionListener(popupMenuChangeActionListener);		
	}
	
	private void createContextMenu(){

		nodeEditMenu = this.popup;
		this.displayTree.setComponentPopupMenu(popup);
		
		remove = new JMenuItem("remove");
		nodeEditMenu.add(remove);
		
		addChildMenu = new JMenu("Add a child:");
		nodeEditMenu.add(addChildMenu);

		changeToMenu = new JMenu("change to :");
		nodeEditMenu.add(changeToMenu);
		
//		int nrFaces = Integer.parseInt(this.nrFacesLabel.getText());
		int nrFaces = this.getNrFaces();

		setupClicableMenuItems("AND",addChildMenu, changeToMenu);
		setupClicableMenuItems("OR",addChildMenu, changeToMenu);
		
		for (int i = 0; i<nrFaces;i++){
			setupClicableMenuItems(""+(i+1),addChildMenu, changeToMenu);
		}
		
		int l = 0;
		while (l<WishTree.getAvailablePresets().length){
			String currPreset = WishTree.getAvailablePresets()[l];
			
			if (currPreset.equalsIgnoreCase("ntupel")){
				
				JMenu ntupelAdd = new JMenu("ntupel");
				JMenu ntupelChange = new JMenu("ntupel");
				addChildMenu.add(ntupelAdd);
				changeToMenu.add(ntupelChange);				
				
				int i = 3;
				while (i<= this.getNrDice()){
					setupClicableMenuItems("ntupel"+(i), ntupelAdd,ntupelChange );
					i++;
				}
			} else if (currPreset.equalsIgnoreCase("allthesame")){
				
				JMenu allTheSameAdd = new JMenu("allthesame");
				JMenu allTheSameChange = new JMenu("allthesame");
				addChildMenu.add(allTheSameAdd);
				changeToMenu.add(allTheSameChange);	
				
				int i=1;
				while (i<= this.getNrFaces()){
					setupClicableMenuItems("allthesame"+i, allTheSameAdd, allTheSameChange);
					i++;
				}
				
				
				
			} else {
				setupClicableMenuItems(currPreset, addChildMenu, changeToMenu);
			}
			l++;
		}
	}

	private void initialise() {
		this.nrDiceLabel.setText("6");
		this.nrFacesLabel.setText("6");
		this.importStringTextField.setText("(OR;(AND;1;2;3;4;5;6))");
		this.importUserString(this.importStringTextField.getText());
		
		
	}

	private void addToMainframe() {
		this.mainFrame.add(close);
		
		this.mainFrame.add(nrDiceUserText);
		this.mainFrame.add(nrDiceLabel);
		this.mainFrame.add(increaseDice);
		this.mainFrame.add(decreaseDice);
		
		this.mainFrame.add(nrFacesUserText);
		this.mainFrame.add(nrFacesLabel);
		this.mainFrame.add(increaseFaces);
		this.mainFrame.add(decreaseFaces);
		
		this.mainFrame.add(imporsStringButton);
		this.mainFrame.add(importStringTextField);
		this.mainFrame.add(importStringLabel);
		
		this.mainFrame.add(scrollPanel);
		this.mainFrame.add(currentTreeLabel);

		this.mainFrame.add(clearTree);
		
		this.mainFrame.add(calculate);
		this.mainFrame.add(resultLabel);
		this.mainFrame.add(result);
		
	}

	private void setBounds(){
		
		int shiftDown = 50;
		int labelWidth = 150;
		int labelHeight = 20;
		int margin = 5;
		int smallbutton = 60;
		
//		this.close.setBounds(10, 
//				20,
//				labelWidth, 
//				labelHeight);
		
		this.nrDiceUserText.setBounds(margin, 
				margin+20, 
				labelWidth, 
				labelHeight);		
		this.decreaseDice.setBounds(margin + labelWidth + margin, 
				margin+20, 
				smallbutton, 
				labelHeight);
		this.nrDiceLabel.setBounds(3*margin + labelWidth + smallbutton,
				margin+20, 
				labelWidth, 
				labelHeight);
		this.increaseDice.setBounds(4*margin + 2*labelWidth + smallbutton, 
				margin+20, 
				smallbutton, 
				labelHeight);
		

		this.nrFacesUserText.setBounds(margin ,
				margin+shiftDown,
				labelWidth, 
				labelHeight);
		this.decreaseFaces.setBounds(margin + labelWidth + margin, 
				margin+shiftDown, 
				smallbutton, 
				labelHeight);
		this.nrFacesLabel.setBounds(3*margin + labelWidth + smallbutton,
				margin+shiftDown, 
				labelWidth, 
				labelHeight);
		this.increaseFaces.setBounds(4*margin + 2*labelWidth + smallbutton, 
				margin+shiftDown, 
				smallbutton, 
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

		
		this.clearTree.setBounds(margin*2+labelWidth,
				labelHeight*2+margin*3+shiftDown, 
				labelWidth, 
				labelHeight);

		this.scrollPanel.setBounds(margin, 
				labelHeight*3+margin*4+shiftDown, 
				labelWidth*3, 
				600);

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

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private void setupActionListeners() {
		
		increaseFaces.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNrFaces(getNrFaces()+1);
			}

		});
		
		decreaseFaces.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNrFaces(getNrFaces()-1);
				
			}

		});
		
		
		increaseDice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNrDice(getNrDice()+1);
				
			}

		});
		
		decreaseDice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNrDice(getNrDice()-1);
				
			}

		});
		

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
		

		
		addChildMenu.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				result.setText(arg0.toString());
				
			}
		});
		
		remove.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				removeNode();
				
			}
		});	
		
		
		
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------

	private WishTree importDisplayedTree(DefaultMutableTreeNode node ){  //(DefaultMutableTreeNode node) {
		
//		KIDZ, DON'T COMPARE STRINGS WITH ==    !!!!!!!
//		
		String nodeString =  node.getUserObject().toString();
		WishTree res;
		boolean isAPreset = false;
		
		for (String preset : WishTree.getAvailablePresets()){
			if (preset.equalsIgnoreCase(nodeString)){
				isAPreset = true;
			}
		}
		if (nodeString.contains("ntupel")||nodeString.contains("allthesame")){
			isAPreset=true;
		}
		if (isAPreset){
			res = WishTree.presetStringToTree(nodeString, this.getNrFaces(), this.getNrDice());
		} else {
			res = new WishTree(nodeString);
		}
		
		
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
			int nrFaces = this.getNrFaces();
			int nrDice = this.getNrDice();
			
			WishTree transitionTree = WishTree.importAndPrepareTree(string, nrFaces, nrDice, true);
			DefaultMutableTreeNode root = wishTreeToDisplay(transitionTree);
			
			this.displayTree = new JTree(root) ;
			
			this.displayTree.setEditable(true);
			scrollPanel.setViewportView(this.displayTree);
			this.expandDisplayTree(displayTree);
			this.displayTree.setComponentPopupMenu(popup);
			TreePath rootPath = new TreePath(root.getPath());
			this.displayTree.setSelectionPath(rootPath);
			
			
//			updateDisplayTreeListener(); //TODO uncomment if sed
//			scrollPanel.setViewportView(this.currentTree);
			
		} catch (Exception e){
			this.currentTree = null;
			this.result.setText("Input string invalid!");
		}

	}
	
	private DefaultMutableTreeNode wishTreeToDisplay(WishTree tree) {
		DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(tree.nodeValueToString());
		
		if (tree.getChildren() != null){
			for (WishTree child : tree.getChildren()){
				currentNode.add(wishTreeToDisplay(child));
				
			}
		}
		
		return currentNode;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private void removeNode(){
		this.result.setText("here is the output text");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
		if (node != null && parent != null){
			node.removeFromParent();
			redrawTree();
			TreePath path = new TreePath(parent.getPath());
			this.displayTree.setSelectionPath(path);
			this.importStringTextField.setText("");
			
		}else{
			result.setText("please select a node before right clicking");
		}
	}
	
	private void changeNodeTo(String target){
		this.result.setText("here is the output text");
		if (isValidNode(target)){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
			
			if (node.getChildCount() != 0 && !target.equalsIgnoreCase("AND") && !target.equalsIgnoreCase("or")){

				node.removeAllChildren();
			}
			node.setUserObject(target);
			
			redrawTree();
			TreePath path = new TreePath(node.getPath());
			this.displayTree.setSelectionPath(path);
			this.importStringTextField.setText("");
		}
	}

	private void addChild(String target) {
		this.result.setText("here is the output text");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
		if (node != null){
			if (Gui.isValidNode(target)){
				if (( (String) node.getUserObject()).equalsIgnoreCase("OR") || (((String) node.getUserObject()).equalsIgnoreCase("AND") )) { 
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(target);
					node.add(newNode);
					redrawTree();
					TreePath path = new TreePath(newNode.getPath());
					this.displayTree.setSelectionPath(path);
					this.importStringTextField.setText("");
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
	
	protected void calculateUserProba() {
		
		if (this.displayTree == null){
			this.result.setText("there is currently no tree selected");
			return;
		}

		currentTree = importDisplayedTree((DefaultMutableTreeNode) this.displayTree.getModel().getRoot());
		
		System.out.println(currentTree);
		
		int nrDice;
		try{
			nrDice = Integer.parseInt(this.nrDiceLabel.getText());
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
			nrFaces = Integer.parseInt(this.nrFacesLabel.getText());
		}catch (Exception e){
			this.result.setText("number of faces must be an integer");
			return;
		}
		if (currentTree==null) {
			this.result.setText("there is currently no wish");
			return;
		}
		
		
		
		WishTree tree = WishTree.prepareTree(currentTree, true, nrDice);
		if (currentTree != null){

			Throw currentThrow = new Throw(tree, nrDice, nrFaces); // TODO seems to be a pb if a goal contains a number  bigger than nrfaces
			double res = (double) currentThrow.probaCompleteRec(tree) / Math.pow(nrFaces, nrDice);
			this.result.setText(""+res);
		}

		
		currentTree = null;

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

	private static boolean isValidNode(String target){
//		boolean res = false;
		if (target.contains("ntupel")){
			return true;
		}
		if (target.contains("allthesame")){
			return true;
		}
		for (String preset : WishTree.getAvailablePresets()){
			if (target.equalsIgnoreCase(preset)){
				return true;
			}
		}
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
	
	public int getNrFaces(){
		int res;
		try {
			res = Integer.parseInt(this.nrFacesLabel.getText());
			return res;
		} catch (final NumberFormatException e){
			return 0;
		}
	}
	
	public int getNrDice(){
		int res;
		try {
			res = Integer.parseInt(this.nrDiceLabel.getText());
			return res;
		} catch (final NumberFormatException e){
			return 0;
		}
	}
	
	private void setNrDice(int newNrDice){
		if (newNrDice > 0 && newNrDice <= MAX_NR_DICE){
			this.nrDiceLabel.setText(""+(newNrDice));
			this.popup = new JPopupMenu();
			createContextMenu();
		}
	}
	
	private void setNrFaces(int newNrFaces){

		if (newNrFaces >1 && newNrFaces <= MAX_NR_FACES){
			this.nrFacesLabel.setText(""+(newNrFaces));
			this.popup = new JPopupMenu();
			createContextMenu();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------

}
	
	
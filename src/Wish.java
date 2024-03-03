//public class Wish {
//
//	private boolean AND;
//	private boolean OR;
//	private boolean NR;
//
//	private int number;
//	
//	public Wish(boolean AND, boolean OR, boolean NR, int i) {
//		this.AND = AND;
//		this.OR = OR;
//		this.NR = NR;
//		this.number = i;
//	}
//
//	public Wish(Wish node) {
//		this.AND = node.AND;
//		this.OR = node.OR;
//		this.NR = node.NR;
//
//		this.number = node.number;
//	}
//
//	public boolean isAND() {
//		return AND;
//	}
//
//	public boolean isOR() {
//		return OR;
//	}
//
//	public boolean isNR() {
//		return NR;
//	}
//
//	public String toString() {
//		if (this.isAND()) {
//			return "And";
//		}
//		if (this.isOR()) {
//			return "Or";
//		}
//		if (this.isNR()) {
//			return ""+number;
//		}
//		if (!(this.isAND() || this.isOR() || this.isNR())) {
//			return "this node is crap";
//		}
//		return " you should never see this";
//
//	}
//	
//	public static Wish wishFromString(String string){
//		Wish wish;
//		if (string.equalsIgnoreCase("AND")){
//			wish = new Wish(true, false, false, -1);
//		} else if (string.equalsIgnoreCase("OR")){
//			wish = new Wish(false, true, false, -1);
//		} else {
//			int i;
//			try {
//				i = Integer.parseInt(string);
//				wish = new Wish(false, false, true, i);
//			}
//			catch (NumberFormatException e) {
//				wish = null;
//			}
//		}
//		return wish;
//	}
//
//	public void setToOr() {
//		this.AND = false;
//		this.OR = true;
//		this.NR = false;
//		this.number = -1;
//
//	}
//
//	public int getNr() {
//		return this.number;
//	}
//
//}

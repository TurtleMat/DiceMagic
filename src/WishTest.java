

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

public class WishTest {

	@Test
	public void testBigNodesAndBigNumbers() {
		String encodingString = "(OR;(AND;1;2;4;8);(OR;1;2;4;5;5;6;7);(AND;3;;3;3;3;3;3;3;3;3;3;3))";
		double res = computeProbability(6, 6, encodingString);
		
		Assert.assertEquals(res, 1.0d, 0.001);
	}

	@Test
	public void testDualUse() {
		String encodingString = "(AND;(OR;1;2;3);(OR;1;2;3))";
		double res = computeProbability(6, 2, encodingString);
		
		Assert.assertEquals(res, 0.25d, 0.001);
	}

	@Test
	public void testDualUse2() {
		String encodingString = "(AND;(OR;1;2);(OR;1;2))";
		double res = computeProbability(6, 2, encodingString);
		
		Assert.assertEquals(res, 0.1111d, 0.001);
	}

	@Test
	public void testSeparateUse() {
		String encodingString = "(AND;(OR;1;2;3);(OR;4;5;6))";
		double res = computeProbability(6, 2, encodingString);
		
		Assert.assertEquals(res, 0.5d, 0.001);
	}
	
	@Test
	public void testBigNodesAndBigNumbers2() {
		String encodingString = "(OR;1;2;3;4;5;6)";
		double res = computeProbability(6, 6, encodingString);
		
		Assert.assertEquals(res, 1.0d, 0.001);
	}
	@Test
	public void testManyValues(){
		int nrDice = 6;
		int nrFaces = 6;
		Vector<String> encodingStrings = new Vector<>();
		Vector<Double> expectedResults = new Vector<>();
		
		encodingStrings.add("(OR;(AND;1;1;3;4;5;6);(AND;2;2;3;4;5;6);(AND;1;3;3;4;5;6);(AND;1;2;3;4;5;6);(AND;1;2;4;4;5;6)");
		expectedResults.add(0.046296296296296294);
		
		encodingStrings.add("(OR;ntupel3");
		expectedResults.add(0.36728395061728397);
//		expectedResults.add(0.46728395061728397);
		
		encodingStrings.add("(OR;tenthousand)");
		expectedResults.add(0.9768518518518519);
		
		encodingStrings.add("(AND;(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6));(OR;(AND;1;1);(AND;2;2);(AND;3;3);(AND;4;4);(AND;5;5);(AND;6;6)))");
		expectedResults.add(0.04835390946502058);
		
		encodingStrings.add("(AND;(OR;(AND;(OR;3;5);1);(AND;2;5));(OR;(AND;3;2);1))");
		expectedResults.add(0.467292524005487);
		
		
		boolean test = false;
		for (int i = 0; i<encodingStrings.size();i++){
			test = this.computeVsRecordedResult(nrFaces, nrDice, encodingStrings.elementAt(i), expectedResults.elementAt(i));
			Assert.assertTrue(test);
		}
		
		
		
	}
	
	private boolean computeVsRecordedResult(int nrFaces, int nrDice, String encodingString, double expectedResult)
			throws AssertionError {
		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, nrFaces, nrDice, true);
		if (toCompute == null) {
			throw new AssertionError("input tree could not be interpretated or is null.");
		} 

		WishTree.prepareTree(toCompute, true, nrDice);
		CalculationsForSingleThrow toThrow = new CalculationsForSingleThrow(toCompute, nrDice, nrFaces);

		double res = toThrow.probaCompleteRec(toCompute)[0] ;

		return res/ Math.pow(nrFaces, nrDice)==expectedResult;
		
	}
	
	private double computeProbability(int nrFaces, int nrDice, String encodingString)
			throws AssertionError {
		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, nrFaces, nrDice, true);
		if (toCompute == null) {
			throw new AssertionError("input tree could not be interpretated or is null.");
		} 

		WishTree.prepareTree(toCompute, true, nrDice);
		CalculationsForSingleThrow toThrow = new CalculationsForSingleThrow(toCompute, nrDice, nrFaces);

		double res = toThrow.probaCompleteRec(toCompute)[0] ;

		System.out.println("the proba rec is " + res/ Math.pow(nrFaces, nrDice));
		System.out.println("-----------------------------------------------------------------------------");
		return res;
	}

}

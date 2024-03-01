

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

	private double computeProbability(int nrFaces, int nrDice, String encodingString)
			throws AssertionError {
		WishTree toCompute = WishTree.importAndPrepareTree(encodingString, true);
		if (toCompute == null) {
			throw new AssertionError("input tree could not be interpretated or is null.");
		} 

		WishTree.prepareTree(toCompute, true);
		Throw toThrow = new Throw(toCompute, nrDice, nrFaces);

		double res = toThrow.probaCompleteRec(toCompute) / Math.pow(nrFaces, nrDice);

		System.out.println("the proba rec is " + res);
		System.out.println("-----------------------------------------------------------------------------");
		return res;
	}

}

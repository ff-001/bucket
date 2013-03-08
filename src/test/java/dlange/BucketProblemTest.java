package dlange;

import static org.junit.Assert.*;
import org.junit.Test;

import dlange.BucketProblem.SolutionResult;


/**
 * Unit test for GraphSolver.
 */
public class BucketProblemTest {
	
	@Test
    public void isSolvableValid() {
		BucketProblem tested = new BucketProblem();
		// valid case
        assertTrue("should be true", tested.isSolvable(4, 5, 3) );
        
        // another valid with easy initial condition
        assertTrue("should be true", tested.isSolvable(4, 5, 0) );
    }
	
	@Test
	public void isSolvableInvalidBuckets() {
		BucketProblem tested = new BucketProblem();
		// invalid case even buckets odd solution
        assertFalse("should be false", tested.isSolvable(4, 6, 3) );
        
        // another invalid can not fill buckets
        assertFalse("should be false", tested.isSolvable(0, 0, 1) );
	}

	@Test
	public void isSolvableInvalidDivisor() {
		BucketProblem tested = new BucketProblem();
		// invalid GCD not divisor to solution size
        assertFalse("should be false", tested.isSolvable(3, 9, 2) );
        
	}
	
	@Test
	public void gcd() {
		BucketProblem tested = new BucketProblem();
	  	assertEquals("should be equal", 10, tested.gcd(10, 100));
	}
	
	@Test
	public void solveBasic() {
		BucketProblem tested = new BucketProblem();
		SolutionResult result = tested.solution(5, 3, 4);
		assertEquals("path size should equal", result.sequence.size(), 7);
	}

	@Test
	public void solveTrivial() {
		BucketProblem tested = new BucketProblem();
		SolutionResult result = tested.solution(5, 3, 0);
		assertEquals("path size should equal", result.sequence.size(), 1);
	}
}

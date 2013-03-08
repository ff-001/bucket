package dlange;

import java.util.ArrayList;
import java.util.List;
import dlange.GraphSolver.Goal;
import dlange.GraphSolver.Transition;

/**
 * Problem statement:
 * 
 * Write a function that takes three parameters. The first two parameters are water bucket sizes, and the third parameter 
 * is the solution size. The idea is that you have an unlimited supply of water, and you want to pour the water into the 
 * two buckets and back and forth in order to get the solution size. Your function should figure out how to pour the water 
 * between buckets to reach exactly the solution size. So if you had:
 *   bucket_solution(5, 3, 4);
 *     Step 1: Fill 5 gallon bucket
 *     Step 2: Pour 5 gallon bucket into 3 gallon bucket (remainder 2)
 *     
 *  etc. until you had a bucket with exactly 4 gallons. You can decide how the instructions are printed, these are just suggestions. 
 *  The function should also be able to say if it's unsolvable.
 *
 *  Solution:
 *  
 *  With a given start state, a goal state, and understanding on how to generate transition states, this problem
 *  can be modeled as a graph search problem. The graph is each state, starting from an initial state and including
 *  transition and goal states. A search algorithm such as Breadth First Search (BFS) is guaranteed to find any
 *  existing solution. Generally search routines like BFS begin with a graph and traverse until goal is discovered.
 *  In this scenario, where transition rules are well known and transition states dependent on initial size conditions,
 *  a 'blind search' is used- only a root node is given, and edge connected nodes are 'discovered' using prior knowledge
 *  from the problem statement (filling, pouring water into buckets, etc).
 *  
 *  Because it may be possible to propose bucket and solution sizes which will not lead to a solution, it is necessary
 *  to determine solvability of problem before proceeding with a BFS. This can be quickly determined by modeling the
 *  problem in another fashion, as a linear diophantine equation:
 *      ax + by = c 
 *  where a, b are bucket sizes, c solution size, x = +/- a pours, y = +/- b pours. 
 *  A well known condition of this equation is that if c is not a multiple of the greatest common divisor 
 *  of a and b, then the Diophantine equation ax + by = c has no solution.
 *  
 *  Note: the parameters x and y can be calculated iteratively using the Extended Euclid algorithm; however, knowing
 *  the count of pours for each bucket does not directly give you a step by step approach for filling / pouring water.
 *  The graph approach models these steps explicitly.
 *  
 * @author dlange
 *
 */
public class BucketProblem {

	/**
	 * Command line to collect input and attempt solution. Standard out shows errors or result.
	 * TODO no limit check on sizes; graph could exceed available memory
	 * 
	 * @param args [water bucket size] [water bucket size] [solution size]
	 */
	public static void main(String[] args) {
		final String Instructions = " BucketProblem [water bucket size] [water bucket size] [solution size] \n Sizes are positive values.";
		if (3 == args.length) {
			try {
				// capture the three parameters
				int sizeBucketA = Integer.parseInt(args[0]);
				int sizeBucketB = Integer.parseInt(args[1]);
				int sizeSolution = Integer.parseInt(args[2]);
				// try the problem
				System.out.println(new BucketProblem().attemptSolution(sizeBucketA, sizeBucketB, sizeSolution));
				
			} catch (NumberFormatException ie) {
				System.out.println("Invalid bucket size or solution size:" + args[0]+" "+args[1]+" "+args[2]);
				System.out.println(Instructions);
			}
		} else {
			System.out.println("Requires 3 parameters, provided "+args.length+" parameters.");
			System.out.println(Instructions);
		}
	}


	/**
	 * Given a set of bucket and solution sizes, answer back a String containing either detailed instructions
	 * on hwo to arrive at solution, or a message indicating problem not solvable.
	 * 
	 * @param sizeBucketA
	 * @param sizeBucketB
	 * @param sizeSolution
	 * @return
	 */
	public String attemptSolution(int sizeBucketA, int sizeBucketB, int sizeSolution) {
		if (isSolvable(sizeBucketA, sizeBucketB, sizeSolution)) {
			return (solution(sizeBucketA, sizeBucketB, sizeSolution).verboseInstructions());
		} else {
			return("Provided bucket size and solution size is not solvable");
		}
	}

	/**
	 * Answer back true if a solution exists; otherwise, false
	 * This two bucket problem can be modeled by a linear diophantine equation in form:
	 * Jx + Ky = G  (J, K are bucket sizes, G solution size, x = +/- J pours, y = +/- K pours)
	 * This equation will have no solutions or many solutions. 
	 * Tests for solvability include
	 *  
	 * @param sizeBucketA
	 * @param sizeBucketB
	 * @param sizesizeSolutionution
	 * @return
	 */
	protected boolean isSolvable(int sizeBucketA, int sizeBucketB, int sizeSolution) {
		// make sure buckets can hold solution size
		if (sizeSolution > sizeBucketA && sizeSolution > sizeBucketB) {
			return false;
		}
		// positive and limited bucket, solution sizes
		if (sizeBucketA < 0 || sizeBucketB < 0 || sizeSolution < 0) {
			return false;
		}
        // quick test 1 if left side sizes even and right side size odd, not solvable
        if (sizeBucketA % 2 == 0 && sizeBucketB % 2 == 0 && sizeSolution % 2 != 0) {
            return false;
        }
        // sizeBucketA=0: sizeBucketB*y = sizeSolution : y = sizeSolution/sizeBucketB
        if (0 == sizeBucketA && ( (0 == sizeBucketB) || (sizeSolution % sizeBucketB != 0) ) ) {
            return false;
        }
        // sizeBucketB=0: sizeBucketA*y = sizeSolution : y = sizeSolution/sizeBucketA
        if (0 == sizeBucketB && ( (0 == sizeBucketA) || (sizeSolution % sizeBucketA != 0) ) ) {
            return false;
        }

        // bucket sizes always positive
        int gd = gcd(sizeBucketA, sizeBucketB);
        if (sizeSolution % gd != 0) {
            return false;
        }
        return true;
	}

	/**
	 * Simple recursive calculation of greatest common divisor between two integers
	 * Always positive, and in this case bucket sizes positive
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
    protected int gcd(int a, int b) {
        if (0 == b) {
            return a;
        }
        return gcd(b, a % b);
    }


    /**
     * Calculate solution by using BFS. The GraphSolver BFS implementation takes a starting root node
     * for the graph, an implementation of a Goal function and an implementation of a state transition
     * generation function. These implementations abstract the algorithm from the problem specifics.
     * The result is encapsulated within a SolutionResult object, which has methods to generate either
     * human readable instructions or machine readable commands.
     * 
     * @param sizeBucketA
     * @param sizeBucketB
     * @param sizeSolution
     * @return
     */
	@SuppressWarnings("unchecked")
	protected SolutionResult solution(int sizeBucketA, int sizeBucketB, int sizeSolution) {
		return new SolutionResult(
				new GraphSolver().bfs(
						new BucketMixture(0, 0, "Initial state"), 
						new SolutionGoal(sizeSolution), 
						new DiscoverBucketStates(sizeBucketA, sizeBucketB)));
	}
	
	/**
	 * Implementation of the Goal interface; used to identify when goal state is reached
	 * 
	 * @author dlange
	 *
	 */
	public class SolutionGoal implements Goal<BucketMixture> {
		int sizeSolution;
		public SolutionGoal(int sizeSolution) {
			this.sizeSolution = sizeSolution;
		}
		@Override
		public boolean isGoal(BucketMixture candidate) {
			if (candidate.sizeBucketA == sizeSolution || candidate.sizeBucketB == sizeSolution) {
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * Implemenation of the Transition interface; used to generate new transition states
	 * from a given node (blind search). Uses heuristics based on problem statement to 
	 * determine a next state.
	 * 
	 * @author dlange
	 *
	 */
	public class DiscoverBucketStates implements Transition<BucketMixture> {
		private int sizeBucketA;
		private int sizeBucketB;
		public DiscoverBucketStates(int sizeA, int sizeB) {
			this.sizeBucketA = sizeA;
			this.sizeBucketB = sizeB;
		}
		@Override
		public List<BucketMixture> transitions(BucketMixture currentMixture) {
			List<BucketMixture> list = new ArrayList<BucketMixture>();
	        int x = currentMixture.sizeBucketA;
	        int y = currentMixture.sizeBucketB;
	        int b1 = sizeBucketA;
	        int b2 = sizeBucketB;
	        if (x < b1 && y > 0) {
	            // move partial from y to x
	            int partial = Math.min(y, b1 - x);
	            String transition = "Pour "+b2+"L bucket into "+b1+"L bucket";
	            list.add(new BucketMixture(x + partial, y - partial, transition));
	        }
	        if (y < b2 && x > 0) {
	            // move partial from x to y
	            int partial = Math.min(x, b2 - y);
	            String transition = "Pour "+b1+"L bucket into "+b2+"L bucket";
	            list.add(new BucketMixture(x - partial, y + partial, transition));
	        }
	        if (x > 0) {
	            // empty x
	            String transition = "Empty "+b1+"L bucket";
	            list.add(new BucketMixture(0, y, transition));
	        }
	        if (y > 0) {
	            // empty y
	            String transition = "Empty "+b2+"L bucket";
	            list.add(new BucketMixture(x, 0, transition));
	        }
	        if (x < b1) {
	            // fill x
	            String transition = "Fill "+b1+"L bucket";
	            list.add(new BucketMixture(b1, y, transition));
	        }
	        if (y < b2) {
	            // fill y
	            String transition = "Fill "+b2+"L bucket";
	            list.add(new BucketMixture(x, b2, transition));
	        }
			return list;
		}
		
	}

	/**
	 * Result of search. Used to provide String results for either human readable instructions (verbose) or 
	 * machine readable commands (concise).
	 * 
	 * @author dlange
	 *
	 */
	protected class SolutionResult {
		final List<BucketMixture> sequence;
		
		protected SolutionResult(List<BucketMixture> sequence) {
			this.sequence = sequence;
		}
	
		protected String conciseInstructions() {
			StringBuilder builder = new StringBuilder();
			for (BucketMixture mixture : sequence) {
				builder.append(mixture.toString());
				builder.append("\n");
			}
			return builder.toString();
		}
		protected String verboseInstructions() {
			StringBuilder builder = new StringBuilder();
			for (BucketMixture mixture : sequence) {
				builder.append(mixture.toVerboseString());
				builder.append("\n");
			}
			return builder.toString();
		}
	}

	/**
	 * Data container
	 * @author dlange
	 *
	 */
	protected class BucketMixture {
		int sizeBucketA;
		int sizeBucketB;
		String transition;
		
		public BucketMixture(int a, int b, String transition) {
			this.sizeBucketA = a;
			this.sizeBucketB = b;
			this.transition = transition;
		}
		public String toVerboseString() {
			return transition+" leaving "+toString();
		}
		public String toString() {
			return "["+sizeBucketA+","+sizeBucketB+"]";
		}

	}
}

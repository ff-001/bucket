bucket
======

water bucket
  Problem statement:
  
  Write a function that takes three parameters. The first two parameters are water bucket sizes, and the third parameter 
  is the solution size. The idea is that you have an unlimited supply of water, and you want to pour the water into the 
  two buckets and back and forth in order to get the solution size. Your function should figure out how to pour the water 
  between buckets to reach exactly the solution size. So if you had:
    bucket_solution(5, 3, 4);
      Step 1: Fill 5 gallon bucket
      Step 2: Pour 5 gallon bucket into 3 gallon bucket (remainder 2)
      
   etc. until you had a bucket with exactly 4 gallons. You can decide how the instructions are printed, these are just suggestions. 
   The function should also be able to say if it's unsolvable.
 
   Solution:
   
   With a given start state, a goal state, and understanding on how to generate transition states, this problem
   can be modeled as a graph search problem. The graph is each state, starting from an initial state and including
   transition and goal states. A search algorithm such as Breadth First Search (BFS) is guaranteed to find any
   existing solution. Generally search routines like BFS begin with a graph and traverse until goal is discovered.
   In this scenario, where transition rules are well known and transition states dependent on initial size conditions,
   a 'blind search' is used- only a root node is given, and edge connected nodes are 'discovered' using prior knowledge
   from the problem statement (filling, pouring water into buckets, etc).
   
   Because it may be possible to propose bucket and solution sizes which will not lead to a solution, it is necessary
   to determine solvability of problem before proceeding with a BFS. This can be quickly determined by modeling the
   problem in another fashion, as a linear diophantine equation:
       ax + by = c 
   where a, b are bucket sizes, c solution size, x = +/- a pours, y = +/- b pours. 
   A well known condition of this equation is that if c is not a multiple of the greatest common divisor 
   of a and b, then the Diophantine equation ax + by = c has no solution.
   
   Note: the parameters x and y can be calculated iteratively using the Extended Euclid algorithm; however, knowing
   the count of pours for each bucket does not directly give you a step by step approach for filling / pouring water.
   The graph approach models these steps explicitly.

Build:
   maven project included
Run:
   java -cp target/bucket-1.0-SNAPSHOT.jar dlange.BucketProblem 5 3 4

   
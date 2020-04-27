import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author Andre Murphy
 */
public class CircuitTracer {
	private CircuitBoard newBoard;
	private Storage<TraceState> stateStore;
	private ArrayList<TraceState> bestPaths;
	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.exit(1);
		}
		try {
			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private static void printUsage() {
		System.out.println("The program requires three command-line arguments:/n");
		System.out.println("First argument:");
		System.out.println("-s   -- use a stack for storage"); 
		System.out.println("-q   -- use a queue for storage/n"); 
		System.out.println("Second argument:");
		System.out.println("-c   -- run program in console mode/n");
		System.out.println("Third argument: name of the input file");
	}

	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	private CircuitTracer(String[] args) {
		String fileName = args[2]; //sets filename to last arguement
		try {
			newBoard = new CircuitBoard(fileName); //creates new board!
			if(!args[0].equals("-s") && !args[0].equals("-q")) //if first args does not equal what is required throw an error
			{
				System.out.println("That data structure is not supported or doesn't exist.");
				printUsage();
				System.exit(1);

			}

			if(!args[1].equals("-c")) //if argument is not console, error
			{
				System.out.println("That display format is unavailable"); //did not implement a GUI
				printUsage();
				System.exit(1);

			}

			else if(args[0].equals("-q")) //if first argument calls for queue, set data structure
			{
				stateStore = new Storage<TraceState>(Storage.DataStructure.queue);
			}
			else if(args[0].equals("-s")) {//if first argument calls for stack, set data structure
				stateStore = new Storage<TraceState>(Storage.DataStructure.stack);
			}
			bestPaths = new ArrayList<TraceState>();	//Initialize an empty List called bestPaths that stores objects of type TraceState
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}

		searchAlgorithm(); //runs the search algorithm

		if(args[1].equals("-c")) //if printing to console,
		{
			for(TraceState pathStates: bestPaths) //iterate through all of the best paths
			{
				System.out.print(pathStates.toString()+"\n"); //print out all of the best paths
			}
		}
	}
	/**
	 * Uses an algorithm form given pseudo code and adds the traces to data structures
	 */
	private void searchAlgorithm() {

		ArrayList<Point> starting = newBoard.adjacentP(newBoard.getStartingPoint().x, newBoard.getStartingPoint().y); //creates array of starting points

		for (Point p: starting) //iterate through all possible starting/adjacent positions
		{

			TraceState storestate = new TraceState(new CircuitBoard(newBoard), p.x, p.y);
			stateStore.store(storestate);// create new TraceState starting at the open positions
		}


		while(!stateStore.isEmpty()) 	//While stateStore is not empty
		{
			TraceState newTrace = stateStore.retrieve();	//Retrieve the next TraceState object from stateStore
			if(newTrace.isComplete())	//If that TraceState object ends with a position adjacent to the ending component,
			{
				
				if(newTrace.pathLength() == shortestPath())	//If that TraceState object's path is equal in length to the shortest TraceState in bestPaths,
				{
					bestPaths.add(newTrace);	//Add it to bestPaths
				}
				else if (newTrace.pathLength() < shortestPath())	//Else if that TraceState object's path is shorter than paths in bestPaths,
				{
					bestPaths.clear();//Clear bestPaths and,
					bestPaths.add(newTrace);//add the current TraceState as the new shortest path
				}
			}

			else	//Else generate all valid next TraceState objects from the current TraceState and add them to stateStore
			{
				ArrayList<Point> adjacentPoints = newTrace.getBoard().adjacentP(newTrace.getRow(), newTrace.getCol()); //make arraylist of adjacent points

				for(Point p: adjacentPoints)
				{
					TraceState newnewTrace = new TraceState(newTrace, p.x, p.y);	//Add a new TraceState object (a path) with the open position as the starting point to stateStore
					stateStore.store(newnewTrace);
				}
			}

		}
	}

	/**
	 * checks to find the shortest path in bestPaths
	 * @return the shortest path
	 */
	private int shortestPath() {
		int shortestPath = 10000000; //needs to be larger than any possible path length so it is larger at first
		for (TraceState traceState : bestPaths) { //iterate through all completed traceStates
			if (traceState.pathLength() < shortestPath) { //if less than,
				shortestPath = traceState.pathLength(); //sets integer to shortest path and loop through whole TraceState
			}
		}
		return shortestPath; //returns shortest path
	}
}

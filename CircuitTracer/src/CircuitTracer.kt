import java.awt.Point
import java.io.FileNotFoundException
import java.util.*

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a Stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 *
 * @author Andre Murphy
 */
class CircuitTracer private constructor(args: Array<String>) {
    private var newBoard: CircuitBoard? = null
    private var stateStore: Storage<TraceState>? = null
    private var bestPaths: ArrayList<TraceState>? = null

    /**
     * Uses an algorithm form given pseudo code and adds the traces to data structures
     */
    private fun searchAlgorithm() {
        val starting = newBoard!!.adjacentP(
            newBoard!!.getStartingPoint().x,
            newBoard!!.getStartingPoint().y
        ) //creates array of starting points
        for (p in starting)  //iterate through all possible starting/adjacent positions
        {
            val storestate = TraceState(CircuitBoard(newBoard!!), p.x, p.y)
            stateStore?.store(storestate) // create new TraceState starting at the open positions
        }
        while (stateStore?.isEmpty()!!) //While stateStore is not empty
        {
            val newTrace: TraceState? = stateStore?.retrieve() //Retrieve the next TraceState object from stateStore
            if (newTrace != null) {
                if (newTrace.isComplete) //If that TraceState object ends with a position adjacent to the ending component,
                {
                    if (newTrace.pathLength() === shortestPath()) //If that TraceState object's path is equal in length to the shortest TraceState in bestPaths,
                    {
                        bestPaths!!.add(newTrace) //Add it to bestPaths
                    } else if (newTrace.pathLength() < shortestPath()) //Else if that TraceState object's path is shorter than paths in bestPaths,
                    {
                        bestPaths!!.clear() //Clear bestPaths and,
                        bestPaths!!.add(newTrace) //add the current TraceState as the new shortest path
                    }
                } else  //Else generate all valid next TraceState objects from the current TraceState and add them to stateStore
                {
                    val adjacentPoints: ArrayList<Point> = newTrace.getBoard()
                        .adjacentP(newTrace.getRow(), newTrace.getCol()) //make arraylist of adjacent points
                    for (p in adjacentPoints) {
                        val newnewTrace = TraceState(
                            newTrace,
                            p.x,
                            p.y
                        ) //Add a new TraceState object (a path) with the open position as the starting point to stateStore
                        stateStore!!.store(newnewTrace)
                    }
                }
            }
        }
    }

    /**
     * checks to find the shortest path in bestPaths
     * @return the shortest path
     */
    private fun shortestPath(): Int {
        var shortestPath = 10000000 //needs to be larger than any possible path length so it is larger at first
        for (traceState in bestPaths!!) { //iterate through all completed traceStates
            if (traceState.pathLength() < shortestPath) { //if less than,
                shortestPath = traceState.pathLength() //sets integer to shortest path and loop through whole TraceState
            }
        }
        return shortestPath //returns shortest path
    }

    companion object {
        /** launch the program
         * @param args three required arguments:
         * first arg: -s for Stack or -q for queue
         * second arg: -c for console output or -g for GUI output
         * third arg: input file name
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 3) {
                printUsage()
                System.exit(1)
            }
            try {
                CircuitTracer(args) //create this with args
            } catch (e: Exception) {
                e.printStackTrace()
                System.exit(1)
            }
        }

        /** Print instructions for running CircuitTracer from the command line.  */
        private fun printUsage() {
            println("The program requires three command-line arguments:/n")
            println("First argument:")
            println("-s   -- use a Stack for storage")
            println("-q   -- use a queue for storage/n")
            println("Second argument:")
            println("-c   -- run program in console mode/n")
            println("Third argument: name of the input file")
        }
    }

    /**
     * Set up the CircuitBoard and all other components based on command
     * line arguments.
     *
     * @param args command line arguments passed through from main()
     */
    init {
        val fileName = args[2] //sets filename to last arguement
        try {
            newBoard = CircuitBoard(fileName) //creates new board!
            if (args[0] != "-s" && args[0] != "-q") //if first args does not equal what is required throw an error
            {
                println("That data structure is not supported or doesn't exist.")
                printUsage()
                System.exit(1)
            }
            if (args[1] != "-c") //if argument is not console, error
            {
                println("That display format is unavailable") //did not implement a GUI
                printUsage()
                System.exit(1)
            } else if (args[0] == "-q") //if first argument calls for queue, set data structure
            {
                stateStore = Storage<TraceState>(Storage.DataStructure.Queue)
            } else if (args[0] == "-s") { //if first argument calls for Stack, set data structure
                stateStore = Storage<TraceState>(Storage.DataStructure.Stack)
            }
            bestPaths =
                ArrayList<TraceState>() //Initialize an empty List called bestPaths that stores objects of type TraceState
        } catch (e: FileNotFoundException) {
            println(e.toString())
        }
        searchAlgorithm() //runs the search algorithm
        if (args[1] == "-c") //if printing to console,
        {
            for (pathStates in bestPaths!!)  //iterate through all of the best paths
            {
                print(pathStates.toString().toString() + "\n") //print out all of the best paths
            }
        }
    }
}
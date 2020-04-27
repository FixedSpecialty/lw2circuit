import java.awt.Point
import java.util.*

/**
 * Represents a search state including a potential path through a CircuitBoard
 *
 * @author mvail
 */
class TraceState {
    private var board: CircuitBoard
    private var path: ArrayList<Point>

    /** Initial state with the trace path beginning at given row and column
     * @param startingBoard
     * @param row initial path row
     * @param col initial path column
     */
    constructor(startingBoard: CircuitBoard?, row: Int, col: Int) {
        board = CircuitBoard(startingBoard!!)
        path = ArrayList()
        board.makeTrace(row, col) //will throw exception if row, col is occupied
        path.add(Point(row, col))
    }

    /** New state adding given row and column position to the path from previous state
     * @param previousState
     * @param row row of next point to add to the path
     * @param col column of next point to add to the path
     */
    constructor(previousState: TraceState, row: Int, col: Int) {
        board = CircuitBoard(previousState.board)
        path = ArrayList(previousState.path)
        board.makeTrace(row, col) //will throw exception if row, col is occupied
        path.add(Point(row, col))
    }

    /** Indicates if a position is open in this state's board
     * @param row row of position to check
     * @param col column of position to check
     * @return true if given row and column position is open
     */
    fun isOpen(row: Int, col: Int): Boolean {
        return board.isOpen(row, col)
    }

    /** @return path length
     */
    fun pathLength(): Int {
        return path.size
    }

    /** @return row of the last point in the path
     */
    fun getRow(): Int {
        return path[path.size - 1].x
    }
    /** @return column of the last point in the path
     */
    fun getCol(): Int {
        return path[path.size - 1].y
    }
    /** @return the current CircuitBoard from this state with the path filled in with 'T's
     */
    fun getBoard(): CircuitBoard {
        return CircuitBoard(board)
    }

    /** @return list of row, column points that make up the path
     */
    fun getPath(): ArrayList<Point> {
        return ArrayList(path)
    }

    /** @return true if path ends adjacent to ending component
     */
    val isComplete: Boolean
        get() = adjacent(path[path.size - 1], board.getEndingPoint())

    /**
     * @param p1 first Point
     * @param p2 second Point
     * @return true if p1 and p2 are adjacent, else false
     */
    private fun adjacent(p1: Point, p2: Point): Boolean {
        if (p1.x - 1 == p2.x && p1.y == p2.y) {
            return true
        }
        if (p1.x + 1 == p2.x && p1.y == p2.y) {
            return true
        }
        if (p1.x == p2.x && p1.y - 1 == p2.y) {
            return true
        }
        return p1.x == p2.x && p1.y + 1 == p2.y
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    override fun toString(): String {
        return board.toString()
    }
}
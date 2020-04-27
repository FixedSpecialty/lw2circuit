import java.awt.Point
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.Scanner as JavaUtilScanner

/**
 * Represents a 2D circuit board as read from an input file.
 *
 * @author Andre Murphy
 */
class CircuitBoard {
    /** current contents of the board  */
    private var board: Array<CharArray>

    /** location of row,col for '1'  */
    private var startingPoint: Point? = null

    /** location of row,col for '2'  */
    private var endingPoint: Point? = null

    //constants you may find useful
    private var rows //initialized in constructor
            : Int = 0
    private var cols //initialized in constructor
            : Int = 0

    /** Construct a CircuitBoard from a given board input file, where the first
     * line contains the number of rows and columns as ints and each subsequent
     * line is one row of characters representing the contents of that position.
     * Valid characters are as follows:
     * 'O' an open position
     * 'X' an occupied, unavailable position
     * '1' first of two components needing to be connected
     * '2' second of two components needing to be connected
     * 'T' is not expected in input files - represents part of the trace
     * connecting components 1 and 2 in the solution
     *
     * @param filename
     * file containing a grid of characters
     * @throws FileNotFoundException if Scanner cannot read the file
     * @throws InvalidFileFormatException for any other format or content issue that prevents reading a valid input file
     */
    constructor(filename: String) {
        val fileScan = JavaUtilScanner(File(filename)) //creates scanner
        fileN = filename //sets static filename variable
        checkSizeFormat() //calls method
        charScan(fileN) //calls method
        rows = fileScan.nextInt() // sets the static row dimension variable
        cols = fileScan.nextInt() // sets the static col dimension variable
        board = Array(rows) { CharArray(cols) } //creates board of that size
        populate(fileScan) //calls method that fills board with file characters
        fileScan.close() //close scanner
    }

    /**
     * This method reads in the characters in the file, adds them to the board and sets the
     * start and end variables
     *
     * @param fileScan (A file scanner for passed in files)
     * @throws FileNotFoundException (catches whether scanner points to nothing)
     */
    @Throws(FileNotFoundException::class)
    private fun populate(fileScan: JavaUtilScanner) {
        for (row in board.indices) { //row iteration
            for (col in board[row].indices) { //column iteration
                if (fileScan.hasNext()) { //reads next
                    board[row][col] =
                        fileScan.next()[0] //sets board with char at 0 since it iterates row->col
                    if (board[row][col] == START) { //if char is the start char, set
                        startingPoint = Point(row, col) //creates new point
                    }
                    if (board[row][col] == END) { //if char is the end char, set
                        endingPoint = Point(row, col) //creates new point
                    }
                }
            }
        }
    }

    /** Copy constructor - duplicates original board
     *
     * @param original board to copy
     */
    constructor(original: CircuitBoard) {
        board = original.getBoard()
        startingPoint = Point(original.startingPoint)
        endingPoint = Point(original.endingPoint)
        rows = original.numRows()
        cols = original.numCols()
    }

    /** utility method for copy constructor
     * @return copy of board array
     */
    private fun getBoard(): Array<CharArray> {
        val copy = Array(board.size) { CharArray(board[0].size) }
        for (row in board.indices) {
            for (col in board[row].indices) {
                copy[row][col] = board[row][col]
            }
        }
        return copy
    }

    /** Return the char at board position x,y
     * @param row row coordinate
     * @param col col coordinate
     * @return char at row, col
     */
    fun charAt(row: Int, col: Int): Char = board[row][col]

    /** Return whether given board position is open
     * @param row
     * @param col
     * @return true if position at (row, col) is open
     */
    fun isOpen(row: Int, col: Int): Boolean {
        return if (row < 0 || row >= board.size || col < 0 || col >= board[row].size) {
            false
        } else board[row][col] == OPEN
    }

    /** Set given position to be a 'T'
     * @param row
     * @param col
     * @throws OccupiedPositionException if given position is not open
     */
    fun makeTrace(row: Int, col: Int) {
        if (isOpen(row, col)) {
            board[row][col] = TRACE
        } else {
            throw OccupiedPositionException(
                "row " + row + ", col " + col + "contains '" + board[row][col] + "'"
            )
        }
    }

    /** @return starting Point(row,col)
     */
    fun getStartingPoint(): Point {
        return Point(startingPoint)
    }

    /** @return ending Point(row,col)
     */
    fun getEndingPoint(): Point {
        return Point(endingPoint)
    }

    /** @return number of rows in this CircuitBoard
     */
    private fun numRows(): Int {
        return rows
    }

    /** @return number of columns in this CircuitBoard
     */
    private fun numCols(): Int {
        return cols
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    override fun toString(): String {
        val str = StringBuilder()
        for (row in board.indices) {
            for (element in board[row]) {
                str.append("$element ")
            }
            str.append("\n")
        }
        return str.toString()
    }

    /**
     * This method checks to see if the there is an open adjacent location
     * @param x coordinate
     * @param y coordinate
     * @return the available "next" moves
     */
    fun adjacentP(x: Int, y: Int): ArrayList<Point> {
        val returned =
            ArrayList<Point>() //an arraylist of points
        val adjP = Point(x, y) //new point to set later
        for (i in board.indices) { //iterate board row
            for (j in board[i].indices) { //iterate board col
                val nextP = Point(i, j) //sets point to what exists in board
                if (isOpen(i, j) && adjacent(
                        adjP,
                        nextP
                    )
                ) { //if there is an open and adjacent location, add to arraylist
                    returned.add(nextP) //adds
                }
            }
        }
        return returned
    }

    /**
     * copied from TraceState because was unsure if I could change modifier and use for search
     * algorithm since it would reduce the amount of code since you would have to hardcode an
     * adjacent search everytime
     * @param p1
     * @param p2
     * @return boolean that states whether adjacent or not
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

    companion object {
        private const val OPEN = 'O' //capital 'o'
        private const val CLOSED = 'X'
        private const val TRACE = 'T'
        private const val START = '1'
        private const val END = '2'
        private const val ALLOWED_CHARS = "OXT12"
        private var rowSize = 0
        private var colSize = 0
        private lateinit var colCheck: IntArray
        private var fileN = ""

        /**
         * This method checks the format of the file to make sure it has no errors
         * It will check the dimensions and characters that the file contains.
         * If any errors are caught, either a FNF or Invalid File Exception will be thrown.
         * @throws FileNotFoundException
         */
        @Throws(FileNotFoundException::class)
        fun checkSizeFormat() {
            val scanFile =
                JavaUtilScanner(File(fileN)) //scans through file
            var rowcount = 0 //counter for loop
            var colcount = 0 //counter for loop
            try {
                rowSize = scanFile.nextInt() //row dimension
                colSize = scanFile.nextInt() //col dimension
            } catch (e: NumberFormatException) {
                scanFile.close()
                throw InvalidFileFormatException("Format error in dimensions")
            } catch (e: InputMismatchException) {
                scanFile.close()
                throw InvalidFileFormatException("Format error in dimensions")
            }
            colCheck = IntArray(colSize * rowSize) //array to check for short col

            try {
                while (scanFile.hasNextLine()) { //row loop
                    if (rowcount > rowSize - 1) { //if row counter goes past size, error
                        scanFile.close() //closes scanner
                        throw InvalidFileFormatException("For row count: $rowcount") //throw custom exception
                    }
                    val curLine = scanFile.nextLine() //scans next row
                    val tokenScan = JavaUtilScanner(curLine) //scanner for cols
                    while (tokenScan.hasNext()) { // if column, read
                        tokenScan.next()
                        if (colcount > colSize - 1) { //if col count passes set size, error
                            tokenScan.close()
                            scanFile.close()
                            throw InvalidFileFormatException("For column count: $colcount")
                        }
                        if (!tokenScan.hasNext() && colcount < colSize - 1) //if at the end of the col and dimension doesnt match, error
                        {
                            tokenScan.close()
                            scanFile.close()
                            throw InvalidFileFormatException("For column count: $colcount")
                        }
                        colcount++ //counts through cols
                        colCheck[colcount] = colcount //checks colcount to see if short
                    }
                    colcount = 0 //resets col counter for next row
                    if (curLine.isNotEmpty()) { //if no line in col, go up row
                        rowcount++ //count up row
                    }
                    tokenScan.close()
                }
                if (colCheck[colSize] != colSize) { //if array index doesnt match, error
                    scanFile.close()
                    throw InvalidFileFormatException("Column short")
                }
                scanFile.close()
            } catch (arra: ArrayIndexOutOfBoundsException) { //tests to see if first line contains more than 2 elements. If it does, it will mess up later logic.
                throw InvalidFileFormatException("Dimension Error")
            }
        }

        /**
         * scans through the file and checks all of the characters.
         * checks to see if there are any duplicate starts or ends in the file as well
         * as unaccepted characters that do not exist in the ALLOWED_CHARS
         * @param fileName
         * @throws FileNotFoundException
         */
        @Throws(FileNotFoundException::class)
        fun charScan(fileName: String) {
            val scanner = JavaUtilScanner(File(fileName)) //file scanner
            var start = 0 //start character count
            var finish = 0 //finished charcter count
            scanner.nextLine() //scans next line since dimensions have already been checked
            try {
                while (scanner.hasNext()) { //while there is another character,
                    val line = scanner.nextLine().split(" ".toRegex())
                        .toTypedArray() //couldnt use delimiter for some reason so split works. gets rid of white space
                    for (lineSTR in line) { //for each loop to go through contents of that row
                        val strChar = lineSTR.toCharArray() //adds all of the contents to a character array
                        for (c in strChar) { //another for each loop to go through those added characters
                            if (c == START) { //if the character = 1, increase counter to check for errors
                                start++
                            }
                            if (c == END) { //if the character = 2, increase counter to check for errors
                                finish++
                            }
                            if (c != START && c != END && c != OPEN && c != TRACE && c != CLOSED) { //if any character that dont exist in board are caught, kill
                                scanner.close()
                                throw InvalidFileFormatException("These are the only valid file characters: $ALLOWED_CHARS")
                            }
                        }
                    }
                }
                if (start != 1) { //if counter went over/under 1, there are too many/few starts
                    scanner.close()
                    throw InvalidFileFormatException("This board has too many starting positions")
                }
                if (finish != 1) { //if counter went over/under 1, there are too many/few starts
                    scanner.close()
                    throw InvalidFileFormatException("This board has too many ending positions")
                }
                scanner.close()
            } catch (e: NumberFormatException) {
                scanner.close()
                throw InvalidFileFormatException("number format error")
            } catch (e: InputMismatchException) {
                scanner.close()
                throw InvalidFileFormatException("input character error")
            }
        }
    }
} // class CircuitBoard

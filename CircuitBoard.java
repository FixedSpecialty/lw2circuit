import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 *  
 * @author Andre Murphy
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	//constants you may find useful
	private final int ROWS; //initialized in constructor
	private final int COLS; //initialized in constructor
	private final static char OPEN = 'O'; //capital 'o'
	private final static char CLOSED = 'X';
	private final static char TRACE = 'T';
	private final static char START = '1';
	private final static char END = '2';
	private final static String ALLOWED_CHARS = "OXT12";
	private static int rowSize;
	private static int colSize;
	private static int[] colCheck;
	private static String fileN;

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot read the file
	 * @throws InvalidFileFormatException for any other format or content issue that prevents reading a valid input file
	 */
	public CircuitBoard(String filename) throws FileNotFoundException{

		Scanner fileScan = new Scanner(new File(filename)); //creates scanner
		fileN = filename; //sets static filename variable
		checkSizeFormat(); //calls method
		charScan(fileN); //calls method
		ROWS = fileScan.nextInt(); // sets the static row dimension variable
		COLS = fileScan.nextInt(); // sets the static col dimension variable
		board = new char[ROWS][COLS]; //creates board of that size
		populate(fileScan); //calls method that fills board with file characters
		fileScan.close(); //close scanner
	}

	/**
	 * This method reads in the characters in the file, adds them to the board and sets the
	 * start and end variables
	 * 
	 * @param fileScan (A file scanner for passed in files)
	 * @throws FileNotFoundException (catches whether scanner points to nothing)
	 */
	private void populate(Scanner fileScan) throws FileNotFoundException{
		for (int row = 0; row < board.length; row++) { //row iteration
			for (int col = 0; col < board[row].length; col++) { //column iteration
				if (fileScan.hasNext()) { //reads next
					board[row][col] = fileScan.next().charAt(0); //sets board with char at 0 since it iterates row->col
					if (board[row][col] == START) { //if char is the start char, set
						startingPoint = new Point(row, col); //creates new point
					}
					if (board[row][col] == END) { //if char is the end char, set
						endingPoint = new Point(row, col); //creates new point
					}
				}
			}
		}
	}

	/**
	 * This method checks the format of the file to make sure it has no errors
	 * It will check the dimensions and characters that the file contains.
	 * If any errors are caught, either a FNF or Invalid File Exception will be thrown.
	 * @throws FileNotFoundException
	 */
	public static void checkSizeFormat() throws FileNotFoundException {
		Scanner scanFile = new Scanner(new File(fileN)); //scans through file
		int rowcount = 0; //counter for loop
		int colcount = 0; //counter for loop
		try {
			rowSize = scanFile.nextInt(); //row dimension
			colSize = scanFile.nextInt(); //col dimension
		}catch (NumberFormatException e) {
			scanFile.close();
			throw new InvalidFileFormatException("Format error in dimensions");
		} catch (InputMismatchException e) {
			scanFile.close();
			throw new InvalidFileFormatException("Format error in dimensions");
		}



		colCheck = new int[colSize*rowSize]; //array to check for short col
		//size = new double[rowSize][colSize]; //array with file
		try {
			while (scanFile.hasNextLine()) { //row loop
				if (rowcount > rowSize-1) { //if row counter goes past size, error
					scanFile.close(); //closes scanner
					throw new InvalidFileFormatException("For row count: " + rowcount); //throw custom exception
				}

				String curLine = scanFile.nextLine(); //scans next row
				Scanner tokenScan = new Scanner(curLine); //scanner for cols

				while (tokenScan.hasNext()) {// if column, read
					tokenScan.next();
					if (colcount > colSize-1) { //if col count passes set size, error
						tokenScan.close();
						scanFile.close(); 
						throw new InvalidFileFormatException("For column count: " + colcount);
					}
					if(!tokenScan.hasNext() && colcount < colSize-1) //if at the end of the col and dimension doesnt match, error
					{
						tokenScan.close();
						scanFile.close(); 
						throw new InvalidFileFormatException("For column count: " + colcount);
					}
					colcount++; //counts through cols
					colCheck[colcount]=colcount; //checks colcount to see if short

				}
				colcount = 0; //resets col counter for next row
				if (!curLine.isEmpty()) { //if no line in col, go up row
					rowcount++; //count up row
				}
				tokenScan.close();
			}
			if(colCheck[colSize]!= colSize) { //if array index doesnt match, error
				scanFile.close();
				throw new InvalidFileFormatException("Column short");
			}
			scanFile.close();

		}
		catch(ArrayIndexOutOfBoundsException arra) { //tests to see if first line contains more than 2 elements. If it does, it will mess up later logic.  
			throw new InvalidFileFormatException("Dimension Error");
		}
	}

	/**
	 * scans through the file and checks all of the characters.
	 * checks to see if there are any duplicate starts or ends in the file as well
	 * as unaccepted characters that do not exist in the ALLOWED_CHARS
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void charScan(String fileName) throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(fileName)); //file scanner
		int start = 0; //start character count
		int finish = 0; //finished charcter count

		scanner.nextLine(); //scans next line since dimensions have already been checked
		try {
			while (scanner.hasNext()) { //while there is another character,
				String[] line = scanner.nextLine().split(" "); //couldnt use delimiter for some reason so split works. gets rid of white space
				for (String lineSTR : line) { //for each loop to go through contents of that row
					char[] strChar = lineSTR.toCharArray(); //adds all of the contents to a character array
					for (char c : strChar) { //another for each loop to go through those added characters
						if (c == START) { //if the character = 1, increase counter to check for errors
							start++;
						}
						if (c == END) { //if the character = 2, increase counter to check for errors
							finish++;
						}

						if (c != START && c != END && c != OPEN && c != TRACE && c != CLOSED) { //if any character that dont exist in board are caught, kill
							scanner.close();
							throw new InvalidFileFormatException("These are the only valid file characters: " + ALLOWED_CHARS);
						}
					}
				}
			}
			if (start != 1) { //if counter went over/under 1, there are too many/few starts
				scanner.close();
				throw  new InvalidFileFormatException("This board has too many starting positions");
			}
			if (finish != 1) { //if counter went over/under 1, there are too many/few starts
				scanner.close();
				throw new InvalidFileFormatException("This board has too many ending positions");
			}
			scanner.close();

		} catch (NumberFormatException e) {
			scanner.close();
			throw new InvalidFileFormatException("number format error");
		} catch (InputMismatchException e) {
			scanner.close();
			throw new InvalidFileFormatException("input character error");
		}
	}

	/** Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/** utility method for copy constructor
	 * @return copy of board array */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}

	/** Return the char at board position x,y
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}

	/** Return whether given board position is open
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open 
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}

	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (isOpen(row, col)) {
			board[row][col] = TRACE;
		} else {
			throw new OccupiedPositionException("row " + row + ", col " + col + "contains '" + board[row][col] + "'");
		}
	}

	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}

	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}

	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}

	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}

	/**
	 * This method checks to see if the there is an open adjacent location
	 * @param x coordinate
	 * @param y coordinate
	 * @return the available "next" moves
	 */
	public ArrayList<Point> adjacentP(int x, int y)
	{
		ArrayList<Point> returned = new ArrayList<Point>(); //an arraylist of points
		Point adjP = new Point(x, y); //new point to set later
		for (int i = 0; i < board.length; i++) { //iterate board row
			for (int j = 0; j < board[i].length; j++) { //iterate board col
				Point nextP = new Point(i, j); //sets point to what exists in board
				if (isOpen(i,j) && adjacent(adjP, nextP)) { //if there is an open and adjacent location, add to arraylist
					returned.add(nextP); //adds
				}
			}
		}
		return returned;
	}

	/**
	 * copied from TraceState because was unsure if I could change modifier and use for search
	 * algorithm since it would reduce the amount of code since you would have to hardcode an 
	 * adjacent search everytime
	 * @param p1
	 * @param p2
	 * @return boolean that states whether adjacent or not
	 */
	private boolean adjacent(Point p1, Point p2) {
		if (p1.x-1 == p2.x && p1.y == p2.y) {
			return true;
		}
		if (p1.x+1 == p2.x && p1.y == p2.y) {
			return true;
		}
		if (p1.x == p2.x && p1.y-1 == p2.y) {
			return true;
		}
		if (p1.x == p2.x && p1.y+1 == p2.y) {
			return true;
		}
		return false;
	}

}// class CircuitBoard

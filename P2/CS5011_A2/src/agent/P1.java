package agent;
import help.Help;
import startCode.GameHandler;

public class P1 {

	private char[][] initBoard;
	private char[][] transBoard;
	private boolean verbose;
	private int cellNumber;
	private int mineNumber;
	private boolean success;
	
	public P1(char[][] initBoard, char[][] transBoard, boolean verbose, int mineNumber) {
		this.initBoard = initBoard;
		this.transBoard = transBoard;
		this.verbose = verbose;
		this.mineNumber = mineNumber;
		this.cellNumber = initBoard[0].length * initBoard[0].length;

		boolean ans = search(this.transBoard); // Search board
		if(!ans) {
			success=false;
		}else {
			success=true;
		}

	}

	private boolean search(char[][] transBoard) {

		for (int i = 0; i < transBoard[0].length; i++) {
			for (int j = 0; j < transBoard[0].length; j++) {

				if (this.transBoard[i][j] == '?') {
					int cellVisit = i * transBoard[0].length + j;
					if (checkSuccess(cellVisit)) {
						return true;
					}
					if (verbose) {
						GameHandler.printBoard(this.transBoard);
					}
					if (initBoard[i][j] == '0') {
						this.transBoard[i][j] = initBoard[i][j];
						uncoverNeighbor(i, j);
					} else if (initBoard[i][j] == 'm') {
						this.transBoard[i][j] = '-';
						return false;
					} else {
						this.transBoard[i][j] = initBoard[i][j];
					}
				}
			}
		}
		return true;
	}

	private void uncoverNeighbor(int i, int j) {

		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				// Search for neighbors around (i,j) cell
				if (m != i || n != j) {
					if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) { // Not out of bounds
						if (Help.checkSymbol(transBoard[m][n], '?')) { 
							// if the cell is covered
							transBoard[m][n] = initBoard[m][n]; // uncover the cell
							if (Help.checkSymbol(transBoard[m][n], '0')) {
								// if the cell clue is 0, open all the neighbors of it
								uncoverNeighbor(m, n);
							}
						}
					}
				}
			}
		}
	}

	private boolean checkSuccess(int cellVisit) {
		// if all safe cells being probed, the agent success
		if (cellNumber - cellVisit == mineNumber) {
			return true;
		}
		return false;
	}
	
	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public char[][] getBoard() {
		return transBoard;
	}

}

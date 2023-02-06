package agent;

import help.Help;
import java.util.ArrayList;

import startCode.GameHandler;

public class P2 {
	protected char[][] initBoard;
	protected char[][] transBoard;
	protected int cellNumber;
	protected int mineNumber;
	protected int blockNumber;
	protected int probedNumber;
	protected int flagedNumber;
	protected boolean success;
	
	public P2(char[][] initBoard, char[][] transBoard, int mineNumber, int blockNumber) {
		this.initBoard = initBoard;
		this.transBoard = transBoard;
		this.mineNumber = mineNumber;
		this.blockNumber = blockNumber;
		this.cellNumber = initBoard[0].length * initBoard[0].length;
		probedNumber = 0;
		flagedNumber = 0;
		
		boolean ans = search(this.transBoard);

		if(!ans) {
			success=false;
		}else {
			success=true;
		}

	}

	private boolean search(char[][] transBoard) {
//		System.out.println("build KBU00");
		boolean updates = true;
		int size = transBoard[0].length;
		if (this.transBoard[0][0] == '?') {
			// Probe the (0,0) cell
			this.transBoard[0][0] = initBoard[0][0];
			probedNumber++;
			if (Help.checkSymbol(this.transBoard[0][0], '0')) {
				uncoverNeighbor(0, 0);
			}
		}
		if (this.transBoard[size / 2][size / 2] == '?') {
			// Probe the middle cell
			this.transBoard[size / 2][size / 2] = initBoard[size / 2][size / 2];
			probedNumber++;
			if (Help.checkSymbol(this.transBoard[size / 2][size / 2], '0')) {
				uncoverNeighbor(size / 2, size / 2);
			}
		}

		while (true) {
			if (cellNumber - probedNumber - blockNumber == mineNumber && flagedNumber == mineNumber) {
				// If probed all the correct cells and flagged all the dangered cell
				return true;
			}
			updates = false;

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (this.transBoard[i][j] == '?') {
						// Using SPS to check the covered cells
						if (checkCoveredNeighbor(i, j)) {
							updates = true;
						}
					}
				}
			}
			if (!updates) {
				return false;
			}
		}

	}

	private boolean checkCoveredNeighbor(int i, int j) {
		// Using SPS to check cell(i,j)
		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				if (m != i || n != j) {
					if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) {
						if (transBoard[m][n] >= '0' && transBoard[m][n] <= '8') { // clue
							int[] situation = checkUncoverNeighbor(m, n);
							if(situation[0]==Integer.parseInt(String.valueOf(transBoard[m][n]))) {
								// all dangers in the neighbor have already been found
								uncoverNeighbor(m,n);
								return true;
							}
							if (situation[1] == Integer.parseInt(String.valueOf(transBoard[m][n])) - situation[0]) {
								// AMN
								// covered = clue - existed danger
								// mark a danger
								transBoard[i][j] = '*';
								flagedNumber++;
								return true;
							}
							if (situation[0] == Integer.valueOf(transBoard[m][n])) {
								// AFN
								// clue = existed danger
								// uncover
								transBoard[i][j] = initBoard[i][j];
								probedNumber++;
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	protected int[] checkUncoverNeighbor(int i, int j) {
		int[] situation = { 0, 0 };
		
		// count the number of flagged cells and unknown cells around cell (i,j)
		// and store in the situation array
		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				if (m != i || n != j) {
					if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) {
						if (transBoard[m][n] == '*') { // count the number of flagged cells
							situation[0]++;
						}
						if (transBoard[m][n] == '?') { // count the unknown cells
							situation[1]++;
						}
					}

				}
			}
		}
		return situation;
	}

	protected void uncoverNeighbor(int i, int j) {
		
		// probe all cells around (i,j)
		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				if (m != i || n != j) {
					if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) {
						if (Help.checkSymbol(transBoard[m][n], '?')) {
							transBoard[m][n] = initBoard[m][n];
							probedNumber++;
							if (Help.checkSymbol(transBoard[m][n], '0')) {
								// if the clue is 0, open all its neighbors
								uncoverNeighbor(m, n);
							}
						}
					}
				}
			}
		}
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

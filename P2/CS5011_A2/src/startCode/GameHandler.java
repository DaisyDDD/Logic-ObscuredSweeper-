
package startCode;
import startCode.World;
public class GameHandler {

	private char[][] initBoard;

	
	public GameHandler(World world) {
		this.setInitBoard(world.map);
		printBoard(initBoard);
		
	}
	public char[][] getInitBoard() {
		return initBoard;
	}
	public void setInitBoard(char[][] initBoard) {
		this.initBoard = initBoard;
	}
	
	
	public static void printBoard(char[][] board) {
		System.out.println();
		// first line
		System.out.print("    ");
		for (int j = 0; j < board[0].length; j++) {
			System.out.print(j + " "); // x indexes
		}
		System.out.println();
		// second line
		System.out.print("    ");
		for (int j = 0; j < board[0].length; j++) {
			System.out.print("- ");// separator
		}
		System.out.println();
		// the board
		for (int i = 0; i < board.length; i++) {
			System.out.print(" "+ i + "| ");// index+separator
			for (int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j] + " ");// value in the board
			}
			System.out.println();
		}
		System.out.println();
	}

}

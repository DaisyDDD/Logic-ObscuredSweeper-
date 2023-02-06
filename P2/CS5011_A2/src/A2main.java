
import agent.P1;
import agent.P2;
import agent.P3;
import agent.P4;
import startCode.GameHandler;
import startCode.World;

public class A2main {
	
	private static int mineNumber=0;
	private static int blockNumber=0;
	
	public static void main(String[] args) {

		boolean verbose=false;
		if (args.length>2 && args[2].equals("verbose") ){
			verbose=true; //prints agent's view at each step if true
		}

		System.out.println("-------------------------------------------\n");
		System.out.println("Agent " + args[0] + " plays " + args[1] + "\n");


		World world = World.valueOf(args[1]);
		GameHandler game=new GameHandler(world);
		
		char[][] array = game.getInitBoard();
		char[][] initBoard = new char[array.length][array.length];
		for(int i=0;i<array.length;i++ ) {
			initBoard[i] = array[i].clone();
		}

		System.out.println("Start!");
		char[][] transBoard=transBoard(initBoard);
		boolean isSuccess=false;
		char[][] finalBoard = new char[array.length][array.length];
		switch (args[0]) {
		case "P1": // Basic agent
			P1 p1=new P1(game.getInitBoard(),transBoard,verbose, mineNumber);
			isSuccess=p1.getSuccess();
			finalBoard=p1.getBoard();
			break;
		case "P2": // Beginner agent
			P2 p2=new P2(game.getInitBoard(),transBoard, mineNumber,blockNumber);
			isSuccess=p2.getSuccess();
			finalBoard=p2.getBoard();
			break;
		case "P3": // Intermediate agent
			P3 p3=new P3(game.getInitBoard(),transBoard, mineNumber,blockNumber);
			isSuccess=p3.getSuccess();
			finalBoard=p3.getBoard();
			break;
		case "P4": // Intermediate agent
			P4 p4 = new P4(game.getInitBoard(),transBoard, mineNumber,blockNumber);
			isSuccess=p4.getSuccess();
			finalBoard=p4.getBoard();
			break;
		}
		System.out.println("Final map");
		GameHandler.printBoard(finalBoard);
		if(isSuccess) {
			System.out.println("\nResult: Agent alive: all solved\n");
		}
		else {
			//System.out.print(args[0].equals("P1"));
			if(args[0].equals("P1")) {
				System.out.println("\nResult: Agent dead: found mine\n");
			}else {
				System.out.println("\nResult: Agent not terminated\n");
			}
		}
		//templates to print results - copy to appropriate places
		//System.out.println("\nResult: Agent alive: all solved\n");
		//System.out.println("\nResult: Agent dead: found mine\n");
		//System.out.println("\nResult: Agent not terminated\n");

	}

	private static char[][] transBoard(char[][] board) {
		int Mnum=0;
		int Bnum=0;
		for(int i =0;i<board[0].length;i++) {
			for(int j=0;j<board[0].length;j++) {
				if(board[i][j]!='b') {
					if(board[i][j]=='m') {
						Mnum++;
					}
					board[i][j]='?';
				}
				else {
					Bnum++;
				}
			}
		}
		blockNumber = Bnum;
		mineNumber = Mnum;
		return board;
		
	}




}

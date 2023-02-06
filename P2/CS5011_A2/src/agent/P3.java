package agent;

import java.util.ArrayList;

import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import help.Help;

public class P3 extends P2{
	
	protected boolean updates=true;
	protected StringBuilder KBU = new StringBuilder();
	protected static ArrayList<String> stack = new ArrayList<String>();
	protected static String[][] possibleDanger;
	protected static int index = 0;
	
	public P3(char[][] initBoard, char[][] transBoard, int mineNumber, int blockNumber) {
		// Call parent construct to use SPS at first
		super(initBoard,transBoard, mineNumber,blockNumber);
		if(!success) {
			// If not success, use DNF encoding now
			boolean ans=search(this.transBoard);
			
			if(!ans) {
				success=false;
			}else {
				success=true;
			}
		}
		
	}
	protected boolean search(char[][] transBoard2) {
		while (true) {
			// If probed all the correct cells and flagged all the dangered cell
			if (cellNumber - probedNumber - blockNumber == mineNumber && flagedNumber == mineNumber) {
				return true;
			}
			updates=false;
			
			// build KBU in DNF format
			KBU = buildKBU();
			for (int i = 0; i < transBoard[0].length; i++) {
				for (int j = 0; j < transBoard[0].length; j++) {
					if (transBoard[i][j] == '?') {
						// using entailment to check each cell one by one
						checkSafe(i, j, false);	
					}
				}
			}

			if(!updates) {
				return false;
			}
		}	
	}
	protected StringBuilder buildKBU() {
		
		if(KBU.length()>0) {
			KBU.delete(0, KBU.length()-1);
		}
		StringBuilder formulas = new StringBuilder();
		for (int i = 0; i < transBoard[0].length; i++) {
			for (int j = 0; j < transBoard[0].length; j++) {
				char cell = transBoard[i][j];
				int[] situation = checkUncoverNeighbor(i, j);
				if (situation[1] > 0 && (cell > '0' && cell <= '8')) {
					// connect cell formulas with & and store in the StringBuilder formulas
					formulas.append("("+cellFormula(i, j)+")"+"&");
				}
			}
		}
		if(formulas.length()>0) {
			// delete the extra & in the string
			formulas.deleteCharAt(formulas.length()-1);
		}
		return formulas;

	}

	protected String checkSafe(int i, int j, boolean flag) {
		// Check the safety of cell (i,j) using entailment KBU
		// At first check the KBU & ~(i,j)
		// If false, flag the cell
		// If true, check KBU & (i,j). If false, probe the cell
		
		if(KBU.length()<=0) {
			return null;
		}
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		Tristate result = null;
		try {
			String cell = Integer.toString(i) + Integer.toString(j);
			final Formula formula;
			if(!flag) {
				
				 formula = p.parse("("+KBU + ")&~" + cell);
			}
			else {
				formula = p.parse("("+KBU + ")&" + cell);
			}
			
			final SATSolver miniSat = MiniSat.miniSat(f);
			miniSat.add(formula);
			result = miniSat.sat();
			
			if (result.toString() == "FALSE"&&flag==false) {
				transBoard[i][j] = '*';
				flagedNumber++;
				KBU=buildKBU();
				updates=true;
			}else if(result.toString() == "FALSE"&&flag==true) {
				transBoard[i][j] = initBoard[i][j];
				probedNumber++;
				if (Help.checkSymbol(transBoard[i][j], '0')) {
					uncoverNeighbor(i, j);
				}
				updates=true;
			}
			else if (result.toString() == "TRUE"&&flag==false) {
				checkSafe(i,j, true);
			}
			else;
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
		return result.toString();

	}
	protected String cellFormula(int i, int j) {
		// build the cell formula using the coordinate of a cell
		ArrayList<String> neighbours = new ArrayList<>();
		StringBuilder cellFormula = new StringBuilder();
		
		int numOfDan = Integer.parseInt(String.valueOf(transBoard[i][j]));
		int[] situation = checkUncoverNeighbor(i, j);
		int numOfFlag = situation[0];
		int num = numOfDan - numOfFlag;

		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) {
					if (transBoard[m][n] == '?') {
						// build the cell formula using the coordinate of a cell
						// for example, cell(1,2) will be represented as 12 in the formula
						neighbours.add(Integer.toString(m) + Integer.toString(n));
					}
				}

			}
		}
		
		if(neighbours.size()==0) {
			
		}
		int size = Help.comb(neighbours.size(), num);
		possibleDanger = new String[size][num];
		index=0;
		//get permutation of the neighbours and store in the possibleDanger
		
		permutation(neighbours, num, 0, 0);

		for (int m = 0; m < possibleDanger.length; m++) {
			// add all items in the cellFormula StringBuilder to create KBU
			cellFormula.append("(");
			for (int h = 0; h < neighbours.size(); h++) {
				boolean flag = false;
				String cell = neighbours.get(h);
				for (int n = 0; n < possibleDanger[m].length; n++) {
					if (cell == possibleDanger[m][n]) {
						flag = true;
						break;
					}
				}
				if (!flag)
					cellFormula.append("~");

				cellFormula.append(cell);
				if (h < neighbours.size() - 1)
					cellFormula.append("&");
			}
			cellFormula.append(")");
			if (m < possibleDanger.length - 1)
				cellFormula.append("|");
		}

		return cellFormula.toString();
	}
	protected static void permutation(ArrayList<String> shu, int targ, int has, int cur) {
		// get permutation of neighbours and store in the possibleDange String array
		if (has == targ) {
			for (int j = 0; j < stack.size(); j++) {
				possibleDanger[index][j] = stack.get(j);
			}
			index++;
			return;
		}

		for (int i = cur; i < shu.size(); i++) {
			if (!stack.contains(shu.get(i))) {
				stack.add(shu.get(i));
				permutation(shu, targ, has + 1, i);
				stack.remove(stack.size() - 1);
			}
		}

	}
	
}

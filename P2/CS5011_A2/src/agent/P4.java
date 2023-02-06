package agent;

import java.util.ArrayList;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import help.Help;

public class P4 extends P3 {
	protected static ArrayList<int[]> clauses;
	protected static int indexOfPD = 0;
	protected int numOfClauses = 0;
	
	public P4(char[][] initBoard, char[][] transBoard, int mineNumber, int blockNumber) {
		// Call parent construct to use SPS and DNF encoding at first
		super(initBoard, transBoard, mineNumber, blockNumber);
		if (!success) {
			// If not success, use CNF encoding now
			boolean ans = searchCNF(this.transBoard);
			
			if(!ans) {
				success=false;
			}else {
				success=true;
			}

		}
		
	}
	
	protected boolean searchCNF(char[][] transBoard2) {
		while (true) {
			// If probed all the correct cells and flagged all the dangered cell
			if (cellNumber - probedNumber - blockNumber == mineNumber && flagedNumber == mineNumber) {
				return true;
			}
			updates = false;

			// Build KBU in CNF encoding format
			KBU = buildKBUCNF();

			for (int i = 0; i < transBoard[0].length; i++) {
				for (int j = 0; j < transBoard[0].length; j++) {
					if (transBoard[i][j] == '?') {
						// Using entailment to check each cell
						checkSafeCNF(i, j, false);
					}
				}
			}
			if (!updates) {
				return false;
			}
		}
	}
	
	protected StringBuilder buildKBUCNF() {
		// Build KBU in CNF format and store all clauses in the clauses ArrayList
		clauses = new ArrayList<int[]>();
		if (KBU.length() > 0) {
			KBU.delete(0, KBU.length() - 1);
		}
		StringBuilder formulas = new StringBuilder();
		for (int i = 0; i < transBoard[0].length; i++) {
			for (int j = 0; j < transBoard[0].length; j++) {
				char cell = transBoard[i][j];
				int[] situation = checkUncoverNeighbor(i, j);
				if (situation[1]  > 0 && (cell > '0' && cell <= '8')) {
					// connect cell formulas with & and store in the StringBuilder formulas
					formulas.append(cellFormulaCNF(i, j) + "&");
				}
			}
		}
		if (formulas.length() > 0) {
			// delete the extra & in the string
			formulas.deleteCharAt(formulas.length() - 1);
		}
		return formulas;

	}

	protected String cellFormulaCNF(int i, int j) {
		ArrayList<String> neighbours = new ArrayList<>();
		StringBuilder cellFormula = new StringBuilder();
		StringBuilder cellFormula1 = new StringBuilder();
		StringBuilder cellFormula2 = new StringBuilder();
		int numOfDan = Integer.parseInt(String.valueOf(transBoard[i][j]));
		int[] situation = checkUncoverNeighbor(i, j);
		int numOfFlag = situation[0];
		int num = numOfDan - numOfFlag;

		for (int m = i - 1; m <= i + 1; m++) {
			for (int n = j - 1; n <= j + 1; n++) {
				if (m >= 0 && m < transBoard[0].length && n >= 0 && n < transBoard[0].length) {
					if (transBoard[m][n] == '?') {
						neighbours.add(Integer.toString(m) + Integer.toString(n));
					}
				}

			}
		}
		int atMostDanger = num;
		int atLeastNoDanger = neighbours.size() - num;
		int size;
		indexOfPD = 0;
		size = Help.comb(neighbours.size(), atMostDanger + 1);
		possibleDanger = new String[size][atMostDanger + 1];
		permutationCNF(neighbours, atMostDanger + 1, 0, 0);
		// Build the at Most k danger part

		for (int m = 0; m < possibleDanger.length; m++) {
			int[] sb1 = new int[atMostDanger + 1];
			cellFormula1.append("(");
			for (int n = 0; n < possibleDanger[m].length; n++) {
				String cell = possibleDanger[m][n];
				cellFormula1.append("~" + cell);
				sb1[n] = (-1) * Integer.parseInt(String.valueOf(cell));

				if (n < possibleDanger[m].length - 1) {
					cellFormula1.append("|");
				}
			}
			clauses.add(sb1);
			cellFormula1.append(")");

			if (m < possibleDanger.length - 1) {
				cellFormula1.append("&");
			}
		}
		numOfClauses += possibleDanger.length;

		size = Help.comb(neighbours.size(), atLeastNoDanger + 1);
		indexOfPD = 0;
		possibleDanger = new String[size][atLeastNoDanger + 1];
		permutationCNF(neighbours, atLeastNoDanger + 1, 0, 0);
		// Build the at Lease k danger part
		for (int m = 0; m < possibleDanger.length; m++) {
			int[] sb2 = new int[atLeastNoDanger + 1];
			cellFormula2.append("(");
			for (int n = 0; n < possibleDanger[m].length; n++) {
				String cell = possibleDanger[m][n];
				cellFormula2.append(cell);
				sb2[n] = Integer.parseInt(String.valueOf(cell));
				if (n < possibleDanger[m].length - 1) {
					cellFormula2.append("|");
				}
			}
			clauses.add(sb2);

			cellFormula2.append(")");

			if (m < possibleDanger.length - 1) {
				cellFormula2.append("&");
			}
		}
		// numOfClauses is used to count the number of clauses
		numOfClauses += possibleDanger.length;
		
		cellFormula = cellFormula1.append("&" + cellFormula2);

		return cellFormula.toString();
	}
	protected static void permutationCNF(ArrayList<String> shu, int targ, int has, int cur) {
		// get permutation of neighbours and store in the possibleDange String array
		if (has == targ) {
			for (int j = 0; j < stack.size(); j++) {
				possibleDanger[indexOfPD][j] = stack.get(j);
			}
			indexOfPD++;
			return;
		}

		for (int i = cur; i < shu.size(); i++) {
			if (!stack.contains(shu.get(i))) {
				stack.add(shu.get(i));
				permutationCNF(shu, targ, has + 1, i);
				stack.remove(stack.size() - 1);
			}
		}

	}
	protected void checkSafeCNF(int i, int j, boolean flag) {

		// CNFs are conjunctions of clauses,
		// where a clause is a disjunction of literals
		
		// Check the safety of cell (i,j) using entailment KBU
		// At first check the KBU & ~(i,j)
		// If false, flag the cell
		// If true, check KBU & (i,j). If false, probe the cell
				
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(i));
		sb.append(Integer.toString(j));
		
		// build the cell formula using the coordinate of a cell
		// for example, cell(1,2) will be represented as 12 in the formula
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(cellNumber - blockNumber);
		solver.setExpectedNumberOfClauses(numOfClauses);
		int[] temp = { 0 };
		int[] clause;
		try {
			for (int i1 = 0; i1 < clauses.size(); i1++) {
				clause = clauses.get(i1);
				solver.addClause(new VecInt(clause));

			}
			if (!flag) {
				temp[0] = (-1) * Integer.parseInt(String.valueOf(sb.toString()));
			} else {
				temp[0] = Integer.parseInt(String.valueOf(sb.toString()));
			}
			clause = temp;
			solver.addClause(new VecInt(clause));
			IProblem problem = solver;
			

			if (!problem.isSatisfiable() && !flag) {
				transBoard[i][j] = '*';
				flagedNumber++;
				KBU=buildKBUCNF();
				updates=true;
			} else if (!problem.isSatisfiable() && flag) {
				transBoard[i][j] = initBoard[i][j];
				probedNumber++;
				if (Help.checkSymbol(transBoard[i][j], '0')) {
					uncoverNeighbor(i, j);
				}
				KBU=buildKBUCNF();
				updates=true;
			} else if (problem.isSatisfiable() && !flag) {
				checkSafeCNF(i, j, true);			
			} 
			else;
		} catch (ContradictionException | TimeoutException e) {

			e.printStackTrace();
		}

	}



}

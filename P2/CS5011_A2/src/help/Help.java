package help;

public class Help {

	public static int comb(int m, int n) {
		if (n == 0)
			return 1;
		if (n == 1)
			return m;
		if (n > m / 2)
			return comb(m, m - n);
		if (n > 1)
			return comb(m - 1, n - 1) + comb(m - 1, n);
		return -1; 
	}
	public static boolean checkSymbol(char c, char d) {
		if (c == d)
			return true;
		else
			return false;
	}
}

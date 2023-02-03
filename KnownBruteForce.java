import java.util.ArrayList;

public class KnownBruteForce {
	
	public KnownBruteForce(ArrayList<Piece> local_piece_list, int xAxissAmount, int yAxissAmount, Piece[][] solution_table, int dimension_amount) {
		Piece[][] solved_table = new Piece[xAxissAmount][yAxissAmount];
		
		if(solved_table.length != solution_table.length || solved_table[0].length != solution_table[0].length)
			throw new IllegalArgumentException("Rozwiazanie nie jest tozsame z zadana sekwencja");
		
		
		for(int x = 0; x < xAxissAmount; x++)
			for(int y = 0; y < yAxissAmount; y++) {
				for(int a = 0; a < local_piece_list.size(); a++) {
					if(solution_table[x][y].getId() == local_piece_list.get(a).getId()) {
						solved_table[x][y] = local_piece_list.get(a);
						System.out.println("solution_table["+x+"]["+y+"]  =  " + local_piece_list.get(a).getId());
						local_piece_list.remove(a);
						break;
					}
				}
			}
		
		System.out.print("\nOtrzymano wynikow¹ sekwencje: ");
		for(int i = 0; i < yAxissAmount; i++) {
			System.out.print("\n");
			for(int j = 0; j < xAxissAmount; j++) {
				if(solved_table[j][i] != null)
					System.out.print(" " + solved_table[j][i].getId());
				else 
					System.out.print(" _ ");
			}
		}
		System.out.print("\n\n");
		
		int rezult = SolvedTableCheck(solved_table, solution_table);
		if(rezult == 0)
			System.out.println("Sekwencje sa zgodne");
		else
			System.out.println("Popelniono w procesie " + rezult + " bledow");

	}

	private static int SolvedTableCheck(Piece[][] solved_table, Piece[][] solution_table) {
		int error_counter = 0;
		if (solved_table.length != solution_table.length || solved_table[0].length != solution_table[0].length)
				throw new IllegalArgumentException("Sekwencja rozwiazania nie jest tozsama z zadana sekwencja pod katem wymiarow!");

		for(int x = 0; x < solution_table.length; x++)
			for(int y = 0; y < solution_table[0].length; y++) {
				if (solved_table[x][y] == null || solution_table[x][y].getId() != solved_table[x][y].getId())
					error_counter++;
			}
		return error_counter;
	}
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class UnknownBruteForce {
	public UnknownBruteForce(ArrayList<Piece> local_piece_list, int xAxissAmount, int yAxissAmount, Piece[][] solution_table, int dimension_amount) {
		Piece[][] solved_table = new Piece[xAxissAmount][yAxissAmount];
		ArrayList<Integer> losowa_krawedz = new ArrayList<Integer>(Arrays.asList(0,1,2,3)); 
		HashMap<Integer, Integer> known_id = new HashMap<Integer, Integer>();
		
		Piece piece = local_piece_list.get(0);
		local_piece_list.remove(0);
		solved_table[piece.getXAxis()][piece.getYAxis()] = piece;
		known_id.put(piece.getId(), piece.getYAxis() * xAxissAmount + piece.getXAxis());
		
		
		int counter = 0, noweXY = -1;
		while(local_piece_list.size() > 0) {
			Collections.shuffle(losowa_krawedz);
			noweXY = -1;
		
		
			for (int u = 0; u < 4; u++)
				if(known_id.containsKey(local_piece_list.get(counter).getExplicitNeighbour(losowa_krawedz.get(u))))
				{
					piece = local_piece_list.get(counter);
					switch(losowa_krawedz.get(u)) {
						case 0:
							noweXY = known_id.get(piece.getExplicitNeighbour(losowa_krawedz.get(u))) + xAxissAmount;
							break;
						case 1:
							noweXY = known_id.get(piece.getExplicitNeighbour(losowa_krawedz.get(u))) - xAxissAmount;
							break;
						case 2:
							noweXY = known_id.get(piece.getExplicitNeighbour(losowa_krawedz.get(u))) + 1;
							break;
						case 3:
							noweXY = known_id.get(piece.getExplicitNeighbour(losowa_krawedz.get(u))) - 1;
							break;
					}
					break;
				}
			
			if(noweXY != -1) {
				System.out.println("nowy elemetn ["+noweXY % xAxissAmount+"]["+noweXY / xAxissAmount+"] = "+ piece.getId());
				solved_table[noweXY % xAxissAmount][noweXY / xAxissAmount] = piece;
				known_id.put(piece.getId(), noweXY);
				
				local_piece_list.remove(counter);
				counter--;
			}
	
			counter++;
			if(counter == local_piece_list.size()) counter = 0;
		}
			
		System.out.print("\nOtrzymano wynikow¹ sekwencjê: ");
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
			System.out.println("Sekwencje s¹ zgodne");
		else
			System.out.println("Pope³niono w procesie " + rezult + " b³êdów");
	}
	
	private static int SolvedTableCheck(Piece[][] solved_table, Piece[][] solution_table) {
		int error_counter = 0;
		if (solved_table.length != solution_table.length || solved_table[0].length != solution_table[0].length)
				throw new IllegalArgumentException("Sekwencja rozwi¹zania nie jest to¿sama z zadan¹ sekwencj¹ pod k¹tem wymiarów!");

		for(int x = 0; x < solution_table.length; x++)
			for(int y = 0; y < solution_table[0].length; y++) {
				if (solved_table[x][y] == null || solution_table[x][y].getId() != solved_table[x][y].getId())
					error_counter++;
			}
		return error_counter;
	}
}

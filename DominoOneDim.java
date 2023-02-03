import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DominoOneDim {
	public DominoOneDim(int xAxissAmount, int yAxissAmount, Piece[][] solution_table, int dimension_amount) {
		Piece[][] solved_table = new Piece[xAxissAmount][yAxissAmount];
		HashMap<Integer, ArrayList<Integer>> known_id = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<PieceSequence> vertical_subseq_set = new ArrayList<PieceSequence>();
		ArrayList<PieceSequence> horizontal_subseq_set = new ArrayList<PieceSequence>();
		ArrayList<PieceSequence> vertical_subseq_ready_set = new ArrayList<PieceSequence>();
		ArrayList<PieceSequence> horizontal_subseq_ready_set = new ArrayList<PieceSequence>();
		Piece[] subseq;
		
		for(int x = 0; x < xAxissAmount; x++)
			for(int y = 0; y < yAxissAmount; y++) {
				if(known_id.containsKey(solution_table[x][y].getId()))		// if piece ID already present on hashmap then add position to corresponding arraylist
					known_id.get(solution_table[x][y].getId()).add(x * yAxissAmount + y);
				else {		// if piece ID not present on hashmap then add mew mapping with ID as key and create new arraylist
					known_id.put(solution_table[x][y].getId(), new ArrayList<Integer>());
					known_id.get(solution_table[x][y].getId()).add(x * yAxissAmount + y);
				}
			}
		
		ArrayList<Integer> max_values = new ArrayList<Integer>();
		
		known_id.forEach((key, value) -> {
			System.out.print("\nklucz " + key + " wartosci: ");
			
			for(int i = 0; i < value.size(); i++)
				System.out.print(value.get(i) + " ");
			System.out.print("\n");
			int common_seq_length = 0;
			ArrayList<Integer> value_copy = new ArrayList<Integer>(value);

			//SimilarSeqLengthHoriz
			
			common_seq_length = SimilarSeqLengthHoriz(value_copy, xAxissAmount, yAxissAmount, solution_table);
			max_values.add(common_seq_length);
			
			//SimilarSeqLengthVert
			
			value_copy.clear();
			value_copy = new ArrayList<Integer>(value);
			common_seq_length = SimilarSeqLengthVert(value_copy, xAxissAmount, yAxissAmount, solution_table);
			max_values.add(common_seq_length + xAxissAmount + yAxissAmount);
			
		});

		
		int max_common_seq_vertical = 1, max_common_seq_horizontal = 1;
		for(int i = 0; i < max_values.size(); i++) {
			if (max_values.get(i) >= xAxissAmount + yAxissAmount && max_values.get(i) - (xAxissAmount + yAxissAmount) > max_common_seq_vertical)
				max_common_seq_vertical = max_values.get(i) - (xAxissAmount + yAxissAmount);
			if (max_values.get(i) < xAxissAmount + yAxissAmount && max_values.get(i) > max_common_seq_horizontal)
				max_common_seq_horizontal = max_values.get(i);
		}
			
		System.out.println("Znaleziono rozmiar maksymalnej powtorzonej podsekwencji w osi X: " + max_common_seq_horizontal + " oraz w osi Y: " + max_common_seq_vertical);

		if (max_common_seq_vertical < yAxissAmount) max_common_seq_vertical++;  //subsequences become deterministic when their length >= K + 2 and lesser than axiss length, K is the greatest repeated subsequence in sequence
		if (max_common_seq_horizontal < xAxissAmount) max_common_seq_horizontal++;
		if (max_common_seq_vertical < yAxissAmount) max_common_seq_vertical++;  
		if (max_common_seq_horizontal < xAxissAmount) max_common_seq_horizontal++;
		
		
		
		for(int y = 0; y < yAxissAmount; y++)
			for(int x = max_common_seq_horizontal - 1; x < xAxissAmount; x++) {
				subseq = new Piece[max_common_seq_horizontal];
				for(int i = 0; i < max_common_seq_horizontal; i++) {
					subseq[i] = solution_table[x - max_common_seq_horizontal + 1 + i][y];
				}
				horizontal_subseq_set.add(new PieceSequence(subseq, true));
			}
			
		if(dimension_amount == 2)
			for(int x = 0; x < xAxissAmount; x++)
				for(int y = max_common_seq_vertical - 1; y < yAxissAmount; y++) {
					subseq = new Piece[max_common_seq_vertical];
					for(int i = 0; i < max_common_seq_vertical; i++) {
						subseq[i] = solution_table[x][y - max_common_seq_vertical + 1 + i];
					}
					vertical_subseq_set.add(new PieceSequence(subseq, false));
				}
		
		System.out.println("Poczatkowa sekwencja zostala podzielona na : " + horizontal_subseq_set.size() + " elementow w osi X oraz :" + vertical_subseq_set.size()+ " elementow w osi Y");
		System.out.println("Podsekwencje poziome: ");
		
		for(int i=0; i < horizontal_subseq_set.size(); i++) {
			for(int j = 0; j < horizontal_subseq_set.get(i).getSequence().length; j++)
				System.out.print(" " + horizontal_subseq_set.get(i).getPiece(j).getId());
			System.out.print("\n");
		}
		System.out.println("Podsekwencje pionowe: ");
		for(int i=0; i < vertical_subseq_set.size(); i++) {
			for(int j = 0; j < vertical_subseq_set.get(i).getSequence().length; j++)
				System.out.print(" " + vertical_subseq_set.get(i).getPiece(j).getId());
			System.out.print("\n");
		}
			
		
		Collections.shuffle(horizontal_subseq_set);
		Collections.shuffle(vertical_subseq_set);
		
		PieceSequence chosen_sqe, matched_seq, new_seq;
		Piece[] piece_seq;
		boolean match_up;
		
		//horizontal sequence combining
		
		if(max_common_seq_horizontal < xAxissAmount)
			while (horizontal_subseq_set.size() > 0) {
				chosen_sqe = horizontal_subseq_set.get(0);
				horizontal_subseq_set.remove(0);
				for(int i = 0; i < horizontal_subseq_set.size(); i++) {
					matched_seq = horizontal_subseq_set.get(i);
					match_up = true;
					
					for(int j = 0; j < max_common_seq_horizontal - 1; j++) //backward check
						if (chosen_sqe.getPiece(j).getId() != matched_seq.getPiece(j + 1).getId()) {
							match_up = false;
							break;
						}
					
					if(match_up == true) {
			            //if both parts of subsequence matched, then proceed to create 
			            //new, bigger subsequence
						piece_seq = new Piece[chosen_sqe.getSequence().length + 1];
						piece_seq[0] = matched_seq.getPiece(0);
						for(int o = 0; o < chosen_sqe.getSequence().length; o++)
							piece_seq[o + 1] = chosen_sqe.getPiece(o);
						
						new_seq = new PieceSequence(piece_seq, true);
						horizontal_subseq_set.remove(i);
						i=-1;

						if(new_seq.getSequence().length == xAxissAmount) {
			                //if new subsequence has maximum possible length 
			                //then it's stored in separate subsequence set for
			                //next step
							horizontal_subseq_ready_set.add(new_seq);
							break;
						}
						else {
							chosen_sqe = new_seq;
							continue;
						}
					}
					
					
					match_up = true;
					for(int j = 0; j < max_common_seq_horizontal - 1; j++) //forward check
						if (chosen_sqe.getPiece(chosen_sqe.getSequence().length - j - 1).getId() != matched_seq.getPiece(matched_seq.getSequence().length - j - 2).getId()) {
							match_up = false;
							break;
						}
					if(match_up == true) {
			            //if both parts of subsequence matched, then proceed to create 
			            //new, bigger subsequence
						piece_seq = new Piece[chosen_sqe.getSequence().length + 1];
						
						piece_seq[chosen_sqe.getSequence().length] = matched_seq.getPiece(matched_seq.getSequence().length - 1);
						for(int o = 0; o < chosen_sqe.getSequence().length; o++) {
							piece_seq[o] = chosen_sqe.getPiece(o);
						}
	
						new_seq = new PieceSequence(piece_seq, true);
						horizontal_subseq_set.remove(i);
						i=-1;

						if(new_seq.getSequence().length == xAxissAmount) {
			                //if new subsequence has maximum possible length 
			                //then it's stored in separate subsequence set for
			                //next step
							horizontal_subseq_ready_set.add(new_seq);
							break;
						}
						else {
							chosen_sqe = new_seq;
							continue;
						}
					}
				}
				
			}
		
		//vertical sequence combining
		
		if(max_common_seq_vertical < yAxissAmount)
			while (vertical_subseq_set.size() > 0) {
				chosen_sqe = vertical_subseq_set.get(0);
				vertical_subseq_set.remove(0);
				for(int i = 0; i < vertical_subseq_set.size(); i++) {
					matched_seq = vertical_subseq_set.get(i);
					match_up = true;
					for(int j = 0; j < max_common_seq_vertical - 1; j++) //backward match check
						if (chosen_sqe.getPiece(j).getId() != matched_seq.getPiece(j + 1).getId()) {
							match_up = false;
							break;
						}
					
					if(match_up == true) {
			            //if both parts of subsequence matched, then proceed to create 
			            //new, bigger subsequence
						piece_seq = new Piece[chosen_sqe.getSequence().length + 1];
						piece_seq[0] = matched_seq.getPiece(0);
						for(int o = 0; o < chosen_sqe.getSequence().length; o++)
							piece_seq[o + 1] = chosen_sqe.getPiece(o);						
						new_seq = new PieceSequence(piece_seq, false);
						vertical_subseq_set.remove(i);
						i=-1;
						if(new_seq.getSequence().length == yAxissAmount) {
			                //if new subsequence has maximum possible length 
			                //then it's stored in separate subsequence set for
			                //next step
							vertical_subseq_ready_set.add(new_seq);
							break;
						}
						else {
							chosen_sqe = new_seq;
							continue;
						}
					}
					
					match_up = true;
					for(int j = 0; j < max_common_seq_vertical - 1; j++) // forward match check
						if (chosen_sqe.getPiece(chosen_sqe.getSequence().length - j - 1).getId() != matched_seq.getPiece(matched_seq.getSequence().length - j - 2).getId()) {
							match_up = false;
							break;
						}
					if(match_up == true) {
			            //if both parts of subsequence matched, then proceed to create 
			            //new, bigger subsequence
						piece_seq = new Piece[chosen_sqe.getSequence().length + 1];
						piece_seq[chosen_sqe.getSequence().length] = matched_seq.getPiece(matched_seq.getSequence().length - 1);
						for(int o = 0; o < chosen_sqe.getSequence().length; o++)
							piece_seq[o] = chosen_sqe.getPiece(o);						
						new_seq = new PieceSequence(piece_seq, false);
						vertical_subseq_set.remove(i);
						i=-1;
						if(new_seq.getSequence().length == yAxissAmount) {
			                //if new subsequence has maximum possible length 
			                //then it's stored in separate subsequence set for
			                //next step
							vertical_subseq_ready_set.add(new_seq);
							break;
						}
						else {
							chosen_sqe = new_seq;
							continue;
						}
					}
					
				}
				
			}
		
		System.out.println("Sekwencja zostala ulozone w : " + horizontal_subseq_ready_set.size() + " wierszy oraz :" + vertical_subseq_ready_set.size()+ " kolumn");
		System.out.println("Wiersze: ");
		
		for(int i = 0; i < horizontal_subseq_ready_set.size(); i++) {
			System.out.print("\nwiersz nr"+i+":");
			for(int j = 0; j < horizontal_subseq_ready_set.get(i).getSequence().length; j++)
				System.out.print(horizontal_subseq_ready_set.get(i).getPiece(j).getId());
		}
		for(int i = 0; i < vertical_subseq_ready_set.size(); i++) {
			System.out.print("\nkolumna nr"+i+":");
			for(int j = 0; j < vertical_subseq_ready_set.get(i).getSequence().length; j++)
				System.out.print(vertical_subseq_ready_set.get(i).getPiece(j).getId());
		}

		HashMap<Integer, ArrayList<Integer>> row_ids = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> column_ids = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> row_position_ids = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> column_position_ids = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> current_list, current_position_list;

		for(int i = 0; i < horizontal_subseq_ready_set.size(); i++) {
			current_list = new ArrayList<Integer>();
			current_position_list = new ArrayList<Integer>();
			for(int j = 0; j < vertical_subseq_ready_set.size(); j++) {
				current_list.add(horizontal_subseq_ready_set.get(i).getPiece(j).getId());
				current_position_list.add(vertical_subseq_ready_set.get(j).getPiece(i).getId());
			}
			Collections.sort(current_list);
			Collections.sort(current_position_list);
			row_ids.put(i, current_list);
			column_position_ids.put(i, current_position_list);
		}
			
		for(int i = 0; i < vertical_subseq_ready_set.size(); i++) {
			current_list = new ArrayList<Integer>();
			current_position_list = new ArrayList<Integer>();
			for(int j = 0; j < horizontal_subseq_ready_set.size(); j++) {
				current_list.add(vertical_subseq_ready_set.get(i).getPiece(j).getId());
				current_position_list.add(horizontal_subseq_ready_set.get(j).getPiece(i).getId());
			}
			Collections.sort(current_list);
			Collections.sort(current_position_list);
			column_ids.put(i, current_list);
			row_position_ids.put(i, current_position_list);
		}
		
		boolean checked_bool, unique_bool;
		//horizontal common pieces with vertical subsequence set position

			for(int i = 0; i < yAxissAmount; i++) {
				if(row_ids.keySet().contains(i)) {
					unique_bool = true;
					for(int j = 0; j < yAxissAmount; j++) {
						if(i != j && row_ids.keySet().contains(j) )
						{
							checked_bool = true;
							for(int k = 0; k < row_ids.get(i).size(); k++) {
								if(row_ids.get(i).get(k) != row_ids.get(j).get(k)) {
									checked_bool = false;
									break;
								}
							}
							if(checked_bool) {
								unique_bool = false;
								break;
							}	
						}
					}
					if(unique_bool) {
						for(int j = 0; j < yAxissAmount; j++) {
							if(column_position_ids.keySet().contains(j)) {
								checked_bool = true;
								for(int k = 0; k < column_position_ids.get(j).size(); k++) {
									if(row_ids.get(i).get(k) != column_position_ids.get(j).get(k)) {
										checked_bool = false;
										break;
									}
								}
								if(checked_bool) {
									for(int k = 0; k < horizontal_subseq_ready_set.get(j).getSequence().length; k++) {
										solved_table[k][j] = horizontal_subseq_ready_set.get(i).getPiece(k);
									}
									column_position_ids.remove(j);
									row_ids.remove(i);
									break;
								}	
							}	
						}	

					}
				}
			}

			
			//vertical common pieces with horizontal subsequence set position
			for(int i = 0; i < xAxissAmount; i++) {
				if(column_ids.keySet().contains(i)) {
					unique_bool = true;
					for(int j = 0; j < xAxissAmount; j++) {
						if(i != j && column_ids.keySet().contains(j) )
						{
							checked_bool = true;
							for(int k = 0; k < column_ids.get(i).size(); k++) {
								if(column_ids.get(i).get(k) != column_ids.get(j).get(k)) {
									checked_bool = false;
									break;
								}
							}
							if(checked_bool) {
								unique_bool = false;
								break;
							}	
						}
					}
					if(unique_bool) {
						for(int j = 0; j < xAxissAmount; j++) {
							if(row_position_ids.keySet().contains(j)) {
								checked_bool = true;
								for(int k = 0; k < row_position_ids.get(j).size(); k++) {
									if(column_ids.get(i).get(k) != row_position_ids.get(j).get(k)) {
										checked_bool = false;
										break;
									}
								}
								if(checked_bool) {
									for(int k = 0; k < vertical_subseq_ready_set.get(j).getSequence().length && solved_table[j][k] == null; k++) {
										solved_table[j][k] = vertical_subseq_ready_set.get(i).getPiece(k);
									}
									row_position_ids.remove(j);
									column_ids.remove(i);
									break;
								}	
							}	
						}	
					}
				}
			}
			
			
			//horizontal supplement
			for(int i = 0; i < horizontal_subseq_ready_set.size() && row_ids.containsKey(i); i++) {
				
				for (int j = 0; j < yAxissAmount; j++) {
					checked_bool = true;
					for (int k = 0; k < xAxissAmount; k++) {
						if(solved_table[k][j] != null && solved_table[k][j].getId() != horizontal_subseq_ready_set.get(i).getPiece(k).getId()) {
							checked_bool = false;
							break;
						}		
					}
					if (checked_bool) {
						for (int k = 0; k < xAxissAmount; k++)
							solved_table[k][j] = horizontal_subseq_ready_set.get(i).getPiece(k);
						
						horizontal_subseq_ready_set.remove(i);
						row_ids.remove(i);
						i--;
						break;
					}
					
				}
			}
		
			//vertical supplement
			
			for(int i = 0; i < vertical_subseq_ready_set.size() && column_ids.containsKey(i); i++) {
				for (int j = 0; j < xAxissAmount; j++) {
					checked_bool = true;
					for (int k = 0; k < yAxissAmount; k++) {
						if(solved_table[j][k] != null && solved_table[j][k].getId() != vertical_subseq_ready_set.get(i).getPiece(k).getId()) {
							checked_bool = false;
							break;
						}		
					}
					if (checked_bool) {
						for (int k = 0; k < yAxissAmount; k++)
							solved_table[j][k] = vertical_subseq_ready_set.get(i).getPiece(k);
						
						vertical_subseq_ready_set.remove(i);
						column_ids.remove(i);
						i--;
						break;
					}
					
				}
			}
		
		System.out.print("\nOtrzymano wynikowa sekwencje: ");
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
	
	private static int SimilarSeqLengthHoriz(ArrayList<Integer> positions, int xAxissAmount, int yAxissAmount, Piece[][] solution_table) {
		//transmission of elements set with same ID and with 
        //the same ID of predecessor element in subsequence
		ArrayList<Integer> next_positions = new ArrayList<Integer>(); 
		int max_counter = 0, counter = 0;
		
		for(int i = 0; i < positions.size(); i++) 
			if(positions.get(i) % xAxissAmount == xAxissAmount - 1) {
				//deletion of extreme seequence elements, which 
                //have no successors to be found
				positions.remove(i);
				i--;
			}
		
		while(positions.size() > 0) {
			// finding all successors ID values
			next_positions.clear();
			next_positions.add(positions.get(0) + 1);
			positions.remove(0);
				

			for(int i = 0; i < positions.size(); i++) {
				//grouping all successors with the same ID
				if(solution_table[(positions.get(i) + 1) % xAxissAmount][(positions.get(i) + 1) / xAxissAmount].getId() == solution_table[next_positions.get(0) % xAxissAmount][next_positions.get(0) / xAxissAmount].getId()) {
					next_positions.add(positions.get(i) + 1);
					positions.remove(i);
					i--;
				}
			}
			
			if(next_positions.size() > 1) {
		        //recursive invocation of function with new set 
		        //of successors as a parameter
				counter = SimilarSeqLengthHoriz(next_positions, xAxissAmount, yAxissAmount, solution_table);
				
				if(max_counter < counter) {
		            //counting maximum level of recurency which is 
		            //equal to longest repetition of subsequence
					max_counter = counter;
				}
			}
		}
		return max_counter + 1;
	}
	
	
	private static int SimilarSeqLengthVert(ArrayList<Integer> positions, int xAxissAmount, int yAxissAmount, Piece[][] solution_table) {	
		//transmission of elements set with same ID and with 
        //the same ID of predecessor element in subsequence
		ArrayList<Integer> next_positions = new ArrayList<Integer>(); 
		int max_counter = 0, counter = 0;
		
		for(int i = 0; i < positions.size(); i++)
			if(positions.get(i) / xAxissAmount == yAxissAmount - 1) {
				//deletion of extreme seequence elements, which 
                //have no successors to be found
				positions.remove(i);
				i--;
			}

		while(positions.size() > 0) {
			// finding all successors ID values
			next_positions.clear();
			next_positions.add(positions.get(0) + xAxissAmount);
			positions.remove(0);
				

			for(int i = 0; i < positions.size(); i++) {
				//grouping all successors with the same ID
				if(solution_table[(positions.get(i) + xAxissAmount) % xAxissAmount][(positions.get(i) + xAxissAmount) / xAxissAmount].getId() == solution_table[next_positions.get(0) % xAxissAmount][next_positions.get(0) / xAxissAmount].getId()) {
					next_positions.add(positions.get(i) + xAxissAmount);
					positions.remove(i);
					i--;
				}
			}

			if(next_positions.size() > 1) { 
		        //recursive invocation of function with new set 
		        //of successors as a parameter
				counter = SimilarSeqLengthVert(next_positions, xAxissAmount, yAxissAmount, solution_table);
				if(max_counter < counter) {
		            //counting maximum level of recurency which is 
		            //equal to longest repetition of subsequence
					max_counter = counter;
				}
			}
		}
		return max_counter + 1;
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

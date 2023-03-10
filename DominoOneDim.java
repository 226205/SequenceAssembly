import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

/* examples of seq files
01_same_element.txt
02_half.txt
03_indeterminate.txt
04_indeterminate.txt
05_single.txt
06_linear.txt
07_square.txt
08_square.txt
09_rectangular.txt
10_long_linear.txt
11_long_rectangle.txt
12_puzzle.txt
13_linear_puzzle.txt
14_long_linear_puzzle.txt
15_big_puzzle.txt
16_big_rectangle_puzzle.txt
17_big_dna_similar_rows.txt
18_big_dna.txt
*/

public class DominoOneDim {
	
	ArrayList<PieceSequence> vertical_subseq_set;
	ArrayList<PieceSequence> horizontal_subseq_set;
	ArrayList<PieceSequence> vertical_subseq_ready_set;
	ArrayList<PieceSequence> horizontal_subseq_ready_set;
	Piece[][] solved_table;
	static Timestamp algoritm_start_dt,  algoritm_end_dt;
	
	public DominoOneDim(int xAxissAmount, int yAxissAmount, Piece[][] solution_table, int dimension_amount, String file_name) {
		algoritm_start_dt = new Timestamp(System.currentTimeMillis());
		solved_table = new Piece[xAxissAmount][yAxissAmount];
		HashMap<Integer, ArrayList<Integer>> known_id = new HashMap<Integer, ArrayList<Integer>>();
		vertical_subseq_set = new ArrayList<PieceSequence>();
		horizontal_subseq_set = new ArrayList<PieceSequence>();
		vertical_subseq_ready_set = new ArrayList<PieceSequence>();
		horizontal_subseq_ready_set = new ArrayList<PieceSequence>();
		Piece[] subseq;
		
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");  
		LocalDateTime now = LocalDateTime.now();  
		String print_string = "";
		PrintWriter file = IOUtils.makeFileWriter("src/Domino_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt");
		
		System.out.println("\nAlgoritm execution started");
		System.out.println("Log created: src/Domino_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt");
		file.println("Running Domino Of One Dimension Subsequences. Started at " + dtf.format(now));
		file.println("Start sequence:\n");
		
		
		for(int i = 0; i < yAxissAmount; i++) {
			print_string = "";
			for(int j = 0; j < xAxissAmount; j++) {
				print_string = (print_string + solution_table[j][i].getId());				
				if(j + 1 != xAxissAmount)
					print_string = (print_string + ", ");
			}
			file.println(print_string);
		}
		
		file.println("\n");
		

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
		boolean check;
		for(int i = 0; i < max_values.size(); i++) {
			if (max_values.get(i) >= xAxissAmount + yAxissAmount && max_values.get(i) - (xAxissAmount + yAxissAmount) > max_common_seq_vertical)
				max_common_seq_vertical = max_values.get(i) - (xAxissAmount + yAxissAmount);
			if (max_values.get(i) < xAxissAmount + yAxissAmount && max_values.get(i) > max_common_seq_horizontal)
				max_common_seq_horizontal = max_values.get(i);
		}
			


		if (max_common_seq_vertical < yAxissAmount) max_common_seq_vertical++;  //subsequences become deterministic when their length >= K + 2 and lesser than axiss length, K is the greatest repeated subsequence in sequence
		if (max_common_seq_horizontal < xAxissAmount) max_common_seq_horizontal++;
		if (max_common_seq_vertical < yAxissAmount) max_common_seq_vertical++;  
		if (max_common_seq_horizontal < xAxissAmount) max_common_seq_horizontal++;
		
		check = false;
		
		while(check == false) {// check if pesymistic case
			for(int y = 0; y < yAxissAmount; y++)
				for(int x = max_common_seq_horizontal - 1; x < xAxissAmount; x++) {
					subseq = new Piece[max_common_seq_horizontal];
					for(int i = 0; i < max_common_seq_horizontal; i++) {
						subseq[i] = solution_table[x - max_common_seq_horizontal + 1 + i][y];
					}
					horizontal_subseq_set.add(new PieceSequence(subseq, true));
				}
			if(max_common_seq_horizontal == xAxissAmount)
				break;
			
			check = true;
			for(int i = 0; i < horizontal_subseq_set.size(); i++) {
				for(int j = i + 1; j < horizontal_subseq_set.size(); j++) {
					if(PieceSequence.isOtherIdSequenceOneDiffOnEdge(horizontal_subseq_set.get(i).getSequence(), horizontal_subseq_set.get(j).getSequence())) {
						
						check = false;
						horizontal_subseq_set.clear();
						max_common_seq_horizontal++;
						break;
					}
				}
				if(!check) {
					break;
				}
			}
		}
		check = false;
		
		if(dimension_amount == 2)
			while(check == false) {
				for(int x = 0; x < xAxissAmount; x++)
					for(int y = max_common_seq_vertical - 1; y < yAxissAmount; y++) {
						subseq = new Piece[max_common_seq_vertical];
						for(int i = 0; i < max_common_seq_vertical; i++) {
							subseq[i] = solution_table[x][y - max_common_seq_vertical + 1 + i];
						}
						vertical_subseq_set.add(new PieceSequence(subseq, false));
					}
				if(max_common_seq_vertical == yAxissAmount)
					break;
				
				check = true;
				for(int i = 0; i < vertical_subseq_set.size(); i++) {
					for(int j = i + 1; j < vertical_subseq_set.size(); j++) {
						if(PieceSequence.isOtherIdSequenceOneDiffOnEdge(vertical_subseq_set.get(i).getSequence(), vertical_subseq_set.get(j).getSequence())) {
							
							check = false;
							vertical_subseq_set.clear();
							max_common_seq_vertical++;
							break;
						}
					}
					if(!check) {
						break;
					}
				}
			}
		
		
		
		print_string = "\nFound size of the maximum repeated subsequence in x-axis: " + max_common_seq_horizontal;
		if (dimension_amount == 2) 
			print_string = print_string + " and in Y-axiss: " + max_common_seq_vertical;
		file.println(print_string);
		
		print_string = "The starting sequence was split into: " + horizontal_subseq_set.size() + " elements in the X axis";
		if(dimension_amount == 2)
			print_string = print_string + " and: " + vertical_subseq_set.size()+ " elements in the Y axis";
		file.println(print_string + "\nCreated horizontal subsequences:");
		
		for(int i=0; i < horizontal_subseq_set.size(); i++) {
			print_string = "(";
			for(int j = 0; j < horizontal_subseq_set.get(i).getSequence().length; j++) {
				print_string = (print_string + horizontal_subseq_set.get(i).getPiece(j).getId());
				if(j + 1 != horizontal_subseq_set.get(i).getSequence().length)
					print_string = (print_string + ", ");
				else
					print_string = (print_string + ")");
			}
			file.println(print_string);
		}
		
		if(dimension_amount == 2) {
			file.println("\nCreated vertical subsequences:");
			for(int i=0; i < vertical_subseq_set.size(); i++) {
				print_string = "(";
				for(int j = 0; j < vertical_subseq_set.get(i).getSequence().length; j++) {
					print_string = (print_string + vertical_subseq_set.get(i).getPiece(j).getId());
					if(j + 1 != vertical_subseq_set.get(i).getSequence().length)
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
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
		else
			horizontal_subseq_ready_set.addAll(horizontal_subseq_set);

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
		else
			vertical_subseq_ready_set.addAll(vertical_subseq_set);
		
		print_string = "\n\nThe subsequences were assembled into: " + horizontal_subseq_ready_set.size() + " completed rows";
		if(dimension_amount == 2) 
			print_string = print_string + " and: " + vertical_subseq_ready_set.size() + " completed columns";
		file.println(print_string + "\nAssembled rows:\n");
		
		for(int i = 0; i < horizontal_subseq_ready_set.size(); i++) {
			print_string = "(";
			for(int j = 0; j < horizontal_subseq_ready_set.get(i).getSequence().length; j++) {
				print_string = (print_string + horizontal_subseq_ready_set.get(i).getPiece(j).getId() + " ");
				if(j + 1 != horizontal_subseq_ready_set.get(i).getSequence().length)
					print_string = (print_string + ", ");
				else
					print_string = (print_string + ")");
			}
			file.println(print_string);
		}
		
		if(dimension_amount == 2) {
			
			file.println("\nAssembled columns:\n");
			for(int i = 0; i < vertical_subseq_ready_set.size(); i++) {
				print_string = "(";
				for(int j = 0; j < vertical_subseq_ready_set.get(i).getSequence().length; j++) {
					print_string = (print_string + vertical_subseq_ready_set.get(i).getPiece(j).getId() + " ");
					if(j + 1 != vertical_subseq_ready_set.get(i).getSequence().length)
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
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
		
		if(dimension_amount == 2) {
		
			file.println("\nOrdered set of elements for each row:\n");
			for(int i = 0; i < row_ids.size(); i++) {
				print_string = "(";
				for(int j = 0; j < row_ids.get(i).size(); j++) {
					print_string = (print_string + row_ids.get(i).get(j) + " ");
					if(j + 1 != row_ids.get(i).size())
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
			
			file.println("\nOrdered set of elements for each column:\n");
			for(int i = 0; i < column_ids.size(); i++) {
				print_string = "(";
				for(int j = 0; j < column_ids.get(i).size(); j++) {
					print_string = (print_string + column_ids.get(i).get(j));
					if(j + 1 != column_ids.get(i).size())
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
			
			file.println("\nOrdered set of elements for each row position:\n");
			for(int i = 0; i < row_position_ids.size(); i++) {
				print_string = ("Position " + i + ":  (");
				for(int j = 0; j < row_position_ids.get(i).size(); j++) {
					print_string = (print_string + row_position_ids.get(i).get(j) + " ");
					if(j + 1 != row_position_ids.get(i).size())
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
			
			file.println("\nOrdered set of elements for each column position:\n");
			for(int i = 0; i < column_position_ids.size(); i++) {
				print_string = ("Position " + i + ":  (");
				for(int j = 0; j < column_position_ids.get(i).size(); j++) {
					print_string = (print_string + column_position_ids.get(i).get(j) + " ");
					if(j + 1 != column_position_ids.get(i).size())
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
		}

		print_string = "";
		
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
							if(row_ids.get(i).get(k).intValue() != row_ids.get(j).get(k).intValue()) {
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
								if(row_ids.get(i).get(k).intValue() != column_position_ids.get(j).get(k).intValue()) {
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
			
		print_string = "";
		file.println("\nAll deterministic rows filled:\n");
		
		for(int i = 0; i < yAxissAmount; i++) {
			print_string = "(";
			for(int j = 0; j < xAxissAmount; j++) {
				if(solved_table[j][i] != null)
					print_string = (print_string + solution_table[j][i].getId());
				else
					print_string = (print_string + "_");
				
				if(j + 1 != xAxissAmount)
					print_string = (print_string + ", ");
				else
					print_string = (print_string + ")");
			}
			file.println(print_string);
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
								if(column_ids.get(i).get(k).intValue() != row_position_ids.get(j).get(k).intValue()) {
									checked_bool = false;
									break;
								}
							}

							if(checked_bool) {
								for(int k = 0; k < vertical_subseq_ready_set.get(j).getSequence().length; k++) {
									if(solved_table[j][k] == null)
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
			
		if(dimension_amount == 2) {
			print_string = "";
			file.println("\nAll deterministic collumn filled:\n");		
			
			for(int i = 0; i < yAxissAmount; i++) {
				print_string = "(";
				for(int j = 0; j < xAxissAmount; j++) {
					if(solved_table[j][i] != null)
						print_string = (print_string + solution_table[j][i].getId());
					else
						print_string = (print_string + "_");
					
					if(j + 1 != xAxissAmount)
						print_string = (print_string + ", ");
					else
						print_string = (print_string + ")");
				}
				file.println(print_string);
			}
		}
			
			
		//horizontal supplement
		for(int i = 0; i < horizontal_subseq_ready_set.size(); i++) {
			if(row_ids.containsKey(i));
				for (int j = 0; j < yAxissAmount; j++) 
					if(column_position_ids.containsKey(j)){
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
							
							column_position_ids.remove(j);
							row_ids.remove(i);
							break;
						}
						
					}
		}
		
		//vertical supplement
			
		for(int i = 0; i < vertical_subseq_ready_set.size(); i++) {
			if(column_ids.containsKey(i))
				for (int j = 0; j < xAxissAmount; j++)  
					if(row_position_ids.containsKey(j)){
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
							
							row_position_ids.remove(j);
							column_ids.remove(i);
							break;
						}
						
					}
		}
		
		print_string = "";
		file.println("\nNew, ordered sequence:\n");	
		
		for(int i = 0; i < yAxissAmount; i++) {
			print_string = "(";
			for(int j = 0; j < xAxissAmount; j++) {
				if(solved_table[j][i] != null)
					print_string = (print_string + solved_table[j][i].getId());
				else
					print_string = (print_string + "_");
				
				if(j + 1 != xAxissAmount)
					print_string = (print_string + ", ");
				else
					print_string = (print_string + ")");
			}
			file.println(print_string);
		}

		file.println("\n");
		algoritm_end_dt = new Timestamp(System.currentTimeMillis());
		
		int rezult = new ShowResults(solved_table, solution_table, "Domino", "src/Domino_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt", algoritm_end_dt.getTime() - algoritm_start_dt.getTime()).getErrorCount();
		if(rezult == 0)
			file.println("Received sequences are equal");
		else
			file.println("Received sequences differ in " + rezult + " places");

		file.println("Run finished at " + dtf.format(now));
		file.println("Algoritm was finished in " + (algoritm_end_dt.getTime() - algoritm_start_dt.getTime()) + "ms");
		
		file.close();
		
	}
	
	public static void main(String[] args) {
		
		String file_name;
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			if (args.length > 0)
				file_name = args[0];
			else {
				System.out.println("Type name of seqence file (without.txt extension): ");
				file_name = scanner.nextLine();
			}
			
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == -1){
				System.out.println("File src/"+ file_name + ".txt exists, but it can't be opened. Try using other file.");
				if (args.length == 0)
					continue;
				else
					System.exit(0);
			}
				
			
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == 0) {
				System.out.println("File with a given name: 'src/" + file_name + ".txt' does not exist, try again.");
				if (args.length == 0)
					continue;
				else
					System.exit(0);
			}
			break;
		}
		
		FileReader f_reader = new FileReader("src/"+ file_name + ".txt");
		
		int dimension_amount, xAxissAmount, yAxissAmount;
		Piece[][] solution_table;
		Timestamp start_dt = new Timestamp(System.currentTimeMillis()), end_dt = new Timestamp(System.currentTimeMillis());
		
		dimension_amount = f_reader.getDimensionAmount();
		xAxissAmount = f_reader.getXAxissAmount();
		yAxissAmount = f_reader.getYAxissAmount();
		solution_table = f_reader.getSolutionTable();
		
		if(f_reader.isAllPiecesSequence()) {

			new DominoOneDim(xAxissAmount, yAxissAmount, solution_table, dimension_amount, file_name);
			
			end_dt = new Timestamp(System.currentTimeMillis());
			long timestamps_diff = end_dt.getTime() - start_dt.getTime();
			
			System.out.println("\nExecution begin timestamp: " + start_dt
					+ "\nAlgoritm execution begin timestamp: " + algoritm_start_dt
					+ "\nAlgoritm execution finish timestamp: " + algoritm_end_dt
					+ "\nAlgoritm execution time: " + (algoritm_end_dt.getTime() - algoritm_start_dt.getTime()) + "ms"
					+ "\nExecution finish timestamp: " + end_dt
					+ "\nExecution time: " + timestamps_diff + "ms");
		
		}
		else
			System.out.println("File does not suffice requirements. The sequence is supposed to not be periodic in order to find shortest repetition!");
		scanner.close();
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
	
	public ArrayList<PieceSequence> getVerticalSubseqSet() { return vertical_subseq_set;}
	public ArrayList<PieceSequence> getHorizontalSubseqSet() { return horizontal_subseq_set;}
	public ArrayList<PieceSequence> getVerticalSubseqReadySet() { return vertical_subseq_ready_set;}
	public ArrayList<PieceSequence> getHorizontalSubseqReadySet() { return horizontal_subseq_ready_set;}
	public Piece[][] getSolvedTable() { return solved_table;}
}

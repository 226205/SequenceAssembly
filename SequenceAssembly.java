import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;


/*
 * 	Main class of program
 *  Contains all logic across methods to correctly display questions, results and get informations from user
 */


public class SequenceAssembly {

	static int dimension_amount, all_pieces_amount, xAxissAmount, yAxissAmount = 1, xAxissSequenceLength, yAxissSequenceLength = 1;
	static boolean all_pieces_unique, all_pieces_sequence, all_pieces_known;
	static Piece[][] solution_table;;

	final static String[] algoritm_names = { "Brute Force - plain puzzles", "Brute Force - known puzzles", "Domino - deterministic subseq 1D", "DNA - DeBrujin"};
	
	private static void s_menu(Scanner scanner) {
		
		
		String input, file_name;
		boolean preconfig = true;
		ArrayList<Piece> piece_list;
		
		//first menu of sequence possessing mode
		while(true){
			System.out.println("Choose operating mode:\n"
					+ "1: Load sequence file with predefined settings\n"
					+ "2: Set new setting and create sequences based on these\n"
					+ "0: Quit");
	
			input = scanner.nextLine();
			
			if (input.length() == 0 || input.matches("\\d+") == false || Arrays.asList(0, 1, 2).contains(Integer.parseInt(input)) == false) {
				System.out.println("\"" + input + "\" Option for a given number does not exist, try again.");
				continue;
			}
			break;
		}
		if (Integer.parseInt(input) == 0) System.exit(0);
		
		if (Integer.parseInt(input) == 2) 
		{
			preconfig = false;
			
			dimension_amount = intQuery(scanner, "Press 1 for 1D sequence, 2 for 2D sequence: ", 2);
			xAxissAmount = intQuery(scanner, "Enter the width of table with elements of seqence (possible input: natural numbers >= 1): ", Integer.MAX_VALUE);
			if (dimension_amount == 2) 
				yAxissAmount = intQuery(scanner, "Enter the length of table with elements of seqence (possible input: natural numbers >= 1): ", Integer.MAX_VALUE);
			all_pieces_unique = boolQuery(scanner, "Press 1 for unique elements, 2 for non-unique");
			if (all_pieces_unique == true)				
				all_pieces_known = boolQuery(scanner, "Press 1 for specified elements (is it known that in position [n][m] occurs X?), 2 for not specified:))");
			else all_pieces_known = true;
			all_pieces_sequence = boolQuery(scanner, "Press 1 for table containing only one sequence, 2 for table with many sequences: ");
			if (all_pieces_sequence == false)
				xAxissSequenceLength = intQuery(scanner, "Enter the width of entered sequence (possible input: natural numbers >= 1)", Integer.MAX_VALUE);
			else xAxissSequenceLength = xAxissAmount;
			if (all_pieces_sequence == false && dimension_amount == 2)
				yAxissSequenceLength = intQuery(scanner, "Enter the length of entered sequence (possible input: natural numbers >= 1)", Integer.MAX_VALUE);
			else yAxissSequenceLength = yAxissAmount;
			if (all_pieces_unique == false)
				all_pieces_amount = intQuery(scanner, "Enter the quantity of unique elements in sequence (possible input: natural numbers >= 1; sequence contains maximum " + xAxissSequenceLength * yAxissSequenceLength + " elements", xAxissSequenceLength * yAxissSequenceLength);
			else all_pieces_amount = xAxissSequenceLength * yAxissSequenceLength;

		}

		while(true) {
			System.out.println("Type name of seqence file (without.txt extension): ");
			file_name = scanner.nextLine();
			
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == -1){
				System.out.println("File already exists, but it can't be opened. Try using other file.");
				continue;
			}
				
			
			if(preconfig && IOUtils.fileExist("src/"+ file_name + ".txt") == 0) {
				System.out.println("File with a given name does not exist, try again.");
				continue;
			}

			if(!preconfig) {
				if(IOUtils.fileExist("src/"+ file_name + ".txt") == 1)
					while(true) {
						System.out.println("File already exists, overwrite? (yes - '1', no - '2')");
						input = scanner.nextLine();
						if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) == 1 ) {
							piece_list = s_WriteToFile("src/"+ file_name + ".txt");
							break;
						}
						else if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) == 2 ) 
							break;
						System.out.println("Option for a given number does not exist, try again.");
					}
				if (IOUtils.fileExist("src/"+ file_name + ".txt") == -1 ) {
					System.out.println("File can't be read. Try again.");
					continue;
				}
				else
					piece_list = s_WriteToFile("src/"+ file_name + ".txt");
			}
			else
				piece_list = s_ReadFromFile("src/"+ file_name + ".txt");			
					
			break;	
		}
		
		int repetition_amt = 1, algoritm_nbr = 0;
		
		
		
		/*	second menu with parameters to test the sequence: algorithm and number of repetitions - for testing purposes
		 */	
		
		for(int i = 0; i < yAxissAmount; i++) {
			System.out.println("");
			for(int j = 0; j < xAxissAmount; j++)
				System.out.print(" " + solution_table[j][i].getId());
		}
		
		while(true) {
			System.out.println("\nChoose algorithm and enter quantity of repetitions. Current values:"
					+ "\n1. Begin algorithm"
					+ "\n2. Change algorithm (currently chosen algorithm): " + algoritm_names[algoritm_nbr] + ")"
					+ "\n3. Change quantity of repetitions (current quantity of repetitions: " + repetition_amt +")"
					+ "\n0. Quit application.");
			input = scanner.nextLine();
			if (input.length() != 0 && input.matches("\\d+") && Arrays.asList(1, 2, 3, 0).contains(Integer.parseInt(input)) ) {
				switch (Integer.parseInt(input)) {
					case 1:
						execute_algoritm(repetition_amt, algoritm_nbr, piece_list, file_name);
						break;
					case 2:
						while(true) {
							System.out.println("Choose algorithm:");
							for (int i = 0; i < algoritm_names.length; i++)
								System.out.println((i+1) + ". " + algoritm_names[i]);
							System.out.println("0. Go back without saving");
							input = scanner.nextLine();
							if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) >= 0  && Integer.parseInt(input) <= algoritm_names.length) {
								if(Integer.parseInt(input) == 0) break;      //{ "Brute Force - common jigsaw puzzle", "Brute Force - known puzzle", "Domino - deterministic subseq 1D", "DNA - DeBrujin"};
								else if(Integer.parseInt(input) == 1 && all_pieces_unique == true) { algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Integer.parseInt(input) == 2 && all_pieces_known == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Arrays.asList(3).contains(Integer.parseInt(input)) && all_pieces_sequence == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Arrays.asList(4).contains(Integer.parseInt(input)) && all_pieces_sequence == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else System.out.println("Option for a given number does not exist with current parameters, try again. Previous choice is still the same.");
							}
							else
								System.out.println("Option for a given number does not exist, try again.");
						}
						break;
					case 3:
						while(true) {
							System.out.println("Enter the number of repetitions: (max 99, 0 to quit without change of setting)");
							input = scanner.nextLine();
							if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) > 0 && Integer.parseInt(input) <= 99) {
								repetition_amt = Integer.parseInt(input);
								break;
							}
							else if ((input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) == 0)
								break;
							else if ((input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) < 0 || (input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) > 99) {
								System.out.println("Entered value is outside of scope. Supported scope is from 1 to 99. Try again.");
								continue;
							}
							else
								System.out.println("Option for a given number does not exist, try again.");
						}
						break;
					case 0:
						System.exit(0);
				}
					
			}
			else
				System.out.println("Option for a given number does not exist, try again.");
		}
	
	}
	
	
	private static boolean boolQuery(Scanner scanner, String prnt_msg) {
		//Helper method to print questions and save boolean answers with additional input check
		boolean var;
		String input;
		
		while(true) {
			System.out.println(prnt_msg);
			input = scanner.nextLine();
			if (input.length() != 0 && input.matches("\\d+") && Arrays.asList(1, 2).contains(Integer.parseInt(input)) ) {
				if (Integer.parseInt(input) == 1) var = true;
				else var = false;
				break;
			}
			System.out.println("Option for a given number does not exist, try again.");
		}
		
		return var;
	}
	
	
	private static int intQuery(Scanner scanner, String prnt_msg, int max_var) {
		//Helper method to print questions and save integer answers with additional input check
		int var;
		String input;
		
		while(true) {
			System.out.println(prnt_msg);
			input = scanner.nextLine();
			if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) > 0 && Integer.parseInt(input) <= max_var) {
				var = Integer.parseInt(input);
				break;
			}
			else if ((input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) <= 0 || (input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) > max_var) {
				System.out.println("Entered value is outside of scope. Supported scope is from 1 to " + max_var + ". Try again.");
				continue;
			}
			System.out.println("Option for a given number does not exist, try again.");
		}
		return var;
	}
	
	
	@SuppressWarnings("unchecked")
	private static void execute_algoritm(int repetition_amt, int algoritm_nbr, ArrayList<Piece> piece_list, String file_name) {
		long[] execution_times = new long[repetition_amt];
		long timestamps_diff;
		Timestamp start_dt = new Timestamp(System.currentTimeMillis()), iteration_dt = new Timestamp(System.currentTimeMillis()), end_dt = new Timestamp(System.currentTimeMillis());
		ArrayList<Piece> local_piece_list;
		
		System.out.println("Starting");
		
		
		for (int i = 0; i < repetition_amt; i++) {
			
			switch (algoritm_nbr) {
				case 0: // "Brute Force - common jigsaw puzzle"
					local_piece_list = (ArrayList<Piece>) piece_list.clone();
					Collections.shuffle(local_piece_list);
					new UnknownBruteForce(local_piece_list, xAxissAmount, yAxissAmount, solution_table, dimension_amount);
					break;
				case 1: // "Brute Force - known puzzle"
					local_piece_list = (ArrayList<Piece>) piece_list.clone();
					Collections.shuffle(local_piece_list);
					new KnownBruteForce(local_piece_list, xAxissAmount, yAxissAmount, solution_table, dimension_amount);
					break;
				case 2: // "Domino - deterministic subseq 1D"
					new DominoOneDim(xAxissAmount, yAxissAmount, solution_table, dimension_amount);
					break;
				case 3: // "DNA - DeBrujin"
					new DNAdeBrujin(xAxissAmount, yAxissAmount, solution_table, dimension_amount);
					break;
			}
			end_dt = new Timestamp(System.currentTimeMillis());
			execution_times[i] = end_dt.getTime() - iteration_dt.getTime();
			iteration_dt = end_dt;
		}
		

		timestamps_diff = end_dt.getTime() - start_dt.getTime();
		
		System.out.println("Work finished. Algorithm used: " + algoritm_names[algoritm_nbr] + " based on file: " + file_name + ".txt"
				+ "\nBegin timestamp: " + start_dt
				+ "\nFinish timestamp: " + end_dt
				+ "\nExecution time: " + timestamps_diff + "ms");

	}

	
	private static ArrayList<Piece> s_ReadFromFile(String file_name) {
		int counter;
		String csv_line;
		IOUtils stdin = new IOUtils(file_name);
		int[] delimiter_index = new int[8];
		int[] id_neighbor = new int[4];
		int id, x, y;
		ArrayList<Piece> piece_list = new ArrayList<Piece>();
		csv_line = stdin.stringInputLine();
		
		try {	
			
			delimiter_index[0] = csv_line.indexOf(";");
			delimiter_index[1] = csv_line.indexOf(";", delimiter_index[0] + 1);
			delimiter_index[2] = csv_line.indexOf(";", delimiter_index[1] + 1);
			delimiter_index[3] = csv_line.indexOf(";", delimiter_index[2] + 1);
			delimiter_index[4] = csv_line.indexOf(";", delimiter_index[3] + 1);
			delimiter_index[5] = csv_line.indexOf(";", delimiter_index[4] + 1);
			delimiter_index[6] = csv_line.indexOf(";", delimiter_index[5] + 1);
			delimiter_index[7] = csv_line.indexOf(";", delimiter_index[6] + 1);
		
			dimension_amount = Integer.parseInt(csv_line.substring(0,delimiter_index[0]));
			all_pieces_amount = Integer.parseInt(csv_line.substring(delimiter_index[0]+1,delimiter_index[1]));
			xAxissAmount = Integer.parseInt(csv_line.substring(delimiter_index[1]+1,delimiter_index[2]));
			yAxissAmount = Integer.parseInt(csv_line.substring(delimiter_index[2]+1,delimiter_index[3]));
			xAxissSequenceLength = Integer.parseInt(csv_line.substring(delimiter_index[3]+1,delimiter_index[4]));
			yAxissSequenceLength = Integer.parseInt(csv_line.substring(delimiter_index[4]+1,delimiter_index[5]));
			all_pieces_unique = Boolean.parseBoolean(csv_line.substring(delimiter_index[5]+1,delimiter_index[6]));
			all_pieces_sequence = Boolean.parseBoolean(csv_line.substring(delimiter_index[6]+1,delimiter_index[7]));
			all_pieces_known = Boolean.parseBoolean(csv_line.substring(delimiter_index[7]+1));
			
		} catch (Exception e) {
			System.out.println("Unable to read parameters from file.");
			System.exit(0);
		}
		
		solution_table = new Piece[xAxissAmount][yAxissAmount];
			
		counter = 0;
		while(true) {
			csv_line = stdin.stringInputLine();

			try {
				
				delimiter_index[0] = csv_line.indexOf(";");
				delimiter_index[1] = csv_line.indexOf(";", delimiter_index[0] + 1);
				delimiter_index[2] = csv_line.indexOf(";", delimiter_index[1] + 1);
				delimiter_index[3] = csv_line.indexOf(";", delimiter_index[2] + 1);
				delimiter_index[4] = csv_line.indexOf(";", delimiter_index[3] + 1);
				delimiter_index[5] = csv_line.indexOf(";", delimiter_index[4] + 1);
				id = Integer.parseInt(csv_line.substring(0,delimiter_index[0]));
				x = Integer.parseInt(csv_line.substring(delimiter_index[0]+1,delimiter_index[1]));
				y = Integer.parseInt(csv_line.substring(delimiter_index[1]+1,delimiter_index[2]));
				id_neighbor[0] = Integer.parseInt(csv_line.substring(delimiter_index[2]+1,delimiter_index[3]));
				id_neighbor[1] = Integer.parseInt(csv_line.substring(delimiter_index[3]+1,delimiter_index[4]));
				id_neighbor[2] = Integer.parseInt(csv_line.substring(delimiter_index[4]+1,delimiter_index[5]));
				id_neighbor[3] = Integer.parseInt(csv_line.substring(delimiter_index[5]+1));

				solution_table[x][y] = new Piece(id, x, y, id_neighbor);
				piece_list.add(solution_table[x][y]);
				
			} catch (Exception e) {
				System.out.println("Loading objects from file failed. Loaded only " + piece_list.size() + " instead of " + xAxissAmount * yAxissAmount);
				System.exit(0);
			}

			
			counter++;
			if(xAxissAmount * yAxissAmount == counter)
				break;
			
			if(stdin.isEOF())
				break;
		}

		System.out.println("Successfully loaded file with seqence and parametrs.\n"
				+ "Loaded parameters:"
				+ "\n   Width of table = " + xAxissAmount
				+ "\n   Length of table = " + yAxissAmount
				+ "\n   Width of single sequence = " + xAxissSequenceLength
				+ "\n   Length of single sequence = " + yAxissSequenceLength
				+ "\n   Quantity of dimensions = " + dimension_amount
				+ "\n   Quantity of unique elements = " + all_pieces_amount
				+ "\n   Unique elements = " + all_pieces_unique
				+ "\n   Single sequence in table = " + all_pieces_sequence
				+ "\n   Known position of particular elements = " + all_pieces_known);
		
		try {
			stdin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return piece_list;
	}

	
	private static ArrayList<Piece> s_WriteToFile(String file_name) {

		
		ArrayList<Integer> randomId = new ArrayList<>();
		Random rand = new Random();
		PrintWriter seqFile = IOUtils.makeFileWriter(file_name);
		solution_table = new Piece[xAxissAmount][yAxissAmount];
		int[] id_neighbor = new int[4];
		ArrayList<Piece> piece_list = new ArrayList<Piece>();
		
		seqFile.println(dimension_amount + ";" + all_pieces_amount + ";" + xAxissAmount + ";" + yAxissAmount + ";" 
							+ xAxissSequenceLength + ";" + yAxissSequenceLength + ";" + all_pieces_unique + ";" 
							+ all_pieces_sequence + ";" + all_pieces_known);
		
		System.out.println(dimension_amount + ";" + all_pieces_amount + ";" + xAxissAmount + ";" + yAxissAmount + ";" 
				+ xAxissSequenceLength + ";" + yAxissSequenceLength + ";" + all_pieces_unique + ";" 
				+ all_pieces_sequence + ";" + all_pieces_known);
		
		for(int i = 1; i <= all_pieces_amount; i++)
			randomId.add(i);
		for(int i = all_pieces_amount; i < xAxissSequenceLength * yAxissSequenceLength; i++)
			randomId.add(rand.nextInt(all_pieces_amount) + 1);
		
		Collections.shuffle(randomId);
		
		
		for (int x = 0; x < xAxissAmount; x++)
			for (int y = 0; y < yAxissAmount; y++) {
				if(y == 0) 
					id_neighbor[0] = 0;
				else if (y % yAxissSequenceLength == 0) 
					id_neighbor[0] = randomId.get((yAxissSequenceLength - 1) * xAxissSequenceLength - (x % xAxissSequenceLength));
				else 
					id_neighbor[0] = randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength - xAxissSequenceLength);
				if(y == yAxissAmount - 1) 
					id_neighbor[1] = 0;
				else if (y % yAxissSequenceLength == yAxissSequenceLength - 1) 
					id_neighbor[1] = randomId.get(x % xAxissSequenceLength);
				else 
					id_neighbor[1] = randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength + xAxissSequenceLength);	
				
				if(x == 0) 
					id_neighbor[2] = 0;
				else if (x % xAxissSequenceLength == 0) 
					id_neighbor[2] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + xAxissSequenceLength - 1);
				else 
					id_neighbor[2] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength) - 1);
				if(x == xAxissAmount - 1) 
					id_neighbor[3] = 0;
				else if (x % xAxissSequenceLength == xAxissSequenceLength - 1) 
					id_neighbor[3] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength);
				else 
					id_neighbor[3] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength) + 1);
				
				solution_table[x][y] = new Piece(randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength), x, y, id_neighbor);
				
				seqFile.println(randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength)) + ";" + x + ";" + y + ";" 
									+ id_neighbor[0] + ";" + id_neighbor[1] + ";" + id_neighbor[2] + ";" + id_neighbor[3] );
			
				piece_list.add(solution_table[x][y]);
			}
		
		seqFile.close();
		return piece_list;
	}


	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		s_menu(scanner);
	}

}

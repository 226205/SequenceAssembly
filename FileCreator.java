import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class FileCreator {

	static int dimension_amount, all_pieces_amount, xAxissAmount, yAxissAmount = 1, xAxissSequenceLength, yAxissSequenceLength = 1;
	static boolean all_pieces_unique, all_pieces_sequence, all_pieces_known;
	static Piece[][] solution_table;
	
	public FileCreator() {
		Scanner scanner = new Scanner(System.in);
		String input, file_name;
		
		dimension_amount = intQuery(scanner, "Press 1 for 1D sequence, 2 for 2D sequence: ", 2);
		xAxissAmount = intQuery(scanner, "Enter the width of seqence (possible input: natural numbers >= 1): ", Integer.MAX_VALUE);
		if (dimension_amount == 2) 
			yAxissAmount = intQuery(scanner, "Enter the length of seqence (possible input: natural numbers >= 1): ", Integer.MAX_VALUE);
		all_pieces_unique = boolQuery(scanner, "Press 1 for unique elements, 2 for non-unique");
		if (all_pieces_unique == true)				
			all_pieces_known = boolQuery(scanner, "Press 1 for specified elements (is it known that in position [n][m] occurs X etement?), 2 for not specified:");
		else all_pieces_known = false;
		all_pieces_sequence = boolQuery(scanner, "Press 1 for plain sequence, 2 periodical sequence.");
		if (all_pieces_sequence == false)
			xAxissSequenceLength = intQuery(scanner, "Enter the width of entered sequence (possible input: natural numbers >= 1)", Integer.MAX_VALUE);
		else xAxissSequenceLength = xAxissAmount;
		if (all_pieces_sequence == false && dimension_amount == 2)
			yAxissSequenceLength = intQuery(scanner, "Enter the length of entered sequence (possible input: natural numbers >= 1)", Integer.MAX_VALUE);
		else yAxissSequenceLength = yAxissAmount;
		if (all_pieces_unique == false)
			all_pieces_amount = intQuery(scanner, "Enter the quantity of unique elements in sequence (possible input: natural numbers >= 1; sequence contains maximum of " + xAxissSequenceLength * yAxissSequenceLength + " elements", xAxissSequenceLength * yAxissSequenceLength);
		else all_pieces_amount = xAxissSequenceLength * yAxissSequenceLength;
		
		while(true) {
			System.out.println("Type name of seqence file (without.txt extension): ");
			file_name = scanner.nextLine();
			
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == -1){
				System.out.println("File already exists, but it can't be opened. Try using other file.");
				continue;
			}
		
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == 1)
				while(true) {
					System.out.println("File already exists, overwrite? (yes - '1', no - '2')");
					input = scanner.nextLine();
					if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) == 1 ) {
						s_WriteToFile("src/"+ file_name + ".txt");
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
				s_WriteToFile("src/"+ file_name + ".txt");
			
			break;
		}
		
		System.out.println("\nGenerated Sequence:");
		
		for(int i = 0; i < yAxissAmount; i++) {
			System.out.println("");
			for(int j = 0; j < xAxissAmount; j++)
				System.out.print(" " + solution_table[j][i].getId());
		}
		
		System.out.println("\nCreated sequence successfully saved to file: project_directory_path/src/" + file_name + ".txt" );
		
	}
	
	public static void main(String[] args) {
		new FileCreator();
	}
	
	private boolean boolQuery(Scanner scanner, String prnt_msg) {
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
	
	
	private int intQuery(Scanner scanner, String prnt_msg, int max_var) {
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
	
	private void s_WriteToFile(String file_name) {

		
		ArrayList<Integer> randomId = new ArrayList<>();
		Random rand = new Random();
		PrintWriter seqFile = IOUtils.makeFileWriter(file_name);
		solution_table = new Piece[xAxissAmount][yAxissAmount];
		int[] id_neighbor = new int[4];
		ArrayList<Piece> piece_list = new ArrayList<Piece>();
		
		seqFile.println(dimension_amount + ";" + all_pieces_amount + ";" + xAxissAmount + ";" + yAxissAmount + ";" 
							+ all_pieces_unique + ";" + all_pieces_sequence + ";" + all_pieces_known);
		
		
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
	}
}
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class UnknownBruteForce {
	
	Piece[][] solved_table;
	static Timestamp algoritm_start_dt,  algoritm_end_dt;
	
	public UnknownBruteForce(ArrayList<Piece> local_piece_list, int xAxissAmount, int yAxissAmount, Piece[][] solution_table, int dimension_amount, String file_name) {
		algoritm_start_dt = new Timestamp(System.currentTimeMillis());
		solved_table = new Piece[xAxissAmount][yAxissAmount];
		ArrayList<Integer> random_edge = new ArrayList<Integer>(Arrays.asList(0,1,2,3)); 
		HashMap<Integer, Integer> known_id = new HashMap<Integer, Integer>();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");  
		LocalDateTime now = LocalDateTime.now();  
		String print_string = "";
		PrintWriter file = IOUtils.makeFileWriter("src/PlainPuzzle_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt");
		
		System.out.println("\nAlgoritm execution started");
		System.out.println("Log created: src/PlainPuzzles_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt");
		file.println("Running PlainPuzzles. Started at " + dtf.format(now));
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
	
		
		Piece piece = local_piece_list.get(0);
		local_piece_list.remove(0);
		solved_table[piece.getXAxis()][piece.getYAxis()] = piece;
		known_id.put(piece.getId(), piece.getYAxis() * xAxissAmount + piece.getXAxis());
		
		
		int counter = 0, newXY = -1;
		while(local_piece_list.size() > 0) {
			Collections.shuffle(random_edge);
			newXY = -1;
		
		
			for (int u = 0; u < 4; u++)
				if(known_id.containsKey(local_piece_list.get(counter).getExplicitNeighbor(random_edge.get(u))))
				{
					piece = local_piece_list.get(counter);
					switch(random_edge.get(u)) {
						case 0:
							newXY = known_id.get(piece.getExplicitNeighbor(random_edge.get(u))) + xAxissAmount;
							break;
						case 1:
							newXY = known_id.get(piece.getExplicitNeighbor(random_edge.get(u))) - xAxissAmount;
							break;
						case 2:
							newXY = known_id.get(piece.getExplicitNeighbor(random_edge.get(u))) + 1;
							break;
						case 3:
							newXY = known_id.get(piece.getExplicitNeighbor(random_edge.get(u))) - 1;
							break;
					}
					break;
				}
			
			if(newXY != -1) {
				print_string = ("Solution for place in sequence ["+newXY % xAxissAmount+"]["+newXY / xAxissAmount+"] was found and is equal to " + piece.getId());
				if(piece.getId() != solution_table[newXY % xAxissAmount][newXY / xAxissAmount].getId())
					print_string = (print_string + " which differs from oryginal piece ID: " + solution_table[newXY % xAxissAmount][newXY / xAxissAmount].getId());
				file.println(print_string);

				solved_table[newXY % xAxissAmount][newXY / xAxissAmount] = piece;
				known_id.put(piece.getId(), newXY);
				
				local_piece_list.remove(counter);
				counter--;
			}
	
			counter++;
			if(counter == local_piece_list.size()) counter = 0;
		}
			
		print_string = "";
		file.println("\nNew, ordered sequence:\n");
		
		for(int i = 0; i < yAxissAmount; i++) {
			for(int j = 0; j < xAxissAmount; j++) {
				if(solved_table[j][i] != null)
					print_string = (print_string + solution_table[j][i].getId());
				else
					print_string = (print_string + "_");
				
				if(j+1 != xAxissAmount)
					print_string = (print_string + ", ");
			}
			file.println(print_string);
			print_string = "";
		}
		
		file.println("\n");
		algoritm_end_dt = new Timestamp(System.currentTimeMillis());
		
		int rezult = new ShowResults(solved_table, solution_table, "Plain Puzzle", "src/PlainPuzzle_" + file_name + "_" + dtf.format(now) + "_experiment_log.txt", algoritm_end_dt.getTime() - algoritm_start_dt.getTime()).getErrorCount();
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
		ArrayList<Piece> piece_list;
		Piece[][] solution_table;
		Timestamp start_dt = new Timestamp(System.currentTimeMillis()), end_dt = new Timestamp(System.currentTimeMillis());
		
		dimension_amount = f_reader.getDimensionAmount();
		xAxissAmount = f_reader.getXAxissAmount();
		yAxissAmount = f_reader.getYAxissAmount();
		solution_table = f_reader.getSolutionTable();
		piece_list = f_reader.getPieceList();
		
		ArrayList<Piece> local_piece_list = new ArrayList<Piece>();
		local_piece_list.addAll(piece_list);
		Collections.shuffle(local_piece_list);
		
		if(f_reader.isAllPiecesUnique()) {
			
			new UnknownBruteForce(local_piece_list, xAxissAmount, yAxissAmount, solution_table, dimension_amount, file_name);
			
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
			System.out.println("File does not suffice requirements. All elements are supposed to be unique!");
		scanner.close();
	}
	
	public Piece[][] getSolvedTable() { return solved_table;}
}

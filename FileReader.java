import java.io.IOException;
import java.util.ArrayList;

public class FileReader {
	
	int dimension_amount, xAxissAmount, yAxissAmount, all_pieces_amount;
	boolean all_pieces_unique, all_pieces_sequence, all_pieces_known;
	Piece[][] solution_table;
	ArrayList<Piece> piece_list = new ArrayList<Piece>();
	
	public FileReader(String file_name) {
		String csv_line;
		IOUtils stdin = new IOUtils(file_name);
		int[] delimiter_index = new int[6];
		int[] id_neighbor = new int[4];
		int id, x, y, counter;
		csv_line = stdin.stringInputLine();
		
		try {	
			
			delimiter_index[0] = csv_line.indexOf(";");
			delimiter_index[1] = csv_line.indexOf(";", delimiter_index[0] + 1);
			delimiter_index[2] = csv_line.indexOf(";", delimiter_index[1] + 1);
			delimiter_index[3] = csv_line.indexOf(";", delimiter_index[2] + 1);
			delimiter_index[4] = csv_line.indexOf(";", delimiter_index[3] + 1);
			delimiter_index[5] = csv_line.indexOf(";", delimiter_index[4] + 1);
		
			dimension_amount = Integer.parseInt(csv_line.substring(0,delimiter_index[0]));
			all_pieces_amount = Integer.parseInt(csv_line.substring(delimiter_index[0]+1,delimiter_index[1]));
			xAxissAmount = Integer.parseInt(csv_line.substring(delimiter_index[1]+1,delimiter_index[2]));
			yAxissAmount = Integer.parseInt(csv_line.substring(delimiter_index[2]+1,delimiter_index[3]));
			//xAxissSequenceLength = Integer.parseInt(csv_line.substring(delimiter_index[3]+1,delimiter_index[4]));
			//yAxissSequenceLength = Integer.parseInt(csv_line.substring(delimiter_index[4]+1,delimiter_index[5]));
			all_pieces_unique = Boolean.parseBoolean(csv_line.substring(delimiter_index[3]+1,delimiter_index[4]));
			all_pieces_sequence = Boolean.parseBoolean(csv_line.substring(delimiter_index[4]+1,delimiter_index[5]));
			all_pieces_known = Boolean.parseBoolean(csv_line.substring(delimiter_index[5]+1));
			
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
	}
	
	public int getDimensionAmount() {return dimension_amount; }
	public int getXAxissAmount() {return xAxissAmount; }
	public int getYAxissAmount() {return yAxissAmount; }
	public int getAllPiecesAmount() {return all_pieces_amount; }
	public Piece[][] getSolutionTable() {return solution_table; }
	public ArrayList<Piece> getPieceList() {return piece_list; }
	public boolean isAllPiecesUnique() {return all_pieces_unique; }
	public boolean isAllPiecesSequence() {return all_pieces_sequence; }
	public boolean isAllPiecesKnown() {return all_pieces_known; }

}

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

	final static String[] algoritm_names = { "Brute Force - zwykle puzzle", "Brute Force - znane puzzle", "Domino - deterministyczne subseq 1D", "DNA - DeBrujin"};
	
	private static void s_menu(Scanner scanner) {
		
		
		String input, file_name;
		boolean preconfig = true;
		ArrayList<Piece> piece_list;
		
		//first menu of sequence possessing mode
		while(true){
			System.out.println("Wybierz tryb pracy:\n"
					+ "1: Wczytaj plik sekwencji razem z ustawieniami\n"
					+ "2: Podaj nowe ustawienia i stwórz sekwencjê na ich podstawie\n"
					+ "0: Wylacz program");
	
			input = scanner.nextLine();
			
			if (input.length() == 0 || input.matches("\\d+") == false || Arrays.asList(0, 1, 2).contains(Integer.parseInt(input)) == false) {
				System.out.println("\"" + input + "\" opcja o tym numerze nie istnieje");
				continue;
			}
			break;
		}
		if (Integer.parseInt(input) == 0) System.exit(0);
		
		if (Integer.parseInt(input) == 2) 
		{
			preconfig = false;
			
			dimension_amount = intQuery(scanner, "Czy generowana sekwencja ma byæ 1d / 2d? (przyjmowane wartoœci: '1', '2')", 2);
			xAxissAmount = intQuery(scanner, "Podaj szerokoœæ ca³ej tablicy elementów sekwencji (przyjmowane wartoœci: liczby naturalne >= 1)", Integer.MAX_VALUE);
			if (dimension_amount == 2) 
				yAxissAmount = intQuery(scanner, "Podaj d³ugoœæ ca³ej tablicy elementów sekwencji (przyjmowane wartoœci: liczby naturalne >= 1)", Integer.MAX_VALUE);
			all_pieces_unique = boolQuery(scanner, "Czy elementy s¹ unikalne (nie mog¹ siê powtorzyæ)? (przyjmowane wartoœci: tak - '1', nie - '2')");
			if (all_pieces_unique == true)				
				all_pieces_known = boolQuery(scanner, "Czy poszczególne elementy sekwencji s¹ znane (posiadamy informacjê, ¿e na pozycji [n][m] wyst¹pi element X)? (przyjmowane wartoœci: tak - '1', nie - '2')");
			else all_pieces_known = true;
			all_pieces_sequence = boolQuery(scanner, "Czy ca³a tablica ma siê sk³adaæ z wy³¹cznie jednej sekwencji? (przyjmowane wartoœci: tak - '1', nie - '2')");
			if (all_pieces_sequence == false)
				xAxissSequenceLength = intQuery(scanner, "Podaj szerokoœæ pojedyñczej sekwencji (przyjmowane wartoœci: liczby naturalne >= 1)", Integer.MAX_VALUE);
			else xAxissSequenceLength = xAxissAmount;
			if (all_pieces_sequence == false && dimension_amount == 2)
				yAxissSequenceLength = intQuery(scanner, "Podaj d³ugoœæ pojedyñczej sekwencji (przyjmowane wartoœci: liczby naturalne >= 1)", Integer.MAX_VALUE);
			else yAxissSequenceLength = yAxissAmount;
			if (all_pieces_unique == false)
				all_pieces_amount = intQuery(scanner, "Podaj iloœæ unikalnych elementów w sekwencji (przyjmowane wartoœci: liczby naturalne >= 1; sekwencja zawiera maksymalnie " + xAxissSequenceLength * yAxissSequenceLength + " elementów", xAxissSequenceLength * yAxissSequenceLength);
			else all_pieces_amount = xAxissSequenceLength * yAxissSequenceLength;

		}

		while(true) {
			System.out.println("Podaj nazwê pliku sekwencji (bez rozszerzenia .txt): ");
			file_name = scanner.nextLine();
			
			if(IOUtils.fileExist("src/"+ file_name + ".txt") == -1){
				System.out.println("Plik istnieje, ale system nie mo¿e go odczytaæ! Spróbuj u¿yæ innego pliku.");
				continue;
			}
				
			
			if(preconfig && IOUtils.fileExist("src/"+ file_name + ".txt") == 0) {
				System.out.println("Plik o podanej nazwie nie istnieje, spróbuj ponownie!");
				continue;
			}

			if(!preconfig) {
				if(IOUtils.fileExist("src/"+ file_name + ".txt") == 1)
					while(true) {
						System.out.println("Plik o podanej nazwie ju¿ istnieje, czy nadpisaæ? (przyjmowane wartoœci: tak - '1', nie - '2')");
						input = scanner.nextLine();
						if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) == 1 ) {
							piece_list = s_WriteToFile("src/"+ file_name + ".txt");
							break;
						}
						else if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) == 2 ) 
							break;
						System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie!");
					}
				if (IOUtils.fileExist("src/"+ file_name + ".txt") == -1 ) {
					System.out.println("Plik niemo¿liwy do odczytu, wybierz ponownie!");
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
			System.out.println("\nMenu wyboru algorytmu oraz iloœci powtórzeñ. Aktualne wartoœci:"
					+ "\n1. Rozpocznij pracê"
					+ "\n2. Zmieñ algorytm (aktualnie wybrany algorytm: " + algoritm_names[algoritm_nbr] + ")"
					+ "\n3. Zmieñ iloœæ powtórzeñ (aktualna iloœæ powtórzeñ: " + repetition_amt +")"
					+ "\n0. Zakoñcz pracê aplikacji");
			input = scanner.nextLine();
			if (input.length() != 0 && input.matches("\\d+") && Arrays.asList(1, 2, 3, 0).contains(Integer.parseInt(input)) ) {
				switch (Integer.parseInt(input)) {
					case 1:
						execute_algoritm(repetition_amt, algoritm_nbr, piece_list, file_name);
						break;
					case 2:
						while(true) {
							System.out.println("Menu wyboru algorytmu:");
							for (int i = 0; i < algoritm_names.length; i++)
								System.out.println((i+1) + ". " + algoritm_names[i]);
							System.out.println("0. Cofnij bez zmian");
							input = scanner.nextLine();
							if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) >= 0  && Integer.parseInt(input) <= algoritm_names.length) {
								if(Integer.parseInt(input) == 0) break;      //{ "Brute Force - common jigsaw puzzle", "Brute Force - known puzzle", "Domino - deterministic subseq 1D", "DNA - DeBrujin"};
								else if(Integer.parseInt(input) == 1 && all_pieces_unique == true) { algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Integer.parseInt(input) == 2 && all_pieces_known == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Arrays.asList(3).contains(Integer.parseInt(input)) && all_pieces_sequence == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else if(Arrays.asList(4).contains(Integer.parseInt(input)) && all_pieces_sequence == true) {algoritm_nbr = Integer.parseInt(input) - 1; break;}
								else System.out.println("Opcja o tym numerze jest niedostêpna dla podanych parametrów sekwencji, wybierz inn¹! Poprzedni wybór nie zosta³ zmieniony.");
							}
							else
								System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie");
						}
						break;
					case 3:
						while(true) {
							System.out.println("Menu wyboru iloœci powtórzeñ: (0 Aby cofn¹æ bez zmian, max 99)");
							input = scanner.nextLine();
							if (input.length() != 0 && input.matches("\\d+") && Integer.parseInt(input) > 0 && Integer.parseInt(input) <= 99) {
								repetition_amt = Integer.parseInt(input);
								break;
							}
							else if ((input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) == 0)
								break;
							else if ((input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) < 0 || (input.length() != 0 && input.matches("\\d+")) && Integer.parseInt(input) > 99) {
								System.out.println("Wybrana wartoœæ nie mieœci siê w zakresie. Obs³ugiwany zakres to od 1 do 99. Spróbuj ponownie");
								continue;
							}
							else
								System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie!");
						}
						break;
					case 0:
						System.exit(0);
				}
					
			}
			else
				System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie!");
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
			System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie!");
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
				System.out.println("Wybrana wartoœæ nie mieœci siê w zakresie. Obs³ugiwany zakres to od 1 do " + max_var + ". Spróbuj ponownie");
				continue;
			}
			System.out.println("Opcja o tym numerze nie istnieje, spróbuj ponownie!");
		}
		return var;
	}
	
	
	@SuppressWarnings("unchecked")
	private static void execute_algoritm(int repetition_amt, int algoritm_nbr, ArrayList<Piece> piece_list, String file_name) {
		long[] execution_times = new long[repetition_amt];
		long timestamps_diff;
		Timestamp start_dt = new Timestamp(System.currentTimeMillis()), iteration_dt = new Timestamp(System.currentTimeMillis()), end_dt = new Timestamp(System.currentTimeMillis());
		ArrayList<Piece> local_piece_list;
		
		System.out.println("Rozpoczynam pracê");
		
		
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
		
		System.out.println("Zakoñczy³em pracê. Wykona³em algorytm: " + algoritm_names[algoritm_nbr] + " na podstawie pliku: " + file_name + ".txt"
				+ "\nCzas rozpoczêcia: " + start_dt
				+ "\nCzas zakoñczenia: " + end_dt
				+ "\n£¹czny czas wykonywania: " + timestamps_diff + "ms");

	}

	
	private static ArrayList<Piece> s_ReadFromFile(String file_name) {
		int counter;
		String csv_line;
		IOUtils stdin = new IOUtils(file_name);
		int[] delimiter_index = new int[8];
		int[] id_neighbour = new int[4];
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
			System.out.println("Nie uda³o siê odczytaæ parametrów z pliku");
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
				id_neighbour[0] = Integer.parseInt(csv_line.substring(delimiter_index[2]+1,delimiter_index[3]));
				id_neighbour[1] = Integer.parseInt(csv_line.substring(delimiter_index[3]+1,delimiter_index[4]));
				id_neighbour[2] = Integer.parseInt(csv_line.substring(delimiter_index[4]+1,delimiter_index[5]));
				id_neighbour[3] = Integer.parseInt(csv_line.substring(delimiter_index[5]+1));

				solution_table[x][y] = new Piece(id, x, y, id_neighbour);
				piece_list.add(solution_table[x][y]);
				
			} catch (Exception e) {
				System.out.println("Nie uda³o siê odczytaæ obiektów z pliku. Wczytano jedynie " + piece_list.size() + " zamiast " + xAxissAmount * yAxissAmount);
				System.exit(0);
			}

			
			counter++;
			if(xAxissAmount * yAxissAmount == counter)
				break;
			
			if(stdin.isEOF())
				break;
		}

		System.out.println("Pomyœlnie wczytano plik z sekwencj¹ i parametrami.\n"
				+ "Wczytane parametry:"
				+ "\n   Szerokoœæ tablicy = " + xAxissAmount
				+ "\n   D³ugoœæ tablicy = " + yAxissAmount
				+ "\n   Szerokoœæ pojedyñczej sekwencji = " + xAxissSequenceLength
				+ "\n   D³ugoœæ pojedyñczej sekwencji = " + yAxissSequenceLength
				+ "\n   Iloœæ wymiarów = " + dimension_amount
				+ "\n   Iloœæ unikalnych elementów = " + all_pieces_amount
				+ "\n   Brak powtórzenia elementów = " + all_pieces_unique
				+ "\n   Pojedyñcza sekwencja w tablicy = " + all_pieces_sequence
				+ "\n   Znana pozycja poszczególnych elementów = " + all_pieces_known);
		
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
		int[] id_neighbour = new int[4];
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
					id_neighbour[0] = 0;
				else if (y % yAxissSequenceLength == 0) 
					id_neighbour[0] = randomId.get((yAxissSequenceLength - 1) * xAxissSequenceLength - (x % xAxissSequenceLength));
				else 
					id_neighbour[0] = randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength - xAxissSequenceLength);
				if(y == yAxissAmount - 1) 
					id_neighbour[1] = 0;
				else if (y % yAxissSequenceLength == yAxissSequenceLength - 1) 
					id_neighbour[1] = randomId.get(x % xAxissSequenceLength);
				else 
					id_neighbour[1] = randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength + xAxissSequenceLength);	
				
				if(x == 0) 
					id_neighbour[2] = 0;
				else if (x % xAxissSequenceLength == 0) 
					id_neighbour[2] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + xAxissSequenceLength - 1);
				else 
					id_neighbour[2] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength) - 1);
				if(x == xAxissAmount - 1) 
					id_neighbour[3] = 0;
				else if (x % xAxissSequenceLength == xAxissSequenceLength - 1) 
					id_neighbour[3] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength);
				else 
					id_neighbour[3] = randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength) + 1);
				
				solution_table[x][y] = new Piece(randomId.get((x % xAxissSequenceLength) + (y % yAxissSequenceLength) * xAxissSequenceLength), x, y, id_neighbour);
				
				seqFile.println(randomId.get((y % yAxissSequenceLength) * xAxissSequenceLength + (x % xAxissSequenceLength)) + ";" + x + ";" + y + ";" 
									+ id_neighbour[0] + ";" + id_neighbour[1] + ";" + id_neighbour[2] + ";" + id_neighbour[3] );
			
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

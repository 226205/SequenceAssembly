import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnknownBruteForceTest {

	@Test
	void testFileReader() {
		FileReader f_reader = new FileReader("src/unique_puzzle.txt");

		Assertions.assertEquals(f_reader.getDimensionAmount(), 2);
		Assertions.assertEquals(f_reader.getXAxissAmount(),10);
		Assertions.assertEquals(f_reader.getYAxissAmount(),10);
		Assertions.assertEquals(f_reader.getAllPiecesAmount(),100);
		Assertions.assertEquals(f_reader.getSolutionTable().length,10);
		Assertions.assertEquals(f_reader.getSolutionTable()[0].length,10);
		Assertions.assertEquals(f_reader.getPieceList().size(),100);
		Assertions.assertTrue (f_reader.isAllPiecesUnique());
		Assertions.assertTrue (f_reader.isAllPiecesSequence());
		Assertions.assertTrue (f_reader.isAllPiecesKnown());
	}

	@Test
	void testKBFmainWrongFile() {
		String[] given_test_file = {"dna_test"};
		KnownBruteForce.main(given_test_file);
	}
	@Test
	void testKBFmain() {
		String[] given_test_file = {"unique_puzzle"};
		KnownBruteForce.main(given_test_file);
	}
	
	
	@Test
	void testDirect() {
		FileReader f_reader = new FileReader("src/unique_puzzle.txt");
		UnknownBruteForce ubf_solution = new UnknownBruteForce(f_reader.getPieceList(), f_reader.getXAxissAmount(), f_reader.getYAxissAmount(), f_reader.getSolutionTable(), f_reader.getDimensionAmount(), "unique_puzzle");
		
		Assertions.assertArrayEquals(ubf_solution.getSolvedTable(), f_reader.getSolutionTable());
	}

}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DNAdeBrujinTest {

	@Test
	void testFileReader() {
		FileReader f_reader = new FileReader("src/dna_test.txt");

		Assertions.assertEquals(f_reader.getDimensionAmount(), 2);
		Assertions.assertEquals(f_reader.getXAxissAmount(),16);
		Assertions.assertEquals(f_reader.getYAxissAmount(),16);
		Assertions.assertEquals(f_reader.getAllPiecesAmount(),4);
		Assertions.assertEquals(f_reader.getSolutionTable().length,16);
		Assertions.assertEquals(f_reader.getSolutionTable()[0].length,16);
		Assertions.assertEquals(f_reader.getPieceList().size(),256);
		Assertions.assertFalse (f_reader.isAllPiecesUnique());
		Assertions.assertTrue (f_reader.isAllPiecesSequence());
		Assertions.assertTrue (f_reader.isAllPiecesKnown());
	}

	@Test
	void testKBFmainWrongFile() {
		String[] given_test_file = {"dna_periodic"};
		DNAdeBrujin.main(given_test_file);
	}
	
	@Test
	void testKBFmain() {
		String[] given_test_file = {"dna_test"};
		DNAdeBrujin.main(given_test_file);
	}
	
	
	@Test
	void testDirect() {
		FileReader f_reader = new FileReader("src/dna_test.txt");
		DNAdeBrujin ddb_solution = new DNAdeBrujin(f_reader.getXAxissAmount(), f_reader.getYAxissAmount(), f_reader.getSolutionTable(), f_reader.getDimensionAmount(), "dna_test");
		
		Assertions.assertEquals(ddb_solution.getVerticalSubseqSet().size(), 0);
		Assertions.assertEquals(ddb_solution.getHorizontalSubseqSet().size(), 0);
		Assertions.assertEquals(ddb_solution.getVerticalSubseqReadySet().size(), f_reader.getYAxissAmount());
		Assertions.assertEquals(ddb_solution.getHorizontalSubseqReadySet().size(), f_reader.getXAxissAmount());
		
		for(int x = 0; x < f_reader.xAxissAmount; x++)
			for(int y = 0; y < f_reader.yAxissAmount; y++) 
				Assertions.assertEquals(ddb_solution.getSolvedTable()[x][y].getId(), f_reader.getSolutionTable()[x][y].getId());
	}

}

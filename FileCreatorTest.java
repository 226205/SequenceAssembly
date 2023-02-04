import org.junit.jupiter.api.Test;

class FileCreatorTest {
	
	// separate tests required due to System.exit() callout in FileCreator Class
    
	@Test
	void testPositive0() throws Exception{
		String[] params = {"test_name0", "14", "12", "4", "false", "false", "9", "9"} ;
		new FileCreator(params);
	}
	@Test
	void testPositive1() throws Exception{
		String[] params = {"test_name1", "14", "12", "4", "false", "false"} ;
		new FileCreator(params);
	}
	@Test
	void testPositive2() throws Exception{
		String[] params = {"test_name2", "14", "12", "168", "true", "false"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative0() {
		String[] params = {"test_name3", "14", "12", "4", "false", "false", "9"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative1() {
		String[] params = {"test_name4", "14", "12", "4", "false", "false", "9", "18"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative2() {
		String[] params = {"test_name5", "14", "12", "4", "false", "false", "18", "9"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative3() {
		String[] params = {"test_name6", "14", "12", "4", "false", "falsxxx"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative4() {
		String[] params = {"test_name7", "14", "12", "4", "falsxxx", "false"} ;
		new FileCreator(params);
	}
	@Test
	void testNegative5() {
		String[] params = {"test_name8", "14", "12", "4", "true", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative6() {
		String[] params = {"test_name9", "14", "12", "a", "false", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative7() {
		String[] params = {"test_name10", "14", "a", "4", "false", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative8() {
		String[] params = {"test_name11", "a", "12", "4", "false", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative9() {
		String[] params = {"test_name13", "14", "12", "0", "false", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative10() {
		String[] params = {"test_name14", "14", "0", "4", "false", "false"};
		new FileCreator(params);
	}
	@Test
	void testNegative11() {
		String[] params = {"test_name15", "0", "12", "4", "false", "false"};
		new FileCreator(params);
	}
}


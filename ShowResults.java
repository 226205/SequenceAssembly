import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class ShowResults {
	int error_counter;

	public ShowResults(Piece[][] solved_table, Piece[][] solution_table, String algoritm_name, String file_name, Long execution_time) {
		
		error_counter = 0;
		if (solved_table.length != solution_table.length || solved_table[0].length != solution_table[0].length)
			throw new IllegalArgumentException("Both sequences differs in dimention length!");

		for(int x = 0; x < solution_table.length; x++)
			for(int y = 0; y < solution_table[0].length; y++) {
				if (solved_table[x][y] == null || solution_table[x][y].getId() != solved_table[x][y].getId())
					error_counter++;
			}
		
		String[][] data1 = new String[solution_table[0].length][solution_table.length];
		String[][] data2 = new String[solution_table[0].length][solution_table.length];
		String[] header = new String[solution_table.length];
		
		for(int x = 0; x < solution_table.length; x++) {
			for(int y = 0; y < solution_table[0].length; y++) {
				data1[y][x] = Integer.toString(solution_table[x][y].getId());
				if(solved_table[x][y] != null)
					data2[y][x] = Integer.toString(solved_table[x][y].getId());
				else 
					data2[y][x] = null;
			}
			
		}
		for(int y = 0; y < solution_table.length; y++)
			header[y] = "";
		
		JLabel l1 = new JLabel("Starting Sequence"), l2 = new JLabel("Assembled Sequence");
		l1.setBounds(200, 25, 300, 25);
		l2.setBounds(835, 25, 300, 25);
		l1.setFont(new Font(l1.getFont().getName(), Font.PLAIN, 16));
		l2.setFont(new Font(l2.getFont().getName(), Font.PLAIN, 16));
		JTable jt1 = new JTable(data1, header);
		JTable jt2 = new JTable(data2, header);
		jt1.setTableHeader(null);
		jt2.setTableHeader(null);
		JTextArea ta = new JTextArea();
		
		if(error_counter == 0)
			ta.setText("Finished executing " + algoritm_name + " algoritm. \nReceived sequences are equal.\nAlgoritm executed in " + execution_time + "ms.\nExact partial results are available in log files in folder:\n    " + file_name);
		else
			ta.setText("Finished executing " + algoritm_name + " algoritm. \nReceived sequences differ in " + error_counter + " places.\nAlgoritm executed in " + execution_time + "ms.\nExact partial results are available in log files in folder:\n    " + file_name);

		ta.setEditable(false);
		ta.setBounds(430, 560, 420, 88);

		jt1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jt2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JScrollPane sp1 = new JScrollPane(jt1); 
		JScrollPane sp2 = new JScrollPane(jt2); 

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel1.add(sp1);
		panel2.add(sp2);
		panel1.setBounds(30,55,600,500);
		panel2.setBounds(650,55,600,500);
		
		JFrame frame = new JFrame();
		frame.setLayout(null);
		frame.setTitle(algoritm_name + " Algoritm");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280,720); 
		frame.setResizable(false);
		frame.add(panel1);
		frame.add(panel2);
		frame.add(ta);
		frame.add(l1);
		frame.add(l2);
		panel1.setVisible(true);
		panel2.setVisible(true);
		frame.setVisible(true);
		
		
	}
	
	public int getErrorCount() {return error_counter;}
}

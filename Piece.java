public class Piece {
	private int id; // id number
	private int x_axis; // X axiss position in start sequence
	private int y_axis; // Y axiss position in start sequence
	private int[] id_neighbor = new int[4]; // 4-elements table for neighbors' ID; ID = 0 if piece is on edge of sequence and has no neighbor on that side
	
	public Piece(int id, int x_axis, int y_axis, int[] id_neighbor) {
		this.id = id;
		this.x_axis = x_axis;
		this.y_axis = y_axis;
		this.id_neighbor[0] = id_neighbor[0]; // Up
		this.id_neighbor[1] = id_neighbor[1]; // Down
		this.id_neighbor[2] = id_neighbor[2]; // Left
		this.id_neighbor[3] = id_neighbor[3]; // Right
	}
	
	
	public int getId() {return id;}
	public int[] getIdNeighbor() {return id_neighbor;}
	public int getXAxis() {return x_axis;}
	public int getYAxis() {return y_axis;}
	public void setXAxis(int x_axis) {
		this.x_axis = x_axis;
	}
	public void setYAxis(int y_axis) {
		this.y_axis = y_axis;
	}
	public int getExplicitNeighbor(int site_id) {
		return id_neighbor[site_id];
	}
	
}


public class test {
	public static void main(String[] args) {
		int[] tt = {0,0,0,0};
		
		
		Piece[] t1 = {new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(1,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(2,0,0,tt)};
		Piece[] t2 = {new Piece(2,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(1,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(2,0,0,tt)};
		Piece[] r1 = {new Piece(1,0,0,tt) , new Piece(3,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(1,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(4,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(3,0,0,tt)};
		Piece[] r2 = {new Piece(1,0,0,tt) , new Piece(3,0,0,tt) , new Piece(4,0,0,tt) , new Piece(4,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(1,0,0,tt) , new Piece(2,0,0,tt) , new Piece(1,0,0,tt) , new Piece(1,0,0,tt) , new Piece(3,0,0,tt) , new Piece(1,0,0,tt) , new Piece(3,0,0,tt)};

		PieceSequence s1 = new PieceSequence(t1, true);
		PieceSequence s2 = new PieceSequence(t2, true);
		PieceSequence e1 = new PieceSequence(r1, true);
		PieceSequence e2 = new PieceSequence(r2, true);
		
		if(PieceSequence.isOtherIdSequenceOneDiffOnEdge(s1.getSequence(), s2.getSequence()))
			System.out.println("jedna ró¿nica");
		else
			System.out.println("inaczej");
		
		if(PieceSequence.isOtherIdSequenceOneDiffOnEdge(e1.getSequence(), e2.getSequence()))
			System.out.println("jedna ró¿nica");
		else
			System.out.println("inaczej");
	}
}

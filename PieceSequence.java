
public class PieceSequence {

	private Piece[] sequence_pieces;
	private Piece[] L_mer;
	private Piece[] R_mer;
	private boolean is_xAxissSeq;
	
	public PieceSequence(Piece[] sequence_pieces, boolean is_xAxissSeq) {
		this.sequence_pieces = sequence_pieces;
		this.is_xAxissSeq = is_xAxissSeq;
	}
	
	public PieceSequence(Piece[] sequence_pieces, boolean is_xAxissSeq, int max_common_seq) {
		this.sequence_pieces = sequence_pieces;
		this.is_xAxissSeq = is_xAxissSeq;
		this.L_mer = new Piece[max_common_seq];
		this.R_mer = new Piece[max_common_seq];
		if(max_common_seq <= sequence_pieces.length) {
			for(int i = 0; i < max_common_seq; i++) {
				this.L_mer[i] = this.sequence_pieces[i];
				this.R_mer[i] = this.sequence_pieces[sequence_pieces.length - max_common_seq + i];
			}
		}
	}
	
	public Piece[] getSequence() {return sequence_pieces;}
	public Piece[] getLmer() {return L_mer;}
	public Piece[] getRmer() {return R_mer;}
	public Piece getPiece(int piece_num) {return sequence_pieces[piece_num];}
	public boolean getIfxAxissSeq() {return is_xAxissSeq;}
	
	public boolean isOtherIdSequenceWithin(Piece[] checked_sequence) {
		int checked_iterator = 0;
		for(int i = 0; i < sequence_pieces.length; i++) {
			if(sequence_pieces[i].getId() == checked_sequence[checked_iterator].getId()) {
				checked_iterator++;
				if(checked_iterator == checked_sequence.length)
					return true;
			}
			else
				checked_iterator = 0;
		}
		return false;
	}

	static public boolean isOtherIdSequenceEqual(Piece[] oryginal_sequence, Piece[] checked_sequence) {
		if(oryginal_sequence.length == checked_sequence.length) {
			for(int i = 0; i < oryginal_sequence.length; i++) {
				if(oryginal_sequence[i].getId() != checked_sequence[i].getId())
					return false;
			}
		}
		else
			return false;
		return true;
	}
}
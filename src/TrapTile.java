import logic.Coordinate;
import logic.piece.Piece;

public class TrapTile extends Tile {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5565096767182305122L;
	
	public TrapTile(Coordinate location) {
		super(location);
	}
	public TrapTile(Coordinate location, String str) {
		super(location, str);
	}
	
	@Override
	public Piece placePiece(Piece toPlace) {
		//sets rank to 0, so anyone can capture this piece
		toPlace.setRank(0);
		return super.placePiece(toPlace);
	}

}

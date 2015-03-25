import logic.Coordinate;
import logic.piece.Piece;

public class DenTile extends Tile {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7096275788336313408L;

	private int side = 0;
	
	public DenTile(Coordinate location, int side) {
		super(location);
		this.side = side;
	}
	public DenTile(Coordinate location, String str, int side) {
		super(location, str);
		this.side = side;
	}
	
	public int getSide()
	{
		return side;
	}
	@Override
	public boolean canPieceEnter(Piece toPlace) {
		//pieces cannot enter there own Den
		if (toPlace.getSide() == side)
			return false;
		else
			return super.canPieceEnter(toPlace);
	}
	
	
	
}

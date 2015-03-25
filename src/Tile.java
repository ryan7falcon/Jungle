import logic.Coordinate;
import logic.piece.Piece;

public class Tile extends logic.tile.Tile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5228172723194722630L;

	public Tile(Coordinate location) {
		super(location);
		// TODO Auto-generated constructor stub
	}
	
	public Tile(Coordinate location, String s) {
		super(location, s);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Piece placePiece(Piece toPlace) {
		//if a swimming piece gets to the non-water tile, its not swimming.
		if ((this.getClass()!=WaterTile.class)&&
				((toPlace.getClass().getSuperclass() == SwimmingPiece.class)||
						(toPlace.getClass() == SwimmingPiece.class)))
			((SwimmingPiece) toPlace).swim(false);
		
		//if its not a trap, set usual rank
		if(this.getClass()!=TrapTile.class)
			toPlace.setRank(toPlace.getUsualRank());
		
		return super.placePiece(toPlace);
	}


}

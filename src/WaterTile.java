import logic.Coordinate;
import logic.piece.*;

public class WaterTile extends Tile 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3665057502491672797L;
	
	public WaterTile(Coordinate location) 
	{
		super(location);
	}
	public WaterTile(Coordinate location, String str) 
	{
		super(location, str);
	}
	
	@Override
	public boolean canPieceEnter(Piece p)
	{
		
		if (p!=null)	
		{
			//only swimming pieces can enter
			if ((p.getClass().getSuperclass() == SwimmingPiece.class)||(p.getClass() == SwimmingPiece.class))	
			{
				return true;
			}
			else
				return false;
		}
		else
			return false;
		
	}
	
	@Override
	public Piece placePiece(Piece toPlace) {
		//if a swimming piece gets to water tile, its swimming
		if ((toPlace.getClass().getSuperclass() == SwimmingPiece.class)||(toPlace.getClass() == SwimmingPiece.class))
			((SwimmingPiece) toPlace).swim(true);
		
		return super.placePiece(toPlace);
	}
	
	@Override
	public boolean canJumpOver(Piece piece) {
		//If it is a jumping piece
		if (piece.getClass() == JumpingPiece.class)
			//and there are no animals in the water straight ahead
			if (this.getPlacedPiece()==null)
				//jump across the water
				return true;
			else
				return false;
		else
			return super.canJumpOver(piece);
	}
	
	
	
}
import logic.ConstantValues;
import logic.piece.*;
public class StrongestPiece extends StandardJunglePiece{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3317502748763073549L;

	public StrongestPiece(int side, int speed, int rank, String name) {
		super(side, speed, rank, name);
		// TODO Auto-generated constructor stub
	}
	
	//An elephant can't eat a rat
	@Override
	public boolean canTakePiece(Piece toTake)
	{
		if (toTake!=null)
		{
			if ((toTake.getRank() == ConstantValues.WEAKEST_PIECE_RANK) && (toTake.getSide() == getSide()*(-1)) )
			{
				return false;
			}
			else
				return super.canTakePiece(toTake);
		}		
		else
			return super.canTakePiece(toTake);
	}

}

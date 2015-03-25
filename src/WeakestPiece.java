import logic.ConstantValues;
import logic.piece.*;


public class WeakestPiece extends SwimmingPiece {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1822390110559602486L;

	public WeakestPiece(int side, int speed, int rank, String name) {
		super(side, speed, rank, name);
	}

	
	@Override
	public boolean canTakePiece(Piece toTake)
	{
		if (toTake!=null)
		{
			//A rat can eat an elephant
			if ((toTake.getRank() == ConstantValues.STRONGEST_PIECE_RANK) && (toTake.getSide() == getSide()*(-1)) )
			{
				if (this.isSwimming())
					return false;
				else
					return true;
			}
			else
			//If its not an Elephant, check super
			{
				return  super.canTakePiece(toTake);
			}
		}		
		else
			return  super.canTakePiece(toTake);
	}
}

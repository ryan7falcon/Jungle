import logic.piece.*;
public class SwimmingPiece extends StandardJunglePiece{

	/**
	 * 
	 */
	
	private boolean Swimming = false;
	private static final long serialVersionUID = 3105991292718861242L;
	
	public SwimmingPiece(int side, int speed, int rank, String name) {
		super(side, speed, rank, name);
	}

	@Override
	public boolean canTakePiece(Piece toTake)
	{
		if (toTake!=null)
		{
			if (toTake.getClass().getSuperclass()==SwimmingPiece.class || toTake.getClass()==SwimmingPiece.class)
			{
				if (this.isSwimming()&&(!((SwimmingPiece) toTake).isSwimming()))
					return false;
				else
					return super.canTakePiece(toTake);
			}
			else
			{
				if (this.isSwimming())
					return false;
				else
					return super.canTakePiece(toTake);
			}
		}
		else return super.canTakePiece(toTake);
			
	}
	
	//check if is in water
	public boolean isSwimming()
	{
		return Swimming;
	}
	
	//sets swimming state
	public void swim(boolean b)
	{
		Swimming = b;
	}
	
}

import java.util.ArrayList;
import java.util.Iterator;

import logic.*;
import logic.piece.*;
public class Game extends logic.Game
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8395759457708163217L;
	
	
	@Override
	public void initBoard() 
	{
		
		//A list of tiles that represents Game board
		Tile[][] list = new Tile[9][7];
		
		//Filling list in
		for (int i = 0; i < 9; i++)
			for (int j = 0; j< 7; j++)
			{
				//the Den
				if ((i == 0) && (j == 3)) //black den
				{
					list[i][j] = new DenTile(new Coordinate(i, j), ConstantValues.BLACK_SIDE);
					list[i][j].setName("Den");
				}
				else
					if ((i == 8) && (j == 3))//red den
					{
						list[i][j] = new DenTile(new Coordinate(i, j), ConstantValues.RED_SIDE);
						list[i][j].setName("Den");
					}
					else
					{
						//Water
						if ( ((i > 2) && (i < 6)) && ( ((j > 0) && (j < 3)) || ((j > 3) && (j < 6)) ) )
						{
							list[i][j] = new WaterTile(new Coordinate(i, j));
							list[i][j].setName("Water");
						}
						else
						{
							//Trap
							if ( (((i == 0) || (i == 8)) && ((j == 2) || (j == 4))) || (((i == 1) || (i == 7)) && (j == 3)) )
							{
								list[i][j] = new TrapTile(new Coordinate(i, j));
								list[i][j].setName("Trap");
							}
							//normal tile
							else
							{
								list[i][j] = new Tile(new Coordinate(i, j));
								list[i][j].setName("Tile");
							}
						}
					}
					
			}
		//setting up game board based on our list
		_gameBoard = new GameBoard (list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initPieces() 
	{
		
		_blackPieces = new ArrayList<Piece>();
		_redPieces = new ArrayList<Piece>();
		
		//Black	
		
		Tile t = (Tile) getTile(2,6);
		StandardJunglePiece elephant_b = new StrongestPiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_ELEPHANT + 1, ConstantValues.GAMEPIECE_ELEPHANT + 1, "elephant");
		t.placePiece(elephant_b);
		_blackPieces.add(elephant_b);
		
		t = (Tile) getTile(0,0);
		//t = (Tile) getTile(6,2);
		StandardJunglePiece lion_b = new JumpingPiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_LION + 1, ConstantValues.GAMEPIECE_LION + 1, "Lion");
		t.placePiece(lion_b);
		_blackPieces.add(lion_b);
		
		t = (Tile) getTile(0,6);
		StandardJunglePiece tiger_b = new JumpingPiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_TIGER + 1, ConstantValues.GAMEPIECE_TIGER + 1, "tiger");
		t.placePiece(tiger_b);
		_blackPieces.add(tiger_b);
		
		t = (Tile) getTile(2,2);
		//t = (Tile) getTile(2,1);
		StandardJunglePiece leopard_b = new StandardJunglePiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_LEOPARD + 1, ConstantValues.GAMEPIECE_LEOPARD + 1, "Leopard");
		t.placePiece(leopard_b);
		_blackPieces.add(leopard_b);		
		
		t = (Tile) getTile(2,4);
		//t = (Tile) getTile(2,5);
		StandardJunglePiece wolf_b = new StandardJunglePiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_WOLF + 1, ConstantValues.GAMEPIECE_WOLF + 1, "wolf");
		t.placePiece(wolf_b);
		_blackPieces.add(wolf_b);
		
		
		t = (Tile) getTile(1,1);
		StandardJunglePiece dog_b = new StandardJunglePiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_DOG + 1, ConstantValues.GAMEPIECE_DOG + 1, "Dog");
		t.placePiece(dog_b);
		_blackPieces.add(dog_b);
		
		t = (Tile) getTile(1,5);
		StandardJunglePiece cat_b = new StandardJunglePiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_CAT + 1, ConstantValues.GAMEPIECE_CAT + 1, "cat");
		t.placePiece(cat_b);
		_blackPieces.add(cat_b);
		
		 t = (Tile) getTile(2,0);
		//Tile t = (Tile) getTile(6,5); //take test
		//t = (Tile) getTile(7,4); //den test
		//Tile t = (Tile) getTile(1,3); //flee test
		//t = (Tile) getTile(6,0); //defence test
		//t = (Tile) getTile(3,0);
		StandardJunglePiece rat_b = new WeakestPiece(ConstantValues.BLACK_SIDE, ConstantValues.GAMEPIECE_RAT + 1, ConstantValues.GAMEPIECE_RAT + 1, "Rat");
		t.placePiece(rat_b);
		_blackPieces.add(rat_b);
		
		//Red
		
		t = (Tile) getTile(6,0);
		StandardJunglePiece elephant_r = new StrongestPiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_ELEPHANT + 1, ConstantValues.GAMEPIECE_ELEPHANT + 1, "elephant");
		t.placePiece(elephant_r);
		_redPieces.add(elephant_r);
		
		t = (Tile) getTile(8,6);
		/*t = (Tile) getTile(4,0);*/
		StandardJunglePiece lion_r = new JumpingPiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_LION + 1, ConstantValues.GAMEPIECE_LION + 1, "Lion");
		t.placePiece(lion_r);
		_redPieces.add(lion_r);
		
		t = (Tile) getTile(8,0);
		//t = (Tile) getTile(3,3);
		StandardJunglePiece tiger_r = new JumpingPiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_TIGER + 1, ConstantValues.GAMEPIECE_TIGER + 1, "tiger");
		t.placePiece(tiger_r);
		_redPieces.add(tiger_r);
		
		t = (Tile) getTile(6,4);
		StandardJunglePiece leopard_r = new StandardJunglePiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_LEOPARD + 1, ConstantValues.GAMEPIECE_LEOPARD + 1, "Leopard");
		t.placePiece(leopard_r);
		_redPieces.add(leopard_r);
		
		t = (Tile) getTile(6,2);
		StandardJunglePiece wolf_r = new StandardJunglePiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_WOLF + 1, ConstantValues.GAMEPIECE_WOLF + 1, "wolf");
		t.placePiece(wolf_r);
		_redPieces.add(wolf_r);
		
		t = (Tile) getTile(7,5);
		StandardJunglePiece dog_r = new StandardJunglePiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_DOG + 1, ConstantValues.GAMEPIECE_DOG + 1, "Dog");
		t.placePiece(dog_r);
		_redPieces.add(dog_r);
		
		t = (Tile) getTile(7,1);
		//t = (Tile) getTile(7,2);
		StandardJunglePiece cat_r = new StandardJunglePiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_CAT + 1, ConstantValues.GAMEPIECE_CAT + 1, "cat");
		t.placePiece(cat_r);
		_redPieces.add(cat_r);
		
		//t = (Tile) getTile(2,3);
		t = (Tile) getTile(6,6);
		StandardJunglePiece rat_r = new WeakestPiece(ConstantValues.RED_SIDE, ConstantValues.GAMEPIECE_RAT + 1, ConstantValues.GAMEPIECE_RAT + 1, "Rat");
		t.placePiece(rat_r);
		_redPieces.add(rat_r);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int whoWon() 
	{
		//if there are no black pieces left, red wins
		if (getBlackPieces().isEmpty())
			return ConstantValues.RED_SIDE;
		
		//if there are no red pieces left, black wins
		if (getRedPieces().isEmpty())
			return ConstantValues.BLACK_SIDE;
			
		//If black den is captured by red side, red wins
		Piece test = this._gameBoard.getTile(0, 3).getPlacedPiece();
		if (test!=null)
		{
			if (test.getSide() == ConstantValues.RED_SIDE)
				return ConstantValues.RED_SIDE;
		}	
		
		//If red den is captured by black side, black wins
		test = this._gameBoard.getTile(8, 3).getPlacedPiece();
		if (test!=null)
		{		
			if (test.getSide() == ConstantValues.BLACK_SIDE)
				return ConstantValues.BLACK_SIDE;	
		}
		
		//check for no moves
		Iterator <Piece> i = getRedPieces().iterator();
		//check if red has no moves
		boolean win = true;
		while (i.hasNext())
		{
			Piece next = i.next();
			if (getValidMoves(next.getLocation().getRow(), next.getLocation().getColumn()) != null)
			{
				win = false;
				break;
			}
		}
		//then black wins
		if (win == true)
			return ConstantValues.BLACK_SIDE;
		
		//check if black has no moves
		i = getBlackPieces().iterator();
		win = true;
		while (i.hasNext())
		{
			Piece next = i.next();
			if (getValidMoves(next.getLocation().getRow(), next.getLocation().getColumn()) != null)
			{
				win = false;
				break;
			}	
		}
		//then red wins		
		if (win == true)
			return ConstantValues.RED_SIDE;
		
		//otherwise, nobody wins yet
		return ConstantValues.NO_SIDE;
	}

	
	
	
	
}

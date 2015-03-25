//Class: Situation
//This class analyses all possible moves for a given piece and chooses a best tile to move it
//
import java.util.ArrayList;
import java.util.Iterator;

import logic.ConstantValues;
import logic.GameBoard;
import logic.piece.Piece;

public class Situation {

	//game board
	private GameBoard _board;
	
	//analysed piece, its tile and valid moves
	private Tile _t;
	private Piece _p;
	private ArrayList<Tile> _validMoves = new ArrayList<Tile>();
	
	//all pieces on the board from friendly side
	private ArrayList<Piece> _friends = new ArrayList<Piece>();
	//all pieces on the board from enemy side
	private ArrayList<Piece> _enemies = new ArrayList<Piece>();
	
	//winning tile to move
	private Tile _winTile = null;
	//estimated best tiles to move
	private ArrayList<Tile> _bestTiles = new ArrayList<Tile>();
	//The best tile to move (equals winning tile in case of win)
	private Tile _bestTile = null;

	//max gain over all possible moves
	private int _maxGain = -1000;
	
	//tactics constants
	private static final double POTENTIAL = 1.5;
	private static final int JUMPING_BONUS = 4;
	private static final int DEFENDER_BONUS = 2;
	private static final int SHIFT = 5;
	//Strategy constants
	private static final int GAIN_RETREAT = -1;
	private static final int GAIN_MOVE_TO_SIDE = 0;
	private static final int GAIN_MOVE_TO_CENTER = 1;
	private static final int GAIN_MOVE_FORWARD = 2;
	private static final int GAIN_GUARDING_POSITION = 4;
	private static final int GAIN_MOVE_TO_GUARD = 3;
	
	private static final int FORWARD = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static final int FORWARD_LEFT = 4;
	private static final int FORWARD_RIGHT = 5;
	private static final int LEFT_FORWARD = 6;
	private static final int RIGHT_FORWARD = 7;
	
	@SuppressWarnings("unchecked")
	public Situation(GameBoard board, Piece p, ArrayList<Piece> friends, ArrayList<Piece> enemies)
	{
		_board = board;
		_p = p;
		_t = (Tile) _board.getTile(_p.getLocation().getRow(),  _p.getLocation().getColumn());
		_validMoves = board.getValidMoves(_t.getLocation().getRow(), _t.getLocation().getColumn());
		_friends = friends;
		_enemies = enemies;
		init();
	}
	
	//calculates best tile to move
	private void init()
	{
		//tactics: Choose all valuable moves, add them to _bestTiles list
		
		//if there are valid moves
		if(_validMoves!=null)
			{
			//for all valid moves
			for (Iterator<Tile> i =_validMoves.iterator(); i.hasNext();)
			{
				//select next tile to move
				Tile nextTile = i.next();
				
				//if its an opponent's den, win
				if (nextTile.getName() == "Den")
				{
					_winTile = nextTile;
					_bestTiles.add(_winTile);
					_maxGain = 1000;
					break;
				}
				
				//max local gain of this move
				int gain = calculateGain(nextTile, _p);
				
				
				//temporary "making" this move to estimate its full value
				int maxLoss = 0;
				int loss = 0;
				Piece temp = nextTile.getPlacedPiece();
				nextTile.removePiece();
				_t.removePiece();
				nextTile.placePiece(_p);
				
				//calculate max loss for all other pieces resting in their positions
				for (Iterator<Piece> f = _friends.iterator(); f.hasNext();)
				{
					Piece friend = f.next();
					if (friend != _p)
					{
						loss = calculateGain((Tile)_board.getTile(friend.getLocation().getRow(),friend.getLocation().getColumn()), friend);
						if (loss < maxLoss)
							maxLoss = loss;
					}
				}
				
				//put everything back
				nextTile.removePiece();
				if (temp!=null)
				{
					nextTile.placePiece(temp);
				}
				_t.placePiece(_p);
				
				//if this move leads to the loss somewhere on the board grater then the gain on this tile, consider this loss as a gain (it will be negative)
				//it means that enemy probably will take another piece on the board, not this one that made a move
				if ((maxLoss < gain)&&(maxLoss < 0))
					gain = maxLoss;
				
				//if its a new max of Gain
				if (gain > _maxGain)
				{
					//clear the list of best tiles and add this tile
					_maxGain = gain;
					_bestTiles.clear();
					_bestTiles.add(nextTile);
				}
				else 
					//if same gain as max
					if (gain == _maxGain)
					{	
						//add it to the list
						_bestTiles.add(nextTile);
					}				
			}
		}
				
	
		//strategy: go to opposite den, cat and dog are guarding their den
		
		if (!_bestTiles.isEmpty())
		{
			int maxStrategicValue = GAIN_RETREAT;
			Tile testTile = _bestTiles.get(0);
			
			//for each best tile estimate its strategic value
			for (Iterator<Tile> i = _bestTiles.iterator(); i.hasNext();)
			{
				Tile next = i.next();
				int strategicValue = getStrategicValue(next);
				
				//if this move is more valuable, choose it
				if (strategicValue > maxStrategicValue)
				{
					testTile = next;
					maxStrategicValue = strategicValue;
				}
			}
			
			//correct max gain with strategic value of best move
			_maxGain += maxStrategicValue;
			_bestTile = testTile;
			
		}
		else _bestTile = null;
		
	}
	
	//returns best tile
	public Tile getBestTile()
	{
		return _bestTile;
	}
	
	//returns the gain of best move
	public int getMaxGain()
	{
		return _maxGain;
	}
	
	//returns starting tile
	public Tile getTile()
	{
		return _t;
	}
	
	//if there is a winning move
	public boolean canWin()
	{
		return _winTile!=null;
	}
	
	//get winning tile
	public Tile getWinTile()
	{
		return _winTile;
	}
	
	//Calculate gain of piece p being on tile t if enemy always takes available pieces
	private int calculateGain (Tile t, Piece p)
	{
		int result = 0;
		
		Piece temp = t.getPlacedPiece();
		
		//because t tile is a valid move or our tile, if there is a piece on it and its not us, p can take it
		if (temp!=null)
		{
			if (temp!=p)
				//add value of a piece to take to the result
				result+=getValue(temp.getSpeed());
			//remove piece for investigation of this tile
			t.removePiece();
		}
		
		//get all enemies and friends, except p, aiming this tile
		ArrayList<Piece> enemies = getNeighbours(t, p, _enemies);
		ArrayList<Piece> friends = getNeighbours(t, p, _friends);
		
		//put the piece back
		if (temp!=null)
			t.placePiece(temp);
		
		
		//calculate gain 
		
		//Step 1: if someone can take p, its a threat
		Piece threat = listCanTake(t, enemies, p);
		if (threat!=null)
		{
			result -= getValue(p.getSpeed());
		}
		else 
		{
			//if no one can take p
			Piece aim = canTakelist(t, p, enemies);
			//if p can take some enemies around this tile
			if (aim!=null)
				//there is a potential gain -- leads to attacking move
				result+=getValue(aim.getSpeed())/POTENTIAL;
			
			//anyways, return result
			return result;
		}
		
		//Step 2: if a threat can be taken by one of friendly pieces, its a defender
		Piece defender = listCanTake(t, friends, threat);
		if (defender!=null)
		{
			result += getValue(threat.getSpeed());
			enemies.remove(threat);
		}
		else
			return result;
		
		//repeat step 1 and step 2
		threat = listCanTake(t, enemies, defender);
		if (threat!=null)
		{
			result -= getValue(defender.getSpeed());
			friends.remove(defender);
		}
		else 
		{
			//if no one can take defender
			Piece aim = canTakelist(t, defender, enemies);
			//if defender can take some enemies around this tile
			if (aim!=null)
				//there is a potential gain -- leads to attacking move
				result+=getValue(aim.getSpeed())/POTENTIAL;
			
			//anyways, return result
			return result;
		}
		
		defender = listCanTake(t, friends, threat);
		if (defender!=null)
		{
			result += getValue(threat.getSpeed());
			enemies.remove(threat);
		}
		else
			return result;
		
		return result;
	}
	
	//if one of pieces from the list can take piece p, return this piece
	//if there are several of them, return the one who has lowest speed;
	private Piece listCanTake(Tile t, ArrayList<Piece> list, Piece p)
	{
		int lowestSpeed = 9;
		Piece result = null;
		for (Iterator<Piece> i = list.iterator(); i.hasNext();)
		{
			Piece hunter = i.next();
			if ((hunter.canTakePiece(p) || t.getName() == "Trap") && (hunter.getSpeed() < lowestSpeed))
			{
				//if hunter is not a swimming piece
				if (hunter.getClass().getSuperclass() != SwimmingPiece.class)
				{	
					//and his prey is not a swimming piece
					if (p.getClass().getSuperclass() != SwimmingPiece.class)
					{
						result = hunter;
						lowestSpeed = hunter.getSpeed();
					}
					//if the prey is a swimming piece
					else
					{
						//then prey should not swim
						if (!((SwimmingPiece)p).isSwimming())
						{
							result = hunter;
							lowestSpeed = hunter.getSpeed();
						}
					}
				}
				//if hunter is a swimming piece
				else
				{
					//if its not swimming now - can take
					if(!((SwimmingPiece)hunter).isSwimming())
					{
						result = hunter;
						lowestSpeed = hunter.getSpeed();
					}
					//if it is swimming
					else
					{
						//then prey should be swimming too
						if (((SwimmingPiece)p).isSwimming())
						{
							result = hunter;
							lowestSpeed = hunter.getSpeed();
						}
					}	
				}
				
			}
		}
		return result;
	}
	
	//if piece p can take one of pieces from the list, return this piece
	//if there are several of them, return the one who has highest speed;
	private Piece canTakelist(Tile t, Piece p, ArrayList<Piece> list)
	{
		int highestSpeed = 0;
		Piece result = null;
		
		for (Iterator<Piece> i = list.iterator(); i.hasNext();)
		{
			Piece prey = i.next();
			if ((p.canTakePiece(prey) || (t.getName() == "Trap")) && (prey.getSpeed() > highestSpeed))
			{
				//if p is not a swimming piece
				if (p.getClass().getSuperclass() != SwimmingPiece.class)
				{	
					//and his prey is not a swimming piece
					if (prey.getClass().getSuperclass() != SwimmingPiece.class)
					{
						result = prey;
						highestSpeed = prey.getSpeed();
					}
					//if the prey is a swimming piece
					else
					{
						//then prey should not swim
						if (!((SwimmingPiece)prey).isSwimming())
						{
							result = prey;
							highestSpeed = prey.getSpeed();
						}
					}
				}
				//if p is a swimming piece
				else
				{
					//if its not swimming - can take
					if(!((SwimmingPiece)p).isSwimming())
					{
						result = prey;
						highestSpeed = prey.getSpeed();
					}
					//if it is swimming now
					else
					{
						//then prey should be swimming too
						if (((SwimmingPiece)prey).isSwimming())
						{
							result = prey;
							highestSpeed = prey.getSpeed();
						}
					}	
				}
			}
		}
		return result;
	}
	
	//get friends or enemies (from list pieces) that can enter this tile
	@SuppressWarnings("unchecked")
	private  ArrayList<Piece> getNeighbours(Tile target, Piece p, ArrayList<Piece> pieces)
	{
		ArrayList <Piece> result = new ArrayList<Piece>();
		
		//for all pieces from list
		for(Iterator<Piece> i = pieces.iterator(); i.hasNext();)
		{
			Piece neighbour = i.next();
			//if its not p
			if (neighbour!=p)
			{
				//get all valid moves
				ArrayList<Tile> validMoves = _board.getValidMoves(neighbour.getLocation().getRow(), neighbour.getLocation().getColumn());
				//if there are any
				if (validMoves!=null)
				{
					//for all valid moves
					for (Iterator<Tile> m = validMoves.iterator();m.hasNext();)
					{			
						Tile move = m.next();
						//if one of moves is target, add this piece as a neighbour
						if(move==target)
							result.add(neighbour);
					}
				}
			}
		}
		
		return result;
	}
	
	//weighted values of pieces - calculated based on power rating of a dominance-directed graph + bonus
	private int getValue (int speed)
	{
		switch (speed)
		{
			case 1: //rat
				return 7 + SHIFT;
			case 2: //cat
				return 2 + DEFENDER_BONUS + SHIFT;
			case 3: //dog
				return 4 + DEFENDER_BONUS + SHIFT;
			case 4: //wolf
				return 7 + SHIFT;
			case 5: //leopard
				return 11 + SHIFT;
			case 6: //tiger
				return 17 + JUMPING_BONUS + SHIFT;
			case 7: //lion
				return 22 + JUMPING_BONUS + SHIFT;
			case 8: //elephant
				return 27 + SHIFT;
				
			default:
				return 1;
			
		}
	}

	//strategic value of going in each direction
	//for all pieces except cat and dog it's better to go towards the den, cat and dog are on guard
	private int getStrategicValue(Tile next)
	{
		int value = GAIN_RETREAT;
		
		
		
		
		
		//if its not a cat or dog
		if (_p.getSpeed()!=2 && _p.getSpeed()!=3)
		{
			
			//remove elephant from 7 column at the start
			if ((_p.getSpeed() == 8) && next.getLocation().getRow() == 2 && next.getLocation().getColumn() == 5 && _t.getLocation().getColumn() == 6)
			{
				value = GAIN_MOVE_FORWARD + 1;
				return value;
			}
			
			//if moving up or down towards the den, its a best move
			if ((next.getLocation().getRow() - _t.getLocation().getRow()) == _p.getSide())
			{
				value=GAIN_MOVE_FORWARD;
				
				//If can reach den with no threats, go to Den
				int path = canReachDen();
				if( path == FORWARD ||  path == FORWARD_LEFT ||  path == FORWARD_RIGHT)
					return 1000;
				
			}
			else
			{
				//if moving sideways, its a second best move
				if ((next.getLocation().getRow()-_t.getLocation().getRow())==0)
				{
					
						//if on the left side then second best option is moving right
						if ( (_t.getLocation().getColumn() < 3) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) > 0) )
						{
							value = GAIN_MOVE_TO_CENTER;
							//If can reach den with no threats, go to Den
							int path = canReachDen();
							if(path == RIGHT || path == RIGHT_FORWARD)
								return 1000;
						}
						else 
						{
						
							//if on the right side then second best option is moving left
							if ( (_t.getLocation().getColumn() > 3) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) < 0) )
							{
								value = GAIN_MOVE_TO_CENTER;
								//If can reach den with no threats, go to Den
								int path = canReachDen();
								if(path == LEFT || path == LEFT_FORWARD)
									return 1000;
							}
							else
								value =  GAIN_MOVE_TO_SIDE;
						}
					
				
				}
			}
		
		}
		
		//Cat and dog are guarding the den
		else
		{
			//cat
			if (_p.getSpeed()==2)
			{
				//black cat
				if (_p.getSide()==ConstantValues.BLACK_SIDE)
				{
					if ( ((next.getLocation().getRow() == 1) && (next.getLocation().getColumn() == 4)) )
							value = GAIN_GUARDING_POSITION;
					else
					{	
						if (
								( (_t.getLocation().getColumn() < 4) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) > 0) ) ||
								( (_t.getLocation().getColumn() > 4) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) < 0) ) ||
								( (_t.getLocation().getRow() < 1) && ((next.getLocation().getRow() - _t.getLocation().getRow()) > 0) ) ||
								( (_t.getLocation().getRow() > 1) && ((next.getLocation().getRow() - _t.getLocation().getRow()) < 0) )
						)		
							value = GAIN_MOVE_TO_GUARD;
					}
						
				}
				//red cat
				else
				{
					if ( ((next.getLocation().getRow() == 7) && (next.getLocation().getColumn() == 2)) )
						value = GAIN_GUARDING_POSITION;
					else
					{	
						if (
								( (_t.getLocation().getColumn() < 2) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) > 0) ) ||
								( (_t.getLocation().getColumn() > 2) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) < 0) ) ||
								( (_t.getLocation().getRow() < 7) && ((next.getLocation().getRow() - _t.getLocation().getRow()) > 0) ) ||
								( (_t.getLocation().getRow() > 7) && ((next.getLocation().getRow() - _t.getLocation().getRow()) < 0) )
						)
							value = GAIN_MOVE_TO_GUARD;
					}
				}
			}
			//dog
			else
			{
				//black dog
				if (_p.getSide()==ConstantValues.BLACK_SIDE)
				{
					if ( ((next.getLocation().getRow() == 1) && (next.getLocation().getColumn() == 2)) )
							
						value = GAIN_GUARDING_POSITION;
					else
					{	
						if (
								( (_t.getLocation().getColumn() < 2) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) > 0) ) ||
								( (_t.getLocation().getColumn() > 2) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) < 0) ) ||
								( (_t.getLocation().getRow() < 1) && ((next.getLocation().getRow() - _t.getLocation().getRow()) > 0) ) ||
								( (_t.getLocation().getRow() > 1) && ((next.getLocation().getRow() - _t.getLocation().getRow()) < 0) )
						)
							value = GAIN_MOVE_TO_GUARD;
					}
				}
				//red dog
				else
				{
					if ( ((next.getLocation().getRow() == 7) && (next.getLocation().getColumn() == 4)) )	
						value = GAIN_GUARDING_POSITION;
					else
					{	
						if (
								( (_t.getLocation().getColumn() < 4) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) > 0) ) ||
								( (_t.getLocation().getColumn() > 4) && ((next.getLocation().getColumn() - _t.getLocation().getColumn()) < 0) ) ||
								( (_t.getLocation().getRow() < 7) && ((next.getLocation().getRow() - _t.getLocation().getRow()) > 0) ) ||
								( (_t.getLocation().getRow() > 7) && ((next.getLocation().getRow() - _t.getLocation().getRow()) < 0) )
						)
							value = GAIN_MOVE_TO_GUARD;	
					}
				}
			}
		}
		
		return value;
	}
	
	//If can reach den with no threats, go to Den
	private int canReachDen ()
	{
		int result = 0;
		
		//1st option: Den is 2 steps ahead
		int row = 2;
		if (_p.getSide() == ConstantValues.BLACK_SIDE)
		{
			row = 6;
		}

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 3))
		{
			Piece def1 = _board.getTile(row + _p.getSide(), 2).getPlacedPiece();
			Piece def2 = _board.getTile(row + _p.getSide(), 4).getPlacedPiece();
			if (def1==null && def2==null)
			{
				return FORWARD;
			}
		}
		
		//2nd option: Den is on the left
		row = 0;
		if (_p.getSide() == ConstantValues.BLACK_SIDE)
		{
			row = 8;
		}

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 5))
		{
			Piece def1 = _board.getTile(row - _p.getSide(), 4).getPlacedPiece();
			if (def1==null)
			{
				return LEFT;
			}
		}
		
		//3rd option: Den is on the right

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 1))
		{
			Piece def1 = _board.getTile(row - _p.getSide(), 2).getPlacedPiece();
			if (def1==null)
			{
				return RIGHT;
			}
		}
		
		//4th option: Can reach den going forward-left
		row = 1;
		if (_p.getSide() == ConstantValues.BLACK_SIDE)
		{
			row = 7;
		}

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 4))
		{
			Piece def1 = _board.getTile(row + _p.getSide(), 5).getPlacedPiece();
			if (def1==null)
			{
				return FORWARD_LEFT;
			}
		}
		
		
		//5th option: Can reach den going forward-right

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 2))
		{
			Piece def1 = _board.getTile(row + _p.getSide(), 1).getPlacedPiece();
			if (def1==null)
			{
				return FORWARD_RIGHT;
			}
		}
		
		//6th option: Can reach den going left-forward

		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 4))
		{
			Piece def1 = _board.getTile(row - _p.getSide(), 3).getPlacedPiece();
			Piece def2 = _board.getTile(row, 2).getPlacedPiece();
			
			if ((def1==null) && (def2==null))
			{
				return LEFT_FORWARD;
			}
		}
		
		//7th option: Can reach den going right-forward
		if ((_t.getLocation().getRow() == row)&&(_t.getLocation().getColumn() == 2))
		{
			Piece def1 = _board.getTile(row - _p.getSide(), 3).getPlacedPiece();
			Piece def2 = _board.getTile(row, 4).getPlacedPiece();
			
			if ((def1==null) && (def2==null))
			{
				return RIGHT_FORWARD;
			}
		}
		
		return result;
	}
}

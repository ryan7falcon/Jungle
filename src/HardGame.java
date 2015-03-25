import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import logic.Coordinate;
import logic.piece.Piece;


public class HardGame extends Game
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2204374322300058559L;

	@SuppressWarnings("unchecked")
	public ArrayList<Tile> getNextMoveBlack()
	{
		return getBestMove(_blackPieces, _redPieces);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Tile> getNextMoveRed()
	{
		return getBestMove(_redPieces, _blackPieces);
	}
	
	//Get the best move for each friendly piece and estimate its value, then choose a piece with a move of highest value
	private ArrayList<Tile> getBestMove (ArrayList<Piece> friends, ArrayList<Piece> enemies)
	{
		
		if(!friends.isEmpty())
		{
			//Piece to go
			Piece toGo;
			//where from
			int startRow;
			int startColumn;
			//array list for result
			ArrayList<Tile> nextMove = new ArrayList<Tile>();
			Tile startTile;
			Tile endTile;
			
			BoardAnalysis ba = new BoardAnalysis();
			
			for (Iterator<Piece> it = friends.iterator(); it.hasNext();)
			{
				//next available piece
				toGo = (Piece) it.next();
				
				//Assign start tile
				startRow = toGo.getLocation().getRow();
				startColumn = toGo.getLocation().getColumn();
				startTile = (Tile) getTile(startRow, startColumn);
		
				//Analyse position of this piece and estimate best move and its value
				Situation s = new Situation(_gameBoard, toGo, friends, enemies);
				
				//if we can win immediately with this piece
				if (s.canWin())
				{
					endTile = s.getWinTile();
	
					nextMove.add(startTile);
					nextMove.add(endTile);
					
					//update piece's location
					toGo.setLocation(new Coordinate(endTile.getLocation().getRow(), endTile.getLocation().getColumn()));
					
					return nextMove;
				}
				
				//add analysis of this tile to board analysis
				if(s.getBestTile()!=null)
					ba.addSituation(s);
				
			}
			
			//perform board analysis
			ba.analyse();
			
			//If there are several pieces with best moves of equal value, choose randomly
			Random r = new Random();
			int index  = r.nextInt(ba.getBestMoves().size());
			return setMove(ba.getBestMoves(), index);
		}
		else
			return null;
	}
	
	//helper function to get a move of given index from a list and prepare a piece to perform it (updates piece's location)
	private ArrayList<Tile> setMove(ArrayList<ArrayList<Tile> >moves, int i)
	{
		ArrayList<Tile> move = moves.get(i);
		Piece toGo = _gameBoard.getTile(move.get(0).getLocation().getRow(), move.get(0).getLocation().getColumn()).getPlacedPiece();
		//update piece's location
		toGo.setLocation(new Coordinate(move.get(1).getLocation().getRow(), move.get(1).getLocation().getColumn()));
		return move;
	}
}

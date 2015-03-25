//Class: BoardAnalysis
//This class chooses pieces with the most valuable moves
import java.util.ArrayList;
import java.util.Iterator;


public class BoardAnalysis {

	private ArrayList<Situation> situationList = new ArrayList<Situation>();
	private  ArrayList< ArrayList<Tile> > _bestMoves = new  ArrayList< ArrayList<Tile> >();
	
	public void addSituation(Situation s)
	{
		situationList.add(s);
	}
	
	public void analyse()
	{

		int maxGain = -1000;
		
		//for each piece
		for (Iterator<Situation> i = situationList.iterator();i.hasNext();)
		{
			Situation active = i.next();
			int gain = active.getMaxGain();
			
			ArrayList<Tile> move = new ArrayList<Tile>();
			move.add(active.getTile());
			move.add(active.getBestTile());

			//if this move leads to new max total gain
			if (gain > maxGain)
			{
				maxGain =gain;
				//clear all previous best moves, add this one
				_bestMoves.clear();
				_bestMoves.add(move);
			}
			else
				//if this move is as good as best one
				if(gain == maxGain)
					//add it
					_bestMoves.add(move);
		}
		
	}
	
	public ArrayList< ArrayList<Tile> > getBestMoves()
	{
		return _bestMoves;
	}
	
}

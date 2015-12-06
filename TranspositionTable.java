private class TranspositionNode
{
	private long key;
	private int depth;
	private int value;
	private int flag;
	private int old;
	private Move bestMove;

	public TranspositionNode (long key, int depth, int value, int flag, int old, Move bestMove)
	{
		this.key = key;
		this.depth = depth;
		this.value = value;
		this.flag = flag;
		this.old = old;
		this.bestMove = bestMove;
	}
}

public class TranspositionTable
{  
	private static final int HASHSIZE = 131072;
	private int[] tt; // Used for transposition table
	private long[] rep; // Used for repetition detection table
	private int ttSize; // The number of slots either table will have
	
	public static final int SLOTS = 6; // 3 for one 'table', 6 for two (two tables means one for depth and one for always replace)
	
	public TranspositionTable ()
	{
		this.ttSize = HASHSIZE;
	}
	
	// TT constructor
	public TranspositionTable(int tableSize)
	{
		this.ttSize = tableSize;
		tt = new int[tableSize];
	}
	
	// RT constructor
	public TranspositionTable(int tableSize, boolean isRep)
	{
		tt = null;
		this.ttSize = tableSize;
		rep = new long[tableSize];		
	}
	
	// Clear repetition table
	public void clearRep()
	{
		rep = new long[ttSize];
	}
	
	// Clear transposition table
	public void clearTT()
	{
		tt = new int[ttSize];
	}
	
	// Records a position the repition table, will search through the table until it finds an empty slot
	public void addRep(long zobrist)
	{		
		int key = (int)(zobrist%ttSize);
		
		if(rep[key] == 0)
		{
			rep[key] = zobrist;
			return;
		}
		
		int i = 1;
		int j = 0; // 'Failsafe', do not loop more than the hashsize
		
		while(j < ttSize)
		{
			try
			{
				if(rep[key+i] == 0)
				{
					rep[key+i] = zobrist;
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException ex)
			{
				// Repetition table is full
				key = 0;
				i = 0;
			}
			i++;
			j++;
		}			
	}
	
	// Remove repetition node
	public void removeRep(long zobrist)
	{
		int key = (int)(zobrist%ttSize);
		
		if(rep[key] == zobrist)
		{
			rep[key] = 0;
			return;
		}
		
		int i = 1;
		int j = 0;
		
		while(j < ttSize)
		{
			try
			{
				if(repZobrist[key+i] == zobrist)
				{
					rep[key+i] = 0;
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException ex)
			{
				// Repetition for removal not found
				key = 0;
				i = 0;	
			}
			i++;
			j++;
		}			
	}
	
	// Check if a given repetition occurs
	public boolean isRep(long zobrist)
	{
		int key = (int)(zobrist%ttSize);
		
		if(rep[key] == 0) 
			return false;
		else if (rep[key] == zobrist) 
			return true;
		
		int i = 1;
		
		while(i < ttSize)
		{
			try
			{
				if(rep[key+i] == 0) 
					return false;
				else if(rep[key+i] == zobrist) 
					return true;
			}
			catch(ArrayIndexOutOfBoundsException ex)
			{
				key = 0;
				i = 0;
			}
			i++;
		}
		return false;
	}

	// Record an entry if empty slot, new position has deeper depth or old position is timed out
	public void recordTT(long zobrist, int depth, int flag, int value, Move move)
	{
		// Depth/new replacement scheme
		int key = (int)(zobrist%HASHSIZE);//*SLOT
		
		// Position empty, add new WTF!!!?
		if(tt[key] == 0)
		{
			tt[key] = 0
				| (value + 0x1FFFF)
				| ((BoardArray.oldNode + 1) << 18)
				| (flag << 20)
				| (depth << 22);
			tt[key+1] = move;
			tt[key+2] = (int)(zobrist >> 32);
			return;
		}

		// Position not empty, add new if it has deeper depth or if current entry is too old WTF!!!?
		if((tt[key] >> 22) <= depth || (((tt[key] >> 18) & 3) - 1) != BoardArray.oldNode)
		{
			tt[key] = 0
				| (value + 0x1FFFF)
				| ((BoardArray.oldNode + 1) << 18)
				| (flag << 20)
				| (depth << 22);
			tt[key+1] = move;
			tt[key+2] = (int)(zobrist >> 32);
			return;
		}
		
		// Position empty, new not deep enough, add to the second part WTF!!!?
		tt[key+3] = 0
			| (value + 0x1FFFF)
			| ((BoardArray.oldNode + 1) << 18)
			| (flag << 20)
			| (depth << 22);
		tt[key+4] = move;
		tt[key+5] = (int)(zobrist >> 32);
		
		/*if(hashtable[hashkey+2] == (int)(zobrist >> 32) && hashtable[hashkey+1] == 0)
		{
			hashtable[hashkey+1] = move;
			return;
		}*/
	}
	
	// WTF!!!?
	public void addValueTT(long zobrist, int value)
	{
		int key = (int)(zobrist%HASHSIZE);//*2;

		tt[key] = (value + 0x1FFFF);
		tt[key+1] = (int)(zobrist >> 32);
	}
	
	public int probeValue(long zobrist)
	{
		int key = (int)(zobrist%HASHSIZE);//*2;
		if(tt[key+1] == ((int)(zobrist >> 32)))
		{
			return (tt[key] - 0x1FFFF);
		}
		// Value not found
		return 10000001;
	}
	
	// WTF!!!?
	public void updateValueTT(long zobrist, int value)
	{	
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		// Confirm entry correct
		if(tt[hashkey+2] == (int)(zobrist >> 32))
		{
			tt[hashkey] = tt[key] & 0xFFFC0000; // Clear value
			tt[hashkey] = tt[key] | (eval + 0x1FFFF); // Set value
		}
		else if(tt[key+5] == (int)(zobrist >> 32))
		{
			tt[key] = tt[key] & 0xFFFC0000; // Clear value
			tt[key] = tt[key] | (eval + 0x1FFFF); // Set value
		}
		
	}
	
	// WTF!!!?
	public boolean isEntryTT(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32) && tt[key] != 0)
			return true;
		else if(tt[key+5] == (int)(zobrist >> 32) && tt[key+3] != 0) 
			return true;
		else 
			return false;
	}

	// Returns value at the right index if key matches
	public int getValueTT(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32)) 
			return ((tt[key] & 0x3FFFF) - 0x1FFFF);
		else if(tt[key+5] == (int)(zobrist >> 32)) 
			return ((tt[key+3] & 0x3FFFF) - 0x1FFFF);

		return 0;
	}

	// Return flag at the right index if key matches
	public int getFlagTT(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32)) 
			return ((tt[key] >> 20) & 3);
		else if(tt[key+5] == (int)(zobrist >> 32)) 
			return ((tt[key+3] >> 20) & 3);

		return 0;
	}
	
	// Return move at the right index if key matches
	public int getMoveTT(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32)) 
			return tt[key+1];
		else if(tt[key+5] == (int)(zobrist >> 32)) 
			return tt[key+4];

		return 0;
	}
	
	// Return depth at the right index if key matches
	public int getDepthTT(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32)) 
			return (tt[key] >> 22);
		else if(tt[key+5] == (int)(zobrist >> 32)) 
			return (tt[key+3] >> 22);

		return 0;
	}
	
	// Return entry at the right index if key matches
	public int getEntry(long zobrist)
	{
		int key = (int)(zobrist%ttSize);//*SLOTS;
		
		if(tt[key+2] == (int)(zobrist >> 32)) 
			return (tt[key]);
		else if(tt[key+5] == (int)(zobrist >> 32)) 
			return (tt[key+3]);
			
		return tt[key];
	}
}

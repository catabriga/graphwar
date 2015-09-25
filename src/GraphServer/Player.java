//  Copyright (C) 2011 Lucas Catabriga Rocha <catabriga90@gmail.com>
//    
//  This file is part of Graphwar.
//
//  Graphwar is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  Graphwar is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.

//  You should have received a copy of the GNU General Public License
//  along with Graphwar.  If not, see <http://www.gnu.org/licenses/>.

package GraphServer;

import java.util.Random;

public class Player
{
	private String name;
	private int numSoldiers;
	private int team;	
	private int playerID;
	private boolean ready;
	
	private static int lastID = 0;
	private static Random random = new Random(System.currentTimeMillis());
	
	public Player(String name)
	{
		this.name = name;
		this.numSoldiers = Constants.INITIAL_NUM_SOLDIERS;
		
		if(random.nextBoolean())
		{
			this.team = Constants.TEAM1;
		}
		else
		{
			this.team = Constants.TEAM2;
		}
		
		this.playerID = lastID;
		this.ready = false;
		
		lastID++;
	}
	
	public boolean getReady()
	{
		return this.ready;
	}
	
	public void setReady(boolean ready)
	{
		this.ready = ready;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getID()
	{
		return this.playerID;
	}
	
	public void setTeam(int team)
	{
		this.team = team;
	}
	
	public int getTeam()
	{
		return this.team;
	}
	
	public int getNumSoldiers()
	{
		return this.numSoldiers;
	}
	
	public void setNumSoldiers(int numSoldiers)
	{
		this.numSoldiers = numSoldiers;
	}
}

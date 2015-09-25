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
package Graphwar;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import GraphServer.Constants;

public class Player
{
	private String name;
	protected int team;
	private int numSoldiers;
	protected Soldier[] soldiers;
	protected int currentTurnSoldier;
	private int playerID;
	private boolean localPlayer;
	private Color color;
	private boolean ready;
	private int nameLength;
	private boolean disconnected;
	
	public Player(String name, int playerID, int team, boolean localPlayer, int numSoldiers, boolean ready)
	{
		this.name = name;		
		this.team = team;	
		this.numSoldiers = numSoldiers;
		
		this.soldiers = new Soldier[Constants.MAX_SOLDIERS_PER_PLAYER];
			for(int i=0; i<soldiers.length; i++)
			{
				soldiers[i] = new Soldier();
			}
		this.currentTurnSoldier = 0;
			
		this.localPlayer = localPlayer;
		
		this.playerID = playerID;
		
		this.color = GraphUtil.getRandomColor();
		
	@SuppressWarnings("deprecation")
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics (Constants.NAME_FONT);		
		this.nameLength = fontMetrics.stringWidth(name);
		
		this.disconnected = false;
	}
	
	public boolean isDisconnected()
	{
		return this.disconnected;
	}
	
	public void markDisconnected()
	{
		this.disconnected = true;
	}
	
	public int getNameLength()
	{
		return nameLength;
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
	
	public void setTeam(int team)
	{
		this.team = team;
	}
	
	public int getTeam()
	{
		return this.team;
	}
	
	public void startSoldier(int soldierNum, int x, int y)
	{
		this.soldiers[soldierNum] = new Soldier(x, y);
	}
	
	public void setSoldiers(int numSoldiers)
	{
		this.numSoldiers = numSoldiers;
	}
	
	public int getNumSoldiers()
	{
		return this.numSoldiers;
	}
	
	public Soldier[] getSoldiers()
	{
		return this.soldiers;
	}
	
	public int getCurrentTurnSoldierIndex()
	{
		return this.currentTurnSoldier;
	}
	
	public Soldier getCurrentTurnSoldier()
	{
		return soldiers[currentTurnSoldier];
	}
	
	public int getID()
	{
		return this.playerID;
	}
	
	public boolean isLocalPlayer()
	{
		return this.localPlayer;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void restartTurn()
	{
		currentTurnSoldier = 0;
	}
	
	public Soldier getNextTurnSoldier()
	{
		int nextSoldier = currentTurnSoldier;
		
		for(int i=0; i<numSoldiers; i++)
		{			
			nextSoldier = (nextSoldier+1)%numSoldiers;
			
			if(soldiers[nextSoldier].isAlive())
			{
				return soldiers[nextSoldier];
			}
		}
		
		return null;
	}
	
	public boolean nextTurn()
	{		
		for(int i=0; i<numSoldiers; i++)
		{
			currentTurnSoldier = (currentTurnSoldier+1)%numSoldiers;
			
			if(soldiers[currentTurnSoldier].isAlive())
			{
				return true;
			}
		}
		
		return false;
	}
}

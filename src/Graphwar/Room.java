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

public class Room 
{
	private String name;
	private int port;
	private String ip;
	private int gameMode;
	private int numPlayers;
	private int roomID;
	
	public Room(String name, int roomID, String ip, int port, int mode, int numPlayers)
	{
		this.name = name;
		this.roomID = roomID;
		this.ip = ip;
		this.port = port;
		
		this.gameMode = mode;
		this.numPlayers = numPlayers;		
	}
	
	public void updateRoom(int numPlayers, int gameMode)
	{
		this.gameMode = gameMode;
		this.numPlayers = numPlayers;
		
	}
		
	public String getName()
	{
		return this.name;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public String getIp()
	{
		return this.ip;
	}
	
	public int getNumPlayers()
	{
		return this.numPlayers;
	}
	
	public int getGameMode()
	{
		return this.gameMode;
	}
	
	public int getRoomID()
	{
		return this.roomID;
	}
}

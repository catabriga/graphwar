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

package RoomServer;

import java.io.IOException;

import GraphServer.Constants;

public class Room
{
	private RemoteGraphServer gameServer;
	private GlobalClient globalClient;
	
	private int roomNum;
	
	public Room(int roomNum) throws IOException
	{
		this.roomNum = roomNum;
		
		globalClient = new GlobalClient();
		
		//int port = Constants.PUBLIC_ROOM_PORT+roomNum;		
		gameServer = new RemoteGraphServer(globalClient);		
		int port = gameServer.getPort();
		
		new Thread(gameServer).start();
		
		globalClient.joinGlobalServer(Constants.GLOBAL_IP, Constants.GLOBAL_PORT, Constants.DUMMY_NAME);
		globalClient.createRoom("Public Room "+roomNum, port);
	}
	
	public int getNumCLients()
	{
		return gameServer.getNumClients();
	}
	
	public int getRoomNum()
	{
		return this.roomNum;
	}
	
	public boolean isAcceptingConnections()
	{
		return gameServer.isAcceptingConnections() && globalClient.isRoomListed();
	}
	
	public void printInfo()
	{
		System.out.print("Room "+roomNum+": ");
		System.out.print(this.getNumCLients()+" clients; ");
		System.out.print(gameServer.getNumPlayers()+" players; ");
		System.out.print("state: "+gameServer.getGameState()+"; ");
		System.out.print("accepting connections: "+gameServer.isAcceptingConnections()+"; ");
		System.out.print("room listed: "+globalClient.isRoomListed()+"; ");
		System.out.println();
		
	}
	
	public void stop()
	{
		gameServer.finalize();
		globalClient.stop();
	}
}

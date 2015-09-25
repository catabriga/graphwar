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

import GraphServer.ClientConnection;
import GraphServer.Constants;
import GraphServer.GraphServer;
import GraphServer.Player;

public class RemoteGraphServer extends GraphServer
{
	private GlobalClient globalClient;
	
	public int getNumClients()
	{
		return this.clients.size();
	}
	
	public int getNumPlayers()
	{
		return this.players.size();
	}
	
	public int getGameState()
	{
		return this.gameState;
	}
	
	public boolean isAcceptingConnections()
	{
		return this.acceptingConnections;
	}
		
	public RemoteGraphServer(GlobalClient globalClient) throws IOException
	{
		super();
		
		this.globalClient = globalClient;		
	}

	protected void sendAddPlayerMessage(Player player, ClientConnection playerFrom)
	{
		super.sendAddPlayerMessage(player, playerFrom);
		
		globalClient.sendRoomStatus(this.gameMode, this.players.size());
	}
	
	protected boolean removePlayer(int playerID, ClientConnection client)
	{
		boolean v = super.removePlayer(playerID, client);
		
		globalClient.sendRoomStatus(this.gameMode, this.players.size());
		
		return v;
	}
	
	protected void sendModeMessage()
	{
		super.sendModeMessage();
		
		globalClient.sendRoomStatus(this.gameMode, this.players.size());
	}
	
	public void removeClient(ClientConnection client)
	{
		super.removeClient(client);
				
		if(this.clients.size() == 0)
		{
			if(this.gameState == Constants.GAME)
			{
				this.goPreGame();
				globalClient.recreateRoom();
			}
		}
		
		globalClient.sendRoomStatus(this.gameMode, this.players.size());
	}
	
	protected void startGame()
	{
		super.startGame();
		
		globalClient.hideRoom();
	}
	
	protected void finishGame(ClientConnection client)
	{
		super.finishGame(client);
		
		if(this.gameState == Constants.PRE_GAME)
		{
			globalClient.recreateRoom();
		}
	}	
}

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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class ClientConnection implements Runnable
{
	private Connection connection;	
	private List<Player> players;	
	private GraphServer server;	
	private boolean running;	
	private boolean leader;
	private boolean readyNextTurn;	
	private boolean gameFinished;
	private boolean skipLevel;
	
	public ClientConnection(GraphServer server, Socket socket) throws IOException
	{
		this.server = server;		
		this.connection = new Connection(socket);
			
		this.players = new ArrayList<Player>();
		
		this.running = false;		
		this.leader = false;
		this.readyNextTurn = false;		
		this.gameFinished = false;
		this.skipLevel = false;
	}
	
	public List<Player> getPlayers()
	{
		return players;		
	}
	
	public boolean isLeader()
	{
		return leader;
	}
	
	public void setLeader(boolean leader)
	{
		this.leader = leader;
	}
	
	public boolean getSkipLevel()
	{
		return skipLevel;
	}
	
	public void setSkipLevel(boolean skip)
	{
		this.skipLevel = skip;
	}
	
	public boolean isFinished()
	{
		return gameFinished;
	}
	
	public void setFinished(boolean finished)
	{
		this.gameFinished = finished;
	}
	
	public void removePlayer(Player player)
	{
		players.remove(player);
	}
	
	public void addPlayer(Player player)
	{
		players.add(player);
	}
	
	public boolean getReadyNextTurn()
	{
		return this.readyNextTurn;
	}
	
	public void setReadyNextTurn(boolean ready)
	{
		this.readyNextTurn = ready;
	}
	
	public boolean checkTimeout()
	{
		if(System.currentTimeMillis() - connection.getLastReceivedTime() > Constants.TIMEOUT_DROP)
		{
			return true;
		}
		
		return false;
	}

	public boolean checkStayAliveTime()
	{
		if(System.currentTimeMillis() - connection.getLastSentTime() > Constants.TIMEOUT_KEEPALIVE)
		{
			return true;
		}
		
		return false;
	}
	
	public void disconnect()
	{
		running = false;
						
		try 
		{
			connection.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message)
	{
		connection.sendMessage(message);
	}
	
	public void sendKeepAlive()
	{
		connection.sendMessage(NetworkProtocol.NO_INFO+"");
	}
	
	public void run() 
	{
		running = true;
		
		while(running)
		{
			try 
			{
				String message = connection.readMessage();
																
				if(message == null)
				{
					server.removeClient(this);
					disconnect();
				}
				else
				{
					server.handleMessage(message, this);
					
					if(checkStayAliveTime())
					{
						sendKeepAlive();
					}
				}
			} 
			catch (SocketTimeoutException e)
			{
				//e.printStackTrace();
				
				if(checkTimeout())
				{
					server.removeClient(this);
					disconnect();
				}
				else
				{
					sendKeepAlive();
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				
				server.removeClient(this);
				disconnect();
			}
		}
	}
}

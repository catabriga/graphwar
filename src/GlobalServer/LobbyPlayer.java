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

package GlobalServer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import GraphServer.Connection;
import GraphServer.Constants;
import GraphServer.NetworkProtocol;


public class LobbyPlayer implements Runnable
{
	private Connection connection;
	private GlobalServer globalServer;
	private String name;	
	private int playerID;
	private boolean running;
	private Room room;
	private boolean dummy;
	
	private static int lastPlayerID = 1;
	
	public LobbyPlayer(Connection connection, GlobalServer globalServer)
	{
		this.connection = connection;
		this.globalServer = globalServer;
		this.running = true;
		this.playerID = lastPlayerID;
		this.name = "Player";
		this.room = null;
		this.dummy = true;
		
		lastPlayerID++;
	}
	
	public String getIpAddress()
	{
		return this.connection.getIpAddress();
	}
	
	public int getID()
	{
		return this.playerID;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean isDummy()
	{
		return dummy;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}
	
	public Room getRoom()
	{
		return this.room;
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
		
		try 
		{
			this.name = connection.readMessage();
			
			System.out.println("New name: "+name);
			
			if(name.compareTo(Constants.DUMMY_NAME) == 0)
			{
				this.dummy = true;
			}
			else
			{
				this.dummy = false;
			}
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
			
			disconnect();
			return;
		} 
		
		this.globalServer.registerNewPlayer(this);
		this.globalServer.sendListPlayers(this);
		this.globalServer.sendListRooms(this);
		
		while(running)
		{
			try 
			{
				String message = connection.readMessage();
								
				if(message == null)
				{
					this.globalServer.removePlayer(this);
					disconnect();
				}
				else
				{
					this.globalServer.handleMessage(message, this);
										
					if(checkStayAliveTime())
					{
						sendKeepAlive();
					}
				}
			} 
			catch (SocketTimeoutException e)
			{
				if(checkTimeout())
				{
					this.globalServer.removePlayer(this);
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
				
				this.globalServer.removePlayer(this);
				disconnect();
			}
		}
	}
}

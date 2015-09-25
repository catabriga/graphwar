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

import java.io.IOException;
import java.net.SocketTimeoutException;

import GraphServer.Connection;
import GraphServer.Constants;
import GraphServer.NetworkProtocol;

public class ServerConnection implements Runnable
{
	private Connection connection;
	
	private GameData gameData;
	
	private boolean running;
	
	public ServerConnection(GameData gameClient, String ip, int port) throws IOException
	{
		this.gameData = gameClient;
		
		this.connection = new Connection(ip, port);
				
		this.running = false;
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
					gameData.kickFromGame();
					disconnect();
				}
				else
				{
					gameData.handleMessage(message);
					
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
					gameData.kickFromGame();
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
				
				gameData.kickFromGame();
				disconnect();
			}
		}
	}
}

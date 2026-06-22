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
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;


import GraphServer.Connection;
import GraphServer.Constants;
import GraphServer.NetworkProtocol;

public class GlobalClient implements Runnable
{			
	private Connection connection;
	
	private String localPlayer;
	
	private boolean running;	
	
	private boolean roomCreated;
	private boolean roomHidden;
	private boolean roomInvalid;
	private boolean roomListed;
	private int roomID;
	private String roomName;
	private int roomPort;
	private long last_keep_alive_time;
	
	
	public GlobalClient()
	{		
		this.running = false;
		
		this.localPlayer = "Player";
		
		this.roomCreated = false;
		this.roomHidden = false;
		this.roomInvalid = false;
		this.roomListed = true;
		this.roomName = "Room";
		this.roomPort = Constants.DEFAULT_PORT;

	}
	
	public String getLocalPlayerName()
	{
		return this.localPlayer;
	}
	
		
	public void joinGlobalServer(String ip, int port, String playerName) throws IOException
	{
		connection = new Connection(ip, port);
		
		// This is not encoded because the dummy name constants is already encoded
		connection.sendMessage(playerName);
		
		this.localPlayer = playerName;
					
		this.running = true;
		new Thread(this).start();		
	}
			
	public boolean isRoomInvalid()
	{
		return roomInvalid;
	}
	
	public boolean isRoomListed()
	{
		boolean timed_out = (System.currentTimeMillis()-last_keep_alive_time) > 10000;

		return roomListed && (!timed_out);
	}
	
	private void handleMessage(String message)
	{		
		String[] info = new String[0];

		if(message != null)
		{
			info = message.split("&");
		}
		
		if(info.length > 0)
		{
			int code = Integer.parseInt(info[0]);
			
			switch(code)
			{		
				case NetworkProtocol.NO_INFO:
				{
					last_keep_alive_time = System.currentTimeMillis();
				}

				case NetworkProtocol.CREATE_ROOM:
				{
					if(info.length == 5)
					{
						try
						{
							String roomName = URLDecoder.decode(info[1], "UTF-8");
							int roomID = Integer.parseInt(info[2]);
//							String ipAddress = URLDecoder.decode(info[3], "UTF-8");
//							int port = Integer.parseInt(info[4]);
							
							if(this.roomName.compareTo(roomName) == 0)
							{
								this.roomListed = true;
								this.roomID = roomID;
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
				}break;
			
				case NetworkProtocol.LIST_ROOMS:
				{
					if(info.length >= 2)
					{						
						int numRooms = Integer.parseInt(info[1]);
						
						System.out.println("LIST_ROOMS " + this.roomName + " " + numRooms);

						this.roomListed = false;
						for(int i=0; i<numRooms; i++)
						{
							try
							{
								String roomName = URLDecoder.decode(info[2+6*i], "UTF-8");
								int roomID = Integer.parseInt(info[3+6*i]);
//								String ipAddress = URLDecoder.decode(info[4+6*i], "UTF-8");
//								int port = Integer.parseInt(info[5+6*i]);
//								int mode = Integer.parseInt(info[6+6*i]);
//								int numPlayers = Integer.parseInt(info[7+6*i]);				
								
								if(this.roomName.compareTo(roomName) == 0)
								{
									this.roomListed = true;
									this.roomID = roomID;
								}
								
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}							
						}

						System.out.println("roomListed " + this.roomName + " " + roomListed);
					}
				}break;
				
				case NetworkProtocol.CLOSE_ROOM:
				{
					if(info.length == 2)
					{
						int roomID = Integer.parseInt(info[1]);
						
						if(this.roomID == roomID)
						{
							this.roomListed = false;
						}
					}
				}break;
			
				case NetworkProtocol.ROOM_INVALID:
				{
					this.hideRoom();
					this.recreateRoom();
				}break;			
			}
		}
	}
		
	public void recreateRoom()
	{
		if(this.roomHidden == true)
		{
			this.roomCreated = true;
			this.roomHidden = false;
			this.roomInvalid = false;
			this.roomListed = true;
		
			try
			{
				String message = NetworkProtocol.CREATE_ROOM+"&"+URLEncoder.encode(this.roomName, "UTF-8")+"&"+this.roomPort;
				this.connection.sendMessage(message);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void createRoom(String roomName, int portNumber)
	{
		this.roomName = roomName;
		this.roomPort = portNumber;
		
		this.roomCreated = true;
		this.roomListed = true;
		
		try
		{
			String message = NetworkProtocol.CREATE_ROOM+"&"+URLEncoder.encode(roomName, "UTF-8")+"&"+portNumber;
			this.connection.sendMessage(message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public void closeRoom()
	{
		if(this.roomCreated)
		{
			this.roomCreated = false;
			this.roomHidden = false;
			this.roomInvalid = false;
			this.connection.sendMessage(NetworkProtocol.CLOSE_ROOM+"");
		}
	}
	
	public void hideRoom()
	{
		if(this.roomCreated)
		{
			this.roomCreated = false;
			this.roomHidden = true;
			this.connection.sendMessage(NetworkProtocol.CLOSE_ROOM+"");
		}
	}
	
	private void quitGlobal()
	{
		String message = NetworkProtocol.QUIT+"";
				
		this.connection.sendMessage(message);
	}
	
	public boolean isRunning()
	{
		return running;
	}

	public void stop()
	{
		quitGlobal();
		disconnect();		
	}
	
	public void sendRoomStatus(int gameMode, int numPlayers)
	{
		if(roomCreated)
		{
			String message = NetworkProtocol.ROOM_STATUS+"&"+gameMode+"&"+numPlayers;
			
			this.connection.sendMessage(message);
		}			
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

	private void disconnect()
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
		while(running)
		{
			try 
			{
				String message = connection.readMessage();
								
				if(message == null)
				{
					disconnect();
				}
				else
				{
					handleMessage(message);
										
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
				
				disconnect();
			}
		}
	}
}

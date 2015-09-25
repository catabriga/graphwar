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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import GraphServer.Connection;
import GraphServer.Constants;
import GraphServer.NetworkProtocol;

public class GlobalClient implements Runnable
{	
	private class LobbyPlayer
	{
		private String name;
		private int playerID;
		
		public LobbyPlayer(String name, int playerID)
		{
			this.name = name;
			this.playerID = playerID;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public int getID()
		{
			return this.playerID;
		}
	}
	
	private Connection connection;
	
	private List<LobbyPlayer> players;
	private List<Room> rooms;
	
	private String localPlayer;
	
	private boolean running;	
	
	private boolean roomCreated;
	private boolean roomHidden;
	private boolean roomInvalid;
	private String roomName;
	private int roomPort;
	
	private Graphwar graphwar;
	
	public GlobalClient(Graphwar graphwar)
	{
		this.players = new Vector<LobbyPlayer>();
		this.rooms = new Vector<Room>();
		
		this.running = false;
		
		this.localPlayer = "Player";
		
		this.roomCreated = false;
		this.roomHidden = false;
		this.roomInvalid = false;
		this.roomName = "Room";
		this.roomPort = Constants.DEFAULT_PORT;
		
		this.graphwar = graphwar;

	}
	
	public String getLocalPlayerName()
	{
		return this.localPlayer;
	}
	
		
	public void joinGlobalServer(String ip, int port, String playerName) throws IOException
	{
		connection = new Connection(ip, port);
		
		connection.sendMessage(URLEncoder.encode(playerName,"UTF-8"));
		
		this.localPlayer = playerName;
					
		this.running = true;
		new Thread(this).start();
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).refreshGlobalButton();
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshGlobalButton();
	}
	
	public String getIP(int roomNum)
	{
		return rooms.get(roomNum).getIp();
	}
	
	public int getNumRooms()
	{
		return rooms.size();
	}
	
	public int getPort(int roomNum)
	{
		return rooms.get(roomNum).getPort();
	}
	
	public String[] getGlobalPlayers()
	{
		String[] names;
		int i=0;
		
		synchronized(players)
		{
			names = new String[this.players.size()];
		
			ListIterator<LobbyPlayer> itr = players.listIterator();
		    				
	    	while(itr.hasNext())
	    	{
	    		LobbyPlayer player = itr.next();
	    		
	    		if(player.getName().equals("SERVERPEWPEW")==false)
	    		{
	    			names[i] = player.getName();
	    			i++;
	    		}	    		
	    	}	
		}
    	
    	names = Arrays.copyOf(names, i);
				
		return names;
	}
	
	public boolean isRoomInvalid()
	{
		return roomInvalid;
	}
	
	public List<Room> getRooms()
	{
		return this.rooms;
	}
	
	private void addPlayer(String name, int id)
	{
		LobbyPlayer player = new LobbyPlayer(name, id);
		
		synchronized(players)
		{
			this.players.add(player);
		}
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshPlayers();
	}
	
	private void removePlayer(int id)
	{	
		synchronized(players)
		{
			ListIterator<LobbyPlayer> itr = players.listIterator();
	    	
			while(itr.hasNext())
	    	{
	    		LobbyPlayer player = itr.next();
	    		
	    		if(player.getID() == id)
	    		{
	    			players.remove(player);
	    			break;
	    		}
	    	}	
		}
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshPlayers();	
	}
	
	private void removeRoom(int id)
	{
		ListIterator<Room> itr = rooms.listIterator();
	    	
		while(itr.hasNext())
    	{
			Room room = itr.next();
    		
    		if(room.getRoomID() == id)
    		{
    			rooms.remove(room);
    			break;
    		}
    	}	
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshRooms();
	}
	
	private void updateRoom(int id, int gameMode, int numPlayers)
	{	
		ListIterator<Room> itr = rooms.listIterator();
    	
		while(itr.hasNext())
    	{
			Room room = itr.next();
    		
    		if(room.getRoomID() == id)
    		{
    			room.updateRoom(numPlayers, gameMode);
    			break;
    		}
    	}	
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshRooms();
	}
	
	private void addRoom(String name, int roomID, String ipAddress, int port, int mode, int numPlayers)
	{
		Room room = new Room(name, roomID, ipAddress, port, mode, numPlayers);
		
		this.rooms.add(room);			
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshRooms();
	}
	
	private void addToChat(int playerId, String chatMessage)
	{
		String playerName = "Anon";
		
		synchronized(players)
		{
			ListIterator<LobbyPlayer> itr = players.listIterator();
		    	
			while(itr.hasNext())
	    	{
	    		LobbyPlayer player = itr.next();
	    		
	    		if(player.getID() == playerId)
	    		{
	    			playerName = player.getName();
	    			break;
	    		}
	    	}	
		}
			
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).addChat(playerName, chatMessage);
	}
	
	private void handleMessage(String message)
	{		
		//System.out.println("Received from "+"server: "+message);
		
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
					
				}break;
			
				case NetworkProtocol.JOIN:
				{
					if(info.length == 3)
					{
						try
						{
							String name = URLDecoder.decode(info[1], "UTF-8");
							int id = Integer.parseInt(info[2]);
							
							addPlayer(name, id);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
				}break;
				
				case NetworkProtocol.SAY_CHAT:
				{
					if(info.length == 3)
					{
						try
						{
							int id = Integer.parseInt(info[1]);
							String chatMessage = URLDecoder.decode(info[2], "UTF-8");
							
							addToChat(id, chatMessage);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
				}break;
				
				case NetworkProtocol.ROOM_STATUS:
				{
					if(info.length == 4)
					{
						int roomID = Integer.parseInt(info[1]);
						int gameMode = Integer.parseInt(info[2]);
						int numPlayers = Integer.parseInt(info[3]);
						
						updateRoom(roomID, gameMode, numPlayers);
					}
				}break;
				
				case NetworkProtocol.CREATE_ROOM:
				{
					if(info.length == 5)
					{
						try
						{
							String roomName = URLDecoder.decode(info[1], "UTF-8");
							int roomID = Integer.parseInt(info[2]);
							String ipAddress = URLDecoder.decode(info[3], "UTF-8");
							int port = Integer.parseInt(info[4]);
							
							addRoom(roomName, roomID, ipAddress, port, Constants.NORMAL_FUNC, 0);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
				}break;
				
				case NetworkProtocol.LIST_PLAYERS:
				{
					if(info.length >= 2)
					{
						players.clear();
						
						int numPlayers = Integer.parseInt(info[1]);
						
						for(int i=0; i<numPlayers; i++)
						{
							try
							{
								String name = URLDecoder.decode(info[2+2*i], "UTF-8");
								int id = Integer.parseInt(info[3+2*i]);
								
								addPlayer(name, id);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}							
						}
					}
				}break;
				
				case NetworkProtocol.LIST_ROOMS:
				{
					if(info.length >= 2)
					{
						rooms.clear();
						
						int numRooms = Integer.parseInt(info[1]);
						
						for(int i=0; i<numRooms; i++)
						{
							try
							{
								String roomName = URLDecoder.decode(info[2+6*i], "UTF-8");
								int roomID = Integer.parseInt(info[3+6*i]);
								String ipAddress = URLDecoder.decode(info[4+6*i], "UTF-8");
								int port = Integer.parseInt(info[5+6*i]);
								int mode = Integer.parseInt(info[6+6*i]);
								int numPlayers = Integer.parseInt(info[7+6*i]);
																
								addRoom(roomName, roomID, ipAddress, port, mode, numPlayers);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}							
						}
					}
				}break;
				
				case NetworkProtocol.CLOSE_ROOM:
				{
					if(info.length == 2)
					{
						int roomID = Integer.parseInt(info[1]);
						
						removeRoom(roomID);
					}
				}break;
				
				case NetworkProtocol.ROOM_INVALID:
				{
					this.roomInvalid = true;
					
					graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN).repaint();
				}break;
				
				case NetworkProtocol.QUIT:
				{
					if(info.length == 2)
					{
						int playerID = Integer.parseInt(info[1]);
						
						removePlayer(playerID);
					}
				}break;
				
			}
		}
	}
	
	
	public void sendChatMessage(String chatMessage)
	{	
		try
		{
			String message = NetworkProtocol.SAY_CHAT+"&"+URLEncoder.encode(chatMessage, "UTF-8");			
			connection.sendMessage(message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	
	public void recreateRoom()
	{
		if(this.roomHidden == true)
		{
			this.roomCreated = true;
			this.roomHidden = false;
			this.roomInvalid = false;
		
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
		if(roomCreated)
		{
			closeRoom();
		}
		
		quitGlobal();
		disconnect(false);		
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).refreshGlobalButton();
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshGlobalButton();
	}
	
	public void sendRoomStatus()
	{
		if(roomCreated)
		{
			String message = NetworkProtocol.ROOM_STATUS+"&"+this.graphwar.getGameData().getGameMode()+"&"+this.graphwar.getGameData().getPlayers().size();
			
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

	private void disconnect(boolean kick)
	{		
		if(kick && running)
		{
			((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).showDisconnectMessage("You have been disconnected.");
		}
		
		running = false;
		
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshGlobalButton();
			
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
					disconnect(true);
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
					disconnect(true);
				}
				else
				{
					sendKeepAlive();
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				
				disconnect(true);
			}
		}
	}
}

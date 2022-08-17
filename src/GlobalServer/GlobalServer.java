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
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import GraphServer.Connection;
import GraphServer.Constants;
import GraphServer.NetworkProtocol;


public class GlobalServer implements Runnable
{
	ServerSocket serverSocket;
	private List<LobbyPlayer> players;
	private List<Room> rooms;
		
	private long lastRoomCheck;
	
	public GlobalServer()
	{
		this.players = new Vector<LobbyPlayer>();
		this.rooms = new Vector<Room>();
		
		lastRoomCheck = System.currentTimeMillis();
       
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(Constants.GLOBAL_PORT));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void registerNewPlayer(LobbyPlayer newPlayer)
	{
		if(newPlayer.isDummy() == false)
		{		
			String message = NetworkProtocol.JOIN+"&"+newPlayer.getName()+"&"+newPlayer.getID();
			
			sendMessageAll(message);
		}
	}
	
    private void sendChat(int playerID, String chatMessage)
    {
    	String message = NetworkProtocol.SAY_CHAT+"&"+playerID+"&"+chatMessage;
    	
    	sendMessageAll(message);
    }
	    
    private void sendNewRoom(Room room)
    {
    	String message = NetworkProtocol.CREATE_ROOM+"&"+room.getName()+"&"+room.getRoomID()+"&"+room.getIp()+"&"+room.getPort();
    	
    	sendMessageAll(message);
    }
    
    public void sendListPlayers(LobbyPlayer player)
    {
    	String message = "";
    	
    	int i=0;
    	
    	synchronized(players)
    	{
	    	ListIterator<LobbyPlayer> itr = players.listIterator();
	    	
	    	while(itr.hasNext())
	    	{
	    		LobbyPlayer tempPlayer = itr.next();
	    		
	    		if(tempPlayer.isDummy()==false)
	    		{
	    			message = message + "&" + tempPlayer.getName() + "&" + tempPlayer.getID();
	    			i++;
	    		}    		
	    	}
    	}
	    	
    	message = NetworkProtocol.LIST_PLAYERS+"&"+i+message;
    	
    	player.sendMessage(message);
    }
    
    public void sendListRooms(LobbyPlayer player)
    {
    	String message = "";    	
    	
    	int i=0;
    	
    	synchronized(rooms)
		{
	    	ListIterator<Room> itr = rooms.listIterator();	    	
	    	while(itr.hasNext())
	    	{
	    		Room room = itr.next();
	    		
	    		message = message +"&"+room.getName()+"&"+room.getRoomID()+"&"+room.getIp()+"&"+room.getPort()+"&"+room.getGameMode()+"&"+room.getNumPlayers();
	    		
	    		i++;
	    	}
		}
	    	
    	message = NetworkProtocol.LIST_ROOMS+"&"+i+message;
    	
    	player.sendMessage(message);
    }
    
    public void updateRoom(Room room)
    {
    	String message = NetworkProtocol.ROOM_STATUS+"&"+room.getRoomID()+"&"+room.getGameMode()+"&"+room.getNumPlayers();
    	
    	sendMessageAll(message);
    }
    
    private void sendMessageAll(String message)
    {
    	//System.out.println("Sent to everyone: "+message);
    	
    	synchronized(players)
    	{
	    	ListIterator<LobbyPlayer> itr = players.listIterator();
	    	
	    	while(itr.hasNext())
	    	{
	    		LobbyPlayer player = itr.next();
	    		
	    		player.sendMessage(message);
	    	}
    	}
    }
    
   
    public void removePlayer(LobbyPlayer player)
    {
    	String message = NetworkProtocol.QUIT+"&"+player.getID();
    	sendMessageAll(message);
    	
    	player.disconnect();
    	
    	synchronized(players)
    	{
    		players.remove(player);
    	}
    	
    	if(player.getRoom() != null)
    	{
    		removeRoom(player.getRoom());
    	}
    }
    
    public void removeRoom(Room room)
    {
		String message = NetworkProtocol.CLOSE_ROOM+"&"+room.getRoomID();
		sendMessageAll(message);
		
		synchronized(rooms)
		{
			rooms.remove(room);
		}
		
    }
    
    private boolean tryConnection(Room room)
    {
    	try
		{
			Connection connection = new Connection(room.getIp(), room.getPort());
			String message = NetworkProtocol.DISCONNECT+"";    	
	    	connection.sendMessage(message);
	    	connection.close();
	    	return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}    	
    }
    
    private void sendRoomInvalid(LobbyPlayer player)
    {
    	String message = NetworkProtocol.ROOM_INVALID+"";
    	
    	player.sendMessage(message);
    }
    
	public synchronized void handleMessage(String message, LobbyPlayer player)
	{		
		String[] info = new String[0];

		if(message != null)
		{
			info = message.split("&");
		}
		
		if(info.length > 0)
		{
			int code = Integer.parseInt(info[0]);
			
			if(code != NetworkProtocol.NO_INFO)
			{
				System.out.println("Received from "+player.getID()+": "+message);				
			}
			
			switch(code)
			{				
				case NetworkProtocol.NO_INFO:
				{
					player.sendMessage(NetworkProtocol.NO_INFO+"");
				}break;
			
				case NetworkProtocol.SAY_CHAT:
				{
					if(info.length == 2)
					{
						String chatMessage = info[1];
						
						sendChat(player.getID(), chatMessage);						
					}
				}break;
				
				case NetworkProtocol.ROOM_STATUS:
				{
					if(info.length == 3)
					{
						int gameMode = Integer.parseInt(info[1]);
						int numPlayers = Integer.parseInt(info[2]);
						
						Room room = player.getRoom();
						
						if(room != null)
						{
							room.updateRoom(numPlayers, gameMode);
							updateRoom(player.getRoom());
						}						
					}
				}break;
				
				case NetworkProtocol.CREATE_ROOM:
				{
					if(info.length == 3)
					{
						String roomName = info[1];
						int portNumber = Integer.parseInt(info[2]);
						String ipAddress = "";
						try 
						{
							ipAddress = URLEncoder.encode(player.getIpAddress(), "UTF-8");
						} catch (UnsupportedEncodingException e) 
						{
							e.printStackTrace();
						}
						
						Room room = new Room(roomName, ipAddress, portNumber);
						
						if(tryConnection(room))
						{
							synchronized(rooms)
							{
								rooms.add(room);	
							}
							
							player.setRoom(room);
							
							sendNewRoom(room);
						}
						else
						{
							sendRoomInvalid(player);
						}
					}
				}break;
				
				case NetworkProtocol.QUIT:
				{
					if(info.length == 1)
					{			
						if(player.getRoom() != null)
						{
							removeRoom(player.getRoom());	
						}
						
						removePlayer(player);												
					}
					
				}break;
				
				case NetworkProtocol.CLOSE_ROOM:
				{
					if(info.length == 1)
					{						
						if(player.getRoom() != null)
						{
							removeRoom(player.getRoom());	
						}					
					}
				}break;
				
			}
		}
	}
	
	private void waitConnection() throws IOException
	{	
		
		Socket tempSocket = null;
		boolean connected = false;
		
		try
		{			
			tempSocket = serverSocket.accept();
			connected = true;
			System.out.println("Connection accepted!");			
		}
		catch(Exception e)
		{
			connected = false;
			
			e.printStackTrace();
		}
	
		if(connected)
		{
			Connection connection = new Connection(tempSocket);
			
			LobbyPlayer player = new LobbyPlayer(connection, this);
			new Thread(player).start();
			
			synchronized(players)
			{
				this.players.add(player);
			}
		}
	}
	
	public void restartServer()
	{
		this.players = new Vector<LobbyPlayer>();
		this.rooms = new Vector<Room>();
       
		try
		{
			serverSocket.close();

			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(Constants.GLOBAL_PORT));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
		
	private void checkRooms()
	{
		if(System.currentTimeMillis() - lastRoomCheck > 5*60*1000)
		{
			lastRoomCheck = System.currentTimeMillis();
			
			synchronized(rooms)
			{
				ListIterator<Room> itr = rooms.listIterator();	    	
		    	while(itr.hasNext())
		    	{
		    		Room room = itr.next();
		    		
		    		boolean roomOk = false;
		    		
		    		ListIterator<LobbyPlayer> pitr = players.listIterator();		    	
			    	while(pitr.hasNext())
			    	{
			    		LobbyPlayer tempPlayer = pitr.next();
			    		
			    		if(tempPlayer.getRoom() == room)
			    		{
			    			roomOk = true;
			    			break;
			    		}
			    	}
			    	
			    	if(roomOk == false)
			    	{
			    		System.out.println("Removing room: "+room.getName());
			    		
			    		itr.remove();
			    	}
		    	}
		    	
			}
			
			ListIterator<LobbyPlayer> pitr = players.listIterator();		    	
	    	while(pitr.hasNext())
	    	{
	    		LobbyPlayer tempPlayer = pitr.next();
	    		
	    		sendListRooms(tempPlayer);
	    	}			
		}
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				System.out.println("Global server started");
				
				while(true)
				{
					try 
					{
						waitConnection();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					
					checkRooms();
				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				restartServer();
			}
		}
			
	}
	
		
	public static void handleArgs(String[] args)
	{
		if(args.length > 0)
		{
			// Overrides ip to create local server
			Constants.GLOBAL_IP = args[0];
		}
	}	
	
	public static void main(String[] args)
	{
		handleArgs(args);
		
		GlobalServer server = new GlobalServer();
		
		new Thread(server).start();
	}

}

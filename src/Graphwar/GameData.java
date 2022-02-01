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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import GraphServer.Constants;
import GraphServer.NetworkProtocol;

public class GameData implements Runnable
{
	private Graphwar graphwar;
	
	private ServerConnection serverConnection;
	
	private List<Player> players;
	private Queue<Integer> nextPCs;	
	
	private Obstacle obstacle;
	
	private int gameMode;
	private int gameState;
	
	private boolean leader;
	
	private int currentTurn;
	private Player lastLocalHumanPlayer;
	private long timeTurnStarted;
	private boolean turnTimeUp;
	private boolean nextTurnSent;
	
	private Function function;
	private boolean drawingFunction;
	private long timeStartedDrawingFunction;
	private boolean exploding;
	private long timeStartedExploding;
	private ArrayList<Soldier> soldiersHit;
	
	private boolean angleUp;
	private boolean angleDown;
	private long timeStartedAngle;
	
	private boolean countingDown;
	private Countdowner countdowner;
	
	private boolean sayFunc;
	
	public GameData(Graphwar graphwar)
	{
		this.graphwar = graphwar;
		
		serverConnection = null;
		
		players = new ArrayList<Player>();
		nextPCs = new LinkedList<Integer>();
		
		obstacle = null;
		
		gameMode = Constants.NORMAL_FUNC;
		gameState = Constants.NONE;
		
		function = null;
		drawingFunction = false;
		exploding = false;
		soldiersHit = new ArrayList<Soldier>();
		
		this.leader = false;
		
		lastLocalHumanPlayer = null;
		currentTurn = -1;
		turnTimeUp = false;
		nextTurnSent = false;
		
		countingDown = false;
		countdowner = null;
		
		sayFunc = true;
	}
	
	public void connect(String ip, int port) throws IOException
	{
		serverConnection = new ServerConnection(this, ip, port);
		
		new Thread(serverConnection).start();
	
		gameState = Constants.PRE_GAME;
		
		drawingFunction = false;
		exploding = false;
		players = new ArrayList<Player>();
		lastLocalHumanPlayer = null;
		currentTurn = -1;
		turnTimeUp = false;
		nextTurnSent = false;
		
		new Thread(this).start();
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshGameButton();
	}
	
	public List<Player> getPlayers()
	{
		return players;
	}
	
	public Player getPlayer(int playerID)
	{
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player.getID() == playerID)
    		{
    			return player;
    		}
    	}
    		
    	return null;
	}
	
	public Player getFirstLocalPlayer()
	{
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player.isLocalPlayer())
    		{
    			return player;
    		}
    	}
    	
    	return null;
	}
	
	public boolean isTerrainReversed()
	{
		if(lastLocalHumanPlayer == null)
		{
			if(getFirstLocalPlayer()!=null)
			{
				if(getFirstLocalPlayer().getTeam() == Constants.TEAM2)
				{
					return true;
				}				
			}
			
			return false;
		}
		
		if(lastLocalHumanPlayer.getTeam() == Constants.TEAM2)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isLeader()
	{
		return this.leader;
	}
	
	public boolean isFunctionReversed()
	{
		if(getCurrentTurnPlayer().getTeam() == Constants.TEAM2)
		{
			return true;
		}
		
		return false;
	}
	
	public Obstacle getObstacle()
	{
		return obstacle;
	}
	
	public Player getCurrentTurnPlayer()
	{
		return players.get(currentTurn);
	}
	
	public int getCurrentTurnIndex()
	{
		return currentTurn;
	}
	
	public synchronized long getRemainingTime()
	{
		long time = Constants.TURN_TIME - (System.currentTimeMillis() - timeTurnStarted);
		
		if(drawingFunction || exploding)
		{
			time = Constants.TURN_TIME - (timeStartedDrawingFunction - timeTurnStarted);
		}
		
		if(time < 0)
		{
			time = 0;
			
			if(turnTimeUp == false)
			{
				turnTimeUp = true;
				
				if(gameState == Constants.GAME)
				{
					String message = NetworkProtocol.TIME_UP+"";
					serverConnection.sendMessage(message);
				}
			}
		}
		
		return time;
	}
	
	public void sendChatMessage(String chat)
	{
		try 
		{
			Player player = getFirstLocalPlayer();
			int id = -1;
			
			if(player != null)
			{
				id = player.getID();
			}
			
			String message = NetworkProtocol.CHAT_MSG+"&"+id+"&"+URLEncoder.encode(chat, "UTF-8");
			serverConnection.sendMessage(message);
			
			handleCommands(chat);
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void sendChatMessage(Player player, String chat)
	{
		try 
		{
			if(player.isLocalPlayer())
			{
				int id = player.getID();			
				String message = NetworkProtocol.CHAT_MSG+"&"+id+"&"+URLEncoder.encode(chat, "UTF-8");
				serverConnection.sendMessage(message);
				
				handleCommands(chat);
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}

	public void sendFunctionPreview(String functionPreview)
	{
		Player currentPlayer = getCurrentTurnPlayer();

		if(currentPlayer.isLocalPlayer() && drawingFunction == false)
		{
			try
			{
				String message = NetworkProtocol.FUNCTION_PREVIEW+"&"+currentPlayer.getID()+"&"+URLEncoder.encode(functionPreview, "UTF-8");
				serverConnection.sendMessage(message);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void sendFunction(String function)
	{
		Player currentPlayer = getCurrentTurnPlayer();
		
		if(currentPlayer.isLocalPlayer() && drawingFunction == false)
		{
			try 
			{
				@SuppressWarnings("unused")
				Function func = new Function(function);
			} 
			catch (MalformedFunction e)
			{
				//Function is wrong... dont send it
				return;
			}			
			
			try 
			{
				String message = NetworkProtocol.FIRE_FUNC+"&"+currentPlayer.getID()+"&"+URLEncoder.encode(function, "UTF-8");
				serverConnection.sendMessage(message);
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void nextMode()
	{
		String message = NetworkProtocol.NEXT_MODE+"";
		serverConnection.sendMessage(message);
	}
	
	public int getGameMode()
	{
		return gameMode;
	}
	
	public int getGameState()
	{
		return gameState;
	}
	
	public void addPlayer(String name)
	{
		if(players.size() < Constants.MAX_PLAYERS)
		{
			try 
			{
				String message = NetworkProtocol.ADD_PLAYER+"&"+URLEncoder.encode(name, "UTF-8");
				serverConnection.sendMessage(message);
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void addPC(String name, int level)
	{
		if(players.size() < Constants.MAX_PLAYERS)
		{
			nextPCs.add(new Integer(level));
			
			addPlayer(name);
		}
	}
	
	public void removePlayer(Player player)
	{
		String message = NetworkProtocol.REMOVE_PLAYER+"&"+player.getID();
		serverConnection.sendMessage(message);
	}
	
	public void addSoldier(Player player)
	{
		String message = NetworkProtocol.ADD_SOLDIER+"&"+player.getID();
		serverConnection.sendMessage(message);
	}
	
	public void removeSoldier(Player player)
	{
		String message = NetworkProtocol.REMOVE_SOLDIER+"&"+player.getID();
		serverConnection.sendMessage(message);
	}
	
	public void switchSide(Player player)
	{
		int otherTeam = Constants.TEAM1;
		
		if(player.getTeam() == Constants.TEAM1)
		{
			otherTeam = Constants.TEAM2;
		}
		
		String message = NetworkProtocol.SET_TEAM+"&"+otherTeam+"&"+player.getID();
		serverConnection.sendMessage(message);		
	}
	
	public boolean isAngleUp()
	{
		return angleUp;
	}
	
	public boolean isAngleDown()
	{
		return angleDown;
	}
	
	public void angleUp()
	{
		if(angleUp == false && this.getCurrentTurnPlayer().isLocalPlayer() && !(this.getCurrentTurnPlayer() instanceof ComputerPlayer))
		{
			this.timeStartedAngle = System.currentTimeMillis();
			this.angleUp = true;
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
		}
	}
	
	public void angleDown()
	{
		if(angleDown == false && this.getCurrentTurnPlayer().isLocalPlayer() && !(this.getCurrentTurnPlayer() instanceof ComputerPlayer))
		{
			this.timeStartedAngle = System.currentTimeMillis();
			this.angleDown = true;
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
		}
	}
	
	public void stopAngle()
	{
		if(this.getCurrentTurnPlayer().isLocalPlayer() && !(this.getCurrentTurnPlayer() instanceof ComputerPlayer))
		{
			Player player = players.get(currentTurn);
			
			double angle = getAngle();
			player.getCurrentTurnSoldier().setAngle(angle);
			
			angleUp = false;
			angleDown = false;
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
			
			String message = NetworkProtocol.SET_ANGLE+"&"+player.getID()+"&"+player.getCurrentTurnSoldierIndex()+"&"+angle;
			serverConnection.sendMessage(message);
		}
	}
	
	public void setAngle(double angle)
	{
		if(this.getCurrentTurnPlayer().isLocalPlayer())
		{
			Player player = players.get(currentTurn);
			
			player.getCurrentTurnSoldier().setAngle(angle);
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
			
			String message = NetworkProtocol.SET_ANGLE+"&"+player.getID()+"&"+player.getCurrentTurnSoldierIndex()+"&"+angle;
			serverConnection.sendMessage(message);
		}
	}
	
	public double getAngle()
	{
		long angleTime = System.currentTimeMillis() - timeStartedAngle;
		
		double acceleration = Constants.ANGLE_ACCELERATION;
		
		if(angleDown)
		{
			acceleration = -acceleration;
		}
		
		double deltaAngle = acceleration*angleTime*angleTime;
		
		double currentAngle = players.get(currentTurn).getCurrentTurnSoldier().getAngle()+deltaAngle;
		
		if(currentAngle > Math.PI/2)
		{
			currentAngle = Math.PI/2;
		}
		
		if(currentAngle < -Math.PI/2)
		{
			currentAngle = -Math.PI/2;
		}
		
		return currentAngle;
	}
	
	public void setReady(Player player, boolean ready)
	{
		int readyInt = 0;
		if(ready)
		{
			readyInt = 1;
		}
		
		String message = NetworkProtocol.SET_READY+"&"+player.getID()+"&"+readyInt;
		serverConnection.sendMessage(message);
	}
	
	private boolean checkGameFinished()
	{
		boolean team1Alive = false;
		boolean team2Alive = false;
		
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		for(int j=0; j<player.getNumSoldiers(); j++)
			{
				if(player.getSoldiers()[j].isAlive())
				{
					if(player.getTeam() == Constants.TEAM1)
					{
						team1Alive = true;
					}
					else
					{
						team2Alive = true;
					}
				}
			}
    	}
		
		if(team1Alive==false || team2Alive==false)
		{
			return true;
		}
		
		return false;
	}
	
	private void nextTurn()
	{
		if(checkGameFinished())
		{
			String message = NetworkProtocol.GAME_FINISHED+"";
			serverConnection.sendMessage(message);
		}
		else
		{
			String message = NetworkProtocol.READY_NEXT_TURN+"";
			serverConnection.sendMessage(message);	
		}
	}
	
	private void addSoldierMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		
		Player player = this.getPlayer(playerID);
		
		player.setSoldiers(player.getNumSoldiers()+1);
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).updatePlayer(player);		
	}
	
	private void removeSoldierMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		
		Player player = this.getPlayer(playerID);
		
		player.setSoldiers(player.getNumSoldiers()-1);
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).updatePlayer(player);		
	}
	
	private void removePlayerMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		
		Player player = this.getPlayer(playerID);
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).removePlayer(player);
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(null,player.getName()+" has left the game.");
		
		if(gameState == Constants.GAME)
		{
			if(players.get(currentTurn).getID() == player.getID() && this.drawingFunction==false)
			{
				nextTurn();
			}
			
			for(int i=0; i<player.getNumSoldiers(); i++)
			{
				if(player.getSoldiers()[i].isAlive())
				{
					player.getSoldiers()[i].setAlive(false);
					player.getSoldiers()[i].setExploding(true);
				}
			}
			
			player.markDisconnected();
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).addChat(null,player.getName()+" has left the game.");
			
		}
		else
		{		
			players.remove(player);
			graphwar.getGlobalClient().sendRoomStatus();
			
			if(checkHaveLocals() == false)
			{
				disconnectKick();
			}
		}
	}
	
	private boolean checkHaveLocals()
	{
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player.isLocalPlayer())
    		{
    			return true;
    		}
    	}
    	
    	return false;
	}
	
	private void setSideMessage(String[] info) throws Exception
	{
		int team = Integer.parseInt(info[1]);
		int playerID = Integer.parseInt(info[2]);
		
		Player player = this.getPlayer(playerID);
		
		player.setTeam(team);
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).updatePlayer(player);
	}
	
	private void addPlayerMessage(String[] info) throws Exception 
	{
		int playerID = Integer.parseInt(info[1]);
		String name = URLDecoder.decode(info[2], "UTF-8");
		int team = Integer.parseInt(info[3]);
		boolean local = Integer.parseInt(info[4]) != 0;
		int numSoldiers = Integer.parseInt(info[5]);
		boolean ready = Integer.parseInt(info[6]) != 0;
			
		Player player;
		
		if(local && !nextPCs.isEmpty())
		{
			int level = nextPCs.poll().intValue();			
			player = new ComputerPlayer(name, playerID, team, local, numSoldiers, ready, level, graphwar);
		}
		else
		{
			player = new Player(name, playerID, team, local, numSoldiers, ready);							
		}
		players.add(player);
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addPlayer(player);
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(null,player.getName()+" has joined the game.");
		
		graphwar.getGlobalClient().sendRoomStatus();
	}
	
	private void addChatMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		String chatMessage = URLDecoder.decode(info[2], "UTF-8");
		
		Player player = this.getPlayer(playerID);
		
		if(gameState == Constants.PRE_GAME)
		{
			((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(player, chatMessage);
		}
		else if(gameState == Constants.GAME)
		{
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).addChat(player, chatMessage);
		}
		
		//handleCommands(chatMessage);
	}
	
	private void handleCommands(String msg)
	{
		if(msg.startsWith("-"))
		{			
			if(msg.compareToIgnoreCase("-sayfunc")==0)
			{
				sayFunc = true;
			}
			else if(msg.compareToIgnoreCase("-stopsayfunc")==0)
			{
				sayFunc = false;
			}	
			else if(msg.compareToIgnoreCase("-shownext")==0)
			{
				((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).setNextMarker(true);
			}
			else if(msg.compareToIgnoreCase("-stopshownext")==0)
			{
				((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).setNextMarker(false);
			}	
		}
	}
	
	private void setModeMessage(String[] info) throws Exception
	{
		int mode = Integer.parseInt(info[1]);
		
		this.gameMode = mode;
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).setMode(mode);
		
		graphwar.getGlobalClient().sendRoomStatus();
	}
	
	private void setReadyMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		boolean ready = Integer.parseInt(info[2])!=0;
		
		Player player = this.getPlayer(playerID);
		
		player.setReady(ready);
		
		if(gameState == Constants.PRE_GAME)
		{
			((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).repaint();
		}
		
		if(player.isLocalPlayer())
		{
			updateReadyButton();
		}
		
		if(ready == false && countingDown==true)
		{
			if(countdowner != null)
			{
				countdowner.stop();
			}
				
			displaySystemMessage("Game start cancelled.");
		}
	}
	
	private void updateReadyButton()
	{		
		ListIterator<Player> itr = players.listIterator();
    	
		boolean readyOn = true;
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player.isLocalPlayer())
    		{
    			if(player.getReady() == false)
    			{
    				readyOn = false;
    				break;
    			}
    		}
    	}
    	
    	((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).setReadyButtonOn(readyOn);
		
	}
	
	private void stopComputers()
	{
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player instanceof ComputerPlayer)
    		{
    			((ComputerPlayer) player).stopThinkFunction();
    		}
    	}
	}
	
	private void startGameMessage(String[] info) throws Exception
	{
		graphwar.getGlobalClient().hideRoom();
		
		stopComputers();
		
		this.gameState = Constants.GAME;
		
		this.drawingFunction = false;
		this.exploding = false;
		
		int numCircles = Integer.parseInt(info[1]);
		
		int[] circles = new int[numCircles*3];
		
		for(int i=0; i<numCircles*3; i++)
		{
			circles[i] = Integer.parseInt(info[2+i]);
		}
				
		obstacle = new Obstacle(numCircles, circles);
					
		ListIterator<Player> itr = players.listIterator();
    	
		int i = 3*numCircles+2;	// Index of first soldier coordinate
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		int numSoldiers = player.getNumSoldiers();
    		
    		for(int s=0; s<numSoldiers; s++)
    		{
    			int x = Integer.parseInt(info[i]);
    			int y = Integer.parseInt(info[i+1]);
    			i += 2;
    			
    			player.startSoldier(s, x, y);
    		}
    		
    		player.restartTurn();
    	}
		
    	int startPlayer = Math.abs(Integer.parseInt(info[i]));
    
    	startPlayer = startPlayer%players.size();
    	
    	currentTurn = startPlayer;
    	
    	timeTurnStarted = System.currentTimeMillis();
    	
    	Player currentPlayer = players.get(currentTurn);
    	if(currentPlayer.isLocalPlayer())
    	{
    		if(currentPlayer instanceof ComputerPlayer)
			{
				((ComputerPlayer) currentPlayer).thinkFunction();
			}
    		else
    		{
    			lastLocalHumanPlayer = currentPlayer; 
    		}
    	}
    	
		graphwar.getUI().setScreen(Constants.GAME_SCREEN);
				
		
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshFunction();
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshSoldiers();
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshBack();
		
	}
	
	private void nextTurnMessage(String[] info) throws Exception
	{		
		if(checkGameFinished())
		{
			String message = NetworkProtocol.GAME_FINISHED+"";
			serverConnection.sendMessage(message);
		}
		
		int numPlayers = players.size();
		for(int i=0; i<numPlayers; i++)
		{
			currentTurn = (currentTurn+1)%numPlayers;
			
			//if the currentTurn player has soldiers alive, then it's his turn
			//if not it's the next player turn
			if(players.get(currentTurn).nextTurn())
			{
				break;
			}
		}
		
		Player currentPlayer = players.get(currentTurn);
		if(currentPlayer.isLocalPlayer())
		{
			if(currentPlayer instanceof ComputerPlayer)
			{
				((ComputerPlayer) currentPlayer).thinkFunction();
			}
    		else
    		{
    			lastLocalHumanPlayer = currentPlayer; 
    		}
		}
		
		timeTurnStarted = System.currentTimeMillis();
		turnTimeUp = false;
		this.drawingFunction = false;
		this.exploding = false;
		nextTurnSent = false;
		
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshFunction();
		
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).refreshBack();
	}
	
	private void fireFunctionMessage(String[] info) throws Exception
	{
		if(info.length == 3 && drawingFunction == false)
		{
			int playerID = Integer.parseInt(info[1]);
			String function = URLDecoder.decode(info[2], "UTF-8");
						
			if(players.get(currentTurn).getID() == playerID)
			{
				Player player = getPlayer(playerID);
				
				processFunction(player, function);
				
				this.drawingFunction = true;
				this.timeStartedDrawingFunction = System.currentTimeMillis();	
			
				((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).startDrawingFunction();
								
				if(sayFunc)
				{
					((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).addChat(player, function);
				}
			}
		}
	}

	private void updateFunctionMessage(String[] info) throws Exception
	{
		if(info.length == 3 && drawingFunction == false)
		{
			int playerID = Integer.parseInt(info[1]);
			String function = URLDecoder.decode(info[2], "UTF-8");

			Player player = getPlayer(playerID);

			if(players.get(currentTurn).getID() == playerID && player.isLocalPlayer() == false)
			{
				((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).updateFunction(function);
			}
		}
	}
	
	public boolean isDrawingFunction()
	{
		return drawingFunction;
	}
	
	public Function getFunction()
	{
		return function;
	}

	public boolean isExploding()
	{
		return exploding;
	}
	
	public synchronized long getTimeExploding()
	{
		long time = System.currentTimeMillis()-timeStartedExploding;
		
		if(time > Constants.NEXT_TURN_DELAY)
		{			
			if(exploding==true && nextTurnSent==false)
			{
				nextTurn();
				nextTurnSent = true;
			}
		}
		
		return time;
	}
	
	public void updateDrawingStuff()
	{
		if(isDrawingFunction())
		{
			getCurrentFunctionPosition();
		}
		
		if(isExploding())
		{
			getTimeExploding();
		}
		
		getRemainingTime();
	}
	
	public synchronized int getCurrentFunctionPosition()
	{
		if(exploding)
		{
			return function.getNumSteps();
		}
		
		int numDrawSteps = (int)((System.currentTimeMillis() - timeStartedDrawingFunction)*Constants.FUNCTION_VELOCITY)/1000;
		
		if(numDrawSteps > function.getNumSteps() && drawingFunction)
		{
			numDrawSteps = function.getNumSteps();
			
			exploding = true;
			timeStartedExploding = System.currentTimeMillis();
						
			if(isFunctionReversed())
			{
				obstacle.setExplosion(Constants.PLANE_LENGTH - (int)function.getLastX(), (int)function.getLastY(), Constants.EXPLOSION_RADIUS);
			}
			else
			{
				obstacle.setExplosion((int)function.getLastX(), (int)function.getLastY(), Constants.EXPLOSION_RADIUS);
			}
			
			obstacle.explodePoint();
			//this.drawingFunction = false;
			//nextTurn();
		}
		
		ListIterator<Soldier> itr = soldiersHit.listIterator();
		
		while(itr.hasNext())
		{
			Soldier soldier = itr.next();
			
			if(soldier.isAlive() == true)
			{
				if(soldier.isExploding())
				{
					if(soldier.getTimeExploding() > Constants.SOLDIER_MAX_DEATH_TIME)
					{
						soldier.setExploding(false);
					}
				}		
				else if(numDrawSteps > soldier.getKillPosition())
				{
					soldier.setExploding(true);
					soldier.setAlive(false);
				}
			}
		}
		
		return numDrawSteps;
	}
	
	private void processFunction(Player player, String functionString) throws MalformedFunction
	{				
		function = new Function(functionString);
								
		player.getCurrentTurnSoldier().setFunction(functionString);
								
		//If player is in team 2 the function must go in the other direction
		//and the obstacles are inverted
		if(player.getTeam()==Constants.TEAM1)
		{			
			switch(gameMode)
			{
				case Constants.NORMAL_FUNC:
					function.processFunctionRange(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, false);	
				break;
				case Constants.FST_ODE:
					function.processRK4Range(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, false);
				break;
				case Constants.SND_ODE:
					function.processRK42Range(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, player.getCurrentTurnSoldier().getAngle(),false);
				break;
			}			
		}
		else
		{
			switch(gameMode)
			{
				case Constants.NORMAL_FUNC:
					function.processFunctionRange(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, true);	
				break;
				case Constants.FST_ODE:
					function.processRK4Range(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, true);
				break;
				case Constants.SND_ODE:
					function.processRK42Range(obstacle, players.toArray(new Player[0]), players.size(), currentTurn, player.getCurrentTurnSoldier().getAngle(),true);
				break;
			}			
		}
		
		soldiersHit = new ArrayList<Soldier>();
		int numPlayersHit = function.getNumPlayersHit();
		for(int i=0; i<numPlayersHit; i++)
		{
			Soldier soldier;
			
			soldier = players.get(function.getPlayerHit(i)).getSoldiers()[function.getSoldierHit(i)];
			soldier.setKillPosition(function.getSoldierHitPosition(i));
			soldiersHit.add(soldier);
		}
		
		player.getCurrentTurnSoldier().setAngle(function.getFireAngle());
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
					
	}
	
	private void removeDisconnectedPlayers()
	{
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		if(player.isDisconnected())
    		{
    			itr.remove();    			
    		}
    	}
	}
	
	private void finishGameMessage(String[] info) throws Exception
	{
		this.gameState = Constants.PRE_GAME;
		
		this.drawingFunction = false;
		this.exploding = false;
		this.nextTurnSent = false;
		
		stopComputers();		
		
		((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).stopPanel();
		Thread.sleep(200);	//This should be enough so that the game is not being painted when players are removed
		
		ListIterator<Player> itr = players.listIterator();
    	
    	while(itr.hasNext())
    	{
    		Player player = itr.next();
    		
    		Soldier[] soldiers = player.getSoldiers();
    		
    		for(int i=0; i<soldiers.length; i++)
    		{
    			if(soldiers[i].isAlive())
    			{
    				((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(null, player.getName()+" won the game.");
    				break;
    			}
    		}
    					
		}
			
		removeDisconnectedPlayers();
		
		updateReadyButton();
		graphwar.getUI().setScreen(Constants.PRE_GAME_SCREEN);
		graphwar.getGlobalClient().recreateRoom();
	}
	
	private void setAngleMessage(String[] info) throws Exception
	{
		int playerID = Integer.parseInt(info[1]);
		int soldierIndex = Integer.parseInt(info[2]);
		double angle = Double.parseDouble(info[3]);
		
		Player player = getPlayer(playerID);
		
		if(player.isLocalPlayer() == false)
		{
			player.getSoldiers()[soldierIndex].setAngle(angle);
			
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).repaintAngle();
		}
	}
	
	private void setLeaderMessage(String[] info) throws Exception
	{
		this.leader = true;
		
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).refreshBoard();
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(null, "You are now the room leader.");
	}
	
	private class Countdowner implements Runnable
	{
		GameData gameData;
		Thread thisThread;
		
		public Countdowner(GameData gameData)
		{
			this.gameData = gameData;
			thisThread = new Thread(this);
			thisThread.start();
		}
		
		public void stop()
		{
			countingDown = false;
			thisThread.interrupt();
		}
		
		public void run() 
		{
			countingDown = true;
			
			for(int i=Constants.START_GAME_DELAY/1000; i>0; i--)
			{
				if(countingDown)
				{
					gameData.displaySystemMessage("Game starting in "+i+"...");
				}
				else
				{
					break;
				}
				
				try
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			
			countingDown = false;
		}
		
	}
	
	protected synchronized void displaySystemMessage(String message)
	{
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).addChat(null, message);
	}
	
	private void startCountdownMessage(String[] info) throws Exception
	{
		if(countingDown==false)
		{	
			if(countdowner != null)
			{
				countdowner.stop();
			}
			
			countdowner = new Countdowner(this);			
		}
	}
	
	private void reorderMessage(String[] info) throws Exception
	{
		List<Player> newPlayers = new ArrayList<Player>();
		
		for(int i=1; i<info.length; i++)
		{
			newPlayers.add(this.getPlayer(Integer.parseInt(info[i])));
		}
		
		this.players = newPlayers;
	}
	
	public synchronized void handleMessage(String message)
	{		
		String[] info = message.split("&");		
	
		try
		{
			int type = Integer.parseInt(info[0]);
			
			//if(type != NetworkProtocol.NO_INFO)
			//{
			//	System.out.println("Message received on client: "+message);
			//}
		
			switch(type)
			{
				case NetworkProtocol.NO_INFO:
				{
					
				}break;
			
				case NetworkProtocol.ADD_PLAYER:
				{
					addPlayerMessage(info);
				}break;
				
				case NetworkProtocol.SET_TEAM:
				{
					setSideMessage(info);
				}break;
				
				case NetworkProtocol.REMOVE_PLAYER:
				{
					removePlayerMessage(info);
				}break;
				
				case NetworkProtocol.ADD_SOLDIER:
				{
					addSoldierMessage(info);
				}break;
				
				case NetworkProtocol.REMOVE_SOLDIER:
				{
					removeSoldierMessage(info);
				}break;
				
				case NetworkProtocol.CHAT_MSG:
				{
					addChatMessage(info);
				}break;
				
				case NetworkProtocol.SET_MODE:
				{
					setModeMessage(info);
				}break;
				
				case NetworkProtocol.SET_READY:
				{
					setReadyMessage(info);
				}break;
				
				case NetworkProtocol.START_GAME:
				{
					startGameMessage(info);
				}break;
				
				case NetworkProtocol.NEXT_TURN:
				{
					nextTurnMessage(info);
				}break;
				
				case NetworkProtocol.FIRE_FUNC:
				{
					fireFunctionMessage(info);
				}break;
				
				case NetworkProtocol.GAME_FINISHED:
				{
					finishGameMessage(info);
				}break;
				
				case NetworkProtocol.SET_ANGLE:
				{
					setAngleMessage(info);
				}break;
				
				case NetworkProtocol.NEW_LEADER:
				{
					setLeaderMessage(info);
				}break;
				
				case NetworkProtocol.START_COUNTDOWN:
				{
					startCountdownMessage(info);
				}break;
				
				case NetworkProtocol.REORDER:
				{
					reorderMessage(info);
				}break;

				case NetworkProtocol.FUNCTION_PREVIEW:
				{
					updateFunctionMessage(info);
				}break;
				
				case NetworkProtocol.GAME_FULL:				
				case NetworkProtocol.DISCONNECT:
				{
					serverConnection.disconnect();
					kickFromGame();
				}
			}		
		}
		catch(Exception e)
		{
			invalidMessage(message);
			e.printStackTrace();
		}
	}
	
	private void invalidMessage(String message)
	{
		System.out.println("Invalid message received: " + message);
	}
	
	public void kickFromGame()
	{
		if(this.gameState == Constants.PRE_GAME)
		{
			((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).showMessage("You have been disconnected.");
		}
		else if(this.gameState == Constants.GAME)
		{
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).showMessage("You have been disconnected.");
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).stopPanel();
		}
				
		this.gameState = Constants.NONE;
		//this.players = new ArrayList<Player>();
		this.serverConnection = null;
		this.drawingFunction = false;
		this.exploding = false;
		this.nextTurnSent = false;
		this.leader = false;		
	}
	
	public void stopGame()
	{
		this.gameState = Constants.NONE;
		//this.players = new ArrayList<Player>();
		this.serverConnection = null;
		this.drawingFunction = false;
		this.exploding = false;
		this.nextTurnSent = false;
		this.leader = false;		
		
		stopComputers();
		
		((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshGameButton();
		((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).restartScreen();
		
		
		if(graphwar.getGlobalClient().isRunning())
		{
			graphwar.getUI().setScreen(Constants.GLOBAL_ROOM_SCREEN);
		}
		else
		{
			graphwar.getUI().setScreen(Constants.MAIN_MENU_SCREEN);
		}
						
		graphwar.finishGame();
	}
	
	public void disconnectKick()
	{
		String message = NetworkProtocol.DISCONNECT+"";
		serverConnection.sendMessage(message);
		serverConnection.disconnect();
		
		kickFromGame();
	}
	
	public void disconnect()
	{
		String message = NetworkProtocol.DISCONNECT+"";
		serverConnection.sendMessage(message);
		serverConnection.disconnect();
		
		stopGame();
	}

	public void run() 
	{
		while(this.gameState != Constants.NONE)
		{
			if(this.gameState == Constants.GAME)
			{
				this.updateDrawingStuff();
			}
			
			try
			{
				Thread.sleep(2000);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}


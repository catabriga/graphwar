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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import GraphServer.Constants;


public class ComputerPlayer extends Player implements Runnable
{		
	private int numGenerations;
	private int numFunctions;
	private EvolvableFunction[] bestFunction;
	private EvolvableFunction[][] functions;
	private boolean over9000;
	//private boolean sayFunc;
	private boolean myTurn;
	
	private class EvolvableFunction implements Comparator<EvolvableFunction>
	{
		public PolishNotationFunction function;
		public double angle;
		public double points;
		
		EvolvableFunction()
		{
			function = new PolishNotationFunction();
			angle = 0;
			points = 0;
		}

		public int compare(EvolvableFunction arg0, EvolvableFunction arg1) 
		{
			if(arg0.points < arg1.points)
			{
				return 1;
			}
			if(arg0.points > arg1.points)
			{
				return -1;
			}
			else
			{
				return 0;	
			}			
		}
		
	}
	
	
	private double bestAngle;
	private String function;
	
	
	private boolean processingFunction;
	
	private Graphwar graphwar;
	
	private static Random random = new Random();
		
	public ComputerPlayer(String name, int playerID, int team, boolean localPlayer, int numSoldiers, boolean ready, int level, Graphwar graphwar)
	{
		super(name, playerID, team, localPlayer, numSoldiers, ready);
		
		this.graphwar = graphwar;
		
		this.numGenerations = level;
		if(level > 9000)
		{
			over9000 = true;
		}
		else
		{
			over9000 = false;
		}
		this.numFunctions = Constants.NUM_FUNCTIONS_AI;
		
		bestFunction = new EvolvableFunction[Constants.MAX_SOLDIERS_PER_PLAYER];
		for(int i=0; i<Constants.MAX_SOLDIERS_PER_PLAYER; i++)
		{
			bestFunction[i] = new EvolvableFunction();
			bestFunction[i].function.makeRandomFunction(Constants.NORMAL_FUNC);
			bestFunction[i].angle = Math.PI*random.nextDouble()-Math.PI/2;
		}
		
		functions = new EvolvableFunction[Constants.MAX_SOLDIERS_PER_PLAYER][numFunctions];
		
		for(int i=0; i<Constants.MAX_SOLDIERS_PER_PLAYER; i++)
		{
			for(int j=0; j<numFunctions; j++)
			{
				functions[i][j] = new EvolvableFunction();
				functions[i][j].function.makeRandomFunction(Constants.NORMAL_FUNC);
				functions[i][j].angle = Math.PI*random.nextDouble()-Math.PI/2;
			}
		}
		
		function = "0";
		bestAngle = 0;
		
		//sayFunc = false;
		myTurn = false;
		
	}
	
	public double getAngle()
	{
		return bestAngle;
	}
		
    public void thinkFunction()
    {    	
    	if(over9000)
    	{
    		myTurn = true;
    	}
    	
    	if(processingFunction == false)
    	{
    		processingFunction = true;
    	
    		new Thread(this).start();
    	}
    }
    
    public void stopThinkFunction()
    {
    	myTurn = false;
    	processingFunction = false;
    }
    
   // public void setSayFunc(boolean sayFunc)
   // {
  //  	this.sayFunc = sayFunc;
  //  }
    
    private double evaluateFunction(PolishNotationFunction ft, double angle)
    {
    	Function func = new Function(ft);
    	
    	Player[] players = graphwar.getGameData().getPlayers().toArray(new Player[0]);
    	int numPlayers = players.length;
    	
    	boolean inverted;
    	
    	if(this.team==Constants.TEAM1)
		{
    		inverted = false;
		}
		else
		{
			inverted = true;	
		}
    	
		switch(graphwar.getGameData().getGameMode())
		{
			case Constants.NORMAL_FUNC:
				func.processFunctionRange(graphwar.getGameData().getObstacle(), players, numPlayers, graphwar.getGameData().getCurrentTurnIndex(), inverted);	
			break;
			case Constants.FST_ODE:
				func.processRK4Range(graphwar.getGameData().getObstacle(), players, numPlayers, graphwar.getGameData().getCurrentTurnIndex(), inverted);
			break;
			case Constants.SND_ODE:
				func.processRK42Range(graphwar.getGameData().getObstacle(), players, numPlayers, graphwar.getGameData().getCurrentTurnIndex(), angle, inverted);
			break;
		}
    	
    	double minDistSquared = 1000000;
    	
    	double totalPoints = 0;  	
    	
    	
    	for(int i=0; i<numPlayers; i++)
    	{
    		soldiers:
    		for(int j=0; j<players[i].getNumSoldiers(); j++)
    		{
    			if(players[i].soldiers[j].isAlive())
    			{
	    			for(int k=0; k<func.getNumPlayersHit(); k++)
					{
						if(func.getPlayerHit(k) == i && func.getSoldierHit(k) == j)
						{
							if(players[i].getTeam() != this.team)
			    			{
								totalPoints += 2000000;
			    			}
							else
							{
								totalPoints -= 2000000;
							}
							
							continue soldiers;
						}
					}
	    			
	    			if(players[i].getTeam() != this.team)
	    			{    				
		    			double soldierMinDist = Double.MAX_VALUE;
		    			
		    			for(int k=0; k<func.getNumSteps(); k++)
		    			{
		    				double distX;
							double distY = (-Constants.PLANE_LENGTH*func.getY(k)/Constants.PLANE_GAME_LENGTH + Constants.PLANE_HEIGHT/2) - players[i].getSoldiers()[j].getY();
							
							if(this.team==Constants.TEAM2)
							{
								distX = Constants.PLANE_LENGTH - (Constants.PLANE_LENGTH*func.getX(k)/Constants.PLANE_GAME_LENGTH + Constants.PLANE_LENGTH/2) - players[i].getSoldiers()[j].getX();
							
								if(distX < 0)
									continue;
							}
							else
							{
								distX = (Constants.PLANE_LENGTH*func.getX(k)/Constants.PLANE_GAME_LENGTH + Constants.PLANE_LENGTH/2) - players[i].getSoldiers()[j].getX();
							
								if(distX > 0)
									continue;
							}
							
							double tempDistSquared = Math.pow(distX,2) + Math.pow(distY,2);
							
							if(tempDistSquared < soldierMinDist)
							{
								soldierMinDist = tempDistSquared;
							}
		    			}
		    			
		    			if(soldierMinDist < minDistSquared)
		    			{
		    				minDistSquared = soldierMinDist;
		    			}
	    			}
    			}
    		}
    	}
    	
    	totalPoints += 1000000-minDistSquared;
    	
    	return totalPoints;
    }

    private int getFunctionToReproduce(EvolvableFunction[] functions, double totalPoints)
    {
    	double randomPoints = totalPoints*random.nextDouble();
    	
    	for(int i=0; i<functions.length; i++)
    	{
    		if(totalPoints > randomPoints)
    		{
    			totalPoints -= functions[i].points;
    		}
    		else
    		{
    			return i;
    		}
    	}
    	
    	return 0;
    }
    
    private double getTotalPoints(EvolvableFunction[] functions)
    {
    	double totalPoints = 0;
    	
    	for(int i=0; i<functions.length; i++)
    	{
    		totalPoints += functions[i].points;
    	}
    	
    	return totalPoints;
    }
    
	public void run() 
	{		
		int gameMode = graphwar.getGameData().getGameMode();
		
		EvolvableFunction[] functions = this.functions[this.getCurrentTurnSoldierIndex()];
		
		double totalPoints = 0;
		
		generationLoop:
		for(int k=0; processingFunction && graphwar.getGameData().getGameState()==Constants.GAME && ((k<numGenerations && (graphwar.getGameData().getRemainingTime()) > 5000) || (over9000)); k++)
		{
			
			if(graphwar.getGameData().getCurrentTurnPlayer() == this)
			{
				functions = this.functions[this.currentTurnSoldier];
			}
			else
			{
				int nextTurnSoldier = this.getCurrentTurnSoldierIndex();
				
				if(nextTurnSoldier == -1)
				{
					return;
				}
				else
				{
					functions = this.functions[nextTurnSoldier];
				}
			}
	    	
	    	EvolvableFunction[] newFunctions = new EvolvableFunction[numFunctions];
	    	
	    	totalPoints = getTotalPoints(functions);
	    	
	    	for(int i=0; i<Constants.NUM_FUNCTIONS_UNCHANGED_TURN_AI; i++)
	    	{
	    		//newFunctions[i] = functions[getFunctionToReproduce(functions, totalPoints)];
	    		newFunctions[i] = functions[i];
	    	}
	    	
	    	for(int i=Constants.NUM_FUNCTIONS_UNCHANGED_TURN_AI; i<Constants.NUM_FUNCTIONS_UNCHANGED_TURN_AI+Constants.NUM_FUNCTION_MUTATED_AI; i++)
	    	{
	    		newFunctions[i] = new EvolvableFunction();
	    		
	    		int funcToMutate = getFunctionToReproduce(functions, totalPoints);	    		
	    		//int funcToMutate = 0;
	    		newFunctions[i].function = new PolishNotationFunction(functions[funcToMutate].function, gameMode);
	    		newFunctions[i].angle = functions[funcToMutate].angle;
	    		
	    		if(gameMode==Constants.SND_ODE)
    			{
	    			if(random.nextBoolean())
    				{
	    				if(random.nextBoolean())
	    				{
	    					functions[i].angle = Math.PI*random.nextDouble()-Math.PI/2;
	    				}
	    				else
	    				{
	    					functions[i].angle = functions[i].angle + functions[i].angle*(random.nextDouble()-0.5)/5;
	    				}
    				}
    			}
	    	}
	    	
	    	for(int i=Constants.NUM_FUNCTIONS_UNCHANGED_TURN_AI+Constants.NUM_FUNCTION_MUTATED_AI; i<numFunctions; i++)
	    	{
	    		newFunctions[i] = new EvolvableFunction();
	    		
	    		int cross1 = getFunctionToReproduce(functions, totalPoints);	    
	    		int cross2 = getFunctionToReproduce(functions, totalPoints);	    
	    		//int cross1 = 0;	    
	    		//int cross2 = 1;
	    		newFunctions[i].function = new PolishNotationFunction(functions[cross1].function, functions[cross2].function, gameMode);
	    		
	    		if(random.nextBoolean())
	    		{
	    			newFunctions[i].angle = functions[cross1].angle;
	    		}
	    		else
	    		{
	    			newFunctions[i].angle = functions[cross2].angle;
	    		}
	    		
	    		if(gameMode==Constants.SND_ODE)
    			{
	    			if(random.nextBoolean())
    				{
	    				if(random.nextBoolean())
	    				{
	    					functions[i].angle = Math.PI*random.nextDouble()-Math.PI/2;
	    				}
	    				else
	    				{
	    					functions[i].angle = functions[i].angle + functions[i].angle*(random.nextDouble()-0.5)/5;
	    				}
    				}
    			}
	    	}
	    	
	    	EvolvableFunction[] oldFunctions = this.functions[this.currentTurnSoldier];
	    	this.functions[this.currentTurnSoldier] = newFunctions;
	    	functions = newFunctions;
	    	
	    	for(int i=0; i<numFunctions; i++)
	    	{   
	    		double points = evaluateFunction(functions[i].function,functions[i].angle);	    
	    		functions[i].points = points;
	    		
	    		//System.out.print("|");
	    		
	    		if((graphwar.getGameData().getRemainingTime()) < 3000 && !over9000)
	    		{
	    			functions = oldFunctions;
	    			this.functions[this.currentTurnSoldier] = oldFunctions;
	    			break generationLoop;
	    		}
	    	}
	    	
	    	//System.out.println();
	    	
	    	Arrays.sort(functions, functions[0]);
	    	
	    		    	
	    	if(over9000)
	    	{
	    		if(myTurn && (graphwar.getGameData().getRemainingTime()) < 5000)
	    		{
	    			bestFunction[this.currentTurnSoldier] =  functions[0];	    			
	    			sendFunction();
	    			
	    			myTurn = false;
	    		}
	    	}
	    		 	
	    			 
		}
		
		//bestFunction[this.currentSoldierTurn] =  functions[getFunctionToReproduce(functions, totalPoints)];	    	
	    if(processingFunction)
	    {
			if(over9000 == false)
			{
				bestFunction[this.currentTurnSoldier] =  functions[0];			
				sendFunction();				
			}
	    }
		
		processingFunction = false;
		
		//gameInfo.sendChatMessage(gameInfo.getCurrentTurn(), function + " " +  functions[0].points);
	}
	
	private void sendFunction()
	{		
		function = bestFunction[this.currentTurnSoldier].function.simplifyFunction().getStringFunction();
		bestAngle = bestFunction[this.currentTurnSoldier].angle;
		
		//System.out.println(this.getName()+":");
		//System.out.println(bestFunction[this.currentTurnSoldier].function.getStringFunction());
		//System.out.println(function);
		
		graphwar.getGameData().setAngle(bestAngle);
		graphwar.getGameData().sendFunction(function);
		
		//if(sayFunc)
		//{
		//	graphwar.getGameData().sendChatMessage(this, function);
		//}
		
	}

}

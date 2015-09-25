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

import javax.swing.JPanel;

import GraphServer.Constants;

public class GraphUI extends JPanel
{
	private JPanel[] screens;
	private JPanel currentScreen;
	private int currentScreenIndex;
	
	private Graphwar graphwar;
	
	public GraphUI(Graphwar graphwar) throws Exception
	{
		super();
		
		this.graphwar = graphwar;
		
		this.setLayout(null);
		
		screens = new JPanel[Constants.NUM_SCREENS];
		
		screens[Constants.MAIN_MENU_SCREEN] = new MainMenuScreen(graphwar, "/rsc/MainMenu.txt");
		screens[Constants.PRE_GAME_SCREEN] = new PreGameScreen(graphwar, "/rsc/PreGame.txt");
		screens[Constants.GLOBAL_ROOM_SCREEN] = new GlobalScreen(graphwar, "/rsc/GlobalRoom.txt");
		screens[Constants.GAME_SCREEN] = new GameScreen(graphwar, "/rsc/GameScreen.txt");
				
		currentScreenIndex = -1;
		currentScreen = null;
	}
	
	public void stop()
	{
		for(int i=0; i< screens.length; i++)
		{
			if(screens[i] instanceof StartStopPanel)
			{
				((StartStopPanel) screens[i]).stopPanel();
			}
		}
	}
	
	public void setScreen(int screenNum)
	{		
		if(currentScreenIndex != screenNum)
		{
			if(currentScreen instanceof StartStopPanel)
			{
				((StartStopPanel) currentScreen).stopPanel();
			}
			
			this.removeAll();
			this.add(screens[screenNum]);		
			this.revalidate();
			this.repaint();
			
			currentScreen = screens[screenNum];
			currentScreenIndex = screenNum;
			
			if(currentScreen instanceof StartStopPanel)
			{
				((StartStopPanel) currentScreen).startPanel();
			}
		}
	}
	
	public JPanel getScreen(int screenNum)
	{
		return screens[screenNum];
	}
	
}

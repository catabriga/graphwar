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

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import GraphServer.Constants;

public class PreGameScreen extends JPanel implements ActionListener
{
	
	private Graphwar graphwar;
	
	private JLabel[] backgroundImages;
	
	private GraphButton normalFuncButton;
	private GraphButton firstFuncButton;
	private GraphButton secondFuncButton;
	private GraphButton back;
	private GraphButton addLocalPlayer;
	private GraphButton addPCPlayer;
	private GraphButton readyOn;
	private GraphButton readyOff;
	private GraphButton global;	
	private JTextField chatField;	
	private GraphTextBox chatBox;
	private PlayerBoard playerBoard;
	
	private JLabel[] backgroundsAddLocal;
	private JTextField nameFieldAddLocal;	
	private GraphButton yesButtonAddLocal;
	private GraphButton noButtonAddLocal;
	private boolean addLocalVisible;
	
	private JLabel[] backgroundsAddPC;
	private JTextField nameFieldAddPC;
	private JTextField levelFieldAddPC;
	private GraphButton yesButtonAddPC;
	private GraphButton noButtonAddPC;
	private boolean addPCVisible;
	
	private JLabel[] backgroundsShowMessage;
	private GraphButton okButton;
	private JLabel messageLabel;
	private boolean showMessageVisible;
	
	// private AudioClip addPlayerSound;
	
	private static Font font = new Font("Sans", Font.BOLD, 12);
			
	public PreGameScreen(Graphwar graphwar, String confFile) throws InterruptedException, IOException
	{
		super();
		
		this.graphwar = graphwar;
		
		this.setLayout(null);		
		this.setBounds(0,0,Constants.WIDTH,Constants.HEIGHT);
		
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(confFile)));
		
		// Stack used to add components in reverse order, because swing draws components added first first
		// and that is not intuitive to the way the data is read from the files
		Stack<Component> components = new Stack<Component>();
		
		/////	Main Screen		/////////////
			
			int numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundImages = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundImages[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundImages[i]);
			}						
			
			normalFuncButton = GraphUtil.makeButton(graphwar, read);
			firstFuncButton = GraphUtil.makeButton(graphwar, read);
			secondFuncButton = GraphUtil.makeButton(graphwar, read);
			back = GraphUtil.makeButton(graphwar, read);
			addLocalPlayer = GraphUtil.makeButton(graphwar, read);
			addPCPlayer = GraphUtil.makeButton(graphwar, read);
			readyOn = GraphUtil.makeButton(graphwar, read);
			readyOff = GraphUtil.makeButton(graphwar, read);
			global = GraphUtil.makeButton(graphwar, read);			
			chatField = GraphUtil.makeTextField(read);			
			chatBox = GraphUtil.makeTextBox(read);
			playerBoard = GraphUtil.makePlayerBoard(graphwar, read);
						
			components.push(normalFuncButton);
			components.push(firstFuncButton);
			components.push(secondFuncButton);
			components.push(back);
			components.push(addLocalPlayer);
			components.push(addPCPlayer);
			components.push(readyOn);			
			components.push(readyOff);
			components.push(global);			
			components.push(chatField);
			components.push(chatBox);
			components.push(playerBoard);
			
			this.normalFuncButton.addActionListener(this);
			this.firstFuncButton.addActionListener(this);
			this.secondFuncButton.addActionListener(this);
			this.back.addActionListener(this);
			this.addLocalPlayer.addActionListener(this);
			this.addPCPlayer.addActionListener(this);
			this.readyOn.addActionListener(this);
			this.readyOff.addActionListener(this);
			this.global.addActionListener(this);
			this.chatField.addActionListener(this);
			
			this.firstFuncButton.setVisible(false);
			this.secondFuncButton.setVisible(false);
			this.readyOn.setVisible(false);
			this.global.setVisible(false);
			
		/////// Add Local ////////////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsAddLocal = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsAddLocal[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsAddLocal[i]);
			}							
			
			this.nameFieldAddLocal = GraphUtil.makeTextField(read);
			this.yesButtonAddLocal = GraphUtil.makeButton(graphwar, read);
			this.noButtonAddLocal = GraphUtil.makeButton(graphwar, read);
			
			components.push(nameFieldAddLocal);
			components.push(yesButtonAddLocal);
			components.push(noButtonAddLocal);
			
			showAddLocal(false);
			
			this.nameFieldAddLocal.addActionListener(this);
			this.yesButtonAddLocal.addActionListener(this);
			this.noButtonAddLocal.addActionListener(this);
			
		/////// Add PC ////////////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsAddPC = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsAddPC[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsAddPC[i]);
			}							
			
			this.nameFieldAddPC = GraphUtil.makeTextField(read);
			this.levelFieldAddPC = GraphUtil.makeTextField(read);
			this.yesButtonAddPC = GraphUtil.makeButton(graphwar, read);
			this.noButtonAddPC = GraphUtil.makeButton(graphwar, read);
			
			components.push(nameFieldAddPC);
			components.push(levelFieldAddPC);
			components.push(yesButtonAddPC);
			components.push(noButtonAddPC);
			
			showAddPC(false);
			
			this.nameFieldAddPC.addActionListener(this);
			this.levelFieldAddPC.addActionListener(this);
			this.yesButtonAddPC.addActionListener(this);
			this.noButtonAddPC.addActionListener(this);
			
		///// Show Message //////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsShowMessage = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsShowMessage[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsShowMessage[i]);
			}				
			
			this.okButton = GraphUtil.makeButton(graphwar, read);			
			this.messageLabel = GraphUtil.makeTextLabel(read);
			
			components.push(okButton);
			components.push(messageLabel);
			
			messageLabel.setFont(new Font("Sans", Font.BOLD, 20));
			messageLabel.setForeground(Color.ORANGE);
			messageLabel.setHorizontalAlignment(JLabel.CENTER);
			messageLabel.setVerticalAlignment(JLabel.CENTER);
			
			showShowMessage(false);
			
			okButton.addActionListener(this);
			
		/////////////////////////////////////
		
		this.addComponentsReversed(this, components);
		this.revalidate();
		
		
		// addPlayerSound = GraphUtil.makeAudioClip(graphwar, read);
		
	}
	
	public void setMode(int mode)
	{
		switch(mode)
		{
			case Constants.NORMAL_FUNC:
			{		
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{				
						normalFuncButton.setVisible(true);
						firstFuncButton.setVisible(false);
						secondFuncButton.setVisible(false);
					}
				}
				);
				
				//normalFuncButton.repaint();
			}break;
			
			case Constants.FST_ODE:
			{
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						normalFuncButton.setVisible(false);
						firstFuncButton.setVisible(true);
						secondFuncButton.setVisible(false);
					}
				}
				);
				
				//firstFuncButton.repaint();
			}break;
			
			case Constants.SND_ODE:
			{
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						normalFuncButton.setVisible(false);
						firstFuncButton.setVisible(false);
						secondFuncButton.setVisible(true);
					}
				}
				);				
				//secondFuncButton.repaint();
			}break;
		}
	}
	
	private void showShowMessage(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				okButton.setVisible(show);
				messageLabel.setVisible(show);
				
				for(int i=0; i<backgroundsShowMessage.length; i++)
				{
					backgroundsShowMessage[i].setVisible(show);
				}
				
				showMessageVisible = show;
				
				repaint();
			}
		}
		);
	}
	
	private void showAddPC(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				nameFieldAddPC.setVisible(show);
				levelFieldAddPC.setVisible(show);
				yesButtonAddPC.setVisible(show);
				noButtonAddPC.setVisible(show);
				addPCVisible = show;
				
				for(int i=0; i<backgroundsAddPC.length; i++)
				{
					backgroundsAddPC[i].setVisible(show);
				}
			}
		}
		);
	}
	
	private void showAddLocal(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				nameFieldAddLocal.setVisible(show);
				yesButtonAddLocal.setVisible(show);
				noButtonAddLocal.setVisible(show);
				addLocalVisible = show;
				
				for(int i=0; i<backgroundsAddLocal.length; i++)
				{
					backgroundsAddLocal[i].setVisible(show);
				}
			}
		}
		);
	}
	
	private void addComponentsReversed(JPanel panel, Stack<Component> components)
	{	
    	while(components.empty() == false)
    	{
    		panel.add(components.pop());
    	}
	}
	
	public void refreshGlobalButton()
	{
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				if(graphwar.getGlobalClient().isRunning())
				{
					global.setVisible(true);
				}
				else
				{
					global.setVisible(false);
				}
				
				repaint();
			}
		}
		);
	}
	
	private class AudioPlayer implements Runnable
	{
		private AudioClip audioClip;
		
		public AudioPlayer(AudioClip audioClip)
		{
			this.audioClip = audioClip;
			
			new Thread(this).start();
		}

		public void run()
		{
			audioClip.play();
		}
		
	}
	
	public void addPlayer(Player player)
	{
		playerBoard.addPlayer(player);
		
		// new AudioPlayer(addPlayerSound);
	}
	
	public void updatePlayer(Player player)
	{
		playerBoard.updatePlayer(player);
	}
	
	public void removePlayer(Player player)
	{
		playerBoard.removePlayer(player);
	}
	
	public void refreshBoard()
	{
		playerBoard.updateBoard();
	}
	
	public void restartScreen()
	{
		chatBox.emptyText();
		playerBoard.restartPlayers();
	}
	
	public void showMessage(String message)
	{
		this.messageLabel.setText(message);
		
		showShowMessage(true);
	}
	
	public void addChat(Player player, String chatMessage)
	{
		String name = null;
		Color color = null;
		
		if(player!=null)
		{
			name = player.getName();
			color = player.getColor();
		}
		
		chatBox.addText(name, color, chatMessage);
		this.repaint();
	}
	
	public void paintComponent(Graphics g)
	{			
		g.setColor(Constants.BACKGROUND);
		g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
		
		paintInvalidMessage(g);
	}
	
	private void paintInvalidMessage(Graphics g)
	{
		if(graphwar.getGlobalClient().isRoomInvalid())
		{
			g.setColor(Color.RED);
			g.setFont(font);
			g.drawString("This room can't be reached. Other players will not be able to join.", 5, 380);
			g.drawString("Check if the port number you are using is properly forwarded.", 15, 400);
		}
	}
	
	public void setReadyButtonOn(boolean on)
	{
		if(on)
		{
			SwingUtilities.invokeLater(
			new Runnable() 
			{
				public void run()
				{
					readyOn.setVisible(true);
					readyOff.setVisible(false);
					revalidate();
				}
			}
			);
		}
		else
		{
			SwingUtilities.invokeLater(
			new Runnable() 
			{
				public void run()
				{
					readyOn.setVisible(false);
					readyOff.setVisible(true);
					revalidate();
				}
			}
			);
		}
	}
		
	public void actionPerformed(ActionEvent arg0)
	{
		if(this.addLocalVisible)
		{
			if(arg0.getSource()==this.noButtonAddLocal)
			{
				this.showAddLocal(false);
				this.repaint();
			}
			else if(arg0.getSource()==this.yesButtonAddLocal || arg0.getSource()==nameFieldAddLocal)
			{
				String name = this.nameFieldAddLocal.getText();
				
				if(name != null && name.length()!=0)
				{
					if(name.length() > 20)
					{
						nameFieldAddLocal.setText(name.substring(0,20));
					}
					else
					{
						graphwar.getGameData().addPlayer(name);
						this.showAddLocal(false);
						this.repaint();
					}
				}
			}
		}
		else if(this.addPCVisible)
		{
			if(arg0.getSource()==this.noButtonAddPC)
			{
				this.showAddPC(false);
				this.repaint();
			}
			else if(arg0.getSource()==this.yesButtonAddPC || arg0.getSource()==nameFieldAddPC || arg0.getSource()==levelFieldAddPC)
			{
				try
				{
					String name = this.nameFieldAddPC.getText();
					int level = 0;
					
					if(name != null && name.length()!=0)
					{
						if(name.length() > 20)
						{
							nameFieldAddPC.setText(name.substring(0,20));
						}
						else
						{
							if(this.levelFieldAddPC.getText().compareToIgnoreCase("Over 9000") == 0)
							{
								level = 9001;
								
								graphwar.getGameData().addPC(name, level);
								this.showAddPC(false);
								this.repaint();
							}
							else
							{
								level = Integer.parseInt(this.levelFieldAddPC.getText());
									
								if(level > 9000)
								{
									this.levelFieldAddPC.setText("Over 9000");
								}					
								else if(name != null && name.length()!=0)
								{
									graphwar.getGameData().addPC(name, level);
									this.showAddPC(false);
									this.repaint();
								}			
							}
						}
					}
				}
				catch(NumberFormatException e)
				{
					//If contains only characters, was too big for an integer, but should transform into over9000
					String text = levelFieldAddPC.getText();
					
					boolean over9000 = true;
					for(int i=0; i<text.length(); i++)
					{
						if(text.charAt(i) > '9' || text.charAt(i) < '0')
						{
							over9000 = false;
							break;
						}
					}
					
					if(over9000)
					{
						this.levelFieldAddPC.setText("Over 9000");
					}
				}	
			}
		}
		else if(showMessageVisible)
		{
			if(arg0.getSource()==okButton)
			{
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
				
				this.showShowMessage(false);
			}
		}
		else
		{
			if(arg0.getSource()==addLocalPlayer)
			{
				this.showAddLocal(true);
				this.repaint();
			}
			else if(arg0.getSource()==addPCPlayer)
			{
				this.nameFieldAddPC.setText(Constants.computerNames[GraphUtil.random.nextInt(Constants.computerNames.length)]);
				
				int pcLevel = (int)(Constants.COMPUTER_LEVEL_MEAN_VALUE+Constants.COMPUTER_LEVEL_STANDARD_DEVIATION*GraphUtil.random.nextGaussian());
				if(pcLevel < Constants.COMPUTER_LEVEL_MIN_VALUE)
				{
					pcLevel = Constants.COMPUTER_LEVEL_MIN_VALUE;
				}
				
				this.levelFieldAddPC.setText(pcLevel+"");
				
				this.showAddPC(true);
				this.repaint();
			}
			else if(arg0.getSource()==chatField)
			{
				String text = chatField.getText();
				if(text.isEmpty()==false)
				{
					graphwar.getGameData().sendChatMessage(text);
					chatField.setText("");
				}
			}
			else if(arg0.getSource()==normalFuncButton || arg0.getSource()==firstFuncButton || arg0.getSource()==secondFuncButton)
			{
				graphwar.getGameData().nextMode();
			}
			else if(arg0.getSource()==back)
			{
				graphwar.getGameData().disconnect();
				graphwar.getGlobalClient().closeRoom();
			}
			else if(arg0.getSource()==readyOff)
			{
				List<Player> players = graphwar.getGameData().getPlayers();
	    		
	    		ListIterator<Player> pitr = players.listIterator();
	        	
	        	while(pitr.hasNext())
	        	{
	        		Player player = pitr.next();
	        		
	        		if(player.isLocalPlayer())
	        		{
	        			graphwar.getGameData().setReady(player, true);
	        		}
	        	}				
			}
			else if(arg0.getSource()==readyOn)
			{
				List<Player> players = graphwar.getGameData().getPlayers();
	    		
	    		ListIterator<Player> pitr = players.listIterator();
	        	
	        	while(pitr.hasNext())
	        	{
	        		Player player = pitr.next();
	        		
	        		if(player.isLocalPlayer())
	        		{
	        			graphwar.getGameData().setReady(player, false);
	        		}
	        	}				
			}
			else if(arg0.getSource()==global)
			{
				graphwar.getUI().setScreen(Constants.GLOBAL_ROOM_SCREEN);
			}
		}
	}

}

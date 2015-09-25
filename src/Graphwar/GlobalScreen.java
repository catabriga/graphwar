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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import GraphServer.Constants;

public class GlobalScreen extends JPanel implements ActionListener, StartStopPanel
{
	private Graphwar graphwar;
	
	private JLabel[] backgroundImages;
	
	private String message;
	private int messageX;
	private boolean showMessage;	
	
	private GraphButton createButton;
	private GraphButton gameRoomButton;
	private GraphButton backButton;	
	private JTextField chatField;	
	private GraphTextBox chatBox;
	private GlobalPlayerBoard playerBoard;
	private RoomBoard roomBoard;

	private JLabel[] backgroundsCreate;
	private JTextField nameFieldCreate;
	private JTextField portFieldCreate;
	private GraphButton yesButtonCreate;
	private GraphButton noButtonCreate;
	private boolean createVisible;
		
	private JLabel[] backgroundsShowMessage;
	private GraphButton okButton;
	private JLabel messageLabel;
	private boolean showMessageVisible;
		
	private static Font font = new Font("Sans", Font.BOLD, 12);
		
	public GlobalScreen(Graphwar graphwar, String confFile) throws InterruptedException, IOException
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
			
			createButton = GraphUtil.makeButton(graphwar, read);
			gameRoomButton = GraphUtil.makeButton(graphwar, read);
			backButton = GraphUtil.makeButton(graphwar, read);
			chatField = GraphUtil.makeTextField(read);			
			chatBox = GraphUtil.makeTextBox(read);
			
			playerBoard = new GlobalPlayerBoard(graphwar, 180, 367);
			playerBoard.setPreferredSize(new Dimension(180,367));			
			JScrollPane playerPane = new JScrollPane(playerBoard);
			playerPane.setBounds(585, 50, 200, 370);
			
			playerPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
			playerPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			roomBoard = new RoomBoard(graphwar, 380, 197);
			roomBoard.setPreferredSize(new Dimension(380,197));	
			JScrollPane roomPane = new JScrollPane(roomBoard);
			roomPane.setBounds(15, 50, 400, 200);
			
			roomPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
			roomPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			components.push(createButton);
			components.push(gameRoomButton);
			components.push(backButton);
			components.push(chatField);
			components.push(chatBox);
			components.push(playerPane);
			components.push(roomPane);
			
		
			this.createButton.addActionListener(this);
			this.gameRoomButton.addActionListener(this);
			this.backButton.addActionListener(this);
			this.chatField.addActionListener(this);
	
			this.gameRoomButton.setVisible(false);
		//////////// Create Game ///////////////////////////////////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsCreate = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsCreate[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsCreate[i]);
			}		

			this.nameFieldCreate = GraphUtil.makeTextField(read);			
			this.portFieldCreate = GraphUtil.makeTextField(read);
			this.yesButtonCreate = GraphUtil.makeButton(graphwar, read);
			this.noButtonCreate = GraphUtil.makeButton(graphwar, read);
			
			components.push(nameFieldCreate);
			components.push(portFieldCreate);
			components.push(yesButtonCreate);
			components.push(noButtonCreate);
			
			showCreateGame(false);
			
			this.nameFieldCreate.addActionListener(this);
			this.portFieldCreate.addActionListener(this);
			this.yesButtonCreate.addActionListener(this);
			this.noButtonCreate.addActionListener(this);
			
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
		
			
		///////////////////////////////////////
			
		this.addComponentsReversed(this, components);
		this.revalidate();
	}
	

	private void addComponentsReversed(JPanel panel, Stack<Component> components)
	{	
    	while(components.empty() == false)
    	{
    		panel.add(components.pop());
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
	
	public void showMessage(String message)
	{
		showMessage = true;
		this.message = message;
		
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics (Constants.NAME_FONT);		
		int messageLength = fontMetrics.stringWidth(message);
		
		this.messageX = (Constants.WIDTH - messageLength)/2;
				
		this.repaint();
	}
	
	public void showDisconnectMessage(String message)
	{
		this.messageLabel.setText(message);
		
		showShowMessage(true);
	}	
	
	public void paintComponent(Graphics g)
	{			
		g.setColor(Constants.BACKGROUND);
		g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
		paintMessage(g);
	}
	
	public void paintMessage(Graphics g)
	{
		if(showMessage)
		{
			g.setFont(font);
			g.setColor(Color.red);
			g.drawString(message, messageX, 15);
		}
	}
	
	private void showCreateGame(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
		
				nameFieldCreate.setVisible(show);
				portFieldCreate.setVisible(show);
				yesButtonCreate.setVisible(show);
				noButtonCreate.setVisible(show);
				createVisible = show;
				
				for(int i=0; i<backgroundsCreate.length; i++)
				{
					backgroundsCreate[i].setVisible(show);
				}
				
				nameFieldCreate.setText(graphwar.getGlobalClient().getLocalPlayerName()+"'s Room");
				portFieldCreate.setText(Constants.DEFAULT_PORT+"");
			}
		}
		);
	}
	
	public void refreshGameButton()
	{
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				if(graphwar.getGameData().getGameState() == Constants.NONE)
				{
					gameRoomButton.setVisible(false);
				}
				else
				{
					gameRoomButton.setVisible(true);
				}
				
				repaint();
			}
		}
		);
	}
	
	public void refreshPlayers()
	{
		this.repaint();
		//this.playerBoard.repaint();
	}
	
	public void refreshRooms()
	{
		this.repaint();
		//this.roomBoard.repaint();
	}
	
	public void addChat(String playerName, String chatMessage)
	{		
		chatBox.addText(playerName, Color.BLACK, chatMessage);
		this.repaint();
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{		
		if(this.createVisible)
		{
			if(arg0.getSource()==this.noButtonCreate)
			{
				this.showCreateGame(false);
				this.showMessage = false;
				this.repaint();
			}
			else if(arg0.getSource()==this.yesButtonCreate || arg0.getSource()==nameFieldCreate || arg0.getSource()==portFieldCreate)
			{				
				try
				{
					String name = nameFieldCreate.getText();
					int port = Integer.parseInt(portFieldCreate.getText());
					
					if(name!=null && name.length()!=0)
					{
						if(name.length() > 20)
						{
							nameFieldCreate.setText(name.substring(0,20));
						}
						else
						{
							this.showMessage("Creating...");
							
							try
							{
								graphwar.createGame(port);
								graphwar.getGlobalClient().createRoom(name, port);
								graphwar.getGameData().addPlayer(graphwar.getGlobalClient().getLocalPlayerName());							
								this.showCreateGame(false);
								this.showMessage = false;
							}
							catch (IOException e) 
							{
								this.showMessage("Failed! Check if port is not already in use.");
								
								e.printStackTrace();
							}
						}
					}
				}
				catch(NumberFormatException e)
				{
					this.showMessage("Port must be a number.");
				}				
			}
		}
		else if(showMessageVisible)
		{
			if(arg0.getSource()==okButton)
			{								
				if(graphwar.getGameData().getGameState() == Constants.NONE)
				{
					graphwar.getUI().setScreen(Constants.MAIN_MENU_SCREEN);
					
					graphwar.finishGame();					
				}
				else if(graphwar.getGameData().getGameState() == Constants.PRE_GAME)
				{
					graphwar.getUI().setScreen(Constants.PRE_GAME_SCREEN);
				}
				else if(graphwar.getGameData().getGameState() == Constants.GAME)
				{
					graphwar.getUI().setScreen(Constants.GAME_SCREEN);
				}
				
				
				this.showShowMessage(false);
			}
		}
		else 
		{
			if(arg0.getSource()==chatField)
			{
				graphwar.getGlobalClient().sendChatMessage(chatField.getText());
				chatField.setText("");
			}
			else if(arg0.getSource()==createButton)
			{
				if(graphwar.getGameData().getGameState() == Constants.NONE)
				{
					this.showCreateGame(true);
					this.repaint();
				}
			}
			else if(arg0.getSource()==backButton)
			{
				graphwar.getGlobalClient().stop();
				
				if(graphwar.getGameData().getGameState()!=Constants.NONE)
				{
					graphwar.getGameData().disconnect();
					graphwar.finishGame();
				}
				
				graphwar.getUI().setScreen(Constants.MAIN_MENU_SCREEN);
			}
			else if(arg0.getSource()==gameRoomButton)
			{
				if(graphwar.getGameData().getGameState() == Constants.PRE_GAME)
				{
					graphwar.getUI().setScreen(Constants.PRE_GAME_SCREEN);
				}
				else if(graphwar.getGameData().getGameState() == Constants.GAME)
				{
					graphwar.getUI().setScreen(Constants.GAME_SCREEN);
				}					
			}
		}
	}


	public void startPanel() 
	{
		this.showMessage = false;
	}


	public void stopPanel() 
	{
		
	}

}

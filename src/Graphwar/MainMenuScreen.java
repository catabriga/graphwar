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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import GraphServer.Constants;

public class MainMenuScreen extends JPanel implements ActionListener
{	
	private Graphwar graphwar;
	
	private JLabel[] backgroundImages;
	
	private GraphButton joinGlobal;
	private GraphButton createGame;
	private GraphButton joinGame;
	
	private String message;
	private int messageX;
	private boolean showMessage;	
	
	private JLabel[] backgroundsGlobal;
	private JTextField nameFieldGlobal;	
	private GraphButton yesButtonGlobal;
	private GraphButton noButtonGlobal;
	private boolean joinGlobalVisible;
	
	
	private JLabel[] backgroundsCreate;
	private JTextField nameFieldCreate;
	private JTextField portFieldCreate;
	private GraphButton yesButtonCreate;
	private GraphButton noButtonCreate;
	private boolean createVisible;
	
	
	private JLabel[] backgroundsJoin;
	private JTextField nameFieldJoin;
	private JTextField portFieldJoin;
	private JTextField ipFieldJoin;
	private GraphButton yesButtonJoin;
	private GraphButton noButtonJoin;
	private boolean joinVisible;
	
	private static Font font = new Font("Sans", Font.BOLD, 12);
	
	public MainMenuScreen(Graphwar graphwar, String confFile) throws InterruptedException, IOException
	{
		super();
		
		this.graphwar = graphwar;
		
		this.setLayout(null);		
		this.setBounds(0,0,Constants.WIDTH,Constants.HEIGHT);
		
		InputStreamReader in = new InputStreamReader(graphwar.getClass().getResourceAsStream(confFile));
		BufferedReader read = new BufferedReader(in);
					
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
					
			this.joinGlobal = GraphUtil.makeButton(graphwar, read);
			this.createGame = GraphUtil.makeButton(graphwar, read);
			this.joinGame = GraphUtil.makeButton(graphwar, read);
			
			components.push(joinGlobal);
			components.push(createGame);
			components.push(joinGame);
				
			this.joinGlobal.addActionListener(this);
			this.createGame.addActionListener(this);
			this.joinGame.addActionListener(this);
			
			///////////// 	Join Global Sub-menu	///////
		
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsGlobal = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsGlobal[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsGlobal[i]);
			}							
			
			this.nameFieldGlobal = GraphUtil.makeTextField(read);
			this.yesButtonGlobal = GraphUtil.makeButton(graphwar, read);
			this.noButtonGlobal = GraphUtil.makeButton(graphwar, read);
			
			components.push(nameFieldGlobal);
			components.push(yesButtonGlobal);
			components.push(noButtonGlobal);
			
			showJoinGlobal(false);
			
			this.nameFieldGlobal.addActionListener(this);
			this.yesButtonGlobal.addActionListener(this);
			this.noButtonGlobal.addActionListener(this);
			
			///////// Create game sub-menu ///////////////
			
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
			
			////// Join game sub-menu	/////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsJoin = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsJoin[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsJoin[i]);
			}
			
			this.nameFieldJoin = GraphUtil.makeTextField(read);
			this.portFieldJoin = GraphUtil.makeTextField(read);
			this.ipFieldJoin = GraphUtil.makeTextField(read);
			
			components.push(nameFieldJoin);
			components.push(portFieldJoin);
			components.push(ipFieldJoin);
			
			this.yesButtonJoin = GraphUtil.makeButton(graphwar, read);
			this.noButtonJoin = GraphUtil.makeButton(graphwar, read);
			
			components.push(yesButtonJoin);
			components.push(noButtonJoin);
			
			showJoinGame(false);
			
			this.nameFieldJoin.addActionListener(this);
			this.portFieldJoin.addActionListener(this);
			this.ipFieldJoin.addActionListener(this);
			this.yesButtonJoin.addActionListener(this);
			this.noButtonJoin.addActionListener(this);
			
			/////////////////////
		
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
	
	private void showJoinGame(boolean s)
	{		
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				nameFieldJoin.setVisible(show);
				portFieldJoin.setVisible(show);
				ipFieldJoin.setVisible(show);
				yesButtonJoin.setVisible(show);
				noButtonJoin.setVisible(show);
				joinVisible = show;
				
				for(int i=0; i<backgroundsJoin.length; i++)
				{
					backgroundsJoin[i].setVisible(show);
				}
				
				portFieldJoin.setText(Constants.DEFAULT_PORT+"");
			}
		}
		);
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
				
				portFieldCreate.setText(Constants.DEFAULT_PORT+"");
			}
		}
		);
	}
	
	private void showJoinGlobal(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				nameFieldGlobal.setVisible(show);
				yesButtonGlobal.setVisible(show);
				noButtonGlobal.setVisible(show);
				joinGlobalVisible = show;
				
				for(int i=0; i<backgroundsGlobal.length; i++)
				{
					backgroundsGlobal[i].setVisible(show);
				}
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

	public void actionPerformed(ActionEvent arg0) 
	{
		if(this.joinGlobalVisible)
		{
			if(arg0.getSource()==this.noButtonGlobal)
			{
				this.showJoinGlobal(false);
				this.showMessage = false;
				this.repaint();
			}
			else if(arg0.getSource()==this.yesButtonGlobal || arg0.getSource()==nameFieldGlobal)
			{
				String name = nameFieldGlobal.getText();
					
				if(name!=null && name.length()>0)
				{
					if(name.length() > 20)
					{
						nameFieldGlobal.setText(name.substring(0,20));
					}
					else
					{
						this.showMessage("Connecting...");
						
						try 
						{
							graphwar.joinGlobal(name);
							this.showJoinGlobal(false);						
							this.showMessage = false;
						}
						catch (IOException e) 
						{
							this.showMessage("Could not connect.");
							
							e.printStackTrace();
						}
					}
				}
			}
		}
		else if(this.createVisible)
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
								graphwar.getGameData().addPlayer(name);		
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
		else if(this.joinVisible)
		{
			if(arg0.getSource()==this.noButtonJoin)
			{
				this.showJoinGame(false);
				this.showMessage = false;
				this.repaint();
			}
			else if(arg0.getSource()==yesButtonJoin || arg0.getSource()==nameFieldJoin || arg0.getSource()==ipFieldJoin || arg0.getSource()==portFieldJoin)
			{
				try
				{
					String name = nameFieldJoin.getText();
					String ip = ipFieldJoin.getText();
					int port = Integer.parseInt(portFieldJoin.getText());
					
					if(name!=null && name.length()>0 && ip!=null && ip.length()>0)
					{
						if(name.length() > 20)
						{
							nameFieldJoin.setText(name.substring(0,20));
						}
						else
						{
							this.showMessage("Connecting...");
							
							try
							{
								graphwar.joinGame(ip, port);
								graphwar.getGameData().addPlayer(name);
								this.showJoinGame(false);
								this.showMessage = false;
								graphwar.getUI().setScreen(Constants.PRE_GAME_SCREEN);		
							}
							catch (IOException e) 
							{
								this.showMessage("Could not connect.");
								graphwar.getGameData().disconnect();
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
		else
		{
			if(arg0.getSource()==joinGlobal)
			{
				this.showJoinGlobal(true);
				this.repaint();
			}
			else if(arg0.getSource()==createGame)
			{
				this.showCreateGame(true);
				this.repaint();
			}
			else if(arg0.getSource()==joinGame)
			{
				this.showJoinGame(true);
				this.repaint();
			}
		}			
	}
}

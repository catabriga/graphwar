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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import GraphServer.Constants;

public class GameScreen extends JPanel implements ActionListener, StartStopPanel, KeyListener, MouseListener
{
	private Graphwar graphwar;
	
	private JLabel[] backgroundImages;
	
	private JLabel yImg;
	private JLabel dyImg;
	private JLabel ddyImg;
	
	private GraphButton fire;
	private GraphButton quit;
	private GraphButton global;	
	private JTextField funcField;	
	private JTextField chatField;	
	private GraphTextBox chatBox;	
	private GraphPlane plane;
	private GraphTimer timer;
	private GraphAngleDisplay angleDisplay;
	
	private JLabel[] backgroundsQuit;
	private GraphButton yesQuit;
	private GraphButton noQuit;
	private boolean quitVisible;
	
	private JLabel[] backgroundsShowMessage;
	private GraphButton okButton;
	private JLabel messageLabel;
	private boolean showMessageVisible;
	
	public GameScreen(Graphwar graphwar, String confFile) throws Exception
	{		
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
			
			yImg = GraphUtil.makeBackgroundImage(graphwar, read);
			dyImg = GraphUtil.makeBackgroundImage(graphwar, read);
			ddyImg = GraphUtil.makeBackgroundImage(graphwar, read);
			fire = GraphUtil.makeButton(graphwar, read);
			quit = GraphUtil.makeButton(graphwar, read);
			global = GraphUtil.makeButton(graphwar, read);			
			funcField = GraphUtil.makeTextField(read);			
			chatField = GraphUtil.makeTextField(read);			
			chatBox = GraphUtil.makeTextBox(read);
			
			plane = new GraphPlane(graphwar);
			plane.setBounds(15, 15, Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT);
			
			timer = new GraphTimer(graphwar);
			timer.setBounds(221, 564, 57, 22);
			
			angleDisplay = new GraphAngleDisplay(graphwar);
			angleDisplay.setBounds(10, 475, 200, 113);
			
			components.push(yImg);
			components.push(dyImg);
			components.push(ddyImg);
			components.push(angleDisplay);
			components.push(fire);
			components.push(quit);
			components.push(global);
			components.push(plane);
			components.push(funcField);
			components.push(chatField);			
			components.push(chatBox);			
			components.push(timer);
			
			this.fire.addActionListener(this);
			this.quit.addActionListener(this);
			this.global.addActionListener(this);
			this.funcField.addActionListener(this);
			this.chatField.addActionListener(this);
			
			this.dyImg.setVisible(false);
			this.ddyImg.setVisible(false);
			this.global.setVisible(false);
		//////// Quit Confirmation ///////////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsQuit = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsQuit[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsQuit[i]);
			}							
			
			this.yesQuit = GraphUtil.makeButton(graphwar, read);
			this.noQuit = GraphUtil.makeButton(graphwar, read);
			
			components.push(yesQuit);
			components.push(noQuit);
			
			showQuit(false);
			
			this.yesQuit.addActionListener(this);
			this.noQuit.addActionListener(this);
			
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
			
		/////////////////////////////////////////////////////////
		
				
		this.funcField.addKeyListener(this);
		this.chatField.addKeyListener(this);
		
		this.addMouseListener(this);
		this.plane.addMouseListener(this);
		this.angleDisplay.addMouseListener(this);
		
		this.setFocusable(true);
		this.addKeyListener(this);
		
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
	
	public void setNextMarker(boolean b)
	{
		plane.setNextMarker(b);
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
	
	private void showQuit(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				yesQuit.setVisible(show);
				noQuit.setVisible(show);
				quitVisible = show;
				
				for(int i=0; i<backgroundsQuit.length; i++)
				{
					backgroundsQuit[i].setVisible(show);
				}
			}
		}
		);
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
	
	public void paint(Graphics g)
	{
		plane.refreshBackground();
		
		super.paint(g);
	}
	
	public void paintComponent(Graphics g)
	{			
		g.setColor(Constants.BACKGROUND);
		g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);	
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
	}
	
	public void showMessage(String message)
	{
		this.messageLabel.setText(message);
		
		showShowMessage(true);
	}
	
	public void refreshBack()
	{
		plane.refreshBackground();
	}
	
	public void refreshSoldiers()
	{
		plane.refreshSoldiers();
	}

	public void refreshFunction()
	{
		Player player = graphwar.getGameData().getCurrentTurnPlayer();
		
		if(player.isLocalPlayer() && !(player instanceof ComputerPlayer))
		{
			final String function = player.getCurrentTurnSoldier().getFunction();
			
			SwingUtilities.invokeLater(
			new Runnable() 
			{
				public void run()
				{
					funcField.setEnabled(true);
					funcField.setText(function);
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
					funcField.setEnabled(false);
				}
			}
			);
		}
	}
	
	public boolean isShowMessageVisible()
	{
		return showMessageVisible;
	}
	
	public boolean isQuitVisible()
	{
		return quitVisible;
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(this.quitVisible)
		{
			if(arg0.getSource()==this.noQuit)
			{
				this.showQuit(false);
				plane.refreshBackground();
				this.repaint();
			}
			else if(arg0.getSource()==this.yesQuit)
			{
				graphwar.getGameData().disconnect();
				graphwar.getGlobalClient().closeRoom();
				this.showQuit(false);
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
			if(arg0.getSource()==quit)
			{
				this.showQuit(true);
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
			else if(arg0.getSource()==fire || arg0.getSource()==funcField)
			{
				if((graphwar.getGameData().getCurrentTurnPlayer() instanceof ComputerPlayer)==false)
				{				
					String function = funcField.getText();
					
					if(function.length() > 0)
					{
						graphwar.getGameData().sendFunction(function);
					}
				}
			}
			else if(arg0.getSource()==global)
			{
				graphwar.getUI().setScreen(Constants.GLOBAL_ROOM_SCREEN);
			}
		}
	}

	public void updateFunction(String function)
	{
		funcField.setText(function);
	}
	
	public void startDrawingFunction()
	{
		this.plane.startDrawingFunction();		
	}
	
	public void repaintAngle()
	{
		this.angleDisplay.repaint();
	}
	
	private void showFuncType()
	{
		switch(graphwar.getGameData().getGameMode())
		{
			case Constants.NORMAL_FUNC:
			{
				// I now officially hate swing				
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						dyImg.setVisible(false);
						ddyImg.setVisible(false);
							
						yImg.setVisible(true);
						yImg.repaint();
					}
				}
				);
				
			}break;
			
			case Constants.FST_ODE:
			{
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						yImg.setVisible(false);
						ddyImg.setVisible(false);
						
						dyImg.setVisible(true);
						dyImg.repaint();
					}
				}
				);						
				
			}break;
			
			case Constants.SND_ODE:
			{
				
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						yImg.setVisible(false);
						dyImg.setVisible(false);
						
						ddyImg.setVisible(true);
						ddyImg.repaint();
					}
				}
				);	
			}break;
		}
	}

	public void startPanel() 
	{
		showFuncType();
		
		this.plane.startAnimating();
		this.timer.startRunning();
	}

	public void stopPanel() 
	{
		this.plane.stopAnimating();
		this.timer.stopRunning();
	}
	
	public void keyPressed(KeyEvent e) 
	{		
		if(graphwar.getGameData().getGameMode() == Constants.SND_ODE)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				graphwar.getGameData().angleUp();
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				graphwar.getGameData().angleDown();
			}
		}

		String function = funcField.getText();
		graphwar.getGameData().sendFunctionPreview(function);
	}

	public void keyReleased(KeyEvent e) 
	{		
		if(graphwar.getGameData().getGameMode() == Constants.SND_ODE)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				graphwar.getGameData().stopAngle();
			}
		}
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		this.requestFocus();
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

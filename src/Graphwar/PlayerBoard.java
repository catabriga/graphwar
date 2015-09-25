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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ListIterator;

import javax.swing.JPanel;

import GraphServer.Constants;

public class PlayerBoard extends JPanel 
{	
	private Graphwar graphwar;
	
	private Image switchNormal;
	private Image switchOver;
	private Image switchGray;
	private BufferedImage switchMask;
	private Image switchNormalMirror;
	private Image switchOverMirror;
	private Image switchGrayMirror;
	private BufferedImage switchMaskMirror;
	
	private Image soldierNormal;
	private Image soldierOver;
	private Image soldierGhost;
	private BufferedImage soldierMask;
	private Image soldierNormalMirror;
	private Image soldierOverMirror;
	private Image soldierGhostMirror;
	private BufferedImage soldierMaskMirror;
	
	private Image removeNormal;
	private Image removeOver;
	private Image removeGray;
	private BufferedImage removeMask;
	private Image removeNormalMirror;
	private Image removeOverMirror;
	private Image removeGrayMirror;
	private BufferedImage removeMaskMirror;
	
	private int entryWidth;
	private int entryHeight;
	
	private int team2Offset;
	
	private PlayerEntry[] playersEntries;
	private int numPlayers;
	
	private class PlayerEntry extends JPanel implements ActionListener, MouseListener
	{
		private Graphwar graphwar;
		
		public Player player;
		
		public GraphButton switchSide;
		public GraphButton[] addSoldier;
		public GraphButton removePlayer;
		
		private int maxNameLength;
		
		public PlayerEntry(	Graphwar graphwar, int width, int height, Player player,
							Image switchNormal, Image switchOver, BufferedImage switchMask,
							Image soldierNormal, Image soldierOver, BufferedImage soldierMask,
							Image removeNormal, Image removeOver, BufferedImage removeMask)
		{
			this.graphwar = graphwar;
			
			this.setLayout(null);
			
			this.player = player;
			
			
			switchSide = new GraphButton(switchNormal, switchOver, switchMask);
			this.add(switchSide);
			
			addSoldier = new GraphButton[Constants.MAX_SOLDIERS_PER_PLAYER];
			
			for(int i=0; i<addSoldier.length; i++)
			{
				addSoldier[i] = new GraphButton(soldierNormal, soldierOver, soldierMask);
				this.add(addSoldier[i]);
			}
			
			removePlayer = new GraphButton(removeNormal, removeOver, removeMask);
			this.add(removePlayer);
			
			int x = width - removeNormal.getWidth(null);			
			this.removePlayer.setBounds(x, 0, removeNormal.getWidth(null), removeNormal.getHeight(null));

			for(int i=0; i<addSoldier.length; i++)
			{
				x = x - soldierNormal.getWidth(null);
				addSoldier[i].setBounds(x, 0, soldierNormal.getWidth(null), soldierNormal.getHeight(null));				
			}
			
			x = x - switchNormal.getWidth(null);
			switchSide.setBounds(x, 0, switchNormal.getWidth(null), switchNormal.getHeight(null));
			
			maxNameLength = x - 5;
			
			switchSide.addActionListener(this);
			for(int i=0; i<addSoldier.length; i++)
			{
				addSoldier[i].addActionListener(this);
				addSoldier[i].addMouseListener(this);
			}
			removePlayer.addActionListener(this);
		}
		
		public void paintComponent(Graphics g)
		{			
			if(this.player.getReady() == false)
			{
				g.setColor(Color.WHITE);
			}
			else
			{
				g.setColor(Color.GREEN);	
			}
			g.fillRect(0, 0, this.getWidth()-1, this.getHeight()-1);
			
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
			
			BufferedImage nameChopper = new BufferedImage(maxNameLength, this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D c = nameChopper.createGraphics();
			c.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);			
			c.setFont(new Font("Sans", Font.PLAIN, 14));
			c.setColor(Color.BLACK);
			c.drawString(player.getName(), 1, this.getHeight()-2);
			g.drawImage(nameChopper, 0, 0, null);
		}

		private boolean isAddSoldier(Object obj)
		{
			for(int i=0; i<addSoldier.length; i++)
			{
				if(addSoldier[i] == obj)
				{
					return true;
				}
			}
			
			return false;
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			if(arg0.getSource() == switchSide)
			{
				graphwar.getGameData().switchSide(player);
			}
			else if(arg0.getSource() == removePlayer)
			{
				graphwar.getGameData().removePlayer(player);
			}
			else if(isAddSoldier(arg0.getSource()))
			{
				graphwar.getGameData().addSoldier(player);
			}
		}

		public void mouseClicked(MouseEvent arg0)
		{
			if(arg0.getButton()==MouseEvent.BUTTON3)
			{
				if(isAddSoldier(arg0.getSource()))
				{
					graphwar.getGameData().removeSoldier(player);
				}
			}
		}

		public void mouseEntered(MouseEvent arg0)
		{
			
		}

		public void mouseExited(MouseEvent arg0) 
		{
			
		}

		public void mousePressed(MouseEvent arg0)
		{
			
		}

		public void mouseReleased(MouseEvent arg0)
		{
			
		}
	}	
	
	public PlayerBoard(	Graphwar graphwar, int entryWidth, int entryHeight, int team2Offset,
						Image switchNormal, Image switchOver, Image switchGray, BufferedImage switchMask,
						Image soldierNormal, Image soldierOver, Image soldierGhost, BufferedImage soldierMask,
						Image removeNormal, Image removeOver, Image removeGray,BufferedImage removeMask)
	{
		
		this.setLayout(null);
		
		this.graphwar = graphwar;
				
		playersEntries = new PlayerEntry[Constants.MAX_PLAYERS];
		numPlayers = 0;
			
		this.switchNormal = switchNormal;
		this.switchOver	= switchOver;
		this.switchGray = switchGray;
		this.switchMask = switchMask;
		this.soldierNormal = soldierNormal;
		this.soldierOver = soldierOver;
		this.soldierGhost = soldierGhost;
		this.soldierMask = soldierMask;
		this.removeNormal = removeNormal;
		this.removeOver = removeOver;
		this.removeGray = removeGray;
		this.removeMask = removeMask;
		
		this.switchNormalMirror = GraphUtil.mirrorImage(switchNormal);
		this.switchOverMirror = GraphUtil.mirrorImage(switchOver);
		this.switchGrayMirror = GraphUtil.mirrorImage(switchGray);
		this.switchMaskMirror = GraphUtil.mirrorImage(switchMask);
		this.soldierNormalMirror = GraphUtil.mirrorImage(soldierNormal);
		this.soldierOverMirror = GraphUtil.mirrorImage(soldierOver);
		this.soldierGhostMirror = GraphUtil.mirrorImage(soldierGhost);
		this.soldierMaskMirror = GraphUtil.mirrorImage(soldierMask);
		this.removeNormalMirror = GraphUtil.mirrorImage(removeNormal);
		this.removeOverMirror = GraphUtil.mirrorImage(removeOver);
		this.removeGrayMirror = GraphUtil.mirrorImage(removeGray);
		this.removeMaskMirror = GraphUtil.mirrorImage(removeMask);
			
		this.entryWidth = entryWidth;
		this.entryHeight = entryHeight;
		this.team2Offset = team2Offset;
		
		this.setOpaque(false);
	}
	
	private void updatePlayerButtons(Player player)
	{
		for(int i=0; i<numPlayers; i++)
		{
			if(playersEntries[i].player == player)
			{
				if(player.getTeam() == Constants.TEAM1)
				{
					if(player.isLocalPlayer() || graphwar.getGameData().isLeader())
					{
						playersEntries[i].switchSide.updateImages(switchNormal, switchOver, switchMask);
						playersEntries[i].removePlayer.updateImages(removeNormal, removeOver, removeMask);
					}
					else
					{
						playersEntries[i].switchSide.updateImages(switchNormal, switchGray, switchMask);
						playersEntries[i].removePlayer.updateImages(removeNormal, removeGray, removeMask);
					}
					
					for(int j=0; j<playersEntries[i].addSoldier.length; j++)
					{
						if(j<playersEntries[i].player.getNumSoldiers())
						{
							playersEntries[i].addSoldier[j].updateImages(soldierNormal, soldierOver, soldierMask);
						}
						else
						{
							playersEntries[i].addSoldier[j].updateImages(soldierGhost, soldierOver, soldierMask);
						}
					}
				}
				else
				{
					if(player.isLocalPlayer() || graphwar.getGameData().isLeader())
					{
						playersEntries[i].switchSide.updateImages(switchNormalMirror, switchOverMirror, switchMaskMirror);
						playersEntries[i].removePlayer.updateImages(removeNormalMirror, removeOverMirror, removeMaskMirror);
					}
					else
					{
						playersEntries[i].switchSide.updateImages(switchNormalMirror, switchGrayMirror, switchMaskMirror);
						playersEntries[i].removePlayer.updateImages(removeNormalMirror, removeGrayMirror, removeMaskMirror);
					}					
					
					for(int j=0; j<playersEntries[i].addSoldier.length; j++)
					{
						if(j<playersEntries[i].player.getNumSoldiers())
						{
							playersEntries[i].addSoldier[j].updateImages(soldierNormalMirror, soldierOverMirror, soldierMaskMirror);
						}
						else
						{
							playersEntries[i].addSoldier[j].updateImages(soldierGhostMirror, soldierOverMirror, soldierMaskMirror);
						}
					}
				}
				
				return;
			}
		}
	}
	
	public void updateBoard()
	{
		this.removeAll();
		this.numPlayers = 0;
		
		ListIterator<Player> itr = graphwar.getGameData().getPlayers().listIterator();
		
		while(itr.hasNext())
		{
			Player player = itr.next();
			
			addPlayer(player);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	public void updatePlayer(Player player)
	{		
		updatePlayerButtons(player);
		rearrangePlayers();		// This will update the team
	}
	
	public void addPlayer(Player player)
	{
		playersEntries[numPlayers] = new PlayerEntry(	graphwar, entryWidth, entryHeight, player,
														switchNormal, switchOver, switchMask,
														soldierNormal, soldierOver, soldierMask,
														removeNormal, removeOver, removeMask);
		
		this.add(playersEntries[numPlayers]);
		this.revalidate();
		
		numPlayers++;		
		
		updatePlayerButtons(player);
		rearrangePlayers();
	}
	
	public void removePlayer(Player player)
	{
		int remove = -1;
		
		for(int i=0; i<numPlayers; i++)
		{
			if(playersEntries[i].player == player)
			{
				remove = i;
				break;
			}
		}
		
		if(remove != -1)
		{
			this.remove(playersEntries[remove]);
			
			for(int i=remove; i<numPlayers-1; i++)
			{
				playersEntries[i] = playersEntries[i+1];
			}
			
			numPlayers--;
			
			rearrangePlayers();
		}
	}
	
	private void rearrangePlayers()
	{
		int heightTeam1 = 0;
		int heightTeam2 = 0;
		
		for(int i=0; i<numPlayers; i++)
		{
			int x = 0;
			int y = heightTeam1;
			
			if(playersEntries[i].player.getTeam() == Constants.TEAM2)
			{
				x = this.team2Offset;
				y = heightTeam2;
				heightTeam2 += entryHeight;
			}
			else
			{
				heightTeam1 += entryHeight;
			}
			
			playersEntries[i].setBounds(x, y, entryWidth, entryHeight);
		}
		
		this.repaint();
	}
	
	public void restartPlayers()
	{
		/*for(int i=0; i<numPlayers; i++)
		{
			this.remove(playersEntries[i]);			
		}*/
		
		this.removeAll();
		this.revalidate();
		
		this.numPlayers = 0;
	}
	
}

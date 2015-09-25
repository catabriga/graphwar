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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import GraphServer.Constants;

public class GraphPlane extends JPanel implements ActionListener
{
	private Graphwar graphwar;
	
	private Image playerDefault;
	private Image helmetDefault;
	private Image helmetMaskDefault;
	
	private Image[][] playerAnimations;
	private int[][] playerDurations;
	
	private Image defaultImages[];
	private Image animationImages[][][];
	
	private Image[] deathImages;
	private int[] deathDurations;
	private int deathFadeDuration;
	
	private Image[] explosionImages;
	private int[] explosionDurations;
	
	private Image[] currentImages;
	private int[] currentDurations;
	private int totalCurrentMarkerDuration;
	
	private Image nextTeamImage;
	
	private BufferedImage functionImage;
	private Graphics2D functionGraphics;
	private int lastStepDrawn;
	
	private BufferedImage background;
	private Graphics2D backg;
	
	private boolean animating;
	
	private boolean repaintBack;
	
	private boolean nextMarker;
	
	private Timer timer;
	
	private static Color transparentWhite = new Color(255,255,255,170);
	
	public GraphPlane(Graphwar graphwar) throws Exception
	{
		this.graphwar = graphwar;
		
		//this.setOpaque(false);
		
		MediaTracker tracker = new MediaTracker(graphwar);
		
		initializeExplosions(graphwar, tracker);
		initializeDeaths(graphwar, tracker);
		initializePlayersAnimations(graphwar, tracker);
		initializeCurrentMarker(graphwar, tracker);
		initializeNextTeamMarker(graphwar, tracker);
		
		tracker.waitForAll();
		
		animating = false;
		
		lastStepDrawn = 0;
		repaintBack = true;
		nextMarker = false;
		
		background = new BufferedImage(Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		backg = background.createGraphics();
		backg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		timer = new Timer(30, this);
		timer.setInitialDelay(30); 
	}
	
	private void initializeNextTeamMarker(Graphwar graphwar, MediaTracker mediaTracker) throws Exception
	{		
		nextTeamImage = ImageIO.read(graphwar.getClass().getResource("/rsc/soldiers/nextTeam.png")); 
				
		mediaTracker.addImage(nextTeamImage, 0);		
	}
	
	private void initializeCurrentMarker(Graphwar graphwar, MediaTracker mediaTracker) throws Exception
	{		
		String filePath = "/rsc/currentPlayerMarker.txt";		
		
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(filePath)));
					
		int numImages = Integer.parseInt(GraphUtil.nextLine(read).trim());
		currentImages = new Image[numImages];
		currentDurations = new int[numImages];
		totalCurrentMarkerDuration = 0;
		
		for(int j=0; j<numImages;j++)
		{	
			currentImages[j] = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
			currentDurations[j] = Integer.parseInt(GraphUtil.nextLine(read).trim());
			totalCurrentMarkerDuration += currentDurations[j];
			
			mediaTracker.addImage(currentImages[j], 0);
		}
		
	}
	
	private void initializeDeaths(Graphwar graphwar, MediaTracker mediaTracker) throws Exception
	{		
		String filePath = "/rsc/soldierDeath.txt";
				
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(filePath)));
					
		int numImages = Integer.parseInt(GraphUtil.nextLine(read).trim());
		deathImages = new Image[numImages];
		deathDurations = new int[numImages];
		
		for(int j=0; j<numImages;j++)
		{	
			deathImages[j] = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
			deathDurations[j] = Integer.parseInt(GraphUtil.nextLine(read).trim());
			mediaTracker.addImage(deathImages[j], 0);
		}
		
		deathFadeDuration = Integer.parseInt(GraphUtil.nextLine(read).trim());
	}
	
	private void initializePlayersAnimations(Graphwar graphwar, MediaTracker mediaTracker) throws Exception
	{
		String filePath = "/rsc/soldier.txt";
		
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(filePath)));
		
		playerDefault = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		mediaTracker.addImage(playerDefault, 0);
					
		helmetDefault = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		mediaTracker.addImage(helmetDefault, 0);
		
		helmetMaskDefault = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		mediaTracker.addImage(helmetMaskDefault, 0);		
		
		
		int numAnimations = Integer.parseInt(GraphUtil.nextLine(read).trim());
		playerAnimations = new Image[numAnimations][];
		playerDurations = new int[numAnimations][];
		
		for(int i=0; i<numAnimations;i++)
		{	
			int numImages = Integer.parseInt(GraphUtil.nextLine(read).trim());
			playerAnimations[i] = new Image[numImages];
			playerDurations[i] = new int[numImages];
			
			for(int j=0; j<numImages;j++)
			{	
				playerAnimations[i][j] = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
				playerDurations[i][j] = Integer.parseInt(GraphUtil.nextLine(read).trim());
				mediaTracker.addImage(playerAnimations[i][j], 0);
			}
		}	
	}
	
	private void initializeExplosions(Graphwar graphwar, MediaTracker mediaTracker) throws Exception
	{		
		String filePath = "/rsc/explosion.txt";
				
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(filePath)));
					
		int numImages = Integer.parseInt(GraphUtil.nextLine(read).trim());
		explosionImages = new Image[numImages];
		explosionDurations = new int[numImages];
		
		for(int j=0; j<numImages;j++)
		{	
			explosionImages[j] = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
			explosionDurations[j] = Integer.parseInt(GraphUtil.nextLine(read).trim());
			mediaTracker.addImage(explosionImages[j], 0);
		}
		
	}
	
	public void setNextMarker(boolean b)
	{
		nextMarker = b;
		
		repaintBack = true;
	}
	
	public void paintComponent(Graphics g)
	{		
		//long times[] = new long[6];
		
		//times[0] = System.nanoTime();
				
		boolean reversed = graphwar.getGameData().isTerrainReversed();
		
		if(repaintBack)
		{
			drawBackground(backg, reversed);	//times[1] = System.nanoTime();
			drawPlayersNames(backg, reversed);	///times[2] = System.nanoTime();	
			drawCurrentPlayerMarker(backg, reversed);
			
			if(nextMarker)
			{
				drawNextPlayersMarkers(backg, reversed);
			}
			
			repaintBack = false;
		}
		
		g.drawImage(background, 0, 0, null);
	//	else
	//	{
	//		times[1] = System.nanoTime();
	//		times[2] = System.nanoTime();
	//	}
		
		drawSoldiers(g, reversed);	//	times[3] = System.nanoTime();
		drawFunction(g, reversed);	//	times[4] = System.nanoTime();
		drawExplosion(g, reversed);	//	times[5] = System.nanoTime();
				
	//	for(int i=1; i<times.length; i++)
	//	{
	//		System.out.println(i+": "+(times[i]-times[i-1]));
	//	}
	
	//	System.out.println();
	//	timeFinishedLastPaint = System.currentTimeMillis();
	}
		
	private void drawBackground(Graphics2D g, boolean reversed)
	{
		if(reversed)
		{
			g.drawImage(graphwar.getGameData().getObstacle().getImage(), 0, 0, Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT, Constants.PLANE_LENGTH, 0, 0, Constants.PLANE_HEIGHT, null);
		}
		else
		{
			g.drawImage(graphwar.getGameData().getObstacle().getImage(), 0, 0, null);
		}
		
		g.setColor(Color.BLACK);
		
		g.drawLine(0, Constants.PLANE_HEIGHT/2, Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT/2);
		g.drawLine(Constants.PLANE_LENGTH/2, 0, Constants.PLANE_LENGTH/2, Constants.PLANE_HEIGHT);
	}
	
	public void startDrawingFunction()
	{
		functionImage = new BufferedImage(Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		functionGraphics = functionImage.createGraphics();
		functionGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		lastStepDrawn = 0;		
	}
	
	private double convertX(double x)
	{
		return Constants.PLANE_LENGTH*x/Constants.PLANE_GAME_LENGTH + Constants.PLANE_LENGTH/2;
	}
	
	private double convertY(double y)
	{
		return -Constants.PLANE_LENGTH*y/Constants.PLANE_GAME_LENGTH + Constants.PLANE_HEIGHT/2;
	}
	
	private void drawFunctionImage(Graphics g, boolean reversed)
	{		
		int numDrawSteps = graphwar.getGameData().getCurrentFunctionPosition();
					
		GeneralPath path = new GeneralPath();
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color playerColor = graphwar.getGameData().getCurrentTurnPlayer().getColor();
		g2d.setColor(playerColor);
		//g2d.setStroke(new BasicStroke(1.0f));
		
		Function function = graphwar.getGameData().getFunction();
		
		double x = convertX(function.getX(lastStepDrawn));
		double y = convertY(function.getY(lastStepDrawn));
		
		if(reversed)
		{
			x = Constants.PLANE_LENGTH-x;
		}
		
		path.moveTo(x,y);
		
		for(int i=lastStepDrawn; i<numDrawSteps; i++)
		{			
			x = convertX(function.getX(i));
			y = convertY(function.getY(i));
			
			if(reversed)
			{
				x = Constants.PLANE_LENGTH-x;
			}
			
			path.lineTo(x,y);
			//path.curve
		}	
		lastStepDrawn = numDrawSteps-1;
		
		if(lastStepDrawn < 0)
		{
			lastStepDrawn = 0;
		}
		
		g2d.draw(path);
				
	}
	
	private void drawFunction(Graphics g, boolean terrainReversed)
	{
		if(graphwar.getGameData().isDrawingFunction())
		{
			boolean funcReversed = graphwar.getGameData().isFunctionReversed();
			drawFunctionImage(functionGraphics, (funcReversed || terrainReversed) && !(funcReversed && terrainReversed));
			
			Graphics2D g2d = (Graphics2D)g;
			
			if(graphwar.getGameData().isExploding())
			{
				float alpha = 1.0f - ((float)graphwar.getGameData().getTimeExploding())/Constants.FUNC_FADE_TIME;
					
				if(alpha < 0)
				{
					alpha = 0;
				}
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			}
						
			g2d.drawImage(functionImage, 0, 0, null);
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
					
			repaintBack = true;
		}
	}
	
	
	private void drawExplosion(Graphics g, boolean terrainReversed)
	{
		if(graphwar.getGameData().isExploding())
		{			
			repaintBack = true;
			
			long timePassedExploding = graphwar.getGameData().getTimeExploding();
			
			Function function = graphwar.getGameData().getFunction();
			
			int width = explosionImages[0].getWidth(null);
			int height = explosionImages[0].getHeight(null);
			
			int drawX = (int) (function.getLastX());
			int drawY = (int) (function.getLastY() - height/2);
			
			boolean funcReversed = graphwar.getGameData().isFunctionReversed();
			if((funcReversed || terrainReversed) && !(funcReversed && terrainReversed))
			{
				drawX = Constants.PLANE_LENGTH-drawX;
			}
			
			drawX = drawX - width/2;
			
			/*
			Image back = graphwar.getGameData().getObstacle().getImage();
			
			int x = drawX;
			if((funcReversed || terrainReversed) && !(funcReversed && terrainReversed))
			{
				x = Constants.PLANE_LENGTH - x;				
			}
						
			g.drawImage(	back, 
							drawX, drawY,
							drawX+width, drawY+height,
							x, drawY,
							x+width, drawY+height,
							null);	
			*/
					
			for(int i=0; i<explosionDurations.length; i++)
			{
				if(timePassedExploding > explosionDurations[i])
				{
					timePassedExploding -= explosionDurations[i];
				}
				else
				{						
					g.drawImage(explosionImages[i], drawX, drawY, null);					
					
					return;
				}
			}
		}
	}
	
	private Image getSoldierImage(Soldier soldier, int player)
	{
		if(soldier.isExploding())
		{
			long timePassedAnimating = soldier.getTimeExploding();
			
			for(int i=0; i<deathImages.length; i++)
			{
				if(timePassedAnimating > deathDurations[i])
				{
					timePassedAnimating -= deathDurations[i];
				}
				else
				{					
					repaintBack = true;
					return deathImages[i];										
				}
			}
			
			if(timePassedAnimating < deathFadeDuration)
			{					
				BufferedImage fadeImage = new BufferedImage(deathImages[deathImages.length-1].getWidth(null), deathImages[deathImages.length-1].getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
				
				Graphics2D g2d = (Graphics2D)fadeImage.getGraphics();
				
				float alpha = 1.0f - ((float)(timePassedAnimating))/deathFadeDuration;
				
				if(alpha < 0)
				{
					alpha = 0;
				}
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				
				g2d.drawImage(deathImages[deathImages.length-1], 0, 0, null);
				
				repaintBack = true;
				
				return fadeImage;
			}
			
			return null;
		}		
		else if(soldier.isAnimating())
		{
			int animationNum = soldier.getAnimationNum()%playerAnimations.length;
			
			long timePassedAnimating = soldier.getAnimationTime();
			
			for(int i=0; i<playerAnimations[animationNum].length; i++)
			{
				if(timePassedAnimating > playerDurations[animationNum][i])
				{
					timePassedAnimating -= playerDurations[animationNum][i];
				}
				else
				{					
					return animationImages[player][animationNum][i];										
				}
			}
			
			soldier.endAnimation();
		}
		
		return defaultImages[player];		
	}
	
	private Image addHelmet(Image image, Color helmetColor)
	{
		BufferedImage soldierImage = new BufferedImage(20,20, BufferedImage.TYPE_4BYTE_ABGR);		
		Graphics2D g = soldierImage.createGraphics();
		
		g.drawImage(image, 0, 0, null);		
		
		BufferedImage helmImage = new BufferedImage(20,20, BufferedImage.TYPE_4BYTE_ABGR);		
		Graphics2D g2 = helmImage.createGraphics();
		
		g2.drawImage(helmetMaskDefault, 0, 0, null);		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
		g2.setColor(helmetColor);
		g2.fillRect(0, 0, 20, 20);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g2.drawImage(helmetDefault, 0, 0, null);
		
		g.drawImage(helmImage, 0, 0, null);
		
		return soldierImage;
	}
	
	private void drawNextPlayersMarkers(Graphics g, boolean reversed)
	{
		List<Player> players = graphwar.getGameData().getPlayers();
		
		Iterator<Player> itr = players.iterator();
		
		while(itr.hasNext())
		{
			Player player = itr.next();
			
			if(graphwar.getGameData().getCurrentTurnPlayer() != player)
			{
				Soldier soldier = player.getNextTurnSoldier();
				
				if(soldier != null)
				{
					int width = nextTeamImage.getWidth(null);
					int height = nextTeamImage.getHeight(null);
					
					int x = soldier.getX();
					int y = soldier.getY();
					
					if(reversed)
					{
						x = Constants.PLANE_LENGTH - x;
					}
					
					g.drawImage(nextTeamImage, x-width/2, y-height/2, null);	
				}
			}
		}
						
	}
	
	private void drawCurrentPlayerMarker(Graphics g, boolean reversed)
	{				
		Player currentTurnPlayer = graphwar.getGameData().getCurrentTurnPlayer();
		Soldier[] currentTurnSoldiers = currentTurnPlayer.getSoldiers();
		int soldierTurn = currentTurnPlayer.getCurrentTurnSoldierIndex();
			
		long timePassed = (System.currentTimeMillis()%totalCurrentMarkerDuration);
		
		int currentImage = 0;
		
		for(int i=0; i<currentDurations.length; i++)
		{
			if(timePassed > currentDurations[i])
			{
				timePassed -= currentDurations[i];
			}
			else
			{
				currentImage = i;
				break;
			}
		}
		
		int width = currentImages[currentImage].getWidth(null);
		int height = currentImages[currentImage].getHeight(null);
		
		int x = currentTurnSoldiers[soldierTurn].getX();
		int y = currentTurnSoldiers[soldierTurn].getY();
		
		if(reversed)
		{
			x = Constants.PLANE_LENGTH - x;
		}
		
		g.drawImage(currentImages[currentImage], x-width/2, y-height/2, null);		
	}
	
	private void drawSoldiers(Graphics g, boolean reversed)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
						
		List<Player> players = graphwar.getGameData().getPlayers();
		
		Iterator<Player> itr = players.iterator();
		
		int playerNum = 0;
		while(itr.hasNext())
		{
			Player player = itr.next();
						
			Soldier[] soldiers = player.getSoldiers();
			int numSoldiers = player.getNumSoldiers();
			for(int i=0; i<numSoldiers; i++)
			{
				if(soldiers[i].isAlive() || soldiers[i].isExploding())
				{
					int x = soldiers[i].getX();
					int y = soldiers[i].getY();
					
					if(reversed)
					{
						x = Constants.PLANE_LENGTH - x;
					}
					
					Image drawImage = getSoldierImage(soldiers[i], playerNum);
						
					if(drawImage != null)
					{					
						int width = drawImage.getWidth(null);
						int height = drawImage.getHeight(null);
						
						g.drawImage(	background, 
										x-width/2, y-height/2,
										x+width/2, y+height/2, 
										x-width/2, y-height/2,
										x+width/2, y+height/2, 	
										null);
						
						if((player.getTeam() == Constants.TEAM2 || reversed) && !(player.getTeam() == Constants.TEAM2 && reversed))
						{							
							g2d.drawImage(	drawImage, 	x-width/2, y-height/2,
											x+width/2, y+height/2, 
											width, 0, 0, height,
											null);								
						}
						else
						{
							g2d.drawImage(drawImage, x-width/2, y-height/2, null);
						}
					}
				}
			}
			
			playerNum++;
		}
	}
	
	private void paintPlayerName(Graphics g, int x, int y, Player player)
	{		
		int border = 3;
		int nameLength = player.getNameLength();
		
		int borderX = x-nameLength/2-border;
		int borderY = y-15-2*Constants.SOLDIER_RADIUS;
		int textX = x-nameLength/2;
		int textY = y-2-2*Constants.SOLDIER_RADIUS;
		
		if(borderY < 0)
		{
			borderY = y-2+2*Constants.SOLDIER_RADIUS;
			textY = y+11+2*Constants.SOLDIER_RADIUS;
		}
		
		if(borderX < 0)
		{
			textX = textX - borderX;
			borderX = 0;
		}
		
		if(borderX + nameLength+2*border > Constants.PLANE_LENGTH)
		{
			textX = (textX - borderX) + Constants.PLANE_LENGTH - nameLength-2*border;
			borderX = Constants.PLANE_LENGTH - nameLength-2*border;			
		}
		
		g.setColor(transparentWhite);
		g.fillRoundRect(borderX, borderY, nameLength+2*border, 15, 7, 7);
		
		g.setColor(player.getColor());
		g.drawRoundRect(borderX, borderY, nameLength+2*border, 15, 7, 7);
		
		g.setColor(Color.BLACK);
		g.drawString(player.getName(), textX, textY);
	}
	
	private void drawPlayersNames(Graphics g, boolean reversed)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setFont(Constants.NAME_FONT);
		
		List<Player> players = graphwar.getGameData().getPlayers();
		
		Iterator<Player> itr = players.iterator();
		
		while(itr.hasNext())
		{
			Player player = itr.next();
			Soldier[] soldiers = player.getSoldiers();
			
			g2d.setColor(player.getColor());
			
			for(int j=0; j<player.getNumSoldiers(); j++)
			{
				int drawX = soldiers[j].getX();
				int drawY = soldiers[j].getY();
				
				if(reversed)
				{
					drawX = Constants.PLANE_LENGTH-(soldiers[j].getX());										
				}
							
				if(soldiers[j].isAlive())
				{					
					paintPlayerName(g2d, drawX, drawY, player);
				}
				else if(soldiers[j].isExploding())
				{					
					float alpha = 1.0f - ((float)(soldiers[j].getTimeExploding()))/Constants.NAME_FADE_TIME;
						
					if(alpha < 0)
					{
						alpha = 0;							
					}
						
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				
					paintPlayerName(g2d, drawX, drawY, player);
					
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				}
			}		
		}
	}
	
	private void initializeImages()
	{
		List<Player> players = graphwar.getGameData().getPlayers();
		
		defaultImages = new Image[players.size()];
		animationImages = new Image[players.size()][playerAnimations.length][];
		
		for(int i=0; i<animationImages.length; i++)
		{
			for(int j=0; j<playerAnimations.length; j++)
			{
				animationImages[i][j] = new Image[playerAnimations[j].length];
			}
		}
		
		for(int i=0; i<defaultImages.length; i++)
		{
			defaultImages[i] = addHelmet(playerDefault, players.get(i).getColor());
		}
		
		for(int i=0; i<animationImages.length; i++)
		{
			for(int j=0; j<animationImages[i].length; j++)
			{
				for(int k=0; k<animationImages[i][j].length; k++)
				{
					animationImages[i][j][k] = addHelmet(playerAnimations[j][k], players.get(i).getColor());
				}
			}
		}
	}

	public void refreshBackground()
	{
		repaintBack = true;
	}
	
	public void refreshSoldiers()
	{
		initializeImages();
	}
	
	public void startAnimating()
	{
		if(this.animating==false)
		{
			initializeImages();
			
			this.animating = true;
			
			timer.start();
		}
	}
	
	public void stopAnimating()
	{
		this.animating = false;
		
		timer.stop();
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).isQuitVisible() ||
			((GameScreen)graphwar.getUI().getScreen(Constants.GAME_SCREEN)).isShowMessageVisible())
		{
			repaintBack = true;
			
			this.getParent().repaint();
		}
		else
		{					
			repaint();				
		}
	}
}

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

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import GraphServer.Constants;

public class GraphUtil
{
	public static Random random = new Random(System.currentTimeMillis());
	
	public static Color getRandomColor()
	{
		Color color;
		
		do
		{
			color = new Color( random.nextInt(256), random.nextInt(256), random.nextInt(256));
		}
		while(color.getRed()*color.getRed() + color.getGreen()*color.getGreen() + color.getBlue()*color.getBlue() > Constants.MAXIMUM_COLOR_MODULE_SQUARED);
	
		return color;
	}
	
	public static String nextLine(BufferedReader read)
	{
		String line = null;
		
		try 
		{
			line = read.readLine();
			
			while(line.trim().isEmpty() || line.startsWith("//"))
			{				
				line = read.readLine();				
			}
			
			line = line.trim();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;		
		
	}
	
	public static BufferedImage mirrorImage(Image image)
	{
		BufferedImage mirror = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g = mirror.createGraphics();
		
		g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), image.getWidth(null), 0, 0, image.getHeight(null), null);
		
		return mirror;
	}
	
	public static PlayerBoard makePlayerBoard(Graphwar graphwar, BufferedReader read) throws InterruptedException, IOException
	{
		int x = Integer.parseInt(GraphUtil.nextLine(read));
		int y = Integer.parseInt(GraphUtil.nextLine(read));
		int entryWidth = Integer.parseInt(GraphUtil.nextLine(read));
		int entryHeight = Integer.parseInt(GraphUtil.nextLine(read));
		int team2Offset = Integer.parseInt(GraphUtil.nextLine(read));
		
		MediaTracker tracker = new MediaTracker(graphwar);
		
		Image switchNormal = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(switchNormal, 0);
		Image switchOver = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(switchOver, 1);
		Image switchGray = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(switchGray, 1);
		Image tempImg = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(tempImg, 2);
		tracker.waitForAll();
		BufferedImage switchMask = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);			
		switchMask.getGraphics().drawImage(tempImg, 0, 0, null);
		
		Image soldierNormal = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(soldierNormal, 0);
		Image soldierOver = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(soldierOver, 1);
		Image soldierGhost = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(soldierGhost, 1);
		tempImg = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(tempImg, 2);
		tracker.waitForAll();
		BufferedImage soldierMask = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);			
		soldierMask.getGraphics().drawImage(tempImg, 0, 0, null);
		
		Image removeNormal = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(removeNormal, 0);
		Image removeOver = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(removeOver, 1);
		Image removeGray = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(removeGray, 2);
		tempImg = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(tempImg, 3);
		tracker.waitForAll();
		BufferedImage removeMask = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);			
		removeMask.getGraphics().drawImage(tempImg, 0, 0, null);
		
		PlayerBoard playerBoard = new PlayerBoard(	graphwar, entryWidth, entryHeight, team2Offset,
													switchNormal, switchOver, switchGray, switchMask,
													soldierNormal, soldierOver, soldierGhost, soldierMask,
													removeNormal, removeOver, removeGray, removeMask);
		
		playerBoard.setBounds(x, y, team2Offset+entryWidth, Constants.MAX_PLAYERS*entryHeight);
		
		return playerBoard;
	}
	
	public static GraphTextBox makeTextBox(BufferedReader read)
	{
		int x = Integer.parseInt(GraphUtil.nextLine(read));
		int y = Integer.parseInt(GraphUtil.nextLine(read));
		int width = Integer.parseInt(GraphUtil.nextLine(read));
		int height = Integer.parseInt(GraphUtil.nextLine(read));
		
		GraphTextBox textBox = new GraphTextBox();
		
		textBox.setBounds(x, y, width, height);
		
		return textBox;
	}
	
	public static JTextField makeTextField(BufferedReader read)
	{
		int fieldX = Integer.parseInt(GraphUtil.nextLine(read));
		int fieldY = Integer.parseInt(GraphUtil.nextLine(read));
		int fieldLength = Integer.parseInt(GraphUtil.nextLine(read));
		
		JTextField field = new JTextField(fieldLength);
		field.setBounds(fieldX, fieldY, fieldLength, Constants.FIELDS_HEIGHT);
		
		return field;
	}
	
	public static AudioClip makeAudioClip(Graphwar graphwar, BufferedReader read)
	{
		AudioClip ac = Applet.newAudioClip(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		
		return ac;
	}
	
	public static JLabel makeBackgroundImage(Graphwar graphwar, BufferedReader read) throws InterruptedException, IOException
	{
		MediaTracker tracker = new MediaTracker(graphwar);
		
		Image image = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));	
		tracker.addImage(image, 0);		
		tracker.waitForAll();		
		
		int x = Integer.parseInt(GraphUtil.nextLine(read));
		int y = Integer.parseInt(GraphUtil.nextLine(read));
		
		JLabel imagePanel = new JLabel(new ImageIcon(image));
		imagePanel.setBounds(x, y, image.getWidth(null), image.getHeight(null));
		
		return imagePanel;
		
	}
	
	public static JLabel makeTextLabel(BufferedReader read)
	{
		int x = Integer.parseInt(GraphUtil.nextLine(read));
		int y = Integer.parseInt(GraphUtil.nextLine(read));
		int length = Integer.parseInt(GraphUtil.nextLine(read));
		int height = Integer.parseInt(GraphUtil.nextLine(read));
		
		JLabel label = new JLabel();
		label.setBounds(x, y, length, height);
		
		return label;
	}
	
	public static GraphButton makeButton(Graphwar graphwar, BufferedReader read) throws InterruptedException, IOException
	{
		MediaTracker tracker = new MediaTracker(graphwar);
		
		Image normal = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(normal, 0);
		Image over = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(over, 1);
		Image tempImg = ImageIO.read(graphwar.getClass().getResource(GraphUtil.nextLine(read)));
		tracker.addImage(tempImg, 2);
		tracker.waitForAll();
		
		
		BufferedImage mask = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);			
		mask.getGraphics().drawImage(tempImg, 0, 0, null);
		
		int x = Integer.parseInt(GraphUtil.nextLine(read));
		int y = Integer.parseInt(GraphUtil.nextLine(read));
				
		GraphButton graphButton = new GraphButton(normal, over, mask);
		graphButton.setBounds(x, y, normal.getWidth(null), normal.getWidth(null));
		
		return graphButton;
	}

}

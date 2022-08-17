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
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GraphAngleDisplay extends JPanel 
{
	private Graphwar graphwar;
	
	private static Font font = new Font("Sans", Font.PLAIN, 14);
	
	public GraphAngleDisplay(Graphwar graphwar)
	{
		this.graphwar = graphwar;
		
		this.setOpaque(false);
	}
	
	public void paintComponent(Graphics g)
	{	
		Player currentPlayer = graphwar.getGameData().getCurrentTurnPlayer();	
		double angle;
		
		if(graphwar.getGameData().isAngleDown() || graphwar.getGameData().isAngleUp())
		{
			angle = graphwar.getGameData().getAngle();
			
			this.repaint();
		}
		else
		{
				
			angle = currentPlayer.getCurrentTurnSoldier().getAngle();
		}
				
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(currentPlayer.getColor());		
		paintCircle(g2d, angle);
		paintAngleText(g2d, angle);
	}
	
	private void paintCircle(Graphics g, double angle)
	{
		boolean reverse = (graphwar.getGameData().isFunctionReversed() || graphwar.getGameData().isTerrainReversed()) && !(graphwar.getGameData().isFunctionReversed() && graphwar.getGameData().isTerrainReversed());
		
		if(reverse)
		{
			g.fillArc(27, 27, 60, 60, 180, -(int)Math.toDegrees(angle));
		}
		else
		{
			g.fillArc(27, 27, 60, 60, 0, (int)Math.toDegrees(angle));
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(4, 57, 111, 57);
		g.drawLine(57, 5, 57, 111);
		
		if(reverse)
		{
			g.drawLine(57, 57, 57-(int)(53*Math.cos(angle)), 57-(int)(53*Math.sin(angle)));
		}
		else
		{
			g.drawLine(57, 57, 57+(int)(53*Math.cos(angle)), 57-(int)(53*Math.sin(angle)));
		}
	}
	
	private void paintAngleText(Graphics g, double angle)
	{		
		//g.setColor(Color.WHITE);
		//g.fillRect(141, 89, 57, 22);
		
		g.setFont(font);
		g.setColor(Color.BLACK);
		
		g.setFont(font);	
		
		double angleDegree = Math.toDegrees(angle);		
		angleDegree = angleDegree*100;		
		int tempAngle = (int)(angleDegree+0.5);		
		angleDegree = ((double)(tempAngle))/100;
		
		g.drawString(Double.toString(angleDegree)+"\u00B0", 145, 108);
	}
}

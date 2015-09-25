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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GraphTimer extends JPanel implements ActionListener
{
	private boolean running;
	
	private Graphwar graphwar;
	
	private Timer timer;

	private static Font font = new Font("Sans", Font.BOLD, 18);
		
	public GraphTimer(Graphwar graphwar)
	{
		this.running = false;
		
		this.graphwar = graphwar;	
		
		timer = new Timer(50, this);
		timer.setInitialDelay(50); 
	}
	
	public void paintComponent(Graphics g)
	{			
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setFont(font);	
		g2d.setColor(Color.BLACK);
		
		long timeLeft = graphwar.getGameData().getRemainingTime();
		timeLeft = timeLeft/100;
		double timeLeftDecimal = (double)timeLeft;
		timeLeftDecimal = timeLeftDecimal/10;
		
		if(timeLeftDecimal<0)
			timeLeftDecimal = 0;
		
		if(timeLeft < 50)
		{
			g2d.setColor(Color.RED);
		}
	
		g2d.drawString(Double.toString(timeLeftDecimal), 4, 19);
	}
	
	public void startRunning()
	{
		if(this.running == false)
		{
			this.running = true;
			
			timer.start();
		}
	}
	
	public void stopRunning()
	{
		this.running = false;
		
		timer.stop();
	}
	public void actionPerformed(ActionEvent e)
	{
		repaint();
	}

}

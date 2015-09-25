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


import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

public class GraphButton extends JButton implements MouseListener, MouseMotionListener
{
	private Image normalImg;
	private Image overImg;
	private BufferedImage maskImg;
	
	private boolean mouseOver;
	
	public GraphButton(Image normalImg, Image overImg, BufferedImage maskImg)
	{
		super();
		
		this.normalImg = normalImg;
		this.overImg = overImg;
		this.maskImg = maskImg;
		
		this.setOpaque(false);
		
		this.mouseOver = false;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void updateImages(Image normalImg, Image overImg, BufferedImage maskImg)
	{
		this.normalImg = normalImg;
		this.overImg = overImg;
		this.maskImg = maskImg;
		
		this.revalidate();
		this.repaint();
	}
	
	public void setVisible(boolean visible)
	{
		if(visible == false)
		{
			this.mouseOver = false;
		}
		
		super.setVisible(visible);
	}
	
	public void paintComponent(Graphics g)
	{		
		if(this.mouseOver)
		{
			g.drawImage(overImg, 0, 0, null);
		}
		else
		{
			g.drawImage(normalImg, 0, 0, null);
		}		
	}
	
	public void paintBorder(Graphics g)
	{
		
	}
	
	public boolean contains(int x, int y)
	{
		//System.out.println(x+" "+y);
		
		if( (0 <= x) && (maskImg.getWidth() > x) &&
			(0 <= y) && (maskImg.getHeight() > y))
		{
			if( maskImg.getRGB(x, y) != -1)
			{
				return true;				
			}			
		}
		
		// Hack to keep buttons from appearead hovered when changing screens
		if(this.mouseOver==true)
		{	
			this.mouseOver = false;
			this.repaint();
		}
		
		return false;		
	}
	
	
	public void mouseDragged(MouseEvent arg0)
	{
		
	}

	public void mouseMoved(MouseEvent arg0) 
	{		
		if(contains(arg0.getX(), arg0.getY()))
		{
			if(this.mouseOver==false)
			{
				this.mouseOver = true;
				this.repaint();
			}
		}
		else
		{
			if(this.mouseOver==true)
			{	
				this.mouseOver = false;
				this.repaint();
			}
				
		}
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		
	}

	public void mouseEntered(MouseEvent arg0)
	{
		
	}

	public void mouseExited(MouseEvent arg0)
	{
		this.mouseOver = false;
		this.repaint();
	}

	public void mousePressed(MouseEvent arg0) 
	{
		
	}

	public void mouseReleased(MouseEvent arg0)
	{
		
	}
	
	
}

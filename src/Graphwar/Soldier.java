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

import java.util.Random;

import GraphServer.Constants;

public class Soldier
{
	private int x;
	private int y;
	private double angle;
	
	private boolean alive;	
	private boolean exploding;
	private long timeExplodingStarted;
	private int killPosition;
	
	private String function;
	
	private boolean animating;
	private int animationNum;
	private long timeAnimationStarted;
	private long nextAnimation;
	
	private static Random random = new Random();
	
	public Soldier()
	{
		this.x = 0;
		this.y = 0;
		
		this.angle = 0;
		
		this.alive = false;
		this.exploding = false;
		
		this.function = "";
	}
	
	public Soldier(int x, int y)
	{
		this.x = x;
		this.y = y;
		
		this.angle = 0;
		
		this.alive = true;
		
		this.function = "";
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public void setAngle(double angle)
	{
		this.angle = angle;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	
	public int getKillPosition()
	{
		return killPosition;
	}
	
	public void setKillPosition(int killPosition)
	{
		this.killPosition = killPosition;
	}
	
	public boolean isExploding()
	{
		return exploding;
	}

	public void setExploding(boolean exploding)
	{
		this.exploding = exploding;
		
		if(exploding == true)
		{
			timeExplodingStarted = System.currentTimeMillis();
		}
	}
	
	public long getTimeExploding()
	{
		return System.currentTimeMillis() - timeExplodingStarted;
	}
	
	public String getFunction()
	{
		return function;
	}
	
	public void setFunction(String function)
	{
		this.function = function;
	}
	
	public boolean isAnimating()
	{
		if(animating)
		{
			return true;
		}
		else
		{
			if(System.currentTimeMillis() > nextAnimation)
			{
				animating = true;
				timeAnimationStarted = System.currentTimeMillis();
				animationNum = random.nextInt(Integer.MAX_VALUE);
				return true;
			}
			
			return false;
		}
	}
	
	public int getAnimationNum()
	{
		return animationNum;
	}
	
	public long getAnimationTime()
	{
		return System.currentTimeMillis() - timeAnimationStarted;
	}
	
	public void endAnimation()
	{
		animating = false;
		
		nextAnimation = (long) (System.currentTimeMillis() + Math.abs(random.nextGaussian()*Constants.SOLDIER_ANIMATION_DELAY_STANDARD_DEVIATION + Constants.SOLDIER_ANIMATION_MEAN_VALUE));
	}
	
}

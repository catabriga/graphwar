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

public class FunctionToken
{	
	public static final int ADD = 1;
	public static final int SUBTRACT = 2;
	public static final int MULTIPLY = 3;
	public static final int DIVIDE = 4;
	public static final int POW = 5;	
	public static final int SQRT = 6;
	public static final int LOG = 7;
	public static final int ABS = 8;
	public static final int SIN = 9;
	public static final int COS = 10;
	public static final int TAN = 11;	
	public static final int LN = 12;
	public static final int VARIABLE1 = 13;
	public static final int VARIABLE2 = 14;
	public static final int VARIABLE3 = 15;
	public static final int VALUE = 16;
	public static final int LEFT_BRACKET = 17;
	public static final int RIGHT_BRACKET = 18;
		
	private int type;		

	FunctionToken(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
}

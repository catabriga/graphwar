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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GraphServer.Constants;

public class PolishNotationFunction
{			
	private static Random random = new Random();
	
	private FunctionToken[] function;
	
	private int readLocation;
		
	
	PolishNotationFunction()
	{
		function = new FunctionToken[0];
	}
	
	PolishNotationFunction(String funcStr) throws MalformedFunction
	{
		FunctionToken[] normalNotation = createRegularNotationTokens(funcStr);
				
		function = reorderTokensToPolishNotation(normalNotation);
		
		/*
		for(int i=0; i<function.length; i++)
		{
			System.out.print("["+printToken(function[i])+"]");
		}
		System.out.println();
		*/
		
		if(getValuesNeeded() != 0)
		{
			throw new MalformedFunction();
		}
	}
	
	private FunctionToken[] reorderTokensToPolishNotation(FunctionToken[] funcTokens)
	{
		List<FunctionToken> polishTokensList = new ArrayList<FunctionToken>(funcTokens.length);
		
		reorderRec(polishTokensList, funcTokens, 0, funcTokens.length-1);
		
		FunctionToken[] polishTokens = polishTokensList.toArray(new FunctionToken[0]);
		
		return polishTokens;
	}
	
	private boolean reorderRec(List<FunctionToken> polishTokens, FunctionToken[] funcTokens, int start, int end)
	{
		if(start > end || start>=funcTokens.length)
		{
			return false;
		}
		
		int next = -1;
		int nextNest = Integer.MAX_VALUE;
		
		int nest = 0;
		
		for(int i=start; i<=end; i++)
		{
			if(funcTokens[i].getType()==FunctionToken.LEFT_BRACKET)
			{
				nest++;
			}
			else if(funcTokens[i].getType()==FunctionToken.RIGHT_BRACKET)
			{
				nest--;
			}			
			else if(nest<nextNest || (nest==nextNest && (next==-1 ||  precedes(funcTokens[i].getType(), funcTokens[next].getType()))))
			{
				next = i;
				nextNest = nest;
			}
		}
		
		if(next == -1)
		{
			return false;			
		}
		else
		{		
			switch(getNumParam(funcTokens[next].getType()))
			{
				case 0:
				{
					polishTokens.add(funcTokens[next]);
				}break;
				
				case 1:
				{
					polishTokens.add(funcTokens[next]);
					reorderRec(polishTokens, funcTokens, next+1, end);
				}break;
				
				case 2:
				{
					polishTokens.add(funcTokens[next]);
					boolean leftExists = reorderRec(polishTokens, funcTokens, start, next-1);
					
					if(funcTokens[next].getType() == FunctionToken.ADD)
					{
						//Add function may have only one operand
						if(leftExists == false)
						{
							polishTokens.add(new ValueToken(0));
						}
					}
					
					reorderRec(polishTokens, funcTokens, next+1, end);
					
					
				}break;
			}
			
			return true;
		}
			
	}
	
	private boolean precedes(int t0, int t1)
	{
		if(t0 < t1)
		{
			return true;
		}
		
		return false;
	}
	
	
	private List<FunctionToken> adjustImplicitMultiplications(List<FunctionToken> tokens)
	{
		ListIterator<FunctionToken> itr = tokens.listIterator();
		
		FunctionToken last = null;
		
		if(itr.hasNext())
		{
			last = itr.next();
		}
		else
		{
			return tokens;
		}
		
		while(itr.hasNext())
		{
			FunctionToken next = itr.next();
			
			if(isImplicit(last.getType(), next.getType()))
			{
				itr.previous();
				itr.add(new FunctionToken(FunctionToken.MULTIPLY));
				itr.next();
			}
			
			last = next;
		}
		
		return tokens;
	}
	
	private boolean isImplicit(int type1, int type2)
	{
		if(	type1==FunctionToken.VALUE || type1==FunctionToken.VARIABLE1 || type1==FunctionToken.VARIABLE2 ||
			type1==FunctionToken.VARIABLE3 || type1==FunctionToken.RIGHT_BRACKET)
		{
			if(	type2==FunctionToken.VALUE || type2==FunctionToken.VARIABLE1 || type2==FunctionToken.VARIABLE2 ||
				type2==FunctionToken.VARIABLE3 || type2==FunctionToken.LEFT_BRACKET || getNumParam(type2)==1)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private FunctionToken[] createRegularNotationTokens(String argStr)
	{	
		String funcStr = argStr.toLowerCase();
			
		funcStr = funcStr.replaceAll("-", "+-");
		funcStr = funcStr.replaceAll("exp", "e^");
		funcStr = funcStr.replaceAll(",", ".");
		
		Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+|\\(|\\)|x|y|y'|\\+|\\*|/|\\^|sqrt|log|abs|sin|sen|cos|tan|tg|-|ln|e|pi");
	  
		Matcher m = pattern.matcher(funcStr);
		
		int maxSize = funcStr.length();
		
		List<FunctionToken> normalNotation = new ArrayList<FunctionToken>(maxSize);
		
		while (m.find()) 
		{		
			String token = funcStr.substring(m.start(0), m.end(0));
			try
			{
				double value = Double.parseDouble(token);
				
				normalNotation.add(new ValueToken(value));
			}
			catch(Exception e)
			{				
				if(token.equals("x"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.VARIABLE1));
				}
				else if(token.equals("y"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.VARIABLE2));
				}
				else if(token.equals("y'"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.VARIABLE3));
				}
				else if(token.equals("+"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.ADD));
				}
				else if(token.equals("-"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.SUBTRACT));
				}
				else if(token.equals("*"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.MULTIPLY));
				}
				else if(token.equals("/"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.DIVIDE));
				}
				else if(token.equals("sqrt"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.SQRT));
				}
				else if(token.equals("log"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.LOG));
				}
				else if(token.equals("abs"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.ABS));
				}
				else if(token.equals("sin") || token.equals("sen"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.SIN));
				}
				else if(token.equals("cos"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.COS));
				}
				else if(token.equals("tan") || token.equals("tg"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.TAN));
				}
				else if(token.equals("^"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.POW));
				}
				else if(token.equals("ln"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.LN));
				} 
				else if(token.equals("e"))
				{
					normalNotation.add(new ValueToken(Math.E));
				}
				else if(token.equals("pi"))
				{
					normalNotation.add(new ValueToken(Math.PI));
				}										
				else if(token.equals("("))
				{
					normalNotation.add(new FunctionToken(FunctionToken.LEFT_BRACKET));
				}
				else if(token.equals(")"))
				{
					normalNotation.add(new FunctionToken(FunctionToken.RIGHT_BRACKET));
				}
			}			
		}	
		
		normalNotation = adjustImplicitMultiplications(normalNotation);
		
		return normalNotation.toArray(new FunctionToken[0]);
	}
	
	PolishNotationFunction(FunctionToken[] function)
	{	
		this.function = function;
	}
	
	PolishNotationFunction(PolishNotationFunction function1, int gameMode)
	{	
		if(random.nextBoolean())
		{
			function = mutateFineTune(function1, gameMode);
		}
		else
		{
			function = mutateRegion(function1, gameMode);			
		}
	}
	
	private FunctionToken[] mutateRegion(PolishNotationFunction function1, int gameMode)
	{
		//Creates a new function mutating a region of the old function
		
		int lengthOut = (int)(Constants.RANDOM_FUNC_MEAN_CROSSOVER_LENGTH*Math.abs(random.nextGaussian()));
		int lengthIn = (int)(Constants.RANDOM_FUNC_MEAN_CROSSOVER_LENGTH*Math.abs(random.nextGaussian()));
		
		if(lengthOut > function1.function.length)
		{
			lengthOut = function1.function.length;
		}
		
		int newSize = function1.function.length - lengthOut + lengthIn;
		
		FunctionToken[] function = new FunctionToken[newSize];
		
		int outLocation = random.nextInt(function1.function.length-lengthOut+1);
		
		for(int i=0; i<outLocation; i++)
		{
			function[i] = function1.function[i];
		}
		
		for(int i=0; i<lengthIn; i++)
		{
			function[outLocation+i] = getRandomToken(gameMode);
		}
		
		for(int i=0; i<function1.function.length-lengthOut-outLocation; i++)
		{
			function[outLocation+lengthIn+i] = function1.function[outLocation+lengthOut+i];
		}
		
		function = adjustFunction(function, gameMode);
		
		return function;
	}
	
	private FunctionToken[] mutateFineTune(PolishNotationFunction function1, int gameMode)
	{		
		PolishNotationFunction newFunction = function1.makeCopy();
		
		int numValues = 0;
		
		for(int i=0; i<newFunction.function.length; i++)
		{
			if(newFunction.function[i].getType() == FunctionToken.VALUE)
			{
				numValues++;
			}
		}
		
				
		if(numValues == 0)
		{
			makeRandomFunction(gameMode);
			return this.function;
		}
		
		int mutatedValue = random.nextInt(numValues);
		
		for(int i=0; i<newFunction.function.length; i++)
		{
			if(newFunction.function[i].getType() == FunctionToken.VALUE)
			{								
				if(mutatedValue == 0)
				{
					if(random.nextBoolean())
					{
						newFunction.function[i] = new ValueToken(random.nextGaussian()*Constants.RANDOM_FUNC_VALUE_MEAN);
					}
					else
					{
						newFunction.function[i] = new ValueToken(((ValueToken) newFunction.function[i]).getValue()*(random.nextGaussian()+1));						
					}
					
					break;
				}
				
				mutatedValue--;
			}			
		}
		
		return newFunction.function;
	}
	
	PolishNotationFunction(PolishNotationFunction function1, PolishNotationFunction function2, int gameMode)
	{
		//Creates a new function doing crossover in other 2 functions
		
		if(random.nextBoolean())
		{
			PolishNotationFunction temp = function1;
			function1 = function2;
			function2 = temp;
		}
		
		int lengthCopy1 = (int)(Constants.RANDOM_FUNC_MEAN_CROSSOVER_LENGTH*Math.abs(random.nextGaussian()));
		int lengthCopy2 = (int)(Constants.RANDOM_FUNC_MEAN_CROSSOVER_LENGTH*Math.abs(random.nextGaussian()));
		
		int length1 = function1.function.length;
		int length2 = function2.function.length;
		
		if(lengthCopy1 > length1)
		{
			lengthCopy1 = length1;
		}
		
		if(lengthCopy2 > length2)
		{
			lengthCopy2 = length2;
		}
		
		int copyLocation1 = random.nextInt(length1-lengthCopy1+1);
		int copyLocation2 = random.nextInt(length2-lengthCopy2+1);
		
		int newSize = length1 - lengthCopy1 + lengthCopy2;
		
		function = new FunctionToken[newSize];
		
		for(int i=0; i<copyLocation1; i++)
		{
			function[i] = function1.function[i];
		}
		
		for(int i=0; i<lengthCopy2; i++)
		{
			function[copyLocation1+i] = function2.function[copyLocation2+i];
		}
		
		for(int i=0; i<length1-lengthCopy1-copyLocation1; i++)
		{
			function[copyLocation1+lengthCopy2+i] = function1.function[copyLocation1+lengthCopy1+i];
		}
		
		function = adjustFunction(function, gameMode);
	}
	
	public PolishNotationFunction makeCopy()
	{
		PolishNotationFunction newFunction = new PolishNotationFunction();
		
		newFunction.function = new FunctionToken[function.length];
		
		for(int i=0; i<newFunction.function.length; i++)
		{
			if(function[i].getType() == FunctionToken.VALUE)
			{
				newFunction.function[i] = new ValueToken(((ValueToken) function[i]).getValue());
			}
			else
			{
				newFunction.function[i] = new FunctionToken(function[i].getType());
			}			
		}
		
		return newFunction;
	}
	
	public String getStringFunction()
	{		
		readLocation = 0;
		
		return makeString();
	}
	
	private String makeString()
	{
		String functionString = "";
				
		FunctionToken currentToken = function[readLocation];
		readLocation++;
		
		int type = currentToken.getType();		
			
		if(isOperation(type))
		{
			if(getNumParam(type) == 2)
			{
				functionString += "("+makeString();
				functionString += printToken(currentToken);
				functionString += makeString()+")";
			}
			else
			{
				if(type==FunctionToken.SUBTRACT)
				{
					functionString += "("+printToken(currentToken);
					functionString += "("+makeString()+"))";
				}
				else
				{
					functionString += printToken(currentToken);
					functionString += "("+makeString()+")";
				}
			}
		}
		else
		{
			if(type==FunctionToken.VALUE && ((ValueToken)currentToken).getValue() < 0)
			{				
				functionString += "("+printToken(currentToken)+")";				
			}
			else
			{
				functionString += printToken(currentToken);
			}
		}	
		
		return functionString;
	}
		
	private String printToken(FunctionToken functionToken)
	{
		String str = "";
	
		switch(functionToken.getType())
		{
			case FunctionToken.VARIABLE1:
				str = "x";
				break;
				
			case FunctionToken.VARIABLE2:
				str = "y";
				break;
				
			case FunctionToken.VARIABLE3:
				str = "y'";
				break;

			case FunctionToken.VALUE:			
				NumberFormat formatter = new DecimalFormat("#######.##");				   
				str = formatter.format(((ValueToken) functionToken).getValue());  
				//str = Double.toString(((double)((int)(100*((ValueToken) functionToken).getValue())))/100.0);				
				break;

			case FunctionToken.ADD:
				str = "+";
				break;

			case FunctionToken.SUBTRACT:
				str = "-";
				break;

			case FunctionToken.MULTIPLY:
				str = "*";
				break;

			case FunctionToken.DIVIDE:
				str = "/";
				break;

			case FunctionToken.SQRT:
				str = "sqrt";
				break;

			case FunctionToken.LOG:
				str = "log";
				break;

			case FunctionToken.ABS:
				str = "abs";
				break;

			case FunctionToken.SIN:
				str = "sin";
				break;

			case FunctionToken.COS:
				str = "cos";
				break;

			case FunctionToken.TAN:
				str = "tan";
				break;

			case FunctionToken.POW:
				str = "^";
				break;

			case FunctionToken.LN:
				str = "ln";
				break;

		}
		
		return str;
	}
		
	public void makeRandomFunction(int gameMode)
	{
		int newSize = (int)(Constants.RANDOM_FUNC_MEAN_LENGTH*Math.abs(random.nextGaussian()));
		
		function = new FunctionToken[newSize];
		
		for(int i=0; i<newSize; i++)
		{
			function[i] = getRandomToken(gameMode);
		}
		
		function = adjustFunction(function, gameMode);
	}
	
	private int getValuesNeeded()
	{
		// Counts how many values are needed to finish a valid function, 2 terms operations add 1 and values decrease 1
		// It may be necessary to finish the function early or add elements at the end
		int valuesNeeded = 1;
		
		for(int i=0; i<function.length; i++)
		{
			if(isOperation(function[i].getType()))
			{
				valuesNeeded += getNumParam(function[i].getType())-1;
			}
			else
			{
				valuesNeeded--;
			}	
			
			if(valuesNeeded == 0 && i+1<function.length)
			{
				return -1;
			}
		}
		
		return valuesNeeded;
	}
	
	private FunctionToken[] adjustFunction(FunctionToken[] function, int gameMode)
	{
		/*
		for(int i=0; i<function.length; i++)
		{
			System.out.print("["+printToken(function[i])+"]");
		}
		System.out.print("\t --> \t");
		*/
		
		// Counts how many values are needed to finish a valid function, 2 terms operations add 1 and values decrease 1
		// It may be necessary to finish the function early or add elements at the end
		int valuesNeeded = 1;
		
		for(int i=0; i<function.length; i++)
		{
			if(isOperation(function[i].getType()))
			{
				valuesNeeded += getNumParam(function[i].getType())-1;
			}
			else
			{
				valuesNeeded--;
			}
			
			if(valuesNeeded == 0)
			{
				function = Arrays.copyOf(function, i+1);
				//System.out.println();
				return function;
			}
		}
		
		int oldSize = function.length;
		int newSize = oldSize + valuesNeeded;
		
		function = Arrays.copyOf(function, newSize);
		
		for(int i=oldSize; i<newSize; i++)
		{
			function[i] = getRandomValueToken(gameMode);
		}
		
		/*
		for(int i=0; i<function.length; i++)
		{
			System.out.print("["+printToken(function[i])+"]");
		}
		System.out.println();
		*/
		
		return function;
	}
	
	private boolean isOperation(int type)
	{
		if(type >= 1 && type <= 12)
		{
			return true;
		}
		
		return false;
	}
	
	private int getNumParam(int type)
	{
		if(type==2)
		{
			return 1;
		}
		else if(type>=1 && type<=5 )
		{
			return 2;
		}
		else if(type>=6 && type<=12)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	private FunctionToken getRandomValueToken(int gameMode)
	{
		FunctionToken token = null;
		
		if(random.nextDouble() < Constants.RANDOM_FUNC_VARIABLE_CHANCE)
		{
			switch(gameMode)
			{
				case Constants.NORMAL_FUNC:
				{
					token = new FunctionToken(FunctionToken.VARIABLE1);
				}break;
				
				case Constants.FST_ODE:
				{
					if(random.nextBoolean())
					{
						token = new FunctionToken(FunctionToken.VARIABLE1);
					}
					else
					{
						token = new FunctionToken(FunctionToken.VARIABLE2);
					}
				}break;
				
				case Constants.SND_ODE:
				{
					if(random.nextInt(3)==0)
					{
						token = new FunctionToken(FunctionToken.VARIABLE1);
					}
					else if(random.nextBoolean())
					{
						token = new FunctionToken(FunctionToken.VARIABLE2);
					}
					else
					{
						token = new FunctionToken(FunctionToken.VARIABLE3);
					}
				}break;
			}
			
		}
		else
		{
			token = new ValueToken(random.nextGaussian()*Constants.RANDOM_FUNC_VALUE_MEAN);
		}
		
		return token;
	}
	
	private FunctionToken getRandomToken(int gameMode)
	{
		FunctionToken token = null;
		
		if(random.nextDouble() < Constants.RANDOM_FUNC_VALUE_CHANCE)
		{
			token = getRandomValueToken(gameMode);
		}
		else
		{
			token = new FunctionToken(getRandomOperator());
		}
		
		return token;
	}

	private char getRandomOperator()
	{
		char type = 0;
		
		switch(random.nextInt(19))
		{
			case 0:
			{
				type = FunctionToken.SQRT;
			}break;
			
			case 1:
			{
				type = FunctionToken.LOG;
			}break;
			
			case 2:
			{
				type = FunctionToken.ABS;
			}break;
			
			case 3:
			{
				type = FunctionToken.SIN;
			}break;
			
			case 4:
			{
				type = FunctionToken.COS;
			}break;
			
			case 5:
			{
				type = FunctionToken.TAN;
			}break;
									
			case 6:
			{
				type = FunctionToken.LN;
			}break;
			
			case 7:
			case 8:
			case 9:	
			case 10:
			{
				type = FunctionToken.ADD;
			}break;			
			
			case 11:
			case 12:
			case 13:
			{
				type = FunctionToken.MULTIPLY;
			}break;
			
			case 14:
			case 15:
			case 16:
			{
				type = FunctionToken.DIVIDE;
			}break;
			
			case 17:
			case 18:			
			{
				type = FunctionToken.POW;
			}break;
			
		}
		
		return type;
	}
	
	public PolishNotationFunction simplifyFunction()
	{		
		this.readLocation = 0;
		
		List<FunctionToken> newFunction = simplifyRec();
		
		return new PolishNotationFunction(newFunction.toArray(new FunctionToken[0]));
	}
	
	private List<FunctionToken> simplifyRec()
	{
		List<FunctionToken> tokens = new LinkedList<FunctionToken>();
		
		FunctionToken currentToken = function[readLocation];
		readLocation++;
		
		if(getNumParam(currentToken.getType()) == 2)
		{
			List<FunctionToken> list1 = simplifyRec();
			List<FunctionToken> list2 = simplifyRec();
			
			if(list1.size()==1 && list2.size()==1)
			{
				FunctionToken param1 = list1.get(0);
				FunctionToken param2 = list2.get(0);
				
				if(param1.getType() == FunctionToken.VALUE && param2.getType() == FunctionToken.VALUE)
				{
					double value = evaluateToken(currentToken, ((ValueToken)param1).getValue(), ((ValueToken)param2).getValue());
					
					tokens.add(new ValueToken(value));
					
					return tokens;
				}
			}			
						
			tokens.add(currentToken);
			tokens.addAll(list1);
			tokens.addAll(list2);
			
			return tokens;
			
		}
		else if(getNumParam(currentToken.getType())==1)
		{
			List<FunctionToken> list1 = simplifyRec();
			
			if(list1.size()==1)
			{
				FunctionToken param1 = list1.get(0);
				
				if(param1.getType() == FunctionToken.VALUE)
				{
					double value = evaluateToken(currentToken, ((ValueToken)param1).getValue(), 0);
					
					tokens.add(new ValueToken(value));
					
					return tokens;
				}
			}			
						
			tokens.add(currentToken);
			tokens.addAll(list1);
			
			return tokens;
		}
		else
		{
			tokens.add(currentToken);
			
			return tokens;
		}
	}
	
	private double var1;
	private double var2;
	private double var3;
	public double evaluateFunction(double var1, double var2, double var3)
	{
		this.var1 = var1;
		this.var2 = var2;
		this.var3 = var3;
		this.readLocation = 0;
		
		return evaluateRec();
	}
	
	private double evaluateToken(FunctionToken token, double param1, double param2)
	{
		double returnValue = 0;
		
		switch(token.getType())
		{
			case FunctionToken.VARIABLE1:
				returnValue = var1;
				break;
				
			case FunctionToken.VARIABLE2:
				returnValue = var2;
				break;
				
			case FunctionToken.VARIABLE3:
				returnValue = var3;
				break;
	
			case FunctionToken.VALUE:			
				returnValue = ((ValueToken)token).getValue();
				break;
	
			case FunctionToken.ADD:
				returnValue = param1 + param2;
				break;
	
			case FunctionToken.SUBTRACT:
				returnValue = -param1;
				break;
	
			case FunctionToken.MULTIPLY:
				returnValue = param1 * param2;
				break;
	
			case FunctionToken.DIVIDE:
				returnValue = param1 / param2;
				break;
	
			case FunctionToken.SQRT:
				returnValue = Math.sqrt(param1);
				break;
	
			case FunctionToken.LOG:
				returnValue = Math.log10(param1);
				break;
	
			case FunctionToken.ABS:
				returnValue = Math.abs(param1);
				break;
	
			case FunctionToken.SIN:
				returnValue = Math.sin(param1);
				break;
	
			case FunctionToken.COS:
				returnValue = Math.cos(param1);
				break;
	
			case FunctionToken.TAN:
				returnValue = Math.tan(param1);
				break;
	
			case FunctionToken.POW:
				returnValue = Math.pow(param1,param2);
				break;
	
			case FunctionToken.LN:
				returnValue = Math.log(param1);
				break;
		}
		
		return returnValue;
	}
	
	private double evaluateRec()
	{
		FunctionToken currentToken = function[readLocation];
		readLocation++;
		
		double returnValue = 0;
		
		switch(currentToken.getType())
		{
			case FunctionToken.VARIABLE1:
				returnValue = var1;
				break;
				
			case FunctionToken.VARIABLE2:
				returnValue = var2;
				break;
				
			case FunctionToken.VARIABLE3:
				returnValue = var3;
				break;
	
			case FunctionToken.VALUE:			
				returnValue = ((ValueToken)currentToken).getValue();
				break;
	
			case FunctionToken.ADD:
				returnValue = evaluateRec() + evaluateRec();
				break;
	
			case FunctionToken.SUBTRACT:
				returnValue = -evaluateRec();
				break;
	
			case FunctionToken.MULTIPLY:
				returnValue = evaluateRec() * evaluateRec();
				break;
	
			case FunctionToken.DIVIDE:
				returnValue = evaluateRec() / evaluateRec();
				break;
	
			case FunctionToken.SQRT:
				returnValue = Math.sqrt(evaluateRec());
				break;
	
			case FunctionToken.LOG:
				returnValue = Math.log10(evaluateRec());
				break;
	
			case FunctionToken.ABS:
				returnValue = Math.abs(evaluateRec());
				break;
	
			case FunctionToken.SIN:
				returnValue = Math.sin(evaluateRec());
				break;
	
			case FunctionToken.COS:
				returnValue = Math.cos(evaluateRec());
				break;
	
			case FunctionToken.TAN:
				returnValue = Math.tan(evaluateRec());
				break;
	
			case FunctionToken.POW:
				returnValue = Math.pow(evaluateRec(),evaluateRec());
				break;
	
			case FunctionToken.LN:
				returnValue = Math.log(evaluateRec());
				break;
		}
		
		return returnValue;
	}
	
	public static void main(String[] args)
	{	
		/*
		PolishNotationFunction function;
		
		try 
		{
			function = new PolishNotationFunction("e^(-((x-8)^2))");
			System.out.println(function.getStringFunction());
		} 
		catch (MalformedFunction e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		for(int i=0; i<10; i++)
		{
			PolishNotationFunction function1 = new PolishNotationFunction();
			PolishNotationFunction function2 = new PolishNotationFunction();			
			
			function1.makeRandomFunction(Constants.SND_ODE);
			function2.makeRandomFunction(Constants.SND_ODE);
			
			PolishNotationFunction function3 = new PolishNotationFunction(function1,function2,Constants.SND_ODE);
			
			PolishNotationFunction function4 = new PolishNotationFunction(function1,Constants.SND_ODE);
			PolishNotationFunction function5 = new PolishNotationFunction(function2,Constants.SND_ODE);	
			
			System.out.println(function1.getStringFunction());
			System.out.println(function2.getStringFunction());
			System.out.println(function3.getStringFunction());
			System.out.println(function4.getStringFunction());
			System.out.println(function5.getStringFunction());
			System.out.println();
		}
	}
	
}

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

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class GraphTextBox extends JScrollPane
{
	private JTextPane textPane;
	
	private StyledDocument text;
	private Style nameStyle;
	private Style messageStyle;
	private Style systemStyle;
	
	public GraphTextBox()
	{
		textPane = new JTextPane();
		textPane.setEditable(false);
		text = (StyledDocument) textPane.getDocument();
		
		nameStyle = text.addStyle("nameStyle", null);
		messageStyle = text.addStyle("messageStyle", null);
		systemStyle = text.addStyle("systemStyle", null);
		
		StyleConstants.setForeground(systemStyle, new Color(160,160,160));
		StyleConstants.setItalic(systemStyle, true);
				
		this.setViewportView(textPane);
		
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	public void addText(final String playerName, final Color playerColor, final String message)
	{	
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				boolean autoScrolls = getVerticalScrollBar().getMaximum() - (getVerticalScrollBar().getValue() + getVerticalScrollBar().getVisibleAmount()) < 20;
				
				if(playerName == null)
				{
					try 
					{
						text.insertString(text.getLength(), message+"\n", systemStyle);
					} 
					catch (BadLocationException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					StyleConstants.setForeground(nameStyle, playerColor);
					StyleConstants.setBold(nameStyle, true);
										
					try
					{						
						text.insertString(text.getLength(), playerName+": ", nameStyle);
						text.insertString(text.getLength(), message+"\n", messageStyle);					
					} 
					catch (BadLocationException e)
					{
						e.printStackTrace();
					}	
					
				}
				
				if(autoScrolls)
				{
					//this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMaximum()-this.getVerticalScrollBar().getVisibleAmount());
					textPane.setCaretPosition(text.getLength());		
				}
				
				revalidate();
			}
		}
		);
		
	}
	
	public void emptyText()
	{
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				try
				{
					text.remove(0, text.getLength());
				} 
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		}
		);
	}
}

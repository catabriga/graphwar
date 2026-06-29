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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import GraphServer.Constants;

public class GameScreen extends JPanel implements ActionListener, StartStopPanel, KeyListener, MouseListener
{
	private Graphwar graphwar;
	
	private JLabel[] backgroundImages;
	
	private JLabel yImg;
	private JLabel dyImg;
	private JLabel ddyImg;
	
	private GraphButton fire;
	private GraphButton quit;
	private GraphButton global;	
	private JTextField funcField;	
	private JTextField chatField;	
	private GraphTextBox chatBox;	
	private GraphPlane plane;
	private GraphTimer timer;
	private GraphAngleDisplay angleDisplay;
	private JDialog functionHelperWindow;
	private JTextField targetXField;
	private JTextField targetYField;
	private JButton targetGenerateButton;
	private JButton targetUndoButton;
	private JButton targetResetButton;
	private JLabel targetStatusLabel;
	private JLabel targetCountLabel;
	private ArrayList<Double> targetXs;
	private ArrayList<Double> targetYs;
	
	private JLabel[] backgroundsQuit;
	private GraphButton yesQuit;
	private GraphButton noQuit;
	private boolean quitVisible;
	
	private JLabel[] backgroundsShowMessage;
	private GraphButton okButton;
	private JLabel messageLabel;
	private boolean showMessageVisible;
	
	public GameScreen(Graphwar graphwar, String confFile) throws Exception
	{		
		this.graphwar = graphwar;
		
		this.setLayout(null);		
		this.setBounds(0,0,Constants.WIDTH,Constants.HEIGHT);
		
		BufferedReader read =  new BufferedReader(new InputStreamReader(graphwar.getClass().getResourceAsStream(confFile)));
		
		// Stack used to add components in reverse order, because swing draws components added first first
		// and that is not intuitive to the way the data is read from the files
		Stack<Component> components = new Stack<Component>();
		
		/////	Main Screen		/////////////
		
			int numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundImages = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundImages[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundImages[i]);
			}
			
			yImg = GraphUtil.makeBackgroundImage(graphwar, read);
			dyImg = GraphUtil.makeBackgroundImage(graphwar, read);
			ddyImg = GraphUtil.makeBackgroundImage(graphwar, read);
			fire = GraphUtil.makeButton(graphwar, read);
			quit = GraphUtil.makeButton(graphwar, read);
			global = GraphUtil.makeButton(graphwar, read);			
			funcField = GraphUtil.makeTextField(read);			
			chatField = GraphUtil.makeTextField(read);			
			chatBox = GraphUtil.makeTextBox(read);
			
			plane = new GraphPlane(graphwar);
			plane.setBounds(15, 15, Constants.PLANE_LENGTH, Constants.PLANE_HEIGHT);
			
			timer = new GraphTimer(graphwar);
			timer.setBounds(221, 564, 57, 22);
			
			angleDisplay = new GraphAngleDisplay(graphwar);
			angleDisplay.setBounds(10, 475, 200, 113);

			targetXs = new ArrayList<Double>();
			targetYs = new ArrayList<Double>();
			functionHelperWindow = makeFunctionHelperWindow();
			
			components.push(yImg);
			components.push(dyImg);
			components.push(ddyImg);
			components.push(angleDisplay);
			components.push(fire);
			components.push(quit);
			components.push(global);
			components.push(plane);
			components.push(funcField);
			components.push(chatField);			
			components.push(chatBox);			
			components.push(timer);
			
			this.fire.addActionListener(this);
			this.quit.addActionListener(this);
			this.global.addActionListener(this);
			this.funcField.addActionListener(this);
			this.chatField.addActionListener(this);
			this.targetGenerateButton.addActionListener(this);
			this.targetUndoButton.addActionListener(this);
			this.targetResetButton.addActionListener(this);
			this.targetXField.addActionListener(this);
			this.targetYField.addActionListener(this);
			
			this.dyImg.setVisible(false);
			this.ddyImg.setVisible(false);
			this.global.setVisible(false);
		//////// Quit Confirmation ///////////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsQuit = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsQuit[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsQuit[i]);
			}							
			
			this.yesQuit = GraphUtil.makeButton(graphwar, read);
			this.noQuit = GraphUtil.makeButton(graphwar, read);
			
			components.push(yesQuit);
			components.push(noQuit);
			
			showQuit(false);
			
			this.yesQuit.addActionListener(this);
			this.noQuit.addActionListener(this);
			
		///// Show Message //////////////
			
			numBackgroundImages = Integer.parseInt(GraphUtil.nextLine(read));			
			backgroundsShowMessage = new JLabel[numBackgroundImages];
			
			for(int i=0; i<numBackgroundImages; i++)
			{
				backgroundsShowMessage[i] = GraphUtil.makeBackgroundImage(graphwar, read);
				components.push(backgroundsShowMessage[i]);
			}				
			
			this.okButton = GraphUtil.makeButton(graphwar, read);			
			this.messageLabel = GraphUtil.makeTextLabel(read);
			
			components.push(okButton);
			components.push(messageLabel);
			
			messageLabel.setFont(new Font("Sans", Font.BOLD, 20));
			messageLabel.setForeground(Color.ORANGE);
			messageLabel.setHorizontalAlignment(JLabel.CENTER);
			messageLabel.setVerticalAlignment(JLabel.CENTER);
			
			showShowMessage(false);
			
			okButton.addActionListener(this);
			
		/////////////////////////////////////
			
		/////////////////////////////////////////////////////////
		
				
		this.funcField.addKeyListener(this);
		this.chatField.addKeyListener(this);
		
		this.addMouseListener(this);
		this.plane.addMouseListener(this);
		this.angleDisplay.addMouseListener(this);
		
		this.setFocusable(true);
		this.addKeyListener(this);

		this.addComponentsReversed(this, components);
		this.revalidate();
	}

	private JDialog makeFunctionHelperWindow()
	{
		JPanel panel = new JPanel(null);
		panel.setPreferredSize(new Dimension(260, 112));
		panel.setBackground(new Color(238, 245, 238));
		panel.setBorder(BorderFactory.createLineBorder(new Color(65, 100, 65)));

		JLabel title = new JLabel("Aim helper");
		title.setFont(new Font("Sans", Font.BOLD, 12));
		title.setBounds(8, 4, 90, 18);
		panel.add(title);

		targetCountLabel = new JLabel("0 points");
		targetCountLabel.setFont(new Font("Sans", Font.PLAIN, 11));
		targetCountLabel.setBounds(150, 4, 100, 18);
		panel.add(targetCountLabel);

		JLabel xLabel = new JLabel("x");
		xLabel.setBounds(8, 27, 12, 20);
		panel.add(xLabel);

		targetXField = new JTextField();
		targetXField.setBounds(22, 27, 70, 22);
		panel.add(targetXField);

		JLabel yLabel = new JLabel("y");
		yLabel.setBounds(102, 27, 12, 20);
		panel.add(yLabel);

		targetYField = new JTextField();
		targetYField.setBounds(116, 27, 70, 22);
		panel.add(targetYField);

		targetGenerateButton = new JButton("Generate");
		targetGenerateButton.setBounds(8, 53, 94, 24);
		panel.add(targetGenerateButton);

		targetUndoButton = new JButton("Undo");
		targetUndoButton.setBounds(108, 53, 70, 24);
		panel.add(targetUndoButton);

		targetResetButton = new JButton("Reset");
		targetResetButton.setBounds(184, 53, 68, 24);
		panel.add(targetResetButton);

		targetStatusLabel = new JLabel("Click map");
		targetStatusLabel.setFont(new Font("Sans", Font.PLAIN, 11));
		targetStatusLabel.setBounds(8, 82, 244, 22);
		panel.add(targetStatusLabel);

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "undoTargetPoint");
		panel.getActionMap().put("undoTargetPoint",
		new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				undoTargetPoint();
			}
		}
		);

		JDialog dialog = new JDialog(graphwar, "Aim Helper", false);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocation(graphwar.getX()+Constants.WIDTH+10, graphwar.getY()+60);

		return dialog;
	}
	
	private void addComponentsReversed(JPanel panel, Stack<Component> components)
	{	
    	while(components.empty() == false)
    	{
    		panel.add(components.pop());
    	}
	}
	
	public void setNextMarker(boolean b)
	{
		plane.setNextMarker(b);
	}
	
	private void showShowMessage(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				okButton.setVisible(show);
				messageLabel.setVisible(show);
				
				for(int i=0; i<backgroundsShowMessage.length; i++)
				{
					backgroundsShowMessage[i].setVisible(show);
				}
				
				showMessageVisible = show;
				
				repaint();
			}
		}
		);
	}
	
	private void showQuit(boolean s)
	{
		final boolean show = s;
		
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				yesQuit.setVisible(show);
				noQuit.setVisible(show);
				quitVisible = show;
				
				for(int i=0; i<backgroundsQuit.length; i++)
				{
					backgroundsQuit[i].setVisible(show);
				}
			}
		}
		);
	}
	
	public void refreshGlobalButton()
	{
		SwingUtilities.invokeLater(
		new Runnable() 
		{
			public void run()
			{
				if(graphwar.getGlobalClient().isRunning())
				{
					global.setVisible(true);
				}
				else
				{
					global.setVisible(false);
				}
				
				repaint();
			}
		}
		);
	}
	
	public void paint(Graphics g)
	{
		plane.refreshBackground();
		
		super.paint(g);
	}
	
	public void paintComponent(Graphics g)
	{			
		g.setColor(Constants.BACKGROUND);
		g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);	
	}
	
	public void addChat(Player player, String chatMessage)
	{
		String name = null;
		Color color = null;
		
		if(player!=null)
		{
			name = player.getName();
			color = player.getColor();
		}
		
		chatBox.addText(name, color, chatMessage);
	}
	
	public void showMessage(String message)
	{
		this.messageLabel.setText(message);
		
		showShowMessage(true);
	}
	
	public void refreshBack()
	{
		plane.refreshBackground();
	}
	
	public void refreshSoldiers()
	{
		plane.refreshSoldiers();
	}

	public void refreshFunction()
	{
		Player player = graphwar.getGameData().getCurrentTurnPlayer();
		
		if(player.isLocalPlayer() && !(player instanceof ComputerPlayer))
		{
			final String function = player.getCurrentTurnSoldier().getFunction();
			
			SwingUtilities.invokeLater(
			new Runnable() 
			{
				public void run()
				{
					funcField.setEnabled(true);
					funcField.setText(function);
				}
			}
			);
		}
		else
		{
			SwingUtilities.invokeLater(
			new Runnable() 
			{
				public void run()
				{
					funcField.setEnabled(false);
				}
			}
			);
		}

		refreshFunctionHelper();
	}

	private void refreshFunctionHelper()
	{
		boolean enabled = false;
		String status = "Normal only";

		if(graphwar.getGameData().getGameMode() == Constants.NORMAL_FUNC)
		{
			Player player = graphwar.getGameData().getCurrentTurnPlayer();
			enabled = player.isLocalPlayer() && !(player instanceof ComputerPlayer) && graphwar.getGameData().isDrawingFunction() == false;
			status = "Click map";
		}

		final boolean helperEnabled = enabled;
		final String helperStatus = status;

		SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run()
			{
				targetXField.setEnabled(helperEnabled);
				targetYField.setEnabled(helperEnabled);
				targetGenerateButton.setEnabled(helperEnabled);
				targetUndoButton.setEnabled(helperEnabled && targetXs.size() > 0);
				targetResetButton.setEnabled(helperEnabled && targetXs.size() > 0);
				targetCountLabel.setText(targetXs.size()+" points");
				if(targetXs.size() == 0)
				{
					targetStatusLabel.setText(helperStatus);
				}
			}
		}
		);
	}

	private double getGameXFromBoardPixel(int x)
	{
		return ((double)x - (double)Constants.PLANE_LENGTH/2.0)
				* (double)Constants.PLANE_GAME_LENGTH/(double)Constants.PLANE_LENGTH;
	}

	private double getGameYFromBoardPixel(int y)
	{
		return ((double)Constants.PLANE_HEIGHT/2.0 - (double)y)
				* (double)Constants.PLANE_GAME_LENGTH/(double)Constants.PLANE_LENGTH;
	}

	private int getBoardPixelXFromGame(double x)
	{
		return (int)Math.round(Constants.PLANE_LENGTH*x/Constants.PLANE_GAME_LENGTH + Constants.PLANE_LENGTH/2.0);
	}

	private int getBoardPixelYFromGame(double y)
	{
		return (int)Math.round(-Constants.PLANE_LENGTH*y/Constants.PLANE_GAME_LENGTH + Constants.PLANE_HEIGHT/2.0);
	}

	private void selectTargetFromPlane(int displayX, int displayY)
	{
		if(graphwar.getGameData().getGameMode() != Constants.NORMAL_FUNC)
		{
			targetStatusLabel.setText("Normal only");
			return;
		}

		int boardX = displayX;
		if(graphwar.getGameData().isTerrainReversed())
		{
			boardX = Constants.PLANE_LENGTH - boardX;
		}

		int boardY = displayY;

		double x = getGameXFromBoardPixel(boardX);
		double y = getGameYFromBoardPixel(boardY);

		addTargetPoint(x, y);
	}

	private void addTargetPointFromFields()
	{
		double x;
		double y;

		try
		{
			x = Double.parseDouble(targetXField.getText().trim().replace(',', '.'));
			y = Double.parseDouble(targetYField.getText().trim().replace(',', '.'));
		}
		catch(NumberFormatException e)
		{
			targetStatusLabel.setText("Bad point");
			return;
		}

		addTargetPoint(x, y);
	}

	private void addTargetPoint(double x, double y)
	{
		targetXField.setText(formatNumber(x));
		targetYField.setText(formatNumber(y));
		targetXs.add(new Double(x));
		targetYs.add(new Double(y));
		targetStatusLabel.setText("Point added");
		refreshTargetMarkers();
		refreshFunctionHelper();
	}

	private String formatNumber(double value)
	{
		if(Math.abs(value) < 0.00005)
		{
			value = 0;
		}

		String text = String.format(Locale.US, "%.4f", value);

		while(text.indexOf('.') >= 0 && text.endsWith("0"))
		{
			text = text.substring(0, text.length()-1);
		}

		if(text.endsWith("."))
		{
			text = text.substring(0, text.length()-1);
		}

		return text;
	}

	private String formatSignedNumber(double value)
	{
		String number = formatNumber(Math.abs(value));
		if(value < 0)
		{
			return "-"+number;
		}

		return "+"+number;
	}

	private double getFunctionX(Player player, double worldX)
	{
		if(player.getTeam() == Constants.TEAM2)
		{
			return -worldX;
		}

		return worldX;
	}

	private void refreshTargetMarkers()
	{
		int[] markerXs = new int[targetXs.size()];
		int[] markerYs = new int[targetYs.size()];

		for(int i=0; i<targetXs.size(); i++)
		{
			markerXs[i] = getBoardPixelXFromGame(targetXs.get(i).doubleValue());
			markerYs[i] = getBoardPixelYFromGame(targetYs.get(i).doubleValue());
		}

		plane.setTargetMarkers(markerXs, markerYs, targetXs.size());
	}

	private void undoTargetPoint()
	{
		if(targetXs.size() == 0)
		{
			targetStatusLabel.setText("No points");
			return;
		}

		int last = targetXs.size()-1;
		targetXs.remove(last);
		targetYs.remove(last);
		targetStatusLabel.setText("Undone");

		if(targetXs.size() > 0)
		{
			targetXField.setText(formatNumber(targetXs.get(targetXs.size()-1).doubleValue()));
			targetYField.setText(formatNumber(targetYs.get(targetYs.size()-1).doubleValue()));
		}
		else
		{
			targetXField.setText("");
			targetYField.setText("");
		}

		refreshTargetMarkers();
		refreshFunctionHelper();
	}

	private void resetTargetPoints()
	{
		targetXs.clear();
		targetYs.clear();
		targetXField.setText("");
		targetYField.setText("");
		targetStatusLabel.setText("Cleared");
		refreshTargetMarkers();
		refreshFunctionHelper();
	}

	private String makePolylineFunction(Player player)
	{
		Soldier soldier = player.getCurrentTurnSoldier();
		double soldierX = getGameXFromBoardPixel(soldier.getX());
		double soldierY = getGameYFromBoardPixel(soldier.getY());

		int numPoints = targetXs.size()+1;
		double[] xs = new double[numPoints];
		double[] ys = new double[numPoints];

		xs[0] = getFunctionX(player, soldierX);
		ys[0] = 0;

		for(int i=0; i<targetXs.size(); i++)
		{
			xs[i+1] = getFunctionX(player, targetXs.get(i).doubleValue());
			ys[i+1] = targetYs.get(i).doubleValue() - soldierY;
		}

		double[] slopes = new double[numPoints-1];
		for(int i=0; i<slopes.length; i++)
		{
			double dx = xs[i+1] - xs[i];
			if(Math.abs(dx) < 0.0001)
			{
				targetStatusLabel.setText("Vertical at point "+(i+1));
				return null;
			}
			else if(dx < 0)
			{
				targetStatusLabel.setText("Point "+(i+1)+" is behind");
				return null;
			}

			slopes[i] = (ys[i+1] - ys[i])/dx;
		}

		double intercept = ys[0] - slopes[0]*xs[0];
		StringBuilder function = new StringBuilder();
		function.append("(");
		function.append(formatNumber(slopes[0])).append("*x");

		if(Math.abs(intercept) >= 0.00005)
		{
			function.append(formatSignedNumber(intercept));
		}

		for(int i=1; i<slopes.length; i++)
		{
			double delta = slopes[i] - slopes[i-1];

			if(Math.abs(delta) < 0.00005)
			{
				continue;
			}

			String offset = formatSignedNumber(-xs[i]);
			function.append(formatSignedNumber(delta));
			function.append("*((x");
			function.append(offset);
			function.append("+abs(x");
			function.append(offset);
			function.append("))/2)");
		}

		function.append(")");

		return function.toString();
	}

	private void generateFunctionToTarget()
	{
		if(graphwar.getGameData().getGameMode() != Constants.NORMAL_FUNC)
		{
			targetStatusLabel.setText("Normal only");
			return;
		}

		Player player = graphwar.getGameData().getCurrentTurnPlayer();
		if(player.isLocalPlayer() == false || player instanceof ComputerPlayer || graphwar.getGameData().isDrawingFunction())
		{
			targetStatusLabel.setText("Not your turn");
			return;
		}

		if(targetXs.size() == 0)
		{
			targetStatusLabel.setText("No points");
			return;
		}

		String function = makePolylineFunction(player);
		if(function == null)
		{
			return;
		}

		try
		{
			@SuppressWarnings("unused")
			Function testFunction = new Function(function);
		}
		catch(MalformedFunction e)
		{
			targetStatusLabel.setText("Invalid");
			return;
		}

		funcField.setText(function);
		graphwar.getGameData().sendFunctionPreview(function);
		targetStatusLabel.setText("Generated");
	}
	
	public boolean isShowMessageVisible()
	{
		return showMessageVisible;
	}
	
	public boolean isQuitVisible()
	{
		return quitVisible;
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(this.quitVisible)
		{
			if(arg0.getSource()==this.noQuit)
			{
				this.showQuit(false);
				plane.refreshBackground();
				this.repaint();
			}
			else if(arg0.getSource()==this.yesQuit)
			{
				graphwar.getGameData().disconnect();
				graphwar.getGlobalClient().closeRoom();
				this.showQuit(false);
			}
		}
		else if(showMessageVisible)
		{
			if(arg0.getSource()==okButton)
			{
				((GlobalScreen)graphwar.getUI().getScreen(Constants.GLOBAL_ROOM_SCREEN)).refreshGameButton();
				((PreGameScreen)graphwar.getUI().getScreen(Constants.PRE_GAME_SCREEN)).restartScreen();
				
				
				if(graphwar.getGlobalClient().isRunning())
				{
					graphwar.getUI().setScreen(Constants.GLOBAL_ROOM_SCREEN);
				}
				else
				{
					graphwar.getUI().setScreen(Constants.MAIN_MENU_SCREEN);
				}
				
				graphwar.finishGame();
				
				this.showShowMessage(false);
			}
		}
		else 
		{
			if(arg0.getSource()==quit)
			{
				this.showQuit(true);
				this.repaint();
			}
			else if(arg0.getSource()==chatField)
			{
				String text = chatField.getText();
				if(text.isEmpty()==false)
				{
					graphwar.getGameData().sendChatMessage(text);
					chatField.setText("");
				}
			}
			else if(arg0.getSource()==fire || arg0.getSource()==funcField)
			{
				if((graphwar.getGameData().getCurrentTurnPlayer() instanceof ComputerPlayer)==false)
				{				
					String function = funcField.getText();
					
					if(function.length() > 0)
					{
						graphwar.getGameData().sendFunction(function);
					}
				}
			}
			else if(arg0.getSource()==targetGenerateButton)
			{
				generateFunctionToTarget();
			}
			else if(arg0.getSource()==targetUndoButton)
			{
				undoTargetPoint();
			}
			else if(arg0.getSource()==targetResetButton)
			{
				resetTargetPoints();
			}
			else if(arg0.getSource()==targetXField || arg0.getSource()==targetYField)
			{
				addTargetPointFromFields();
			}
			else if(arg0.getSource()==global)
			{
				graphwar.getUI().setScreen(Constants.GLOBAL_ROOM_SCREEN);
			}
		}
	}

	public void updateFunction(String function)
	{
		funcField.setText(function);
	}
	
	public void startDrawingFunction()
	{
		this.plane.startDrawingFunction();		
	}
	
	public void repaintAngle()
	{
		this.angleDisplay.repaint();
	}
	
	private void showFuncType()
	{
		switch(graphwar.getGameData().getGameMode())
		{
			case Constants.NORMAL_FUNC:
			{
				// I now officially hate swing				
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						dyImg.setVisible(false);
						ddyImg.setVisible(false);
							
						yImg.setVisible(true);
						yImg.repaint();
					}
				}
				);
				
			}break;
			
			case Constants.FST_ODE:
			{
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						yImg.setVisible(false);
						ddyImg.setVisible(false);
						
						dyImg.setVisible(true);
						dyImg.repaint();
					}
				}
				);						
				
			}break;
			
			case Constants.SND_ODE:
			{
				
				SwingUtilities.invokeLater(
				new Runnable() 
				{
					public void run()
					{
						yImg.setVisible(false);
						dyImg.setVisible(false);
						
						ddyImg.setVisible(true);
						ddyImg.repaint();
					}
				}
				);	
			}break;
		}
	}

	private void showFunctionHelperWindow()
	{
		SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run()
			{
				refreshFunctionHelper();
				functionHelperWindow.setVisible(true);
			}
		}
		);
	}

	private void hideFunctionHelperWindow()
	{
		SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run()
			{
				functionHelperWindow.setVisible(false);
			}
		}
		);
	}

	public void startPanel() 
	{
		showFuncType();
		showFunctionHelperWindow();
		
		this.plane.startAnimating();
		this.timer.startRunning();
	}

	public void stopPanel() 
	{
		hideFunctionHelperWindow();
		this.plane.stopAnimating();
		this.timer.stopRunning();
	}
	
	public void keyPressed(KeyEvent e) 
	{		
		if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z)
		{
			undoTargetPoint();
			return;
		}

		if(graphwar.getGameData().getGameMode() == Constants.SND_ODE)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				graphwar.getGameData().angleUp();
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				graphwar.getGameData().angleDown();
			}
		}

		String function = funcField.getText();
		graphwar.getGameData().sendFunctionPreview(function);
	}

	public void keyReleased(KeyEvent e) 
	{		
		if(graphwar.getGameData().getGameMode() == Constants.SND_ODE)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				graphwar.getGameData().stopAngle();
			}
		}
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		if(arg0.getSource() == plane && SwingUtilities.isLeftMouseButton(arg0))
		{
			selectTargetFromPlane(arg0.getX(), arg0.getY());
		}

		this.requestFocus();
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

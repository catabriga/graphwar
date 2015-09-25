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

package GraphServer;

import java.awt.Color;
import java.awt.Font;

public class Constants 
{
	public static final Color BACKGROUND = new Color(158,215,155);
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public static final int FIELDS_HEIGHT = 25;
	
	public static final int TIMEOUT_CONNECTING = 10000;
	public static final int TIMEOUT_KEEPALIVE = 5000;
	public static final int TIMEOUT_DROP = 30000;
	
	public static String GLOBAL_IP = "www.graphwar.com";
	public static final int DEFAULT_PORT = 6112;
	public static final int GLOBAL_PORT = 23761;
	public static final int PUBLIC_ROOM_PORT = 28842;
	
	public static final String DUMMY_NAME = "23E(S_%24%40)!Xc";
	//public static final String ENCODED_DUMMY_NAME = "%23E(S_%24%40)!Xc";
	
	public static final int START_GAME_DELAY = 5000;
	
	public static final int TURN_TIME = 60000;
	
	public static final int MAX_PLAYERS = 10;
	public static final int MAX_SOLDIERS_PER_PLAYER = 4;
	public static final int MAX_CLIENTS = 10;
	public static final int INITIAL_NUM_SOLDIERS = 2;
	
	public static final int MAXIMUM_COLOR_MODULE_SQUARED = 3*160*160;
	
	public static final int TEAM1 = 1;
	public static final int TEAM2 = 2;
	
	public static final int NORMAL_FUNC = 0;
	public static final int FST_ODE = 1;
	public static final int SND_ODE = 2;
	
	public static final int PLANE_LENGTH = 770;
	public static final int PLANE_HEIGHT = 450;
	public static final int PLANE_GAME_LENGTH = 50;
	
	public static final int CIRCLE_MEAN_RADIUS = 40;
	public static final int CIRCLE_STANDARD_DEVIATION = 25;
	public static final int NUM_CIRCLES_MEAN_VALUE = 15;
	public static final int NUM_CIRCLES_STANDARD_DEVIATION = 7;
	
	public static final int SOLDIER_RADIUS = 7;
	public static final int SOLDIER_SELECTION_RADIUS = 15;
	public static final int SOLDIER_ANIMATION_DELAY_STANDARD_DEVIATION = 5000;
	public static final int SOLDIER_ANIMATION_MEAN_VALUE = 3000;
	public static final int SOLDIER_MAX_DEATH_TIME = 6000;
	public static final int NAME_FADE_TIME = 1000;
	public static final Font NAME_FONT = new Font("Sans", Font.PLAIN, 14);
	
	public static final int EXPLOSION_RADIUS = 12;
	
	public static final int FUNCTION_VELOCITY = 1500;	//steps per second
	public static final int FUNC_FADE_TIME = 1000;
	public static final int NEXT_TURN_DELAY = 3000;	// Delay after functions hits target to change turn
	
	public static final int FUNC_MAX_STEPS = 20000;
	public static final double FUNC_MAX_STEP_DISTANCE_SQUARED = 0.001;
	public static final double FUNC_MIN_X_STEP_DISTANCE = 0.00001;
	public static final double STEP_SIZE = 0.01;	
	public static final double ANGLE_STEP_MIN = Math.PI/(20*360);
	public static final double ANGLE_STEP_MAX = 0.03;
	public static final double ANGLE_FACTOR = 1.2;
	public static final double ANGLE_ERROR = Math.PI/360;
	public static final int MAX_ANGLE_LOOPS = 100;
	
	public static final int RANDOM_TREE_MAX_RAND = 1000;
	public static final int RANDOM_TREE_VALUE_VARIABLE_CHANCE = 900;
	public static final int RANDOM_TREE_ONE_PARAMETER_CHANCE = 500;
	public static final int MAX_AI_RANDOM_DOUBLE = 100;
	public static final int NODE_MUTATION_CHANCE = 750;
	public static final int MAX_AI_TIME = 2;

	public static final double ANGLE_ACCELERATION = 0.000003;
	
	public static final int NUM_FUNCTIONS_AI = 50;
	public static final int NUM_FUNCTIONS_UNCHANGED_TURN_AI = 5;
	public static final int NUM_FUNCTION_MUTATED_AI = 25;
	public static final int RANDOM_FUNC_MEAN_LENGTH = 10;
	public static final double RANDOM_FUNC_VALUE_CHANCE = 0.5;
	public static final double RANDOM_FUNC_VARIABLE_CHANCE = 0.5;
	public static final double RANDOM_FUNC_VALUE_MEAN = 10.2;
	public static final int RANDOM_FUNC_MEAN_CROSSOVER_LENGTH = 5;
	public static final int RANDOM_FUNC_MEAN_MUTATION_LENGTH = 5;
	
	public static final String[] computerNames = {"Deep Thought", "HAL", "Skynet", "Agent Smith", "Multivac", "Deep Blue", "Cleverbot", "Alpha Zordon", "Wolfram"};
	public static final int COMPUTER_LEVEL_STANDARD_DEVIATION = 40;
	public static final int COMPUTER_LEVEL_MEAN_VALUE = 50;
	public static final int COMPUTER_LEVEL_MIN_VALUE = 10;
	
	/////// Game States /////////
	
	public static final int NONE = 0;
	public static final int PRE_GAME = 1;
	public static final int GAME = 2;
	
	///////// Screens //////////////
	
	public static final int MAIN_MENU_SCREEN = 0;
	public static final int PRE_GAME_SCREEN = 1;
	public static final int GLOBAL_ROOM_SCREEN = 2;
	public static final int GAME_SCREEN = 3;
	public static final int NUM_SCREENS = 4;
}

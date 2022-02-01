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

public class NetworkProtocol
{
	public static final int NO_INFO = 10;
	public static final int ALL_INFO = 11;
	public static final int SET_NAME = 12;
	public static final int CHANGE_MODE = 13;
	public static final int CHAT_MSG = 14;
	public static final int CLOSE_CONNECTION = 15;
	public static final int ADD_PLAYER = 16;
	public static final int ADD_SOLDIER = 17;
	public static final int SET_SOLDIER = 18;
	public static final int REMOVE_SOLDIER = 19;
	public static final int SET_TEAM = 20;
	public static final int SET_READY = 21;	
	public static final int START_GAME = 22;
	public static final int CHANGE_GAME_TYPE = 23;
	public static final int FIRE_FUNC = 24;
	public static final int NEXT_TURN = 25;
	public static final int SEND_FUNC = 26;
	public static final int READY_NEXT_TURN = 27;
	public static final int SET_ANGLE = 28;
	public static final int REMOVE_PLAYER = 29;
	public static final int END_GAME = 30;
	public static final int NEXT_MODE = 31;
	public static final int PREVIOUS_MODE = 32;
	public static final int SET_MODE = 33;
	public static final int CHANGE_ANGLE = 34;
	public static final int KILL_PLAYER = 35;
	public static final int CONNECTION_ACCEPTED = 36;
	public static final int TIME_UP = 37;
	public static final int GAME_FULL = 38;
	public static final int DISCONNECT = 39;
	public static final int GAME_FINISHED = 40;
	public static final int NEW_LEADER = 41;
	public static final int START_COUNTDOWN = 42;
	public static final int REORDER = 43;
	public static final int FUNCTION_PREVIEW = 44;
	
	public static final int JOIN = 101;
	public static final int SAY_CHAT = 102;
	public static final int LIST_PLAYERS = 103;
	public static final int LIST_ROOMS = 104;
	public static final int ROOM_STATUS = 105;
	public static final int QUIT = 106;
	public static final int CLOSE_ROOM = 107;
	public static final int CREATE_ROOM = 108;
	public static final int ROOM_INVALID = 109;
	
}	

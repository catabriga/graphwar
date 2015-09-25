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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection
{
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private long lastReceivedTime;
	private long lastSentTime;
	
	public Connection(String ip, int port) throws IOException
	{		
		SocketAddress sockaddr = new InetSocketAddress(ip, port);
		socket = new Socket();
		
		socket.connect(sockaddr, Constants.TIMEOUT_CONNECTING);
		
		socket.setSoTimeout(Constants.TIMEOUT_KEEPALIVE);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		
		
		lastReceivedTime = System.currentTimeMillis();
		lastSentTime = System.currentTimeMillis();
				
	}
		
	public Connection(Socket socket) throws IOException
	{
		this.socket = socket;
		
		socket.setSoTimeout(Constants.TIMEOUT_KEEPALIVE);
		
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
	}
	
	public void close() throws IOException
	{
		out.close();
		in.close();
		
		socket.close();
	}
	
	public String getIpAddress()
	{
		return socket.getInetAddress().getHostAddress();
	}
	
	public long getLastSentTime()
	{
		return this.lastSentTime;
	}
	
	public long getLastReceivedTime()
	{
		return this.lastReceivedTime;
	}
	
	public void sendMessage(String message)
	{
		out.println(message);
		lastSentTime = System.currentTimeMillis();
		//System.out.println("Message sent: "+message);
	}
	
	public String readMessage() throws IOException
	{		
		String line = null;
		
		line = in.readLine();
		lastReceivedTime = System.currentTimeMillis();
		//System.out.println("Message received: "+line);
		
		return line;
	}
}

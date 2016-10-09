/*
 * ao-messaging - Asynchronous bidirectional messaging over various protocols.
 * Copyright (C) 2014, 2015, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-messaging.
 *
 * ao-messaging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-messaging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-messaging.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.messaging.tcp.server;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.messaging.base.AbstractSocketContext;
import com.aoindustries.messaging.tcp.TcpSocket;
import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import com.aoindustries.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Client component for bi-directional messaging over TCP.
 */
public class TcpSocketServer extends AbstractSocketContext<TcpSocket> {

	private static final boolean KEEPALIVE = true;

	private static final boolean SOCKET_SO_LINGER_ENABLED = true;
	private static final int SOCKET_SO_LINGER_SECONDS = 15;

	private static final boolean TCP_NO_DELAY = true;

	private final Executors executors = new Executors();

	private final int port;
	private final int backlog;
	private final InetAddress bindAddr;

	private final Object lock = new Object();
	private ServerSocket serverSocket;

	public TcpSocketServer(int port) {
		this(port, 50, null);
	}

	public TcpSocketServer(int port, int backlog) {
		this(port, backlog, null);
	}

	public TcpSocketServer(int port, int backlog, InetAddress bindAddr) {
		this.port = port;
		this.backlog = backlog;
		this.bindAddr = bindAddr;
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			executors.dispose();
		}
	}

	/**
	 * Starts the I/O of a socket server.  After creation, a socket server does
	 * not accept connections until started.  This allows listeners to be
	 * registered between creation and start call.
	 * 
	 * @throws IllegalStateException  if closed or already started
	 */
	public void start(
		final Callback<? super TcpSocketServer> onStart,
		final Callback<? super Exception> onError
	) throws IllegalStateException {
		if(isClosed()) throw new IllegalStateException("TcpSocketServer is closed");
		synchronized(lock) {
			if(serverSocket != null) throw new IllegalStateException();
			executors.getUnbounded().submit(
				new Runnable() {
					@Override
					public void run() {
						try {
							if(isClosed()) throw new SocketException("TcpSocketServer is closed");
							final ServerSocket newServerSocket = new ServerSocket(port, backlog, bindAddr);
							synchronized(lock) {
								TcpSocketServer.this.serverSocket = newServerSocket;
							}
							// Handle incoming messages in a Thread, can try nio later
							executors.getUnbounded().submit(
								new Runnable() {
									@Override
									public void run() {
										try {
											while(true) {
												synchronized(lock) {
													// Check if closed
													if(newServerSocket!=TcpSocketServer.this.serverSocket) break;
												}
												Socket socket = newServerSocket.accept();
												long connectTime = System.currentTimeMillis();
												socket.setKeepAlive(KEEPALIVE);
												socket.setSoLinger(SOCKET_SO_LINGER_ENABLED, SOCKET_SO_LINGER_SECONDS);
												socket.setTcpNoDelay(TCP_NO_DELAY);
												CompressedDataInputStream in = new CompressedDataInputStream(socket.getInputStream());
												CompressedDataOutputStream out = new CompressedDataOutputStream(socket.getOutputStream());
												Identifier id = newIdentifier();
												out.writeLong(id.getHi());
												out.writeLong(id.getLo());
												out.flush();
												TcpSocket tcpSocket = new TcpSocket(
													TcpSocketServer.this,
													id,
													connectTime,
													socket,
													in,
													out
												);
												addSocket(tcpSocket);
											}
										} catch(Exception exc) {
											if(!isClosed()) callOnError(exc);
										} finally {
											close();
										}
									}
								}
							);
							if(onStart!=null) onStart.call(TcpSocketServer.this);
						} catch(Exception exc) {
							if(onError!=null) onError.call(exc);
						}
					}
				}
			);
		}
	}
}

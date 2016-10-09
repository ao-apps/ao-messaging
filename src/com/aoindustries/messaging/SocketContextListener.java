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
package com.aoindustries.messaging;

/**
 * Receives notification on context events.
 * <p>
 * None of the messages will be triggered concurrently on this listener;
 * however, different listeners may be notified in parallel.
 * </p>
 */
public interface SocketContextListener {

	/**
	 * Called when a new socket is created, but before it is started.
	 * This may be used to register a listener on the socket without
	 * missing any messages.
	 */
	void onNewSocket(SocketContext socketContext, Socket newSocket);

	/**
	 * Called when an error occurs.  The socket is closed
	 * after the first error.
	 */
	void onError(SocketContext socketContext, Exception exc);

	/**
	 * Called when a socket context is closed.
	 * This will only be called once.
	 */
	void onSocketContextClose(SocketContext socketContext);
}

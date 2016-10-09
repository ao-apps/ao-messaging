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

import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import com.aoindustries.util.concurrent.ConcurrentListenerManager;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;

/**
 * One established connection.
 */
public interface Socket extends Closeable {

	/**
	 * Gets the context this socket is running within.
	 */
	SocketContext getSocketContext();

	/**
	 * Gets this socket's unique identifier.  This identifier should remain secret
	 * as compromising an identifier may allow hijacking a connection.
	 * No two sockets will have the same identifier for a given context at the same time.
	 */
	Identifier getId();

	/**
	 * Gets the time this connection was established.
	 */
	long getConnectTime();

	/**
	 * Gets the time this connection closed or null if still connected.
	 */
	Long getCloseTime();

	/**
	 * Gets the protocol, such as "http" or "tcp".
	 */
	String getProtocol();

	/**
	 * Gets the remote address at connection time.  This value will not change.
	 */
	SocketAddress getConnectRemoteSocketAddress();

	/**
	 * Gets the most recently seen remote address.  This value may change.
	 */
	SocketAddress getRemoteSocketAddress();

	/**
	 * Starts the I/O of a socket.  After connection, a socket does not send
	 * I/O events until started.  This allows listeners to be registered between
	 * connect and start calls.
	 * 
	 * @throws IllegalStateException  if closed or already started
	 */
	void start(
		Callback<? super Socket> onStart,
		Callback<? super Exception> onError
	) throws IllegalStateException;

	/**
	 * Closing a socket also removes it from its context.
	 */
	@Override
	void close() throws IOException;

	boolean isClosed();

	/**
	 * @see  ConcurrentListenerManager#addListener(java.lang.Object, boolean)
	 */
	void addSocketListener(SocketListener listener, boolean synchronous) throws IllegalStateException;

	/**
	 * @see  ConcurrentListenerManager#removeListener(java.lang.Object)
	 */
	boolean removeSocketListener(SocketListener listener);

	/**
	 * Sends a single message.  This will never block.
	 *
	 * @throws  IllegalStateException  if this socket is closed
	 */
	void sendMessage(Message message) throws IllegalStateException;

	/**
	 * Sends a set of messages.  This will never block.
	 * If messages is empty, the request is ignored.
	 *
	 * @throws  IllegalStateException  if this socket is closed
	 */
	void sendMessages(Collection<? extends Message> messages) throws IllegalStateException;
}

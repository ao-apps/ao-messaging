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

import java.net.SocketAddress;
import java.util.List;

/**
 * Receives messages as they come in from the sockets.
 * Also notified on other important socket events.
 * <p>
 * None of the messages will be triggered concurrently on this listener;
 * however, different listeners may be notified in parallel.
 * This means, for example, that onClose will not happen while onMessages is being invoked.
 * </p>
 * <p>
 * The given socket will always represent the current state, while the events are
 * delivered in-order.  Thus, newRemoteSocketAddress may not necessarily be the
 * same as the HttpSocket.getMostRecentRemoteSocketAddress.
 * </p>
 */
public interface SocketListener {

	/**
	 * Called when one or more new messages arrive.
	 * Messages are always delivered in-order.
	 * At least one message will be provided.
	 * Subsequent messages will not be sent until this onMessage completes.
	 * 
	 * @param  messages  The unmodifiable list of messages in the order received
	 */
	void onMessages(Socket socket, List<? extends Message> messages);

	/**
	 * Called when an error occurs.  The socket is closed
	 * after the first error.
	 */
	void onError(Socket socket, Exception exc);

	/**
	 * Called when a new remote address is seen.
	 */
	void onRemoteSocketAddressChange(
		Socket socket,
		SocketAddress oldRemoteSocketAddress,
		SocketAddress newRemoteSocketAddress
	);

	/**
	 * Called when a socket is closed.
	 * This will only be called once.
	 */
	void onSocketClose(Socket socket);
}

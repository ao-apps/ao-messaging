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

import java.io.Closeable;
import java.io.IOException;

/**
 * Any type of encapsulated message.
 */
public interface Message extends Closeable {

	/**
	 * Two messages of the same type with the same body must be considered equal.
	 */
	@Override
	boolean equals(Object o);

	/**
	 * The hash code must be consistent with equals.
	 */
	@Override
	int hashCode();

	/**
	 * Gets the message type.
	 */
	MessageType getMessageType();

	/**
	 * Gets a String representation of this message.
	 */
	String encodeAsString() throws IOException;

	/**
	 * Gets a binary representation of this message.
	 */
	ByteArray encodeAsByteArray() throws IOException;

	/**
	 * The message should be closed when it is no longer needed.
	 */
	@Override
	void close() throws IOException;
}

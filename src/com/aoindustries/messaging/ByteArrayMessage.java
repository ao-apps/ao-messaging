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

import com.aoindustries.util.Base64Coder;

/**
 * A message that is a byte[].
 */
public class ByteArrayMessage implements Message {

	public static final ByteArrayMessage EMPTY_BYTE_ARRAY_MESSAGE = new ByteArrayMessage(ByteArray.EMPTY_BYTE_ARRAY);

	/**
	 * base-64 decodes the message.
	 */
	public static ByteArrayMessage decode(String encodedMessage) {
		if(encodedMessage.isEmpty()) return EMPTY_BYTE_ARRAY_MESSAGE;

		return new ByteArrayMessage(Base64Coder.decode(encodedMessage));
	}

	private final ByteArray message;

	public ByteArrayMessage(byte[] message) {
		this(new ByteArray(message));
	}

	public ByteArrayMessage(ByteArray message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ByteArrayMessage(" + message.size + ")";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof ByteArrayMessage)) return false;
		ByteArrayMessage other = (ByteArrayMessage)o;
		return message.equals(other.message);
	}

	@Override
	public int hashCode() {
		return message.hashCode();
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.BYTE_ARRAY;
	}

	/**
	 * base-64 encodes the message.
	 */
	@Override
	public String encodeAsString() {
		if(message.size == 0) return "";

		return new String(Base64Coder.encode(message.array, message.size));
	}

	@Override
	public ByteArray encodeAsByteArray() {
		return message;
	}

	@Override
	public void close() {
		// Nothing to do
	}

	public ByteArray getMessage() {
		return message;
	}
}

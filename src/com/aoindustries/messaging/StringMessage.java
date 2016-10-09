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

import com.aoindustries.nio.charset.Charsets;
import java.nio.charset.Charset;

/**
 * A message that is a String.
 */
public class StringMessage implements Message {

	public static final StringMessage EMPTY_STRING_MESSAGE = new StringMessage("");

	private static final Charset CHARSET = Charsets.UTF_8;

	/**
	 * UTF-8 decodes the message.
	 */
	public static StringMessage decode(ByteArray encodedMessage) {
		if(encodedMessage.size == 0) return EMPTY_STRING_MESSAGE;

		return new StringMessage(new String(encodedMessage.array, 0, encodedMessage.size, CHARSET));
	}

	private final String message;

	public StringMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		if(message.length() > 21) {
			return "StringMessage(\"" + message.substring(0, 20) + "…\")";
		} else {
			return "StringMessage(\"" + message + "\")";
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof StringMessage)) return false;
		StringMessage other = (StringMessage)o;
		return message.equals(other.message);
	}

	@Override
	public int hashCode() {
		return message.hashCode();
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.STRING;
	}

	@Override
	public String encodeAsString() {
		return message;
	}

	/**
	 * UTF-8 encodes the message.
	 */
	@Override
	public ByteArray encodeAsByteArray() {
		if(message.isEmpty()) return ByteArray.EMPTY_BYTE_ARRAY;

		return new ByteArray(message.getBytes(CHARSET));
	}

	@Override
	public void close() {
		// Nothing to do
	}

	public String getMessage() {
		return message;
	}
}

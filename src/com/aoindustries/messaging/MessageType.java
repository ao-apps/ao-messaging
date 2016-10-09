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

import java.io.IOException;

/**
 * The types of serializations.
 */
public enum MessageType {

	BYTE_ARRAY {
		@Override
		public byte getTypeByte() {
			return 0;
		}

		@Override
		public char getTypeChar() {
			return 'b';
		}

		@Override
		public ByteArrayMessage decode(String encodedMessage) {
			return ByteArrayMessage.decode(encodedMessage);
		}

		@Override
		public ByteArrayMessage decode(ByteArray encodedMessage) {
			return new ByteArrayMessage(encodedMessage);
		}
	},
	FILE {
		@Override
		public byte getTypeByte() {
			return 1;
		}

		@Override
		public char getTypeChar() {
			return 'f';
		}

		@Override
		public FileMessage decode(String encodedMessage) throws IOException {
			return FileMessage.decode(encodedMessage);
		}

		@Override
		public FileMessage decode(ByteArray encodedMessage) throws IOException {
			return FileMessage.decode(encodedMessage);
		}
	},
	STRING {
		@Override
		public byte getTypeByte() {
			return 2;
		}

		@Override
		public char getTypeChar() {
			return 's';
		}

		@Override
		public StringMessage decode(String encodedMessage) {
			return new StringMessage(encodedMessage);
		}

		@Override
		public StringMessage decode(ByteArray encodedMessage) {
			return StringMessage.decode(encodedMessage);
		}
	},
	MULTI {
		@Override
		public byte getTypeByte() {
			return 3;
		}

		@Override
		public char getTypeChar() {
			return 'm';
		}

		@Override
		public MultiMessage decode(String encodedMessage) throws IOException {
			return MultiMessage.decode(encodedMessage);
		}

		@Override
		public MultiMessage decode(ByteArray encodedMessage) throws IOException {
			return MultiMessage.decode(encodedMessage);
		}
	};

	public static MessageType getFromTypeByte(byte typeByte) {
		switch(typeByte) {
			case 0 : return BYTE_ARRAY;
			case 1 : return FILE;
			case 2 : return STRING;
			case 3 : return MULTI;
			default : throw new IllegalArgumentException("Invalid type byte: " + typeByte);
		}
	}

	public static MessageType getFromTypeChar(char typeChar) {
		switch(typeChar) {
			case 'b' : return BYTE_ARRAY;
			case 'f' : return FILE;
			case 's' : return STRING;
			case 'm' : return MULTI;
			default : throw new IllegalArgumentException("Invalid type char: " + typeChar);
		}
	}

	abstract public byte getTypeByte();

	abstract public char getTypeChar();

	/**
	 * Constructs a message of this type from its string encoding.
	 */
	abstract public Message decode(String encodedMessage) throws IOException;

	/**
	 * Constructs a message of this type from its byte array encoding.
	 */
	abstract public Message decode(ByteArray encodedMessage) throws IOException;
}

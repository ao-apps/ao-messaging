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

import com.aoindustries.io.AoByteArrayInputStream;
import com.aoindustries.io.AoByteArrayOutputStream;
import com.aoindustries.util.AoCollections;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * A message that is a combination of multiple messages.
 */
public class MultiMessage implements Message {

	private static final char DELIMITER = ',';

	public static final MultiMessage EMPTY_MULTI_MESSAGE;
	static {
		Collection<? extends Message> empty = Collections.emptyList();
		EMPTY_MULTI_MESSAGE = new MultiMessage(empty);
	}

	/**
	 * Decodes the messages.
	 */
	public static MultiMessage decode(String encodedMessages) throws IOException {
		if(encodedMessages.isEmpty()) return EMPTY_MULTI_MESSAGE;

		int pos = encodedMessages.indexOf(DELIMITER);
		if(pos == -1) throw new IllegalArgumentException("Delimiter not found");
		final int size = Integer.parseInt(encodedMessages.substring(0, pos++));
		List<Message> decodedMessages = new ArrayList<Message>(size);
		for(int i=0; i<size; i++) {
			MessageType type = MessageType.getFromTypeChar(encodedMessages.charAt(pos++));
			int nextPos = encodedMessages.indexOf(DELIMITER, pos);
			if(nextPos == -1) throw new IllegalArgumentException("Delimiter not found");
			final int capacity = Integer.parseInt(encodedMessages.substring(pos, nextPos++));
			pos = nextPos + capacity;
			decodedMessages.add(type.decode(encodedMessages.substring(nextPos, pos)));
		}
		if(pos != encodedMessages.length()) throw new IllegalArgumentException("pos != encodedMessages.length()");
		return new MultiMessage(Collections.unmodifiableList(decodedMessages));
	}

	/**
	 * Decodes the messages.
	 */
	public static MultiMessage decode(ByteArray encodedMessages) throws IOException {
		if(encodedMessages.size == 0) return EMPTY_MULTI_MESSAGE;

		DataInputStream in = new DataInputStream(new AoByteArrayInputStream(encodedMessages.array));
		try {
			int totalRead = 0;
			final int size = in.readInt();
			totalRead += 4;
			List<Message> decodedMessages = new ArrayList<Message>(size);
			for(int i=0; i<size; i++) {
				MessageType type = MessageType.getFromTypeByte(in.readByte());
				totalRead++;
				final int capacity = in.readInt();
				totalRead += 4;
				byte[] encodedMessage = new byte[capacity];
				in.readFully(encodedMessage, 0, capacity);
				totalRead += capacity;
				decodedMessages.add(type.decode(new ByteArray(encodedMessage, capacity)));
			}
			if(totalRead != encodedMessages.size) throw new IllegalArgumentException("totalRead != encodedMessages.size");
			return new MultiMessage(Collections.unmodifiableList(decodedMessages));
		} finally {
			in.close();
		}
	}

	private final Collection<? extends Message> messages;

	public MultiMessage(Collection<? extends Message> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "MultiMessage(" + messages.size() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof MultiMessage)) return false;
		MultiMessage other = (MultiMessage)o;
		return AoCollections.equals(messages, other.messages);
	}

	@Override
	public int hashCode() {
		return AoCollections.hashCode(messages);
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.MULTI;
	}

	/**
	 * Encodes the messages into a single string.
	 */
	@Override
	public String encodeAsString() throws IOException {
		final int size = messages.size();
		if(size == 0) return "";

		StringBuilder sb = new StringBuilder();
		sb.append(size).append(DELIMITER);
		int count = 0;
		for(Message message : messages) {
			count++;
			String str = message.encodeAsString();
			sb
				.append(message.getMessageType().getTypeChar())
				.append(str.length())
				.append(DELIMITER)
				.append(str)
			;
		}
		if(count != size) throw new ConcurrentModificationException();
		return sb.toString();
	}

	/**
	 * Encodes the messages into a single ByteArray.
	 * There is likely a more efficient implementation that reads-through, but this
	 * is a simple implementation.
	 */
	@Override
	public ByteArray encodeAsByteArray() throws IOException {
		final int size = messages.size();
		if(size == 0) return ByteArray.EMPTY_BYTE_ARRAY;

		AoByteArrayOutputStream bout = new AoByteArrayOutputStream();
		try {
			DataOutputStream out = new DataOutputStream(bout);
			try {
				out.writeInt(size);
				int count = 0;
				for(Message message : messages) {
					count++;
					ByteArray byteArray = message.encodeAsByteArray();
					final int capacity = byteArray.size;
					out.writeByte(message.getMessageType().getTypeByte());
					out.writeInt(capacity);
					out.write(byteArray.array, 0, capacity);
				}
				if(count != size) throw new ConcurrentModificationException();
			} finally {
				out.close();
			}
		} finally {
			bout.close();
		}
		return new ByteArray(bout.getInternalByteArray(), bout.size());
	}

	/**
	 * Closes each of the underlying messages.
	 */
	@Override
	public void close() throws IOException {
		for(Message message : messages) {
			message.close();
		}
	}

	public Collection<? extends Message> getMessages() {
		return messages;
	}
}

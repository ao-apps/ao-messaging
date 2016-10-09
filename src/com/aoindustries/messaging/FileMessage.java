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

import com.aoindustries.io.AoByteArrayOutputStream;
import com.aoindustries.io.FileUtils;
import com.aoindustries.io.IoUtils;
import com.aoindustries.util.Base64Coder;
import com.aoindustries.util.WrappedException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A message that is a file.
 */
public class FileMessage implements Message {

	/**
	 * base-64 decodes the message into a temp file.
	 */
	public static FileMessage decode(String encodedMessage) throws IOException {
		return decode(
			encodedMessage.isEmpty()
			? ByteArray.EMPTY_BYTE_ARRAY
			: new ByteArray(
				Base64Coder.decode(
					encodedMessage
				)
			)
		);
	}

	/**
	 * Restores this message into a temp file.
	 */
	public static FileMessage decode(ByteArray encodedMessage) throws IOException {
		File file = File.createTempFile("FileMessage.", null);
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(encodedMessage.array, 0, encodedMessage.size);
		} finally {
			out.close();
		}
		return new FileMessage(true, file);
	}

	private final boolean isTemp;
	private final Object lock = new Object();
	private File file;

	public FileMessage(File file) {
		this(false, file);
	}

	private FileMessage(boolean isTemp, File file) {
		this.isTemp = isTemp;
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileMessage(\"" + file.getPath() + "\")";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof FileMessage)) return false;
		FileMessage other = (FileMessage)o;
		try {
			return FileUtils.contentEquals(file, other.file);
		} catch(IOException e) {
			throw new WrappedException(e);
		}
	}

	@Override
	public int hashCode() {
		try {
			return FileUtils.contentHashCode(file);
		} catch(IOException e) {
			throw new WrappedException(e);
		}
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.FILE;
	}

	/**
	 * base-64 encodes the message.
	 */
	@Override
	public String encodeAsString() throws IOException {
		ByteArray byteArray = encodeAsByteArray();
		if(byteArray.size == 0) return "";
		return new String(Base64Coder.encode(byteArray.array, byteArray.size));
	}

	@Override
	public ByteArray encodeAsByteArray() throws IOException {
		long len = file.length();
		InputStream in = new FileInputStream(file);
		try {
			AoByteArrayOutputStream bout = new AoByteArrayOutputStream(len > 0 && len <= Integer.MAX_VALUE ? (int)len : 32);
			try {
				IoUtils.copy(in, bout);
			} finally {
				bout.close();
			}
			return new ByteArray(bout.getInternalByteArray(), bout.size());
		} finally {
			in.close();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized(lock) {
			if(isTemp && file != null) {
				FileUtils.delete(file);
				file = null;
			}
		}
	}

	public boolean isTemp() {
		return isTemp;
	}

	public File getMessage() {
		return file;
	}
}

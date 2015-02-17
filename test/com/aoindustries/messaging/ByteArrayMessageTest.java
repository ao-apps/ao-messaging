/*
 * ao-messaging - Asynchronous bidirectional messaging over various protocols.
 * Copyright (C) 2014, 2015  AO Industries, Inc.
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
import java.security.SecureRandom;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class ByteArrayMessageTest {

	public ByteArrayMessageTest() {
	}

	private static final Random random = new SecureRandom();

	@Test
	public void testEncodeAndDecode() throws IOException {
		for(int i=0; i<100; i++) {
			int len = random.nextInt(10000);
			byte[] bytes = new byte[len + random.nextInt(10)];
			random.nextBytes(bytes);

			ByteArrayMessage original = new ByteArrayMessage(bytes);
			try {
				// Encode to String
				String encodedString = original.encodeAsString();

				// Decode back to message
				ByteArrayMessage decoded = (ByteArrayMessage)MessageType.BYTE_ARRAY.decode(encodedString);
				try {
					assertEquals(original, decoded);
				} finally {
					decoded.close();
				}
			} finally {
				original.close();
			}
		}
	}
}

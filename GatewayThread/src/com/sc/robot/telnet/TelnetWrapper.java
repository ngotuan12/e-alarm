package com.sc.robot.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetWrapper extends TelnetClient {
	private int miInterval = 100;

	/**
	 * 
	 * @param iInterval
	 *            int
	 * @throws IOException
	 */
	public void setInterval(int iInterval) throws IOException {
		miInterval = iInterval;
	}

	/**
	 * Returns bytes available to be read. Since they haven't been negotiated
	 * over, this could be misleading...
	 * 
	 * @return int
	 * @throws IOException
	 */
	public int available() throws IOException {
		return getInputStream().available();
	}

	/**
	 * Returns a String from the telnet connection. Blocks until one is
	 * available. No guarantees that the string is in any way complete. NOTE:
	 * uses Java 1.0.2 style String-bytes conversion.
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String receive() throws IOException {
		if (!this.readerThread) {
			int j = this._socket_.getInputStream().available();
			if (j > 0) {
				byte[] bt = new byte[j];
				int iByteRead = this._socket_.getInputStream().read(bt);
				return new String(bt, 0, iByteRead);
			}
			return "";
		} else {
			byte[] bt = new byte[available()];
			int iByteRead = getInputStream().read(bt);
			return new String(bt, 0, iByteRead);
		}
	}

	/**
	 * Returns all data received up until a certain token.
	 * 
	 * @param token
	 *            String to wait for
	 * @exception IOException
	 *                on problems with the socket connection
	 * @see #wait
	 * @return String
	 * @throws IOException
	 */
	public String receiveUntil(String token) throws IOException {
		return new String(com.fss.util.StreamUtil.getDataTerminatedBySymbol(
				getInputStream(), token.getBytes()));
	}

	/**
	 * Returns all data received up until a certain token.
	 * 
	 * @param token
	 *            String to wait for
	 * @param timeout
	 *            time in milliseconds to wait (negative means wait forever)
	 * @exception IOException
	 *                on problems with the socket connection
	 * @exception TimedOutException
	 *                if time runs out before token received
	 * @see #wait(String, long)
	 * @return String
	 */
	/**
	 * DEPQ Dung ham nay bi loi trong truong hop nhu sau Neu Server khong tra ve
	 * token ma lien tuc tra ve du lieu thi vong while(available() <= 0){} khong
	 * bao gio vao trong duoc(da tung xay ra tai VNM) khi do vong
	 * while(buf.indexOf(token) == -1){} lien tuc chay cho den khi bat duoc
	 * token mac du da qua thoi gian timeout --> Co the dan den vong nay chay
	 * mai khong co dieu kien thoat khi Server lien tuc tra ve du lieu nhung
	 * khong co Token Khac phuc: Dung ham receiveUntilEx(token,timeout)
	 */
	public String receiveUntil(String token, long timeout) throws IOException {
		StringBuffer buf = new StringBuffer();
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		}
		do {
			if (timeout >= 0) {
				while (available() <= 0) {
					if (System.currentTimeMillis() > deadline) {
						throw new IOException("Wait '" + token + "' for "
								+ timeout + " miliseconds was timeout\r\n"
								+ buf.toString());
					}
					try {
						Thread.sleep(miInterval);
					} catch (InterruptedException ignored) {
					}
				}
			}
			buf.append(receive());
		} while (buf.indexOf(token) == -1);
		return buf.toString();
	}

	/**
	 * depq 2009: Nhan tra ve khi gui lenh telnet + Doi trong thoi gian timeout
	 * cho den khi tra ve token + Neu qua thoi gian timeout ma van khong tra ve
	 * token thi throw exception + Vi ham nay chi lay du lieu trong queue cu
	 * TelnetInputStrem -- >Neu this.setReaderThread(true) hoac khong set(mac
	 * dinh bang true) thi moi su dung duoc ham nay. Neu setReaderThread(false)
	 * thi j=available() luc nao cung=0, do khong co du lieu nao duoc tu dong
	 * nhan ve queue
	 * 
	 * @param token
	 *            String
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */
	public String receiveUntilEx(String token, long timeout) throws IOException {
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		String total = "";
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			// tiep tuc doi neu khong co du lieu va chua het gio timeout
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				try {
					Thread.sleep(miInterval);
				} catch (InterruptedException ex) {
				}
				j = available();
			}
			// lay du lieu neu stream ton tai du lieu va ngung lay khi het
			// timeout hoac tim thay token
			while (j > 0 && System.currentTimeMillis() < deadline) {
				total += receive(j);
				if (total.contains(token)) {
					break;
				}
				j = available();
			}
			if (total.contains(token)) {
				break;
			}
			if (j <= 0) {
				continue;
			}
		}
		if (!total.contains(token)) {
			throw new IOException("Wait for " + timeout
					+ " miliseconds was timeout\r\n" + total);
		}
		return total;
	}

	/**
	 * DEPQ:
	 * 
	 * @param token
	 *            String
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */
	public String receiveUntilStand(String token, long timeout)
			throws IOException {
		InputStream is = this.getInputStream(); // org.apache.commons.net.telnet.TelnetInputStream
		// ///////////////////////////////////////////////////////////////
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		StringBuffer buffer = new StringBuffer();
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			// tiep tuc doi neu khong co du lieu va chua het gio timeout
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				if (!this.readerThread) {
					int iValue = 0;
					long cost = 0;
					/**
					 * TelnetInputStream.read() khong chi doc mot ki tu. Neu
					 * InputStream van con available thi tiep tu doc cho den het
					 * sau do dua du lieu nay vao TelnetInputStream.queue va chi
					 * tra ve mot ki tu dau tien
					 */
					long start = System.currentTimeMillis();
					iValue = is.read();
					cost = System.currentTimeMillis() - start;
					if (iValue < 0) {
						if (cost >= timeout) {
							throw new IOException("Wait for " + timeout
									+ " miliseconds was timeout\r\n" + buffer);
						} else {
							throw new IOException("No data to read");
						}
					}
					buffer.append((char) iValue);
				} else {
					try {
						Thread.sleep(miInterval);
					} catch (InterruptedException ex) {
					}
				}
				j = available();
			}
			// lay du lieu neu stream ton tai du lieu va ngung lay khi het
			// timeout hoac tim thay token
			while (j > 0 && System.currentTimeMillis() < deadline) {
				buffer.append(receive(j));
				// DEPQ tam thoi them vao de doi pho tai VNM 26/07/2010
				if (buffer.lastIndexOf(token) > 0) { // &&
														// buffer.length()!=token.length()){
					break;
				}
				j = available();
			}
			if (buffer.lastIndexOf(token) > 0) { // &&
													// buffer.length()!=token.length()){
				break;
			}
			if (j <= 0) {
				continue;
			}
		}
		String result = buffer.toString();
		if (result.startsWith(token)) {
			result = result.substring(token.length());
		}
		if (result.indexOf(token) < 0) {
			throw new IOException("Wait for " + timeout
					+ " miliseconds was timeout\r\n" + buffer);
		}
		return result;
	}

	/**
	 * DEPQ 11/06/2011
	 * 
	 * @param token
	 *            String[]
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */

	public String[] receiveUntilStand(String[] token, long timeout)
			throws IOException {
		return receiveUntilStand(token, timeout, false);
	}

	/**
	 * DEPQ 11/06/2011
	 * 
	 * @param token
	 *            String[]
	 * @param timeout
	 *            long
	 * @param trimStartWith
	 *            boolean
	 * @return String
	 * @throws IOException
	 */
	public String[] receiveUntilStand(String[] token, long timeout,
			boolean trimStartWith) throws IOException {
		InputStream is = this.getInputStream(); // org.apache.commons.net.telnet.TelnetInputStream
		// ///////////////////////////////////////////////////////////////
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		if (token == null || token.length == 0) {
			throw new IOException("You must setup wait token");
		}

		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		String[] retnAString = new String[2];
		boolean foundToken = false;
		StringBuffer buffer = new StringBuffer();
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			// tiep tuc doi neu khong co du lieu va chua het gio timeout
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				if (!this.readerThread) {
					int iValue = 0;
					long cost = 0;
					/**
					 * TelnetInputStream.read() khong chi doc mot ki tu. Neu
					 * InputStream van con available thi tiep tu doc cho den het
					 * sau do dua du lieu nay vao TelnetInputStream.queue va chi
					 * tra ve mot ki tu dau tien
					 */
					long start = System.currentTimeMillis();
					iValue = is.read();
					cost = System.currentTimeMillis() - start;
					if (iValue < 0) {
						if (cost >= timeout) {
							throw new IOException("Wait for " + timeout
									+ " miliseconds was timeout\r\n" + buffer);
						} else {
							throw new IOException("No data to read");
						}
					}
					buffer.append((char) iValue);
				} else {
					try {
						Thread.sleep(miInterval);
					} catch (InterruptedException ex) {
					}
				}
				j = available();
			}
			// lay du lieu neu stream ton tai du lieu va ngung lay khi het
			// timeout hoac tim thay token
			while (j > 0 && System.currentTimeMillis() < deadline) {
				buffer.append(receive(j));
				// DEPQ tam thoi them vao de doi pho tai VNM 26/07/2010
				String check = checkLastIndexOfToken(buffer, token);
				if (check != null) {
					foundToken = true;
					retnAString[0] = check;
					retnAString[1] = buffer.toString();
					break;
				}
				j = available();
			}
			if (foundToken) { // && buffer.length()!=token.length()){
				break;
			} else {
				String check = checkLastIndexOfToken(buffer, token);
				if (check != null) {
					foundToken = true;
					retnAString[0] = check;
					retnAString[1] = buffer.toString();
					break;
				}
			}
		}
		if (trimStartWith) {
			retnAString[1] = trimStartWith(retnAString[1], retnAString[0]);
		}
		if (!foundToken) {
			retnAString[1] = buffer.toString();
			String strToken = "";
			for (int i = 0; i < token.length; i++) {
				strToken += token[i] + ",";
			}
			strToken = strToken.substring(0, strToken.length() - 1);
			throw new IOException("Wait [" + strToken + "] for " + timeout
					+ " miliseconds was timeout\r\n" + buffer);
		}
		return retnAString;
	}

	/**
	 * Sends a String to the remote host. NOTE: uses Java 1.0.2 style
	 * String-bytes conversion.
	 * 
	 * @exception IOException
	 *                on problems with the socket connection
	 * @param s
	 *            String
	 * @param bAutoFlush
	 *            boolean
	 * @throws IOException
	 */
	public void send(String s, boolean bAutoFlush) throws IOException {
		byte[] buf = s.getBytes();
		send(buf, bAutoFlush);
	}

	public void send(String s) throws IOException {
		send(s, true);
	}

	/**
	 * DEPQ thay doi nay 23/07/2010 tai VNM: check socket con ket noi ngon thi
	 * moi tiep tuc gui du lieu len telnet session Sends bytes over the telnet
	 * connection.
	 * 
	 * @param buf
	 *            byte[]
	 * @param bAutoFlush
	 *            boolean
	 * @throws IOException
	 */
	public void send(byte[] buf, boolean bAutoFlush) throws IOException {
		// DEPQ 23/07/2010
		if (this._socket_.isClosed() || !this._socket_.isConnected()
				|| !this._socket_.isBound()) {
			throw new SocketException("Connection to TelnetServer is closed");
		}
		getOutputStream().write(buf);
		if (bAutoFlush) {
			getOutputStream().flush();
		}
	}

	/**
	 * Connects to the default telnet port on the given host. If the
	 * defaultLogin and defaultPassword are non-null, attempts login.
	 * 
	 * @param host
	 *            String
	 * @throws IOException
	 */
	public TelnetWrapper(String host) throws IOException {
		connect(host);
		setSoLinger(true, 0);
	}

	/**
	 * Connects to a specific telnet port on the given host. If the defaultLogin
	 * and defaultPassword are non-null, attempts login.
	 * 
	 * @param host
	 *            String
	 * @param port
	 *            int
	 * @throws IOException
	 */
	public TelnetWrapper(String host, int port) throws IOException {
		this.connect(host, port);
		this.setSoLinger(true, 0);
	}

	/**
	 * DEPQ sua 23/07/2010 Neu Socket.setSoTimeout(timeout) va
	 * setReaderThread(true)<mac dinh> thi --> connect(host,port) -- >
	 * TelnetClient._connectAction_() --> TelnetInputStream.run()--> 564
	 * __read(true) --> TelnetInputStream se khoi tao tien trinh lien tuc doc
	 * InputStream.read() --> Neu qua timeout ma khong co du lieu nao gui xuong
	 * tu server thi bien TelnetInputStream.__ioException ==
	 * java.net.SocketTimeoutException: Read timed out va lan sau neu goi
	 * TelnetInputStream.read() lap tuc tra ve SocketTimeoutException: Read
	 * timed out(xem TelnetInputStream:335-->241)
	 * 
	 * @param host
	 *            String
	 * @param port
	 *            int
	 * @throws IOException
	 */
	public TelnetWrapper(String host, int port, int timeout) throws IOException {
		this.setConnectTimeout(timeout);
		this.setDefaultTimeout(timeout);
		// Neu set==true<mac dinh>:ReaderThread se tu dong doc du lieu tu
		// SocketInputStream va gan exception sau thoi gian timeout neu server
		// ko tra ve du lieu nao
		this.setReaderThread(false);
		this.connect(host, port);
		this.setSoTimeout(timeout);
		this.setSoLinger(true, 0);
	}

	/**
	 * Ends the telnet connection.
	 * 
	 * @throws Throwable
	 */
	public void finalize() throws Throwable {
		try {
			disconnect();
		} catch (IOException e) {
		}
		super.finalize();
	}

	public boolean isOpen() {
		return isConnected();
	}

	/**
	 * depq 2009 receive(int count)
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String receive(int count) throws IOException {
		byte[] bt = new byte[count];
		int iByteRead = getInputStream().read(bt);
		return new String(bt, 0, iByteRead);
	}

	public byte[] receiveByte(int count) throws IOException {
		byte[] bt = new byte[count];
		getInputStream().read(bt);
		return bt;
	}

	/**
	 * depq 2009 receiveAll
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String receiveAll() throws IOException {
		String total = null;
		int j = available();
		while (j > 0) {
			if (total == null) {
				total = receive(j);
			} else {
				total += receive(j);
			}
			j = available();
		}
		if (total == null) {
			throw new IOException(
					"Not yet receiver data  from Input stream\r\n");
		}
		return total;
	}

	/**
	 * DEPQ 2011 Lay toan bo buffer trong timeout s. Neu ko co gi thi tra ra ""
	 * 
	 * @param <any>
	 *            long
	 * @return String
	 */
	public String receiveAll(String token, long timeout) throws IOException {
		StringBuffer total = new StringBuffer();

		int j = available();
		long deadline = System.currentTimeMillis() + timeout;
		boolean found = false;
		while (System.currentTimeMillis() < deadline && !found) {
			j = available();
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				try {
					Thread.sleep(miInterval);
				} catch (InterruptedException ex) {
				}
				j = available();
			}
			while (j > 0 && System.currentTimeMillis() < deadline) { // ngung
																		// lay
																		// du
																		// lieu
																		// khi
																		// het
																		// timeout
				total.append(receive(j));
				if (total.indexOf(token) >= 0) {
					found = true;
					break;
				}
				j = available();
			}
		}
		return total.toString();
	}

	/**
	 * depq 2009: neu input stream available lien tuc thi doc lien tuc trong
	 * thoi gian timeout neu khong available thi doi de het thoi gian time out
	 * 
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */
	public String receiveAllUntil(long timeout) throws IOException {
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		String total = "";
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				try {
					Thread.sleep(miInterval);
				} catch (InterruptedException ex) {
				}
				j = available();
			}
			while (j > 0) {
				total += receive(j);
				j = available();
			}
			if (j <= 0) {
				break;
			}
		}
		if (total.equals("")) {
			throw new IOException("Wait for " + timeout
					+ " miliseconds was timeout\r\n");
		}
		return total;
	}

	/**
	 * depq 2009: Nhan tra ve khi gui lenh telnet + Doi trong thoi gian timeout
	 * cho den khi tra ve token + Neu qua thoi gian timeout ma van khong tra ve
	 * token thi: - Kiem tra khong co du lieu tra ve thi throw exception - Co du
	 * lieu tra ve thi tra ra du lieu tra ve do
	 * 
	 * @param token
	 *            String
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */
	public String receiveAllUntil(String token, long timeout)
			throws IOException {
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		String total = "";
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				try {
					Thread.sleep(miInterval);
				} catch (InterruptedException ex) {
				}
				j = available();
			}
			while (j > 0) { // Khac ham receiveUntilEx o cho khong ngung lay khi
							// het timeout
				total += receive(j);
				if (total.contains(token)) {
					break;
				}
				j = available();
			}
			if (total.contains(token)) {
				break;
			}
			if (j <= 0) {
				continue;
			}
		}
		if (total.equals("")) {
			throw new IOException("Wait for " + timeout
					+ " miliseconds was timeout\r\n");
		}
		return total;
	}

	/**
	 * depq : giong ham receiveAllUntil(String token,long timeout) nhung cho
	 * phep truyen vao mot chuoi token
	 * 
	 * @param token
	 *            String
	 * @param timeout
	 *            long
	 * @return String
	 * @throws IOException
	 */
	public String receiveAllUntil(String token[], long timeout)
			throws IOException {
		long deadline = 0;
		if (timeout >= 0) {
			deadline = System.currentTimeMillis() + timeout;
		} else {
			throw new IOException("Must set timeout > 0");
		}
		// chua het thoi gian timeout ma van con du lieu trong inputstream thi
		// tiep tu lay ve
		String total = "";
		while (System.currentTimeMillis() < deadline) {
			int j = available();
			while (j <= 0 && System.currentTimeMillis() < deadline) {
				try {
					Thread.sleep(miInterval);
				} catch (InterruptedException ex) {
				}
				j = available();
			}
			while (j > 0) {
				total += receive(j);
				if (checkToken(total, token)) {
					break;
				}
				j = available();
			}
			if (checkToken(total, token)) {
				break;
			}
			if (j <= 0) {
				continue;
			}
		}
		if (total.equals("")) {
			throw new IOException("Wait for " + timeout
					+ " miliseconds was timeout\r\n");
		}
		return total;
	}

	/**
	 * depq
	 * 
	 * @param strCheck
	 *            String
	 * @param token
	 *            String[]
	 * @return boolean
	 */
	public boolean checkToken(String strCheck, String token[]) {
		for (int i = 0; i < token.length; i++) {
			if (strCheck.contains(token[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * DEPQ 11/06/2011
	 * 
	 * @param strCheck
	 *            String
	 * @param token
	 *            String[]
	 * @return boolean
	 */
	public boolean checkLastIndexOfToken(String strCheck, String token[]) {
		for (int i = 0; i < token.length; i++) {
			if (strCheck.lastIndexOf(token[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * DEPQ 11/06/2011
	 * 
	 * @param strBufferCheck
	 *            StringBuffer
	 * @param token
	 *            String[]
	 * @return boolean
	 */
	public String checkLastIndexOfToken(StringBuffer strBufferCheck,
			String token[]) {
		for (int i = 0; i < token.length; i++) {
			if (strBufferCheck.lastIndexOf(token[i]) >= 0) {
				return token[i];
			}
		}
		return null;
	}

	public String trimStartWith(String s, String token) {
		if (s.startsWith(token)) {
			return s.substring(token.length());
		}
		return s;
	}

}

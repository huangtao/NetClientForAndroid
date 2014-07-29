package com.gameld.gameldgm;

//import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * my tcp client
 */
public class MyTcpClient implements Runnable {
	// xieyi id define
	public static final short XYID_A = 1;
	public static final short XYID_LOGIN = 2;
	public static final short XYID_B = 1;

	private static final String TAG = "MyTcpClient";
	// 10.0.2.2 is avd host(pc)'s ip
	private static final String svr_ip = "10.0.2.2";
	private static final int svr_port = 7021;
	private static final int timeout = 5000;

	private int state = 0;
	private boolean mCanSend = false;
	private Socket socket = null;
	private Handler mLoginHandler = null;
	private Handler mAccountHandler = null;
	private InputStream iStream;
	private OutputStream oStream;
	private LinkedBlockingQueue<MyQueueData> mQueue;

	/**
	 * convert big-endian to little-endian
	 * 
	 * @param x
	 * @return
	 */
	public static final short BigEndian2LittleEndian16(short x) {
		return (short) ((x & 0xFF) << 8 | 0xFF & (x >> 8));
	}

	public static final int BigEndian2LittleEndian32(int x) {
		return (x & 0xFF) << 24 | (0xFF & x >> 8) << 16 | (0xFF & x >> 16) << 8
				| (0xFF & x >> 24);
	}

	public static final short LittleEndian2BigEndian16(short x) {
		return (short) ((x & 0xFF) << 8 | 0xFF & (x >> 8));
	}

	public static final int LittleEndian2BigEndian32(int x) {
		return (x & 0xFF) << 24 | (0xFF & x >> 8) << 16 | (0xFF & x >> 16) << 8
				| (0xFF & x >> 24);
	}

	public MyTcpClient() {
		// this.handler = handler;
		state = 0;
		mCanSend = false;
		mQueue = new LinkedBlockingQueue<MyQueueData>();
	}

	public synchronized void setLoginHandler(Handler handler) {
		mLoginHandler = handler;
	}

	public synchronized void setAccountHandler(Handler handler) {
		mAccountHandler = handler;
	}

	public synchronized boolean canSend() {
		return mCanSend;
	}

	public synchronized void incState() {
		state++;
	}

	public void pushCommand(MyQueueData data) {
		mQueue.offer(data);
	}

	private void sendHello() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			// send something
			dos.writeChars("{id:hello,a:1,b:2}");
			byte[] data = bos.toByteArray();
			oStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void recvData(byte[] data) {
		try {
			// example: read like binary protocol
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			DataInputStream dis = new DataInputStream(bis);

			byte b8 = dis.readByte();
			short b16 = dis.readShort();
			// if server is LittleEndian
			// short value = LittleEndian2BigEndian16(b16);

			// do something
			b8 = (byte) b16;
			b16 = (short) b8;

			// inform main thread(UI)
			int x = 1;
			if (x > 0) {
				Message msg = mLoginHandler.obtainMessage(XYID_A);
				msg.arg1 = 0;
				mLoginHandler.sendMessage(msg);
			} else {
				Message msg = mAccountHandler.obtainMessage(XYID_A);
				msg.arg1 = 0;
				mAccountHandler.sendMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean flag = true;
		int bytes;

		while (flag) {
			switch (state) {
			case 0:
				try {
					socket = new Socket();
					socket.connect(new InetSocketAddress(svr_ip, svr_port),
							timeout);
					iStream = socket.getInputStream();
					oStream = socket.getOutputStream();

					/** send first xieyi */
					sendHello();

					state = 1;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					byte[] buffer = new byte[1024];
					if (iStream.available() > 0) {
						// Read from the InputStream
						bytes = iStream.read(buffer);
						if (bytes == -1) {
							break;
						}

						recvData(buffer);
						Log.d(TAG, "Rec:" + String.valueOf(buffer));
					} else {
						ProcessQueue();
					}
					// handler.obtainMessage(MyApp.TCP_RECV, bytes, -1,
					// buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
				}
				break;
			case 2:
				try {
					socket.close();
					flag = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public void ProcessQueue() {
		MyQueueData q = mQueue.poll();
		if (q != null) {
			if (q.mCmd == MyQueueData.CMD_CTRL)
				state = 2;
			else if (q.mCmd == MyQueueData.CMD_NET) {
				// do your customer protocol define
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(bos);

					String str = "{id:login,uid:hi...}";
					dos.writeChars(str);
					byte[] data = bos.toByteArray();
					oStream.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void write(byte[] buffer) {
		try {
			oStream.write(buffer);
		} catch (IOException e) {
			Log.e(TAG, "Exception during write socket", e);
		}
	}
}

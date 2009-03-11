package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

public final class Rms {

	private RecordStore rs = null;

	private String rmsName;

	public Rms(String rmsName) {
		this.rmsName = rmsName;
		openRms(rmsName);
	}

	public int getRecordNums() {
		if (rs == null) {
			return 0;
		}
		try {
			int nums = rs.getNumRecords();
			return nums;
		} catch (RecordStoreNotOpenException ex) {
			// log.error (ex,ex.toString ());
			return 0;
		}
	}

	public void delectRecord(int id) {
		int nums = getRecordNums();
		if (id < 1 || id > nums) {
			return;
		}
		try {
			rs.deleteRecord(id);
		} catch (RecordStoreNotOpenException e) {
			e.printStackTrace();
			// log.error (e,e.toString ());
		} catch (InvalidRecordIDException e) {
			e.printStackTrace();
			// log.error (e,e.toString ());
		} catch (RecordStoreException e) {
			e.printStackTrace();
			// log.error (e,e.toString ());
		}
	}

	// public int getNextRecordID() {
	// if (rs == null) {
	// return 0;
	// }
	// try {
	// int nums = rs.getNextRecordID();
	// return nums;
	// } catch (RecordStoreNotOpenException ex) {
	// // log.error (ex,ex.toString ());
	// } catch (RecordStoreException ex) {
	// // log.error (ex,ex.toString ());
	// }
	// return 0;
	// }

	public void openRms(String rmsName) {
		try {
			rs = RecordStore.openRecordStore(rmsName, true);
		} catch (RecordStoreException ex) {
			// log.error (ex,ex.toString ());
			// System.out.println("openRms--error");
		}
	}

	public void addRecord(byte[] data) {
		int len = 0;
		if (data == null) {
			len = 0;
		} else {
			len = data.length;
		}
		try {
			rs.addRecord(data, 0, len);
		} catch (RecordStoreNotOpenException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreException ex) {
			// log.error (ex,ex.toString ());
		}
	}

	public void setRecord(int id, byte[] data) {
		try {
			int len = 0;
			if (data != null) {
				len = data.length;
			}
			rs.setRecord(id, data, 0, len);
		} catch (InvalidRecordIDException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreNotOpenException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreException ex) {
			// log.error (ex,ex.toString ());
		}
	}

	public byte[] getRecode(int id) {
		byte[] dataByte = null;
		try {
			dataByte = rs.getRecord(id);
		} catch (InvalidRecordIDException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreNotOpenException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreException ex) {
			// log.error (ex,ex.toString ());
		}
		return dataByte;
	}

	public void deleteRMS() {
		close();
		try {
			RecordStore.deleteRecordStore(rmsName);
		} catch (RecordStoreException ex1) {
			// log.error (ex1,ex1.toString ());
		}
	}

	public void close() {
		try {
			if (rs != null) {
				rs.closeRecordStore();
				rs = null;
			}
		} catch (RecordStoreNotOpenException ex) {
			// log.error (ex,ex.toString ());
		} catch (RecordStoreException ex) {
			// log.error (ex,ex.toString ());
		}
	}

	public void clean() {
		close();
		rmsName = null;
	}

	// public byte[] string2ByteArr(String s) {
	// if (s == null) {
	// return null;
	// }
	// // 把String[] str中的内容写到rs中去
	// byte[] buffer = null;
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// DataOutputStream dos = new DataOutputStream(baos);
	// try {
	// dos.writeUTF(s);
	// buffer = baos.toByteArray();
	// } catch (IOException ex) {
	// } finally {
	// try {
	// baos.close();
	// dos.close();
	// } catch (IOException ex1) {
	// }
	// baos = null;
	// dos = null;
	// }
	// return buffer;
	// }

	/**
	 * byte2String
	 * 
	 * @param bs
	 *            byte[]
	 * @return String
	 */
	public String byteArr2String(byte[] bs) {
		if (bs == null) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		DataInputStream dis = new DataInputStream(bais);
		String ts = "";
		try {
			ts = dis.readUTF();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				dis.close();
				bais.close();
			} catch (IOException ex1) {
			}
			dis = null;
			bais = null;
		}
		return ts;
	}

	// public int byteArr2Int(byte[] b) {
	// if (b == null || b.length != 4) {
	// return 0;
	// } else {
	// int i = ((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
	// | ((b[2] & 0xff) << 8) | (b[3] & 0xff);
	// return i;
	// }
	// }

	public byte[] int2ByteArr(int i) {
		byte[] intData = new byte[4];
		intData[0] = (byte) ((i & 0xff000000) >> 24);
		intData[1] = (byte) ((i & 0x00ff0000) >> 16);
		intData[2] = (byte) ((i & 0x0000ff00) >> 8);
		intData[3] = (byte) (i & 0x000000ff);
		return intData;
	}

	public byte[] byte2ByteArr(byte i) {
		byte[] byteData = new byte[1];
		byteData[0] = i;
		return byteData;
	}

	public static byte[] StringToByte(String commentContent) {
		if (commentContent == null)
			return null;
		byte[] buffer = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeUTF(commentContent);
			buffer = baos.toByteArray();
		} catch (IOException ex) {
		} finally {
			try {
				baos.close();
				dos.close();
			} catch (IOException ex1) {
			}
			baos = null;
			dos = null;
		}
		return buffer;
	}
	// public byte byteArr2Byte(byte[] i) {
	// byte byteData = i[0];
	// return byteData;
	// }

	// public byte[] boolean2ByteArr(boolean bool) {
	// byte[] data = new byte[1];
	// if (bool) {
	// data[0] = 1;
	// }
	// return data;
	// }

	// public boolean byteArr2Boolean(byte[] data) {
	// boolean bool = false;
	// if (data[0] == 1) {
	// bool = true;
	// }
	// return bool;
	// }

	// public void add(boolean bool) {
	// byte[] data = boolean2ByteArr(bool);
	// addRecord(data);
	// }

	public void add(int i) {
		byte[] data = int2ByteArr(i);
		addRecord(data);
	}

	public void add(byte b) {
		byte[] data = byte2ByteArr(b);
		addRecord(data);
	}

	public void add(byte[] data) {
		addRecord(data);
	}

	public void add(String s) {
		byte[] data = StringToByte(s);
		addRecord(data);
	}

	// public void set(int id, boolean bool) {
	// byte[] tpdata = boolean2ByteArr(bool);
	// setRecord(id, tpdata);
	// }

	// public void set(int id, int data) {
	// byte[] tpdata = int2ByteArr(data);
	// setRecord(id, tpdata);
	// }

	public void set(int id, byte data) {
		byte[] tpdata = byte2ByteArr(data);
		setRecord(id, tpdata);
	}

	// public void set(int id, byte[] data) {
	// setRecord(id, data);
	// }

	public void set(int id, String data) {
		byte[] tpdata = StringToByte(data);
		setRecord(id, tpdata);
	}

	public String getString(int id) {
		String data = null;
		byte[] tpdata = getRecode(id);
		data = byteArr2String(tpdata);
		return data;
	}

	// public int getInt(int id) {
	// int data = 0;
	// byte[] tpdata = getRecode(id);
	// data = byteArr2Int(tpdata);
	// return data;
	// }

	// public boolean getBoolean(int id) {
	// byte[] tpdata = getRecode(id);
	// boolean data = byteArr2Boolean(tpdata);
	// return data;
	// }

	public byte getByte(int id) {
		byte data = 0;
		byte[] tpdata = getRecode(id);
		if (tpdata == null) {
			return -1;
		}
		data = tpdata[0];
		return data;
	}

	public byte[] getByteArr(int id) {
		byte[] tpdata = getRecode(id);
		return tpdata;
	}

	// public void addRecordNums (int nums) {
	// for (int i = 0; i < nums; i++) {
	// try {
	// rs.addRecord (null, 0, 0);
	// } catch (RecordStoreNotOpenException e) {
	// //log.error (e,e.toString ());
	// } catch (RecordStoreFullException e) {
	// //log.error (e,e.toString ());
	// } catch (RecordStoreException e) {
	// //log.error (e , e.toString ());
	// }
	// }
	// }

	// public int getSize() {
	// // TODO 自动生成方法存根
	// int size = 0;
	// if (rs != null) {
	// try {
	// size = rs.getSize();
	// } catch (RecordStoreNotOpenException e) {
	// // TODO 自动生成 catch 块
	// // log.error (e,e.toString ());
	// }
	// }
	// return size;
	// }

	// public int getSizeAvailable() {
	// // TODO 自动生成方法存根
	// int size = 0;
	// if (rs != null) {
	// try {
	// size = rs.getSizeAvailable();
	// } catch (RecordStoreNotOpenException e) {
	// // TODO 自动生成 catch 块
	// // log.error (e,"getSizeAvailable()" + e.toString ());
	// }
	// }
	// return size;
	// }

}

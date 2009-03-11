package rms;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import utils.ArrayList;

public class ZincRMSDatabase {
	private static final String INDEX_ENTRY_TABLE = "indexentry";
	private ArrayList indexEntries = null;

	public void loadIndexEntries() {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(INDEX_ENTRY_TABLE, false);
			if (rs.getNumRecords() > 0) {
				indexEntries = new ArrayList(rs.getNumRecords());
				RecordEnumeration enumeration = rs.enumerateRecords(null, null,
						false);
				while (enumeration.hasNextElement()) {
					byte[] data = enumeration.nextRecord();
					IndexEntry indexEntry = new IndexEntry();
					indexEntry.id = enumeration.nextRecordId();
					indexEntry.index = ((data[0] & 0xff) << 24)
							| ((data[1] & 0xff) << 16)
							| ((data[2] & 0xff) << 8) | (data[3] & 0xff);
					indexEntry.key = new String(data, 4, data.length);
					indexEntries.add(indexEntry);
					indexEntry = null;
					data = null;
				}
				enumeration = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				} finally {
					rs = null;
				}
			}
		}
	}

	public void addData(String recordName, String key, byte[] data) {
		if (recordName == null || key == null || data == null)
			return;
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(recordName, true);
			int index = getEntryIndex(key);
			if (index == -1) {
				index = rs.addRecord(data, 0, data.length);
				addIndexEntry(index, key);
			} else {
				rs.setRecord(index, data, 0, data.length);
			}
		} catch (Exception e) {
		} finally {
			if (rs != null) {
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				} finally {
					rs = null;
				}
			}
		}
	}

	public byte[] getData(String key) {
		int index = getEntryIndex(key);
		if (index == -1) {
			return null;
		} else {
			return null;
		}
	}

	public int getEntryIndex(String key) {
		if (key == null || indexEntries == null)
			return -1;
		for (int i = 0; i < indexEntries.size(); i++) {
			IndexEntry indexEntry = (IndexEntry) indexEntries.get(i);
			if (key.equals(indexEntry.key))
				return indexEntry.index;
			indexEntry = null;
		}
		return -1;
	}

	public void addIndexEntry(int index, String key) {
		byte[] keydata = key.getBytes();
		byte[] data = new byte[keydata.length + 4];
		data[0] = (byte) ((index & 0xff000000) >> 24);
		data[1] = (byte) ((index & 0x00ff0000) >> 16);
		data[2] = (byte) ((index & 0x0000ff00) >> 8);
		data[3] = (byte) (index & 0x000000ff);
		for (int i = 0; i < keydata.length; i++) {
			data[i + 4] = keydata[i];
		}
		keydata = null;
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(INDEX_ENTRY_TABLE, true);
			int id = rs.addRecord(data, 0, data.length);
			IndexEntry indexEntry = new IndexEntry(id, index, key);
			if (indexEntries == null)
				indexEntries = new ArrayList(2);
			indexEntries.add(indexEntry);
			indexEntry = null;
		} catch (Exception e) {
		} finally {
			if (rs != null) {
				try {
					rs.closeRecordStore();
				} catch (Exception e) {
				} finally {
					rs = null;
				}
			}
		}

	}

	class IndexEntry {
		public int id = -1;
		public int index = -1;
		public String key = null;

		public IndexEntry() {
		}

		public IndexEntry(int id, int index, String key) {
			this.id = id;
			this.index = index;
			this.key = key;
		}
	}
}

package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.util.Util;

public class TextIdList implements Value {
	
	private final List<TextId> list = new ArrayList<TextId>();
	
	public boolean contains(long textId) {
		for(TextId tid : list) {
			if(textId == tid.getTextId()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsAll(Label[] labels) {
		for(Label l : labels) {
			if(!contains(l.asLong())) {
				return false;
			}
		}
		return true;
	}
	
	public List<TextId> getList() {
		return list;
	}
	
	public void addTextId(long textId) {
		TextId tid = new TextId();
		tid.setTextId(textId);
		list.add(tid);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Util.PrefixLengthShort.write(bb, list);
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		Util.PrefixLengthShort.read(bb, list, true, TextId.class);
	}
	
	@Override
	public java.lang.String toString() {
		return list.toString();
	}

}

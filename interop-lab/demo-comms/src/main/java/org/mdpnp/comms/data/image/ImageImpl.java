package org.mdpnp.comms.data.image;

import org.mdpnp.comms.IdentifierImpl;

@SuppressWarnings("serial")
public class ImageImpl extends IdentifierImpl implements Image {

	public ImageImpl(Class<?> cls, String name) {
		super(cls, name);
	}

	@Override
	public String getIdentifierClass() {
		return Image.class.getName();
	}

}

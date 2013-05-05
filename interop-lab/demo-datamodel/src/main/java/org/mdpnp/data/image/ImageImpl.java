package org.mdpnp.data.image;

import org.mdpnp.data.IdentifierImpl;

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

package com.secunet.eidserver.testbed.web.i18n;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum Icon
{
	// see: https://vaadin.com/icons


	CANCEL(FontAwesome.BAN),

	SAVE(FontAwesome.SAVE),

	ADD(FontAwesome.PLUS),

	DELETE(FontAwesome.MINUS),

	REMOVE(FontAwesome.MINUS),

	CONFIGURE(FontAwesome.COGS),

	RIGHT(FontAwesome.CARET_RIGHT),

	LEFT(FontAwesome.CARET_LEFT),

	UP(FontAwesome.CARET_UP),

	DOWN(FontAwesome.CARET_DOWN),

	DOWNLOAD(FontAwesome.DOWNLOAD),

	MORE_H(FontAwesome.ELLIPSIS_H),

	MORE_V(FontAwesome.ELLIPSIS_V),

	APPLY(FontAwesome.CHECK),

	ACCEPT(FontAwesome.CHECK),
	
	NAV_REPORT(FontAwesome.CLIPBOARD)
	
	;


	final Resource icon;

	Icon(Resource icon)
	{
		this.icon = icon;
	}

	public Resource getIcon()
	{
		return icon;
	}

}

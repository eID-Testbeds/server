package com.secunet.eidserver.testbed.web.i18n;

import com.vaadin.ui.Image;

public enum I18NImage
{

	PRODUCT_LOGO("product.gif", null, null, false),

	PRODUCT_LOGO_ICON_NAV("product_icon.gif", "16 px", "16 px", false),

	VENDOR_LOGO("product.gif", null, null, false),

	;


	private final String filePath;
	/** as usable for vaadin {@link Image#setWidth(String)} */
	private final String width;
	/** as usable for vaadin {@link Image#setHeight(String)} */
	private final String heigth;
	private final boolean localized;

	I18NImage(final String filepath, String h, String w, boolean isLocalized)
	{
		this.filePath = filepath;
		this.heigth = h;
		this.width = w;
		this.localized = isLocalized;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public String getWidth()
	{
		return width;
	}

	public String getHeigth()
	{
		return heigth;
	}

	public boolean isLocalized()
	{
		return localized;
	}

}

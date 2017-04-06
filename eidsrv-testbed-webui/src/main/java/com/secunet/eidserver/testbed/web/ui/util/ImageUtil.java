package com.secunet.eidserver.testbed.web.ui.util;


import java.io.File;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NImage;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;

/**
 * easy access to images in the /WEB-INF/images/ folder
 */
public class ImageUtil
{
	/**
	 * easy access to images in the /WEB-INF/images/ folder
	 *
	 * @param imageName
	 *            must not be null
	 * @return the file resource
	 */
	public static FileResource getImageFileResource(final String imageName)
	{
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/" + imageName));

		return resource;
	}

	public static FileResource getImageFileResource(I18NImage imgRes)
	{
		String imagePath = I18NHandler.getImagePath(imgRes);
		return getImageFileResource(imagePath);
	}

	/**
	 * easy access to images in the /WEB-INF/images/ folder
	 *
	 * @param imageName
	 *            must not be null
	 * @return the Vaadin Image
	 */
	public static Image getImage(final String imageName)
	{
		FileResource imageFileResource = getImageFileResource(imageName);
		if (imageFileResource != null)
		{
			Image image = new Image(null, imageFileResource);
			image.setDescription(imageName);

			return image;
		}

		return null;
	}

	public static Image getImage(I18NImage imageRes)
	{
		Image image = getImage(I18NHandler.getImagePath(imageRes));
		if (image != null)
		{
			image.setDescription(I18NHandler.getText(imageRes));

			if (imageRes.getWidth() != null && imageRes.getHeigth() != null)
			{
				image.setWidth(imageRes.getWidth());
				image.setHeight(imageRes.getHeigth());
			}
		}

		return image;
	}


}

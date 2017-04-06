package com.secunet.eidserver.testbed.web.ui.comp;

import com.secunet.eidserver.testbed.web.core.App;
import com.vaadin.server.Page;
import com.vaadin.ui.VerticalLayout;

abstract public class AbstractTestbedView extends VerticalLayout
{


	/** generated */
	private static final long serialVersionUID = 6551070468393423973L;

	public AbstractTestbedView()
	{
		setUpPageTitle();
	}

	/**
	 * setup the title of the page shown in the browser bar
	 */
	protected void setUpPageTitle()
	{
		final String PATH = Page.getCurrent().getUriFragment();
		Page.getCurrent().setTitle(App.getProductName() + ((PATH != null) ? " " + PATH : "/"));
	}
}

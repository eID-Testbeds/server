package com.secunet.eidserver.testbed.web.ui.comp;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

public class MainLayout extends HorizontalLayout
{

	/** generated */
	private static final long serialVersionUID = 6781615051717375684L;
	
	
	protected CssLayout menuLayout = new CssLayout();
	protected CssLayout contentLayout = new CssLayout();

	public MainLayout()
	{
		setSizeFull();

		menuLayout.setPrimaryStyleName("valo-menu");

		contentLayout.setPrimaryStyleName("valo-content");
		contentLayout.addStyleName("v-scrollable");
		contentLayout.setSizeFull();

		addComponents(menuLayout, contentLayout);
		setExpandRatio(contentLayout, 1);
		
		setSpacing(true);
	}

	public void addMenu(Component menu)
	{
		menu.addStyleName("valo-menu-part");
		menuLayout.addComponent(menu);
	}

	public ComponentContainer getContentLayout()
	{
		return contentLayout;
	}
}

package com.secunet.eidserver.testbed.web;

import com.secunet.eidserver.testbed.common.classes.Singletons;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.comp.MainLayout;
import com.secunet.eidserver.testbed.web.ui.comp.NavigationLayout;
import com.secunet.eidserver.testbed.web.ui.view.error.ErrorView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.URL;

@Theme("testbed")
@Widgetset("com.secunet.eidserver.testbed.TestbedWidgetSet")
@CDIUI("")
public class TestbedUI extends UI
{
	private static final long serialVersionUID = -1638210627374641494L;

	@Inject
	private CDIViewProvider viewProvider;

	protected MainLayout layout;

	private Navigator navigator;

	@Override
	@PostConstruct
	protected void init(final VaadinRequest vaadinRequest)
	{
		try
		{
			VaadinSession.getCurrent().setLocale(vaadinRequest.getLocale());


			// TODO
			// - report "mouse over" test step report log message details window
			// - Resourcen
			// - ICONS
			// - exception handling
			// - profile icon weg?
			// - backend anbindung ggf. mocken
			// - session die
			// - chrome crash

			layout = new MainLayout();
			ComponentContainer contentLayout = layout.getContentLayout();
			NavigationLayout navLayout = new NavigationLayout();
			layout.addMenu(navLayout);

			setContent(layout);

			// AuthNavigator an = new AuthNavigator(this, contentLayout);
			// an.addProvider(viewProvider);
			// setNavigator(an);
			// getNavigator().navigateTo(getNavigator().getState());

			// navigator = new AuthNavigator(this, contentLayout);
			// navigator.addProvider(viewProvider);
			// setNavigator(navigator);
			// navigator.navigateTo(navigator.getState());


			navigator = new Navigator(this, contentLayout);
			navigator.addProvider(viewProvider);
			setNavigator(navigator);
			navigator.setErrorView(ErrorView.class);
			navigator.navigateTo(navigator.getState());
			VaadinServletRequest req = (VaadinServletRequest) vaadinRequest;
			StringBuffer uriString = req.getRequestURL();
			Singletons.currentUrl = new URL(uriString.toString());
		}
		catch (Exception exc)
		{
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_ContactAdministrator), exc);
		}
	}

}

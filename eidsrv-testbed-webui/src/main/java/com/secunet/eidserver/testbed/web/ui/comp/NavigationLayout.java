package com.secunet.eidserver.testbed.web.ui.comp;

import java.util.HashMap;
import java.util.Map;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.nav.TestBedView;
import com.secunet.eidserver.testbed.web.ui.util.ImageUtil;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class NavigationLayout extends CssLayout
{

	/** Generated */
	private static final long serialVersionUID = 1065933268573171128L;

	protected Label titleLabel;
	protected VerticalLayout mainNavigationLayout;
	protected CssLayout menuEntriesLayout = new CssLayout();

	protected MenuBar settings;
	protected HorizontalLayout header;
	protected Map<I18NText, AbstractComponent> components;

	public NavigationLayout()
	{
		components = new HashMap<>();
		buildNavigation();
	}

	protected void buildNavigation()
	{
		header = new HorizontalLayout();
		header.setWidth("100%");
		header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		header.addStyleName(ValoTheme.MENU_TITLE);
		addComponent(header);


		// final Button showMenu = new Button("Menu", e -> {
		// if (getStyleName().contains("valo-menu-visible"))
		// removeStyleName("valo-menu-visible");
		// else
		// addStyleName("valo-menu-visible");
		// });
		// showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		// showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		// showMenu.addStyleName("valo-menu-toggle");
		// showMenu.setIcon(FontAwesome.LIST);
		// addComponent(showMenu);

		final Label title = new Label("<h3>eID-Server <b>Testbed</b></h3>", ContentMode.HTML);
		title.setSizeUndefined();
		header.addComponent(title);
		header.setExpandRatio(title, 1);

		settings = new MenuBar();
		settings.addStyleName("user-menu");
		final MenuItem settingsItem = settings.addItem("admin", ImageUtil.getImageFileResource("profile-pic-300px.jpg"), null);
		settingsItem.addItem("Deutsch", new MenuBar.Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				I18NHandler.setLocale(I18NHandler.GERMAN);
				updateCaptions();
				UI.getCurrent().getNavigator().navigateTo(UI.getCurrent().getNavigator().getState());
			}
		});
		settingsItem.addItem("English", new MenuBar.Command() {
			private static final long serialVersionUID = 1L;

			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				I18NHandler.setLocale(I18NHandler.ENGLISH);
				updateCaptions();
				UI.getCurrent().getNavigator().navigateTo(UI.getCurrent().getNavigator().getState());
			}
		});
		addComponent(settings);

		menuEntriesLayout.setPrimaryStyleName("valo-menuitems");
		addComponent(menuEntriesLayout);


		// configuration
		Label label = generateGroup(I18NText.Navigation_Configuration, null);
		menuEntriesLayout.addComponent(label);

		Button menuEntry = generateButton(I18NText.Navigation_Configuration_CandidateAdd, e -> navigate(TestBedView.CANDIDATE, "add"));
		menuEntry.setIcon(TestBedView.CANDIDATE.getIcon());
		menuEntriesLayout.addComponent(menuEntry);

		menuEntry = generateButton(I18NText.Navigation_Configuration_CandidateEdit, e -> navigate(TestBedView.CANDIDATE, "edit"));
		menuEntry.setIcon(TestBedView.CANDIDATE.getIcon());
		menuEntriesLayout.addComponent(menuEntry);

		menuEntry = generateButton(I18NText.Navigation_Configuration_TestCaseEdit, e -> navigate(TestBedView.TESTCASE, "edit"));
		menuEntry.setIcon(TestBedView.TESTCASE.getIcon());
		menuEntriesLayout.addComponent(menuEntry);

		menuEntry = generateButton(I18NText.Navigation_Configuration_TestStepMgmt, e -> navigate(TestBedView.TESTSTEP, "edit"));
		menuEntry.setIcon(TestBedView.TESTSTEP.getIcon());
		menuEntriesLayout.addComponent(menuEntry);


		// Tests
		label = generateGroup(I18NText.Navigation_Test, null);
		menuEntriesLayout.addComponent(label);

		menuEntry = generateButton(I18NText.Navigation_Test_Execution, e -> navigate(TestBedView.EXECUTION, "edit"));
		menuEntry.setIcon(TestBedView.EXECUTION.getIcon());
		menuEntriesLayout.addComponent(menuEntry);


		// reports
		label = generateGroup(I18NText.Navigation_Reports, null);
		menuEntriesLayout.addComponent(label);
		menuEntry = generateButton(I18NText.Navigation_Reports_View, e -> navigate(TestBedView.REPORTS));
		menuEntry.setIcon(TestBedView.REPORTS.getIcon());
		menuEntriesLayout.addComponent(menuEntry);
	}


	protected void updateCaptions()
	{
		components.forEach((t, c) -> {
			if (c instanceof Label)
				((Label) c).setValue(I18NHandler.getText(t));
			else
				c.setCaption(I18NHandler.getText(t));
		});
	}

	public Button generateButton(I18NText caption, ClickListener l)
	{
		Button menuEntry = new Button(I18NHandler.getText(caption), l);
		menuEntry.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		menuEntry.setHtmlContentAllowed(true);

		components.put(caption, menuEntry);

		return menuEntry;
	}

	private void navigate(TestBedView view)
	{
		navigate(view, null);
	}

	private void navigate(TestBedView view, String parameter)
	{
		UI.getCurrent().getNavigator().navigateTo(view.getViewName() + (parameter != null ? ("/" + parameter) : ""));
	}

	private Label generateGroup(I18NText caption, Resource icon)
	{
		Label label = new Label(I18NHandler.getText(caption), ContentMode.HTML);
		label.setIcon(icon);
		label.setPrimaryStyleName("valo-menu-subtitle");
		label.addStyleName("h4");
		label.setSizeUndefined();

		components.put(caption, label);

		return label;
	}
}

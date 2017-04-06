package com.secunet.eidserver.testbed.web.ui.view.error;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView("badrights")
public class BadRightsView extends AbstractTestbedView implements View, PropertyChangeListener
{

	private static final long serialVersionUID = -5820941744456718257L;


	public BadRightsView()
	{
		Label header = new Label(I18NHandler.getText(I18NText.Warning_MissingRights_Caption));
		header.addStyleName(TestbedTheme.LABEL_H1);
		Label descr = new Label(I18NHandler.getText(I18NText.Warning_MissingRights_Description));

		// layouting
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth(100, Unit.PERCENTAGE);

		VerticalLayout textLayout = new VerticalLayout(new Label(""), header, descr);
		textLayout.setSpacing(true);
		textLayout.setMargin(true);


		layout.addComponent(textLayout);

		layout.setExpandRatio(textLayout, 1f);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{

	}

	@Override
	public void enter(ViewChangeEvent event)
	{
	}

}

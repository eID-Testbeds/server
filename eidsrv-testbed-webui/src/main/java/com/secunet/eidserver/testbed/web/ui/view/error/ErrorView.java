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
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView("error")
public class ErrorView extends AbstractTestbedView implements View, PropertyChangeListener
{

	private static final long serialVersionUID = 7367206544656849922L;

	public ErrorView()
	{
		setSpacing(true);
		setMargin(new MarginInfo(true, true, false, false));

		Label header = new Label(I18NHandler.getText(I18NText.Error_GeneralError));
		header.addStyleName(TestbedTheme.LABEL_H1);
		Label descr = new Label(I18NHandler.getText(I18NText.Instruction_ContactAdministrator));

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
	public void propertyChange(PropertyChangeEvent arg0)
	{

	}

	@Override
	public void enter(ViewChangeEvent event)
	{
	}

}

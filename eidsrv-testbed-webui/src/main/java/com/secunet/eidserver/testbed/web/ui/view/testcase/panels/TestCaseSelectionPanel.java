package com.secunet.eidserver.testbed.web.ui.view.testcase.panels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.view.testcase.TestCaseEditModel;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;

public class TestCaseSelectionPanel extends TogglePanelLayout implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;


	protected TestCaseEditModel model;

	protected FormLayout layout;
	protected ComboBox selectionCombobox;

	public TestCaseSelectionPanel(TestCaseEditModel model, String caption, Resource icon)
	{
		super(caption, icon);

		this.model = model;
	}


	public void initializeUI()
	{
		selectionCombobox = new ComboBox(I18NHandler.getText(I18NText.TestCase_Selection));
		selectionCombobox.setWidth(100, Unit.PERCENTAGE);
		selectionCombobox.setFilteringMode(FilteringMode.CONTAINS);
		selectionCombobox.setNullSelectionAllowed(false);
		selectionCombobox.addValueChangeListener(evt -> model.setSelectedTestCase((TestCase) evt.getProperty().getValue()));


		layout = new FormLayout(selectionCombobox);
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);

		updateComboBox();
	}

	protected void updateComboBox()
	{
		List<TestCase> sortedTestcases = model.getTestCasesSorted();
		selectionCombobox.setContainerDataSource(new ListContainer<>(sortedTestcases));
		sortedTestcases.forEach(tc -> selectionCombobox.setItemCaption(tc, tc.getName() + ((tc instanceof DefaultTestCase) ? " (default)" : "")));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case TestCaseEditModel.PROPERTY_SELECTED_TESTCASE:
					setMinimized(true);
				case TestCaseEditModel.PROPERTY_TESTCASES:
					updateComboBox();
					break;

				default:
					// not interested in this property
					return;
			}
		}
	}

}

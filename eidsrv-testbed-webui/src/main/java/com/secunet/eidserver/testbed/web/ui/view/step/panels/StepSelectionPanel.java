package com.secunet.eidserver.testbed.web.ui.view.step.panels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditModel;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;

public class StepSelectionPanel extends TogglePanelLayout implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;


	protected TeststepEditModel model;

	protected FormLayout layout;
	protected ComboBox selectionCombobox;

	public StepSelectionPanel(TeststepEditModel model, String caption, Resource icon)
	{
		super(caption, icon);

		this.model = model;
	}


	public void initializeUI()
	{
		selectionCombobox = new ComboBox(I18NHandler.getText(I18NText.TestCaseStep_Selection));
		selectionCombobox.setWidth(100, Unit.PERCENTAGE);
		selectionCombobox.setFilteringMode(FilteringMode.CONTAINS);
		selectionCombobox.setNullSelectionAllowed(false);
		selectionCombobox.addValueChangeListener(evt -> model.setSelectedStep((TestCaseStep) evt.getProperty().getValue()));


		layout = new FormLayout(selectionCombobox);
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);

		updateComboBox();
	}

	protected void updateComboBox()
	{
		List<TestCaseStep> sortedTestcases = model.getAllStepsSorted();
		selectionCombobox.setContainerDataSource(new ListContainer<>(sortedTestcases));
		sortedTestcases.forEach(tc -> selectionCombobox.setItemCaption(tc, Humanizer.getCaption(tc)));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case TeststepEditModel.STEPS:
					updateComboBox();
					break;

				default:
					// not interested in this property
					return;
			}
		}
	}

}

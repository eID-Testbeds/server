package com.secunet.eidserver.testbed.web.ui.view.testrun.panels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.secunet.eidserver.testbed.web.ui.view.testrun.RunViewModel;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;

public class CandidateSelectionPanel extends TogglePanelLayout implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;


	protected RunViewModel model;

	protected FormLayout layout;
	protected ComboBox selectionCombobox;

	public CandidateSelectionPanel(RunViewModel model, String caption, Resource icon)
	{
		super(caption, icon);

		this.model = model;
	}


	public void initializeUI()
	{
		selectionCombobox = new ComboBox(I18NHandler.getText(I18NText.View_TestCandidate_CandidateSelection));
		selectionCombobox.setWidth(100, Unit.PERCENTAGE);
		selectionCombobox.setFilteringMode(FilteringMode.CONTAINS);
		selectionCombobox.setNullSelectionAllowed(false);
		selectionCombobox.addValueChangeListener(evt -> model.setSelectedCandidate((TestCandidate) evt.getProperty().getValue()));


		layout = new FormLayout(selectionCombobox);
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);

		updateComboBox();
	}

	protected void updateComboBox()
	{
		Collection<TestCandidate> sortedTestcases = model.getCandidates().values();
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
				case RunViewModel.CANDIDATES:
					updateComboBox();
					break;

				default:
					// not interested in this property
					return;
			}
		}
	}


	public void select(TestCandidate testCandidate)
	{
		selectionCombobox.select(testCandidate);
	}

}

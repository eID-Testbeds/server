package com.secunet.eidserver.testbed.web.ui.view.testcase.panels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.SortedMultiValueListSelection;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.secunet.eidserver.testbed.web.ui.view.testcase.TestCaseEditModel;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.server.Resource;
import com.vaadin.ui.FormLayout;

public class TestCaseStepsSelectionPanel extends TogglePanelLayout implements PropertyChangeListener
{

	/** generated */
	private static final long serialVersionUID = 1393328094954734139L;


	protected TestCaseEditModel model;
	protected CopyTestCase testCase;
	protected Set<TestCaseStep> allSteps;

	protected FormLayout form;
	@PropertyId("testCaseSteps")
	protected SortedMultiValueListSelection<TestCaseStep> enhanceStepField;

	protected BeanFieldGroup<CopyTestCase> fieldGroup;


	public TestCaseStepsSelectionPanel(TestCaseEditModel model, String caption, Resource icon)
	{
		super(caption, icon);

		this.model = model;
		allSteps = model.getSteps();
	}

	public void initializeUI()
	{
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		enhanceStepField = new SortedMultiValueListSelection<>(allSteps, testCaseSteps, I18NHandler.getText(I18NText.TestCaseStep_Selection), null);
		enhanceStepField.setCaptionConverter(t -> Humanizer.getCaption(t));

		form = new FormLayout(enhanceStepField);
		form.setSpacing(true);
		form.setMargin(true);

		setContent(form);

		fieldGroup = new BeanFieldGroup<>(CopyTestCase.class);
		fieldGroup.bindMemberFields(this);


		setMinimized(false);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		if (arg0.getSource() == model)
		{
			switch (arg0.getPropertyName())
			{
				case TestCaseEditModel.PROPERTY_EDIT_TESTCASE:
					testCase = (CopyTestCase) arg0.getNewValue();
					updateUI();
					break;

				default:
					return; // noi
			}
		}
	}

	protected void updateUI()
	{
		setMinimized(testCase == null);

		if (testCase != null)
			fieldGroup.setItemDataSource(testCase);
	}

	public void commitChanges() throws CommitException
	{
		fieldGroup.commit();
	}

}

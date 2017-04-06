package com.secunet.eidserver.testbed.web.ui.view.step.panels.in;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditModel;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditView;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class StepDetailsPanel extends TogglePanelLayout implements PropertyChangeListener
{
	private static final long serialVersionUID = 6463291033593479347L;

	private TestCaseStepDAO testCaseStepDAO;

	protected TeststepEditModel model;
	protected TestCaseStep selection;
	protected TestCaseStep editing;
	protected TeststepEditView parentView;
	protected boolean showScheme = true;

	protected FormLayout layout;
	protected CheckBox isDefault;
	protected CheckBox isInbound;

	@PropertyId("suffix")
	protected TextField suffix;
	@PropertyId("xsdName")
	protected TextArea scheme;
	@PropertyId("target")
	protected ComboBox targetInterfaceType;

	protected BeanFieldGroup<TestCaseStep> fieldGroup;


	public StepDetailsPanel(TeststepEditModel model, String caption, Resource icon, TestCaseStepDAO testCaseStepDAO, TeststepEditView parentView)
	{
		super(caption, icon);
		this.testCaseStepDAO = testCaseStepDAO;
		this.model = model;
		this.parentView = parentView;
	}

	public void initializeUI()
	{
		setMinimized(true);

		layout = new FormLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setWidth(100, Unit.PERCENTAGE);

		isDefault = new CheckBox(I18NHandler.getText(I18NText.TestCaseStep_isDefault));
		isDefault.setEnabled(false);
		isInbound = new CheckBox(I18NHandler.getText(I18NText.TestCaseStep_isInbound));
		isInbound.setEnabled(false);
		suffix = new TextField(I18NHandler.getText(I18NText.TestCaseStep_suffix));
		suffix.setWidth(100, Unit.PERCENTAGE);
		suffix.setNullRepresentation("");
		suffix.setRequired(true);
		suffix.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 4911275265811612225L;

			@Override
			public void textChange(TextChangeEvent event)
			{
				parentView.enableSave(event.getText().length() > 0);
			}
		});
		scheme = new TextArea(I18NHandler.getText(I18NText.TestCaseStep_Scheme));
		scheme.setWidth(100, Unit.PERCENTAGE);
		scheme.setRows(6);
		scheme.setNullRepresentation("");
		scheme.setVisible(showScheme);
		
		targetInterfaceType = new ComboBox(I18NHandler.getText(I18NText.TestCaseStep_TargetInterfaceType), Arrays.asList(TargetInterfaceType.values()));
		targetInterfaceType.setWidth(100, Unit.PERCENTAGE);

		layout.addComponent(isDefault);
		layout.addComponent(isInbound);
		layout.addComponent(suffix);
		layout.addComponent(scheme);
		layout.addComponent(targetInterfaceType);


		setContent(layout);

		fieldGroup = new BeanFieldGroup<>(TestCaseStep.class);
		fieldGroup.bindMemberFields(this);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case TeststepEditModel.SELECTED:
					selection = (TestCaseStep) evt.getNewValue();
					updateUI();
					break;

				default:
					return; // noi
			}
		}
	}


	protected void updateUI()
	{
		String oldSuffix = null;
		if (selection != null)
		{
			isDefault.setValue(selection.isDefault());
			isInbound.setValue(selection.getInbound());

			oldSuffix = selection.getSuffix();

			if (selection.isDefault())
			{
				TestCaseStep copy = testCaseStepDAO.createNew();
				copy.setName(selection.getName());
				copy.setSuffix("");
				copy.setDefault(false);
				copy.setInbound(selection.getInbound());
				copy.setMessage(selection.getMessage());
				copy.setOptional(selection.isOptional());
				copy.setTarget(selection.getTarget());
				copy.setXsd(selection.getXsd());

				editing = copy;
			}
			else
			{
				editing = selection;
			}
		}

		setMinimized(editing == null);
		fieldGroup.setItemDataSource(editing);

		model.setEditableStep(editing, oldSuffix);
	}

	public void commitChanges() throws CommitException
	{
		fieldGroup.commit();

		CommitHandler.clearUserError(fieldGroup.getFields());
	}

	public void showScheme(boolean showScheme)
	{
		this.showScheme = showScheme;

		scheme.setVisible(showScheme);
	}

}

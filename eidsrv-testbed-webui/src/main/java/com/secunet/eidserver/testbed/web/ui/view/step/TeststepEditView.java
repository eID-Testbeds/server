package com.secunet.eidserver.testbed.web.ui.view.step;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.secunet.eidserver.testbed.web.ui.util.ParameterHandler;
import com.secunet.eidserver.testbed.web.ui.view.step.panels.StepSelectionPanel;
import com.secunet.eidserver.testbed.web.ui.view.step.panels.in.StepDetailsPanel;
import com.secunet.eidserver.testbed.web.ui.view.step.panels.in.StepInboundStepPanel;
import com.secunet.eidserver.testbed.web.ui.view.step.panels.out.StepOutboundStepPanel;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

@CDIView("teststeps")
public class TeststepEditView extends AbstractTestbedView implements Serializable, View, PropertyChangeListener
{

	@EJB
	private TestCaseController testCaseController;

	@EJB
	private TestCaseStepDAO testCaseStepDAO;

	/** Generated */
	private static final long serialVersionUID = 8093868782386154457L;


	protected TeststepEditModel model;

	protected StepSelectionPanel stepSelectionPanel;
	protected StepDetailsPanel stepDetailsPanel;
	protected StepInboundStepPanel inboundStepPanel;
	protected StepOutboundStepPanel outboundStepPanel;
	protected HorizontalLayout buttonLayout;
	protected Button saveButton;

	@PostConstruct
	public void setupView()
	{
		setSpacing(true);
		setMargin(new MarginInfo(true, true, true, false));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model && TeststepEditModel.EDIT.equals(evt.getPropertyName()))
			stepDetailsPanel.showScheme(evt.getNewValue() != null && ((TestCaseStep) evt.getNewValue()).getInbound());
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		try
		{
			String uuid = null;

			ParameterHandler paramHandler = new ParameterHandler(event.getParameters());
			if (paramHandler.isEdit() && paramHandler.getAssociatedUUID().isPresent())
				uuid = paramHandler.getAssociatedUUID().get();

			Set<TestCaseStep> allTestCaseSteps = testCaseController.getAllTestCaseSteps();
			model = new TeststepEditModel(allTestCaseSteps);

			setupUI(uuid);
		}
		catch (Exception exc)
		{
			// TODO check handling of exceptions and message
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
	}

	protected void setupUI(String stepUUID)
	{
		removeAllComponents();

		Label header = new Label(I18NHandler.getText(I18NText.TestCaseStep_Caption));
		header.addStyleName(TestbedTheme.LABEL_H1);
		addComponent(header);

		stepSelectionPanel = new StepSelectionPanel(model, I18NHandler.getText(I18NText.TestCaseStep_Selection), null);
		stepSelectionPanel.initializeUI();

		stepDetailsPanel = new StepDetailsPanel(model, I18NHandler.getText(I18NText.TestCaseStep_Details), null, testCaseStepDAO, this);
		stepDetailsPanel.initializeUI();
		stepDetailsPanel.setMinimized(true);

		inboundStepPanel = new StepInboundStepPanel(model);
		inboundStepPanel.initializeUI();
		inboundStepPanel.setVisible(false); // we have no selected inbound/outbound step, so dont show it

		outboundStepPanel = new StepOutboundStepPanel(model);
		outboundStepPanel.initializeUI();
		outboundStepPanel.setVisible(false);


		saveButton = new Button(I18NHandler.getText(I18NText.Button_Save), FontAwesome.SAVE);
		saveButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);
		saveButton.addClickListener(e -> save());
		saveButton.setEnabled(false);

		buttonLayout = new HorizontalLayout(saveButton);
		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

		addComponent(stepSelectionPanel);
		addComponent(stepDetailsPanel);
		addComponent(inboundStepPanel);
		addComponent(outboundStepPanel);
		addComponent(buttonLayout);

		model.addPropertyChangeListener(this);
		model.addPropertyChangeListener(stepSelectionPanel);
		model.addPropertyChangeListener(stepDetailsPanel);
		model.addPropertyChangeListener(inboundStepPanel);
		model.addPropertyChangeListener(outboundStepPanel);
	}

	public void enableSave(boolean enable)
	{
		saveButton.setEnabled(enable);
	}

	private void save()
	{
		if (model.getEditableStep() != null)
		{
			try
			{
				stepDetailsPanel.commitChanges();

				if (model.getEditableStep().getInbound())
				{
					inboundStepPanel.commitChanges();
				}
				else
				{
					outboundStepPanel.commitChanges();
				}

				testCaseController.persistTestCaseStep(model.getEditableStep(), model.oldSuffix());
				Notification.show("Info", I18NHandler.getText(I18NText.View_Notification_saved), Notification.Type.TRAY_NOTIFICATION);
			}
			catch (CommitException e)
			{
				CommitHandler.displayUserErrorMessageInvalidFields(e);
			}
			catch (Exception exc)
			{
				// TODO check handling of exceptions and message
				Log.log().error(Log.getStackTrace(exc));
				ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
			}
		}
	}


}

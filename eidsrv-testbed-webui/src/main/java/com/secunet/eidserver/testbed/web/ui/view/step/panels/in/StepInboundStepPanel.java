package com.secunet.eidserver.testbed.web.ui.view.step.panels.in;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditModel;
import com.secunet.testbedutils.utilities.JaxBUtil;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.VerticalLayout;

public class StepInboundStepPanel extends VerticalLayout implements PropertyChangeListener
{
	/** generated */
	private static final long serialVersionUID = 8929740463837737182L;

	private static final Logger logger = LogManager.getRootLogger();

	protected TeststepEditModel model;
	protected TestCaseStep editing;
	protected Step xmlRepresentation;

	protected StepInboundTokenPanel headerPanel;
	protected StepInboundTokenPanel protocolPanel;


	public StepInboundStepPanel(TeststepEditModel model)
	{
		setWidth(100, Unit.PERCENTAGE);

		this.model = model;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case TeststepEditModel.EDIT:
					editing = (TestCaseStep) evt.getNewValue();
					updateUI();
					break;

				default:
					return; // noi
			}
		}

	}

	private void updateUI()
	{
		if (editing != null && editing.getInbound())
		{
			// TODO build table model and insert it into the table
			try
			{
				xmlRepresentation = JaxBUtil.unmarshal(editing.getMessage(), Step.class);
			}
			catch (Exception e)
			{
				logger.error(e);
			}

			if (xmlRepresentation == null)
				xmlRepresentation = new Step();

			List<StepToken> httpStepToken = xmlRepresentation.getHttpStepToken();
			List<StepToken> protocolStepToken = xmlRepresentation.getProtocolStepToken();

			headerPanel.updateModel(httpStepToken);
			headerPanel.setVisible(true);
			headerPanel.setMinimized(false);


			protocolPanel.updateModel(protocolStepToken);
			protocolPanel.setVisible(true);
			protocolPanel.setMinimized(false);
		}


		setVisible((editing != null && editing.getInbound()));
	}

	public void initializeUI()
	{
		setSpacing(true);

		headerPanel = new StepInboundTokenPanel(I18NHandler.getText(I18NText.TestCaseStep_Header), null);
		headerPanel.initializeUI();
		headerPanel.setMinimized(true);

		protocolPanel = new StepInboundTokenPanel(I18NHandler.getText(I18NText.TestCaseStep_Protocol), null);
		protocolPanel.initializeUI();
		protocolPanel.setMinimized(true);

		addComponent(headerPanel);
		addComponent(protocolPanel);
	}

	public void commitChanges() throws CommitException
	{
		try
		{
			String marshall = JaxBUtil.marshall(xmlRepresentation);
			model.getEditableStep().setMessage(marshall);
		}
		catch (Exception exc)
		{
			throw new CommitException("failed to marshall into xml", exc);
		}
	}
}

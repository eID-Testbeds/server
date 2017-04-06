package com.secunet.eidserver.testbed.web.ui.view.step.panels.out;

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
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditModel;
import com.secunet.testbedutils.utilities.JaxBUtil;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class StepOutboundStepPanel extends VerticalLayout implements PropertyChangeListener
{
	/** generated */
	private static final long serialVersionUID = 8929740463837737182L;

	private static final Logger logger = LogManager.getRootLogger();

	protected TeststepEditModel model;
	protected TestCaseStep editing;
	protected Step xmlRepresentation;

	protected TextArea headerTA;
	protected FormLayout headerForm;
	protected TogglePanelLayout headerPanel;

	protected TextArea protocolTA;
	protected FormLayout protocolForm;
	protected TogglePanelLayout protocolPanel;


	public StepOutboundStepPanel(TeststepEditModel model)
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
		if (editing != null && !editing.getInbound())
		{
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

			if (httpStepToken != null && !httpStepToken.isEmpty())
			{
				try
				{
					updateHeader(httpStepToken.get(0).getValue());
				}
				catch (Exception e)
				{
					// TODO
					logger.error(e);
					updateHeader("");
				}
			}
			else
				updateHeader("");
			headerPanel.setVisible(true);
			headerPanel.setMinimized(false);


			if (protocolStepToken != null && !protocolStepToken.isEmpty())
			{
				try
				{
					updateProtocol(protocolStepToken.get(0).getValue());
				}
				catch (Exception e)
				{
					// TODO
					logger.error(e);
					updateProtocol("");
				}
			}
			else
				updateProtocol("");
			protocolPanel.setVisible(true);
			protocolPanel.setMinimized(false);
		}


		setVisible((editing != null && !editing.getInbound()));
	}

	private void updateProtocol(String marshall)
	{
		protocolTA.setValue(marshall);
	}

	private void updateHeader(String marshall)
	{
		headerTA.setValue(marshall);
	}

	public void initializeUI()
	{
		setSpacing(true);

		headerTA = new TextArea("");
		headerTA.setNullRepresentation("");
		headerTA.setWidth(100, Unit.PERCENTAGE);
		headerForm = new FormLayout(headerTA);
		headerForm.setSpacing(true);
		headerForm.setMargin(true);
		headerForm.setWidth(100, Unit.PERCENTAGE);
		headerPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.TestCaseStep_Header), headerForm);


		protocolTA = new TextArea("");
		protocolTA.setWidth(100, Unit.PERCENTAGE);
		protocolTA.setNullRepresentation("");
		protocolForm = new FormLayout(protocolTA);
		protocolForm.setWidth(100, Unit.PERCENTAGE);
		protocolForm.setSpacing(true);
		protocolForm.setMargin(true);
		protocolPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.TestCaseStep_Protocol), protocolForm);
		protocolPanel.setMinimized(true);

		addComponent(headerPanel);
		addComponent(protocolPanel);
	}

	public void commitChanges() throws CommitException
	{
		// header
		StepToken headToken = new StepToken();
		headToken.setName("header");
		headToken.setValue(headerTA.getValue());
		headToken.setIsMandatory(true);

		xmlRepresentation.getHttpStepToken().clear();
		xmlRepresentation.getHttpStepToken().add(headToken);

		// protocol
		StepToken protToken = new StepToken();
		protToken.setName("message");
		protToken.setValue(protocolTA.getValue());
		headToken.setIsMandatory(false);

		xmlRepresentation.getProtocolStepToken().clear();
		xmlRepresentation.getProtocolStepToken().add(protToken);

		try
		{
			String marshall = JaxBUtil.marshall(xmlRepresentation);
			model.getEditableStep().setMessage(marshall);
		}
		catch (Exception exc)
		{
			throw new CommitException("Failed to marshall step into XML", exc);
		}
	}
}

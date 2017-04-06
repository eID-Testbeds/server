package com.secunet.eidserver.testbed.web.ui.view.testcase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.dao.CopyTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.nav.TestBedView;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.secunet.eidserver.testbed.web.ui.util.NavigatorHelper;
import com.secunet.eidserver.testbed.web.ui.util.ParameterHandler;
import com.secunet.eidserver.testbed.web.ui.view.testcase.panels.TestCaseDetailsPanel;
import com.secunet.eidserver.testbed.web.ui.view.testcase.panels.TestCaseSelectionPanel;
import com.secunet.eidserver.testbed.web.ui.view.testcase.panels.TestCaseStepsSelectionPanel;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@CDIView("testcase")
public class TestCaseEditView extends AbstractTestbedView implements Serializable, View, PropertyChangeListener
{

	@EJB
	private CandidateController candidateController;

	@EJB
	private TestCaseController testCaseController;


	/** Generated */
	private static final long serialVersionUID = -8667982480904738251L;

	@EJB
	private CopyTestCaseDAO copyTestCaseDAO;

	protected TestCaseEditModel model;

	protected TestCaseSelectionPanel selection;
	protected TestCaseDetailsPanel details;
	protected TestCaseStepsSelectionPanel stepsSelection;
	protected Button submitButton;

	@PostConstruct
	public void setupView()
	{
		setSpacing(true);
		setMargin(new MarginInfo(true, true, true, false));
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		try
		{
			Set<TestCandidate> candidates = candidateController.getAllCandidates();
			Set<TestCase> allTestCases = testCaseController.getAllTestCases();
			Set<TestCaseStep> allSteps = testCaseController.getAllTestCaseSteps();

			model = new TestCaseEditModel(allTestCases, candidates, allSteps);
			model.addPropertyChangeListener(this);


			ParameterHandler handler = new ParameterHandler(event.getParameters());
			String testcaseUUID = null;

			if (handler.isEdit() && handler.getAssociatedUUID().isPresent())
				testcaseUUID = handler.getAssociatedUUID().get();


			setupUI(testcaseUUID);
		}
		catch (Exception exc)
		{
			// TODO check handling of exceptions and message
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
	}


	protected void setupUI(String testcaseUUID)
	{
		removeAllComponents();

		Label header = new Label(I18NHandler.getText(I18NText.TestCase_Caption));
		header.addStyleName(TestbedTheme.LABEL_H1);
		addComponent(header);


		selection = new TestCaseSelectionPanel(model, I18NHandler.getText(I18NText.TestCase_Selection), null);
		selection.initializeUI();
		model.addPropertyChangeListener(selection);

		submitButton = new Button(I18NHandler.getText(I18NText.Button_Save));
		submitButton.setIcon(FontAwesome.SAVE);
		submitButton.addClickListener(e -> submit());
		submitButton.setEnabled(false);
		submitButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);

		details = new TestCaseDetailsPanel(copyTestCaseDAO, model, I18NHandler.getText(I18NText.TestCase_Details), null, submitButton);
		details.initializeUI();
		details.setMinimized(true);
		model.addPropertyChangeListener(details);

		stepsSelection = new TestCaseStepsSelectionPanel(model, I18NHandler.getText(I18NText.TestCaseStep_Selection), null);
		stepsSelection.initializeUI();
		stepsSelection.setMinimized(true);
		model.addPropertyChangeListener(stepsSelection);

		addComponent(selection);
		addComponent(details);
		addComponent(stepsSelection);
		addComponent(submitButton);
		setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);

		if (testcaseUUID != null)
		{
			Optional<TestCase> findAny = model.getTestCases().stream().filter(c -> testcaseUUID.equals(c.getId())).findAny();
			if (findAny.isPresent())
				model.setSelectedTestCase(findAny.get());
		}
	}


	protected void submit()
	{
		try
		{
			details.commitChanges();
			stepsSelection.commitChanges();

			testCaseController.persistTestCaseCopy(model.getEditTestCase(), details.isDefault());

			String navString = NavigatorHelper.getNavigationString(TestBedView.TESTCASE, Arrays.asList("edit", model.getEditTestCase().getId()));
			UI.getCurrent().getNavigator().navigateTo(navString);
		}
		catch (CommitException e)
		{
			CommitHandler.displayUserErrorMessageInvalidFields(e);

			String text = I18NHandler.getText(I18NText.Warning_CorrectInvalidInput0, e.getMessage());
			ExceptionWindow.showNewWindow(text, text, e);
		}
		catch (EJBException exc)
		{
			String text = I18NHandler.getText(I18NText.Warning_CorrectInvalidInput0, exc.getMessage());
			ExceptionWindow.showNewWindow(text, text, exc);
		}
		catch (Exception exc)
		{
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_BackendFailed_TryAgainOrContactAdministration), exc);
		}
	}
}

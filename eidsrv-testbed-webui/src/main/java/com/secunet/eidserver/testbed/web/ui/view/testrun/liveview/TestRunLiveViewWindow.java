package com.secunet.eidserver.testbed.web.ui.view.testrun.liveview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJBException;

import com.secunet.eidserver.testbed.common.classes.TestRunModel;
import com.secunet.eidserver.testbed.common.interfaces.beans.RunController;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestRunLiveViewWindow extends Window
{

	static ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue()
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};


	private RunController runController;
	protected TestRunModel runningTestsModel;

	/** generated */
	private static final long serialVersionUID = -2520933361424999914L;


	protected HorizontalLayout mainLayout;
	protected VerticalLayout textLayout;
	protected Button viewTestRunButton;
	protected Label testCandidateLabel;
	protected Label progressText;
	protected ProgressBar progressBar;
	protected PollListener pollListener;


	public TestRunLiveViewWindow(RunController runController)
	{
		this.runController = runController;
	}


	public void initialize(TestRunModel runModel)
	{
		this.runningTestsModel = runModel;

		initUI();

		initPolling();
	}

	protected void initUI()
	{
		// window
		setCaption("Test in progress...");
		setDraggable(true);
		// setClosable(false);
		setResizable(false);


		// components
		mainLayout = new HorizontalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);

		textLayout = new VerticalLayout();
		textLayout.setWidth(100, Unit.PERCENTAGE);
		textLayout.setSpacing(true);

		testCandidateLabel = new Label("Starting test run...");
		progressText = new Label("");
		progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setWidth(100f, Unit.PERCENTAGE);

		viewTestRunButton = new Button(FontAwesome.EYE);
		viewTestRunButton.addClickListener(e -> viewTestrun());

		textLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		textLayout.addComponents(testCandidateLabel, progressText, progressBar, viewTestRunButton);
		textLayout.setComponentAlignment(viewTestRunButton, Alignment.MIDDLE_RIGHT);

		mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(textLayout);

		setContent(mainLayout);

		addCloseListener(e -> stopPolling());
	}

	private void viewTestrun()
	{
		if (runningTestsModel != null)
			UI.getCurrent().getNavigator().navigateTo("execution/edit/" + runningTestsModel.getCandidate().getId());
	}


	protected void initPolling()
	{
		pollListener = new PollListener() {

			private static final long serialVersionUID = -2352234208552588720L;

			@Override
			public void poll(PollEvent event)
			{
				TestRunModel trm =  runController.getTestRunModel(runningTestsModel.getCandidate());
				if(trm != null)
				{
					runningTestsModel = trm;

					updateUI();

					Date finishedDate = runningTestsModel.getFinished();
					if (finishedDate != null)
						handleTestRunningNotRunning();
				}
				else
				{
					handleTestRunningNotRunning();
				}
			}
		};

		UI.getCurrent().addPollListener(pollListener);
		UI.getCurrent().setPollInterval((int) TimeUnit.SECONDS.toMillis(1));
	}


	/**
	 * MOCK METHOD, REMOVE BEFORE PRODUCTION
	 * 
	 * @param runningTestsModel2
	 *            must not be null
	 * @return the same object, but with progressed tests
	 */
	protected TestRunModel mock(TestRunModel runningTestsModel2)
	{
		Random r = new Random();
		Map<TestCase, Integer> progress = runningTestsModel2.getProgress();
		progress.keySet().forEach(tc -> {
			if (progress.get(tc).intValue() < 100)
				progress.put(tc, (progress.get(tc).intValue() + r.nextInt((101 - progress.get(tc).intValue()))));
		});


		boolean ok = true;
		for (Integer v : progress.values())
			ok &= (100 == v.intValue());
		if (ok)
		{
			runningTestsModel2.setFinished(new Date(System.currentTimeMillis()));
		}

		return runningTestsModel2;
	}


	protected void handleTestRunningNotRunning()
	{
		stopPolling();

		// TODO
		// here we could add something that would be a nice animation
		// that will tell the user, that the test case is finished
		String candidateName = runningTestsModel.getCandidate().getCandidateName();
		Date executionStarted = runningTestsModel.getExecutionStarted();
		Notification.show("Test run finished...", "Test for " + candidateName + " (" + format.get().format(executionStarted) + ") finished!", Type.TRAY_NOTIFICATION);

		close();
	}


	public void stopPolling()
	{
		if (pollListener != null)
		{
			UI.getCurrent().removePollListener(pollListener);
			UI.getCurrent().setPollInterval(-1);
			pollListener = null;
		}
	}


	protected void updateUI()
	{
		String candidateName = runningTestsModel.getCandidate().getCandidateName();
		Date executionStarted = runningTestsModel.getExecutionStarted();
		Date finishedDate = runningTestsModel.getFinished();

		if (finishedDate != null)
		{
			progressBar.setValue(1f);
		}
		else
		{
			int maxTestCases = (runningTestsModel == null || runningTestsModel.getProgress() == null) ? 0 : runningTestsModel.getProgress().size();
			int doneTests = (runningTestsModel == null || runningTestsModel.getProgress() == null) ? 0 : getTestsDone(runningTestsModel.getProgress());
			float progress = 0;
			if (maxTestCases > 0 && doneTests > 0)
				progress = doneTests / ((float) maxTestCases);
			else
				progress = 0;

			testCandidateLabel.setValue(I18NHandler.getText(I18NText.TestExecution_Live_Testing0At1, candidateName, format.get().format(executionStarted)));
			progressText.setValue(I18NHandler.getText(I18NText.TestExecution_Live_0of1TestsExecuted, doneTests, maxTestCases));
			if (progress > 0)
				progressBar.setValue(progress);

			progressBar.setIndeterminate(progress > 0);

			setCaption((int) (progress * 100) + " %");
		}
	}


	private int getTestsDone(Map<TestCase, Integer> progress)
	{
		if (progress == null || progress.isEmpty())
			return 0;
		else
			return (int) progress.values().stream().filter(tcProgress -> (tcProgress != null && tcProgress.intValue() >= 100)).count();
	}
}

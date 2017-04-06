package com.secunet.eidserver.testbed.web.ui.view.testrun;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.naming.NamingException;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.classes.TestRunModel;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.exceptions.InvalidObjectException;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.RunController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.comp.NotLazyTreeTable;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.ParameterHandler;
import com.secunet.eidserver.testbed.web.ui.view.testrun.liveview.TestRunLiveViewWindow;
import com.secunet.eidserver.testbed.web.ui.view.testrun.panels.CandidateSelectionPanel;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIView("execution")
public class RunView extends AbstractTestbedView implements View, PropertyChangeListener
{
	@EJB
	private RunController runController;

	@EJB
	private TestCaseController testCaseController;

	@EJB
	private CandidateController candidateController;

	private static final String TABLE_COLUMN_RUN_PROGRESS = "Progress";
	private static final String TALBE_COLUM_RUN_SELECTED = "Selected";
	private static final String TABLE_COLUMN_RUN_NAME = "Name";

	/** generated */
	private static final long serialVersionUID = 7871848055683587281L;


	/**
	 * this is the main model for this view
	 */
	protected RunViewModel model;

	protected Map<Object, TestCase> testSetupSelectionMap;
	protected Map<String, ProgressBar> runningProgressMap;

	protected CandidateSelectionPanel candidateSelectionPanel;
	protected TogglePanelLayout testcaseSelectionPanel;
	protected TogglePanelLayout runningTestcasePanel;
	protected TreeTable treeTable;
	protected int numTreeitems = 20;

	protected Map<Object, String> manualExplanations = new HashMap<>();
	protected Map<Object, String> clientExplanations = new HashMap<>();

	@PostConstruct
	private void setupView()
	{
		setSpacing(true);
		setMargin(new MarginInfo(true, true, false, false));
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		try
		{
			Set<TestCandidate> allCandidates = candidateController.getAllCandidates();
			model = new RunViewModel(allCandidates);
			model.setRootModuleTestCaseNode(null);
			model.addPropertyChangeListener(this);

			Optional<TestCandidate> selectedCandidate = Optional.empty();
			ParameterHandler handler = new ParameterHandler(event.getParameters());

			if (handler.isEdit() && handler.getAssociatedUUID().isPresent())
				selectedCandidate = Optional.of(model.getCandidates().get(handler.getAssociatedUUID().get()));


			if (selectedCandidate.isPresent())
				model.setRootModuleTestCaseNode(testCaseController.getRootTestModule(selectedCandidate.get()));

			setupUI(selectedCandidate);

			// TODO create a way to poll against
		}
		catch (Exception exc)
		{
			// TODO check handling of exceptions and message
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
					exc.getCause().getLocalizedMessage());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName() != null)
		{
			switch (evt.getPropertyName())
			{
				case RunViewModel.CANDIDATES:
					setupUI(Optional.of(model.getSelectedCandidate()));
					break;

				case RunViewModel.ROOT_NODE:
					// displayCandidate((TestCandidate) evt.getNewValue(), true);
					break;

				case RunViewModel.RUNNING_CANDIDATES:
					displayCandidate((TestCandidate) evt.getNewValue(), true);
					break;

				case RunViewModel.SELECTED_CANDIDATE:
					displayCandidate((TestCandidate) evt.getNewValue(), false);
					break;
				default:
					// not supported Event
					return;
			}
		}
	}

	protected void displayCandidate(TestCandidate candidate, boolean updateOnlyIfSelected)
	{
		try
		{
			TestRunModel runModel = runController.getTestRunModel(candidate);
			if (runModel != null)
			{
				model.addRunning(candidate, runModel);
			}

			if (!updateOnlyIfSelected)
			{
				removeCandidateDisplays();
				if (model.isRunning(candidate))
					displayRunning(candidate);
				else
					displayTestcaseSelection(candidate);
			}
			else if (updateOnlyIfSelected && model.isSelected(candidate))
			{
				if (model.isRunning(candidate))
				{
					if (runningTestcasePanel == null)
						Notification.show(I18NHandler.getText(I18NText.View_Run_Notification_IsNowRunning), I18NHandler.getText(I18NText.View_Run_Notification_IsNowRunning),
								Type.ASSISTIVE_NOTIFICATION);

					displayRunning(candidate);
				}
				else
				{
					if (testcaseSelectionPanel == null)
						Notification.show(I18NHandler.getText(I18NText.View_Run_Notification_IsNowRunning), I18NHandler.getText(I18NText.View_Run_Notification_IsNowRunning),
								Type.ASSISTIVE_NOTIFICATION);

					displayTestcaseSelection(candidate);
				}
			}
			// we do not update or display
		}
		catch (Exception exc)
		{
			// TODO check handling of exceptions and message
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
					exc.getCause().getLocalizedMessage());
		}
	}

	protected void displayTestcaseSelection(TestCandidate candidate) throws InvalidObjectException, NamingException
	{
		testSetupSelectionMap = new HashMap<>();

		treeTable = new NotLazyTreeTable();
		treeTable.addContainerProperty(TABLE_COLUMN_RUN_NAME, String.class, null);
		treeTable.addContainerProperty(TALBE_COLUM_RUN_SELECTED, CheckBox.class, true);
		treeTable.setColumnAlignment(TALBE_COLUM_RUN_SELECTED, Align.CENTER);
		treeTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
		treeTable.setCacheRate(2);
		treeTable.setWidth(100, Unit.PERCENTAGE);

		TestModule rootNode = testCaseController.getRootTestModule(candidate);
		rootNode.setName("EIDSERVER");
		model.setRootModuleTestCaseNode(rootNode);

		generateTreeNodes(null, rootNode);
		treeTable.setPageLength(0);

		treeTable.setSortAscending(true);
		treeTable.setSortContainerPropertyId(TABLE_COLUMN_RUN_NAME);
		treeTable.sort();
		treeTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
			private static final long serialVersionUID = -2932815261829056841L;

			@Override
			public String generateDescription(Component source, Object itemId, Object propertyId)
			{
				if (null != itemId)
				{
					if (manualExplanations.containsKey(itemId))
					{
						String data = manualExplanations.get(itemId);
						return data;
					}
					if (clientExplanations.containsKey(itemId))
					{
						String data = clientExplanations.get(itemId);
						return data;

					}
				}
				return "";
			}

		});


		Button engageButton = new Button(I18NHandler.getText(I18NText.Button_StartTestRun));
		engageButton.setIcon(FontAwesome.TRAIN); // TODO icon enum nutzen und erweitern
		engageButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);
		engageButton.addClickListener(e ->

		startSelectedTestCase());

		VerticalLayout layout = new VerticalLayout(treeTable, engageButton);
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setComponentAlignment(engageButton, Alignment.MIDDLE_RIGHT);
		layout.setHeight(100, Unit.PERCENTAGE);

		testcaseSelectionPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.TestCase_Selection), layout);
		testcaseSelectionPanel.setHeight(100, Unit.PERCENTAGE);

		addComponent(testcaseSelectionPanel);
	}

	private void generateTreeNodes(Object parentID, TestModule testModule)
	{
		Object generatedID = generateItemIntoTree(parentID, testModule.getName(), false);

		treeTable.setChildrenAllowed(generatedID,
				(testModule.getSubModules() != null && !testModule.getSubModules().isEmpty()) || (testModule.getTestCases() != null && !testModule.getTestCases().isEmpty()));

		if (testModule.getTestCases() != null)
			testModule.getTestCases().forEach(tc -> {
				// check if the test subject has to be reconfigured
				boolean manual = (null != tc.getManualExplanation() && tc.getManualExplanation().length() > 0);
				boolean singleClient = (!model.getSelectedCandidate().isMultiClientCapable() && tc.getEservice() != GeneralConstants.DEFAULT_ESERVICE);
				Object tcObjTreeID = generateItemIntoTree(generatedID, tc.getName(), manual | singleClient);
				if (manual)
				{
					manualExplanations.put(tcObjTreeID, tc.getManualExplanation());
				}
				if (singleClient)
				{
					clientExplanations.put(tcObjTreeID, ((tc.getClientExplanation() != null) ? tc.getClientExplanation() : ""));
				}

				treeTable.setChildrenAllowed(tcObjTreeID, false);
				testSetupSelectionMap.put(tcObjTreeID, tc);
			});

		if (testModule.getSubModules() != null)
			testModule.getSubModules().forEach(subModule -> generateTreeNodes(generatedID, subModule));
	}

	/**
	 * 
	 * @return the item id of the item in the tree
	 */
	@SuppressWarnings("unchecked")
	private Object generateItemIntoTree(Object parentID, String name, boolean manual)
	{
		Object generatedID = treeTable.addItem();
		Item item = treeTable.getItem(generatedID);
		treeTable.setCollapsed(generatedID, (parentID != null) ? true : false);

		item.getItemProperty(TABLE_COLUMN_RUN_NAME).setValue(name);

		CheckBox checkBox = new CheckBox("", !manual);
		item.getItemProperty(TALBE_COLUM_RUN_SELECTED).setValue(checkBox);
		checkBox.addValueChangeListener(e -> selectNodeAndSubTree(generatedID, (Boolean) e.getProperty().getValue()));

		if (parentID != null)
			treeTable.setParent(generatedID, parentID);

		return generatedID;
	}

	protected void startSelectedTestCase()
	{
		try
		{
			// get all testcases that are selected
			Set<String> selectedTestCaseUUIDs = testSetupSelectionMap.keySet().stream()
					.filter(objID -> ((CheckBox) treeTable.getItem(objID).getItemProperty(TALBE_COLUM_RUN_SELECTED).getValue()).getValue().booleanValue())
					.map(e -> testSetupSelectionMap.get(e).getId()).collect(Collectors.toSet());

			Set<TestCase> allTestCases = testCaseController.getAllTestCases();
			Set<TestCase> testCases = allTestCases.stream().filter(tc -> selectedTestCaseUUIDs.contains(tc.getId())).collect(Collectors.toSet());

			if (testCases.isEmpty())
			{
				Notification.show(I18NHandler.getText(I18NText.TestExecution_NoTestCaseSelectedNotification), Type.TRAY_NOTIFICATION);

				return;
			}

			Map<TestCase, Integer> testCaseProgressMap = new HashMap<>();
			testCases.forEach(tc -> testCaseProgressMap.put(tc, 0));

			TestRunModel startThisRunModel = new TestRunModel();
			startThisRunModel.setCandidate(model.getSelectedCandidate());
			startThisRunModel.setProgress(testCaseProgressMap);

			runController.startTestrun(startThisRunModel);
			startThisRunModel.setExecutionStarted(new Date(System.currentTimeMillis()));

			TestRunLiveViewWindow liveViewWindow = new TestRunLiveViewWindow(runController);
			liveViewWindow.initialize(startThisRunModel);
			UI.getCurrent().addWindow(liveViewWindow);
			liveViewWindow.center();
			liveViewWindow.setImmediate(true);
			liveViewWindow.setResponsive(true);
			UI.getCurrent().setResponsive(true);
			UI.getCurrent().setImmediate(true);
			liveViewWindow.setPosition(-1, 5);
		}
		catch (Exception exc)
		{
			// TODO check handling of exceptions and message
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
	}

	private void selectNodeAndSubTree(Object nodeID, Boolean value)
	{
		((CheckBox) treeTable.getItem(nodeID).getItemProperty(TALBE_COLUM_RUN_SELECTED).getValue()).setValue(value);

		if (treeTable.hasChildren(nodeID))
		{
			Collection<?> childrenIDs = treeTable.getChildren(nodeID);
			if (childrenIDs != null)
				childrenIDs.forEach(childID -> selectNodeAndSubTree(childID, value));
		}
	}

	/**
	 * RUNNING
	 * 
	 * @throws NamingException
	 * @throws InvalidObjectException
	 * 
	 */
	protected void displayRunning(TestCandidate candidate) throws InvalidObjectException, NamingException
	{
		runningProgressMap = new HashMap<>();

		TreeTable treeTable = new TreeTable();
		treeTable.addContainerProperty(TABLE_COLUMN_RUN_NAME, String.class, null);
		treeTable.addContainerProperty(TABLE_COLUMN_RUN_PROGRESS, ProgressBar.class, null);
		treeTable.setColumnAlignment(TABLE_COLUMN_RUN_PROGRESS, Align.CENTER);
		treeTable.setColumnExpandRatio(TABLE_COLUMN_RUN_NAME, 1);
		treeTable.setWidth(100, Unit.PERCENTAGE);
		treeTable.setSelectable(true);
		treeTable.setCacheRate(3);
		treeTable.setPageLength(20);
		treeTable.setAnimationsEnabled(true);


		TestModule rootNode = testCaseController.getRootTestModule(candidate);
		model.setRootModuleTestCaseNode(rootNode);

		ProgressBar pg = new ProgressBar(0f);
		runningProgressMap.put(rootNode.getName(), pg);
		treeTable.addItem(new Object[] { rootNode.getName(), pg }, rootNode);
		treeTable.setCollapsed(rootNode, false);
		addRunningChildrenIntoTree(treeTable, rootNode);

		runningTestcasePanel = new TogglePanelLayout("res:todo", treeTable);
		addComponent(runningTestcasePanel);

		updateProgressForTestModules();
	}

	protected void updateProgressForTestModules()
	{
		TestModule rootNode = model.getRootModuleTestCaseNode();

		updateProgressRecursivly(rootNode);
	}

	private Float updateProgressRecursivly(TestModule node)
	{
		float childrenCount = node.getSubModules().size() + node.getTestCases().size();
		int progressChildren = 0;

		// if we are a leaf, update test cases
		for (TestCase tc : node.getTestCases())
		{
			int intValue = model.getRunningCandidates().get(model.getSelectedCandidate().getId()).getProgress().get(tc).intValue();
			ProgressBar progressBar = runningProgressMap.get(tc.getName());
			progressBar.setValue(intValue / 100f);
			progressBar.setCaption(progressBar.getValue().toString());
			progressBar.setDescription(progressBar.getValue().toString());
			progressChildren += intValue;
		}

		// if we have child modules, update them
		for (TestModule child : node.getSubModules())
		{
			Float progressRecursivly = updateProgressRecursivly(child);
			progressChildren += (int) (progressRecursivly.floatValue());
		}

		// Update UI
		final float actualProgress = progressChildren / childrenCount;
		ProgressBar progressBar = runningProgressMap.get(node.getName());
		progressBar.setValue(actualProgress / 100f);
		progressBar.setCaption(progressBar.getValue().toString());

		return actualProgress;
	}

	protected void addRunningChildrenIntoTree(TreeTable treeTable, TestModule parentNode)
	{
		for (TestModule childModule : parentNode.getSubModules())
		{
			ProgressBar pg = new ProgressBar(0f);
			runningProgressMap.put(childModule.getName(), pg);

			treeTable.addItem(new Object[] { childModule.getName(), pg }, childModule);
			treeTable.setParent(childModule, parentNode);
			treeTable.setCollapsed(childModule, false);

			addRunningChildrenIntoTree(treeTable, childModule);
		}

		for (TestCase childCase : parentNode.getTestCases())
		{
			TestRunModel testRunModel = model.getRunningCandidates().get(model.getSelectedCandidate().getId());
			Integer progress = testRunModel.getProgress().get(childCase);
			float progressFloat = progress.intValue() / 100f;
			ProgressBar pg = new ProgressBar(progressFloat);
			pg.setCaption("" + progressFloat);
			runningProgressMap.put(childCase.getName(), pg);

			treeTable.addItem(new Object[] { childCase.getName(), pg }, childCase);
			treeTable.setParent(childCase, parentNode);
			treeTable.setChildrenAllowed(childCase, false);
		}

		if (parentNode.getSubModules().isEmpty())
			treeTable.setChildrenAllowed(parentNode, (!parentNode.getSubModules().isEmpty() || !parentNode.getTestCases().isEmpty()));
	}


	protected void removeCandidateDisplays()
	{
		if (testcaseSelectionPanel != null)
		{
			removeComponent(testcaseSelectionPanel);
			testcaseSelectionPanel = null;
		}
		if (runningTestcasePanel != null)
		{
			removeComponent(runningTestcasePanel);
			runningTestcasePanel = null;
		}
	}

	protected void setupUI(Optional<TestCandidate> selectedCandidate)
	{
		removeAllComponents();

		Label header = new Label(I18NHandler.getText(I18NText.TestExecution_Header));
		header.addStyleName(TestbedTheme.LABEL_H1);
		addComponent(header);


		candidateSelectionPanel = new CandidateSelectionPanel(model, I18NHandler.getText(I18NText.View_Run_CandidateSelection), null);
		candidateSelectionPanel.initializeUI();

		addComponent(candidateSelectionPanel);

		if (selectedCandidate.isPresent() && model.getCandidates().containsValue(selectedCandidate))
		{
			candidateSelectionPanel.select(selectedCandidate.get());
		}
		else if (model.getCandidates().size() > 0)
		{
			candidateSelectionPanel.select(model.getCandidates().values().iterator().next());
		}

	}


}

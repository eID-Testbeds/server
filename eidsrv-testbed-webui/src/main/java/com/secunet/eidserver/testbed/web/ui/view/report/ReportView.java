package com.secunet.eidserver.testbed.web.ui.view.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.naming.NamingException;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.classes.TestModule;
import com.secunet.eidserver.testbed.common.classes.TestReportModel;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportController;
import com.secunet.eidserver.testbed.common.interfaces.beans.RunController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestCaseController;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.comp.NotLazyTreeTable;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@CDIView("reports")
public class ReportView extends AbstractTestbedView implements PropertyChangeListener, View
{

	@EJB
	private CandidateController candidateController;

	@EJB
	private TestCaseController testCaseController;

	@EJB
	private RunController runController;

	@EJB
	private ReportController reportController;

	private static final String COLUMN_SELECTION = "Selection";
	private static final String COLUMN_STATE = "State";
	private static final String COLUMN_NAME = "Name";

	private Map<String, String> descriptionTable;


	/** Generated */
	private static final long serialVersionUID = 8828900870597620620L;


	protected ReportModel model;


	protected ComboBox comboboxCandidates;
	protected ComboBox comboboxRuns;
	protected TogglePanelLayout selectionPanel;

	protected TogglePanelLayout reportParamPanel;
	protected TreeTable reportTreeTable;
	protected Map<String, LogMessage> reportTreeEntryIDToLogMessageMap;

	protected Window reportLogMessageDetailsWindow;
	protected TextArea detailsWindowTextArea;

	@PostConstruct
	public void setupView()
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

			model = new ReportModel(allCandidates);
			model.addPropertyChangeListener(this);

			setupUI(null, null);
		}
		catch (Exception exc)
		{
			// TODO backend exceptions
			// TODO rest "software runtime exceptions"
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
					exc.getCause().getLocalizedMessage());
		}
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case ReportModel.PROPERTY_CANDIDATE_SELECTION:
					updateModel();
					TestCandidate candidate = (TestCandidate) evt.getNewValue();
					updateSelectionRunTestcase(reportController.getTestReportModels(candidate));
					break;
				case ReportModel.PROPERTY_TESTRUN_SELECTION:
					updateSelectionReport((TestReportModel) evt.getNewValue());
					break;

				default:
					return; // noi
			}
		}

	}


	protected void updateModel()
	{
		TestCandidate selectedCandidate = model.getSelectedCandidate();

		try
		{
			final TestModule rootModel = testCaseController.getRootTestModule(selectedCandidate);
			model.setRootTestSetup(rootModel);
		}
		catch (Exception exc)
		{
			// TODO backend exceptions
			// TODO rest "software runtime exceptions"
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
					exc.getCause().getLocalizedMessage());
		}
	}


	private void setupUI(TestCandidate candidate, Object run) throws NamingException
	{
		removeAllComponents();

		showMainCaption();
		showSelectionPanel(candidate, run);

		if (candidate == null && model.getCandidates() != null && !model.getCandidates().isEmpty())
		{
			comboboxCandidates.select(model.getCandidates().iterator().next());
		}
	}

	private void showMainCaption()
	{
		Label header = new Label(I18NHandler.getText(I18NText.Reports));
		header.setStyleName(TestbedTheme.LABEL_H1);
		addComponent(header);
	}

	private void showSelectionPanel(TestCandidate candidate, Object run) throws NamingException
	{
		Collection<TestCandidate> candidates = model.getCandidates();
		comboboxCandidates = new ComboBox(I18NHandler.getText(I18NText.View_TestCandidate_CandidateSelection), candidates);
		comboboxCandidates.setFilteringMode(FilteringMode.CONTAINS);
		candidates.forEach(tp -> comboboxCandidates.setItemCaption(tp, Humanizer.getCaption(tp)));
		comboboxCandidates.setWidth(100, Unit.PERCENTAGE);
		comboboxCandidates.setNullSelectionAllowed(false);
		if (candidate != null)
			comboboxCandidates.select(candidates);
		comboboxCandidates.addValueChangeListener(e -> model.setSelectedCandidate(((TestCandidate) e.getProperty().getValue())));

		Collection<TestReportModel> runs = new ArrayList<>();
		if (null != candidate)
		{
			runs.addAll(reportController.getTestReportModels(candidate));
		}

		comboboxRuns = new ComboBox(I18NHandler.getText(I18NText.Reports_Runs), runs);
		comboboxRuns.setFilteringMode(FilteringMode.CONTAINS);
		updateSelectionRunTestcase(runs);
		comboboxRuns.setWidth(100, Unit.PERCENTAGE);
		comboboxRuns.setNullSelectionAllowed(false);
		if (run != null)
			comboboxRuns.select(runs);
		comboboxRuns.setVisible(!runs.isEmpty());
		comboboxRuns.addValueChangeListener(e -> model.setSelectedTestReport((TestReportModel) e.getProperty().getValue()));


		FormLayout form = new FormLayout(comboboxCandidates, comboboxRuns);
		form.setWidth(100, Unit.PERCENTAGE);
		form.setSpacing(true);
		form.setMargin(true);

		selectionPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.Reports_SelectionPanelCaption), form);
		addComponent(selectionPanel);
	}

	private void updateSelectionRunTestcase(Collection<TestReportModel> runs)
	{
		try
		{
			if (runs.isEmpty())
			{
				Notification.show("No reports available!"); // TODO I18N
			}
			else
			{
				comboboxRuns.setVisible(!runs.isEmpty());

				comboboxRuns.setContainerDataSource(new ListContainer<>(runs));
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm", I18NHandler.getLocale());
				TestReportModel latest = null;
				for (TestReportModel r : runs)
				{
					comboboxRuns.setItemCaption(r, (sdf.format(r.getFinished()) + " (Tests: " + r.getSuccessMapping().size() + ")"));
					if (latest == null || r.getFinished().after(latest.getFinished()))
					{
						latest = r;
					}
				}
				comboboxRuns.select(latest);
			}
		}
		catch (Exception exc)
		{
			// TODO backend exceptions
			// TODO rest "software runtime exceptions"
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
					exc.getCause().getLocalizedMessage());
		}
	}

	private void updateSelectionReport(TestReportModel selectedReport)
	{
		// remove old report display
		if (reportParamPanel != null)
			removeComponent(reportParamPanel);

		if (selectedReport == null)
			return;


		// show new display
		reportTreeTable = new NotLazyTreeTable();
		reportTreeTable.addContainerProperty(COLUMN_NAME, String.class, null);
		reportTreeTable.addContainerProperty(COLUMN_STATE, Label.class, null);
		reportTreeTable.addContainerProperty(COLUMN_SELECTION, CheckBox.class, null);
		reportTreeTable.setColumnAlignment(COLUMN_STATE, Align.CENTER);
		reportTreeTable.setColumnAlignment(COLUMN_SELECTION, Align.CENTER);
		reportTreeTable.setColumnExpandRatio(COLUMN_NAME, 1);
		reportTreeTable.setWidth(100, Unit.PERCENTAGE);
		reportTreeTable.setSelectable(true);
		reportTreeTable.setCacheRate(3);
		reportTreeTable.setPageLength(0);
		reportTreeTable.setAnimationsEnabled(true);
		reportTreeTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
			private static final long serialVersionUID = -345152775114348672L;

			@Override
			public String generateDescription(Component source, Object itemId, Object propertyId)
			{
				if (null != itemId && descriptionTable.containsKey(itemId))
				{
					String data = descriptionTable.get(itemId);
					return data;
				}
				return "";
			}

		});
		reportTreeTable.addItemClickListener(evt -> reportRowSelected(evt));

		// populate the tree and sort by name
		populateTree(selectedReport);
		reportTreeTable.setSortContainerPropertyId(COLUMN_NAME);
		reportTreeTable.sort();


		// display buttons for file generation
		Button downloadReportButton = new Button(I18NHandler.getText(I18NText.Reports_DownloadReport));
		downloadReportButton.setEnabled(false);
		downloadReportButton.setDescription(I18NHandler.getText(I18NText.Reports_DownloadReport));
		downloadReportButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);
		downloadReportButton.setIcon(FontAwesome.DOWNLOAD);
		StreamResource streamResource = new StreamResource(new StreamSource() {
			private static final long serialVersionUID = -7308680845393515801L;

			@Override
			public InputStream getStream()
			{
				try
				{
					// TODO only one test run and only selected items!
					String report = reportController.createReport(selectedReport.getCandidate().getId(), 1);
					byte[] decode = Base64.getDecoder().decode(report.getBytes());
					return new ByteArrayInputStream(decode);
				}
				catch (Exception exc)
				{
					// TODO check if handling ok
					Log.log().error(Log.getStackTrace(exc));
					// TODO check for message
					ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage),
							exc.getCause().getLocalizedMessage());
					return null;
				}
			}
		}, "report.pdf");
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadReportButton);


		detailsWindowTextArea = new TextArea(null, "");
		detailsWindowTextArea.setWidth(100, Unit.PERCENTAGE);
		detailsWindowTextArea.setVisible(false);
		detailsWindowTextArea.setRows(10);

		// layout
		VerticalLayout reportLayout = new VerticalLayout(detailsWindowTextArea, reportTreeTable, downloadReportButton);
		reportLayout.setSizeFull();
		reportLayout.setSpacing(true);
		reportLayout.setMargin(true);
		reportLayout.setComponentAlignment(downloadReportButton, Alignment.MIDDLE_RIGHT);

		reportParamPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.Reports), reportLayout);
		addComponent(reportParamPanel);
	}

	private void reportRowSelected(ItemClickEvent evt)
	{
		if (descriptionTable.containsKey(evt.getItemId()))
		{
			if (evt.isDoubleClick())
				detailsWindowTextArea.setVisible(!detailsWindowTextArea.isVisible());

			String text = descriptionTable.get(evt.getItemId());
			detailsWindowTextArea.setValue(text);
			int len = text.split(System.getProperty("line.separator")).length;
			if (len > 20)
			{
				detailsWindowTextArea.setRows(len / 2);
			}
			else
			{
				detailsWindowTextArea.setRows(10);
			}
		}
	}


	@SuppressWarnings("unchecked")
	protected void populateTree(TestReportModel selectedReport)
	{
		Map<String, List<LogMessage>> successMapping = selectedReport.getSuccessMapping();
		descriptionTable = new HashMap<>();

		// now build the tree bottom to top, where the teststeps are the leafes
		// owned by testcases
		for (String childCase : successMapping.keySet())
		{
			String childCaseItemID = getItemID(childCase, null);
			Item itemTestCase = reportTreeTable.addItem(childCaseItemID);

			Label labelChild = new Label();
			CheckBox cbChild = new CheckBox("", true);
			cbChild.addValueChangeListener(e -> selectNodeAndSubTree(itemTestCase, childCaseItemID, (Boolean) e.getProperty().getValue()));

			itemTestCase.getItemProperty(COLUMN_NAME).setValue(childCase);
			itemTestCase.getItemProperty(COLUMN_STATE).setValue(labelChild);
			itemTestCase.getItemProperty(COLUMN_SELECTION).setValue(cbChild);

			reportTreeTable.setCollapsed(childCaseItemID, true);

			boolean allOk = true;
			int stepIndex = 1;
			for (LogMessage logMsg : successMapping.get(childCase))
			{
				String stepItemID = getItemID(childCase, logMsg.getTestStepName());
				Label labelStep = new Label();
				if (logMsg.getSuccess())
				{
					labelStep.addStyleName(TestbedTheme.LABEL_SUCCESS);
				}
				else
				{
					labelStep.addStyleName(TestbedTheme.LABEL_FAILURE);
					allOk = false;
				}

				Item itemStep = reportTreeTable.addItem(stepItemID);
				itemStep.getItemProperty(COLUMN_NAME).setValue(getDoubleDigitIndex(stepIndex++) + ". " + logMsg.getTestStepName());
				itemStep.getItemProperty(COLUMN_STATE).setValue(labelStep);
				itemStep.getItemProperty(COLUMN_SELECTION).setValue(new CheckBox("", true));
				reportTreeTable.setParent(stepItemID, childCaseItemID);
				reportTreeTable.setChildrenAllowed(stepItemID, false);

				descriptionTable.put(stepItemID, logMsg.toString());
			}

			if (allOk)
				((Label) itemTestCase.getItemProperty(COLUMN_STATE).getValue()).addStyleName(TestbedTheme.LABEL_SUCCESS);
			else
				((Label) itemTestCase.getItemProperty(COLUMN_STATE).getValue()).addStyleName(TestbedTheme.LABEL_FAILURE);
		}
	}

	private String getDoubleDigitIndex(int number)
	{
		if (number > 9 || number < -9)
		{
			return number + "";
		}
		else
		{
			if (number >= 0)
			{
				return "0" + number;
			}
			else
			{
				String negative = "" + number;
				return "-0" + negative.substring(1);
			}
		}
	}


	private String getItemID(String childCase, String stepName)
	{
		StringBuilder strBldr = new StringBuilder();
		strBldr.append("childCase=");
		if (childCase != null)
			strBldr.append(childCase);
		strBldr.append("step=");
		if (stepName != null)
			strBldr.append(stepName);
		strBldr.append("_");
		strBldr.append(new Random().nextInt(4242));

		return strBldr.toString();
	}


	private void selectNodeAndSubTree(Item itemM, Object actualTreeItem, Boolean value)
	{
		((CheckBox) itemM.getItemProperty(COLUMN_SELECTION).getValue()).setValue(value);


		Collection<?> children = reportTreeTable.getChildren(actualTreeItem);
		if (children != null && !children.isEmpty())
		{
			children.forEach(child -> selectNodeAndSubTree(reportTreeTable.getItem(child), null, value));
		}
	}

}

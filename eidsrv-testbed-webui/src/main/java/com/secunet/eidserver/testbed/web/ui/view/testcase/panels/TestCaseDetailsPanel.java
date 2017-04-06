package com.secunet.eidserver.testbed.web.ui.view.testcase.panels;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.dao.CopyTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.StringMapField;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.secunet.eidserver.testbed.web.ui.view.testcase.TestCaseEditModel;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

public class TestCaseDetailsPanel extends TogglePanelLayout implements PropertyChangeListener
{
	private CopyTestCaseDAO copyTestCaseDAO;

	private static final long serialVersionUID = 1L;

	protected TestCaseEditModel model;
	protected TestCase selectedTestCase;
	protected CopyTestCase editTestCase;

	protected FormLayout layout;
	protected CheckBox isDefault;
	@PropertyId("name")
	protected TextField oldTestcaseName;
	@PropertyId("card")
	protected ComboBox card;
	@PropertyId("eservice")
	protected ComboBox eservice;
	@PropertyId("suffix")
	protected TextField suffix;
	@PropertyId("description")
	protected TextArea description;
	@PropertyId("manualExplanation")
	protected TextArea manualExplanation;
	@PropertyId("clientExplanation")
	protected TextArea clientExplanation;
	@PropertyId("candidates")
	protected TwinColSelect selectedCandidates;
	@PropertyId("variables")
	protected StringMapField variablesField;

	protected BeanFieldGroup<CopyTestCase> fieldGroup;

	protected Button submitButton;


	public TestCaseDetailsPanel(CopyTestCaseDAO copyTestCaseDAO, TestCaseEditModel model, String caption, Resource icon, Button submitButton)
	{
		super(caption, icon);

		this.copyTestCaseDAO = copyTestCaseDAO;
		this.model = model;

		this.submitButton = submitButton;
	}

	public void initializeUI()
	{
		setMinimized(true);


		layout = new FormLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setWidth(100, Unit.PERCENTAGE);

		oldTestcaseName = new TextField(I18NHandler.getText(I18NText.TestCase_isDefault));
		oldTestcaseName.setEnabled(false);
		oldTestcaseName.setWidth(100, Unit.PERCENTAGE);
		oldTestcaseName.setNullRepresentation("");
		oldTestcaseName.setReadOnly(true);

		isDefault = new CheckBox(I18NHandler.getText(I18NText.TestCase_isDefault));
		isDefault.setEnabled(false);
		suffix = new TextField(I18NHandler.getText(I18NText.TestCase_suffix));
		suffix.setWidth(100, Unit.PERCENTAGE);
		suffix.setRequired(true);
		suffix.setNullRepresentation("");
		suffix.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1484764597270042772L;

			@Override
			public void textChange(TextChangeEvent event)
			{
				submitButton.setEnabled(event.getText().length() > 0);
			}
		});
		description = new TextArea(I18NHandler.getText(I18NText.TestCase_description));
		description.setWidth(100, Unit.PERCENTAGE);
		description.setNullRepresentation("");

		card = new ComboBox(I18NHandler.getText(I18NText.TestCase_card));
		card.setWidth(100, Unit.PERCENTAGE);
		card.setTextInputAllowed(false);
		for (EidCard c : EidCard.values())
		{
			card.addItem(c);
		}

		eservice = new ComboBox(I18NHandler.getText(I18NText.TestCase_eservice));
		eservice.setWidth(100, Unit.PERCENTAGE);
		eservice.setTextInputAllowed(false);
		for (EService s : EService.values())
		{
			eservice.addItem(s);
		}

		manualExplanation = new TextArea(I18NHandler.getText(I18NText.TestCase_Manual));
		manualExplanation.setWidth(100, Unit.PERCENTAGE);
		manualExplanation.setNullRepresentation("");
		manualExplanation.setDescription(I18NHandler.getText(I18NText.TestCase_ManualDescription));

		clientExplanation = new TextArea(I18NHandler.getText(I18NText.TestCase_SingleClient));
		clientExplanation.setWidth(100, Unit.PERCENTAGE);
		clientExplanation.setNullRepresentation("");
		clientExplanation.setDescription(I18NHandler.getText(I18NText.TestCase_SingleClientDescription));

		List<TestCandidate> candidates = model.getCandidatesSorted();
		selectedCandidates = new TwinColSelect(I18NHandler.getText(I18NText.TestCase_SelectedCandidates), candidates);
		selectedCandidates.setWidth(100, Unit.PERCENTAGE);

		Converter<Object, Object> converter = getConverter();
		selectedCandidates.setConverter(converter);
		candidates.forEach(tc -> selectedCandidates.setItemCaption(tc, Humanizer.getCaption(tc)));

		variablesField = new StringMapField(null, "Variables", null);

		layout.addComponent(oldTestcaseName);
		layout.addComponent(isDefault);
		layout.addComponent(suffix);
		layout.addComponent(description);
		layout.addComponent(card);
		layout.addComponent(eservice);
		layout.addComponent(manualExplanation);
		layout.addComponent(clientExplanation);
		layout.addComponent(selectedCandidates);
		layout.addComponent(variablesField);


		setContent(layout);

		fieldGroup = new BeanFieldGroup<>(CopyTestCase.class);
		fieldGroup.bindMemberFields(this);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == model)
		{
			switch (evt.getPropertyName())
			{
				case TestCaseEditModel.PROPERTY_SELECTED_TESTCASE:
					selectedTestCase = (TestCase) evt.getNewValue();
					updateUI();
					break;

				default:
					return; // noi
			}
		}
	}

	protected void updateUI()
	{
		boolean wasCopy = false;
		if (selectedTestCase != null)
		{
			if (selectedTestCase instanceof DefaultTestCase)
			{
				CopyTestCase copy = copyTestCaseDAO.createNew();
				copy.setCandidates(selectedTestCase.getCandidates());
				copy.setDescription(selectedTestCase.getDescription());
				copy.setName(selectedTestCase.getName());
				copy.setCertificateBaseNames(new ArrayList<>(selectedTestCase.getCertificateBaseNames()));
				copy.setDefaultTestcase((DefaultTestCase) selectedTestCase);
				copy.setSuffix(null);
				copy.setTestCaseSteps(new ArrayList<>(selectedTestCase.getTestCaseSteps()));
				copy.setVariables(selectedTestCase.getVariables());
				copy.setManualExplanation(selectedTestCase.getManualExplanation());
				copy.setMandatoryProfiles(selectedTestCase.getMandatoryProfiles());
				copy.setOptionalProfiles(selectedTestCase.getOptionalProfiles());
				copy.setCard(selectedTestCase.getCard());
				copy.setEservice(selectedTestCase.getEservice());
				copy.setClientExplanation(selectedTestCase.getClientExplanation());
				copy.setModule(selectedTestCase.getModule());

				editTestCase = copy;
				isDefault.setValue(true);
			}
			else
			{
				submitButton.setEnabled(true);
				editTestCase = (CopyTestCase) selectedTestCase;
				isDefault.setValue(false);
				wasCopy = true;
			}
		}

		setMinimized(editTestCase == null);
		fieldGroup.setItemDataSource(editTestCase);

		model.setEditTestCase(editTestCase);

		suffix.setReadOnly(wasCopy);
		oldTestcaseName.setReadOnly(true);
	}


	public boolean isDefault()
	{
		return isDefault.getValue();
	}

	public void commitChanges() throws CommitException
	{
		fieldGroup.commit();

		CommitHandler.clearUserError(fieldGroup.getFields());
	}


	private Converter<Object, Object> getConverter()
	{
		return new Converter<Object, Object>() {

			private static final long serialVersionUID = 1L;


			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			// public List<?> convertToModel(Object value, Class<? extends List<?>> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
			public Object convertToModel(Object value, Class<? extends Object> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
			{
				return new ArrayList((Set) value);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Object convertToPresentation(Object value, Class<? extends Object> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
			{

				return new HashSet((List<?>) value);
			}

			@Override
			public Class<Object> getModelType()
			{
				return Object.class;
			}

			@Override
			public Class<Object> getPresentationType()
			{
				return Object.class;
			}
		};
	}
}

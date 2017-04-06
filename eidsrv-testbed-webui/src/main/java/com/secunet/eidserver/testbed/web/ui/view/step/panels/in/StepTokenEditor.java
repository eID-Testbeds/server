package com.secunet.eidserver.testbed.web.ui.view.step.panels.in;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.dao.GenericDAO;
import com.secunet.eidserver.testbed.common.types.testcase.Attribute;
import com.secunet.eidserver.testbed.common.types.testcase.Attributes;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.i18n.Icon;
import com.secunet.eidserver.testbed.web.ui.comp.CollectionComponentField;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class StepTokenEditor extends Window
{

	/** generated */
	private static final long serialVersionUID = 9019067269178568182L;


	@FunctionalInterface
	public static interface Callback extends Serializable
	{
		public abstract void submit(StepToken token);
	}


	// model data
	protected StepToken token;
	protected BeanFieldGroup<StepToken> fieldGroup;
	protected Callback callback;
	protected BeanFieldGroup<Attribute> fieldGroupAttributes;

	// ui
	protected VerticalLayout mainLayout;
	protected HorizontalLayout buttonLayout;
	protected FormLayout formLayout;
	protected FormLayout attributeForm;
	protected Button acceptButton;
	protected Button cancelButton;

	protected CollectionComponentField<List<Attribute>, Attribute> attributesField;


	public StepTokenEditor(StepToken token, Callback callback)
	{
		this.token = token;
		this.callback = callback;

		setResizable(true);
		setCaption(I18NHandler.getText(I18NText.TestCaseStep_Token_EditorCaption));
		setClosable(false);
		setModal(true);

		// generate field for basic inputs
		fieldGroup = new BeanFieldGroup<>(StepToken.class);
		TextField parentNameField = (TextField) fieldGroup.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_ParentName), "parentName");
		parentNameField.setWidth(100, Unit.PERCENTAGE);
		parentNameField.setRequired(false);
		parentNameField.setNullRepresentation("");
		TextField nameField = (TextField) fieldGroup.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_Name), "name");
		nameField.setWidth(100, Unit.PERCENTAGE);
		nameField.setRequired(true);
		nameField.setNullRepresentation("");
		TextField valueField = (TextField) fieldGroup.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_Value), "value");
		valueField.setWidth(100, Unit.PERCENTAGE);
		valueField.setRequired(true);
		valueField.setNullRepresentation("");
		TextField maxNumberOfOccurencesField = (TextField) fieldGroup.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_maxNumberOfOccurences), "maxNumberOfOccurences");
		maxNumberOfOccurencesField.setWidth(100, Unit.PERCENTAGE);
		maxNumberOfOccurencesField.setNullRepresentation("");
		Field<?> isMandatoryField = fieldGroup.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_isMandatory), "isMandatory");
		isMandatoryField.setWidth(100, Unit.PERCENTAGE);
		fieldGroup.setItemDataSource(this.token);

		// generate complex attribute field
		if (token.getAttributes() == null)
			token.setAttributes(new Attributes());
		fieldGroupAttributes = new BeanFieldGroup<Attribute>(Attribute.class);
		TextField attrNameField = (TextField) fieldGroupAttributes.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_Attributes_Name), "name");
		attrNameField.setNullRepresentation("");
		attrNameField.setWidth(100, Unit.PERCENTAGE);
		TextField attrValueField = (TextField) fieldGroupAttributes.buildAndBind(I18NHandler.getText(I18NText.TestCaseStep_Token_Attributes_Value), "value");
		attrValueField.setWidth(100, Unit.PERCENTAGE);
		attrValueField.setNullRepresentation("");
		attributeForm = new FormLayout(attrNameField, attrValueField);
		attributeForm.setSpacing(true);

		String caption = I18NHandler.getText(I18NText.TestCaseStep_Token_Attributes);
		attributesField = new CollectionComponentField<>(caption, token.getAttributes().getAttribute(), null, attributeForm, fieldGroupAttributes, Attribute.class, createDummyDAO);
		attributesField.setCaption(caption);
		attributesField.setCaptionConverter(a -> a.getName() + "= " + a.getValue());
		attributesField.setShowFormAlways(true);
		attributesField.setPageSize(3);
		attributesField.getInnerLayout().setMargin(false);


		// create buttons
		cancelButton = new Button(I18NHandler.getText(I18NText.Button_Cancel), Icon.CANCEL.getIcon());
		cancelButton.addClickListener(e -> cancel());
		acceptButton = new Button(I18NHandler.getText(I18NText.Button_Accept), Icon.ACCEPT.getIcon());
		acceptButton.addClickListener(e -> submit());
		acceptButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);


		// create layouts
		Label spacer = new Label("");
		buttonLayout = new HorizontalLayout(spacer, acceptButton, cancelButton);
		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		buttonLayout.setSpacing(true);
		buttonLayout.setExpandRatio(spacer, 1f);

		formLayout = new FormLayout(parentNameField, nameField, valueField, maxNumberOfOccurencesField, isMandatoryField, attributesField);
		formLayout.setSpacing(true);

		mainLayout = new VerticalLayout(formLayout, buttonLayout);
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);


		setContent(mainLayout);
	}


	private void submit()
	{
		try
		{
			fieldGroup.commit();

			callback.submit(token);
			close();
		}
		catch (CommitException exc)
		{
			// TODO Auto-generated catch block
			CommitHandler.displayUserErrorMessageInvalidFields(exc);
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
		catch (Exception exc)
		{
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
	}


	private void cancel()
	{
		token = null;
		fieldGroup = null;
		callback = null;

		close();
	}

	private GenericDAO<Attribute> createDummyDAO = new GenericDAO<Attribute>() {

		@Override
		public Attribute createNew()
		{
			return new Attribute();
		}

		@Override
		public void persist(Attribute entity)
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}

		@Override
		public void merge(Attribute entity)
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}

		@Override
		public Attribute findById(Serializable id)
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}

		@Override
		public Attribute update(Attribute entity)
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}

		@Override
		public void delete(Attribute entity)
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}

		@Override
		public Set<?> findAll()
		{
			throw new UnsupportedOperationException("This operation is not permitted");
		}
	};
}

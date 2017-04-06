package com.secunet.eidserver.testbed.web.ui.comp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.i18n.Icon;
import com.vaadin.server.Resource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class StringMapField extends CustomField<Map<String, String>> implements Focusable
{

	/** generated */
	private static final long serialVersionUID = 5367040322076861041L;

	static class Entry
	{
		public Entry(String k, String r)
		{
			key = k;
			representation = r;
		}

		public final String key;
		public final String representation;
	}

	protected Map<String, String> actualFieldValue;
	protected List<Entry> representation;

	// UI
	protected VerticalLayout mainLayout;
	protected HorizontalLayout keyValueAddLayout;
	protected HorizontalLayout keyValueLayout;
	protected HorizontalLayout selectionAndRemoveLayout;

	protected ListSelect entrySelection;
	protected Label mappingLabel;
	protected TextField keyTextField;
	protected TextField valueTextField;
	protected Button addButton;
	protected Button removeButton;

	public StringMapField(final Map<String, String> actualValue, final String caption, final Resource icon)
	{
		setCaption(caption);
		setIcon(icon);

		this.actualFieldValue = actualValue;


		// create UI
		addButton = new Button(Icon.ADD.getIcon());
		addButton.addClickListener(e -> addEntry());
		removeButton = new Button(Icon.REMOVE.getIcon());
		removeButton.addClickListener(e -> removeEntry());

		keyTextField = new TextField();
		keyTextField.setRequired(true);
		keyTextField.setWidth(100, Unit.PERCENTAGE);
		keyTextField.setNullRepresentation("");
		mappingLabel = new Label(" = ");
		valueTextField = new TextField();
		valueTextField.setRequired(true);
		valueTextField.setWidth(100, Unit.PERCENTAGE);
		valueTextField.setNullRepresentation("");

		keyValueLayout = new HorizontalLayout(keyTextField, mappingLabel, valueTextField);
		keyValueLayout.setWidth(100, Unit.PERCENTAGE);
		keyValueLayout.setSpacing(true);
		keyValueLayout.setExpandRatio(keyTextField, 3);
		keyValueLayout.setExpandRatio(valueTextField, 3);

		keyValueAddLayout = new HorizontalLayout(keyValueLayout, addButton);
		keyValueAddLayout.setWidth(100, Unit.PERCENTAGE);
		keyValueAddLayout.setSpacing(true);
		keyValueAddLayout.setExpandRatio(keyValueLayout, 1);


		entrySelection = new ListSelect(null, new ListContainer<>(new ArrayList<Entry>()));
		entrySelection.setMultiSelect(false);
		entrySelection.setNullSelectionAllowed(false);
		entrySelection.setWidth(100, Unit.PERCENTAGE);

		selectionAndRemoveLayout = new HorizontalLayout(entrySelection, removeButton);
		selectionAndRemoveLayout.setWidth(100, Unit.PERCENTAGE);
		selectionAndRemoveLayout.setSpacing(true);
		selectionAndRemoveLayout.setComponentAlignment(entrySelection, Alignment.TOP_CENTER);
		selectionAndRemoveLayout.setComponentAlignment(removeButton, Alignment.TOP_CENTER);
		selectionAndRemoveLayout.setExpandRatio(entrySelection, 1);


		mainLayout = new VerticalLayout(keyValueAddLayout, selectionAndRemoveLayout);
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setSpacing(true);


		createListSelectionValue();
	}

	@Override
	protected void setInternalValue(final Map<String, String> newValue)
	{
		super.setInternalValue(newValue);
		actualFieldValue = newValue;

		createListSelectionValue();
	}

	private void createListSelectionValue()
	{
		representation = new ArrayList<Entry>();

		if (actualFieldValue != null)
		{
			actualFieldValue.keySet().forEach((key) -> representation.add(new Entry(key, key + " = " + actualFieldValue.get(key))));
			setValue(actualFieldValue);
		}

		entrySelection.setContainerDataSource(new ListContainer<>(representation));
		representation.forEach(r -> entrySelection.setItemCaption(r, r.representation));
	}

	private void removeEntry()
	{
		Entry value = (Entry) entrySelection.getValue();
		if (value != null)
		{
			actualFieldValue.remove(value.key);
		}

		createListSelectionValue();
	}

	protected void addEntry()
	{
		if (!validateInputFields())
			return;

		String key = keyTextField.getValue();
		String value = valueTextField.getValue();

		if (actualFieldValue == null)
			actualFieldValue = new HashMap<String, String>();

		actualFieldValue.put(key, value);

		keyTextField.setValue(null);
		valueTextField.setValue(null);

		createListSelectionValue();

		representation.forEach(e -> {
			if (key.equals(e.key))
				entrySelection.select(e);
		});
	}

	public boolean validateInputFields()
	{
		boolean ok = true;
		if (keyTextField.getValue() == null || keyTextField.getValue().isEmpty())
		{
			keyTextField.setComponentError(new UserError(I18NHandler.getText(I18NText.Error_InvalidEntry)));
			ok = false;
		}
		else
			keyTextField.setComponentError(null);

		if (valueTextField.getValue() == null || valueTextField.getValue().isEmpty())
		{
			valueTextField.setComponentError(new UserError(I18NHandler.getText(I18NText.Error_InvalidEntry)));
			ok = false;
		}
		else
			valueTextField.setComponentError(null);

		return ok;
	}

	@Override
	protected Component initContent()
	{
		return mainLayout;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getType()
	{
		return Object.class;
	}

}

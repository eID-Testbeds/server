package com.secunet.eidserver.testbed.web.ui.comp;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.vaadin.viritin.ListContainer;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

public class SortedMultiValueListSelection<T> extends CustomField<List<T>> implements Focusable
{

	/** Generated */
	private static final long serialVersionUID = -5870038013269703780L;


	protected Set<T> availableEntries;
	protected List<T> actualValue;


	@FunctionalInterface
	public interface CaptionConverter<T> extends Serializable
	{
		public abstract String generateCaption(T event);
	}

	@FunctionalInterface
	public interface IconConverter<T> extends Serializable
	{
		public abstract Resource generateIcon(T event);
	}

	protected CaptionConverter<T> captionGenerator;
	protected IconConverter<T> iconGenerator;

	// UI
	protected HorizontalLayout mainLayout;
	protected VerticalLayout transferButtonLayout;
	protected VerticalLayout sortButtonLayout;

	private ListSelect availableOptions;
	private ListSelect sortedSelection;
	private Button up;
	private Button down;
	private Button add;
	private Button remove;

	public SortedMultiValueListSelection(Set<T> availableEntries, List<T> actualValue, String caption, Resource icon)
	{
		setCaption(caption);
		setIcon(icon);

		this.availableEntries = availableEntries;
		this.actualValue = actualValue;


		// create
		mainLayout = new HorizontalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setSpacing(true);
		transferButtonLayout = new VerticalLayout();
		transferButtonLayout.setWidth(50, Unit.PIXELS);
		sortButtonLayout = new VerticalLayout();
		sortButtonLayout.setWidth(50, Unit.PIXELS);


		availableOptions = new ListSelect("", availableEntries);
		availableOptions.setMultiSelect(false);
		availableOptions.setNullSelectionAllowed(false);
		availableOptions.setWidth(100, Unit.PERCENTAGE);
		availableEntries.forEach(t -> availableOptions.setItemCaption(t, getCaption(t)));
		availableEntries.forEach(t -> availableOptions.setItemIcon(t, getIcon(t)));

		sortedSelection = new ListSelect("", new ListContainer<>(actualValue));
		sortedSelection.setMultiSelect(false);
		sortedSelection.setNullSelectionAllowed(false);
		sortedSelection.setWidth(100, Unit.PERCENTAGE);
		availableEntries.forEach(t -> sortedSelection.setItemCaption(t, getCaption(t)));
		availableEntries.forEach(t -> sortedSelection.setItemIcon(t, getIcon(t)));

		up = new Button(FontAwesome.ANGLE_UP);
		down = new Button(FontAwesome.ANGLE_DOWN);
		add = new Button(FontAwesome.PLUS);
		remove = new Button(FontAwesome.MINUS);


		// add into layouts
		transferButtonLayout.addComponent(add);
		transferButtonLayout.addComponent(remove);
		sortButtonLayout.addComponent(up);
		sortButtonLayout.addComponent(down);
		mainLayout.addComponent(availableOptions);
		mainLayout.setExpandRatio(availableOptions, 0.5f);
		mainLayout.addComponent(transferButtonLayout);
		mainLayout.setComponentAlignment(transferButtonLayout, Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(sortedSelection);
		mainLayout.setExpandRatio(sortedSelection, 0.5f);
		mainLayout.addComponent(sortButtonLayout);
		mainLayout.setComponentAlignment(sortButtonLayout, Alignment.MIDDLE_CENTER);


		// handlers
		add.addClickListener(e -> add());
		remove.addClickListener(e -> remove());
		up.addClickListener(e -> up());
		down.addClickListener(e -> down());
	}

	@Override
	protected void setInternalValue(final List<T> newValue)
	{
		super.setInternalValue(newValue);
		actualValue = newValue;
		if (newValue != null && sortedSelection != null)
		{
			sortedSelection.setContainerDataSource(new ListContainer<>(actualValue));
			actualValue.forEach(t -> sortedSelection.setItemCaption(t, getCaption(t)));
			actualValue.forEach(t -> sortedSelection.setItemIcon(t, getIcon(t)));
		}
	}

	@Override
	protected Component initContent()
	{
		return mainLayout;
	}

	private void down()
	{
		@SuppressWarnings("unchecked")
		T value = (T) sortedSelection.getValue();
		if (value != null)
		{
			int indexOf = actualValue.indexOf(value);
			if (indexOf < actualValue.size() - 1)
			{
				actualValue.add(indexOf + 2, value);
				actualValue.remove(indexOf);
				sortedSelection.setContainerDataSource(new ListContainer<>(actualValue));
				setValue(actualValue);
				sortedSelection.focus();
				sortedSelection.select(value);
			}
		}
	}

	private void up()
	{
		@SuppressWarnings("unchecked")
		T value = (T) sortedSelection.getValue();
		if (value != null)
		{
			int indexOf = actualValue.indexOf(value);
			if (indexOf > 0)
			{
				actualValue.add(indexOf - 1, value);
				actualValue.remove(indexOf + 1);
				sortedSelection.setContainerDataSource(new ListContainer<>(actualValue));
				setValue(actualValue);
				sortedSelection.focus();
				sortedSelection.select(value);
			}
		}
	}

	private void remove()
	{
		@SuppressWarnings("unchecked")
		T value = (T) sortedSelection.getValue();
		if (value != null)
		{
			int indexOf = actualValue.indexOf(value);
			actualValue.remove(value);
			sortedSelection.setContainerDataSource(new ListContainer<>(actualValue));
			sortedSelection.setItemCaption(value, getCaption(value));
			sortedSelection.setItemIcon(value, getIcon(value));
			setValue(actualValue);
			sortedSelection.focus();
			availableOptions.addItem(value);
			if (sortedSelection.size() > indexOf)
				sortedSelection.select(actualValue.get(indexOf));
		}
	}

	private void add()
	{
		@SuppressWarnings("unchecked")
		T value = (T) availableOptions.getValue();
		if (value != null)
		{
			actualValue.add(value);
			sortedSelection.setContainerDataSource(new ListContainer<>(actualValue));
			sortedSelection.setItemCaption(value, getCaption(value));
			sortedSelection.setItemIcon(value, getIcon(value));
			setValue(actualValue);
			sortedSelection.focus();
			sortedSelection.select(value);
			availableOptions.removeItem(value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getType()
	{
		// if (actualValue != null)
		// return (Class<? extends List<T>>) actualValue.getClass();
		// else
		// {
		// return (Class<? extends List<T>>) new ArrayList<T>().getClass();
		return Object.class;
		// }
	}

	public void setCaptionConverter(CaptionConverter<T> function)
	{
		this.captionGenerator = function;

		if (actualValue != null && sortedSelection != null)
		{
			actualValue.forEach(t -> sortedSelection.setItemCaption(t, getCaption(t)));
			if (availableEntries != null)
				availableEntries.forEach(t -> sortedSelection.setItemCaption(t, getCaption(t)));
		}
		if (availableEntries != null && availableOptions != null)
		{
			availableEntries.forEach(t -> availableOptions.setItemCaption(t, getCaption(t)));
			if (actualValue != null)
				actualValue.forEach(t -> availableOptions.setItemCaption(t, getCaption(t)));
		}
	}

	public void setIconGenerator(IconConverter<T> function)
	{
		this.iconGenerator = function;

		if (actualValue != null && sortedSelection != null)
			actualValue.forEach(t -> sortedSelection.setItemIcon(t, getIcon(t)));
		if (availableEntries != null && availableOptions != null)
			availableEntries.forEach(t -> availableOptions.setItemIcon(t, getIcon(t)));
	}


	/** overwrite this method to create caption method */
	protected String getCaption(T component)
	{
		if (captionGenerator != null)
			return captionGenerator.generateCaption(component);
		else
			return (component == null ? "NULL" : component.toString());
	}

	protected Resource getIcon(T obj)
	{
		if (obj == null || iconGenerator == null)
			return null;

		return iconGenerator.generateIcon(obj);
	}
}

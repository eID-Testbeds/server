package com.secunet.eidserver.testbed.web.ui.comp;

import java.io.Serializable;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.interfaces.dao.GenericDAO;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ItemSorter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

//public class CollectionComponentField<C extends Collection<O>, O> extends CustomField<C>
public class CollectionComponentField<C extends Collection<W>, W> extends CustomField<C>
{
	private static final Logger logger = LogManager.getRootLogger();

	public static final String CAPTION_PROPERTY = "caption";
	public static final String VALUE_PROPERTY = "value";
	public static final String ICON_PROPERTY = "icon";


	@FunctionalInterface
	public interface CaptionGenerator<W> extends Serializable
	{
		public abstract String generateCaption(W event);
	}

	@FunctionalInterface
	public interface IconGenerator<W> extends Serializable
	{
		public abstract Resource generateIcon(W event);
	}


	/** Generated */
	private static final long serialVersionUID = -6922802835587362931L;

	// behaviour and rights
	protected boolean editInPlace = true;
	protected boolean showSelectedInPlace = true;
	protected boolean hideEditor = true;
	protected boolean canEdit = true;
	protected boolean canCreate = true;
	protected boolean canDelete = true;
	protected boolean canBeEmpty = true;
	protected boolean fixedRowSize = false;


	// model and datastructure
	protected Class<W> typeCollClazz;
	protected GenericDAO<?> dao;
	protected C collection;
	protected Class<C> collectionClass;
	protected W selectedItem;
	protected IndexedContainer container;
	protected String caption;
	protected boolean isCreationMode = false;
	protected CaptionGenerator<W> captionGenerator;
	protected IconGenerator<W> iconGenerator;

	// UI Elements;
	protected BeanFieldGroup<W> beanFieldGroup;
	protected ListSelect collectionItemlistSelect;
	protected Window externalEditor;
	protected FormLayout formLayout;
	protected AbstractOrderedLayout mainLayout;
	protected AbstractOrderedLayout buttonsLayout;
	protected AbstractOrderedLayout selectAndButtonsLayout;
	protected AbstractOrderedLayout editorLayout;
	protected HorizontalLayout formButtonLayout;
	protected Button createButton;
	protected Button deleteButton;
	protected Button editButton;
	protected Button acceptButton;
	protected Button cancelButton;


	public CollectionComponentField(final String caption, final C collection, final IndexedContainer container, FormLayout formLayout, BeanFieldGroup<W> beanFieldGroup, Class<W> typeInterfClazz,
			GenericDAO<?> dao)
	{
		this(caption, collection, container, formLayout, beanFieldGroup, typeInterfClazz, dao, null, null);
	}

	public CollectionComponentField(String caption, C collection, IndexedContainer container, FormLayout formLayout, BeanFieldGroup<W> beanFieldGroup, Class<W> typeInterfClazz, GenericDAO<?> dao,
			CaptionGenerator<W> captionFunction, IconGenerator<W> iconGenerator)
	{
		if (collection == null)
			throw new IllegalArgumentException("Collection must not be null");
		if (formLayout == null)
			throw new IllegalArgumentException("formLayout must not be null");
		if (beanFieldGroup == null)
			throw new IllegalArgumentException("beanFieldGroup must not be null");
		if (dao == null)
			throw new IllegalArgumentException("View must not be null");
		if (typeInterfClazz == null)
			throw new IllegalArgumentException("Class must not be null");

		this.collection = collection;
		this.collectionClass = (Class<C>) collection.getClass();
		this.container = container;
		this.formLayout = formLayout;
		this.caption = caption;
		this.beanFieldGroup = beanFieldGroup;
		this.typeCollClazz = typeInterfClazz;
		this.dao = dao;
		this.captionGenerator = captionFunction;
		this.iconGenerator = iconGenerator;

		setWidth(100, Unit.PERCENTAGE);


		formLayout.setEnabled(false);
		// formLayout.setMargin(true);

		generateContainer();

		generateButtons();
		collectionItemlistSelect = generateCollectionItemsSelection();
		editorLayout = generateEditorLayout();
		buttonsLayout = generateButtonsLayout();
		selectAndButtonsLayout = generateSelectionLayout();
		mainLayout = generateMainLayout();

		final boolean editMode = false;
		switchEditModeUIElements(editMode);

		setValue(collection);

		hideEditor();
	}

	public void selectFirst()
	{
		if (collection != null && collection.size() > 0)
			collectionItemlistSelect.select(collection.iterator().next());
	}


	public AbstractOrderedLayout getInnerLayout()
	{
		return mainLayout;
	}

	@Override
	protected void setInternalValue(final C newValue)
	{
		super.setInternalValue(newValue);
		collection = newValue;
	}

	@Override
	protected Component initContent()
	{
		return mainLayout;
	}

	@SuppressWarnings("unchecked")
	protected void generateContainer()
	{
		container = new IndexedContainer();
		container.addContainerProperty(CollectionComponentField.CAPTION_PROPERTY, String.class, null);
		container.addContainerProperty(CollectionComponentField.VALUE_PROPERTY, typeCollClazz, null);
		container.addContainerProperty(CollectionComponentField.ICON_PROPERTY, Resource.class, null);


		for (W obj : collection)
		{
			Item addItem = container.addItem(obj);
			addItem.getItemProperty(VALUE_PROPERTY).setValue(obj);
			addItem.getItemProperty(CAPTION_PROPERTY).setValue(getCaption(obj));
			addItem.getItemProperty(ICON_PROPERTY).setValue(getIcon(obj));
		}

		container.setItemSorter(new ItemSorter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSortProperties(Sortable container, Object[] propertyId, boolean[] ascending)
			{
			}

			@Override
			public int compare(Object itemId1, Object itemId2)
			{
				String s1 = (String) container.getContainerProperty(itemId1, CollectionComponentField.CAPTION_PROPERTY).getValue();
				String s2 = (String) container.getContainerProperty(itemId2, CollectionComponentField.CAPTION_PROPERTY).getValue();
				return s1.compareTo(s2);
			}
		});
		container.sort(null, null);
	}

	protected AbstractOrderedLayout generateEditorLayout()
	{
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setWidth(100, Unit.PERCENTAGE);

		Label spacer = new Label();
		formButtonLayout = new HorizontalLayout(spacer, acceptButton, cancelButton);
		formButtonLayout.setWidth(100, Unit.PERCENTAGE);
		formButtonLayout.setExpandRatio(spacer, 1);
		formButtonLayout.setSpacing(true);
		// formButtonLayout.setMargin(new MarginInfo(false, true, true, true));
		formButtonLayout.setVisible(false);

		outerLayout.addComponent(formLayout);
		outerLayout.addComponent(formButtonLayout);

		return outerLayout;
	}

	protected ListSelect generateCollectionItemsSelection()
	{
		ListSelect lstSel = new ListSelect(null, container);

		lstSel.setDescription(caption);
		lstSel.setMultiSelect(false);
		lstSel.setItemCaptionPropertyId(CAPTION_PROPERTY);
		lstSel.setItemIconPropertyId(ICON_PROPERTY);
		lstSel.setRows((collection == null || collection.isEmpty() ? 1 : collection.size()));
		lstSel.setNullSelectionAllowed(true);
		lstSel.setWidth(100, Unit.PERCENTAGE);
		lstSel.addValueChangeListener(e -> selectionChanged(e));

		return lstSel;
	}

	@SuppressWarnings("unchecked")
	protected void selectionChanged(com.vaadin.data.Property.ValueChangeEvent e)
	{
		if (e.getProperty().getValue() != null && canEdit)
		{
			editButton.setEnabled(true);
			selectedItem = (W) container.getItem(e.getProperty().getValue()).getItemProperty(VALUE_PROPERTY).getValue();
			showComponentForm();
		}
		else if (showSelectedInPlace)
			formLayout.setVisible(true);

		if (canEdit)
			editButton.setEnabled(selectedItem != null);
		if (canDelete)
			deleteButton.setEnabled(selectedItem != null);
	}

	public void showComponentForm()
	{
		beanFieldGroup.setItemDataSource(selectedItem);

		if (showSelectedInPlace)
			formLayout.setVisible(true);
	}

	protected void generateButtons()
	{
		if (canEdit)
		{
			editButton = new Button(null, e -> edit());
			editButton.setIcon(FontAwesome.COGS); // TODO icons should come from the ICONS enum
			editButton.setDescription(I18NHandler.getText(I18NText.Button_Edit));
			editButton.setWidth(100, Unit.PERCENTAGE);
		}
		if (canCreate)
		{
			createButton = new Button(null, e -> create());
			createButton.setIcon(FontAwesome.PLUS); // TODO icons should come from the ICONS enum
			createButton.setDescription(I18NHandler.getText(I18NText.Button_Create));
			createButton.setWidth(100, Unit.PERCENTAGE);
		}
		if (canDelete)
		{
			deleteButton = new Button(null, e -> delete());
			deleteButton.setIcon(FontAwesome.CLOSE); // TODO icons should come from the ICONS enum
			deleteButton.setDescription(I18NHandler.getText(I18NText.Button_Delete));
			deleteButton.setWidth(100, Unit.PERCENTAGE);
		}
		if (canEdit || canCreate)
		{
			acceptButton = new Button(I18NHandler.getText(I18NText.Button_Accept), e -> accept());
			acceptButton.setIcon(FontAwesome.SAVE); // TODO icons should come from the ICONS enum
			acceptButton.setDescription(I18NHandler.getText(I18NText.Button_Accept));
			acceptButton.setEnabled(false);

			cancelButton = new Button(I18NHandler.getText(I18NText.Button_Cancel), e -> cancel());
			cancelButton.setDescription(I18NHandler.getText(I18NText.Button_Cancel));
			cancelButton.setEnabled(false);
		}

	}

	protected AbstractOrderedLayout generateSelectionLayout()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(true);

		layout.addComponent(collectionItemlistSelect);
		layout.addComponent(buttonsLayout);
		layout.setExpandRatio(collectionItemlistSelect, 0.9f);

		return layout;
	}

	protected AbstractOrderedLayout generateButtonsLayout()
	{
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidthUndefined();

		if (canEdit)
			layout.addComponent(editButton);

		if (canCreate)
			layout.addComponent(createButton);

		if (canDelete)
			layout.addComponent(deleteButton);

		layout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		return layout;
	}

	protected void cancel()
	{
		final boolean editMode = false;
		switchEditModeUIElements(editMode);
	}

	public void switchEditModeUIElements(boolean editMode)
	{
		collectionItemlistSelect.setEnabled(!editMode);
		createButton.setEnabled(!editMode);
		deleteButton.setEnabled(!editMode && !collection.isEmpty());
		editButton.setEnabled(!editMode && !collection.isEmpty());

		acceptButton.setEnabled(editMode);
		cancelButton.setEnabled(editMode);

		formLayout.setEnabled(editMode);
		formLayout.setVisible(!hideEditor || editMode || (showSelectedInPlace && selectedItem != null));
		formButtonLayout.setVisible(editMode);

		if (!fixedRowSize)
			collectionItemlistSelect.setRows((collection == null || collection.isEmpty()) ? 1 : collection.size());
	}

	protected void accept()
	{
		try
		{
			beanFieldGroup.commit();

			if (isCreationMode)
				collection.add(selectedItem);

			generateContainer();

			collectionItemlistSelect.setContainerDataSource(container);
			collectionItemlistSelect.select(selectedItem);

			switchEditModeUIElements(false);

			beanFieldGroup.getFields().forEach(field -> {
				if (AbstractComponent.class.isAssignableFrom(field.getClass()))
					((AbstractComponent) field).setComponentError(null);
			});
		}
		catch (CommitException e)
		{
			logger.error(e);
			e.getInvalidFields().forEach((field, exc) -> {
				if (AbstractComponent.class.isAssignableFrom(field.getClass()))
					((AbstractComponent) field).setComponentError(new UserError(e.getInvalidFields().get(field).getLocalizedMessage()));
			});
		}

		collectionItemlistSelect.setEnabled(true);

	}

	protected void delete()
	{
		collection.remove(selectedItem);
		container.removeItem(selectedItem);

		collectionItemlistSelect.unselect(selectedItem);
		selectedItem = null;

		if (!collection.isEmpty())
			collectionItemlistSelect.select(collection.stream().findFirst().get());
		else
		{
			formLayout.setVisible(false);
			beanFieldGroup.setItemDataSource((W) null);
		}

		if (canEdit)
			editButton.setEnabled(selectedItem != null);
		if (canDelete)
			deleteButton.setEnabled(selectedItem != null);
	}

	@SuppressWarnings("unchecked")
	protected void create()
	{
		selectedItem = (W) dao.createNew();
		isCreationMode = true;
		switchEditModeUIElements(true);

		showComponentForm();
	}

	@SuppressWarnings("unchecked")
	protected void edit()
	{
		if (selectedItem == null)
			selectedItem = (W) collectionItemlistSelect.getValue();

		if (selectedItem != null)
		{
			isCreationMode = false;

			final boolean editMode = true;
			switchEditModeUIElements(editMode);
		}
	}

	protected AbstractOrderedLayout generateMainLayout()
	{
		if (showSelectedInPlace || editInPlace)
		{
			VerticalLayout layout = new VerticalLayout(selectAndButtonsLayout, editorLayout);
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.setMargin(true);

			return layout;
		}
		else
		{
			return selectAndButtonsLayout;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getType()
	{
		return Object.class;
		// if (collection != null)
		// return (Class<? extends C>) collection.getClass();
		// else if (collectionClass != null)
		// return collectionClass;
		// else
		// return null;
	}

	@SuppressWarnings("unchecked")
	public void setCaptionConverter(CaptionGenerator<W> function)
	{
		this.captionGenerator = function;
		if (function != null && collection != null)
		{
			for (W obj : collection)
				container.getItem(obj).getItemProperty(CAPTION_PROPERTY).setValue(getCaption(obj));
		}
	}

	@SuppressWarnings("unchecked")
	public void setIconGenerator(IconGenerator<W> function)
	{
		this.iconGenerator = function;
		if (function != null && collection != null)
		{
			for (W obj : collection)
				container.getItem(obj).getItemProperty(ICON_PROPERTY).setValue(getIcon(obj));
		}
	}


	/** overwrite this method to create caption method */
	protected String getCaption(W component)
	{
		if (captionGenerator != null)
			return captionGenerator.generateCaption(component);
		else
			return (component == null ? "NULL" : component.toString());
	}

	private Resource getIcon(W obj)
	{
		if (obj == null || iconGenerator == null)
			return null;

		return iconGenerator.generateIcon(obj);
	}

	public void hideEditor()
	{
		if (formLayout != null && hideEditor)
			formLayout.setVisible(false);
	}

	public void setShowFormAlways(boolean showFormAlways)
	{
		hideEditor = !showFormAlways;

		hideEditor();
	}

	public void setPageSize(int i)
	{
		fixedRowSize = (i > 1);
		if (i > 1 && collectionItemlistSelect != null)
			collectionItemlistSelect.setRows(i);
	}

}
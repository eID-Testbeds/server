package com.secunet.eidserver.testbed.web.ui.view.step.panels.in;

import java.util.Arrays;
import java.util.List;

import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.Attributes;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class StepInboundTokenPanel extends TogglePanelLayout
{

	private static final long serialVersionUID = 1L;


	protected List<StepToken> tokens;
	protected ListContainer<StepToken> tableContainer;

	protected VerticalLayout layout;
	protected Table table;
	protected HorizontalLayout buttonLayout;
	protected Button edit;
	protected Button add;
	protected Button delete;


	protected BeanFieldGroup<TestCaseStep> fieldGroup;


	public StepInboundTokenPanel(String caption, Resource icon)
	{
		super(caption, icon);

	}

	public void initializeUI()
	{
		setMinimized(true);
		setVisible(false);

		layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setWidth(100, Unit.PERCENTAGE);


		table = new Table();
		table.setDescription(I18NHandler.getText(I18NText.TestCaseStep_Header));
		table.setWidth(100, Unit.PERCENTAGE);
		table.setSelectable(true);
		table.setMultiSelect(false);
		// table.setPageLength();
		table.setCacheRate(3);
		table.setColumnHeader("parentName", I18NHandler.getText(I18NText.TestCaseStep_Token_ParentName));
		table.setColumnHeader("name", I18NHandler.getText(I18NText.TestCaseStep_Token_Name));
		table.setColumnHeader("value", I18NHandler.getText(I18NText.TestCaseStep_Token_Value));
		table.setColumnHeader("isMandatory", I18NHandler.getText(I18NText.TestCaseStep_Token_isMandatory));
		table.setColumnHeader("maxNumberOfOccurences", I18NHandler.getText(I18NText.TestCaseStep_Token_maxNumberOfOccurences));
		table.setSortContainerPropertyId("name");
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.addItemClickListener(e -> updateButtons(e));

		Label spacerLeft = new Label("");
		Label spacerMid = new Label("");
		edit = new Button(I18NHandler.getText(I18NText.Button_Edit), e -> edit());
		edit.setIcon(FontAwesome.EDIT);
		delete = new Button(I18NHandler.getText(I18NText.Button_Delete), e -> delete());
		delete.setIcon(FontAwesome.EDIT);
		add = new Button(I18NHandler.getText(I18NText.Button_Add), e -> add());
		add.setIcon(FontAwesome.PLUS);

		buttonLayout = new HorizontalLayout(spacerLeft, add, spacerMid, edit, delete);
		buttonLayout.setExpandRatio(spacerLeft, 0.9f);
		buttonLayout.setExpandRatio(spacerMid, 0.1f);
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);
		buttonLayout.setWidth(100, Unit.PERCENTAGE);

		layout.addComponent(table);
		layout.addComponent(buttonLayout);


		setContent(layout);
	}


	private void add()
	{
		StepToken stepToken = new StepToken();
		stepToken.setAttributes(new Attributes());

		StepTokenEditor editor = new StepTokenEditor(stepToken, token -> handleTokenEditorResult(token));
		UI.getCurrent().addWindow(editor);
	}

	private void handleTokenEditorResult(StepToken token)
	{
		if (!tokens.contains(token))
			tokens.add(token);

		updateModel(tokens);
	}

	private void edit()
	{
		Object selectedValue = table.getValue();
		if (selectedValue != null && selectedValue instanceof StepToken)
		{
			StepTokenEditor editor = new StepTokenEditor((StepToken) selectedValue, token -> handleTokenEditorResult(token));
			UI.getCurrent().addWindow(editor);
		}
	}


	private void delete()
	{
		Object selectedValue = table.getValue();
		if (selectedValue != null && selectedValue instanceof StepToken)
		{
			tokens.remove(selectedValue);

			updateModel(tokens);
		}
	}

	public void updateModel(List<StepToken> steps)
	{
		tokens = steps;

		if (tokens != null && !tokens.isEmpty())
		{
			tableContainer = new ListContainer<>(tokens);
			table.setContainerDataSource(tableContainer, Arrays.asList("parentName", "name", "value", "isMandatory", "maxNumberOfOccurences"));
			table.setPageLength(tokens.size());
		}
		else
		{
			table.setContainerDataSource(null);
		}
	}

	private void updateButtons(ItemClickEvent e)
	{
		edit.setEnabled((e.getItemId() != null));
		delete.setEnabled((e.getItemId() != null));
	}

	public List<StepToken> commitChanges() throws CommitException
	{
		return tokens;
	}

}

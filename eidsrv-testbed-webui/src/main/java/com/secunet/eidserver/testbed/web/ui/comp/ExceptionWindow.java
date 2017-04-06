package com.secunet.eidserver.testbed.web.ui.comp;


import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * Simple view to show exceptions. This window has three main parts:
 * <ol>
 * <li>The exception message</li>
 * <li>instructions how the handle the error</li>
 * <li>the exception details, mostl likeley the exception callstack</li>
 * </ol>
 */
public class ExceptionWindow extends Window
{

	/** generated */
	private static final long serialVersionUID = -7063749662974588318L;

	protected VerticalLayout layout;
	protected Button ok;

	/**
	 * @param messageLocalized
	 *            the localized message
	 * @param instructionsLocalized
	 *            the localized instruction
	 * @param exc
	 *            must not be null
	 */
	public ExceptionWindow(final String messageLocalized, final String instructionsLocalized, final Exception exc)
	{
		this(messageLocalized, instructionsLocalized, (exc.getLocalizedMessage() + "\n" + Log.getStackTrace(exc)));
	}

	@SuppressWarnings("deprecation")
	public ExceptionWindow(final String messageLocalized, final String instructionsLocalized, final String details)
	{
		layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.addStyleName(TestbedTheme.SMALLMARGINS);

		Label header = new Label(I18NHandler.getText(I18NText.ExceptionWindow_Header));
		header.addStyleName(TestbedTheme.LABEL_H1);

		Label excMessage = new Label(messageLocalized);
		excMessage.addStyleName(TestbedTheme.LABEL_COLORED);

		Label spacer1 = new Label("");
		Label instructionsHeader = new Label(I18NHandler.getText(I18NText.ExceptionWindow_InstructionsCaption));
		header.addStyleName(TestbedTheme.LABEL_H3);
		Label actualAdvice = new Label(instructionsLocalized);
		Label spacer2 = new Label("");


		TextArea detailsArea = new TextArea();
		detailsArea.setValue(details);
		detailsArea.setWidth(100, Unit.PERCENTAGE);
		detailsArea.setRows(10);
		detailsArea.setReadOnly(true);
		detailsArea.addStyleName(TestbedTheme.BUTTON_BORDERLESS);

		TogglePanelLayout detailsPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.ExceptionWindow_Details), null, detailsArea, true, 100, Unit.PERCENTAGE);

		ok = new Button("OK");
		ok.addStyleName(TestbedTheme.BUTTON_PRIMARY);
		ok.addClickListener(e -> close());

		layout.addComponent(header);
		layout.addComponent(excMessage);
		layout.addComponent(spacer1);
		layout.addComponent(instructionsHeader);
		layout.addComponent(actualAdvice);
		layout.addComponent(spacer2);
		layout.addComponent(detailsPanel);
		layout.addComponent(ok);
		layout.setComponentAlignment(ok, Alignment.BOTTOM_RIGHT);
		layout.setExpandRatio(excMessage, 1);


		// setup window
		setContent(layout);
		setCaption(I18NHandler.getText(I18NText.ExceptionWindow_Title));
		setWidth(75, Unit.PERCENTAGE);
		setResizable(false);
		setIcon(FontAwesome.EXCLAMATION_TRIANGLE);
		setCloseShortcut(KeyCode.ESCAPE, null);
		setModal(true);
		center();
	}

	public static void showNewWindow(final String messageLocalized, String instructionsLocalized, Exception exception)
	{
		UI.getCurrent().addWindow(new ExceptionWindow(messageLocalized, instructionsLocalized, exception));
	}

	public static void showNewWindow(final String messageLocalized, String instructionsLocalized, String details)
	{
		UI.getCurrent().addWindow(new ExceptionWindow(messageLocalized, instructionsLocalized, details));
	}

	/**
	 * @param textLocalized
	 *            must be already localized
	 * @return a failure label with the given localized text as value and description
	 */
	public static Label generateFailedLabel(String textLocalized)
	{
		Label label = new Label(textLocalized);
		label.addStyleName(TestbedTheme.LABEL_FAILURE);
		label.setDescription(textLocalized);
		label.setWidth(100, Unit.PERCENTAGE);

		return label;
	}
}

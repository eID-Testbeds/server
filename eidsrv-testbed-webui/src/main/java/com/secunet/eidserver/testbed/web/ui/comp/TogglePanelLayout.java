package com.secunet.eidserver.testbed.web.ui.comp;


import java.util.List;

import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Panel (actual {@link CssLayout} that has a button in the Panel Caption field (on the left side), that "minimizes" or maximazies the Panel.
 *
 *
 */
public class TogglePanelLayout extends CssLayout
{

	/** generated */
	private static final long serialVersionUID = 3997421070427211404L;

	/** the actual content in the panel layout */
	protected AbstractComponent content;
	/** the current state of the panel */
	protected boolean minimized = false;
	/** button for toggling */
	protected Button toggleButton;
	/** label of the panel containing the caption */
	protected Label captionLabel;

	/** captionHeaderLayout, contains caption and toggle button, can contain more */
	protected HorizontalLayout captionLayout;

	protected List<AbstractComponent> extraComponents;
	protected Resource ICON_MAXIMIZE = FontAwesome.ANGLE_DOWN;
	protected Resource ICON_MINIMIZE = FontAwesome.ANGLE_UP;

	/**
	 * @param caption
	 *            caption of the panel
	 * @param icon
	 *            icon for the caption label
	 * @param widthUnit
	 *            should not be null, if null, will be set to 100% width
	 */
	public TogglePanelLayout(final String caption, final Resource icon, float width, Unit widthUnit)
	{
		this(caption, icon, null, false, width, widthUnit);
	}

	/**
	 * @param caption
	 *            caption of the panel
	 * @param icon
	 *            icon for the caption label
	 */
	public TogglePanelLayout(final String caption, final Resource icon)
	{
		this(caption, icon, null, false, 100f, Unit.PERCENTAGE);
	}

	public TogglePanelLayout(final String caption, final AbstractComponent content)
	{
		this(caption, null, content, false, 100, Unit.PERCENTAGE);
	}

	public TogglePanelLayout(final String caption, final Resource icon, final AbstractComponent content)
	{
		this(caption, icon, content, false, 100, Unit.PERCENTAGE);
	}

	/**
	 * @param caption
	 *            caption of the panel
	 * @param icon
	 *            icon for the caption label
	 * @param content
	 *            the actual content
	 * @param minimized
	 *            the state of the panel
	 * @param widthUnit
	 *            should not be null, if null, will be set to 100% width
	 */
	public TogglePanelLayout(final String caption, final Resource icon, final AbstractComponent content, boolean minimized, float width, Unit widthUnit)
	{
		this.content = content;
		this.minimized = minimized;

		if (widthUnit == null)
		{
			width = 100f;
			widthUnit = Unit.PERCENTAGE;
		}

		captionLayout = new HorizontalLayout();
		captionLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		captionLayout.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		captionLayout.setMargin(new MarginInfo(false, false, false, true));
		captionLayout.setWidth(width, widthUnit);

		captionLabel = new Label(caption);
		captionLabel.setIcon(icon);
		captionLabel.setWidth(((caption == null) ? 10 : caption.length()), Unit.EM);
		captionLabel.addStyleName(ValoTheme.LABEL_LARGE);
		captionLayout.addComponent(captionLabel);
		captionLayout.setExpandRatio(captionLabel, 1);

		toggleButton = new Button();
		toggleButton.setIcon(ICON_MINIMIZE);
		styleExtraButton(toggleButton);

		captionLayout.addComponent(toggleButton);


		addStyleName(ValoTheme.LAYOUT_CARD);
		setWidth(width, widthUnit);

		addComponent(captionLayout);
		if (this.content != null)
			addComponent(content);

		toggleButton.addClickListener(e -> togglePanel());

		if (this.minimized)
		{
			toggleButton.setIcon(ICON_MAXIMIZE);
			if (content != null)
				content.setVisible(false);
		}
	}

	protected void styleExtraButton(Button button)
	{
		button.addStyleName(TestbedTheme.BUTTON_QUIET);
		button.addStyleName(TestbedTheme.BUTTON_SMALL);
		button.addStyleName(TestbedTheme.BUTTON_ICON_ONLY);
	}

	protected void togglePanel()
	{
		if (content != null)
		{
			minimized = !minimized;

			if (minimized)
			{
				toggleButton.setIcon(ICON_MAXIMIZE);
				content.setVisible(false);
			}
			else
			{
				toggleButton.setIcon(ICON_MINIMIZE);
				content.setVisible(true);
			}
		}
	}

	public AbstractComponent getContent()
	{
		return content;
	}

	public void setContent(AbstractComponent replaceContent)
	{
		if (this.content != null)
			removeComponent(this.content);

		this.content = replaceContent;

		if (this.content != null)
			addComponent(this.content);
	}

	public boolean isMinimized()
	{
		return minimized;
	}

	public void setMinimized(boolean minimized)
	{
		this.minimized = minimized;

		if (content != null)
		{
			content.setVisible(!minimized);
		}

		if (minimized)
		{
			toggleButton.setIcon(ICON_MINIMIZE);
		}
		else
		{
			toggleButton.setIcon(ICON_MAXIMIZE);
		}
	}

	public void setExtraHeaderContent(List<AbstractComponent> newExtras)
	{
		// remove old
		if (extraComponents != null && !extraComponents.isEmpty())
		{
			extraComponents.forEach(c -> removeComponent(c));
			extraComponents = null;
		}

		// create new setup
		extraComponents = newExtras;
		if (extraComponents != null && !extraComponents.isEmpty())
		{
			for (int i = 0; i < extraComponents.size(); i++)
				captionLayout.addComponent(extraComponents.get(i), (i + 1));
		}
	}

	public Button getToggleButton()
	{
		return toggleButton;
	}

	public HorizontalLayout getCaptionLayout()
	{
		return captionLayout;
	}
}

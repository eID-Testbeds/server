package com.secunet.eidserver.testbed.web.nav;


import com.vaadin.navigator.Navigator.ClassBasedViewProvider;
import com.vaadin.navigator.View;

/**
 * Allow caching of certain views.
 *
 */
public class CachingViewProvider extends ClassBasedViewProvider
{

	private static final long serialVersionUID = 1039255276884519592L;
	/* This field caches an already initialized view instance if the view should be cached (stateful views). */
	private View cachedInstance;
	/** needed to identify if we are in stateful mode */
	protected TestBedView viewType;

	public CachingViewProvider(TestBedView viewType)
	{
		super(viewType.getViewName(), viewType.getViewClass());

		this.viewType = viewType;
	}


	@Override
	public View getView(final String viewName)
	{
		View result = null;
		if (viewType.getViewName().equals(viewName))
		{
			if (viewType.isStateful())
			{
				// Stateful views get lazily instantiated
				if (cachedInstance == null)
					cachedInstance = super.getView(viewType.getViewName());

				result = cachedInstance;
			}
			else
			{
				// Non-stateful views get instantiated every time
				result = super.getView(viewType.getViewName());
			}
		}

		return result;
	}

}

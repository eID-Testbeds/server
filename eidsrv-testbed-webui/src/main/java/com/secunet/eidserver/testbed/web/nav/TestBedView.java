package com.secunet.eidserver.testbed.web.nav;


import com.secunet.eidserver.testbed.web.i18n.Icon;
import com.secunet.eidserver.testbed.web.ui.view.candidate.TestCandidateView;
import com.secunet.eidserver.testbed.web.ui.view.error.BadRightsView;
import com.secunet.eidserver.testbed.web.ui.view.error.ErrorView;
import com.secunet.eidserver.testbed.web.ui.view.report.ReportView;
import com.secunet.eidserver.testbed.web.ui.view.step.TeststepEditView;
import com.secunet.eidserver.testbed.web.ui.view.testcase.TestCaseEditView;
import com.secunet.eidserver.testbed.web.ui.view.testrun.RunView;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum TestBedView
{
	// TODO icons

	CANDIDATE("candidate", TestCandidateView.class, FontAwesome.FLASK, true),

	EXECUTION("execution", RunView.class, FontAwesome.DASHBOARD, false),

	TESTCASE("testcase", TestCaseEditView.class, FontAwesome.BAR_CHART, false),

	TESTSTEP("teststeps", TeststepEditView.class, FontAwesome.BAR_CHART_O, false),

	REPORTS("reports", ReportView.class, Icon.NAV_REPORT.getIcon(), false),

	BADRIGHTS("badrights", BadRightsView.class, FontAwesome.BULLHORN, true), // TODO

	ERROR("error", ErrorView.class, FontAwesome.BOLT, true); // TODO


	private final String viewNameResource;
	private final Class<? extends View> viewClass;
	private final Resource icon;
	private final boolean stateful;

	private TestBedView(final String viewNameResource, final Class<? extends View> viewClass, final Resource icon, final boolean stateful)
	{
		this.viewNameResource = viewNameResource;
		this.viewClass = viewClass;
		this.icon = icon;
		this.stateful = stateful;
	}

	public boolean isStateful()
	{
		return stateful;
	}

	public String getViewName()
	{
		return viewNameResource;
	}

	public Class<? extends View> getViewClass()
	{
		return viewClass;
	}

	public Resource getIcon()
	{
		return icon;
	}

	/**
	 * @return can be null
	 */
	public static TestBedView getByViewName(final String viewName)
	{
		try
		{
			TestBedView[] values = values();
			for (int i = 0; i < values.length; i++)
			{
				if (values[i].getViewName().equals(viewName))
					return values[i];
			}
		}
		catch (final IllegalArgumentException ex)
		{
		}

		return null;
	}

	/**
	 * cuts everything after the first "/" in completeStateOrPath
	 *
	 * @param completeStateOrPath
	 *            can be null
	 * @return null, or the {@link TestBedView} value.
	 */
	public static TestBedView extractView(String completeStateOrPath)
	{
		if (completeStateOrPath == null)
			return null;


		if (completeStateOrPath.contains("/"))
			completeStateOrPath = completeStateOrPath.substring(0, completeStateOrPath.indexOf("/"));

		for (TestBedView v : values())
		{
			if (v.name().equals(completeStateOrPath))
				return v;
		}

		return null;
	}
}

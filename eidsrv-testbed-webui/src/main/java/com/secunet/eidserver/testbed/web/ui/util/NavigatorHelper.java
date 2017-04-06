package com.secunet.eidserver.testbed.web.ui.util;


import java.util.List;

import com.secunet.eidserver.testbed.web.nav.TestBedView;

public class NavigatorHelper
{

	public static String getNavigationString(TestBedView view, List<String> paras)
	{
		String navigation = view.getViewName();
		if (paras != null && !paras.isEmpty())
			navigation = navigation + "/" + paras.stream().reduce((u, t) -> u + "/" + t).get();

		return navigation;
	}

}

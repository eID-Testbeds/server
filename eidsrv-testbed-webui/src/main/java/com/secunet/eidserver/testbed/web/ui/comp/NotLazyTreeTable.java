package com.secunet.eidserver.testbed.web.ui.comp;

import com.vaadin.ui.TreeTable;

/**
 * 
 * This class overrides the isPartialRowUpdate()-method of Vaadin in order to disable lazy loading after the user sorts the table
 *
 */
public class NotLazyTreeTable extends TreeTable
{
	private static final long serialVersionUID = 6843270692841520099L;

	@Override
	protected boolean isPartialRowUpdate()
	{
		return false;
	}
}

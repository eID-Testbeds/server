package com.secunet.eidserver.testbed.web.ui.comp;

import java.util.HashSet;
import java.util.Set;

import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.vaadin.data.Validator;

/**
 * Check by the backend if a value is already in use at a certain column of a certain table or if the name is empty
 * 
 */
public class UniqueCandidateProfileNameValidator implements Validator
{

	/** generated */
	private static final long serialVersionUID = 8318778456748881106L;

	/** e.g. "Tablename.Column" */
	private final String errorMessage;
	private final Set<String> whitelist;
	private CandidateController candidateController;

	public UniqueCandidateProfileNameValidator(final String errorMessage, final CandidateController ctrler, String allowedObject)
	{
		this(errorMessage, ctrler, (Set<String>) null);

		if (allowedObject != null)
			whitelist.add(allowedObject);
	}

	/**
	 * @param databaseTableAndColumn
	 *            e.g. "Tablename.Column"
	 */
	public UniqueCandidateProfileNameValidator(final String errorMessage, final CandidateController candidateController, Set<String> whitelist)
	{
		this.candidateController = candidateController;
		this.errorMessage = errorMessage;
		if (whitelist != null)
			this.whitelist = whitelist;
		else
			this.whitelist = new HashSet<String>();
	}

	@Override
	public void validate(Object value) throws InvalidValueException
	{
		if (value == null)
			return;
		if (whitelist.contains(value))
			return;

		if (!(value instanceof String))
			throw new InvalidValueException(errorMessage);

		try
		{
			String stringValue = (String) value;
			if (stringValue.length() == 0 || candidateController.isCandidateNameAlreadyTaken(stringValue))
			{
				throw new InvalidValueException(errorMessage);
			}
		}
		catch (InvalidValueException ive)
		{
			throw ive;
		}
		catch (Exception exc)
		{
			throw new InvalidValueException(I18NHandler.getText(I18NText.Error_GeneralError) + ": " + exc.getClass().getSimpleName() + " - " + exc.getLocalizedMessage());
		}

	}

}

package com.secunet.eidserver.testbed.web.ui.util;

import java.util.Collection;
import java.util.Map;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

public class CommitHandler
{

	/**
	 * Only working for implementations of AbstractField
	 * 
	 * @param commitException
	 *            could be null
	 */
	public static void displayUserErrorMessageInvalidFields(final CommitException commitException)
	{
		if (commitException == null || commitException.getInvalidFields() == null)
			return;

		Map<Field<?>, InvalidValueException> invalidFields = commitException.getInvalidFields();
		for (Field<?> field : invalidFields.keySet())
		{
			if (field instanceof AbstractField)
				((AbstractField<?>) field).setComponentError(new UserError(invalidFields.get(field).getMessage()));
		}
	}

	/**
	 * removes the component error for AbstractField implementations in the given collection
	 * 
	 * @param fields
	 *            could be null
	 */
	public static void clearUserError(final Collection<Field<?>> fields)
	{
		if (fields == null)
			return;

		fields.forEach(field -> {
			if (field instanceof AbstractField)
				((AbstractField<?>) field).setComponentError(null);
		});
	}
}

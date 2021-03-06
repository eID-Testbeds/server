package com.secunet.eidserver.testbed.web.ui.comp;

import java.text.NumberFormat;
import java.util.Locale;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.vaadin.data.util.converter.StringToDoubleConverter;

/**
 * Removes grouping, done by the default implementation.
 *
 * @see StringToIntegerParameterConverter
 */
public class StringToDoubleParameterConverter extends StringToDoubleConverter
{
	private static final long serialVersionUID = -6667572298962608098L;

	protected boolean useGrouping = false;

	public StringToDoubleParameterConverter(boolean groupingUsed)
	{
		useGrouping = groupingUsed;
	}

	@Override
	protected java.text.NumberFormat getFormat(Locale locale)
	{
		NumberFormat format = super.getFormat(locale);

		format.setGroupingUsed(useGrouping);

		return format;
	};

	@Override
	public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale) throws ConversionException
	{
		try
		{
			return super.convertToModel(value, targetType, locale);
		}
		catch (ConversionException ce)
		{
			throw new ConversionException(I18NHandler.getText(I18NText.Error_ConversionOfValue0toType1Failed, value, getModelType().getSimpleName()), ce);
		}
	}

}

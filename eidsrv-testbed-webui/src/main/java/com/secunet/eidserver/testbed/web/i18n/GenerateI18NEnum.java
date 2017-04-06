package com.secunet.eidserver.testbed.web.i18n;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;

public class GenerateI18NEnum
{
	
	private static final String filepath = "src/main/java/com/secunet/eidserver/testbed/web/i18n/I18NText.java";
	private static final String RESBUNDLE_NAME_GLOBAL = I18NHandler.RESBUNDLE_NAME_GLOBAL;


	public static void main(String[] args) throws IOException
	{
		ResourceBundle bundleEN = ResourceBundle.getBundle(RESBUNDLE_NAME_GLOBAL, Locale.ENGLISH);
		ResourceBundle bundleDE = ResourceBundle.getBundle(RESBUNDLE_NAME_GLOBAL, Locale.GERMAN);
		checkPresence(bundleEN, bundleDE);


		Enumeration<String> keys = bundleEN.getKeys();
		File f = new File(filepath);

		System.out.println(f.getAbsolutePath());
		List<String> sortedKeys = new ArrayList<>();
		while (keys.hasMoreElements())
			sortedKeys.add(keys.nextElement());
		Collections.sort(sortedKeys);

		final ListIterator<String> listIterator = sortedKeys.listIterator();

		try (FileOutputStream fos = new FileOutputStream(f); OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder()); BufferedWriter bw = new BufferedWriter(osw))
		{

			bw.write("package com.secunet.eidserver.testbed.web.i18n;");
			bw.write("\n");
			bw.write("\n");
			bw.write("public enum I18NText\n");
			bw.write("{\n");

			while (listIterator.hasNext())
			{
				String key = listIterator.next();
				String textEn = bundleEN.getString(key);

				bw.write("\n");
				bw.write("\t/** EN:'" + textEn + "' */\n");
				bw.write("\t" + key.replace(".", "_") + "(\"" + key + "\")");
				if (listIterator.hasNext())
					bw.write(",\n");
				else
					bw.write(";\n");

			}

			bw.write("\n");
			bw.write("\n");
			bw.write("\tprivate final String resource;\n");
			bw.write("\n");
			bw.write("\tI18NText(final String resCode)\n");
			bw.write("\t{\n");
			bw.write("\t\tresource = resCode;\n");
			bw.write("\t}\n");
			bw.write("\n");
			bw.write("\tpublic String getKey()\n");
			bw.write("\t{\n");
			bw.write("\t\treturn resource;\n");
			bw.write("\t}\n");
			bw.write("}\n");
		}
		System.out.println("Done!");
	}


	public static void checkPresence(ResourceBundle bundleEN, ResourceBundle bundleDE)
	{
		final Enumeration<String> checkKeysEN = bundleEN.getKeys();
		final Enumeration<String> checkKeysDE = bundleDE.getKeys();
		final HashSet<String> keysEN = new HashSet<>();
		final HashSet<String> keysDE = new HashSet<>();

		while (checkKeysEN.hasMoreElements())
			keysEN.add(checkKeysEN.nextElement());
		while (checkKeysDE.hasMoreElements())
			keysDE.add(checkKeysDE.nextElement());

		keysEN.forEach(k -> {
			if (!bundleDE.containsKey(k))
				System.err.println("'" + k + "'\t not found in DE");
		});
		keysDE.forEach(k -> {
			if (!bundleEN.containsKey(k))
				System.err.println("'" + k + "'\t not found in EN");
		});
	}
}

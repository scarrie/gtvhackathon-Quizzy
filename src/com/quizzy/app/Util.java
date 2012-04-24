package com.quizzy.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Util {
	public static String getStringFromStream(InputStream is) throws IOException 
	{
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(is, "UTF-8");
		try {
		  int read;
		  do {
		    read = in.read(buffer, 0, buffer.length);
		    if (read>0) {
		      out.append(buffer, 0, read);
		    }
		  } while (read>=0);
		} finally {
		  in.close();
		}
		String result = out.toString();
		return result;
	}

}

/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 14, 2009 1:24:36 PM
 */
public interface NotifyEventFilter {

	public boolean accept(NotifyEvent ne);
}

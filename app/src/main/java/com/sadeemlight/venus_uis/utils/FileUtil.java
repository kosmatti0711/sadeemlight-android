package com.sadeemlight.venus_uis.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

/**
 * This utility class implements a method for copying a whole directory
 * to a new location in the file system.
 * @author www.codejava.net
 *
 */
public class FileUtil {

	/**
	 * Copy a whole directory to another location.
	 * @param sourceDir a File object represents the source directory
	 * @param destDir a File object represents the destination directory
	 * @throws IOException thrown if IO error occurred.
	 */
	public static void copyDirectory(File sourceDir, File destDir)
			throws IOException {
		// creates the destination directory if it does not exist
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		// throws exception if the source does not exist
		if (!sourceDir.exists()) {
			throw new IllegalArgumentException("sourceDir does not exist");
		}

		// throws exception if the arguments are not directories
		if (sourceDir.isFile() || destDir.isFile()) {
			throw new IllegalArgumentException(
					"Either sourceDir or destDir is not a directory");
		}

		copyDirectoryImpl(sourceDir, destDir);
	}

	/**
	 * Internal implementation of the copy method.
	 * @param sourceDir a File object represents the source directory
	 * @param destDir a File object represents the destination directory
	 * @throws IOException thrown if IO error occurred.
	 */
	private static void copyDirectoryImpl(File sourceDir, File destDir)
			throws IOException {
		File[] items = sourceDir.listFiles();
		if (items != null && items.length > 0) {
			for (File anItem : items) {
				if (anItem.isDirectory()) {
					// create the directory in the destination
					File newDir = new File(destDir, anItem.getName());
					System.out.println("CREATED DIR: "
							+ newDir.getAbsolutePath());
					newDir.mkdir();

					// copy the directory (recursive call)
					copyDirectory(anItem, newDir);
				} else {
					// copy the file
					File destFile = new File(destDir, anItem.getName());
					copySingleFile(anItem, destFile);
				}
			}
		}
	}

	/**
	 * Copy a file from a location to another
	 * @param sourceFile a File object represents the source file
	 * @param destFile a File object represents the destination file
	 * @throws IOException thrown if IO error occurred.
	 */
	public static void copySingleFile(File sourceFile, File destFile)
			throws IOException {
		System.out.println("COPY FILE: " + sourceFile.getAbsolutePath()
				+ " TO: " + destFile.getAbsolutePath());
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel sourceChannel = null;
		FileChannel destChannel = null;

		try {
			sourceChannel = new FileInputStream(sourceFile).getChannel();
			destChannel = new FileOutputStream(destFile).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
		} finally {
			if (sourceChannel != null) {
				sourceChannel.close();
			}
			if (destChannel != null) {
				destChannel.close();
			}
		}
	}

	public static void deleteDirectory( File dir )
	{
		if ( dir.isDirectory() )
		{
			String [] children = dir.list();
			for ( int i = 0 ; i < children.length ; i ++ )
			{
				File child =    new File( dir , children[i] );
				if(child.isDirectory()){
					deleteDirectory( child );
					child.delete();
				}else{
					child.delete();

				}
			}
			dir.delete();
		}
		else
		{
			dir.delete();
		}
	}

	public static boolean writeFileWithString(File file, String data)
	{
		try
		{
			file.createNewFile();
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);

			myOutWriter.close();

			fOut.flush();
			fOut.close();
		}
		catch (IOException e)
		{
			Log.e("SAEEMLIGHT", "File write failed: " + e.toString());
			return false;
		}

		return true;
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String readStringFromFile (File file) throws Exception {
		FileInputStream fin = new FileInputStream(file);
		String ret = convertStreamToString(fin);
		//Make sure you close all streams.
		fin.close();
		return ret;
	}
}
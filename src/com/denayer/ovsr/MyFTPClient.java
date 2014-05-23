/*
 * Copyright (C) <2014> <Dries Goossens / driesgoossens93@gmail.com , Koen Daelman / koendaelman@gmail.com >
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
*/
package com.denayer.ovsr;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.*;

import android.content.Context;
import android.util.Log;

public class MyFTPClient {
	
	//Now, declare a public FTP client object.

	private static final String TAG = "MyFTPClient";
	public FTPClient mFTPClient = null; 

	/*! \brief Method to connect to FTP server
	 * 
	 * 	This function connects to a FTP server and creates a client object to be used in other functions
	 * @param host is the IP of the host server
	 * @param username is the user name to login with
	 * @param password is the password that matches with the username
	 * @param port is the FTP port of the server
	 * @return This function returns true of the connection is made
	 */
	public boolean ftpConnect(String host, String username,
	                          String password, int port)
	{
	    try {
	        mFTPClient = new FTPClient();
	        // connecting to the host
	        mFTPClient.connect(host, port);

	        // now check the reply code, if positive mean connection success
	        if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
	            // login using username & password
	            boolean status = mFTPClient.login(username, password);

	            /* Set File Transfer Mode
	             *
	             * To avoid corruption issue you must specified a correct
	             * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
	             * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE
	             * for transferring text, image, and compressed files.
	             */
	            mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
	            mFTPClient.enterLocalPassiveMode();

	            return status;
	        }
	    } catch(Exception e) {
	        Log.d(TAG, "Error: could not connect to host " + host );
	    }

	    return false;
	} 

	//Method to disconnect from FTP server:
	/*! \brief This function disconnects the client that is made in the ftpconnect function.
	 * @return Returns true if disconnection is successful.
	 */
	public boolean ftpDisconnect()
	{
	    try {
	        mFTPClient.logout();
	        mFTPClient.disconnect();
	        return true;
	    } catch (Exception e) {
	        Log.d(TAG, "Error occurred while disconnecting from ftp server.");
	    }

	    return false;
	} 

	/*! \brief Method to get current working directory
	 * 	
	 * @return This function returns the current working directory. If the function returns null, you have a connection error.
	 */
	public String ftpGetCurrentWorkingDirectory()
	{
	    try {
	        String workingDir = mFTPClient.printWorkingDirectory();
	        return workingDir;
	    } catch(Exception e) {
	        Log.d(TAG, "Error: could not get current working directory.");
	    }

	    return null;
	} 

	/*! \brief Method to change working directory
	 * 	
	 * @param directory_path is the path to change the working directory to. 
	 * @return This function returns true if the path is changed
	 */
	public boolean ftpChangeDirectory(String directory_path)
	{
	    try {
	        mFTPClient.changeWorkingDirectory(directory_path);
	        return true;
	    } catch(Exception e) {
	        Log.d(TAG, "Error: could not change directory to " + directory_path);
	    }

	    return false;
	} 

	
	/*! \brief Method to list all files in a directory
	 * 	
	 * @param dir_path is the path to list all files from
	 */
	public void ftpPrintFilesList(String dir_path)
	{
	    try {
	        FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
	        int length = ftpFiles.length;

	        for (int i = 0; i < length; i++) {
	            String name = ftpFiles[i].getName();
	            boolean isFile = ftpFiles[i].isFile();

	            if (isFile) {
	                Log.i(TAG, "File : " + name);
	            }
	            else {
	                Log.i(TAG, "Directory : " + name);
	            }
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	} 
	/*! \brief Method to create new directory
	 * 	
	 * @param new_dir_path is the name of the new directory to be created
	 * @return Returns true if a new directory is been made
	 */
	public boolean ftpMakeDirectory(String new_dir_path)
	{
	    try {
	        boolean status = mFTPClient.makeDirectory(new_dir_path);
	        return status;
	    } catch(Exception e) {
	        Log.d(TAG, "Error: could not create new directory named " + new_dir_path);
	    }

	 return false;
	} 
	/*! \brief Method to delete/remove a directory
	 * 	
	 * @param dir_path is the name of the directory to be deleted
	 * @return Returns true if the directory is deleted
	 */
	public boolean ftpRemoveDirectory(String dir_path)
	{
	    try {
	        boolean status = mFTPClient.removeDirectory(dir_path);
	        return status;
	    } catch(Exception e) {
	        Log.d(TAG, "Error: could not remove directory named " + dir_path);
	    }

	    return false;
	} 

	/*! \brief Method to delete a file
	 * 	
	 * @param filePath is the name of the file to be deleted
	 * @return Returns true if the file is deleted
	 */
	public boolean ftpRemoveFile(String filePath)
	{
	    try {
	        boolean status = mFTPClient.deleteFile(filePath);
	        return status;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	} 
	/*! \brief Method to rename a file
	 * 	
	 * @param from is the name of the file to be renamed
	 * @param to is the new name of the file
	 * @return Returns true if the file is renamed
	 */
	public boolean ftpRenameFile(String from, String to)
	{
	    try {
	        boolean status = mFTPClient.rename(from, to);
	        return status;
	    } catch (Exception e) {
	        Log.d(TAG, "Could not rename file: " + from + " to: " + to);
	    }

	    return false;
	} 
	/*! \brief Method to download a file from FTP server
	 * 	
	 * @param srcFilePath path to the source file in FTP server
	 * @param desFilePath path to the destination file to be saved in sdcard
	 * @return Returns true if the file is downloaded
	 */
	public boolean ftpDownload(String srcFilePath, String desFilePath)
	{
	    boolean status = false;
	    try {
	        FileOutputStream desFileStream = new FileOutputStream(desFilePath);;
	        status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
	        desFileStream.close();

	        return status;
	    } catch (Exception e) {
	        Log.d(TAG, "download failed");
	    }

	    return status;
	} 

	/*! \brief Method to upload a file to FTP server
	 * @param srcFilePath source file path in sdcard
	 * @param desFileName file name to be stored in FTP server
	 * @param desDirectory directory path where the file should be upload to
	 * @param context is the connect of the calling class
	 * @return Returns true if the file is uploaded
	 */
	public boolean ftpUpload(String srcFilePath, String desFileName,
	                         String desDirectory, Context context)
	{
	    boolean status = false;
	    try {
	       // FileInputStream srcFileStream = new FileInputStream(srcFilePath);
	        
	        FileInputStream srcFileStream = context.openFileInput(srcFilePath);

	        // change working directory to the destination directory
	        //if (ftpChangeDirectory(desDirectory)) {
	            status = mFTPClient.storeFile(desFileName, srcFileStream);
	        //}

	        srcFileStream.close();
	        return status;
	    } 
	    catch (Exception e) {
	        Log.d(TAG, "upload failed: " + e);
	    }

	    return status;
	}
}

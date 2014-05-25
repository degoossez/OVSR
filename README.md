#README

This README will be a step by step tutorial on how to set up the OVSR application and server needed to cross-compile RenderScript.
To compile the OVSR source code you will need the [Android Developer Tools (ADT) ](https://developer.android.com/sdk/installing/bundle.html) and [Android NDK](https://developer.android.com/tools/sdk/ndk/index.html#Installing).
##OVSR
####Getting the source code
* Open a terminal window
* copy / paste " git clone https://github.com/degoossez/OVSR "

####Import into eclipse
* File 
* Import
* Android > Existing Android Code Into Workspace
* Browse to the OVSR directory you cloned in step 1
* Finish

####Setup the NDK
* Window > Preferences > Android > NDK
* Browser to the NDK directory, example: /home/user/android-ndk-r9d
* Apply and Ok

####Add NDK to OVSR project
* Right click on the OVSR project
* Android Tools > add native support 
* The name must be "libOVSR.so" so type OVSR in the input box!
* Click finish

####Add file browser library
* svn checkout http://android-file-dialog.googlecode.com/svn/trunk/ android-file-dialog-read-only
* Import the project into eclipse
* Build the project (don't run!) , use the build button or Ctrl + b
* There is a fileexplorer.jar in the bin directory of the fileexplorer project
* Right click on the OVSR project > Properties > Android > Add > FileExplorer
* When the jar is not created, follow this tutorial: http://aplacetogeek.wordpress.com/android-embedded-file-browser/

####Add video support
* Download the library's from (Ctrl + S to download them all at once!) https://drive.google.com/file/d/0B2MNqrU4BEonek5ldENzMlNSV2M/edit?usp=sharing  
* Unzip it into a folder you can remember
* In OVSR's libs folder you can find "JavaCV_Copy_Script.sh" 
* Open it and edit the second line to cp -r /the/path/where/you/extracted/the/library/* .
* Example: cp -r ~/AndroidJavaCVLibs/* .
* Save and close it
* Right click on the project > properties > Builders > New > Program > Ok
* Name: JavaCV_Copy_Script
* Location: ${workspace_loc:/OVSR/libs/JavaCV_Copy_Script.sh}
* Working directory: ${workspace_loc:/OVSR/libs}
* Ok
* Check it

####RenderScript

The RenderScript code should work without any additional changes. The project does **not** make use of the support library, so if you want to
run the application on an older device, take a look at the following link. http://www.doubleencore.com/2013/10/renderscript-for-all/
####OVSR server

The server is only necessary if you want to compile RenderScript code at runtime.
You also need to install this [FTP server](https://help.ubuntu.com/community/PureFTP) (very easy) which the application uses to download de bytecode from. It's highly recommended to use the same paths as in the tutorial.
To setup the OVSR server, the following steps need to be followed:
* Open a terminal window
* Copy / paste " git clone https://github.com/degoossez/OVSRServer"
* Copy the folder to a location of your preference
* Open de OVSRServer.pro file located in the OVSR directory with Qt Creator
* Inside Qt Creator locate the settings.h file under the Headers directory
* Adjust these settings to match your own configuration
 * **PORT** port the server listens on 
 * **SDK_PATH** path where your SDK is installed
 * **BUILD_TOOLS_VERSION** version of the build tools that's installed on your system
 * **FTP_USERS_DIR** directory where all FTP users are stored
 * **FTP_GROUP** group of the FTP users
* If you followed the FTP tutorial exactly, the last two settings don't have to be changed 

####Troubleshooting
* When calling the RenderScript compiler in the server I get the following error message: **./llvm-rs-cc: error while loading shared libraries: libclang.so: cannot open shared object file: No such file or directory**
 * cd /etc/ld.so.conf.d
 * create a new file : sudo nano renderscript.conf
 * write the path to your build tools inside the file, for example : [HOME]/android-sdk-linux/build-tools/19.0.0/
 * save the file 
 * execute ldconfig to reload the shared libraries : sudo ldconfig


_Describe how to install the project before running predictors_

# Conventions #

  * %DATA\_SET% - Directory containing the extracted data set files supplied by Netflix (containing the download folder).
  * %PROJECT\_HOME% - Directory containing the oknetflix project source.

# Installation Steps #

  1. Download the supplied betterprobe.txt from the project's Downloads page and save it under %DATA\_SET%/downloads (the directory that contains the training\_set directory with all 17770 text files).
  1. Open the project source code with your preferred IDE. **In Eclipse:** go to "File->Import->Existing Projects Into Workspace" and select the %PROJECT\_HOME% folder (Please notice that if you don't use Eclipse you'll need to manually add the supplied jama1.0.2.jar file located under %PROJECT\_HOME%/lib to the project's classpath).
  1. Change NETFLIX\_MODEL\_DIR initialization in "Constants.java" file from "E:/finalproject..." into %DATA\_SET%/downloads (the folder containing the downloaded betterprobe.txt file).
  1. Change NETFLIX\_OUTPUT\_DIR initialization in "Constants.java" file from "E:/finalproject..." into %PROJECT\_HOME%/binFiles.
  1. Compile "Installer.java" and run the compiled code. **In Eclipse:** open the Installer class (ctrl+shift+t) and go to "Run->Run Configurations...". Then, add a new "Java Application" runner and write the following parameters in the arguments tab under "VM Arguments": "-Xmx3300m -Xms3300m". Then simply press "Run" and wait until the class will finish running.

Now you are ready to move on to the [Usage](Usage.md) wiki for instructions on how to run the various predictors.
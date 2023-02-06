About running the source code for CS5011 A2, several steps are needed. 
1.	Enter the terminal.

2.	Change to the CS5011_A2 directory.

3.	Compile all source code
	javac *.java help/*.java agent/*.java startCode/*.java -cp .;../libs/*

4.	Run the source code
	java -cp .;../libs/* A2main <P1|P2|P3|P4> <ID> [verbose]
	
	The “-cp .;../libs/*” is used to include the libraries needed in our projects
	For example, if I want to use SPS to player the mine sweeper game of TEST1, I should use
 	     java -cp .;../libs/* A2main P2 TEST1 


If there shows an error like 
“/cs/studres/2021_2022/CS5011/Practicals/A2/Tests/build-all.sh: line 4: ./playSweeper.sh: Permission denied”

Please get into /src/ at first, and then run this command to get a permission
  chmod 777 playSweeper.sh


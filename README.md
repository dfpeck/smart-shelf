<!-- Note: GitHub interprets this file as Markdown. Information on Markdown
syntax may be found at daringfireball.net/projects/markdown/syntax -->

# smart-shelf #
Code for The Green's automatic inventory system.

# Software Requirements #
## H2  ##
Most classes in the `db` directory require the [H2 database
engine](http://h2database.com) to be installed and in your Java classpath.
## Java ##
NetNode and run code may be compiled using:

javac -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ @compileNetMat

javac -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ @compileNetUI

javac -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ @compileStartServer

and then ran using:

java -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ run.MainMat

java -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ run.TEST_MainUI

java -cp /full/path/to/smart-shelf/db/h2-1.4.197.jar:/full/path/to/smart-shelf/ run.StartServer

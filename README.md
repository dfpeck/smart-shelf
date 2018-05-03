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

# Style Guidelines #
## Code ##
- Restrict line length to 80 characters (as best as possible).
- Generally pattern code style after Java conventions, as the majority of our code
  is in Java.

## Makefile ##
- Include binaries/executables in the BIN variable.
- Include *all* recipes as dependencies to the `all` recipe, except `all` itself
  (obviously).

## Directories and Filenames ##
- Create subdirectories in the base directory for each major project
  component. The names of these folders should be all lowercase.
- Output binaries/executables to the repository's base directory.
- Prefix the names of test files with `TEST_` in all caps. That way, we can
  filter such files out when we build for distribution. Windows users: while its
  true that Windows filenames aren't case sensitive, the file paths used by OSX
  and \*Nix systems *are*. In the interest of code-portability, please pay
  attention to case when you name your files.

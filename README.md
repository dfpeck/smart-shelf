<!-- Note: GitHub interprets this file as Markdown. Information on Markdown
syntax may be found at daringfireball.net/projects/markdown/syntax -->

# smart-shelf #
Code for The Green's automatic inventory system.

# Style Guidelines #
## Code ##
- Restrict line length to 80 characters (as best as possible).

### C/C++ ###
- In names, use underscores as separators when a name...
    - begins with a lowercase character
    - consists entirely of uppercase characters<br>
    Examples:
        - o `this_is_correct`
        - o `THIS_IS_ALSO_CORRECT`
        - o `DittoForThis`
        - x `thisIsIncorrect`
        - x `This_Too_Is_Incorrect`

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

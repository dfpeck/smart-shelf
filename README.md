<!-- Note: GitHub interprets this file as Markdown. Information on Markdown
syntax may be found at daringfireball.net/projects/markdown/syntax -->

# smart-shelf
Code for The Green's automatic inventory system.

# Style Guidelines
## Code
- Use a 2-space indent width in your code.
    - I only picked 2-space because it's the default in Emacs and what I've been
        working with so far. If anyone has a preference for something else
        (e.g. 4-space indents), let me know; it's not much fuss to
        change. ---dfpeck
- Restrict line length to 80 characters (as best as possible).
### C/C++
- In names, use underscores as separators when a name...
    - begins with a lowercase character
    - consists entirely of uppercase characters<br>
    Examples:
        - o `this_is_correct`
        - o `THIS_IS_ALSO_CORRECT`
        - o `DittoForThis`
        - x `thisIsIncorrect`
        - x `This_Too_Is_Incorrect`

## Directories and Filenames
- Output binaries/executables to the repository's base directory.
- Prefix the names of test files with `TEST_` in all caps. That way, we can
  filter such files out when we build for distribution. Windows users: while its
  true that Windows filenames aren't case sensitive, the file paths used by OSX
  and \*Nix systems *are*. In the interest of code-portability, please pay
  attention to case when you name your files.

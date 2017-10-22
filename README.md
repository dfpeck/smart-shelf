<!-- Note: GitHub interprets this file as Markdown. Information on Markdown
syntax may be found at daringfireball.net/projects/markdown/syntax -->

# smart-shelf
Code for The Green's automatic inventory system.

# Style Guidelines
## Code
- Use a 2-space indent width in your code
  - I only picked 2-space because it's the default in Emacs and what I've been
    working with so far. If anyone has a preference for something else
    (e.g. 4-space indents), let me know; it's not much fuss to
    change. ---dfpeck

## Filenames
- Prefix testing code's filenames with `TEST_` in all caps. That way, we can
  filter such files out when we build for distribution. Windows users: I know
  Windows filenames aren't case sensitive, but the POSIX file paths used by OSX
  and \*Nix systems *are*. In the interest of code-portability, please pay
  attention to case when you name your files.

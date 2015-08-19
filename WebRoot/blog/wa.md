<!--
            tags:eeee,test
name:dddfdf
data:eeeer
-->

# markdown-js

Yet another Markdown parser, this time for JavaScript. There's a few
options that precede this project but they all treat Markdown to HTML
conversion as a single step process. You pass Markdown in and get HTML
out, end of story. We had some pretty particular views on how the
process should actually look, which include:

  * Producing well-formed HTML. This means that `em` and `strong` nesting
    is important, as is the ability to output as both HTML and XHTML
  * Having an intermediate representation to allow processing of parsed
    data (we in fact have two, both [JsonML]: a markdown tree and an HTML tree)
  * Being easily extensible to add new dialects without having to
    rewrite the entire parsing mechanics
  * Having a good test suite. The only test suites we could find tested
    massive blocks of input, and passing depended on outputting the HTML
    with exactly the same whitespace as the original implementation



http://sssssqqwq//wqw
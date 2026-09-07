# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 1.1.x   | Yes       |
| < 1.1   | No        |

## Reporting a vulnerability

Please report suspected vulnerabilities privately, **not** as a public issue.

Use GitHub's private reporting form:
<https://github.com/fangyidong/json-simple/security/advisories/new>

If you cannot use GitHub, email <fangyidong@gmail.com> instead.

Please include a minimal reproducer and the version you tested against. You can
expect an initial response within 14 days.

## Known limitations

These are documented behaviours rather than vulnerabilities, but they matter when
parsing untrusted input:

* **Encoding is recursive.** `JSONValue.toJSONString` recurses once per level of
  nesting, so a deep enough structure exhausts the stack and throws
  `StackOverflowError` — an `Error`, so ordinary `catch` blocks do not stop it.
  There is no fixed safe depth: the limit follows the thread's stack size, and
  was measured at roughly 12,000 levels on a default 1 MB stack but under 2,000
  on a 512 KB stack. Decoding is iterative and is not affected at any depth.
  Bound the size of untrusted JSON before round-tripping it. A configurable
  depth limit is planned for 1.2.0.
* **The parser is lenient**, accepting trailing commas, optional separators, and
  leading zeros. If you rely on json-simple to reject malformed input, do not.
  See the leniency table in README.md.
* **No input size limits.** A large document consumes memory proportional to its
  size. Apply your own limits before parsing untrusted data.

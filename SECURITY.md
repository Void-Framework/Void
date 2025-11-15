# Security Policy

This document describes how to report vulnerabilities, what versions are supported with fixes, and guidance for using
Void securely.

## Supported Versions

We currently provide security updates for the latest release line.

| Version | Supported |
|--------:|:---------:|
|  latest |     ✅     |
|   older |     ❌     |

If you are using an older version, please upgrade to the latest release.

## Reporting a Vulnerability

Please report security issues privately so we can triage and fix them before public disclosure.

- Do not open a public issue for security problems
- Preferred contact: security reports via email to jadeczjade008@gmail.com
- Include: affected version(s), environment, PoC or reproduction steps, and impact assessment if known
- If the issue involves a third-party dependency, note the dependency and version

We will:

- Acknowledge your report within 48 hours
- Provide an initial assessment within 5 business days
- Coordinate a fix and release, and credit reporters who request it

## Disclosure Policy

- We practice responsible coordinated disclosure
- We will propose a disclosure timeline after triage based on severity
- We may request a short embargo until a fixed version is available

## Scope and Safe Harbor

- Scope: code in this repository and official modules under the io.voidx.* namespace
- Out of scope: issues exclusively in downstream applications built with Void
- Safe Harbor: we will not initiate legal action for good-faith research and reporting that follows this policy and
  avoids privacy violations and service disruption

## Security Guidance for Users

- Always use HTTPS in production; consider enabling the HTTPS server with a valid certificate
- Validate and sanitize user input; middleware can help implement cross-cutting checks
- Set security-related HTTP headers in responses (e.g., X-Content-Type-Options, Content-Security-Policy) via middleware
  or response builders
- Keep dependencies updated (consider Dependabot/GitHub Alerts)
- If exposing the admin or developer endpoints, protect them behind authentication

## Contact and PGP

- Primary: jadeczjade008@gmail.com
- If you require encrypted communication, open an issue requesting a PGP key exchange (do not include vulnerability
  details in that issue)

## License

Void is licensed under the MIT License. See LICENSE for details.

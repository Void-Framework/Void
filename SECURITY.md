# 🛡️ Security Policy

## Project: Void (Kotlin Web Framework)

### Supported Versions

We currently maintain the latest stable release. Security updates are applied as needed.

| Version       | Supported |
|---------------|-----------|
| Latest        | ✅        |
| Older releases| ❌        |

### 📢 Reporting a Vulnerability

If you discover a security vulnerability in Void or any of its submodules:

- **DO NOT** open a public GitHub issue.
- Please email us at: **[jadeczjade008@gmail.com]**
- Provide a detailed description of the issue, its potential impact, and steps to reproduce.

We will acknowledge receipt within **48 hours** and aim to provide a fix or mitigation plan within **7 days**, depending on the severity.

### 🔒 Security Best Practices

If you're using Void in production, we recommend following these security guidelines:

- **Validate all inputs**: Void assumes that developers handle input validation properly.
- **Use HTTPS** in production environments.
- **Restrict reflective access** when using custom Kotlin class renderers.
- **Keep dependencies updated**: Use tools like [Dependabot](https://github.com/dependabot) to keep your dependencies secure.
- **Audit your code** before deploying. Void offers flexibility, but that means you are responsible for maintaining security.

### 📄 License

Void is licensed under the **MIT License**. See the [LICENSE](./LICENSE) file for full terms.

---

Thank you for using Void! If you have any questions or suggestions, feel free to open an issue or contact us directly.

# Void Framework 🌌

The **Void Framework** is a modular web framework designed to be lightweight, flexible, and easy to extend. Inspired by frameworks like Spring, Void aims to provide a solid foundation for building web applications, while offering optional submodules to extend its functionality.

## Features ✨
- 🖥️ **Core Framework:** A lightweight core to build web applications
- 📦 **Modular Design:** Add or remove submodules as needed
- 🛠️ **Customizable:** Easily extend the framework to fit your needs
- ⚙️ **Planned Submodules:** ORM, security, routing, and more

## Getting Started 🚀
### Prerequisites 📋
- 🖥️ **Java 17+**
- 📦 **Gradle**

### Installation 📂
Since Void Framework is currently not hosted on any public repository, you can clone the project locally and add it as a dependency to your project:
```bash
git clone https://github.com/Jadiefication/void-framework.git
```
Then, add the local path to your build tool configuration.

## Usage 💻
Here's a quick example to get started in Kotlin:
```kotlin
package main.java.main.Test

import main.java.main.Server.Server

fun main() {
    val server = Server()
    server.startServer(8080)
}
```

### Server Class Overview 📝
Here's a brief overview of how the `Server` class works:
- 🔧 **`Server()` constructor**: Initializes the server and creates a `clients.txt` file to store client data.
- 🚀 **`startServer(port: Int)` method**: Starts the server on the specified port and accepts client connections.
- 📡 **Command Extraction**: The server listens for input commands like `ERROR(message)`, `WARN(message)`, `LOG(message)`, and `ALERT(message)` to handle logs dynamically.

Example command inputs:
```shell
> ❌ ERROR(Something went wrong!)
> ⚠️ WARN(This might be an issue)
> 📜 LOG(Server started successfully)
> 🚨 ALERT(Important message!)
```

## Roadmap 🛤️
The following features and submodules are planned for future releases:
- 🗄️ **ORM (Object-Relational Mapping)**: Simplify database interactions
- 🌐 **WebSocketServer Enhancements**: Add arguments to customize the WebSocket server
- 🔐 **Security**: Authentication and authorization mechanisms
- 🛣️ **Routing**: Handle HTTP requests and responses
- 🖼️ **Templating**: Dynamic HTML generation
- 📦 **Dependency Injection**: Manage components and services

## Contributing 🤝
Contributions are welcome! Feel free to fork the repository and submit a pull request.

### How to Contribute 🛠️
1. 🍴 **Fork the repository**
2. 🌱 **Create a new branch**
3. ✏️ **Make your changes**
4. 📩 **Submit a pull request**

## License 📄
This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

Made with ❤️ by [Jadiefication](https://github.com/Jadiefication)


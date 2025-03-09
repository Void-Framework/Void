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
- 📦 **Maven**

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
import main.router.Router

val router = Router().addRoutes()

fun main() {
    val server = Server(router = router)

    server.startServer(8080)

}
```

### Server Class Overview 📝
Here's a brief overview of how the `Server` class works:
- 🔧 **`Server()` constructor**: Initializes the server.
- 🚀 **`startServer(port: Int)` method**: Starts the server on the specified port and accepts client connections.

## Roadmap 🛤️
The following features and submodules are planned for future releases:
- 🗄️ **ORM (Object-Relational Mapping)**: Simplify database interactions
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


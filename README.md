<div align="center">
    <h1>Void Framework 🌌</h1><br>
  <img src=".github/image.png" alt="Centered Image">
    <hr>
</div>

The **Void Framework** is a modular web framework designed to be lightweight, flexible, and easy to extend. Inspired by frameworks like Spring, Void aims to provide a solid foundation for building web applications, while offering optional submodules to extend its functionality.

## Use 🚀
### Prerequisites 📋
- 🖥️ **Java 17+**
- 📦 **A build tool of your choice**

### Installation 📂
Since Void Framework is currently not hosted on any public repository, you can clone the project locally and add it as a dependency to your project:
```bash
https://github.com/Jadiefication/Void.git
```
Then, add the local path to your build tool configuration.

## Usage 💻
Here's a quick example to get started in Kotlin:
```kotlin
package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router

val router = Router().addRoutes(HomeRoute())

fun main() {
    val server = Server(router = router)

    server.startServer(8080)

}
```

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


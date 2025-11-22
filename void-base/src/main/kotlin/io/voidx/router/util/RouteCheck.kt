package io.voidx.router.util

import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.page.ExceptionPage
import io.voidx.page.NotFoundPage
import io.voidx.page.Page
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import io.voidx.router.exceptions.RouteNoTargetException
import io.voidx.router.exceptions.RouteTargetUsedException
import java.util.concurrent.ConcurrentHashMap

internal interface RouteCheck {
    /**
     * Validates and registers the [route] target into [routes].
     *
     * - Throws [io.voidx.router.exceptions.RouteTargetUsedException] if the path is already in use.
     * - Throws [io.voidx.router.exceptions.RouteNoTargetException] if the path does not start with "/".
     */
    fun handleTargetChecking(
        route: Page,
        routes: ConcurrentHashMap<String, Page>,
    ) {
        if (routes.containsKey(route.target)) {
            throw RouteTargetUsedException(target = route.target)
        } else {
            if (route.target.startsWith("/")) {
                routes[route.target] = route
            } else {
                throw RouteNoTargetException(target = route.target)
            }
        }
    }

    /**
     * Defaults used by the router when an exception is thrown or a route is not found.
     * These can be overridden by registering custom pages in the router.
     */
    companion object {
        internal var exceptionPage: ExceptionPage =
            exceptionPage {
                val ex = exception
                return@exceptionPage buildResponse {
                    status = 500
                    statusText = "Server Error"
                    headers {
                        put("Content-Type", "text/html")
                        put("Connection", "close")
                    }
                    body = "<!doctype html><html>" +
                            "<head>" +
                            "  <style>" +
                            "#__next-dev-overlay {\n" +
                            "  position: fixed;\n" +
                            "  top: 0;\n" +
                            "  left: 0;\n" +
                            "  width: 100%;\n" +
                            "  height: 100%;\n" +
                            "  background: rgba(0, 0, 0, 0.8);\n" +
                            "  color: #fff;\n" +
                            "  font-family: system-ui, sans-serif;\n" +
                            "  z-index: 2147483647; /* Ensures it stays on top */\n" +
                            "}\n" +
                            "\n" +
                            "/* Main overlay styling */\n" +
                            ".overlay {\n" +
                            "  max-width: 800px;\n" +
                            "  margin: 50px auto;\n" +
                            "  background: #1e1e1e;\n" +
                            "  padding: 20px;\n" +
                            "  border-radius: 4px;\n" +
                            "  box-shadow: 0 2px 10px rgba(0,0,0,0.3);\n" +
                            "}\n" +
                            "\n" +
                            "/* Header section with title and close button */\n" +
                            ".overlay__header {\n" +
                            "  display: flex;\n" +
                            "  justify-content: space-between;\n" +
                            "  align-items: center;\n" +
                            "  margin-bottom: 15px;\n" +
                            "}\n" +
                            "\n" +
                            ".overlay__title {\n" +
                            "  font-size: 1.5em;\n" +
                            "  font-weight: bold;\n" +
                            "}\n" +
                            "\n" +
                            ".overlay__close {\n" +
                            "  background: transparent;\n" +
                            "  border: none;\n" +
                            "  font-size: 1.5em;\n" +
                            "  color: #fff;\n" +
                            "  cursor: pointer;\n" +
                            "}\n" +
                            "\n" +
                            "/* Styling for the error message and stack trace */\n" +
                            ".overlay__content {\n" +
                            "  color: #FF4C4C;\n" +
                            "}\n" +
                            ".error-message pre,\n" +
                            ".stack-trace pre {\n" +
                            "  margin: 0;\n" +
                            "  padding: 10px;\n" +
                            "  overflow: auto;\n" +
                            "  background: #2d2d2d;\n" +
                            "  border-radius: 4px;\n" +
                            "  font-size: 0.9em;\n" +
                            "}" +
                            "  </style>" +
                            "</head>" +
                            "<body>" +
                            "<div id=\"__next-dev-overlay\">\n" +
                            "  <div class=\"overlay\">\n" +
                            "    <div class=\"overlay__header\">\n" +
                            "      <span class=\"overlay__title\">${ex::class.simpleName}: ${ex.message ?: ""}</span>\n" +
                            "    </div>\n" +
                            "    <div class=\"overlay__content\">\n" +
                            "      <div class=\"error-message\">\n" +
                            "        <pre>${ex::class.simpleName}: ${ex.message ?: ""}</pre>\n" +
                            "      </div>\n" +
                            "      <div class=\"stack-trace\">\n" +
                            "        <pre>\n" +
                            "          ${ex.stackTrace.joinToString("\n")}\n" +
                            "        </pre>\n" +
                            "      </div>\n" +
                            "    </div>\n" +
                            "  </div>\n" +
                            "</div>" +
                            "</body>" +
                            "</html>"
                }
            }
        internal var nullPage: NotFoundPage =
            notFoundPage {
                val req = request
                return@notFoundPage buildResponse {
                    status = 404
                    statusText = "Not Found"
                    headers {
                        put("Content-Type", "text/html")
                        put("Connection", "close")
                    }
                    body =
                        """
                        <!doctype html>
                        <html lang="en">
                        <head>
                            <meta charset="utf-8">
                            <title>404 | Page Not Found</title>
                            <style>
                                body {
                                    margin: 0;
                                    height: 100vh;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    font-family: system-ui, sans-serif;
                                    background: #0f0f0f;
                                    color: #e5e5e5;
                                }
                                .container {
                                    text-align: center;
                                    padding: 2rem;
                                    max-width: 600px;
                                }
                                h1 {
                                    font-size: 5rem;
                                    margin-bottom: 0.5rem;
                                    color: #ff5555;
                                }
                                p {
                                    margin: 0.5rem 0 1.5rem;
                                    color: #aaa;
                                }
                                a {
                                    color: #61dafb;
                                    text-decoration: none;
                                    font-weight: 600;
                                    border: 1px solid #61dafb;
                                    border-radius: 6px;
                                    padding: 0.5rem 1rem;
                                    transition: 0.2s;
                                }
                                a:hover {
                                    background: #61dafb;
                                    color: #0f0f0f;
                                }
                                .path {
                                    font-size: 0.85rem;
                                    opacity: 0.7;
                                    margin-top: 1rem;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <h1>404</h1>
                                <p>The page you're looking for could not be found.</p>
                                <a href="/">Return Home</a>
                                <div class="path">Requested: ${req.target}</div>
                            </div>
                        </body>
                        </html>
                        """.trimIndent()
                }
            }
    }
}

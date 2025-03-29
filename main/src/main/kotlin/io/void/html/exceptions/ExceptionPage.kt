package io.void.html.exceptions

data class ExceptionPage(val e: Exception) {

    val page = "<!doctype html><html>" +
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
            "      <span class=\"overlay__title\">${e::class.simpleName}: ${e.localizedMessage}</span>\n" +
            "    </div>\n" +
            "    <div class=\"overlay__content\">\n" +
            "      <div class=\"error-message\">\n" +
            "        <pre>${e::class.simpleName}: ${e.localizedMessage}</pre>\n" +
            "      </div>\n" +
            "      <div class=\"stack-trace\">\n" +
            "        <pre>\n" +
            "          ${e.stackTrace.joinToString("\n")}\n"+
            "        </pre>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</div>" +
            "</body>" +
            "</html>"
}

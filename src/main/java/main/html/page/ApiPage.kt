package main.html.page

abstract class ApiPage(override val target: String, val method: String): Page(target = target) {

    abstract fun serverGetter()
}
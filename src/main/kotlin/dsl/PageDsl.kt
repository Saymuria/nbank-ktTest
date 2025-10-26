package dsl

import ui.pages.BasePage

operator fun <T : BasePage<T>> T.invoke(block: T.() -> Unit): T {
    this.block()
    return this
}
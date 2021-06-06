package com.tuguzteam.netdungeons.ui

open class YesNoDialog(
    title: String,
    private val onYesOption: () -> Unit,
    private val onNoOption: () -> Unit = {}
) : Dialog(title) {

    init {
        addButton()
        size().pad()
    }

    private fun addButton(): YesNoDialog {
        button("Yes", true).button("No", false)
        return this
    }

    override fun result(`object`: Any?) {
        super.result(`object`)
        if (`object` as Boolean) onYesOption()
        else onNoOption()
    }
}

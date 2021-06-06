package com.tuguzteam.netdungeons.ui

class YesNoDialog(
    title: String,
    private val onYesOption: () -> Unit,
    private val onNoOption: () -> Unit = {}
) : Dialog(title) {

    init {
        button("Yes", true).button("No", false)
        size().pad()
    }

    override fun result(`object`: Any?) {
        super.result(`object`)
        if (`object` as Boolean) onYesOption()
        else onNoOption()
    }
}

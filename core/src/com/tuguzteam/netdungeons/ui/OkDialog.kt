package com.tuguzteam.netdungeons.ui

class OkDialog(title: String, private val onOkOption: () -> Unit = {}) : Dialog(title) {
    init {
        button("OK")
        size().pad()
    }

    override fun result(`object`: Any?) {
        super.result(`object`)
        onOkOption()
    }
}

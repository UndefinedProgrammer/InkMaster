package com.undefinedProgrammer.inkmaster

import android.view.View
import android.widget.Button

/**
 * This class replaces the generated data binding class for floating_menu_layout.xml
 * and provides direct access to the views in the layout.
 */
class FloatingMenuViewAccessor(val root: View) {
    val button1: Button = root.findViewById(R.id.button1)
    val button2: Button = root.findViewById(R.id.button2)
    val button3: Button = root.findViewById(R.id.button3)
    val button4: Button = root.findViewById(R.id.button4)

}


fun FloatingMenuViewAccessor?.updateButtons(mode: Int) = this?.run {
    listOf(button1, button2, button3, button4).forEach {
        it.deselect()
    }

    when (mode) {
        1 -> button1.select()
        2 -> button2.select()
        3 -> button3.select()
        4 -> button4.select()
    }
}

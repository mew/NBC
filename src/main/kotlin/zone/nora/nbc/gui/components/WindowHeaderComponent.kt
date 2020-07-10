package zone.nora.nbc.gui.components

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.*
import java.awt.Color

class WindowHeaderComponent(windowTitle: String) : UIBlock(Color(255, 165, 0)) {
    var dragging = false
    var dragOffset = 0f to 0f
    private val titleText: UIText

    init {
        constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = RelativeConstraint(1f) - 2.pixels()
            height = 10.pixels()
        }.onMouseClick { e ->
            dragging = true
            dragOffset = e.absoluteX to e.absoluteY
            e.stopPropagation()
        }.onMouseRelease {
            dragging = false
        }.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (!dragging) return@onMouseDrag
            val absX = mouseX + getLeft()
            val absY = mouseY + getTop()
            val deltaX = absX - dragOffset.first
            val deltaY = absY - dragOffset.second
            dragOffset = absX to absY
            val newX = this.parent.getLeft() + deltaX
            val newY = this.parent.getTop() + deltaY
            this.parent.setX(newX.pixels())
            this.parent.setY(newY.pixels())
        }

        titleText = UIText(windowTitle).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf this
    }

    fun setTitle(title: String) = titleText.setText(title)

}
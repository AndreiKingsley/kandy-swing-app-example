import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.translator.toLetsPlot
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.intern.toSpec
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

fun main() {

    val plots = mapOf(
        "Plot1" to plot {
            bars {
                x(listOf(1, 2, 3))
                y(listOf(4, 5, 6))
            }
        },
        "Plot2" to plot {
            line {
                x(listOf(1, 2, 3))
                y(listOf(4, 5, 6))
            }
        },
    )

    val selectedPlotKey = plots.keys.first()
    val controller = Controller(
        plots,
        selectedPlotKey,
        false
    )

    val window = JFrame("Example App (Swing-Batik)")
    window.defaultCloseOperation = EXIT_ON_CLOSE
    window.contentPane.layout = BoxLayout(window.contentPane, BoxLayout.Y_AXIS)

    // Add controls
    val controlsPanel = Box.createHorizontalBox().apply {
        // Plot selector
        val plotButtonGroup = ButtonGroup()
        for (key in plots.keys) {
            plotButtonGroup.add(
                JRadioButton(key, key == selectedPlotKey).apply {
                    addActionListener {
                        controller.plotKey = this.text
                    }
                }
            )
        }

        this.add(Box.createHorizontalBox().apply {
            border = BorderFactory.createTitledBorder("Plot")
            for (elem in plotButtonGroup.elements) {
                add(elem)
            }
        })

        // Preserve aspect ratio selector
        val aspectRadioButtonGroup = ButtonGroup()
        aspectRadioButtonGroup.add(JRadioButton("Original", false).apply {
            addActionListener {
                controller.preserveAspectRadio = true
            }
        })
        aspectRadioButtonGroup.add(JRadioButton("Fit container", true).apply {
            addActionListener {
                controller.preserveAspectRadio = false
            }
        })

        this.add(Box.createHorizontalBox().apply {
            border = BorderFactory.createTitledBorder("Aspect ratio")
            for (elem in aspectRadioButtonGroup.elements) {
                add(elem)
            }
        })
    }
    window.contentPane.add(controlsPanel)

    // Add plot panel
    val plotContainerPanel = JPanel(GridLayout())
    window.contentPane.add(plotContainerPanel)

    controller.plotContainerPanel = plotContainerPanel
    controller.rebuildPlotComponent()

    SwingUtilities.invokeLater {
        window.pack()
        window.size = Dimension(850, 400)
        window.setLocationRelativeTo(null)
        window.isVisible = true
    }
}

private class Controller(
    private val plots: Map<String, Plot>,
    initialPlotKey: String,
    initialPreserveAspectRadio: Boolean
) {
    var plotContainerPanel: JPanel? = null
    var plotKey: String = initialPlotKey
        set(value) {
            field = value
            rebuildPlotComponent()
        }
    var preserveAspectRadio: Boolean = initialPreserveAspectRadio
        set(value) {
            field = value
            rebuildPlotComponent()
        }

    fun rebuildPlotComponent() {
        plotContainerPanel?.let {
            val container = plotContainerPanel!!
            // cleanup
            for (component in container.components) {
                if (component is Disposable) {
                    component.dispose()
                }
            }
            container.removeAll()

            // build
            container.add(createPlotPanel())
            container.revalidate()
        }
    }

    fun createPlotPanel(): JPanel {
        val rawSpec = plots[plotKey]!!.toLetsPlot().toSpec()
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

        return DefaultPlotPanelBatik(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRadio,
            preferredSizeFromPlot = false,
            repaintDelay = 10,
        ) { messages ->
            for (message in messages) {
                println("[Example App] $message")
            }
        }
    }
}
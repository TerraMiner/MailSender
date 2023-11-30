package ua.terra.thread

import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.util.concurrent.CompletableFuture


fun <T> async(block: () -> T): CompletableFuture<T> = CompletableFuture.supplyAsync(block)

fun appSync(action: () -> Unit) {
    Platform.runLater(action)
}

fun Image.replaceColor(r: Int, g: Int, b: Int): Image {
    val modifiedImage = WritableImage(width.toInt(), height.toInt())
    for (x in 0..<modifiedImage.width.toInt()) {
        for (y in 0..<modifiedImage.height.toInt()) {
            val originalColor = pixelReader.getColor(x, y)
            val modifiedColor = originalColor.run {
                Color.color(r.toDouble() / 255.0,g.toDouble() / 255.0,b.toDouble() / 255.0,originalColor.opacity)
            }
            modifiedImage.pixelWriter.setColor(x, y, modifiedColor)
        }
    }
    return modifiedImage
}

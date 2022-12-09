package model

import scala.util.Random

object MathGenerator {
    private val randGenerator = Random()
    def randomGenerate() : CodeModel = {
        val lines = randGenerator.nextInt(10) + 1
        return null
    }
}

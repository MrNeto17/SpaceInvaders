package game

import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas
import pt.isel.canvas.onFinish
import pt.isel.canvas.onStart
import javax.print.attribute.standard.JobHoldUntil

const val canvasWidth = 700
const val canvasHeight = 500


   fun main(args: Array<String>) { //realiza todos os passos do jogo
       onStart {
           val cv = Canvas(canvasWidth, canvasHeight, BLACK)
           var game = buildInitialGame()
           game = game.menu(true)
           cv.drawMenu(game)

           cv.onTimeProgress(1) {
               cv.drawGame(game)
               game = if (game.directtoplist(game.scoreslist)) game.menu(false)
               else game.step()
               cv.onKeyPressed { k ->
                   when (k.char) {
                       'z' -> game = game.menu(false)
                       'x' -> game = game.start()
                       'c' -> game.resetscorelist(false)
                       else -> game.copy()
                   }
               }
           }

           cv.onTimeProgress(666) {
               game = game.aliensMove()
           }

           cv.onTimeProgress(250) {
               game = game.addShot()
           }

           cv.onMouseMove {
               game = game.moveSpaceship(it.x)
           }

           cv.onKeyPressed { k ->
               game = game.keyShipShot(k)
           }

           cv.onMouseDown {
               game = game.mouseShipShot()
           }
       }
       onFinish { }
   }


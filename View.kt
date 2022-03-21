package game

import pt.isel.canvas.*

const val shotWidth = 4
const val shotHeight = 7
const val imageWidth = 112
const val imageHeight = 80
const val shipHalf = 25
const val shipY = 50

/* desenhar aliens x -> quando animationStep = true o sprite do alien muda
 y -> desenha os aliens pela ordem da enum class */
fun Canvas.drawAlien(alien :Alien) {
    val x = if (alien.animationStep) imageWidth else 0
    val y = alien.alienType.ordinal * imageHeight
    drawImage("invaders|$x,$y,112,80", alien.pos.x, alien.pos.y, aliensWidth, aliensHeight)
}

fun Canvas.drawSpaceship(ship :Spaceship) { //desenho da nave e do tiro da nave
    drawImage("spaceship", ship.pos.x - shipHalf, ship.pos.y - shipY, ship.width, ship.height)
    if (ship.shot != null) drawRect(ship.shot.pos.x, ship.shot.pos.y - shipY, shotWidth, shotHeight, WHITE) //desenho do tiro dos aliens
}

fun Canvas.drawShot(shot :Shot) = drawRect(shot.pos.x, shot.pos.y, shot.width, shot.height, RED) //desenha os tiros dos aliens

fun Canvas.drawScreens(Over: Boolean, Restart: Boolean, game: Game) { // desenha os ecras de vitoria e derrota
    if (Over) drawText(238, (canvasHeight/1.05).toInt(), "Game Over", RED, 30)
    if (Restart) drawText(132, canvasHeight/2, "You Win", RED, 100)
    drawText(50, (canvasHeight/1.65).toInt(), "Press 'x' to start", WHITE, 27)
    drawText(305, (canvasHeight/1.45).toInt(), "Press 'z' to menu", WHITE, 27)
}

fun Canvas.drawScores(game: Game) { //desenha os scores
    drawText(10, canvasHeight - 10, "${game.score}", WHITE, 35)
    drawText(440, canvasHeight - 10, "HIGHSCORE: ${takescore("highscore.txt")}", WHITE, 27)
}

fun Canvas.drawMenu(game: Game) { //desenha o menu
    val inicialx = 210
    val finalx = 455
    val inicialy = (canvasHeight/1.40).toInt()
    val finaly = (canvasHeight/3.05).toInt()
    val linecolour = GREEN
    drawText(205, (canvasHeight / 1.25).toInt(), "Press 'x' to start", WHITE, 30)
    drawText(215, (canvasHeight / 1.43).toInt(), "Press 'c' to restart all scores", GREEN, 16)
    drawText(220, (canvasHeight / 5.50).toInt(), "MENU", RED, 75)
    drawText(210, (canvasHeight / 3.35).toInt(), "TopScores:", GREEN, 23)
    drawLine(inicialx, inicialy, finalx, inicialy, linecolour)
    drawLine(inicialx, finaly, finalx, finaly, linecolour)
    drawLine(inicialx, inicialy, inicialx, finaly, linecolour)
    drawLine(finalx, inicialy, finalx, finaly, linecolour)
    for (i in 4 downTo 1) {
        val l = when { (i == 4) -> 1 ; (i == 3) -> 2 ;(i == 2) -> 3
            else -> 4 }
        val y = ((canvasHeight / 3.00) * (1 - 0.23* (i + 1.1)) + 230).toInt()
        drawText(220, y, "${l}º ${game.scoreslist[i-1]} points", GREEN, 18) }
}

fun Canvas.drawGame(game :Game) { //desenha o painel jogo
    erase()
    game.aliens.list.forEach { drawAlien(it) }
    drawSpaceship(game.ship)
    game.alienShots.forEach { drawShot(it) }
    if (game.aliens.list.isEmpty() && (game.score > 0)) drawScreens(false, true, game)
    if (game.over && game.aliens.list.isNotEmpty() && !(game.directtoplist(game.scoreslist))) drawScreens(true, false, game)
    drawScores(game)
    if ((game.score == 0) && (game.aliens.list.isEmpty())) drawMenu(game)
}

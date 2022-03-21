package game

import pt.isel.canvas.KeyEvent
import javax.swing.text.StyledEditorKit

data class Area(val width :Int, val height :Int)
data class Game(val area :Area,
                val aliens :Aliens,
                val alienShots :List<Shot>,
                val ship :Spaceship,
                val over :Boolean,
                val score :Int,
                val highscore: Int,
                val scoreslist: MutableList<Int>)
val deltaAlienShots = 1..4
const val deltaShipShots = 4
val PlayersInTop = 1..4
const val shotscore = 1


fun buildInitialGame() :Game { //estrutura inicial do jogo
    val area = Area(canvasWidth, canvasHeight)
    val shots = emptyList<Shot>()
    val aliens = aliens()
    val spaceship = Spaceship(Position(area.width/2 - shipHalf, area.height - shipY), null)
    val highscore = takescore("highscore.txt")
    val scoreslist = mutableListOf<Int>()
    for (i in PlayersInTop) scoreslist.add(takescore("P$i.txt"))
    return Game(area, aliens ,shots, spaceship, false, score = 0, highscore, scoreslist)
}

// step() -> Decide o comportamento do jogo ao longo do tempo conforme as colisões e ações do jogador
fun Game.step() :Game {
    if (over || aliens.list.isEmpty()) return this
    val alienShotScore = alienShots.mapNotNull { it.shotsCollision(false, true, ship.shot) }
    val alienShotDestroyed = alienShots.mapNotNull { it.shotsCollision(true, false, ship.shot) }
    val aliensScore = aliens.list.mapNotNull { it.aliensCollision(false, true, ship.shot)}
    val newAliensList = aliens.list.mapNotNull { it.aliensCollision(true, false, ship.shot)}
    val alienShotsMoved = if(alienShotScore.isNotEmpty()) alienShotDestroyed else alienShots.mapNotNull { it.moveAlienShot() }
    val shipShotMoved = if(aliensScore.isNotEmpty()) Spaceship(ship.pos, null) else Spaceship(ship.pos, ship.shot?.moveShipShot())
    val newScore = when {
        aliensScore.isNotEmpty() -> score + aliensScore[0].alienType.points
     alienShotScore.isNotEmpty() -> score + shotscore
        else -> score }
    val gameOver = alienShots.any { ship.shipCollision(it) }
    val aliens = Aliens(newAliensList, aliens.moveRight)
    if (score >= highscore) storescore("highscore.txt", score) else storescore("highscore.txt", highscore)
    val realscore = takescore("highscore.txt")
    directtoplist(scoreslist)
    return Game(area, aliens, alienShotsMoved, shipShotMoved, gameOver, newScore, realscore, scoreslist)
}

fun Game.menu(FirstTime: Boolean) : Game { //volta para o menu do jogo
    if (!over &&  FirstTime || over || aliens.list.isEmpty()) {
       val scoreslist = scorelistcheck(scoreslist).scoreslist
        return Game(area, Aliens(emptyList(), false), emptyList(),
            Spaceship(Position(0, 0), null), over, 0, highscore, scoreslist)
    }
        return step()
}

fun Game.resetscorelist(FirstTime: Boolean): Game {
    return if (!over && FirstTime || over  || aliens.list.isEmpty()) {
        for (i in PlayersInTop) {
            scoreslist[i - 1] = 0
            storescore("P$i.txt", 0)
        }
        storescore("highscore.txt", 0)

        Game(area, aliens, alienShots, ship, over, score = 0, highscore = 0, scoreslist)
    } else step()
}

fun Game.start() : Game { //começa o jogo
    val game = buildInitialGame()
    val scoreslist = scorelistcheck(scoreslist).scoreslist
    return when{
        (over || aliens.list.isEmpty()) -> Game(
            area, game.aliens, game.alienShots, game.ship, game.over, score = 0, takescore("highscore.txt"), scoreslist)
        else -> step()
    }
}

fun Game.addShot() :Game {      //adiciona um novo tiro dos aliens com 50% de chance de aparecer no canvas
    if (over || aliens.list.isEmpty()) return this
    val newAlienShots = alienShots + Shot(Position(aliens.list.random().pos.x + aliensWidth/2,
                                                   aliens.list.random().pos.y + aliensHeight),
                                          deltaAlienShots.random())
    val alienShotChance = (1..2).random()
    val chanceResult = if (alienShotChance == 1) newAlienShots else alienShots
    return Game(area, aliens, chanceResult, ship, over, score, highscore, scoreslist)
}
fun Game.moveSpaceship(newX :Int) :Game {       //move a nave sem passar os limites do canvas
    if (over || aliens.list.isEmpty()) return this
    val newSpaceship = ship.move(newX).spaceshipLimit()
    return Game(area, aliens, alienShots, newSpaceship, over, score, highscore, scoreslist)
}

fun Game.keyShipShot(k :KeyEvent) :Game {       //se o espaço for clicado e não existir nenhum tiro da nave na
    if (over || aliens.list.isEmpty()) return this                       // tela a nave dispara um tiro novo
    val spaceshipShot = if (k.char == ' ' && ship.shot == null)
        Spaceship(ship.pos, Shot(Position(ship.pos.x, ship.pos.y),
                                 deltaShipShots))
    else ship
    return Game(area, aliens, alienShots, spaceshipShot , over, score, highscore, scoreslist)
}

fun Game.mouseShipShot() :Game {        // o mesmo que o keyShipShot mas com o clique do rato em vês do espaço
    if (over || aliens.list.isEmpty()) return this
    val spaceshipShot = if (ship.shot == null)
        Spaceship(ship.pos, Shot(Position(ship.pos.x, ship.pos.y),
                                 deltaShipShots))
    else ship
    return Game(area, aliens, alienShots, spaceshipShot , over, score, highscore, scoreslist)
}

fun Game.aliensMove() :Game {
    if (over || aliens.list.isEmpty()) return this
    val aliensMoved = aliens.aliensMove()
    return Game(area, aliensMoved, alienShots, ship, over, score, highscore, scoreslist)
}

fun Game.scorelistcheck(lista: MutableList<Int>): Game{
    val newlist = lista.sorted().toMutableList()
    if (lista.isEmpty()) for (i in PlayersInTop) lista.add(0)
        return if ((over || aliens.list.isEmpty()) && score > lista[0]) {
            newlist[0] = score
            val finallist = newlist.sorted().toMutableList()
            for (i in PlayersInTop) storescore("P${i}.txt",finallist[i-1])
     Game(area, aliens,  alienShots, ship, over, score, highscore, finallist)
        }
    else copy()
}
fun Game.directtoplist(lista: MutableList<Int>): Boolean {
    return (lista != scorelistcheck(lista).scoreslist)
}




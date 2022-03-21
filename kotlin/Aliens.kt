package game

data class Aliens(val list: List<Alien>, val moveRight: Boolean)
data class Alien(val alienType : AlienType, val pos: Position, val animationStep: Boolean)
enum class AlienType(val points :Int) {Squid(30), Crab(20), Octopus(10)}

const val aliensWidth = 56
const val aliensHeight = 40
const val aliensMoveX = 4
const val aliensMoveY = 20
const val spaceBetweenAliens = 12
const val heightDiff = 40

fun aliens() :Aliens { // cria uma lista de aliens ordenada por coluna
    val aliensList = mutableListOf<Alien>()
    val alienTypes = listOf(AlienType.Squid, AlienType.Squid, AlienType.Crab, AlienType.Crab, AlienType.Octopus)
    for (a in 0..10)
        for (i in 0..4)
            aliensList.add(Alien(alienTypes[i], Position(a * aliensWidth, i * aliensHeight), false))
    return Aliens(aliensList, true)
}

/* aliensMove -> os aliens  movem-se para a direita e quando algum  toca nas borda todos
descem 20 pixeis e movem-se no sentido contrário */

fun Aliens.aliensMove() :Aliens {
    val reverse = moveRight && list.last().pos.x + aliensWidth + aliensMoveX > canvasWidth ||
                  !moveRight && list.first().pos.x - aliensMoveX < 0
    val newX = if (reverse) 0 else if (moveRight) aliensMoveX else -aliensMoveX
    val newY = if (reverse) aliensMoveY else 0
    val finalList = list.map {it.copy(it.alienType,
                                      Position(it.pos.x + newX, it.pos.y + newY),
                                      !it.animationStep)}
    val direction = if (reverse) !moveRight else moveRight
    return Aliens(finalList, direction)
}
/* Alien.aliensCollision -> Retorna o Alien ou null dependendo se há colisao e se queremos contar o score
ou que o alien desapareça */
fun Alien.aliensCollision(alienDisappear: Boolean, scoreCount: Boolean, shot: Shot?) : Alien? {
    val collisionX = when  {
        alienType == AlienType.Squid -> pos.x + spaceBetweenAliens / 2..pos.x + aliensWidth - spaceBetweenAliens / 2
        alienType == AlienType.Crab -> pos.x + spaceBetweenAliens / 4..pos.x + aliensWidth - spaceBetweenAliens / 4
        else -> pos.x + shotWidth ..pos.x + aliensWidth + shotWidth
    }
    val collisionY = pos.y + heightDiff ..pos.y + aliensHeight + heightDiff
    val collisionCondition = (shot?.pos?.x in collisionX && shot?.pos?.y in collisionY)
    val defaultAlien = Alien(alienType, pos, animationStep)
    return when {
        alienDisappear && !collisionCondition -> defaultAlien
        scoreCount && collisionCondition -> defaultAlien
        else -> null
    }
}
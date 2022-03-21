package game
data class Spaceship(val pos :Position, val shot :Shot?, val width :Int = 50, val height :Int = 30)

fun Spaceship.move(newX :Int) = Spaceship(Position(newX, pos.y), shot) //move a nave

fun Spaceship.spaceshipLimit() :Spaceship{            // função para a nave não passar os limites
    val rightLimit = 700 - width/2                    // do canvas
    val leftLimit = width/2
    val spaceshipLimit = when {
        pos.x > rightLimit -> Spaceship(Position(rightLimit, pos.y), shot)
        pos.x < leftLimit -> Spaceship(Position(leftLimit, pos.y), shot)
        else -> Spaceship(Position(pos.x, pos.y), shot)
    }
    return spaceshipLimit
}

fun Spaceship.shipCollision(shot :Shot) :Boolean {        //colisão dos tiros dos aliens com a nave
    val collisionX = pos.x - shipHalf .. pos.x + shipHalf
    val collisionY = pos.y - shipY .. pos.y - shipY + height
    return shot.pos.x in collisionX && shot.pos.y in collisionY
}
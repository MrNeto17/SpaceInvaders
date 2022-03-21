package game

data class Position(val x :Int, val y :Int)
data class Shot(val pos :Position, val delta :Int, val width :Int = 4, val height :Int = 7)

fun Shot.moveAlienShot(): Shot? {       //movimento dos tiros dos aliens
    val newPosition = pos.y + delta
    return if (newPosition <= canvasHeight)
        Shot(Position(pos.x, newPosition), delta)
    else null
}

fun Shot.moveShipShot(): Shot? {        //movimento dos tiros da nave
    val newPosition = pos.y - delta
    return if (newPosition >= 0)
        Shot(Position(pos.x, newPosition), delta)
    else null
}

fun Shot.shotsCollision(shotDisappear: Boolean, scoreCount: Boolean, shot: Shot?) : Shot? { // Caso haja colisao entre shots o alien shot fica null
    val collisionX = pos.x ..pos.x + shotWidth
    val collisionY = pos.y + heightDiff..pos.y + shotHeight + heightDiff
    val collisionCondition = shot?.pos?.x in collisionX && shot?.pos?.y in collisionY
    val defaultShot= Shot(pos, delta)
    return when {
        shotDisappear && !collisionCondition -> defaultShot
        scoreCount && collisionCondition -> defaultShot
        else -> null
    }
}
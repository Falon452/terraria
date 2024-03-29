package com.falon.terraria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Timer

class Ground(
    private val world: World,
    private val batch: SpriteBatch,
    private val textures: List<String>,
    val squareSize: Float,
    val leftCornerX: Float,
    val leftCornerY: Float,
    private val density: Float = 1F,
    private val baseTimeToNextTexture: Float = 0.3F,
) {

    private var square: PolygonShape? = null
    private var img: Texture? = null
    private var body: Body? = null
    private val timerTasks: MutableList<Timer.Task> = mutableListOf()
    var currentTexturePointer = -1


    fun create() {
        currentTexturePointer++
        img = Texture(textures[currentTexturePointer])

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(leftCornerX, leftCornerY)

        body = world.createBody(bodyDef)

        square = PolygonShape()
        square?.setAsBox(squareSize, squareSize)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = square
        fixtureDef.density = density
        fixtureDef.friction = 0.4f
        fixtureDef.restitution = 0.6f

        body?.createFixture(fixtureDef)
    }

    fun render() {
        batch.begin()
        if (img != null) {
            batch.draw(img, leftCornerX, leftCornerY, squareSize, squareSize)
        }
        batch.end()
    }

    fun dispose() {
        if (body != null) world.destroyBody(body)
        square?.dispose()
        square = null
        img?.dispose()
        img = null
    }

    fun onTouchDown() {
        textures.subList(currentTexturePointer + 1, textures.size).forEachIndexed { index, _ ->
            timerTasks.add(nextTextureTask(baseTimeToNextTexture * (index + 1)))
        }
        timerTasks.add(destroyTask(baseTimeToNextTexture * textures.size - currentTexturePointer))
    }

    private fun nextTextureTask(delay: Float): Timer.Task = Timer.schedule(
        object : Timer.Task() {
            override fun run() {
                currentTexturePointer++
                img = Texture(textures[currentTexturePointer])
            }
        },
        delay
    )

    private fun destroyTask(delay: Float): Timer.Task = Timer.schedule(
        object : Timer.Task() {
            override fun run() {
                dispose()
            }
        },
        delay
    )


    fun onTouchUp() {
        cancelTextureTransition()
    }

    private fun cancelTextureTransition() {
        timerTasks.forEach { it.cancel() }
    }

    fun onTouchCancelled() {
        cancelTextureTransition()
    }
}
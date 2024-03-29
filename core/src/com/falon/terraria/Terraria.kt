package com.falon.terraria

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ScreenUtils

internal class Terraria : ApplicationAdapter(), InputProcessor {

    private lateinit var batch: SpriteBatch
    private lateinit var world: World
    private lateinit var grounds: List<Ground>
    private lateinit var person: Person
    private lateinit var debugRenderer: Box2DDebugRenderer
    private var isTouchingLeft = false
    private var isTouchingRight = false


    override fun create() {
        batch = SpriteBatch()
        world = World(Vector2(0f, -9.8f), true)
        grounds = List(NUMBER_OF_COLUMNS) { i ->
            List(NUMBER_OF_ROWS) { j ->
                Ground(
                    world,
                    batch,
                    listOf("ground0.png", "ground1.png", "ground2.png", "ground3.png"),
                    100f,
                    i * 100f,
                    j * 100f
                )
            }
        }.flatten()
        grounds.forEach { it.create() }
        person = Person(world, batch, "person.png", 600F,600F)
        person.create()
        debugRenderer = Box2DDebugRenderer()
        Gdx.input.inputProcessor = this
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        val deltaTime = Gdx.graphics.deltaTime
        world.step(deltaTime * 10   , 6, 2)
        person.render()
        grounds.forEach { it.render() }
        debugRenderer.render(world, batch.projectionMatrix)

        if (isTouchingLeft) {
            person.body.applyForceToCenter(-FORCE_MOVING_BODY, 0f, true)
        } else if (isTouchingRight) {
            person.body.applyForceToCenter(FORCE_MOVING_BODY, 0f, true)
        }
    }

    override fun dispose() {
        grounds.forEach { it.dispose() }
        batch.dispose()
        debugRenderer.dispose()
    }

    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (screenX <= MARGIN_FOR_MOVING_BODY) {
            isTouchingLeft = true
        } else if (screenX >= Gdx.graphics.width - MARGIN_FOR_MOVING_BODY) {
            isTouchingRight = true
        }
        val worldY = Gdx.graphics.height - screenY

        grounds.forEach { ground ->
            if (
                screenX >= ground.leftCornerX &&
                screenX <= ground.leftCornerX + ground.squareSize &&
                worldY >= ground.leftCornerY &&
                worldY <= ground.leftCornerY + ground.squareSize
            ) {
                val distance = Vector2.dst(screenX.toFloat(), worldY.toFloat(), person.body.position.x, person.body.position.y)
                if (distance <= RADIUS_FOR_MINING) {
                    ground.onTouchDown()
                }
            }
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        person.body.linearDamping = 10f
        isTouchingLeft = false
        isTouchingRight = false
        val worldY = Gdx.graphics.height - screenY

        // Check if the touch coordinates are within the bounds of the ground
        grounds.forEach { ground ->
            if (screenX >= ground.leftCornerX && screenX <= ground.leftCornerX + ground.squareSize &&
                worldY >= ground.leftCornerY && worldY <= ground.leftCornerY + ground.squareSize
            ) {
                ground.onTouchUp()
            }
        }

        // Return true to indicate that the touch event has been handled
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // Convert the y-coordinate from screen coordinates to world coordinates
        val worldY = Gdx.graphics.height - screenY

        // Check if the touch coordinates are within the bounds of the ground
        grounds.forEach { ground ->
            if (screenX >= ground.leftCornerX && screenX <= ground.leftCornerX + ground.squareSize &&
                worldY >= ground.leftCornerY && worldY <= ground.leftCornerY + ground.squareSize
            ) {
                ground.onTouchCancelled()
            }
        }

        // Return true to indicate that the touch event has been handled
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

    companion object {

        private val NUMBER_OF_COLUMNS = 20
        private val NUMBER_OF_ROWS = 4

        private val FORCE_MOVING_BODY = 10000000F
        private val MARGIN_FOR_MOVING_BODY = 100

        private val RADIUS_FOR_MINING = 200
    }
}
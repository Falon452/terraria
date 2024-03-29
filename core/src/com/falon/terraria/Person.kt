package com.falon.terraria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World

class Person(
    private val world: World,
    private val batch: SpriteBatch,
    private val texturePath: String,
    private val startX: Float,
    private val startY: Float
) {
    lateinit var body: Body
    private lateinit var img: Texture
    private val size = 100F

    fun create() {
        img = Texture(texturePath)
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(startX, startY)

        body = world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(size / 2, size / 2)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 1f
        fixtureDef.friction = 0.4f
        fixtureDef.restitution = 0.6f

        body.createFixture(fixtureDef)
        shape.dispose()
    }
    fun render() {
        batch.begin()
        batch.draw(img, body.position.x - size /2 , body.position.y - size / 2)
        batch.end()
    }
}
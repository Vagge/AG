package com.example.aghello.model

import androidx.annotation.IntDef
import kotlin.random.Random

class Tank(private var posX: Int, private var posY: Int){
    companion object{
        @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
        @IntDef(DAMAGE_BOOST, HEALTH_BOOST, AMMO_BOOST)
        @Retention(AnnotationRetention.SOURCE)
        annotation class PowerUp
        const val NONE = 0
        const val DAMAGE_BOOST = 1
        const val HEALTH_BOOST = 2
        const val AMMO_BOOST = 3

        fun generateRandomPowerUp(): @PowerUp Int{
            return Random.nextInt(3) + 1
        }
    }
    var timeAtProjFired: Long = 0
    var timeAtPwrUpPick: Long = 0
    var hp = 100
    private val MAX_SHELLS = 15
    val shells = MutableList(MAX_SHELLS) {Shell()}
    private var isPoweredUp = false
    @PowerUp
    private var powerUp = NONE

    fun moveBy(dx: Int, dy: Int){
        posX += dx
        posY += dy
    }

    //check if there is a powerup active in the game class, if no then powerup can be picked
    fun powerUpPicked(@PowerUp powerUp: Int){
        timeAtPwrUpPick = System.currentTimeMillis()
        isPoweredUp = true
        this.powerUp = powerUp
        when(powerUp){
            DAMAGE_BOOST -> shells.forEach{ i -> i.damage*=2} //gives 2x damage
            HEALTH_BOOST -> hp+=50 //gives 50 health
            AMMO_BOOST -> shells.addAll(List(MAX_SHELLS/3) { Shell() }) //gives 5 shells (15/3)
        }
    }

    fun fireProjectile(): Boolean{
        return if(shells.isNotEmpty() && System.currentTimeMillis() - timeAtProjFired > 2){
            timeAtProjFired = System.currentTimeMillis()
            return true
        }
        else false
    }

    fun removeProjectile(index: Int): Boolean{
        return if(shells.size > index){
            shells.removeAt(index)
            true
        }
        else false
    }

    /**
     * This function is for disabling damage boost if 10 seconds have passed.
     * As well as enabling the ability to pick new powerups for this tank
     * It should be called while the game is active
     */
    fun checkBoostDuration(){
        if(System.currentTimeMillis() - timeAtPwrUpPick >= 10){
            if(powerUp == DAMAGE_BOOST) {
                shells.forEach { i -> i.damage = 100 }
            }
            isPoweredUp = false
        }
    }
}
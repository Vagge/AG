package com.example.aghello.model

class TankGameRules { //how should the game loop look like?
    private lateinit var player1 : Tank
    private lateinit var player2 : Tank
    private val powerups = ArrayList<@Tank.Companion.PowerUp Int>()

    fun initNewGame(){ //tanks cant take damage yet (they shouldn't take damage unless they are shot at)
        player1 = Tank(0,0)
        player2 = Tank(0,0)
        for(i in 0..6){
            powerups.add(Tank.generateRandomPowerUp())
        }
    }

    //startRound (starts round, tanks can now take damage)  //need to understand how game loop is gonna work before I can implement this
    fun startRound(){}

    //getPowerups (returns a list of all the powerups that are currently on the map)
    fun getPowerUps(): ArrayList<@Tank.Companion.PowerUp Int>{ //could be omitted
        return powerups
    }

    //removeProjectile(whichTank, whichProjectile)
    fun removeProjectile(player : Tank, index: Int): Boolean{
        return if(player == player1 || player == player2){
            player.removeProjectile(index)
        }
        else false
    }

    //fireProjectile(whichTank)
    fun fireProjectile(player : Tank){
        if(player == player1 || player == player2) player.fireProjectile()
    }

    //consumePowerup(whichtank, poweruptype)
    fun consumePowerup(player : Tank, index: Int){ //changed poweruptype to an index from the powerups arraylist so we know which powerup was picked
        if(player == player1 || player == player2){
            player.powerUpPicked(powerups[index])
            powerups.removeAt(index)
        }
    }

    //projectileHit(whichtank, whichProjectile), //return true if game is won(tank hit is below 0 hp)
    fun projectileHit(player: Tank, index: Int): Boolean {
        if (player == player1) {
            if (player2.hp <= 0) return true
        }
        if (player == player2) {
            if (player1.hp <= 0) return true
        }
        return false
    }

    //getCurrentPowerups (same as getPowerUps?)
}




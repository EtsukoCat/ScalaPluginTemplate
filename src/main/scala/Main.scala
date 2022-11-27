package org.example

import arc.Events
import arc.util.{CommandHandler, Log}
import mindustry.Vars
import mindustry.content.{Blocks, Items}
import mindustry.game.EventType
import mindustry.game.EventType.BuildSelectEvent
import mindustry.gen.{Call, Groups, Player}
import mindustry.mod.Plugin
import mindustry.net.Administration
import mindustry.net.Administration.ActionType
import mindustry.world.blocks.storage.CoreBlock

class Main extends Plugin :
  // Called when game initializes
  override def init(): Unit = {
    Log.info("Hello from @!", this.getClass.getName)

    Events.on(classOf[EventType.BuildSelectEvent], (event: EventType.BuildSelectEvent) => {
      if (!event.breaking && event.builder != null && event.builder.buildPlan != null && (event.builder.buildPlan.block eq Blocks.thoriumReactor) && event.builder.isPlayer) { //player is the unit controller
        val player = event.builder.getPlayer
        // Send a message to everyone saying that this player has begun building a reactor
        Call.sendMessage("[scarlet]ALERT![] " + player.name + " has begun building a reactor at " + event.tile.x + ", " + event.tile.y)
      }
    })

    // Add a chat filter that changes the contents of all messages
    // In this case, all instances of "heck" are censored
    Vars.netServer.admins.addChatFilter((player: Player, text: String) => text.replace("heck", "h*ck"))

    // Add an action filter for preventing players from doing certain things
    Vars.netServer.admins.addActionFilter((action: Administration.PlayerAction) => {
      // Random example: prevent blast compound depositing
      if ((action.`type` eq ActionType.depositItem) && (action.item eq Items.blastCompound) && action.tile.block.isInstanceOf[CoreBlock]) {
        action.player.sendMessage("Example action filter: Prevents players from depositing blast compound into the core.")
      }
      true
    })
  }

  // Register commands that run on the server//register commands that run on the server
  override def registerServerCommands(handler: CommandHandler): Unit = {
    handler.register("reactors", "List all thorium reactors in the map.", (args: Array[String]) => {
      for (x <- 0 until Vars.world.width) {
        for (y <- 0 until Vars.world.height) {
          // Loop through and log all found reactors
          // Make sure to only log reactor centers
          if ((Vars.world.tile(x, y).block eq Blocks.thoriumReactor) && Vars.world.tile(x, y).isCenter) {
            Log.info("Reactor at @, @", x, y)
          }
        }
      }
    })
  }

  override def registerClientCommands(handler: CommandHandler): Unit = {
    // Register a simple reply command
    handler.register[Player]("reply", "<text...>", "A simple ping command that echoes a player's text.", (args, player) => {
      player.sendMessage("You said: [accent] " + args(0))
    })

    // Register a whisper command which can be used to send other players messages
    handler.register[Player]("whisper", "<player> <text...>", "Whisper text to another player.", (args, player) => {
      val other = Groups.player.find((p: Player) => p.name.equalsIgnoreCase(args(0)))

      // Give error message with scarlet-colored text if player isn't found
      if (other == null) {
        player.sendMessage("[scarlet]No player by that name found!")
      } else {
        // Send the other player a message, using [lightgray] for gray text color and [] to reset color
        other.sendMessage("[lightgray](whisper) " + player.name + ":[] " + args(1))
      }
    })
  }
end Main
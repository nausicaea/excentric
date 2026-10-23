package net.nausicaea.excentric.fabric.api.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.nausicaea.excentric.fabric.api.network.VillagerDebugBroadcaster

/**
 * Registers the mod's commands. Everything is restricted to operators.
 *
 * - `/excentric debug needs`: toggle the villager debug overlay for the executing player.
 * - `/excentric debug needs <true|false>`: set the villager debug overlay for the executing player.
 */
object ExcentricCommands {
    private const val OPERATOR_LEVEL = 2

    @JvmStatic
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> dispatcher.register(root()) }
    }

    private fun root(): LiteralArgumentBuilder<CommandSourceStack> =
        Commands
            .literal("excentric")
            .requires { it.hasPermission(OPERATOR_LEVEL) }
            .then(
                Commands
                    .literal("debug")
                    .then(
                        Commands
                            .literal("needs")
                            .executes { setNeedsOverlay(it, null) }
                            .then(
                                Commands
                                    .argument("enabled", BoolArgumentType.bool())
                                    .executes { setNeedsOverlay(it, BoolArgumentType.getBool(it, "enabled")) },
                            ),
                    ),
            )

    private fun setNeedsOverlay(
        ctx: CommandContext<CommandSourceStack>,
        enabled: Boolean?,
    ): Int {
        val source = ctx.source
        val player = source.playerOrException
        val target = enabled ?: !VillagerDebugBroadcaster.isEnabled(player)
        if (!VillagerDebugBroadcaster.setEnabled(player, target)) {
            source.sendFailure(Component.literal("Your client does not have excentric installed, nothing can be displayed"))
            return 0
        }
        source.sendSuccess({ Component.literal("Villager needs overlay ${if (target) "enabled" else "disabled"}") }, false)
        return 1
    }
}

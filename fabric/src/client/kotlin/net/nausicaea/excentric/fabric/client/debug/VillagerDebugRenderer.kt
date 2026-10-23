package net.nausicaea.excentric.fabric.client.debug

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.util.Mth
import net.nausicaea.excentric.ai.debug.VillagerDebug
import java.util.Locale

/**
 * Draws a hovering text billboard above each villager for which the server sent a [VillagerDebug] snapshot.
 *
 * The text is drawn see-through, so that it remains readable through walls.
 */
object VillagerDebugRenderer {
    private const val MAX_DISTANCE = 24.0
    private const val SCALE = 0.025f
    private const val LINE_HEIGHT = 10
    private const val HOVER_OFFSET = 0.5
    private const val BACKGROUND_OPACITY = 0.25f
    private const val COLOR_DEFAULT = 0xFFFFFF
    private const val COLOR_CRITICAL = 0xFF5555
    private const val COLOR_SERVICE = 0xFFFF55
    private const val COLOR_MUTED = 0xAAAAAA
    private const val CRITICAL_SATISFACTION = 0.2

    private data class Line(
        val text: String,
        val color: Int,
    )

    fun render(context: WorldRenderContext) {
        val entries = VillagerDebugStore.current()
        if (entries.isEmpty()) {
            return
        }
        val level = context.world() ?: return
        val poseStack = context.matrixStack() ?: return
        val consumers = context.consumers() ?: return
        val camera = context.camera()
        val cameraPos = camera.position
        val partialTick = context.tickCounter().getGameTimeDeltaPartialTick(false).toDouble()
        val mc = Minecraft.getInstance()
        val font = mc.font
        val background = mc.options.getBackgroundColor(BACKGROUND_OPACITY)

        for (entry in entries) {
            val entity = level.getEntity(entry.entityId) ?: continue
            if (entity.distanceToSqr(cameraPos) > MAX_DISTANCE * MAX_DISTANCE) {
                continue
            }
            val x = Mth.lerp(partialTick, entity.xOld, entity.x) - cameraPos.x
            val y = Mth.lerp(partialTick, entity.yOld, entity.y) - cameraPos.y + entity.bbHeight + HOVER_OFFSET
            val z = Mth.lerp(partialTick, entity.zOld, entity.z) - cameraPos.z

            val lines = lines(entry)
            val top = -lines.size * LINE_HEIGHT

            poseStack.pushPose()
            poseStack.translate(x, y, z)
            poseStack.mulPose(camera.rotation())
            poseStack.scale(SCALE, -SCALE, SCALE)
            val pose = poseStack.last().pose()
            for ((i, line) in lines.withIndex()) {
                font.drawInBatch(
                    line.text,
                    -font.width(line.text) / 2f,
                    (top + i * LINE_HEIGHT).toFloat(),
                    line.color,
                    false,
                    pose,
                    consumers,
                    Font.DisplayMode.SEE_THROUGH,
                    background,
                    LightTexture.FULL_BRIGHT,
                )
            }
            poseStack.popPose()
        }
        // Make sure the text is not drawn behind later translucent geometry.
        (consumers as? MultiBufferSource.BufferSource)?.endBatch()
    }

    private fun lines(entry: VillagerDebug): List<Line> {
        val lines = mutableListOf<Line>()
        val svc = entry.service
        if (svc == null) {
            lines += Line("service: none", COLOR_MUTED)
        } else {
            lines += Line("service: ${svc.type}", COLOR_SERVICE)
            lines += Line(svc.description, COLOR_SERVICE)
            lines += Line("need: ${svc.need.path}", COLOR_SERVICE)
            lines += Line("base ${fmt(svc.baseUtility)} | score ${fmt(svc.score)} | weight ${fmt(svc.weight)}", COLOR_SERVICE)
        }
        for (need in entry.needs) {
            val color = if (need.satisfaction < CRITICAL_SATISFACTION) COLOR_CRITICAL else COLOR_DEFAULT
            lines += Line("${need.key.path}: ${fmt(need.satisfaction)}", color)
        }
        return lines
    }

    private fun fmt(value: Double): String = String.format(Locale.ROOT, "%.2f", value)
}

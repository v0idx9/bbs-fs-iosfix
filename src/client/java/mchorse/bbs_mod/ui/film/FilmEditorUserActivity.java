package mchorse.bbs_mod.ui.film;

import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.utils.keys.KeyAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Tracks whether the user is actively editing (not AFK) for the film's {@code time_spent_active} counter.
 * AFK = no keyboard/mouse input for {@link #AFK_IDLE_MS} while the game window is focused.
 */
public final class FilmEditorUserActivity
{
    private static final long AFK_IDLE_MS = 120_000L;

    private int lastMouseX = Integer.MIN_VALUE;
    private int lastMouseY = Integer.MIN_VALUE;
    private long lastActivityMs;

    public void reset()
    {
        this.lastMouseX = Integer.MIN_VALUE;
        this.lastMouseY = Integer.MIN_VALUE;
        this.lastActivityMs = 0L;
    }

    public void onFilmOpened()
    {
        this.lastMouseX = Integer.MIN_VALUE;
        this.lastMouseY = Integer.MIN_VALUE;
        this.lastActivityMs = System.currentTimeMillis();
    }

    public boolean shouldAccumulateActiveTime(MinecraftClient mc, UIContext context, long nowMs)
    {
        if (!mc.isWindowFocused() || mc.isPaused())
        {
            return false;
        }

        if (this.detectActivity(mc, context))
        {
            this.lastActivityMs = nowMs;
        }

        return nowMs - this.lastActivityMs < AFK_IDLE_MS;
    }

    // ==================================================
    // Исправление: полностью отключаем detectActivity,
    // чтобы избежать IndexOutOfBoundsException на iOS.
    // ==================================================
    private boolean detectActivity(MinecraftClient mc, UIContext context)
    {
        // На iOS вызов GLFW.glfwGetKey с некоторыми кодами клавиш
        // приводит к падению. Отключаем определение активности.
        return false;
    }
}

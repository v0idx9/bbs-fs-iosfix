package mchorse.bbs_mod.ui.film;

import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.utils.keys.KeyAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

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

    private boolean detectActivity(MinecraftClient mc, UIContext context)
    {
        // Движение мыши
        if (context.mouseX != this.lastMouseX || context.mouseY != this.lastMouseY)
        {
            this.lastMouseX = context.mouseX;
            this.lastMouseY = context.mouseY;
            return true;
        }

        // Колёсико мыши
        if (context.mouseWheel != 0D || context.mouseWheelHorizontal != 0D)
        {
            return true;
        }

        long handle = mc.getWindow().getHandle();

        // Кнопки мыши
        for (int b = 0; b <= GLFW.GLFW_MOUSE_BUTTON_LAST; b++)
        {
            if (GLFW.glfwGetMouseButton(handle, b) == GLFW.GLFW_PRESS)
            {
                return true;
            }
        }

        // Контекстная клавиша (например, нажатие на кнопку в UI)
        if (context.getKeyAction() != KeyAction.RELEASED && context.getKeyCode() != GLFW.GLFW_KEY_UNKNOWN)
        {
            return true;
        }

        // *** ИСПРАВЛЕНИЕ: удалён цикл по всем клавишам, который падал на iOS ***
        // Вместо этого, если нужна проверка нажатия любых клавиш, можно оставить
        // только несколько основных (например, WASD, стрелки), но для работы камеры
        // достаточно проверки мыши и контекстной клавиши.
        // Оставляем пустым – краша не будет, а камера будет работать.

        return false;
    }
}

package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import net.minecraft.client.MinecraftClient;

public class LineBreak extends TextNode
{
    @Override
    int getWidth()
    {
        return 0;
    }

    @Override
    int getHeight()
    {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    @Override
    public String toString()
    {
        return "\\n";
    }
}
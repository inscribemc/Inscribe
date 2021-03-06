package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class TextBlockWidget extends GuideWidget
{
    public static final Identifier MONO_FONT = new Identifier(Inscribe.MOD_ID, "mono");
    public static final float MAX_SCALE = 2.0F;
    private final Alignment horizontalAlignment,
                            verticalAlignment;
    private final int widthHint,
                      heightHint;
    private final List<TextNode> content;
    private final float scale;

    public TextBlockWidget(Alignment horizontalAlignment, Alignment verticalAlignment, List<TextNode> content, float scale)
    {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.content = content;
        this.scale = scale;
        int width = 0, height = 0;
        int lineWidth = 0, lineHeight = 0;
        for (Iterator<TextNode> iter = content.iterator(); iter.hasNext();)
        {
            TextNode node = iter.next();
            lineWidth += node.getWidth();
            lineHeight = Math.max(lineHeight, node.getHeight());
            if (!iter.hasNext() || node instanceof LineBreak)
            {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                //Reset for next line
                lineWidth = lineHeight = 0;
            }
        }
        this.widthHint = MathHelper.ceil(width * scale);
        this.heightHint = MathHelper.ceil(height * scale);
        margin().setVertical(1);
    }
    
    public TextBlockWidget(Alignment horizontalAlignment, Alignment verticalAlignment, TextNode... content)
    {
        this(horizontalAlignment, verticalAlignment, Arrays.asList(content), 1.0F);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        float left = horizontalAlignment.offsetX(x(), this, hintWidth());
        float x = left;
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        for (TextNode node : content)
        {
            if (node instanceof FormattedTextNode)
                ((FormattedTextNode) node).mouseClicked(x, y, (int) mouseX, (int) mouseY, button);
            if (node instanceof LineBreak)
            {
                y += 9;
                x = left;
            }
            else
                x += node.getWidth();
        }
        return false;
    }

    @Override
    public void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        float left = horizontalAlignment.offsetX(x(), this, hintWidth());
        float x = left;
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        int lineHeight = 0;
        matrices.push();
        matrices.scale(scale, scale, scale);
        Map<Vec2f, ElementHostNode> renderables = new HashMap<>();
        for (TextNode node : content)
        {
            lineHeight = Math.max(lineHeight, node.getHeight());
            node.render(vertices, matrices, x / scale, y / scale, mouseX, mouseY, lastFrameDuration);
            if (node instanceof ElementHostNode)
                renderables.put(new Vec2f(x, y), (ElementHostNode) node);
            if (node instanceof LineBreak)
            {
                y += lineHeight;
                x = left;
                lineHeight = 0;
            }
            else
                x += node.getWidth();
        }
        matrices.pop();
        // Attached renderables render over/after nodes
        for (Entry<Vec2f, ElementHostNode> entry : renderables.entrySet())
        {
            float x2 = entry.getKey().x, y2 = entry.getKey().y;
            entry.getValue().renderAttached(vertices, matrices, x2, y2, mouseX, mouseY, lastFrameDuration);
        }
    }

    @Override
    public int hintHeight()
    {
        return heightHint;
    }

    @Override
    public int hintWidth()
    {
        return widthHint;
    }

    @Override
    public void dispose()
    {
        for (TextNode node : content)
            node.dispose();
    }

    @Override
    public String toString()
    {
        return String.format("TextBlockWidget[nodes=%s, horizontalAlignment=%s, verticalAlignment=%s]", content, horizontalAlignment, verticalAlignment);
    }
}

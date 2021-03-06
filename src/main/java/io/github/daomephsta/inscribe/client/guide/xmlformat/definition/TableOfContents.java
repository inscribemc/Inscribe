package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import net.minecraft.util.Identifier;

public class TableOfContents
{
    private final Identifier id;
    private final GuideIdentifier filePath;
    private final List<Link> links;
    private final int columns;

    public TableOfContents(Identifier id, GuideIdentifier filePath, List<Link> links, int columns)
    {
        this.id = id;
        this.filePath = filePath;
        this.links = ImmutableList.copyOf(links);
        this.columns = columns;
    }

    public Identifier getId()
    {
        return id;
    }

    public GuideIdentifier getFilePath()
    {
        return filePath;
    }

    public Iterable<Link> getLinks()
    {
        return links;
    }

    public int getColumns()
    {
        return columns;
    }

    public static class Link
    {
        public final String name;
        public final Identifier destination;
        public final LinkStyle style;
        private final Consumer<GuideFlow> iconFactory;

        public Link(Consumer<GuideFlow> iconFactory, String name, Identifier destination, LinkStyle style)
        {
            this.iconFactory = iconFactory;
            this.name = name;
            this.destination = destination;
            this.style = style;
        }

        public void addIcon(GuideFlow output)
        {
            if (iconFactory == null)
                throw new NullPointerException("Icon not present");
            iconFactory.accept(output);
        }
    }
}

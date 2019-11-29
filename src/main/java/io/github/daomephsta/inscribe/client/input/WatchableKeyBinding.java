package io.github.daomephsta.inscribe.client.input;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;
import net.minecraft.util.Identifier;

public class WatchableKeyBinding extends FabricKeyBinding
{
    private final List<WatchedKeyBinding> children = new ArrayList<>();

    public WatchableKeyBinding(Identifier id, InputUtil.Type type, int code, String category)
    {
        super(id, type, code, category);
    }

    void addChild(WatchedKeyBinding child)
    {
        children.add(child);
    }

    @Override
    public void setKeyCode(KeyCode keyCode)
    {
        super.setKeyCode(keyCode);
        for (WatchedKeyBinding child : children)
            child.markDelegateDirty();
    }
}
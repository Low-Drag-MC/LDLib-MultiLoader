package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.block;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@LDLRegister(name = "remove block", group = "graph_processor.node.minecraft.block")
public class RemoveBockNode extends LinearTriggerNode {
    @InputPort
    public Level level;
    @InputPort
    public Vector3f xyz;

    @Override
    public void process() {
        if (level != null && xyz != null) {
            level.removeBlock(new BlockPos((int) xyz.x, (int) xyz.y, (int) xyz.z), false);
        }
    }
}

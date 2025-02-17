package thunder.hack.features.modules.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

import static thunder.hack.utility.render.Render3DEngine.*;
import static thunder.hack.utility.render.Render3DEngine.OUTLINE_QUEUE;
import static thunder.hack.utility.render.Render3DEngine.OUTLINE_SIDE_QUEUE;
import static thunder.hack.utility.render.Render3DEngine.FILLED_QUEUE;
import static thunder.hack.utility.render.Render3DEngine.FILLED_SIDE_QUEUE;
import static thunder.hack.utility.render.Render3DEngine.OutlineAction;
import static thunder.hack.utility.render.Render3DEngine.OutlineSideAction;
import static thunder.hack.utility.render.Render3DEngine.FillAction;
import static thunder.hack.utility.render.Render3DEngine.FillSideAction;

public class BlockHighLight extends Module {
    public BlockHighLight() {
        super("BlockHighLight", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting("Mode", Mode.Outline);
    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(0xFFFFFFFF));
    private final Setting<Float> lineWidth = new Setting<>("LineWidth", 1F, 0f, 5F);

    private enum Mode {
        Both, BothSide, Fill, FilledSide, Outline, OutlinedSide
    }

    public void onRender3D(MatrixStack stack) {
        if (mc.crosshairTarget == null) return;
        if (mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult bhr)) return;

        switch (mode.getValue()) {
            case Both -> {
                OUTLINE_QUEUE.add(new OutlineAction(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(), 255), lineWidth.getValue()));
                FILLED_QUEUE.add(new FillAction(new Box(bhr.getBlockPos()), color.getValue().getColorObject()));
            }
            case BothSide -> {
                OUTLINE_SIDE_QUEUE.add(new OutlineSideAction(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue(),bhr.getSide()));
                FILLED_SIDE_QUEUE.add(new FillSideAction(new Box(bhr.getBlockPos()),color.getValue().getColorObject(),bhr.getSide()));
            }
            case Fill -> FILLED_QUEUE.add(new FillAction(new Box(bhr.getBlockPos()), color.getValue().getColorObject()));
            case FilledSide -> FILLED_SIDE_QUEUE.add(new FillSideAction(new Box(bhr.getBlockPos()),color.getValue().getColorObject(),bhr.getSide()));

            case Outline ->  OUTLINE_QUEUE.add(new OutlineAction(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue()));
            case OutlinedSide -> OUTLINE_SIDE_QUEUE.add(new OutlineSideAction(new Box(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue(),bhr.getSide()));
        }
    }
}

package aiven.zomboidhealthsystem.foundation.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface OnClick {
    void onClick(int x, int y, int button);
}

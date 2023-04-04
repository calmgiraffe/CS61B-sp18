package byog.Core.Graphics;

import java.awt.*;

public class Colors {

    public static Color rainbowColor(int angle) {
        int r = (int) (Math.sin(Math.toRadians(angle)) * 127) + 128;
        int g = (int) (Math.sin(Math.toRadians(angle + 120)) * 127) + 128;
        int b = (int) (Math.sin(Math.toRadians(angle + 240)) * 127) + 128;
        return new Color(r, g, b);
    }

    public static Color shimmeringColor(Color baseColor, int shimmerAmplitude) { // Todo: redo
        int r = baseColor.getRed();
        int g = baseColor.getGreen();
        int b = baseColor.getBlue();
        int angle = (r + g + b) % 360;
        int shimmerOffset = (int) (shimmerAmplitude * Math.sin(Math.toRadians(angle)));
        return new Color(
                Math.min(255, Math.max(0, r + shimmerOffset)),
                Math.min(255, Math.max(0, g + shimmerOffset)),
                Math.min(255, Math.max(0, b + shimmerOffset))
        );
    }
}

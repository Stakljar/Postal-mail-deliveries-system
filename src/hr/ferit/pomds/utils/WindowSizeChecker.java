package hr.ferit.pomds.utils;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

public class WindowSizeChecker {

	/**
	 * @param width limit value for width
	 * @param height limit value for height
	 * @return 0 if device resolution is above limit values, 1 if otherwise, 2 if not supported
	 * @author Dražen Antunović
	 */
	public static int checkWindowSize(int width, int height) {
		
		try {
			GraphicsDevice graphicsDevice = GraphicsEnvironment.
					getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if(graphicsDevice.getDisplayMode().getWidth() > width && graphicsDevice.getDisplayMode().getHeight() > height) {
				return 0;
			}
			else {
				return 1;
			}
		}
		catch (HeadlessException e) {
			return 2;
		}
	}
}

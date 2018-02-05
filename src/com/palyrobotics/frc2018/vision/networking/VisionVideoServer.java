package com.palyrobotics.frc2018.vision.networking;

import com.palyrobotics.frc2018.util.logger.Logger;
import com.palyrobotics.frc2018.vision.VisionData;
import com.palyrobotics.frc2018.vision.util.AbstractVisionServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Sends video from the robot to the dashboard. Video data is obtained from queue in {@link VisionData}
 *
 * @author Quintin Dwight
 */
public class VisionVideoServer extends AbstractVisionServer {


	public VisionVideoServer() {
		super("Video Server");
	}

	@Override public void afterInit() { }

	/**
	 * Writes the image given in a byte array to the output stream for the javascript client to read and display on the dashboard.
	 */
	private void writeImageToServer(final byte[] data) {
		try {
			final DataOutputStream outputStream = new DataOutputStream(m_Client.getOutputStream());
			outputStream.writeInt(data.length);
			outputStream.write(data);
		} catch (final IOException ioe) {
			log(Level.FINEST, ioe.toString());
			closeClient();
		}
	}

	@Override
	protected void afterUpdate() {
		switch (m_ThreadState) {
			case RUNNING: {
				switch (m_ServerState) {
					case OPEN: {
						final ConcurrentLinkedQueue<byte[]> videoFrames = VisionData.getVideoQueue();
						// Send frame from nexus if they exist, else show default image
						if (videoFrames.size() > 0)
							writeImageToServer(videoFrames.remove());
						break;
					}
				}
				break;
			}
		}
	}
}

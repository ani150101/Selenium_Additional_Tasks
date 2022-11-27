package automating.other.things;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MonitorMailThread extends Thread{
	private String timeDiff;
	private SimpleEmail email;
	private boolean upDownFlag;
	
	public MonitorMailThread (String timeDiff, boolean upDownFlag) {
		this.timeDiff = timeDiff;
		this.upDownFlag = upDownFlag;
	}
	@Override
	public void run() {
		email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("singhaniket150101@gmail.com", "dvtfmdtnvucmojlt"));
		email.setSSLOnConnect(true);
		try {
			email.setFrom("singhaniket150101@gmail.com");
			email.addTo("memyselfpro@gmail.com");
			if (!upDownFlag) {
				email.setSubject("Jenkins server down");
				email.setMsg("Jenkins server was up for " + timeDiff);
				if (email.send().length() > 0) {
					System.out.println("Server down: Email sent!");
				}
			}
			else if (upDownFlag) {
				email.setSubject("Jenkins server is up and running");
				email.setMsg("Jenkins server was down for " + timeDiff);
				if (email.send().length() > 0) {
					System.out.println("Server up: Email sent!");
				}
			}
		} catch (EmailException e) {
			e.printStackTrace();
		}
		
	}

}

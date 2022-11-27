package automating.other.things;

import java.util.Date;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ServerMonitoring {
	static Date upDt = null;
	static Date downDt = null;
	static SimpleEmail email = null;

	public static void main(String[] args) throws InterruptedException, EmailException {

		String url = "http://localhost:8080/login";

		Response response = null;
		System.out.println("Monitoring " + url);
		while (true) {
			try {
				response = RestAssured.get(url);

				int code = response.getStatusCode();
				if (code > 400) {
					throw new Exception("Server Down");
				}

				if (code < 400) {
					if (upDt == null) {
						upDt = new Date();
					}
					if (downDt != null) {
						MonitorMailThread mailThread = new MonitorMailThread(timeDiff(downDt, upDt), true);
						mailThread.start();
						downDt = null;
					}
				}
			} catch (Exception e) {
				handleError4xx();
			}
			Thread.sleep(1000);
		}
	}

	private static void handleError4xx() throws EmailException {
		if (downDt == null) {
			downDt = new Date();
			MonitorMailThread mailThread = new MonitorMailThread(timeDiff(upDt, downDt), false);
			mailThread.start();
		}

		upDt = null;
	}

	private static String timeDiff(Date date, Date date2) {
		long difference_In_Time = date2.getTime() - date.getTime();
		long difference_In_Seconds = (difference_In_Time / 1000) % 60;
		long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
		long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

		String hrsDiff = difference_In_Hours > 0 ? difference_In_Hours + " hours, " : "";

		String minsDiff = difference_In_Minutes > 0
				? (difference_In_Minutes == 1 ? difference_In_Minutes + " minute, "
						: difference_In_Minutes + " minutes, ")
				: "";
		String secsDiff = difference_In_Seconds > 0 ? difference_In_Seconds + " seconds" : "";

		return (hrsDiff + minsDiff + secsDiff);
	}

}

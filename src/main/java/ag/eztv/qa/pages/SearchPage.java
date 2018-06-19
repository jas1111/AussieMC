package ag.eztv.qa.pages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.client.Client.ClientState;

import ag.eztv.qa.base.BasePage;

public class SearchPage extends BasePage {

	// Logger
	private Logger logger = LogManager.getLogger(this.getClass());
	
	// Page objects
	// Identify the table
	WebElement Table = driver.findElement(By
			.xpath("//*[@id='header_holder']/table[5]/tbody"));

	// Fetch # rows in table
	List<WebElement> rows = Table.findElements(By.tagName("tr"));


	// Init
	public SearchPage() {
		PageFactory.initElements(driver, this);
	}
	
	// To verify landing page
	public String verifyPageTitle() {
		return driver.getTitle();
	}

	// Reading the table rows and columns
	public void verifyReadTable() throws IOException {
		int rnum, cnum;
		rnum = 1;

		for (WebElement trElement : rows.subList(0, 13)) {      // Limiting to 10 rows ( 3 - 13 in table )
			
			// To traverse all the row elements, use the below for loop instead of the top one.
			// for (WebElement trElement : rows ) {
			
			List<WebElement> td_collection = trElement.findElements(By.xpath("td"));

			cnum = 1;
			for (WebElement tdElement : td_collection) {
				if (rnum > 2 && rnum < 10 && cnum == 2) {
					// if (rnum > 2 && cnum == 2) {

					try {

						WebElement td_link = driver
								.findElement(By
										.xpath("//*[@id='header_holder']/table[5]/tbody/tr["
												+ rnum + "]/td[3]/a[2]"));


						String[] torrent_name = td_link.getAttribute("href").split("/");

						logger.info(torrent_name[4]);

					
						String[] episode_id = tdElement.getText().split(" ");
						String episode_id_status = episode_id[2] + "-Done";

						logger.info(episode_id_status);

						if (searchIfDownloaded(prop.getProperty("stats_filename"), episode_id_status) == true) 
						{

							logger.info("---------- Download already done");
							
						} else {
							try {


								FileWriter fileWriter = new FileWriter(
										prop.getProperty("stats_filename"),
										true);
								BufferedWriter bufferedWriter = new BufferedWriter(
										fileWriter);

								logger.info("----------Writing New episode ID to file and downloading");	
								bufferedWriter.write(episode_id[2] + '-');
								// bufferedWriter.write(torrent_href + '\t');

								// 1. Clicking on the torrent link
								
								td_link.click();

								// 2. Downloading the file using torrent client.

								logger.info("----------- Downloading the FILE ");
								
								downloadTorrent(prop.getProperty("tor_path"),
										torrent_name);

								// 3. Updating the download_stats.txt with Episode_ID-Done

								bufferedWriter.write("Done");
								bufferedWriter.newLine();

								logger.info("----------- Episode ID Status log Done");
								bufferedWriter.close();

							}

							catch (IOException ex) {

								logger.error("*************** Error writing to file", ex );
								// Or we could just do this:
								// ex.printStackTrace();
							}
						}

					} catch (NoSuchElementException e) {
						System.out.println("NoSuchElementException occured");
						logger.error("*************** Element NOT found ", e);
					}

				}

				cnum++;

			}

			rnum++;
		}

	}

	public static boolean searchIfDownloaded(String filePath, String searchQuery)
			throws IOException {
		searchQuery = searchQuery.trim();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath)));
			String line = null;

			while ((line = br.readLine()) != null) {

				if (line.matches(searchQuery)) {
					br.close();
					return true;
				}

			}
		} catch (Exception e) {
			System.err.println("Exception while closing bufferedreader "
					+ e.toString());
		}

		return false;
	}

	public static void downloadTorrent(String tor_path, String[] torrent_name) {

		File output = new File(prop.getProperty("tor_path"));

		File torrentPath = new File(tor_path + torrent_name[4]);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println(e);
		}

		// Start downloading file
		try {
			SharedTorrent torrent = SharedTorrent.fromFile(torrentPath, output);
			System.out.println("Starting client for torrent: "
					+ torrent.getName());
			Client client = new Client(InetAddress.getLocalHost(), torrent);

			try {
				System.out.println("Start to download: " + torrent.getName());
				client.share(); // SEEDING for completion signal
				// client.download() // DONE for completion signal

				while (!ClientState.SEEDING.equals(client.getState())) {
					// Check if there's an error
					if (ClientState.ERROR.equals(client.getState())) {
						throw new Exception("ttorrent client Error State");
					}

					// Display statistics
					System.out
							.printf("%f %% - %d bytes downloaded - %d bytes uploaded\n",
									torrent.getCompletion(),
									torrent.getDownloaded(),
									torrent.getUploaded());

					// Wait one second
					TimeUnit.SECONDS.sleep(1);
				}

				System.out.println("download completed.");

			} catch (Exception e) {
				System.err.println("An error occurs...");
				e.printStackTrace(System.err);
			} finally {
				System.out.println("stop client.");
				client.stop();

				if (torrentPath.delete()) {
					System.out.println("TORRENT File deleted successfully");
				} else {
					System.out.println("Failed to delete the TORRENT file");
				}
			}
		} catch (Exception e) {
			System.err.println("An error occurs...");
			e.printStackTrace(System.err);
		}
	}

	public boolean verifyFileDownloaded(String downloadPath, String fileName) {


		boolean flag = false;
		File dir = new File(downloadPath);
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			if (dir_contents[i].getName().endsWith(fileName))
				return flag = true;
		}

		return flag;
	}

	public boolean verifyTorrentCleaup(String downloadPath, String fileName) {

//		boolean flag = true;
		File dir = new File(downloadPath);
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			if (dir_contents[i].getName().endsWith(fileName))
				return false;
		}

		return true;
	}

}

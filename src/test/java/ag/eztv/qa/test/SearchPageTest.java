package ag.eztv.qa.test;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ag.eztv.qa.base.BasePage;
import ag.eztv.qa.pages.SearchPage;

public class SearchPageTest extends BasePage{
	
	SearchPage searchpage;
	
	public SearchPageTest(){
		super();
	}
	
	@BeforeClass
	public void setUp(){

		initialization();
		searchpage = new SearchPage();
	}
	
	@Test(priority=1)
	public void searchPageTitleTest(){

		String title = searchpage.verifyPageTitle();
		Assert.assertEquals(title, "EZTV - TV Torrents Online Series Download | Official");
	}
	
	
	@Test(priority=2)
	public void searchPageDownloadTest(){

		try {
			searchpage.verifyReadTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertTrue(searchpage.verifyFileDownloaded(prop.getProperty("tor_path"), "mkv"), "Failed to download Torrent MKV File");
		
	}
	
	@Test(priority=3)
	public void torrentCleanupTest(){

		Assert.assertTrue(searchpage.verifyTorrentCleaup(prop.getProperty("tor_path"), "torrent"), "Failed to cleanup TORRENT file");
		
	}
	
	@AfterClass
	public void tearDown(){

		driver.quit();
	}
	
	
}

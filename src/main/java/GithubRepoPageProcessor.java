import custom.HttpClientDownloader;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yaochao on 2018/10/19 上午11:16
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Logger logger = LoggerFactory.getLogger(GithubRepoPageProcessor.class);

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class=\"public\"]/strong/a/text()").toString());
        if (page.getResultItems().get("name") == null) {
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//*[@id=\"readme\"]/text()"));
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor()).setDownloader(new HttpClientDownloader()).addUrl("https://github.com/yaochao").run();
    }
}

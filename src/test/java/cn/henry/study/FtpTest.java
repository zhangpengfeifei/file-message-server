package cn.henry.study;

import cn.henry.study.application.FtpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * description: FTP测试用例
 *
 * @author Hlingoes
 * @date 2019/12/22 23:52
 */
@SpringBootTest(classes = FileMessageServer.class)
@RunWith(SpringRunner.class)
public class FtpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpTest.class);

    @Autowired
    private FtpService ftpService;


    public void testDownload() {
        ftpService.download("/资料/bak/[muchong.com]数学物理方法.pdf",
                "G:\\迅雷下载\\[muchong.com]数学物理方法.pdf");
    }

    @Test
    public void testUpload() {
        File dir = new File("G:\\下载");
        File[] files = dir.listFiles();
        String path = "/books/bak/";
        for (File file : files) {
            ftpService.upload(path, file.getName(), file);
        }
    }

    public void testDelete() {
        String path = "/资料/bak/[emuch.net]普林斯顿数学指南.pdf";
        ftpService.delete(path);
    }

}

package cn.henry.study;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * description: 文件服务应用的启动类，包括http，ftp，rabbitmq系列服务
 * 自定义的bean解析，需要取消自动注解
 *
 * @author Hlingoes
 * @date 2019/12/21 18:36
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("cn.henry.study.mapper")
public class WebMessageServer {
    public static void main(String[] args) {
        SpringApplication.run(WebMessageServer.class);
    }
}
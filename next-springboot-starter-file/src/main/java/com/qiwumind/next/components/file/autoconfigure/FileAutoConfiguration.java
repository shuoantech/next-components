package com.qiwumind.next.components.file.autoconfigure;

import com.qiwumind.next.components.file.core.client.FileClientFactory;
import com.qiwumind.next.components.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 * @author qiwumind
 */
@Configuration(proxyBeanMethods = false)
public class FileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}

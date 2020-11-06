package org.yage.hello.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yage.hello.entity.request.HelloRequest;
import org.yage.hello.entity.response.BaseResponse;
import org.yage.hello.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/9/21
 * @date 23:12
 */
@Slf4j
@Service
public class HelloService {

    /**
     * @param helloRequest
     * @return
     */
    public BaseResponse sayHello(HelloRequest helloRequest) {

        log.info(LogUtil.ing("Hello : " + helloRequest.getUsername()));

        return BaseResponse.success(helloRequest);
    }

    /**
     * @param multipartFile
     * @return
     */
    public BaseResponse sayHelloByUpload(MultipartFile multipartFile) throws IOException {

        InputStream inputStream = this.compress(multipartFile);
        this.upload(inputStream);

        return BaseResponse.success("upload success");
    }

    private InputStream compress(MultipartFile multipartFile) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(multipartFile.getInputStream()).scale(0.1).toOutputStream(byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private void upload(InputStream inputStream) {

        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4GHyjCpLRSng1PmGjjNW";
        String accessKeySecret = "hehJzwP2KkgdGdsOqGQVgpS3o8nLqe";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        ossClient.putObject("yage-first", "upload_zip_test", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}

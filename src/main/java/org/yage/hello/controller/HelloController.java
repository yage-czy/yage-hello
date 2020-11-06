package org.yage.hello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yage.hello.entity.request.HelloRequest;
import org.yage.hello.entity.response.BaseResponse;
import org.yage.hello.service.HelloService;

import java.io.IOException;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/9/21
 * @date 23:11
 */
@Slf4j
@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    /**
     * @param helloRequest
     * @return
     */
    @RequestMapping(value = "/sayHelloByJson", method = RequestMethod.POST)
    public BaseResponse sayHelloByJson(@RequestBody HelloRequest helloRequest) {

        return this.helloService.sayHello(helloRequest);
    }

    /**
     * @param helloRequest
     * @return
     */
    @RequestMapping(value = "/sayHelloByForm", method = RequestMethod.POST)
    public BaseResponse sayHelloByForm(@ModelAttribute HelloRequest helloRequest) {

        return this.helloService.sayHello(helloRequest);
    }

    /**
     * @param username
     * @return
     */
    @RequestMapping(value = "/sayHelloByGet", method = RequestMethod.GET)
    public BaseResponse sayHelloByGet(@RequestParam String username) {

        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setUsername(username);
        return this.helloService.sayHello(helloRequest);
    }

    /**
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/sayHelloByUpload", method = RequestMethod.POST)
    public BaseResponse sayHelloByUpload(
            @RequestParam(value = "helloFile01", required = true) MultipartFile helloFile01,
            @RequestParam(value = "helloFile02", required = true) MultipartFile helloFile02) throws IOException {

        return this.helloService.sayHelloByUpload(helloFile01);
    }
}

package org.yage.hello.filter.apilog;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.yage.hello.util.LogUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/9/23
 * @date 22:55
 */
@Slf4j
public class ApiLogFilter extends OncePerRequestFilter {

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        // 获取请求地址
        String requestUri = httpServletRequest.getRequestURI();

        // 获取method
        String method = httpServletRequest.getMethod();

        // 获取请求IP
        String remoteAddr = this.getRemoteAddr(httpServletRequest);

        // 获取CONTENT_TYPE
        String headerOfContentType = httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE);

        // 将参数转换为JSON字符串
        String requestBodyAsJson = this.getRequestBodyAsJson(contentCachingRequestWrapper, method, headerOfContentType);

        // 将header转换为JSON字符串
        String requestHeaderAsJson = this.getRequestHeaderAsJson(httpServletRequest);

        String requestId = UUID.randomUUID().toString();

        // 设置日志信息
        MDC.put(LogUtil.KEY_REQUEST_ID, requestId);
        MDC.put(LogUtil.KEY_REQUEST_URI, requestUri);
        MDC.put(LogUtil.KEY_REQUEST_METHOD, method);
        MDC.put(LogUtil.KEY_REQUEST_BODY_AS_JSON, requestBodyAsJson);
        MDC.put(LogUtil.KEY_REQUEST_HEADER_AS_JSON, requestHeaderAsJson);
        MDC.put(LogUtil.KEY_REMOTE_ADDR, remoteAddr);

        log.info(LogUtil.start());

        // doFilter
        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);

        // 获取接口返回值
        String resultJson = IOUtils.toString(contentCachingResponseWrapper.getContentInputStream(), contentCachingResponseWrapper.getCharacterEncoding());
        MDC.put(LogUtil.KEY_RESPONSE_CODE, String.valueOf(contentCachingResponseWrapper.getStatus()));
        MDC.put(LogUtil.KEY_RESPONSE_BODY_AS_JSON, resultJson);

        log.info(LogUtil.end());

        // 重新设置response内容
        contentCachingResponseWrapper.copyBodyToResponse();
    }

    /**
     * @param contentCachingRequestWrapper
     * @param method
     * @param headerOfContentType
     * @return
     * @throws IOException
     */
    private String getRequestBodyAsJson(
            ContentCachingRequestWrapper contentCachingRequestWrapper,
            String method,
            String headerOfContentType) throws IOException {

        String requestBody = "YAGE_UNKNOWN_REQUEST_BODY";

        // get
        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            Map<String, String[]> parameterMap = contentCachingRequestWrapper.getParameterMap();
            return JSON.toJSONStringWithDateFormat(parameterMap, JSON.DEFFAULT_DATE_FORMAT);
        }

        // application/x-www-form-urlencoded
        if (headerOfContentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            Map<String, String[]> parameterMap = contentCachingRequestWrapper.getParameterMap();
            return JSON.toJSONStringWithDateFormat(parameterMap, JSON.DEFFAULT_DATE_FORMAT);
        }

        // application/json
        if (headerOfContentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            return IOUtils.toString(contentCachingRequestWrapper.getBody(), contentCachingRequestWrapper.getCharacterEncoding());
        }

        // multipart/form-data
        if (headerOfContentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {

            MultipartResolver multipartResolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest multipartHttpServletRequest = multipartResolver.resolveMultipart(contentCachingRequestWrapper);

            Map<String, String[]> parameterMap = multipartHttpServletRequest.getParameterMap();

            Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
            Set<Map.Entry<String, MultipartFile>> entrySet = fileMap.entrySet();
            Map<String, Map<String, String>> fileInfoMap = new HashMap<>(entrySet.size());
            entrySet.forEach(e -> {

                String key = e.getKey();
                MultipartFile value = e.getValue();

                Map<String, String> itemFileInfoMap = new HashMap<>();
                itemFileInfoMap.put("contentType", value.getContentType());
                itemFileInfoMap.put("name", value.getName());
                itemFileInfoMap.put("originalFilename", value.getOriginalFilename());
                itemFileInfoMap.put("size", String.valueOf(value.getSize()));

                fileInfoMap.put(key, itemFileInfoMap);
            });

            Map<String, Object> map = new HashMap<>();
            map.put("paramInfo", parameterMap);
            map.put("fileInfo", fileInfoMap);

            return JSON.toJSONStringWithDateFormat(map, JSON.DEFFAULT_DATE_FORMAT);
        }

        return requestBody;
    }

    /**
     * 获取发送请求的客户端IP
     *
     * @param httpServletRequest
     * @return
     */
    private String getRemoteAddr(HttpServletRequest httpServletRequest) {

        String unknownIp = "unknown";

        String ip = httpServletRequest.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || unknownIp.equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || unknownIp.equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || unknownIp.equalsIgnoreCase(ip)) {
            ip = httpServletRequest.getRemoteAddr();
        }

        return ip;
    }

    /**
     * @param httpServletRequest
     * @return
     */
    private String getRequestHeaderAsJson(HttpServletRequest httpServletRequest) {

        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        Map<String, String> map = new HashMap<>();

        while (headerNames.hasMoreElements()) {

            String nextElement = headerNames.nextElement();
            map.put(nextElement, httpServletRequest.getHeader(nextElement));
        }

        return JSON.toJSONString(map);
    }
}

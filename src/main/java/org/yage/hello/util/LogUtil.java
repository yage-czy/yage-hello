package org.yage.hello.util;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.yage.hello.constant.RequestStageEnum;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author 常振亚
 * @version 1.0
 * @date 2020/10/7
 * @date 20:14
 */
public class LogUtil {

    public static final String KEY_REQUEST_ID = "requestId";
    public static final String KEY_REQUEST_URI = "requestUri";
    public static final String KEY_REQUEST_STAGE = "requestStage";
    public static final String KEY_CURRENT_TIME = "currentTime";
    public static final String KEY_DURATION = "duration";

    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_REQUEST_METHOD = "requestMethod";
    public static final String KEY_REQUEST_BODY_AS_JSON = "requestBodyAsJson";
    public static final String KEY_REQUEST_HEADER_AS_JSON = "requestHeaderAsJson";
    public static final String KEY_REMOTE_ADDR = "remoteAddr";

    public static final String KEY_RESPONSE_CODE = "responseCode";
    public static final String KEY_RESPONSE_BODY_AS_JSON = "responseBodyAsJson";

    public static final String KEY_ING_MESSAGE = "ingMessage";

    /**
     * 系统换行符
     */
    private static final String SLS = System.getProperty("line.separator");

    private static final String START_LOG = SLS +
            "--------------------------------" +
            SLS + KEY_REQUEST_ID + " -> %s" +
            SLS + KEY_REQUEST_URI + " -> %s" +
            SLS + KEY_REQUEST_METHOD + " -> %s" +
            SLS + KEY_REQUEST_STAGE + " -> %s" +
            SLS + KEY_CURRENT_TIME + " -> %s" +
            SLS + KEY_DURATION + " -> %s" +
            SLS + KEY_REQUEST_BODY_AS_JSON + " -> %s" +
            SLS + KEY_REQUEST_HEADER_AS_JSON + " -> %s" +
            SLS + KEY_REMOTE_ADDR + " -> %s";

    private static final String END_LOG = SLS +
            "================================" +
            SLS + KEY_REQUEST_ID + " -> %s" +
            SLS + KEY_REQUEST_URI + " -> %s" +
            SLS + KEY_REQUEST_METHOD + " -> %s" +
            SLS + KEY_REQUEST_STAGE + " -> %s" +
            SLS + KEY_CURRENT_TIME + " -> %s" +
            SLS + KEY_DURATION + " -> %s" +
            SLS + KEY_RESPONSE_CODE + " -> %s" +
            SLS + KEY_RESPONSE_BODY_AS_JSON + " -> %s";

    public static final String ING_LOG = SLS +
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
            SLS + KEY_REQUEST_ID + " -> %s" +
            SLS + KEY_REQUEST_URI + " -> %s" +
            SLS + KEY_REQUEST_STAGE + " -> %s" +
            SLS + KEY_CURRENT_TIME + " -> %s" +
            SLS + KEY_DURATION + " -> %s" +
            SLS + KEY_ING_MESSAGE + " -> %s";

    public static final String WARN_LOG = SLS +
            "********************************" +
            SLS + KEY_REQUEST_ID + " -> %s" +
            SLS + KEY_REQUEST_URI + " -> %s" +
            SLS + KEY_REQUEST_STAGE + " -> %s" +
            SLS + KEY_CURRENT_TIME + " -> %s" +
            SLS + KEY_DURATION + " -> %s" +
            SLS + "warnMessage -> {}";

    private static final String ERROR_LOG = SLS +
            "################################" +
            SLS + KEY_REQUEST_ID + " -> %s" +
            SLS + KEY_REQUEST_URI + " -> %s" +
            SLS + KEY_REQUEST_STAGE + " -> %s" +
            SLS + KEY_CURRENT_TIME + " -> %s" +
            SLS + KEY_DURATION + " -> %s" +
            SLS + "errorMessage -> {}";

    /**
     * 计算消耗时间
     */
    private static void calculateDuration() {

        // 如果是start日志，直接计算为0
        String requestStage = Optional.ofNullable(MDC.get(KEY_REQUEST_STAGE)).orElse(RequestStageEnum.START.name());
        if (RequestStageEnum.START.name().equals(requestStage)) {
            MDC.put(KEY_DURATION, String.valueOf(0));
            return;
        }

        // IF - NULL - 不能因为日志记录异常导致业务执行异常，所以此处如果为空不抛出异常
        long startTimeStr = Optional.ofNullable(MDC.get(KEY_START_TIME)).map(Long::parseLong).orElse(0L);

        long now = System.currentTimeMillis();

        MDC.put(KEY_DURATION, String.valueOf(now - startTimeStr));
    }

    /**
     * start log
     *
     * @return
     */
    public static String start() {

        String requestStage = RequestStageEnum.START.name();
        String currentTime = String.valueOf(System.currentTimeMillis());

        MDC.put(KEY_REQUEST_STAGE, requestStage);
        MDC.put(KEY_CURRENT_TIME, currentTime);
        MDC.put(KEY_START_TIME, currentTime);
        calculateDuration();

        return String.format(START_LOG,
                MDC.get(KEY_REQUEST_ID),
                MDC.get(KEY_REQUEST_URI),
                MDC.get(KEY_REQUEST_METHOD),
                MDC.get(KEY_REQUEST_STAGE),
                MDC.get(KEY_CURRENT_TIME),
                MDC.get(KEY_DURATION),
                MDC.get(KEY_REQUEST_BODY_AS_JSON),
                MDC.get(KEY_REQUEST_HEADER_AS_JSON),
                MDC.get(KEY_REMOTE_ADDR));
    }

    /**
     * end log
     *
     * @return
     */
    public static String end() {

        String requestStage = RequestStageEnum.END.name();
        String currentTime = String.valueOf(System.currentTimeMillis());

        MDC.put(KEY_REQUEST_STAGE, requestStage);
        MDC.put(KEY_CURRENT_TIME, currentTime);
        calculateDuration();

        return String.format(END_LOG,
                MDC.get(KEY_REQUEST_ID),
                MDC.get(KEY_REQUEST_URI),
                MDC.get(KEY_REQUEST_METHOD),
                MDC.get(KEY_REQUEST_STAGE),
                MDC.get(KEY_CURRENT_TIME),
                MDC.get(KEY_DURATION),
                MDC.get(KEY_RESPONSE_CODE),
                MDC.get(KEY_RESPONSE_BODY_AS_JSON));
    }

    /**
     * ing log
     *
     * @param ingMessage
     * @return
     */
    public static String ing(String ingMessage) {

        String requestStage = RequestStageEnum.ING.name();
        String currentTime = String.valueOf(System.currentTimeMillis());

        MDC.put(KEY_REQUEST_STAGE, requestStage);
        MDC.put(KEY_CURRENT_TIME, currentTime);
        calculateDuration();

        return String.format(ING_LOG,
                MDC.get(KEY_REQUEST_ID),
                MDC.get(KEY_REQUEST_URI),
                MDC.get(KEY_REQUEST_STAGE),
                MDC.get(KEY_CURRENT_TIME),
                MDC.get(KEY_DURATION),
                ingMessage);
    }

    /**
     * warn log
     *
     * @param logger
     * @param warnMessage
     */
    public static void warn(Logger logger, String warnMessage) {

        String requestStage = RequestStageEnum.ING.name();
        String currentTime = String.valueOf(System.currentTimeMillis());

        MDC.put(KEY_REQUEST_STAGE, requestStage);
        MDC.put(KEY_CURRENT_TIME, currentTime);
        calculateDuration();

        logger.warn(WARN_LOG,
                MDC.get(KEY_REQUEST_ID),
                MDC.get(KEY_REQUEST_URI),
                MDC.get(KEY_REQUEST_STAGE),
                MDC.get(KEY_CURRENT_TIME),
                MDC.get(KEY_DURATION));
    }

    /**
     * error log
     *
     * @param logger
     * @param e
     */
    public static void error(Logger logger, Exception e) {

        String requestStage = RequestStageEnum.END.name();
        String currentTime = String.valueOf(System.currentTimeMillis());

        MDC.put(KEY_REQUEST_STAGE, requestStage);
        MDC.put(KEY_CURRENT_TIME, currentTime);
        calculateDuration();

        // 拼接异常信息字符串
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        String errorMessage = stringWriter.toString();

        try {
            printWriter.close();
            stringWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        logger.error(ERROR_LOG,
                MDC.get(KEY_REQUEST_ID),
                MDC.get("requestUri"),
                requestStage,
                errorMessage);
    }

}

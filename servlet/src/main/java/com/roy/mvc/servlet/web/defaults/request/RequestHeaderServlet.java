package com.roy.mvc.servlet.web.request;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
        printHeaderUtils(request);
        printEtc(request);
    }

    private void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-START-LINE START ---");
        System.out.printf("request.getMethod(): %s%n", request.getMethod());
        System.out.printf("request.getProtocol(): %s%n", request.getProtocol());
        System.out.printf("request.getScheme(): %s%n", request.getScheme());
        System.out.printf("request.getRequestURL(): %s%n", request.getRequestURL());
        System.out.printf("request.getRequestURI(): %s%n", request.getRequestURI());
        System.out.printf("request.getQueryString(): %s%n", request.getQueryString());
        System.out.printf("request.isSecure(): %s%n", request.isSecure());
        System.out.println("--- REQUEST-START-LINE END ---");
    }

    private void printHeaders(HttpServletRequest request) {
        System.out.println("--- HEADERS START ---");
        request.getHeaderNames().asIterator().forEachRemaining(
                headerName -> System.out.printf("%s: %s%n", headerName, request.getHeader(headerName))
        );
        System.out.println("--- HEADERS END ---");
    }

    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- HEADER ํธ์ ์กฐํ START ---");
        System.out.println("[Host ํธ์ ์กฐํ]");
        System.out.printf("request.getServerName(): %s%n", request.getServerName());
        System.out.printf("request.getServerPort(): %s%n", request.getServerPort());
        System.out.println();

        System.out.println("[Accept-Language ํธ์ ์กฐํ]");
        request.getLocales().asIterator().forEachRemaining(
                locale -> System.out.printf("locale: %s%n", locale));
        System.out.printf("request.getLocale(): %s%n", request.getLocale());
        System.out.println();

        System.out.println("[Cookie ํธ์ ์กฐํ]");
        if (Objects.nonNull(request.getCookies())) {
            Arrays.stream(request.getCookies()).forEach(cookie ->
                System.out.printf("%s: %s%n", cookie.getName(), cookie.getValue())
            );
        }
        System.out.println();

        System.out.println("[Content ํธ์ ์กฐํ]");
        System.out.printf("request.getContentType(): %s%n", request.getContentType());
        System.out.printf("request.getContentLength(): %s%n", request.getContentLength());
        System.out.printf("request.getCharacterEncoding(): %s%n", request.getCharacterEncoding());
        System.out.println("--- HEADER ํธ์ ์กฐํ END ---");
        System.out.println();
    }

    private void printEtc(HttpServletRequest request) {
        System.out.println("--- ๊ธฐํ ์กฐํ START ---");

        System.out.println("[Remote ์?๋ณด]");
        System.out.printf("request.getRemoteHost(): %s%n", request.getRemoteHost());
        System.out.printf("request.getRemoteAddr(): %s%n", request.getRemoteAddr());
        System.out.printf("request.getRemotePort(): %s%n", request.getRemotePort());
        System.out.println();

        System.out.println("[Local ์?๋ณด]");
        System.out.printf("request.getLocalName(): %s%n", request.getLocalName());
        System.out.printf("request.getLocalAddr(): %s%n", request.getLocalAddr());
        System.out.printf("request.getLocalPort(): %s%n", request.getLocalPort());

        System.out.println("--- ๊ธฐํ ์กฐํ END ---");
        System.out.println();
    }

}

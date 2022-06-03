package hello.upload.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/servlet")
public class ServletUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(HttpServletRequest request) throws ServletException, IOException {
        log.info("request = {}", request);
        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);
        Collection<Part> parts = request.getParts();
        log.info("parts = {}", parts);

        int index = 0;
        for (Part part : parts) {
            index++;
            log.info("===== PART - {} =====", index);
            log.info("name = {}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName, part.getHeader(headerName));
            }

            log.info("submittedFileName = {}", part.getSubmittedFileName());
            log.info("size = {}", part.getSize());

            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body = {}", body);

            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("fullPath = {}", fullPath);
                part.write(fullPath);
            }
        }
        return "upload-form";
    }

}

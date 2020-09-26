package demo.security.controller;

import demo.test.dto.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * 文件控制器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 20:48
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @PostMapping
    public FileInfo upload(MultipartFile file, HttpSession session) throws IOException {
        FileInfo fileInfo = new FileInfo();
        String realPath = session.getServletContext().getRealPath("/");
        // 测试用
        //String realPath = "d:/";
        String realFileName = file.getOriginalFilename();
        Path localPath = Paths.get(realPath + FileSystems.getDefault().getSeparator()
                                           + Instant.now().toEpochMilli() + realFileName);

        FileChannel fileChannel = FileChannel.open(localPath, WRITE, CREATE_NEW);
        fileChannel.transferFrom(Channels.newChannel(file.getInputStream()), 0, file.getSize());

        fileInfo.setPath(localPath.toString());
        if (log.isDebugEnabled())
        {
            log.debug("上传文件名：{}，保存路径信息：{}", realFileName, localPath.toString());
        }
        return fileInfo;
    }

    @GetMapping("/{id}")
    public void download(@PathVariable("id") String fileName,
                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StringUtils.isNotBlank(fileName))
        {
            fileName = fileName.replaceAll("_", ".");
            log.info("fileName = {}", fileName);
            String localPath = request.getServletContext().getRealPath("/");
            // 测试用
            //String localPath = "D:/";
            String localFile = localPath + FileSystems.getDefault().getSeparator() + fileName;
            try (FileChannel localChannel = FileChannel.open(Paths.get(localFile), READ);
                 WritableByteChannel targetChannel = Channels.newChannel(response.getOutputStream())
            )
            {
                // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
                // response.setContentType("multipart/form-data")
                response.setContentType("application/x-download");
                // 2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
                if (localChannel != null)
                {
                    localChannel.transferTo(0, localChannel.size(), targetChannel);
                }
            }
        }
    }
}

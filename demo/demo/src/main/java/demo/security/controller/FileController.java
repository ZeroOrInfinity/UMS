/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.security.controller;

import demo.test.dto.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @author  YongWu zheng
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
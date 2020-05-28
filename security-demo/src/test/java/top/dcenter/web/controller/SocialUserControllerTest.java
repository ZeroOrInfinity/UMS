package top.dcenter.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器测试
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/1 19:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class SocialUserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenQuerySucess() throws Exception {
        String result =
        mockMvc.perform(get("/user").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                                .param("username", "jake")
                                .param("page", "5")
                                .param("size", "15")
                                .param("sort", "age,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }

    @Test
    public void whenGenInfoSuccess() throws Exception {
        String result = mockMvc.perform(get("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tom"))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }

    @Test
    public void whenGetInfoFail() throws Exception {
        mockMvc.perform(get("/user/a").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenCreateSuccess() throws Exception {

        String content = "{\"username\":\"tom\",\"password\":null, \"birthday\":"+ new Date().getTime() +"}";
        log.warn(content);
        String result = mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1 ))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }
    @Test
    public void whenUpdateSuccess() throws Exception {

        String content =
                "{\"id\":\"1\",\"username\":\"tom\",\"password\":\"111\", \"birthday\":"+ new Date().getTime() +"}";
        log.warn("test: " + content);
        String result = mockMvc.perform(put("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1 ))
                .andReturn().getResponse().getContentAsString();
        log.warn("test: " + result);
    }

    @Test
    public void whenDeleteSuccess() throws Exception {
        mockMvc.perform(delete("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUploadSuccess() throws Exception {
        String result = mockMvc.perform(multipart("/file")
                                              .file(new MockMultipartFile(
                                                      "file",
                                                      "test.txt",
                                                      "multipart/form-data",
                                                      ("hello world!!!!").getBytes())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        log.info("result = {}", result);
    }

    @Test
    public void download() throws Exception {
        // 1588426747049test.txt
        MockHttpServletResponse response = mockMvc.perform(get("/file/1588426747049test_txt"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        log.info("response.getContentAsString() = {}", response.getContentAsString());
        log.info("response.getHeader(\"Content-Disposition\") = {}", response.getHeader("Content-Disposition"));
    }
}

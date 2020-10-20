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

package demo.test.dto;

import com.fasterxml.jackson.annotation.JsonView;
import demo.test.validator.MyConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 用户信息
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/1 19:51
 */
@Data
@NoArgsConstructor
@ToString
public class User {

    public interface UserSimpleView {}
    public interface UserDetailView extends UserSimpleView{}

    @JsonView(UserSimpleView.class)
    private String id;
    @JsonView(UserSimpleView.class)
    @MyConstraint(message = " 用户名不能为空")
    private String username;
    @JsonView(UserDetailView.class)
    @Length(min = 4, message = " 密码长度必须大于等于4位")
    private String password;
    @JsonView(UserSimpleView.class)
    @Past(message = " 生日格式错误或必须为过去日期")
    private Date birthday;;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
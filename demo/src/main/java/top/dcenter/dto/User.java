package top.dcenter.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import top.dcenter.validator.MyConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 用户信息
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/1 19:51
 */
@Data
@ToString
public class User {

    public interface UserSimpleView {}
    public interface UserDetailView extends UserSimpleView{}

    @JsonView(UserSimpleView.class)
    @NotBlank(message = " id不能为空")
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

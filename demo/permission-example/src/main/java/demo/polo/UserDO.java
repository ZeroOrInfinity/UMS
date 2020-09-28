package demo.polo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户 DO
 * @author zyw
 * @version V1.0  Created by 2020/9/27 13:55
 */
@Data
public class UserDO implements Serializable {
    private static final long serialVersionUID = -8880908064021597641L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private String authorities;
    private Integer status;
}

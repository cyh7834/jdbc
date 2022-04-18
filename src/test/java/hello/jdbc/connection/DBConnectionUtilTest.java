package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
public class DBConnectionUtilTest {

    @Test
    void connection() {
        // Connection 인터페이스의 구현체는 현재 설정된 org.h2.jdbc.JdbcConnection 이다.
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }
}

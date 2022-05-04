package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            log.info("error", e);
        }
    }

    /**
     * 위의 테스트 처럼 모든 오류 케이스의 예외를 모든 DB에 맞게 구현하는 것은 사실상 불가능하다.
     * 그래서 스프링에서는 예외를 추상화하여 제공한다.
     * */
    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.info("resultEx", resultEx);
            Assertions.assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }

    }
}

package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

/**
 * 계층 구조에 따라 예외를 밖으로 던진다면 모든 계층에서 다 예외에 의존해야한다.
 * 예외가 변경되면 의존되던 코드를 모두 변경해야하는 일이 발생한다.
 * 또한 대부분의 예외는 복구가 불가능하다. 이런 문제는 일관성있게 공통으로 처리해야한다.
 * 오류 로그를 남기고 개발자가 해당 오류를 빠르게 인지하는것이 필요하다.
 * 서블릿 필터, 인터셉터, 'ControllerAdvice'를 사용하여 공통으로 해결.
 * */
@Slf4j
public class UnCheckedAppTest {
    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }

    @Test void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    /**
     * 기존 예외를 꼭 포함시켜서 로그로 남겨야 한다.
     * */
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e); // 기존 예외를 넣어줌.
            }
        }

        public void runSQL() throws SQLException{
            throw new SQLException("연결 실패");

        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}

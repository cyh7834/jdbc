package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static hello.jdbc.connection.ConnectionConst.URL;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션을 획득
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 기존 DriverManagerDataSource에서 Hikari로 변경해도 구현 코드를 변경할 일이 발생하지 않는다.

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }
    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV110", 10000);

        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);

        Assertions.assertThat(findMember).isEqualTo(member);

        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());

        Assertions.assertThat(updatedMember.getMoney()).isEqualTo(20000);

        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * packageName    : hello.jdbc.service
 * fileName       : MemberServiceV1
 * author         : Sora
 * date           : 2024-06-03
 * description    : 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-03        Sora       최초 생성
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    /**
     * 트랜잭션은 서비스단에서 실행되어야 한다
     * - 트랜잭션을 사용하는 동안은 같은 커넥션을 유지해야한다 (그래야 같은 세션을 사용)
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
       Connection con =  dataSource.getConnection();
        try{
            con.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); // 성공시 커밋
        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            // 트랜잭션 종료
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);  // 검증 : 예외 발생시키기
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    private void release(Connection con) {
        if(con != null){
            try{
                // 풀에서 쓰고 반납할 때 처음 설정(true)로 안해주면 다음 커넥션에서도 false로 유지됨
                con.setAutoCommit(true);
                con.close();
            }catch(Exception e){
                log.info("error", e);
            }

        }
    }
}

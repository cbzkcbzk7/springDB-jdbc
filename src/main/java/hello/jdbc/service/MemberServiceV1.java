package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

/**
 * packageName    : hello.jdbc.service
 * fileName       : MemberServiceV1
 * author         : Sora
 * date           : 2024-06-03
 * description    : 트랙잭션 없는 경우
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-03        Sora       최초 생성
 */
@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);  // 검증 : 예외 발생시키기
        memberRepository.update(toId, toMember.getMoney() + money);

        // 트랜잭션 종료
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}

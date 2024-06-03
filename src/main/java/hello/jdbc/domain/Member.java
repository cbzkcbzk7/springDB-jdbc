package hello.jdbc.domain;

import lombok.Data;

/**
 * packageName    : hello.jdbc.domain
 * fileName       : Member
 * author         : Sora
 * date           : 2024-05-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-05-30        Sora       최초 생성
 */
@Data
public class Member {
    private String memberId;
    private int money;

    public Member(){}
    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }

}

package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * packageName    : hello.jdbc.repository
 * fileName       : MemberRepositoryV0
 * author         : Sora
 * date           : 2024-05-30
 * description    : JDBC - ConnectionParam ( 커넥션을 파라미터로 보내줌 )
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-05-30        Sora       최초 생성
 */

@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Member save(Member member) throws SQLException {
        String sql = "insert into Member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
           close(con,pstmt,null);
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from Member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){  // 커서가 있어서 next() 해야지 데이터부터 시작함
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            }else{
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (Exception e) {
            log.info("error",e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Connection con,String memberId) throws SQLException {
        String sql = "select * from Member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){  // 커서가 있어서 next() 해야지 데이터부터 시작함
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            }else{
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (Exception e) {
            log.info("error",e);
            throw e;
        }finally {
            // connection은 세션유지를 위해 service단에서 닫아줘야함
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
           // JdbcUtils.closeConnection(con);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update Member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (Exception e) {
            log.info("error",e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }

    }

    public void update(Connection con,String memberId, int money) throws SQLException {
        String sql = "update Member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (Exception e) {
            log.info("error",e);
            throw e;
        }finally {
            // connection은 세션유지를 위해 service단에서 닫아줘야함
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from Member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (Exception e) {
            log.info("error",e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }



    }
    /*
     * connection을 닫아줘야 닫힘
     */
    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass() );
        return con;
    }
}

package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //메소드 이름으로 쿼리 생성

    @Query(name = "Member.findByUsername")  // 주석처리하여도 네임드쿼리가 동작한다. 이유는 JPA가 엔티티.메소드명으로 먼저 namedQuery를 찾고 없으면, 메소드 이름으로 쿼리 생성을 하기 때문.
    List<Member> findByUsername(@Param("username") String username);  // 네임드 쿼리 호출. 파라미터가 있는 경우 @Param을 해주어야 한다.

    @Query("select m from Member m where m.username = :username and m.age = :age") // 실무에서 많이 사용한다. "select ~~~" string이지만 컴파일 시 오타작성하면 오류 발생.
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();  // @Query 값 타입으로 조회

    @Query("select new study.datajpa.dto.MemberDto (m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto(); // @Query DTO 타입으로 조회

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names); //파라미터 바인딩
}

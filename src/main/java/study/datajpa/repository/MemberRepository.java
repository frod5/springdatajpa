package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

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

    List<Member> findListByUsername(String username);  //컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

//    @Query(value = "select m from Member m left join m.team", countQuery = "select count(m.username) from Member m") countQuery 분리가능.
//    데이터 갯수에 영향을 주지 않는다면 총 데이터가 많을 경우에는 카운트 쿼리를 분리하는게 좋다.
//    전체 카운트 쿼리는 너무 무겁다.
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);
}

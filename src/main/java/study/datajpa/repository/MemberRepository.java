package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
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

    @Modifying(clearAutomatically = true)  //선언하지 않으면 update되지 않는다. clearAutomatically = true 선언 시 update 쿼리가 나가고 영속성 초기화를 해준다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})  // N+1 문제를 해결할떄 Fetch Join을 사용하는데, findAll() 또는 메소드 이름으로 생성하는 방식에는 직접 JPQL을 작성하지 않는다. 그럴떄 EntityGraph 사용.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})  //JPQL을 직접 작성하여도 entityGraph를 사용하면 fetch join과 같은 결과.
    @Query("select m From Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})  주로 JPQL을 직접 작성할 필요없는 경우에 간단하게 fetch join이 필요한 경우 사용한다.
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true")) //JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}

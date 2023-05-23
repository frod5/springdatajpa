package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        System.out.println("memberRepository.class = " + memberRepository.getClass());

        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("AAA",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    void namedQueryTest() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    void methodQueryTest() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("BBB", 20);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member2);
    }

    @Test
    void findUsernameList() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String username : usernameList) {
            System.out.println("username = " + username);
        }
    }

    @Test
    void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA",10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    void findByNames() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(List.of("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");  //리스트 반환

        // 만약에 데이터가 없으면 null이다. jpa에서는 singleResult()로 해서 exception을 내주는데, spring data jpa에서는 exception을 잡아 null로 세팅해준다.
        // 데이터가 1건 이상이면 jpa는 nonunique~ exception을 반환하지만 spring이 데이터 사이즈 exception으로 바꿔줜다.
        // 1건인경우는 받아진다.
        Member aaa1 = memberRepository.findMemberByUsername("AAA");

        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    void paging() {
        //given
        memberRepository.save(new Member("AAA1",10));
        memberRepository.save(new Member("AAA2",10));
        memberRepository.save(new Member("AAA3",10));
        memberRepository.save(new Member("AAA4",10));
        memberRepository.save(new Member("AAA5",10));
        memberRepository.save(new Member("AAA6",10));

        int age = 10;

        //spring data jpa는 페이지가 0부터 시작
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);  //반환값이 Page면 totalcount 쿼리도 같이 나간다.
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);  //totalcount가 나가지않고 limit에 +1에서 쿼리를 날린다.

        //컨트롤러에서 엔티티를 반환하면 안되기 때문에 보통 DTO로 변환하게되는데 page.map()을 사용해서 편리하게 DTO 변환을 할 수 있다.
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then

        //page
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(0);  // page.getNumber() 페이지번호
        assertThat(page.getTotalPages()).isEqualTo(2);  // 총 페이지 갯수
        assertThat(page.isFirst()).isTrue();  // 첫 페이지 여부
        assertThat(page.hasNext()).isTrue();  // 다음 페이지 존재 여부

        //slice
        List<Member> sliceContent = slice.getContent();

        assertThat(sliceContent.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);  // page.getNumber() 페이지번호
        assertThat(slice.isFirst()).isTrue();  // 첫 페이지 여부
        assertThat(slice.hasNext()).isTrue();  // 다음 페이지 존재 여부
    }
    @Test
    void bulkUpdate() {
        //given
        memberRepository.save(new Member("AAA1",10));
        memberRepository.save(new Member("AAA2",19));
        memberRepository.save(new Member("AAA3",20));
        memberRepository.save(new Member("AAA4",21));
        memberRepository.save(new Member("AAA5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);  // 변경감지가 아닌 벌크 연산 후에는 영속성 초기화를 해주어야한다.
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("AAA5");
        Member member = result.get(0);
        System.out.println("member = " + member);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10);
        member1.setTeam(teamA);
        Member member2 = new Member("member2", 10);
        member2.setTeam(teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");


        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team.name = " + member.getTeam());
            System.out.println("member.team.class = " + member.getTeam().getClass());
        }
    }

    @Test
    void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");  // readOnly hint가 적용되어 변경 감지할 snapshot을 만들지않아 변경감지를 하지 못한다.
        findMember.setUsername("member2"); // 변경감지를 할 수 없어서 update가 되지 않는다.

        em.flush();
    }

    @Test
    void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    void callCustom() {
        List<Member> members = memberRepository.findMemberCustom();
    }

    @Test
    void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);
        assertThat(result.get(0).getUsername()).isEqualTo("m1");

        //장점
        //동적 쿼리를 편리하게 처리
        //도메인 객체를 그대로 사용
        //데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
        //스프링 데이터 JPA JpaRepository 인터페이스에 이미 포함

        //단점
        //조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
        //다음과 같은 중첩 제약조건 안됨
        //firstname = ?0 or (firstname = ?1 and lastname = ?2)
        //매칭 조건이 매우 단순함
        //문자는 starts/contains/ends/regex
        //다른 속성은 정확한 매칭( = )만 지원

        //정리
        //실무에서 사용하기에는 매칭 조건이 너무 단순하고, LEFT 조인이 안됨
        //실무에서는 QueryDSL을 사용하자
    }

    @Test
    void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //when
//        List<UserNameOnly> result = memberRepository.findProjectionsInterfaceByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsClassByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsClassByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections userNameOnly : result) {
            System.out.println("userNameOnly = " + userNameOnly);
        }

        //주의
        //프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
        //프로젝션 대상이 ROOT가 아니면
        //LEFT OUTER JOIN 처리
        //모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산

        //정리
        //프로젝션 대상이 root 엔티티면 유용하다.
        //프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
        //실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
        //실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자
    }
}
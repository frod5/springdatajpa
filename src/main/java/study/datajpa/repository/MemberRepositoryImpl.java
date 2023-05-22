package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    // class명을 조심해야한다. 원본 respositoryImpl로 만들어야한다. ex) MemberRepositoryImpl
    // Impl 대신 다른 이름으로 변경하고 싶으면?
    // @EnableJpaRepositories(basePackages = "study.datajpa.repository", repositoryImplementationPostfix = "Impl")
    // 관례를 따르는게 좋다.

    //스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + Impl 을 적용하는 대신에
    //사용자 정의 인터페이스 명 + Impl 방식도 지원한다.
    //예를 들어서 위 예제의 MemberRepositoryImpl 대신에 MemberRepositoryCustomImpl 같이 구현해도 된다.

    ////다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
    ////JPA 직접 사용( EntityManager )
    ////스프링 JDBC Template 사용
    ////MyBatis 사용
    ////데이터베이스 커넥션 직접 사용 등등...
    ////Querydsl 사용

    //참고: 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용
    //참고: 항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다.
    //예를들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서
    //그냥 직접 사용해도 된다. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다.

    private final EntityManager em;
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}

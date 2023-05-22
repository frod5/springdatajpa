package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable(value = "id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable(value = "id") Member member) {  //Web 확장 - 도메인 클래스 컨버터

        //HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
        // 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음

        //주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
        //(트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)

        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        //파라미터로 Pageable 을 받을 수 있다.
        //Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성

        //예) /members?page=0&size=3&sort=id,desc&sort=username,desc
        //page: 현재 페이지, 0부터 시작한다.
        //size: 한 페이지에 노출할 데이터 건수
        //sort: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort
        //파라미터 추가 ( asc 생략 가능)

        //Page를 1부터 시작하기
        // 스프링 데이터는 Page를 0부터 시작한다.

        //만약 1부터 시작하려면?
        //1. Pageable, Page를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리한다. 그리고
        //직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘긴다. 물론 응답값도 Page 대신에
        //직접 만들어서 제공해야 한다.

        //2. spring.data.web.pageable.one-indexed-parameters 를 true 로 설정한다. 그런데 이 방법은
        //web에서 page 파라미터를 -1 처리 할 뿐이다. 따라서 응답값인 Page 에 모두 0 페이지 인덱스를 사용하는 한계가 있다.

        return memberRepository.findAll(pageable)
                .map(member -> new MemberDto(member.getId(), member.getUsername(), null));  //DTO 반환
    }

    @PostConstruct
    public void init() {
        for (int i=0; i < 100; i++) {
            memberRepository.save(new Member("user"+i,i));
        }
    }
}


package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

//인터페이스만 생성하면 JPA가 구현클래스를 만들어서 주입
public interface TeamRepository extends JpaRepository<Team, Long> {
}

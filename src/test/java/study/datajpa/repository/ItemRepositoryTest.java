package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void save() {

        //매우 중요!!!
        //* save() 메서드*
        //새로운 엔티티면 저장( persist )
        //새로운 엔티티가 아니면 병합( merge )

        //새로운 엔티티를 판단하는 기본 전략

        //식별자가 객체일 때 null 로 판단
        //식별자가 자바 기본 타입일 때 0 으로 판단
        //Persistable 인터페이스를 구현해서 판단 로직 변경 가능

        Item item = new Item("A");
        // 기본키를 직접할당하면 식별자에 값이 있기때문에 em.merge가 실행된다.
        // merge는 select쿼리로 값이 있는지 판단하고 없으면 insert를 하기때문에, 불필요한 select가 일어난다.

        // 직접 키를 할당하고싶은경우에는 Persistable을 사용해야한다.

        //참고: JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운
        //엔티티로 인식해서 정상 동작한다. 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미
        //식별자 값이 있는 상태로 save() 를 호출한다. 따라서 이 경우 merge() 가 호출된다. merge() 는 우선
        //DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다. 따라서
        //Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
        //> 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할
        //수 있다. (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)

        itemRepository.save(item);
    }
}
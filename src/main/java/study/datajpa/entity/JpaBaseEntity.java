package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)  // 수정 불가능한 컬럼
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist  // persist 하기전에 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate  // 업데이트 하기전에 실행
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}

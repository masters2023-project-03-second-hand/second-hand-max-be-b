package kr.codesquad.secondhand.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditingFields {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    protected LocalDateTime updatedAt;
}

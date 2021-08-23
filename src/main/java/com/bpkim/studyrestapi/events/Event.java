package com.bpkim.studyrestapi.events;

import com.bpkim.studyrestapi.accounts.Account;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
// Equals랑 HashCode를 구현할때 모든 필드를 이용한다. 나중에 엔티티 간의 연관관계가 있을때 상호 참조하는 관게가 되버리면
// Equals랑 HashCode를 구현한 안에서 stack over flow 가 발생 할 수 있다. 따라서 id 값만 가지고 비교하도록 선언한 것
// 다른 몇가지 필드를 추가하여 비교할 수 있다. @EqualsAndHashCode(of = {"id", "name"})
//
// 롬복 애노테이션은 하나로 선언하여 사용할 수 없다. 메타 애노테이션이 아니기 때문
// @Data는 쓰지말자 엔티티위에
// 그 이유는 그 안에 equals 가 있는데 모든 필드를 비교하기 때문! 스택오버플로우 날 수 있다.
@Entity
public class Event implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    // EnumType.ORDINAL
    // Enum 의 나중에 순서가 바뀔 수도 있기 때문에 Ordinal 보다 string 으로 선언하자.
    private EventStatus eventStatus = EventStatus.DRAFT;

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

    @ManyToOne
    private Account manager;

    public void update(){
        // Update free
        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        }else{
            this.free = false;
        }

        if(this.location == null || this.location.isBlank()){
            this.offline = false;
        }else{
            this.offline = true;
        }
    }
}

/*
Lombok, jpa 등 애노테이션이 많아진다.
입력값 검증에 대한 애노테이션을 분산하기 위해
입력값을 받는 부분을 dto로 반들자
dto 를 만들자.
단점은 변수가 중복 될 수도 있다.
* */
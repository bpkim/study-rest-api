package com.bpkim.studyrestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    /**
     * 빌더 로 만들 수 있는지 확인
     */
    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflean Spring REST API")
                .description("REST API development with Spring")
                .build();

        assertThat(event).isNotNull();
    }

    /**
     * 자바 객체 선언 방식으로 가능한지 확인
     */
    @Test
    public void javaBean(){
        Event event = new Event();
        String name = "Event";
        String dis = "Spring";
        event.setName(name);
        event.setDescription(dis);
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(dis);
    }

    @Test
    @Parameters({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    @Test
    @Parameters
//    @Parameters(method = "parametersForTestFreeParam")
    public void testFreeParam(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);

    }
    // paramsFor 로 앞에 쓰면 알아서 @Parameters 가 알아서 해준다.
    private Object[] parametersForTestFreeParam(){
        return new Object[]{
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters/*(method = "parametersForTestOffLine")*/
    public void testOffline(String location, boolean isOffline){

        // Given
        Event event = Event.builder()
                .location(location)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);

    }

    private Object[] parametersForTestOffline(){
        return new Object[]{
                new Object[] {"강남역", true},
                new Object[] {"", false},
                new Object[] {null, false}
        };
    }
}
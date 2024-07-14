package com.jxx.groupware.testUtil;

import org.junit.jupiter.api.Test;

public class ReferenceTest {

    @Test
    void testV1() {
        StringBuilder sb = new StringBuilder("Hello");
        modifyObject(sb);
        System.out.println(sb.toString()); // 출력 결과: "Hello World!" (메서드 호출 후에 변경된 값이 유지됨)
    }

    void modifyObject(StringBuilder builder) {
        builder.append(" World!"); // 문자열 뒤에 " World!" 추가
    }


    @Test
    void test() {
        StringBuilder sb = new StringBuilder("Hello"); // sb 참조값:1234
        changeReference(sb); // sb 참조값:1234 를 복사해서 들어간다.
        System.out.println(sb);
    }

    void changeReference(StringBuilder builder) {
        builder.append(" World"); // builder 참조값:1234
        builder = new StringBuilder("Good Bye"); // sb 참조값:5678
        System.out.println("test");
    }
}

// 원본을 넘기는게 아니라 참조 값을 복사해서 넘긴다.
// 원본을 넘긴다면 new StringBuilder 로 변경되어야 한다.
// 참조 값을 복사해서 넘기기 때문에 수정은 가능하다.
// 반면 참조 값을 복사해서 넘겼기 때문에 새로운 인스턴스를 할당하면 메서드 스택 area 가 종료되면서 없어진다.
package com.diouma.studentmanagementapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifie que le contexte Spring complet demarre correctement en s'appuyant sur
 * la base H2 du profil "test".
 */
@SpringBootTest
@ActiveProfiles("test")
class StudentManagementApiApplicationTests {

    @Test
    void contextLoads() {
    }

}

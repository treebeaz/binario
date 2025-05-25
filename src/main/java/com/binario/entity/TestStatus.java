package com.binario.entity;

public enum TestStatus {
    NOT_STARTED,  // не начат
    SUBMITTED,      // тест отправлен (для кодовых - ожидает проверки)
    EVALUATED       // проверен ( только для кода)
}

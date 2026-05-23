package com.projeto.financeiro.service;

import com.projeto.financeiro.entity.enums.Recorrencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecorrenciaCalculatorTest {

    private final RecorrenciaCalculator calculator = new RecorrenciaCalculator();

    @Test
    void shouldReturnSingleDateWhenRecorrenciaIsNull() {
        LocalDate inicio = LocalDate.of(2026, 1, 10);

        List<LocalDate> datas = calculator.gerarDatas(
                inicio,
                null,
                null
        );

        assertEquals(List.of(inicio), datas);
    }

    @Test
    void shouldReturnEmptyListWhenDataInicioIsNull() {
        List<LocalDate> datas = calculator.gerarDatas(
                null,
                LocalDate.of(2026, 3, 1),
                Recorrencia.MENSAL
        );

        assertTrue(datas.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenRecorrenciaExistsButDataFimIsNull() {
        List<LocalDate> datas = calculator.gerarDatas(
                LocalDate.of(2026, 1, 1),
                null,
                Recorrencia.MENSAL
        );

        assertTrue(datas.isEmpty());
    }

    @Test
    void shouldGenerateMonthlyDatesIncludingStartAndEnd() {
        List<LocalDate> datas = calculator.gerarDatas(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1),
                Recorrencia.MENSAL
        );

        assertEquals(
                List.of(
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 3, 1)
                ),
                datas
        );
    }

    @Test
    void shouldGenerateWeeklyDates() {
        List<LocalDate> datas = calculator.gerarDatas(
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 19),
                Recorrencia.SEMANAL
        );

        assertEquals(
                List.of(
                        LocalDate.of(2026, 1, 5),
                        LocalDate.of(2026, 1, 12),
                        LocalDate.of(2026, 1, 19)
                ),
                datas
        );
    }

    @Test
    void shouldReturnEmptyListWhenDataFimIsBeforeDataInicio() {
        List<LocalDate> datas = calculator.gerarDatas(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 1, 1),
                Recorrencia.MENSAL
        );

        assertTrue(datas.isEmpty());
    }
}

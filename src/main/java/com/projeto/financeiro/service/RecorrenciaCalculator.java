package com.projeto.financeiro.service;

import com.projeto.financeiro.entity.enums.Recorrencia;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecorrenciaCalculator {

    public List<LocalDate> gerarDatas(LocalDate dataInicio, LocalDate dataFim, Recorrencia recorrencia) {
        List<LocalDate> datas = new ArrayList<>();

        if (dataInicio == null) {
            return datas;
        }

        if (recorrencia == null) {
            datas.add(dataInicio);
            return datas;
        }

        if (dataFim == null) {
            return datas;
        }

        LocalDate cursor = dataInicio;

        while (!cursor.isAfter(dataFim)) {
            datas.add(cursor);
            cursor = avancar(cursor, recorrencia);
        }

        return datas;
    }

    private LocalDate avancar(LocalDate dataBase, Recorrencia recorrencia) {
        return switch (recorrencia) {
            case SEMANAL -> dataBase.plusWeeks(1);
            case MENSAL -> dataBase.plusMonths(1);
            case BIMESTRAL -> dataBase.plusMonths(2);
            case TRIMESTRAL -> dataBase.plusMonths(3);
            case SEMESTRAL -> dataBase.plusMonths(6);
            case ANUAL -> dataBase.plusYears(1);
        };
    }
}
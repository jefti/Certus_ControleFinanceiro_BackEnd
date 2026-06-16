package com.projeto.financeiro.service.export;

import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.StatusTitulo;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import com.projeto.financeiro.repository.FaturamentoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // -------------------------------------------------------------------------
    // PALETA DE CORES
    // -------------------------------------------------------------------------
    private static final byte[] COR_AZUL_ESCURO    = {(byte) 31,  (byte) 73,  (byte) 125};
    private static final byte[] COR_AZUL_MEDIO     = {(byte) 68,  (byte) 114, (byte) 196};
    private static final byte[] COR_AZUL_CLARO     = {(byte) 218, (byte) 227, (byte) 243};
    private static final byte[] COR_CINZA          = {(byte) 242, (byte) 242, (byte) 242};
    private static final byte[] COR_BRANCO         = {(byte) 255, (byte) 255, (byte) 255};
    private static final byte[] COR_VERDE_FUNDO    = {(byte) 198, (byte) 239, (byte) 206};
    private static final byte[] COR_VERDE_TEXTO    = {(byte) 0,   (byte) 97,  (byte) 0  };
    private static final byte[] COR_VERMELHO_FUNDO = {(byte) 255, (byte) 199, (byte) 206};
    private static final byte[] COR_VERMELHO_TEXTO = {(byte) 156, (byte) 0,   (byte) 6  };

    private final FaturamentoRepository faturamentoRepository;
    private final CentroDeCustoRepository centroDeCustoRepository;

    // -------------------------------------------------------------------------
    // ESTILOS — record criado uma única vez por exportação
    // -------------------------------------------------------------------------
    private record Estilos(
            CellStyle tituloPrincipal,
            CellStyle header, CellStyle subheader, CellStyle tituloGrupo,
            CellStyle label,
            CellStyle positivo, CellStyle negativo, CellStyle neutro,
            CellStyle totalPos, CellStyle totalNeg, CellStyle totalNeutro,
            CellStyle data
    ) {
        CellStyle paraStatus(StatusTitulo status) {
            return switch (status) {
                case PAGO     -> positivo();
                case ATRASADO -> negativo();
                default       -> neutro();
            };
        }

        CellStyle paraTipo(TipoTitulo tipo) {
            return tipo == TipoTitulo.RECEBER ? positivo() : negativo();
        }
    }

    private Estilos criarEstilos(XSSFWorkbook wb) {
        return new Estilos(
                estilo(wb, COR_AZUL_ESCURO, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, COR_BRANCO, true, (short) 14),
                estilo(wb, COR_AZUL_ESCURO, true,  HorizontalAlignment.LEFT,   VerticalAlignment.CENTER, COR_BRANCO, true, (short) 11),
                estilo(wb, COR_AZUL_MEDIO,  true,  HorizontalAlignment.CENTER, null,                     COR_BRANCO, true, (short) 10),
                estilo(wb, COR_AZUL_CLARO,  true,  null,                       null,                     COR_AZUL_ESCURO, true, (short) 10),
                estilo(wb, COR_CINZA,       true,  null,                       null,                     null, true, (short) 10),
                estilo(wb, COR_VERDE_FUNDO,    true, null, null, COR_VERDE_TEXTO,    true, (short) 10),
                estilo(wb, COR_VERMELHO_FUNDO, true, null, null, COR_VERMELHO_TEXTO, true, (short) 10),
                novoEstilo(wb, null, true),
                estilo(wb, COR_VERDE_FUNDO,    true, null, null, COR_VERDE_TEXTO,    true, (short) 11),
                estilo(wb, COR_VERMELHO_FUNDO, true, null, null, COR_VERMELHO_TEXTO, true, (short) 11),
                estilo(wb, COR_CINZA, true, null, null, null, true, (short) 11),
                estiloData(wb)
        );
    }

    // -------------------------------------------------------------------------
    // ENTRADA PÚBLICA
    // -------------------------------------------------------------------------
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public byte[] exportar(LocalDate periodoInicial, LocalDate periodoFinal) {
        Usuario usuario = usuarioAutenticado();

        List<Faturamento> faturamentos = faturamentoRepository.findByPeriodoAndUsuario(
                periodoInicial, periodoFinal, usuario);
        List<CentroDeCusto> centros = centroDeCustoRepository.findByUsuario(usuario);

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Estilos e = criarEstilos(wb);

            criarAbaResumo(wb, e, faturamentos, periodoInicial, periodoFinal);
            criarAbaFluxoDeCaixa(wb, e, faturamentos);
            criarAbaCentrosDeCusto(wb, e, centros, faturamentos);
            criarAbaLancamentos(wb, e, faturamentos);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao gerar exportacao Excel da dashboard", ex);
        }
    }

    // -------------------------------------------------------------------------
    // ABA 1: RESUMO
    // -------------------------------------------------------------------------
    private void criarAbaResumo(XSSFWorkbook wb, Estilos e, List<Faturamento> fats,
                                LocalDate periodoInicial, LocalDate periodoFinal) {
        XSSFSheet sheet = wb.createSheet("Resumo");
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 8000);

        BigDecimal totalReceitas = somarPorTipo(fats, TipoTitulo.RECEBER);
        BigDecimal totalDespesas = somarPorTipo(fats, TipoTitulo.PAGAR);
        BigDecimal saldo         = totalReceitas.subtract(totalDespesas);

        long pago     = fats.stream().filter(f -> f.getDataPagamento() != null).count();
        long emAberto = fats.stream().filter(f -> f.getDataPagamento() == null && !f.getDataVencimento().isBefore(LocalDate.now())).count();
        long atrasado = fats.stream().filter(f -> f.getDataPagamento() == null && f.getDataVencimento().isBefore(LocalDate.now())).count();

        int linha = 0;
        linha = escreverTituloPrincipal(sheet, linha, e, "RELATÓRIO FINANCEIRO — DASHBOARD", 1);
        linha++;

        linha = criarLinhaInfo(sheet, linha, "Período Inicial", periodoInicial.format(DATE_FMT), e.label(), e.neutro());
        linha = criarLinhaInfo(sheet, linha, "Período Final",   periodoFinal.format(DATE_FMT),   e.label(), e.neutro());
        linha++;

        linha = escreverSecaoHeader(sheet, linha, e, "INDICADORES FINANCEIROS", 1);
        linha = criarLinhaInfo(sheet, linha, "Total de Receitas", "R$ " + formatar(totalReceitas), e.label(), e.positivo());
        linha = criarLinhaInfo(sheet, linha, "Total de Despesas", "R$ " + formatar(totalDespesas), e.label(), e.negativo());
        linha = criarLinhaInfo(sheet, linha, "Saldo do Período",  "R$ " + formatar(saldo),         e.label(), e.paraTipo(saldo.compareTo(BigDecimal.ZERO) >= 0 ? TipoTitulo.RECEBER : TipoTitulo.PAGAR));
        linha++;

        linha = escreverSecaoHeader(sheet, linha, e, "LANÇAMENTOS", 1);
        linha = criarLinhaInfo(sheet, linha, "Total de Lançamentos", String.valueOf(fats.size()), e.label(), e.neutro());
        linha = criarLinhaInfo(sheet, linha, "Pagos / Recebidos",    String.valueOf(pago),        e.label(), e.positivo());
        linha = criarLinhaInfo(sheet, linha, "Em Aberto",            String.valueOf(emAberto),    e.label(), e.neutro());
        criarLinhaInfo(sheet, linha, "Atrasados", String.valueOf(atrasado), e.label(), atrasado > 0 ? e.negativo() : e.neutro());
    }

    // -------------------------------------------------------------------------
    // ABA 2: FLUXO DE CAIXA
    // -------------------------------------------------------------------------
    private void criarAbaFluxoDeCaixa(XSSFWorkbook wb, Estilos e, List<Faturamento> fats) {
        XSSFSheet sheet = wb.createSheet("Fluxo de Caixa");
        configurarLarguras(sheet, 5000, 7000, 7000, 7000);
        escreverHeaderColunas(sheet, e, "Data", "Receitas (R$)", "Despesas (R$)", "Saldo do Dia (R$)");

        Map<LocalDate, BigDecimal> receitasPorData = agruparPorData(fats, TipoTitulo.RECEBER);
        Map<LocalDate, BigDecimal> despesasPorData = agruparPorData(fats, TipoTitulo.PAGAR);

        List<LocalDate> datas = fats.stream().map(Faturamento::getDataVencimento).distinct().sorted().toList();

        int linha = 1;
        for (LocalDate data : datas) {
            linha = escreverLinhaFluxo(sheet, linha, e, data, receitasPorData, despesasPorData);
        }

        escreverTotaisFluxo(sheet, linha + 1, e, receitasPorData, despesasPorData);
    }

    private int escreverLinhaFluxo(XSSFSheet sheet, int linha, Estilos e, LocalDate data,
                                   Map<LocalDate, BigDecimal> receitas, Map<LocalDate, BigDecimal> despesas) {
        BigDecimal rec  = receitas.getOrDefault(data, BigDecimal.ZERO);
        BigDecimal desp = despesas.getOrDefault(data, BigDecimal.ZERO);
        BigDecimal saldo = rec.subtract(desp);

        Row row = sheet.createRow(linha++);
        escreverCelula(row, 0, data.format(DATE_FMT), e.data());
        escreverCelula(row, 1, rec.doubleValue(),  e.positivo());
        escreverCelula(row, 2, desp.doubleValue(), e.negativo());
        escreverCelula(row, 3, saldo.doubleValue(), saldo.compareTo(BigDecimal.ZERO) >= 0 ? e.positivo() : e.negativo());
        return linha;
    }

    private void escreverTotaisFluxo(XSSFSheet sheet, int linha, Estilos e,
                                     Map<LocalDate, BigDecimal> receitas, Map<LocalDate, BigDecimal> despesas) {
        BigDecimal totalRec  = receitas.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDesp = despesas.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo     = totalRec.subtract(totalDesp);

        Row row = sheet.createRow(linha);
        row.setHeightInPoints(18);
        escreverCelula(row, 0, "TOTAL",               e.totalNeutro());
        escreverCelula(row, 1, totalRec.doubleValue(), e.totalPos());
        escreverCelula(row, 2, totalDesp.doubleValue(), e.totalNeg());
        escreverCelula(row, 3, saldo.doubleValue(), saldo.compareTo(BigDecimal.ZERO) >= 0 ? e.totalPos() : e.totalNeg());
    }

    // -------------------------------------------------------------------------
    // ABA 3: CENTROS DE CUSTO
    // -------------------------------------------------------------------------
    private void criarAbaCentrosDeCusto(XSSFWorkbook wb, Estilos e,
                                        List<CentroDeCusto> centros, List<Faturamento> fats) {
        XSSFSheet sheet = wb.createSheet("Centros de Custo");
        configurarLarguras(sheet, 8000, 8000, 5000, 5000, 5000, 5000, 6000);

        int linha = 0;
        for (CentroDeCusto centro : centros) {
            linha = escreverBlocoCentro(sheet, linha, centro, fats, e);
            linha += 2;
        }
    }

    private int escreverBlocoCentro(XSSFSheet sheet, int linha, CentroDeCusto centro,
                                    List<Faturamento> fats, Estilos e) {
        linha = escreverCabecalhoCentro(sheet, linha, centro, e);

        List<Faturamento> fatsDoCentro = filtrarFatsDoCentro(fats, centro);
        BigDecimal[] totais = {BigDecimal.ZERO, BigDecimal.ZERO};

        if (fatsDoCentro.isEmpty()) {
            linha = escreverLinhaVazia(sheet, linha, e.neutro());
        } else {
            Map<Long, List<Faturamento>> porTitulo = fatsDoCentro.stream()
                    .collect(Collectors.groupingBy(f -> f.getTitulo().getId(), LinkedHashMap::new, Collectors.toList()));
            for (List<Faturamento> grupo : porTitulo.values()) {
                linha = escreverGrupoTitulo(sheet, linha, grupo, totais, e);
            }
        }

        return escreverTotaisCentro(sheet, linha, totais[0], totais[1], e);
    }

    private int escreverCabecalhoCentro(XSSFSheet sheet, int linha, CentroDeCusto centro, Estilos e) {
        linha = escreverTituloPrincipal(sheet, linha, e, "CENTRO DE CUSTO: " + centro.getDescricao().toUpperCase(), 6);

        if (centro.getObservacao() != null && !centro.getObservacao().isBlank()) {
            linha = escreverLinhaSpan(sheet, linha, "Observação: " + centro.getObservacao(), e.neutro(), 6);
        }

        escreverHeaderColunas(sheet, linha++, e, "Título", "Vencimento", "Tipo", "Valor (R$)", "Data Pagamento", "Status", "Observação");
        return linha;
    }

    private int escreverGrupoTitulo(XSSFSheet sheet, int linha, List<Faturamento> grupo,
                                    BigDecimal[] totais, Estilos e) {
        TipoTitulo tipo      = grupo.get(0).getTitulo().getTipo();
        String     descricao = grupo.get(0).getTitulo().getDescricao();

        linha = escreverLinhaSpan(sheet, linha, "  » " + descricao + " (" + tipo.getDescricao() + ")", e.tituloGrupo(), 6);

        for (Faturamento f : grupo) {
            linha = escreverLinhaFaturamentoCentro(sheet, linha, f, tipo, e);
            acumularTotais(totais, f, tipo);
        }
        return linha;
    }

    private int escreverLinhaFaturamentoCentro(XSSFSheet sheet, int linha, Faturamento f,
                                               TipoTitulo tipo, Estilos e) {
        Row row = sheet.createRow(linha++);
        escreverCelula(row, 0, f.getTitulo().getDescricao(),                                    e.neutro());
        escreverCelula(row, 1, f.getDataVencimento().format(DATE_FMT),                         e.data());
        escreverCelula(row, 2, tipo.getDescricao(),                                             e.paraTipo(tipo));
        escreverCelula(row, 3, f.getValor().doubleValue(),                                     e.paraTipo(tipo));
        escreverCelula(row, 4, dataPagamentoOuTraco(f),                                        e.neutro());
        escreverCelula(row, 5, f.getStatus().getDescricao(),                                   e.paraStatus(f.getStatus()));
        escreverCelula(row, 6, f.getObservacao() != null ? f.getObservacao() : "-",            e.neutro());
        return linha;
    }

    private int escreverTotaisCentro(XSSFSheet sheet, int linha, BigDecimal totalRec,
                                     BigDecimal totalDesp, Estilos e) {
        BigDecimal saldo = totalRec.subtract(totalDesp);
        Row row = sheet.createRow(linha++);
        row.setHeightInPoints(18);
        escreverCelula(row, 0, "Total do Centro",                       e.totalNeutro());
        escreverCelula(row, 1, "Receitas: R$ " + formatar(totalRec),   e.totalPos());
        escreverCelula(row, 2, "Despesas: R$ " + formatar(totalDesp),  e.totalNeg());
        escreverCelula(row, 3, "Saldo: R$ " + formatar(saldo), saldo.compareTo(BigDecimal.ZERO) >= 0 ? e.totalPos() : e.totalNeg());
        return linha;
    }

    // -------------------------------------------------------------------------
    // ABA 4: LANÇAMENTOS
    // -------------------------------------------------------------------------
    private void criarAbaLancamentos(XSSFWorkbook wb, Estilos e, List<Faturamento> fats) {
        XSSFSheet sheet = wb.createSheet("Lancamentos");
        configurarLarguras(sheet, 4000, 8000, 5000, 5000, 5000, 5000, 5000, 7000);
        escreverHeaderColunas(sheet, e, "ID", "Título", "Tipo", "Vencimento", "Valor (R$)", "Pagamento", "Status", "Observação");

        BigDecimal[] totais = {BigDecimal.ZERO, BigDecimal.ZERO};
        int linha = 1;

        for (Faturamento f : fats) {
            linha = escreverLinhaLancamento(sheet, linha, f, totais, e);
        }

        escreverTotaisLancamentos(sheet, linha + 1, totais[0], totais[1], e);
    }

    private int escreverLinhaLancamento(XSSFSheet sheet, int linha, Faturamento f,
                                        BigDecimal[] totais, Estilos e) {
        TipoTitulo tipo = f.getTitulo().getTipo();
        Row row = sheet.createRow(linha++);
        escreverCelula(row, 0, f.getId(),                                                       e.neutro());
        escreverCelula(row, 1, f.getTitulo().getDescricao(),                                    e.neutro());
        escreverCelula(row, 2, tipo.getDescricao(),                                             e.paraTipo(tipo));
        escreverCelula(row, 3, f.getDataVencimento().format(DATE_FMT),                         e.data());
        escreverCelula(row, 4, f.getValor().doubleValue(),                                     e.paraTipo(tipo));
        escreverCelula(row, 5, dataPagamentoOuTraco(f),                                        e.neutro());
        escreverCelula(row, 6, f.getStatus().getDescricao(),                                   e.paraStatus(f.getStatus()));
        escreverCelula(row, 7, f.getObservacao() != null ? f.getObservacao() : "-",            e.neutro());
        acumularTotais(totais, f, tipo);
        return linha;
    }

    private void escreverTotaisLancamentos(XSSFSheet sheet, int linha, BigDecimal totalRec,
                                           BigDecimal totalDesp, Estilos e) {
        BigDecimal saldo = totalRec.subtract(totalDesp);

        Row rowTotal = sheet.createRow(linha);
        rowTotal.setHeightInPoints(18);
        escreverCelula(rowTotal, 0, "TOTAL",               e.totalNeutro());
        escreverCelula(rowTotal, 4, totalRec.doubleValue(), e.totalPos());
        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 0, 3));

        Row rowSaldo = sheet.createRow(linha + 1);
        escreverCelula(rowSaldo, 0, "SALDO",              e.totalNeutro());
        escreverCelula(rowSaldo, 4, saldo.doubleValue(), saldo.compareTo(BigDecimal.ZERO) >= 0 ? e.totalPos() : e.totalNeg());
        sheet.addMergedRegion(new CellRangeAddress(linha + 1, linha + 1, 0, 3));
    }

    // -------------------------------------------------------------------------
    // HELPERS DE ESCRITA
    // -------------------------------------------------------------------------
    private int escreverTituloPrincipal(XSSFSheet sheet, int linha, Estilos e, String texto, int ultimaColuna) {
        Row row = sheet.createRow(linha++);
        row.setHeightInPoints(30);
        Cell cell = row.createCell(0);
        cell.setCellValue(texto);
        cell.setCellStyle(e.tituloPrincipal());
        sheet.addMergedRegion(new CellRangeAddress(linha - 1, linha - 1, 0, ultimaColuna));
        return linha;
    }

    private int escreverSecaoHeader(XSSFSheet sheet, int linha, Estilos e, String texto, int ultimaColuna) {
        Row row = sheet.createRow(linha++);
        Cell cell = row.createCell(0);
        cell.setCellValue(texto);
        cell.setCellStyle(e.header());
        sheet.addMergedRegion(new CellRangeAddress(linha - 1, linha - 1, 0, ultimaColuna));
        return linha;
    }

    private int escreverLinhaSpan(XSSFSheet sheet, int linha, String texto, CellStyle style, int ultimaColuna) {
        Row row = sheet.createRow(linha++);
        Cell cell = row.createCell(0);
        cell.setCellValue(texto);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(linha - 1, linha - 1, 0, ultimaColuna));
        return linha;
    }

    private int criarLinhaInfo(XSSFSheet sheet, int linha, String label, String valor,
                               CellStyle styleLabel, CellStyle styleValor) {
        Row row = sheet.createRow(linha++);
        row.setHeightInPoints(16);
        escreverCelula(row, 0, label, styleLabel);
        escreverCelula(row, 1, valor, styleValor);
        return linha;
    }

    private void escreverHeaderColunas(XSSFSheet sheet, Estilos e, String... colunas) {
        escreverHeaderColunas(sheet, 0, e, colunas);
    }

    private void escreverHeaderColunas(XSSFSheet sheet, int linha, Estilos e, String... colunas) {
        Row row = sheet.createRow(linha);
        row.setHeightInPoints(20);
        for (int i = 0; i < colunas.length; i++) {
            escreverCelula(row, i, colunas[i], e.header());
        }
    }

    private void escreverCelula(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private void escreverCelula(Row row, int col, double valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private void escreverCelula(Row row, int col, long valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private int escreverLinhaVazia(XSSFSheet sheet, int linha, CellStyle style) {
        return escreverLinhaSpan(sheet, linha, "Nenhum lançamento no período para este centro de custo.", style, 6);
    }

    private void configurarLarguras(XSSFSheet sheet, int... larguras) {
        for (int i = 0; i < larguras.length; i++) sheet.setColumnWidth(i, larguras[i]);
    }

    // -------------------------------------------------------------------------
    // HELPERS DE NEGÓCIO
    // -------------------------------------------------------------------------
    private List<Faturamento> filtrarFatsDoCentro(List<Faturamento> fats, CentroDeCusto centro) {
        return fats.stream()
                .filter(f -> f.getTitulo().getCentroDeCusto() != null &&
                        f.getTitulo().getCentroDeCusto().stream().anyMatch(c -> c.getId().equals(centro.getId())))
                .sorted(Comparator.comparing(Faturamento::getDataVencimento))
                .toList();
    }

    private Map<LocalDate, BigDecimal> agruparPorData(List<Faturamento> fats, TipoTitulo tipo) {
        Map<LocalDate, BigDecimal> mapa = new TreeMap<>();
        fats.stream().filter(f -> f.getTitulo().getTipo() == tipo)
                .forEach(f -> mapa.merge(f.getDataVencimento(), f.getValor(), BigDecimal::add));
        return mapa;
    }

    private void acumularTotais(BigDecimal[] totais, Faturamento f, TipoTitulo tipo) {
        if (tipo == TipoTitulo.RECEBER) totais[0] = totais[0].add(f.getValor());
        else                            totais[1] = totais[1].add(f.getValor());
    }

    private BigDecimal somarPorTipo(List<Faturamento> fats, TipoTitulo tipo) {
        return fats.stream()
                .filter(f -> f.getTitulo().getTipo() == tipo)
                .map(Faturamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String dataPagamentoOuTraco(Faturamento f) {
        return f.getDataPagamento() != null ? f.getDataPagamento().toLocalDate().format(DATE_FMT) : "-";
    }

    private String formatar(BigDecimal valor) {
        return String.format("%,.2f", valor);
    }

    // -------------------------------------------------------------------------
    // BUILDERS DE ESTILO
    // -------------------------------------------------------------------------
    private XSSFCellStyle estilo(XSSFWorkbook wb, byte[] corFundo, boolean borda,
                                 HorizontalAlignment hAlign, VerticalAlignment vAlign,
                                 byte[] corTexto, boolean bold, short tamanho) {
        XSSFCellStyle style = novoEstilo(wb, corFundo, borda);
        if (hAlign != null) style.setAlignment(hAlign);
        if (vAlign != null) style.setVerticalAlignment(vAlign);
        if (corTexto != null || bold) style.setFont(novaFonte(wb, corTexto, bold, tamanho));
        return style;
    }

    private XSSFCellStyle novoEstilo(XSSFWorkbook wb, byte[] corFundo, boolean borda) {
        XSSFCellStyle style = wb.createCellStyle();
        if (corFundo != null) {
            style.setFillForegroundColor(new XSSFColor(corFundo, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (borda) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
        return style;
    }

    private XSSFCellStyle estiloData(XSSFWorkbook wb) {
        XSSFCellStyle style = novoEstilo(wb, null, true);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private XSSFFont novaFonte(XSSFWorkbook wb, byte[] cor, boolean bold, short tamanho) {
        XSSFFont font = wb.createFont();
        if (cor != null) font.setColor(new XSSFColor(cor, null));
        font.setBold(bold);
        font.setFontHeightInPoints(tamanho);
        return font;
    }

    // -------------------------------------------------------------------------
    // AUTENTICAÇÃO
    // -------------------------------------------------------------------------
    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }
}
